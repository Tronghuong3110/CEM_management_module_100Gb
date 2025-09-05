package com.module_service_insert.screen;

import com.module_service_insert.action.TopBarHandler;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.model.tableData.ModuleTableData;
import com.module_service_insert.model.tableData.ClusterTableData;
import com.module_service_insert.presenter.ClusterPresenter;
import com.module_service_insert.presenter.ModulePresenter;
import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.functionUtils.NormalizeString;
import com.module_service_insert.utils.screenUtils.AddCssForBtnUtil;
import com.module_service_insert.utils.screenUtils.CreateColumnTableUtil;
import com.module_service_insert.utils.screenUtils.CreateTopBarUtil;
import com.module_service_insert.utils.screenUtils.PaginationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Trọng Hướng
 */
public class DetailNodeScreen extends VBox {

    private TextField tfId, tfName, tfDescription;
    private ComboBox<String> comboBoxStatus;
    private boolean isLoadedModules = false;
    private final PaginationUtil<ModuleTableData> paginationUtil = new PaginationUtil();

    private final ModulePresenter modulePresenter;
    private final ClusterPresenter clusterPresenter;

    private TableView<ModuleTableData> moduleTable;
    private Pagination modulePagination;
    private ModuleTableData selectedModule;
    private TextField tfModuleName;
    private ObservableList<ModuleTableData> moduletableTableDatas = FXCollections.observableArrayList();
    private ObservableList<ModuleTableData> filteredModuletableTableDatas = FXCollections.observableArrayList();
    private List<Long> idsToDelete = new ArrayList<>();

    private ObservableList<ModuleTableData> sampleData = FXCollections.observableArrayList();
    private FilteredList<ModuleTableData> filteredModule = new FilteredList<>(sampleData, p -> true);

