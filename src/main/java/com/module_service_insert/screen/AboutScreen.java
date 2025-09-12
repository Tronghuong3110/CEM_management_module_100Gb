package com.module_service_insert.screen;

import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.screenUtils.AddCssForBtnUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author Trọng Hướng
 */
public class AboutScreen extends StackPane {

    private VBox container;
    private StackPane overLay;

    private Label statusVal, typeVal, userVal, locationVal, expiryVal, statusLabel;

    public AboutScreen() {
        container = new VBox(10);
        getChildren().add(container);
        setStyle("-fx-background-insets: 0; -fx-background-color: #ededed;");
        createLogo();
        createStatusLicense();
        createInformationApp();

        overLay = new StackPane(new ProgressIndicator());
        overLay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        overLay.setVisible(false);

        getChildren().add(overLay);
    }

    private void showLoading(boolean show) {
        overLay.setVisible(show);
    }

    private void createLogo() {
        Image logo = new Image(getClass().getResourceAsStream(
                "/com/module_service_insert/icons/logo_banner.png"
        ));
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(200);
        logoView.setFitWidth(770);
        container.getChildren().add(logoView);
    }

    private void createStatusLicense() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setPrefWidth(Double.MAX_VALUE);
        grid.setStyle (
            "-fx-padding: 10;" +
            "-fx-background-insets: 0;" +
            "-fx-background-color: #ededed;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );

        // Tiêu đề
        Label title = new Label("Thông tin License");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Labels với icon
        statusVal  = new Label("Chưa kích hoạt");
        typeVal     = new Label("N/A");
        userVal     = new Label("N/A");
        locationVal = new Label("N/A");
        expiryVal   = new Label("N/A");

        statusLabel   = createLabelWithIcon("Trạng thái", "/com/module_service_insert/icons/status_fail.png");
        Label typeLabel     = createLabelWithIcon("Loại license", "/com/module_service_insert/icons/type.png");
        Label userLabel     = createLabelWithIcon("Đơn vị sử dụng", "/com/module_service_insert/icons/user_use.png");
        Label locationLabel = createLabelWithIcon("Vị trí license", "/com/module_service_insert/icons/folder.png");
        Label expiryLabel   = createLabelWithIcon("Ngày hết hạn", "/com/module_service_insert/icons/expiry.png");

        String valueStyle = "-fx-font-size: 16px;";
        statusVal.setStyle("-fx-text-fill: #f41717;" + valueStyle);
        typeVal.setStyle(valueStyle);
        userVal.setStyle(valueStyle);
        locationVal.setStyle(valueStyle);
        expiryVal.setStyle(valueStyle);

        grid.add(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);
        grid.addRow(1, statusLabel, statusVal);
        grid.addRow(2, typeLabel, typeVal);
        grid.addRow(3, userLabel, userVal);
        grid.addRow(4, locationLabel, locationVal);
        grid.addRow(5, expiryLabel, expiryVal);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65);
        grid.getColumnConstraints().addAll(col1, col2);

        createButtonActionLicense(grid);

