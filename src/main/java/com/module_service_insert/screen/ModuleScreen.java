package com.module_service_insert.screen;

import com.module_service_insert.action.TopBarHandler;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.model.tableData.ModuleTableData;
import com.module_service_insert.presenter.ModulePresenter;
import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.functionUtils.NormalizeString;
import com.module_service_insert.utils.screenUtils.AddCssForBtnUtil;
import com.module_service_insert.utils.screenUtils.CreateColumnTableUtil;
import com.module_service_insert.utils.screenUtils.CreateTopBarUtil;
import com.module_service_insert.utils.screenUtils.PaginationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * @author Trọng Hướng
 */
public class ModuleScreen extends VBox {

    private final PaginationUtil<ModuleTableData> paginationUtil = new PaginationUtil<>();
    private final ModulePresenter modulePresenter;
    private TableView<ModuleTableData> moduleTable;
    private Pagination modulePagination;
    private ModuleTableData selectedModule;
    private TextField tfModuleName;
    private ObservableList<ModuleTableData> moduletableTableDatas = FXCollections.observableArrayList();
    private ObservableList<ModuleTableData> filteredModuletableTableDatas = FXCollections.observableArrayList();
    private ModuleTableData selected;
    private TextField tfName, tfDescription, tfCommand;

//    private ObservableList<ModuleTableData> sampleData = FXCollections.observableArrayList();
    private ObservableList<ModuleTableData> workingData = FXCollections.observableArrayList();

    public ModuleScreen() {
        modulePresenter = ModulePresenter.getInstance();
        setSpacing(15);
        setStyle("-fx-padding: 5 15px 15 15px; -fx-background-insets: 0; -fx-background-color: #f3f3f3");

        HBox topBar = CreateTopBarUtil.createTopBar(new TopBarHandler() {
            @Override
            public void onAdd() {
                tfCommand.clear();
                tfDescription.clear();
                tfName.clear();

                selected = null;
                AlertUtils.showAlert("Thông báo", "Vui lòng điền thông tin module.", "INFORMATION");
            }

            @Override
            public void onEdit() {
            }

            @Override
            public void onDelete() {

            }

            @Override
            public void onReload() {

            }

            @Override
            public void onExport() {

            }

            @Override
            public void onImport() {

            }
        });
        VBox moduleSection = createModuleTable();
        moduleSection.setPadding(Insets.EMPTY);
        moduleSection.setStyle("-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-background-color: #ffffff;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;");
        modulePagination.setPadding(Insets.EMPTY);
        modulePagination.setStyle("-fx-padding: 0; -fx-background-insets: 0;");
        VBox form = createForm();
        form.setStyle(
                "-fx-background-insets: 0;" +
                "-fx-background-color: #ffffff;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;");
        getChildren().addAll(topBar, form, moduleSection);
    }

