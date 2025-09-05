package com.module_service_insert;

import com.module_service_insert.screen.ConfigGeneralScreen;
import com.module_service_insert.screen.ScreenNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ModuleInsertApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();

        ScreenNavigator.initScreen(root);

        ScreenNavigator.showLoading();

        Platform.runLater(() -> {
            ConfigGeneralScreen managerNodeAndModuleScreen = new ConfigGeneralScreen();
            managerNodeAndModuleScreen.showTable();
            ScreenNavigator.navigateTo(managerNodeAndModuleScreen);
        });

        Scene scene = new Scene(root, 1000, 800);
        stage.setTitle("Ứng dụng quản lý module");
        stage.setScene(scene);
        stage.show();


        stage.setOnCloseRequest(e -> {
            System.out.println("Closing application");
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}