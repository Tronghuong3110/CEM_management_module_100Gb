package com.module_service_insert.utils.screenUtils;

import com.module_service_insert.screen.ConfigRunModuleScreen;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * @author Trọng Hướng
 */
public class ButtonUtil {

    public static Button createBtn(String label, String iconPath) {
        Image icon = new Image(Objects.requireNonNull(ConfigRunModuleScreen.class.getResourceAsStream(iconPath)));
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(16);
        iconView.setFitHeight(16);
        Button button = new Button(label, iconView);
        return button;
    }
}
