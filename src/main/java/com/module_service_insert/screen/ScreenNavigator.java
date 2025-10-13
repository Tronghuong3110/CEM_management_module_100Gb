package com.module_service_insert.screen;

import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.screenUtils.CreateLoadingUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Trọng Hướng
 */
public class ScreenNavigator {
    private static BorderPane root;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void initScreen(BorderPane rootPane) {
        root = rootPane;
        setLeftScreen();
    }

    private static void setScreenInternal(Node screen) {
        root.setCenter(screen);
    }

    public static void navigateTo(Node newScreen) {
        setScreenInternal(newScreen);
    }

    public static void showLoading() {
        setScreenInternal(CreateLoadingUtil.createLoading());
    }

    private static void setLeftScreen() {
        BorderPane mainLayout = new BorderPane();

        // ==== Sidebar ====
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.getStylesheets().add(
                Objects.requireNonNull(ScreenNavigator.class.getResource(
                        "/com/module_service_insert/css/sidebar.css")).toExternalForm()
        );

        // Logo
        Label logo = new Label("CEM");
        logo.getStyleClass().add("sidebar-logo");
        logo.setMaxWidth(Double.MAX_VALUE);
        logo.setAlignment(Pos.CENTER);
        VBox logoBox = new VBox(logo);
        logoBox.getStyleClass().add("logo-box");

        // Menu
        VBox menuBox = new VBox(6);
        menuBox.getStyleClass().add("menu-box");
        menuBox.setFillWidth(true);

        List<HBox> items = new ArrayList<>();
        items.add(createSidebarItem("General Config", 26, 26, "/com/module_service_insert/icons/config.png", () -> {
            ScreenNavigator.showLoading();
            Platform.runLater(() -> {
                ConfigGeneralScreen s = new ConfigGeneralScreen();
                s.showTable();
                scheduler.shutdown();
                ScreenNavigator.navigateTo(s);
            });
        }));
        items.add(createSidebarItem("Run Config", 26, 26, "/com/module_service_insert/icons/play_menu.png", () -> {
            ScreenNavigator.showLoading();
            Platform.runLater(() -> {
                ConfigRunModuleScreen s = new ConfigRunModuleScreen(null);
                s.showTable();
                if(scheduler.isShutdown()) {
                    scheduler = Executors.newScheduledThreadPool(1);
                }
                s.setScheduler(scheduler);
                ScreenNavigator.navigateTo(s);
            });
        }));
        items.add(createSidebarItem("About", 30, 30, "/com/module_service_insert/icons/about.png", () -> {
            ScreenNavigator.showLoading();
            scheduler.shutdown();
            AboutScreen about = new AboutScreen();
            Task<JSONObject> task = new Task<JSONObject>() {
                @Override
                protected JSONObject call() throws Exception {
                    return about.checkStatusLicense(null);
                }
            };

            task.setOnSucceeded(event -> {
                JSONObject result = task.getValue();
                about.setValue(result);
                Platform.runLater(() -> {
                    ScreenNavigator.navigateTo(about);
                });
            });
            task.setOnFailed(event -> {
                Throwable ex = task.getException();
                ex.printStackTrace();
                AlertUtils.showAlert("Lỗi", "Có lỗi xảy ra khi kiểm tra license", "ERROR");
            });
            new Thread(task).start();
        }));
        menuBox.getChildren().addAll(items);

        VBox sidebarContent = new VBox(16);
        sidebarContent.getChildren().addAll(logoBox, menuBox);
        VBox.setVgrow(menuBox, Priority.ALWAYS);
        sidebar.setStyle("-fx-font-size: 18px;");
        sidebar.getChildren().add(sidebarContent);

        mainLayout.setLeft(sidebar);
        root.setLeft(mainLayout);

//        if (!items.isEmpty())
        setActive(items, items.get(0));
    }

    // ===== Tạo 1 item trong sidebar =====
    private static HBox createSidebarItem(String text, int width, int height, String iconPath, Runnable action) {
        ImageView icon = new ImageView(new Image(
                Objects.requireNonNull(ScreenNavigator.class.getResourceAsStream(iconPath))
        ));
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.getStyleClass().add("menu-icon");

        Label label = new Label(text);
        label.getStyleClass().add("menu-label");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setTextOverrun(OverrunStyle.ELLIPSIS);
        HBox.setHgrow(label, Priority.ALWAYS);

        HBox box = new HBox(12, icon, label);
        box.getStyleClass().add("menu-item");
        box.setAlignment(Pos.CENTER_LEFT);
        box.setCursor(Cursor.HAND);
        box.setMaxWidth(Double.MAX_VALUE);

        box.setOnMouseClicked(e -> {
            VBox parent = (VBox) box.getParent();
            List<HBox> siblings = parent.getChildren().stream()
                    .filter(n -> n instanceof HBox)
                    .map(n -> (HBox) n)
                    .toList();
            setActive(siblings, box);
            action.run();
        });
        return box;
    }

    // ===== Đặt trạng thái active theo style class ".active" =====
    private static void setActive(List<HBox> allItems, HBox activeItem) {
        for (HBox item : allItems) item.getStyleClass().remove("active");
        if (!activeItem.getStyleClass().contains("active")) activeItem.getStyleClass().add("active");
    }

    private static TreeItem<String> createItem(String name, String iconPath, int height, int width) {
        Image image = null;
        try {
            image = new Image(ScreenNavigator.class.getResource(iconPath).toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(true);
        return new TreeItem<>(name, imageView);
    }

    private static HBox createTitleHeader() {
        HBox statusBar = new HBox(10);
        statusBar.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 5px 10px; -fx-border-color: #d3d3d3; -fx-border-width: 2px; -fx-border-radius: 5px;");

        Label statusLabel = new Label("Quản lý module insert.");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        statusBar.getChildren().add(statusLabel);
        statusBar.setStyle("-fx-background-color: #e1f0f7; -fx-padding: 10px; -fx-border-width: 1; -fx-border-color: #c1dfee");
        return statusBar;
    }
}