        container.getChildren().add(grid);
    }

    private void createButtonActionLicense(GridPane grid) {
        ImageView checkIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/module_service_insert/icons/check.png")));
        ImageView importIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/module_service_insert/icons/import.png")));

        checkIcon.setFitHeight(16);
        checkIcon.setFitWidth(16);
        importIcon.setFitHeight(16);
        importIcon.setFitWidth(16);


        Button btnCheck = new Button("Check license", checkIcon);
        Button btnImport = new Button("Load license", importIcon);
        AddCssForBtnUtil.addCssStyleForBtn(btnCheck);
        AddCssForBtnUtil.addCssStyleForBtn(btnImport);

        HBox buttonBox = new HBox(10, btnImport, btnCheck);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(0, 30, 0, 0));

        grid.add(buttonBox, 0, 6, 2, 1);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);

        btnCheck.setOnMouseClicked(event -> {
            overLay.setVisible(true);
            String licensePath = locationVal.getText();
            if(licensePath == null || licensePath.isBlank()) {
               AlertUtils.showAlert("Cảnh báo", "Chưa nhập license, vui lòng chọn license.", "WARNING");
               return;
            }
            Task<JSONObject> task = new Task<JSONObject>() {
                @Override
                protected JSONObject call() throws Exception {
                    return checkStatusLicense(licensePath);
                }
            };

            task.setOnSucceeded(e -> {
                JSONObject result = task.getValue();
                setValue(result);
                overLay.setVisible(false);
            });

            task.setOnFailed(e -> {
                overLay.setVisible(false);
                Throwable ex = task.getException();
                ex.printStackTrace();
                AlertUtils.showAlert("Lỗi", "Có lỗi xảy ra khi kiểm tra license", "ERROR");
            });
            new Thread(task).start();
        });

        btnImport.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load license");
            fileChooser.setSelectedExtensionFilter(
                    new FileChooser.ExtensionFilter("Txt", "*.txt")
            );
            Window owner = null;
            for(Window w : Window.getWindows()) {
                if(w.isShowing()) {
                    owner = w;
                    break;
                }
            }

            File chooseFile = fileChooser.showOpenDialog(owner);
            if(chooseFile != null) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
                String destPath = "D:\\license\\" + chooseFile.getName().split("\\.")[0] + dtf.format(LocalDateTime.now()) + ".txt";
                try {
                    Files.copy(chooseFile.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                overLay.setVisible(true);
                Task<JSONObject> task = new Task<JSONObject>() {
                    @Override
                    protected JSONObject call() throws Exception {
                        return checkStatusLicense(destPath);
                    }
                };
                task.setOnSucceeded(e -> {
                    JSONObject result = task.getValue();
                    setValue(result);
                    overLay.setVisible(false);
                });
                task.setOnFailed(e -> {
                    overLay.setVisible(false);
                    Throwable ex = task.getException();
                    ex.printStackTrace();
                    AlertUtils.showAlert("Lỗi", "Có lỗi xảy ra khi kiểm tra license: ", "ERROR");
                });
                new Thread(task).start();
            }
        });
    }

    private void createInformationApp() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(40, 0, 0, 0));
        String fontText = "-fx-font-size: 16px;";
        Label version = new Label("Version: 1.0.0.20251009");
        Label releaseDate = new Label("Release Date: September 10, 2025");
        Label copyRight = new Label("Copyright © 2010-2025 Newlife Tech. All rights reserved.");
        version.setStyle(fontText);
        releaseDate.setStyle(fontText);
        copyRight.setStyle(fontText);

        String valueStyle = "-fx-font-size: 16px;";
        version.setStyle(valueStyle);
        releaseDate.setStyle(valueStyle);
        copyRight.setStyle(valueStyle);
        grid.add(version, 0, 1, 2, 1);
        GridPane.setHalignment(version, HPos.CENTER);

        grid.add(releaseDate, 0, 2, 2, 1);
        GridPane.setHalignment(releaseDate, HPos.CENTER);

        grid.add(copyRight, 0, 3, 2, 1);
        GridPane.setHalignment(copyRight, HPos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65);
        grid.getColumnConstraints().addAll(col1, col2);

        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        container.getChildren().addAll(grid);
    }

    public JSONObject checkStatusLicense(String licensePath) {
        if(licensePath == null || licensePath.isBlank()) {
            String folderLicense = "D:\\license\\";
            File folder = new File(folderLicense);
            File[] filesLicense = folder.listFiles((dir, filename) -> filename.endsWith(".txt"));
            if(filesLicense != null && filesLicense.length > 0) {
                File newest = Arrays.stream(filesLicense)
                        .max(Comparator.comparingLong(File::lastModified))
                        .orElse(null);
                licensePath = newest.getPath();
                System.out.println(licensePath);
            }
        }
        try {
            String moduleCheckLicense = "D:\\license\\check_license.py";
            String command = "python -u  " + moduleCheckLicense  + " " + licensePath;
            Process processCheckLicense = Runtime.getRuntime().exec(command);
            BufferedReader output = new BufferedReader(new InputStreamReader(processCheckLicense.getInputStream()));
            String line;
            JSONObject response = new JSONObject();
            response.put("location", licensePath);
            while ((line = output.readLine()) != null) {
                if(line.startsWith("status")) {
                    boolean isCheckLicense = line.split("=")[1].toLowerCase().contains("success");
                    response.put("status", isCheckLicense ? "Đã kích hoạt" : "Chưa kích hoạt");
                    if(isCheckLicense) {
                        response.put("icon", "/com/module_service_insert/icons/status_success.png");
                        response.put("style", "-fx-text-fill: #08c608; -fx-font-size: 16px;");
                    }
                    else {
                        response.put("icon", "/com/module_service_insert/icons/status_fail.png");
                        response.put("style", "-fx-text-fill: #f41717; -fx-font-size: 16px;");
                    }
                }
                else if(line.startsWith("type")) {
                    response.put("type", line.split("=")[1].toLowerCase());
                }
                else if(line.startsWith("organization")) {
                    response.put("organization", line.split("=")[1].toLowerCase());
                }
                else if(line.startsWith("expiry")) {
                    response.put("expiry", line.split("=")[1].toLowerCase());
                }
            }
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Label createLabelWithIcon(String text, String iconPath) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(24);
        icon.setFitHeight(24);

        Label label = new Label(text, icon);
        label.setFont(Font.font(16));
        label.setContentDisplay(ContentDisplay.LEFT);
        label.setStyle("-fx-font-weight: bold;");
        label.setGraphicTextGap(15);
        return label;
    }

    public void setValue(JSONObject jsonObject) {
//        Platform.runLater(() -> {
           String status = jsonObject.getString("status");
           String type = jsonObject.getString("type");
           String organization = jsonObject.getString("organization");
           String expiry = jsonObject.getString("expiry");
           String style = jsonObject.getString("style");
           String icon = jsonObject.getString("icon");
           String locationPath = jsonObject.getString("location");

           statusVal.setText(status);
           typeVal.setText(type);
           userVal.setText(organization);
           expiryVal.setText(expiry);
           ImageView iconView = (ImageView) statusLabel.getGraphic();
           iconView.setImage(new Image(getClass().getResourceAsStream(icon)));
           locationVal.setText(locationPath);
           statusVal.setStyle(style);
//        });
        overLay.setVisible(false);
    }
}