    public DetailNodeScreen(ClusterTableData oldNodeTableData) {
        modulePresenter = ModulePresenter.getInstance();
        clusterPresenter = ClusterPresenter.getInstance();
        setSpacing(15);
        setStyle("-fx-padding: 5 15px 15 15px; -fx-background-insets: 0; -fx-background-color: #f3f3f3");

        HBox topBar = CreateTopBarUtil.createTopBar(new TopBarHandler() {
            @Override
            public void onAdd() {

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

        VBox formAddNew = createFormAddNew(oldNodeTableData);
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

        HBox btnActionRow = createButtonRow(oldNodeTableData);

        getChildren().addAll(topBar, formAddNew, moduleSection, btnActionRow);
    }

    private VBox createFormAddNew(ClusterTableData oldNodeTableData) {
        // === Form bên trái ===
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(10);
        leftForm.setPadding(new Insets(15));

        leftForm.add(new Label("Mã cụm *"), 0, 0);
        tfId = new TextField();
        setColorBorder(tfId);
        leftForm.add(tfId, 1, 0);

        leftForm.add(new Label("Tên Cụm *"), 0, 1);
        tfName = new TextField();
        setColorBorder(tfName);
        leftForm.add(tfName, 1, 1);

        // === Form bên phải ===
        GridPane rightForm = new GridPane();
        rightForm.setHgap(10);
        rightForm.setVgap(10);
        rightForm.setPadding(new Insets(15));

        rightForm.add(new Label("Trạng thái cụm *"), 0, 0);
        comboBoxStatus = new ComboBox<>();
        comboBoxStatus.getItems().addAll("Active", "Inactive");
        comboBoxStatus.setValue("Active");
        comboBoxStatus.setEditable(false);
        comboBoxStatus.getStyleClass().add("custom-combobox");
        setColorBorder(comboBoxStatus);
        rightForm.add(comboBoxStatus, 1, 0);

        rightForm.add(new Label("Mô tả *"), 0, 1);
        tfDescription = new TextField();
        setColorBorder(tfDescription);
        rightForm.add(tfDescription, 1, 1);

        if (oldNodeTableData != null) {
            tfId.setText(oldNodeTableData.getId() != null ? String.valueOf(oldNodeTableData.getId()) : "");
            tfName.setText(oldNodeTableData.getName() != null ? oldNodeTableData.getName() : "");
            comboBoxStatus.setValue(oldNodeTableData.getStatusStr() != null ? oldNodeTableData.getStatusStr() : "");
            tfDescription.setText(oldNodeTableData.getDescription() != null ? oldNodeTableData.getDescription() : "");

            // lấy danh sách module của cụm
            List<ModuleTableData> moduleTableDataByCluster = clusterPresenter.findAllByCluster(oldNodeTableData.getId());
            moduletableTableDatas.setAll(moduleTableDataByCluster);
            filteredModuletableTableDatas.setAll(moduleTableDataByCluster);
        }

        // === Gộp 2 form vào 1 hàng ngang ===
        HBox formColumns = new HBox(30, leftForm, rightForm);
        HBox.setHgrow(leftForm, Priority.ALWAYS);
        HBox.setHgrow(rightForm, Priority.ALWAYS);

        // === Form module ===
        Label lbProduct = new Label("Chọn module *");
        ComboBox<ModuleTableData> cbModule = new ComboBox<>();
        cbModule.setPromptText("Tìm theo tên module");
        cbModule.setEditable(true);
        cbModule.getStyleClass().add("custom-combobox");
        setColorBorder(cbModule);
        VBox productCol = new VBox(5, lbProduct, cbModule);

        Label lbCpuList = new Label("Danh sách cpu *");
        TextField tfCpuList = new TextField();
        setColorBorder(tfCpuList);
        VBox cpuListCol = new VBox(5, lbCpuList, tfCpuList);

        Label lbFolderConfig = new Label("Chọn file cấu hình *");
        TextField tfFolderConfig = new TextField();
        tfFolderConfig.setEditable(false);
        setColorBorder(tfFolderConfig);
        tfFolderConfig.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file cấu hình");
            File selected = fileChooser.showOpenDialog(tfFolderConfig.getScene().getWindow());
            if(selected != null) {
                tfFolderConfig.setText(selected.getAbsolutePath());
            }
        });
        VBox folderConfigCol = new VBox(5, lbFolderConfig, tfFolderConfig);

        Label lbRunPath = new Label("Chọn folder chạy *");
        TextField tfRunPath = new TextField();
        tfRunPath.setEditable(false);
        setColorBorder(tfRunPath);
        tfRunPath.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn folder chạy");
            File selected = fileChooser.showOpenDialog(tfRunPath.getScene().getWindow());
            if(selected != null) {
                tfRunPath.setText(selected.getAbsolutePath());
            }
        });
        VBox runPathCol = new VBox(5, lbRunPath, tfRunPath);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAddModule = new Button("Thêm");
        AddCssForBtnUtil.addCssStyleForBtn(btnAddModule);
        btnAddModule.setPrefWidth(150);
        btnAddModule.setPrefHeight(36);

        VBox buttonBox = new VBox(btnAddModule);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setMaxWidth(Double.MAX_VALUE);

        HBox moduleSection = new HBox(25, productCol, cpuListCol, folderConfigCol, runPathCol, buttonBox);
        moduleSection.setPadding(new Insets(10));
        moduleSection.setAlignment(Pos.CENTER_LEFT);

        // === Logic load dữ liệu cbProduct ===
        cbModule.setOnMouseClicked(e -> {
            if (!isLoadedModules) {
                initData();
                cbModule.setItems(sampleData);
                isLoadedModules = true;
            }
        });

        cbModule.setItems(filteredModule);

        cbModule.setConverter(new StringConverter<ModuleTableData>() {
            @Override
            public String toString(ModuleTableData module) {
                return (module == null) ? "" : module.getModuleName();
            }
            @Override
            public ModuleTableData fromString(String string) {
                return sampleData.stream()
                        .filter(p -> p.getModuleName().equals(string))
                        .findFirst().orElse(null);
            }
        });

