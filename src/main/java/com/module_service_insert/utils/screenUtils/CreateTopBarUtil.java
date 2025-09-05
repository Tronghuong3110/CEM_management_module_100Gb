package com.module_service_insert.utils.screenUtils;

import com.module_service_insert.action.TopBarHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Map;

/**
 * @author Trọng Hướng
 */
public class CreateTopBarUtil {
    public static HBox createTopBar(TopBarHandler handler) {
        Image addIcon = new Image(CreateTopBarUtil.class.getResource("/com/module_service_insert/icons/add.png").toExternalForm()); // Or .svg, .jpg, etc.
        Image editIcon = new Image(CreateTopBarUtil.class.getResource("/com/module_service_insert/icons/pencil.png").toExternalForm());
        Image deleteIcon = new Image(CreateTopBarUtil.class.getResource("/com/module_service_insert/icons/delete.png").toExternalForm());
        Image reloadIcon = new Image(CreateTopBarUtil.class.getResource("/com/module_service_insert/icons/reload.png").toExternalForm());

        Button btnAdd = new Button("Thêm", new ImageView(addIcon));
        Button btnEdit = new Button("Sửa", new ImageView(editIcon));
        Button btnDelete = new Button("Xóa", new ImageView(deleteIcon));
        Button btnReload = new Button("Tải lại", new ImageView(reloadIcon));

        Map<Button, Runnable> buttonActions = Map.of(
                btnAdd, handler::onAdd,
                btnEdit, handler::onEdit,
                btnDelete, handler::onDelete,
                btnReload, handler::onReload
        );

        for (Map.Entry<Button, Runnable> entry : buttonActions.entrySet()) {
            Button btn = entry.getKey();
            ImageView imageView = (ImageView) btn.getGraphic();
            imageView.setFitWidth(16);
            imageView.setPreserveRatio(true);
            AddCssForBtnUtil.addCssStyleForBtn(btn);

            btn.setOnMouseClicked(e -> entry.getValue().run());
        }

        // HBox for the top bar
        HBox topBar = new HBox(10, btnAdd, btnEdit, btnDelete);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(5));

        topBar.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-padding: 11px 5;" +
                "-fx-alignment: center-left;" +
                "-fx-spacing: 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.15, 0, 0);" +
                "-fx-background-radius: 5px"
        );
        return topBar;
    }

    public static HBox createTopBarReadOnly(TopBarHandler handler) {
        Image reloadIcon = new Image(CreateTopBarUtil.class.getResource("/com/module_service_insert/icons/reload.png").toExternalForm());
        Image loadConfigIcon = new Image(CreateTopBarUtil.class.getResource("/com/module_service_insert/icons/import.png").toExternalForm());
        Image exportIcon = new Image(CreateTopBarUtil.class.getResource("/com/module_service_insert/icons/export.png").toExternalForm());

        Button btnImport = new Button("Import Config", new ImageView(loadConfigIcon));
        Button btnReload = new Button("Refresh", new ImageView(reloadIcon));
        Button btnExport = new Button("Export Config", new ImageView(exportIcon));

        Map<Button, Runnable> buttonActions = Map.of (
                btnImport, handler::onImport,
                btnReload, handler::onReload,
                btnExport, handler::onExport
        );

        for (Map.Entry<Button, Runnable> entry : buttonActions.entrySet()) {
            Button btn = entry.getKey();
            ImageView imageView = (ImageView) btn.getGraphic();
            imageView.setFitWidth(16);
            imageView.setPreserveRatio(true);
            AddCssForBtnUtil.addCssStyleForBtn(btn);

            btn.setOnMouseClicked(e -> entry.getValue().run());
        }

        // HBox for the top bar
        HBox topBar = new HBox(10, btnReload, btnImport, btnExport);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(5));

        topBar.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-padding: 11px 5;" +
                        "-fx-alignment: center-left;" +
                        "-fx-spacing: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.15, 0, 0);" +
                        "-fx-background-radius: 5px"
        );
        return topBar;
    }
}
