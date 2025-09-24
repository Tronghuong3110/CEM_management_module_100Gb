package com.module_service_insert;

import com.module_service_insert.constant.VariableCommon;
import com.module_service_insert.screen.ConfigGeneralScreen;
import com.module_service_insert.screen.ScreenNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        Scene scene = new Scene(root, 1150, 800);
        stage.setTitle("Ứng dụng quản lý module");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(e -> {
            System.out.println("Closing application");
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equalsIgnoreCase("--config") || args[i].equalsIgnoreCase("-c")) {
                VariableCommon.CONFIG_PATH = args[i + 1];
            }
        }
//        VariableCommon.LICENSE_CHECK_MODULE_PATH = "D:\\license";
//        VariableCommon.LICENSE_CHECK_MODULE_NAME = "python -u D:\\license\\check_license.py ";
        ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
        schedule.scheduleAtFixedRate(() -> {
            File configFile = new File(VariableCommon.CONFIG_PATH);
            if(!configFile.exists()) {
                System.out.println("Config file doesn't exist");
            }
            else {
                try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if(line.startsWith("#") || line.isBlank()) continue;
                        String[] parts = line.split("=");
                        if(line.startsWith("check_license_path")) {
                            if(VariableCommon.LICENSE_CHECK_MODULE_PATH == null || !VariableCommon.LICENSE_CHECK_MODULE_PATH.equalsIgnoreCase(parts[1])) {
                                VariableCommon.LICENSE_CHECK_MODULE_PATH = parts[1];
                            }
                        }
                        if(line.startsWith("check_license_name")) {
                            if(VariableCommon.LICENSE_CHECK_MODULE_NAME == null || !VariableCommon.LICENSE_CHECK_MODULE_NAME.equalsIgnoreCase(parts[1])) {
                                VariableCommon.LICENSE_CHECK_MODULE_NAME = parts[1];
                            }
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10, TimeUnit.MINUTES);
        launch();
    }
}