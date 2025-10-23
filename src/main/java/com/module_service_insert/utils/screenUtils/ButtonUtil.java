package com.module_service_insert.utils.screenUtils;

import com.module_service_insert.screen.ConfigRunModuleScreen;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.Objects;

/**
 * @author Trọng Hướng
 */
public class ButtonUtil {

    public static Button createBtn(String label, String iconPath) {
        ImageView iconView = null;
        if (iconPath != null && !iconPath.isEmpty()) {
            iconView = new ImageView(
                    new Image(Objects.requireNonNull(
                            ConfigRunModuleScreen.class.getResourceAsStream(iconPath))
                    )
            );
            iconView.setFitHeight(16);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            iconView.setCache(true);
        }

        Label textLabel = null;
        if (label != null && !label.isEmpty()) {
            textLabel = new Label(label);
        }

        HBox content = new HBox(6);
        content.setAlignment(Pos.CENTER);
        if (iconView != null && textLabel != null) {
            content.getChildren().addAll(iconView, textLabel);
        } else if (iconView != null) {
            content.getChildren().add(iconView);
        } else if (textLabel != null) {
            content.getChildren().add(textLabel);
        }

        // Tạo button
        Button button = new Button();
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setPrefHeight(28);
        button.setMinWidth(Region.USE_PREF_SIZE);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setGraphicTextGap(6);
        button.setStyle("-fx-alignment: center; -fx-padding: 4 8 4 8;");

        return button;
    }

    public static Button createBtn(String iconPath, int height, int width) {
        ImageView iconView = null;
        if (iconPath != null && !iconPath.isEmpty()) {
            iconView = new ImageView(
                    new Image(Objects.requireNonNull(
                            ConfigRunModuleScreen.class.getResourceAsStream(iconPath))
                    )
            );
            iconView.setFitHeight(height);
            iconView.setFitWidth(width);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            iconView.setCache(true);
        }

        HBox content = new HBox(6);
        content.setAlignment(Pos.CENTER);
        if (iconView != null) {
            content.getChildren().add(iconView);
        }

        // Tạo button
        Button button = new Button();
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setPrefHeight(28);
        button.setMinWidth(Region.USE_PREF_SIZE);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setGraphicTextGap(6);
        button.setStyle("-fx-alignment: center; -fx-padding: 4 8 4 8;");

        return button;
    }
}
