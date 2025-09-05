package com.module_service_insert.screen;

import com.module_service_insert.action.TopBarHandler;
import com.module_service_insert.model.ConfigModel;
import com.module_service_insert.model.tableData.ConfigClusterModuleTableData;
import com.module_service_insert.model.tableData.ConfigRunModuleTableData;
import com.module_service_insert.presenter.ConfigPresenter;
import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.functionUtils.NormalizeString;
import com.module_service_insert.utils.screenUtils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Trọng Hướng
 */
public class ConfigRunModuleScreen extends VBox {

    private final PaginationUtil<ConfigRunModuleTableData> paginationUtil = new PaginationUtil();
    private final ConfigPresenter configPresenter;

    private TableView<ConfigRunModuleTableData> configRunModuleTable;
    private Pagination pagination;
    private ObservableList<ConfigRunModuleTableData> configRunModuleTableDatas = FXCollections.observableArrayList();
    private ObservableList<ConfigRunModuleTableData> filterConfigRunModuleTableDatas = FXCollections.observableArrayList();

    private TextField tfModuleName;
    private ComboBox<String> cbStatus;
    private ComboBox<ConfigModel> cbConfigByName;

    private VBox splitPane = new VBox();
    private ObservableList<ConfigClusterModuleTableData> sampleData = FXCollections.observableArrayList();
    private String configName;

    public ConfigRunModuleScreen(String name) {
        configPresenter = ConfigPresenter.getInstance();
        configName = name;
        setSpacing(15);
        setStyle("-fx-padding: 5 15 15 15; -fx-background-insets: 0; -fx-background-color: #f3f3f3");

        HBox topBar = createTopBar();
        HBox inputSearch = createFilterRow();

        createInfoConfigTable();
        pagination = paginationUtil.createPagination(configRunModuleTable, filterConfigRunModuleTableDatas, configRunModuleTableDatas);

        VBox infoConfigSection = new VBox(inputSearch, configRunModuleTable, pagination);
        infoConfigSection.setSpacing(0);
        infoConfigSection.setPadding(Insets.EMPTY);
        infoConfigSection.setStyle("-fx-padding: 0; -fx-background-insets: 0;");

        configRunModuleTable.setPadding(Insets.EMPTY);
        pagination.setPadding(Insets.EMPTY);
        pagination.setStyle("-fx-padding: 0; -fx-background-insets: 0;");

        splitPane.getChildren().addAll(infoConfigSection);
        splitPane.setPadding(Insets.EMPTY);
        splitPane.setStyle("""
            -fx-padding: 0;
            -fx-background-insets: 0;
            -fx-background-color: transparent;
            -fx-border-width: 0;
            -fx-divider-width: 1;
        """);

        VBox.setVgrow(splitPane, Priority.ALWAYS);

        getChildren().addAll(topBar, splitPane);
    }

