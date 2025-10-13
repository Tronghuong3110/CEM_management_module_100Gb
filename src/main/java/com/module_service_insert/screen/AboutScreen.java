package com.module_service_insert.screen;

import com.module_service_insert.constant.VariableCommon;
import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.screenUtils.AddCssForBtnUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger logger = LoggerFactory.getLogger(AboutScreen.class);

    private VBox container;
    private StackPane overLay;

    private Label statusVal, failureCauseVal, licenseNameVal, locationVal, expiryVal, statusLabel;

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
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Labels với icon
        statusVal  = new Label("Chưa kích hoạt");
        failureCauseVal     = new Label("N/A");
        licenseNameVal     = new Label("N/A");
        locationVal = new Label("N/A");
        expiryVal   = new Label("N/A");

        statusLabel   = createLabelWithIcon("Trạng thái", "/com/module_service_insert/icons/status_fail.png");
        Label failureCauseLabel     = createLabelWithIcon("Mã lỗi", "/com/module_service_insert/icons/type.png");
        Label licenseNameLabel     = createLabelWithIcon("Tên license", "/com/module_service_insert/icons/user_use.png");
//        Label locationLabel = createLabelWithIcon("Vị trí license", "/com/module_service_insert/icons/folder.png");
        Label expiryLabel   = createLabelWithIcon("Thời gian license", "/com/module_service_insert/icons/expiry.png");

        String valueStyle = "-fx-font-size: 18px;";
        statusVal.setStyle("-fx-text-fill: #f41717;" + valueStyle);
        failureCauseVal.setStyle(valueStyle);
        licenseNameVal.setStyle(valueStyle);
        locationVal.setStyle(valueStyle);
        expiryVal.setStyle(valueStyle);

        grid.add(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);
        grid.addRow(1, statusLabel, statusVal);
        grid.addRow(2, failureCauseLabel, failureCauseVal);
        grid.addRow(3, licenseNameLabel, licenseNameVal);