    private VBox createModuleTable() {
        moduleTable = new TableView<>();
        TableColumn<ModuleTableData, String> moduleNameCol = CreateColumnTableUtil.createColumn("Module name", ModuleTableData::moduleNameProperty);
        TableColumn<ModuleTableData, String> descriptionCol = CreateColumnTableUtil.createColumn("Description", ModuleTableData::descriptionProperty);
        TableColumn<ModuleTableData, String> commandCol = CreateColumnTableUtil.createColumn("Command", ModuleTableData::commandProperty);
        TableColumn<ModuleTableData, Void> actionCol = new TableColumn<>("Action");

        setColumnPercentWidth(moduleNameCol, 20);
        setColumnPercentWidth(descriptionCol, 25);
        setColumnPercentWidth(commandCol, 25);
        setColumnPercentWidth(actionCol, 30);

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = createIconButton("/com/module_service_insert/icons/pencil.png");
            private final Button deleteBtn = createIconButton("/com/module_service_insert/icons/delete.png");
//            private final Button configBtn = createIconButton("/com/module_service_insert/icons/config.png");
            private final HBox btnBox = new HBox(5, editBtn, deleteBtn);
            {
                btnBox.setAlignment(Pos.CENTER);
                editBtn.setOnAction(e -> {
                    ModuleTableData item = getTableView().getItems().get(getIndex());
                    System.out.println("Edit: " + item.getModuleName());
                    fillInfoModule(item);
                });

                deleteBtn.setOnAction(e -> {
                    ModuleTableData item = getTableView().getItems().get(getIndex());
                    System.out.println("Delete: " + item.getModuleName());
                    boolean isConfirmDelete = AlertUtils.confirm("Xác nhận xóa", "Bạn chắc chắn muốn xóa module: " + item.getModuleName() + " không?");
                    if(isConfirmDelete) {
                        try {
                            modulePresenter.delete(item.getModuleId());
                            AlertUtils.showAlert("Thành công", "Xóa module " + item.getModuleName() + " thành công.", "INFORMATION");
                            filteredModuletableTableDatas.remove(item);
                            moduleTable.refresh();
                        }
                        catch (Exception ex) {
                            AlertUtils.showAlert("Lỗi", ex.getMessage(), "ERROR");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        });

        moduleTable.getColumns().addAll(moduleNameCol, descriptionCol, commandCol, actionCol);

        moduleTable.setPrefHeight(700);
        moduleTable.setMaxHeight(300);
        moduleTable.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");

        moduleTable.setRowFactory(tv -> {
            TableRow<ModuleTableData> row = new TableRow<>() {
                @Override
                protected void updateItem(ModuleTableData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else if (isSelected()) {
                        setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                    } else {
                        if (getIndex() % 2 == 0) {
                            setStyle("-fx-background-color: #ffffff;");
                        } else {
                            setStyle("-fx-background-color: #f1f1f1f1;");
                        }
                    }
                }
            };

            // Sự kiện click
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    selected = row.getItem();
                    tfName.setText(selected.getModuleName());
                    tfDescription.setText(selected.getDescription());
                    tfCommand.setText(selected.getCommand());
                }
            });

            // Lắng nghe chọn/deselect -> đổi màu
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                ModuleTableData item = row.getItem();
                if (item != null) {
                    if (isNowSelected) {
                        row.setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                    } else {
                        if (row.getIndex() % 2 == 0) {
                            row.setStyle("-fx-background-color: #ffffff;");
                        } else {
                            row.setStyle("-fx-background-color: #f1f1f1f1;");
                        }
                    }
                } else {
                    row.setStyle("");
                }
            });

            return row;
        });

        moduleTable.getStylesheets().add(
            Objects.requireNonNull(
                    getClass().getResource("/com/module_service_insert/css/table_row.css")
            ).toExternalForm()
        );

        modulePagination = paginationUtil.createPagination(moduleTable, filteredModuletableTableDatas, moduletableTableDatas);
        HBox inputSearch = createFilterModuleTable();
        VBox box = new VBox(inputSearch, moduleTable, modulePagination);
        box.setSpacing(0);
        box.setPadding(Insets.EMPTY);
        box.setStyle("-fx-padding: 0; -fx-background-insets: 0;");
        paginationUtil.updatePagination(modulePagination, filteredModuletableTableDatas, moduletableTableDatas, moduleTable);
        return box;
    }

    private HBox createFilterModuleTable() {
        tfModuleName = new TextField();
        tfModuleName.setPromptText("Tìm theo tên module");
        tfModuleName.textProperty().addListener((obs, oldVal, newVal) -> filterModuleNameTables());
        tfModuleName.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        HBox filterRow = new HBox(tfModuleName);
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));
        filterRow.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-width: 1px 0 0 0; -fx-border-color: #cccccc");
        return filterRow;
    }

    private void filterModuleNameTables() {
        String moduleNameFilter = NormalizeString.normalizeString(tfModuleName.getText() != null ? tfModuleName.getText().trim().toLowerCase() : "");
        System.out.println(moduleNameFilter);
        workingData.setAll(
                moduletableTableDatas.stream()
                        .filter(item -> {
                            String moduleName = NormalizeString.normalizeString(item.getModuleName() != null ? item.getModuleName().toLowerCase() : "");
                            System.out.println("Module name: " + moduleName + ", is contains: " + moduleName.trim().contains(moduleNameFilter));
                            return moduleName.trim().contains(moduleNameFilter);
                        })
                        .toList()
        );
        paginationUtil.updatePagination(
                modulePagination,
                filteredModuletableTableDatas,
                workingData,
                moduleTable
        );
    }

    protected void setColumnPercentWidth(TableColumn<?, ?> col, double percent) {
        moduleTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            col.setPrefWidth(width * percent / 100.0);
        });
    }

    public void showTable() {
        try {
            this.getStylesheets().add(this.getClass().getResource("/com/module_service_insert/css/header_table.css").toExternalForm());
            List<ModuleTableData> moduleTableDatas = modulePresenter.findAll();
            moduletableTableDatas.setAll(moduleTableDatas);
            filteredModuletableTableDatas.setAll(moduleTableDatas);
            moduleTable.setItems(filteredModuletableTableDatas);
            paginationUtil.updatePagination(modulePagination, filteredModuletableTableDatas,
                    moduletableTableDatas, moduleTable);
        }
        catch (DaoException e) {
            AlertUtils.showAlert("Lỗi", e.getMessage(), "ERROR");
        }
    }

    private VBox createForm() {
        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #32327e; -fx-font-weight: normal;";
        String textFieldStyle = ""
                + "-fx-font-size: 14px;"
                + "-fx-text-fill: #114962;"
                + "-fx-pref-height: 35px;"
                + "-fx-background-color: white;"
                + "-fx-border-color: #cccccc;"
                + "-fx-border-radius: 5px;"
                + "-fx-background-radius: 5px;"
                + "-fx-padding: 0 5 0 5;";

        // === Form bên trái ===
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(15);
        leftForm.setPadding(new Insets(15));

        Label lblName = new Label("Tên module *");
        lblName.setStyle(labelStyle);
        tfName = new TextField();
        tfName.setStyle(textFieldStyle);
        leftForm.add(lblName, 0, 0);
        leftForm.add(tfName, 1, 0);

        Label lblDescription = new Label("Description");
        lblDescription.setStyle(labelStyle);
        tfDescription = new TextField();
        tfDescription.setStyle(textFieldStyle);
        leftForm.add(lblDescription, 0, 1);
        leftForm.add(tfDescription, 1, 1);


        // === Form bên phải ===
        GridPane rightForm = new GridPane();
        rightForm.setHgap(10);
        rightForm.setVgap(15);
        rightForm.setPadding(new Insets(15));

        Label lblCommand = new Label("Command");
        lblCommand.setStyle(labelStyle);
        tfCommand = new TextField();
        tfCommand.setStyle(textFieldStyle);
        rightForm.add(lblCommand, 0, 0);
        rightForm.add(tfCommand, 1, 0);

        // === Gộp 2 form vào 1 hàng ngang ===
        HBox formColumns = new HBox(30, leftForm, rightForm);
        HBox.setHgrow(leftForm, Priority.ALWAYS);
        HBox.setHgrow(rightForm, Priority.ALWAYS);

        // === Giao diện tổng thể ===
        HBox btnRow = createBtnAction();
        VBox root = new VBox(20, formColumns, btnRow);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #ffffff;");
        return root;
    }

    private HBox createBtnAction() {
        HBox actionRow = new HBox(10);
        Button saveBtn = new Button("Save");
        AddCssForBtnUtil.addCssStyleForBtn(saveBtn);

        Button cancelBtn = new Button("Cancel");
        AddCssForBtnUtil.addCssStyleForBtn(cancelBtn);
        cancelBtn.setOnMouseClicked(e -> {
            ModuleScreen exportReceiptScreen = new ModuleScreen();
            exportReceiptScreen.showTable();
            ScreenNavigator.navigateTo(exportReceiptScreen);
        });
        saveBtn.setOnMouseClicked(e -> {
            String moduleName = tfName.getText().trim();
            String description = tfDescription.getText().trim();
            String command = tfCommand.getText().trim();

            if(moduleName.isBlank()) {
               AlertUtils.showAlert("Cảnh báo", "Vui lòng nhập tên module.", "WARNING");
               return;
            }
            if(command.isBlank()) {
                AlertUtils.showAlert("Cảnh báo", "Vui lòng nhập tên command.", "WARNING");
                return;
            }

            try {
                if(selected == null) {
                    ModuleTableData moduleTableData = new ModuleTableData(-1, moduleName, command, description);
                    long moduleId = modulePresenter.save(moduleTableData);
                    System.out.println("Thêm mới thành công.");
                    moduleTableData.setModuleId(moduleId);
                    filteredModuletableTableDatas.add(moduleTableData);
                    AlertUtils.showAlert("Thành công", "Thêm mới module thành công", "INFORMATION");
                }
                else {
                    if(!command.equals(selected.getCommand())) {
                        selected.setCommand(command);
                        selected.setIsChange(true);
                    }
                    if(!description.equals(selected.getDescription())) {
                        selected.setDescription(description);
                        selected.setIsChange(true);
                    }
                    if (!moduleName.equals(selected.getModuleName())) {
                        selected.setModuleName(moduleName);
                        selected.setIsChange(true);
                    }
                    if(selected.isChange()) {
                        modulePresenter.update(selected);
                        AlertUtils.showAlert("Thành công", "Cập nhật module thành công", "INFORMATION");
                    }
                    moduleTable.refresh();
                }
                tfName.clear();
                tfDescription.clear();
                tfCommand.clear();
            }
            catch (DaoException ex) {
                AlertUtils.showAlert("Lỗi", ex.getMessage(), "ERROR");
            }
        });

        actionRow.getChildren().addAll(saveBtn, cancelBtn);
        return actionRow;
    }

    private void fillInfoModule(ModuleTableData moduleTableData) {
        tfName.setText(moduleTableData.getModuleName() != null ? moduleTableData.getModuleName() : "");
        tfDescription.setText(moduleTableData.getDescription() != null ? moduleTableData.getDescription() : "");
        tfCommand.setText(moduleTableData.getCommand() != null ? moduleTableData.getCommand() : "");
    }

    private Button createIconButton(String iconPath) {
        ImageView icon = new ImageView(ConfigGeneralScreen.class.getResource(iconPath).toExternalForm());
        icon.setFitHeight(16);
        icon.setFitWidth(16);
        Button button = new Button();
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return button;
    }
}