        cbModule.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!isLoadedModules) {
                initData();
                filteredModule = new FilteredList<>(sampleData, p -> true);
                cbModule.setItems(filteredModule);
                isLoadedModules = true;
            }
            ModuleTableData selected = cbModule.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getModuleName().equals(newText)) return;

            String normalizedInput = NormalizeString.normalizeString(newText);
            filteredModule.setPredicate(module -> NormalizeString.normalizeString(module.getModuleName())
                    .contains(normalizedInput));

            if (!cbModule.isShowing()) cbModule.show();
        });
        // === Giao diện tổng thể ===
        VBox root = new VBox(20, formColumns, moduleSection);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-background-color: #ffffff;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;");

        root.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/module_service_insert/css/combobox_style.css")
                ).toExternalForm()
        );

        // xử lý khi click vào thêm
        btnAddModule.setOnAction(e -> {
            ModuleTableData selected = cbModule.getSelectionModel().getSelectedItem();
            if(selected == null) {
                AlertUtils.showAlert("Cảnh báo.", "VUi lòng chọn module.", "WARNING");
                return;
            }

            String cpuList =  tfCpuList.getText().trim();
            String runPath =  tfRunPath.getText().trim();
            String clusterIdStr = tfId.getText().trim();
            String configPath = tfFolderConfig.getText().trim();
            if(cpuList.isEmpty()) {
                AlertUtils.showAlert("Cảnh báo.", "Vui lòng cấu hình danh sách cpu chạy cho module.", "WARNING");
                return;
            }
            if(runPath.isBlank()) {
                AlertUtils.showAlert("Cảnh báo.", "Vui lòng chọn cấu hình folder chạy cho module.", "WARNING");
                return;
            }
            if(clusterIdStr.isEmpty()) {
                AlertUtils.showAlert("Cảnh báo.", "Vui lòng nhập id cụm.", "WARNING");
                return;
            }
            if(configPath.isEmpty()) {
                AlertUtils.showAlert("Cảnh báo.", "Vui lòng chọn file config cho module.", "WARNING");
                return;
            }
            ModuleTableData moduleTableData = new ModuleTableData(
                    -1,
                    selected.getModuleName(),
                    cpuList,
                    runPath,
                    configPath,
                    selected.getCommand(),
                    selected.getModuleId()
            );
            filteredModuletableTableDatas.add(moduleTableData);
            moduletableTableDatas.add(moduleTableData);

            tfFolderConfig.clear();
            tfRunPath.clear();
            tfCpuList.clear();
            tfModuleName.clear();
            cbModule.getSelectionModel().clearSelection();
        });

        return root;
    }

    private VBox createModuleTable() {
        moduleTable = new TableView<>();
        TableColumn<ModuleTableData, String> moduleNameCol = CreateColumnTableUtil.createColumn("Module name", ModuleTableData::moduleNameProperty);
        TableColumn<ModuleTableData, String> cpuListCol = CreateColumnTableUtil.createColumn("Cpu list", ModuleTableData::cpuListProperty);
        TableColumn<ModuleTableData, String> runFolderCol = CreateColumnTableUtil.createColumn("Run path", ModuleTableData::runFolderProperty);
        TableColumn<ModuleTableData, String> folderConfigCol = CreateColumnTableUtil.createColumn("Config path", ModuleTableData::configFolderProperty);
        TableColumn<ModuleTableData, String> commandCol = CreateColumnTableUtil.createColumn("Command", ModuleTableData::commandProperty);
        TableColumn<ModuleTableData, Void> actionCol = new TableColumn<>("Action");

        setColumnPercentWidth(moduleNameCol, 20);
        setColumnPercentWidth(cpuListCol, 10);
        setColumnPercentWidth(runFolderCol, 20);
        setColumnPercentWidth(folderConfigCol, 20);
        setColumnPercentWidth(commandCol, 20);
        setColumnPercentWidth(actionCol, 10);

        moduleTable.getColumns().addAll(moduleNameCol, cpuListCol, runFolderCol, folderConfigCol, commandCol, actionCol);

        moduleTable.setPrefHeight(700);
        moduleTable.setMaxHeight(300);
//        moduleTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c1dfee;");

        moduleTable.setRowFactory(tv -> new TableRow<>() {
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
            {
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    updateItem(getItem(), isEmpty());
                });
            }
        });

        moduleTable.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/module_service_insert/css/table_row.css")
                ).toExternalForm()
        );

        this.getStylesheets().add(this.getClass().getResource("/com/module_service_insert/css/header_table.css").toExternalForm());
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
        moduleTable.setItems(filteredModuletableTableDatas);

        // set action
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = createIconButton("/com/module_service_insert/icons/delete.png");
            private final HBox btnBox = new HBox(5, deleteBtn);
            {
                btnBox.setAlignment(Pos.CENTER);
                deleteBtn.setOnAction(e -> {
                    ModuleTableData item = getTableView().getItems().get(getIndex());
                    idsToDelete.add(item.getClusterModuleId());
                    getTableView().getItems().remove(getIndex());
                    moduleTable.refresh();
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
        filterRow.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10; -fx-background-radius: 10");
        return filterRow;
    }

    private void filterModuleNameTables() {
        String moduleNameFilter = NormalizeString.normalizeString(tfModuleName.getText() != null ? tfModuleName.getText().trim().toLowerCase() : "");
        filteredModuletableTableDatas.setAll(
                moduletableTableDatas.stream()
                        .filter(item -> {
                            String moduleName = NormalizeString.normalizeString(item.getModuleName() != null ? item.getModuleName().toLowerCase() : "");
                            return moduleName.contains(moduleNameFilter);
                        })
                        .toList()
        );
        paginationUtil.updatePagination(modulePagination, filteredModuletableTableDatas, moduletableTableDatas, moduleTable);
    }

    protected void setColumnPercentWidth(TableColumn<?, ?> col, double percent) {
        moduleTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            col.setPrefWidth(width * percent / 100.0);
        });
    }

    private void initData() {
        try {
            List<ModuleTableData> moduleTableDatas = modulePresenter.findAll();
            sampleData.addAll(moduleTableDatas);
        }
        catch (DaoException e) {
            AlertUtils.showAlert("Lỗi", e.getMessage(), "ERROR");
        }
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

    private void setColorBorder(Node node) {
        node.setStyle("-fx-border-color: #cfcfcf;");
    }

    private HBox createButtonRow(ClusterTableData oldClusterTableData) {
        HBox actionRow = new HBox(10);
        Button saveBtn = new Button("Save");
        AddCssForBtnUtil.addCssStyleForBtn(saveBtn);
        saveBtn.setOnMouseClicked(e -> {
            try {
                String clusterName = tfName.getText();
                String status = comboBoxStatus.getValue();
                String description = tfDescription.getText();

                if(oldClusterTableData != null) {
                    if(!clusterName.equals(oldClusterTableData.getName())) {
                        oldClusterTableData.setName(clusterName);
                    }
                    if(!status.equals(oldClusterTableData.getStatusStr())) {
                        oldClusterTableData.setStatusStr(status);
                    }
                    if(!description.equals(oldClusterTableData.getDescription())) {
                        oldClusterTableData.setDescription(description);
                    }

                    clusterPresenter.update(oldClusterTableData, moduletableTableDatas, idsToDelete);
                    AlertUtils.showAlert("Thành công", "Cập nhật thông tin thành công.", "INFORMATION");
                }
                else {
                    ClusterTableData clusterTableData = new ClusterTableData(
                            description, clusterName, status
                    );

                    clusterPresenter.save(clusterTableData, moduletableTableDatas);

                    AlertUtils.showAlert("Thành công", "Thêm mới thành công.", "INFORMATION");
                }

                ConfigGeneralScreen managerNodeAndModuleScreen = new ConfigGeneralScreen();
//                managerNodeAndModuleScreen.showTable();
                ScreenNavigator.navigateTo(managerNodeAndModuleScreen);
            }
            catch (DaoException ex) {
                ex.printStackTrace();
                AlertUtils.showAlert("Lỗi", ex.getMessage(), "ERROR");
            }
        });

        Button cancelBtn = new Button("Cancel");
        AddCssForBtnUtil.addCssStyleForBtn(cancelBtn);
        cancelBtn.setOnMouseClicked(e -> {
            ConfigGeneralScreen managerNodeAndModuleScreen = new ConfigGeneralScreen();
//            managerNodeAndModuleScreen.showTable();
            ScreenNavigator.navigateTo(managerNodeAndModuleScreen);
        });

        actionRow.getChildren().addAll(saveBtn, cancelBtn);
        actionRow.setStyle("-fx-padding: 10;" +
                "-fx-background-insets: 0;" +
                "-fx-background-color: #ffffff;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;");
        return actionRow;
    }
}