//        grid.addRow(4, locationLabel, locationVal);
        grid.addRow(4, expiryLabel, expiryVal);

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
        btnCheck.setMinWidth(Region.USE_PREF_SIZE);
        btnImport.setMinWidth(Region.USE_PREF_SIZE);
        btnCheck.setMaxWidth(Region.USE_PREF_SIZE);
        btnImport.setMaxWidth(Region.USE_PREF_SIZE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonBox = new HBox(10, spacer, btnCheck, btnImport);
//        buttonBox.setStyle("-fx-border-width: 1; -fx-border-color: #000000");
        buttonBox.setMaxWidth(Region.USE_PREF_SIZE);
        buttonBox.setMinWidth(Region.USE_PREF_SIZE);

        grid.add(buttonBox, 0, 6, 2, 1);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);
        GridPane.setValignment(buttonBox, VPos.BOTTOM);

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
                    new FileChooser.ExtensionFilter("Lic", "*.lic")
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
                String destPath = VariableCommon.LICENSE_CHECK_MODULE_PATH + "/" + chooseFile.getName().split("\\.")[0] + "_" + dtf.format(LocalDateTime.now()) + ".lic";
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
        String fontText = "-fx-font-size: 18px;";
        Label version = new Label("Version: 1.0.0.20251009");
        Label releaseDate = new Label("Release Date: September 10, 2025");
        Label copyRight = new Label("Copyright © 2010-2025 Newlife Tech. All rights reserved.");
        version.setStyle(fontText);
        releaseDate.setStyle(fontText);
        copyRight.setStyle(fontText);

        String valueStyle = "-fx-font-size: 18px;";
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
            String folderLicense = VariableCommon.LICENSE_CHECK_MODULE_PATH; // đường dẫn tới folder chưa các file license
            File folder = new File(folderLicense);
            File[] filesLicense = folder.listFiles((dir, filename) -> filename.endsWith(".lic"));
            if(filesLicense != null && filesLicense.length > 0) {
                File newest = Arrays.stream(filesLicense)
                        .max(Comparator.comparingLong(File::lastModified))
                        .orElse(null);
                licensePath = newest.getPath();
            }
        }
        try {
//            String command = VariableCommon.LICENSE_CHECK_MODULE_PATH + "/" + VariableCommon.LICENSE_CHECK_MODULE_NAME + " -e"; // tên module chạy check license
            String[] command = {"/bin/bash", "-c",
                    "echo '123456' | sudo -S " + VariableCommon.LICENSE_CHECK_MODULE_PATH + "/" + VariableCommon.LICENSE_CHECK_MODULE_NAME + " -e"
            };
            logger.info("Start check license, command: " + Arrays.toString(command));
            Process processCheckLicense = Runtime.getRuntime().exec(command);
            BufferedReader output = new BufferedReader(new InputStreamReader(processCheckLicense.getInputStream()));
            String line;
            JSONObject response = new JSONObject();
            System.out.println(licensePath);
            response.put("location", licensePath == null ? "N/A" : licensePath);
            response.put("failure_cause", "N/A");
            response.put("license_time", "N/A");
            response.put("license_name", "N/A");
            System.out.println(response);
            boolean isLicenseError = false;
            while ((line = output.readLine()) != null) {
                if(line.startsWith("License validation")) {
                    isLicenseError = line.toLowerCase().contains("failed");
                    System.out.println(line);
                    response.put("status", !isLicenseError ? "Đã kích hoạt" : "Chưa kích hoạt");
                    if(!isLicenseError) {
                        response.put("icon", "/com/module_service_insert/icons/status_success.png");
                        response.put("style", "-fx-text-fill: #08c608; -fx-font-size: 18px;");
                    }
                    else {
                        response.put("icon", "/com/module_service_insert/icons/status_fail.png");
                        response.put("style", "-fx-text-fill: #f41717; -fx-font-size: 18px;");
                    }
                }
                if(line.startsWith("License name")) {
                    response.put("license_name", line.split(": ")[1]);
                    continue;
                }
                if(line.startsWith("License time")) {
                    response.put("license_time", line.split(": ")[1]);
                    continue;
                }
                if(line.startsWith("Failure cause")) {
                    response.put("failure_cause", line.split(": ")[1]);
                }
            }
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Check license error, details: ", e);
            throw new RuntimeException(e);
        }
    }

    private Label createLabelWithIcon(String text, String iconPath) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(24);
        icon.setFitHeight(24);

        Label label = new Label(text, icon);
        label.setFont(Font.font(18));
        label.setContentDisplay(ContentDisplay.LEFT);
        label.setStyle("-fx-font-weight: bold;");
        label.setGraphicTextGap(15);
        return label;
    }

    public void setValue(JSONObject jsonObject) {
       String status = jsonObject.get("status") != null ? jsonObject.get("status").toString() : "N/A";
       String failureCause = jsonObject.get("failure_cause") != null ?  jsonObject.get("failure_cause").toString() : "N/A";
       String licenseName = jsonObject.get("license_name") != null ? jsonObject.getString("license_name") : "N/A";
       String expiry = jsonObject.get("license_time") != null ? jsonObject.getString("license_time") : "N/A";
       String style = jsonObject.get("style") != null ? jsonObject.getString("style") : "-fx-text-fill: #08c608;";
       String icon = jsonObject.get("icon") != null ? jsonObject.getString("icon") : "N/A";
       String locationPath = jsonObject.get("location") != null ? jsonObject.get("location").toString() : "N/A";

       statusVal.setText(status);
       failureCauseVal.setText(failureCause);
       licenseNameVal.setText(licenseName);
       expiryVal.setText(expiry);
       ImageView iconView = (ImageView) statusLabel.getGraphic();
       iconView.setImage(new Image(getClass().getResourceAsStream(icon)));
       locationVal.setText(locationPath);
       statusVal.setStyle(style);
       overLay.setVisible(false);
    }
}