    private void createInfoConfigTable() {
        configRunModuleTable = new TableView<>();
        TableColumn<ConfigRunModuleTableData, Boolean> selectCol = new TableColumn<>("");
        TableColumn<ConfigRunModuleTableData, String> moduleNameCol = CreateColumnTableUtil.createColumn("Module name", ConfigRunModuleTableData::moduleNameProperty);
        TableColumn<ConfigRunModuleTableData, String> commandCol = CreateColumnTableUtil.createColumn("Command", ConfigRunModuleTableData::commandProperty);
        TableColumn<ConfigRunModuleTableData, String> statusCol = CreateColumnTableUtil.createColumn("Status", ConfigRunModuleTableData::statusProperty);
        TableColumn<ConfigRunModuleTableData, String> interfaceNameCol = CreateColumnTableUtil.createColumn("Interface name", ConfigRunModuleTableData::interfaceNameProperty);
        TableColumn<ConfigRunModuleTableData, String> clusterNameCol =CreateColumnTableUtil.createColumn("Cluster name", ConfigRunModuleTableData::clusterNameProperty);
        TableColumn<ConfigRunModuleTableData, Void> actionColumn = new TableColumn<>("Action");

        setColumnPercentWidth(selectCol, 5);
        setColumnPercentWidth(moduleNameCol, 10);
        setColumnPercentWidth(statusCol, 10);
        setColumnPercentWidth(commandCol, 10);
        setColumnPercentWidth(interfaceNameCol, 10);
        setColumnPercentWidth(clusterNameCol, 15);
        setColumnPercentWidth(actionColumn, 20);

        configRunModuleTable.getColumns().addAll(selectCol, moduleNameCol, statusCol, commandCol, interfaceNameCol, clusterNameCol, actionColumn);

        configRunModuleTable.setItems(filterConfigRunModuleTableDatas);
        configRunModuleTable.setPrefHeight(600);
        configRunModuleTable.setMaxHeight(300);
        configRunModuleTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        configRunModuleTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ConfigRunModuleTableData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #ffffff;");
                    } else {
                        setStyle("-fx-background-color: #e0f2f7;");
                    }
                }
            }
            {
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    updateItem(getItem(), isEmpty());
                });
            }
        });
        configRunModuleTable.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/module_service_insert/css/table_row.css")
                ).toExternalForm()
        );

        statusCol.setCellFactory(col -> new TableCell<>() {
            private final Circle circle = new Circle(8);
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    switch (status.toLowerCase()) {
                        case "active" -> circle.setFill(Color.GREEN);
                        case "inactive" -> circle.setFill(Color.GRAY);
                        case "error" -> circle.setFill(Color.RED);
                        default -> circle.setFill(Color.LIGHTGRAY);
                    }
                    setGraphic(circle);
                    setText(null);
                }
            }
        });

        CheckBox selectAllCheckBox = new CheckBox();
        selectCol.setGraphic(selectAllCheckBox);
        selectCol.setEditable(true);

        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));

        selectAllCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            for (ConfigRunModuleTableData item : configRunModuleTable.getItems()) {
                item.setSelected(newVal);
            }
        });

         configRunModuleTable.setRowFactory(tv -> {
            TableRow<ConfigRunModuleTableData> row = new TableRow<>() {
                @Override
                protected void updateItem(ConfigRunModuleTableData item, boolean empty) {
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

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    ConfigRunModuleTableData item = row.getItem();
                    item.setSelected(!item.isSelected());  // đảo trạng thái
                }
            });

            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                ConfigRunModuleTableData item = row.getItem();
                if (item != null) {
                    if (isNowSelected) {
                        row.setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                    } else {
                        if (row.getIndex() % 2 == 0) {
                            row.setStyle("-fx-background-color: #ffffff;");
                        } else {
                            row.setStyle("-fx-background-color: #e0f2f7;");
                        }
                    }
                }
            });

            return row;
        });
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button startBtn = createIconButton("/com/module_service_insert/icons/play.png", 20, 20);
            private final Button stopBtn = createIconButton("/com/module_service_insert/icons/stop.png", 20, 20);
            private final HBox btnBox = new HBox(5, startBtn, stopBtn);
            {
                btnBox.setAlignment(Pos.CENTER);
                startBtn.setOnAction(e -> {
                    ConfigRunModuleTableData item = getTableView().getItems().get(getIndex());
                    System.out.println("Start: " + item.getModuleName());
                });

                stopBtn.setOnAction(e -> {
                    ConfigRunModuleTableData item = getTableView().getItems().get(getIndex());
                    System.out.println("Stop: " + item.getModuleName());
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
    }

    protected void setColumnPercentWidth(TableColumn<?, ?> col, double percent) {
        configRunModuleTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            col.setPrefWidth(width * percent / 100.0);
        });
    }

    private HBox createTopBar() {
        return CreateTopBarUtil.createTopBarReadOnly(getTopBarHandler());
    }

    private TopBarHandler getTopBarHandler() {
        return new TopBarHandler() {

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
        };
    }

    protected HBox createFilterRow() {
        tfModuleName = new TextField();
        tfModuleName.setPromptText("Tìm theo tên cụm");
        tfModuleName.textProperty().addListener((obs, o, n) -> filterNode());
        tfModuleName.getStyleClass().add("filter-text-field");

        cbStatus = new ComboBox<>();
        cbStatus.setPromptText("Lọc theo trạng thái");
        cbStatus.getItems().addAll("All", "Active", "Inactive");
        cbStatus.getSelectionModel().selectFirst();
        cbStatus.valueProperty().addListener((obs, o, n) -> filterNode());
        cbStatus.getStyleClass().add("filter-combo-box");

        cbStatus.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
            }
        });

        List<ConfigModel> configModels = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            configModels.add(new ConfigModel(
                "Config_" + i
            ));
        }
        cbConfigByName = new ComboBox<>();
        cbConfigByName.getItems().addAll(configModels);
        cbConfigByName.setPromptText("Chọn bản cấu hình theo tên");
        if(configName == null || configName.isBlank()) {
            cbConfigByName.getSelectionModel().selectFirst();
        }
        else {
            configModels.stream()
                    .filter(c -> c.getName().equals(configName))
                    .findFirst()
                    .ifPresent(c -> cbConfigByName.getSelectionModel().select(c));
        }
        cbConfigByName.setConverter(new StringConverter<ConfigModel>() {
            @Override
            public String toString(ConfigModel config) {
                return (config == null) ? "" : config.getName();
            }

            @Override
            public ConfigModel fromString(String string) {
                return cbConfigByName.getItems().stream()
                        .filter(c -> c.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        cbConfigByName.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Chọn config: " + newVal.getName());
            }
        });

        cbConfigByName.setCellFactory(lv -> new ListCell<ConfigModel>() {
            @Override
            protected void updateItem(ConfigModel item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? "" : item.getName());
            }
        });

        cbConfigByName.setButtonCell(new ListCell<ConfigModel>() {
            @Override
            protected void updateItem(ConfigModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        Button runAllBtn = ButtonUtil.createBtn("Run all", "/com/module_service_insert/icons/play.png");
        AddCssForBtnUtil.addCssStyleForBtn(runAllBtn);
        runAllBtn.setOnAction(e -> {
            boolean isRun = false;
            for (ConfigRunModuleTableData item : configRunModuleTable.getItems()) {
                if (item.isSelected()) {
                    isRun = true;
                    System.out.println("Chạy module: " + item.getModuleName());
                }
            }
            if(!isRun) {
                AlertUtils.showAlert("Cảnh báo", "Vui lòng chọn ít nhất một module cần chạy", "WARNING");
            }
        });

        Button stopAllBtn = ButtonUtil.createBtn("Stop all", "/com/module_service_insert/icons/stop.png");
        AddCssForBtnUtil.addCssStyleForBtn(stopAllBtn);
        stopAllBtn.setOnAction(e -> {
            System.out.println("Dừng toàn bộ module.");
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Tạo container
        HBox filterRow = new HBox(tfModuleName, cbStatus, spacer, cbConfigByName,  runAllBtn, stopAllBtn);
        filterRow.getStyleClass().add("filter-row");
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));

        filterRow.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/module_service_insert/css/form_style.css")
                ).toExternalForm()
        );
        return filterRow;
    }

    private void filterNode() {
        ObservableList<ConfigRunModuleTableData> filteredData = FXCollections.observableArrayList();
        String nameFilter = NormalizeString.normalizeString(tfModuleName.getText() != null ? tfModuleName.getText().trim().toLowerCase() : "");
        String statusFilter = NormalizeString.normalizeString(cbStatus.getValue() != null && !cbStatus.getValue().equals("All") ? cbStatus.getValue().toLowerCase() : "");
        for (ConfigRunModuleTableData item : configRunModuleTableDatas) {
            boolean match = true;
            String name = NormalizeString.normalizeString(item.getModuleName() != null ? item.getModuleName().toLowerCase() : "");
            String status = NormalizeString.normalizeString(item.getStatus() != null ? item.getStatus().toLowerCase() : "");

            if (!nameFilter.isEmpty() && !name.contains(nameFilter)) match = false;
            if (!statusFilter.isEmpty() && !status.equals(statusFilter)) match = false;
            if (match) filteredData.add(item);
            System.out.println("Status item: " + status + ", match: " + match);
        }

        filterConfigRunModuleTableDatas.setAll(filteredData);
        paginationUtil.updatePagination(pagination, filterConfigRunModuleTableDatas, configRunModuleTableDatas, configRunModuleTable);
    }

    public void showTable() {
        try {
            this.getStylesheets().add(this.getClass().getResource("/com/module_service_insert/css/header_table.css").toExternalForm());
//            List<ConfigClusterModuleTableData> configClusterModuleTableDatas = configPresenter.findAll(cbConfigByName.getValue().getId());
//            infoConfigModuleRunTableDatas.setAll(configClusterModuleTableDatas);
//            filterInfoConfigModuleRunTableDatas.setAll(configClusterModuleTableDatas);
//            infoConfigModuleRunTableDataTable.setItems(filterInfoConfigModuleRunTableDatas);
//            paginationUtil.updatePagination(pagination, filterInfoConfigModuleRunTableDatas, infoConfigModuleRunTableDatas, infoConfigModuleRunTableDataTable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button createIconButton(String iconPath, int height, int width) {
        ImageView icon = new ImageView(ConfigGeneralScreen.class.getResource(iconPath).toExternalForm());
        icon.setFitHeight(height);
        icon.setFitWidth(width);
        Button button = new Button();
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return button;
    }
}
