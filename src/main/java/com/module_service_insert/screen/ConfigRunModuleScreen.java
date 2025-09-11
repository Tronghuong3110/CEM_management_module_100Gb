package com.module_service_insert.screen;

import com.module_service_insert.action.TopBarHandler;
import com.module_service_insert.model.ConfigModel;
import com.module_service_insert.model.tableData.ArgumentsTableData;
import com.module_service_insert.model.tableData.ConfigClusterModuleTableData;
import com.module_service_insert.model.tableData.ConfigRunModuleTableData;
import com.module_service_insert.presenter.ConfigPresenter;
import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.functionUtils.ChooseLocationFile;
import com.module_service_insert.utils.functionUtils.NormalizeString;
import com.module_service_insert.utils.screenUtils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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

    private HashMap<String, Label> valueArgsMap = new HashMap<>();
    private Label argTitle;
    private final HashMap<String, ComboBox<String>> comboBoxByName = new HashMap<>();

    public ConfigRunModuleScreen(String name) {
        configPresenter = ConfigPresenter.getInstance();
        configName = name;
        initData();
        setSpacing(15);
        setStyle("-fx-padding: 5 15 15 15; -fx-background-insets: 0; -fx-background-color: #f3f3f3");

        HBox topBar = createTopBar();
        HBox inputSearch = createFilterRow();

        createConfigTable();
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
        Node argsBox = createArgsBox(new ConfigRunModuleTableData());
        getChildren().addAll(topBar, splitPane, argsBox);
    }

    private void createConfigTable() {
        configRunModuleTable = new TableView<>();
        TableColumn<ConfigRunModuleTableData, Boolean> selectCol = new TableColumn<>("");
        TableColumn<ConfigRunModuleTableData, String> moduleNameCol = CreateColumnTableUtil.createColumn("Module name", ConfigRunModuleTableData::moduleNameProperty);
        TableColumn<ConfigRunModuleTableData, String> commandCol = CreateColumnTableUtil.createColumn("Command", ConfigRunModuleTableData::commandProperty);
        TableColumn<ConfigRunModuleTableData, String> statusCol = CreateColumnTableUtil.createColumn("Status", ConfigRunModuleTableData::statusProperty);
        TableColumn<ConfigRunModuleTableData, String> interfaceNameCol = CreateColumnTableUtil.createColumn("Interface name", ConfigRunModuleTableData::interfaceNameProperty);
        TableColumn<ConfigRunModuleTableData, Void> actionColumn = new TableColumn<>("Action");

        setColumnPercentWidth(selectCol, 5);
        setColumnPercentWidth(moduleNameCol, 20);
        setColumnPercentWidth(statusCol, 8);
        setColumnPercentWidth(commandCol, 30);
        setColumnPercentWidth(interfaceNameCol, 20);
        setColumnPercentWidth(actionColumn, 17);

        configRunModuleTable.getColumns().addAll(selectCol, moduleNameCol, statusCol, commandCol, interfaceNameCol, actionColumn);

        configRunModuleTable.setItems(filterConfigRunModuleTableDatas);
        configRunModuleTable.setPrefHeight(600);
        configRunModuleTable.setMaxHeight(300);
        configRunModuleTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1px;");

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
                    item.setSelected(!item.isSelected());
                    argTitle.setText("Các tham số đầu vào của module: " + item.getModuleName());
                    Map<String, String> args = item.getArgs().getArgsMap();
                    for(Map.Entry<String, String> entry : args.entrySet()) {
                        Label arglabel = valueArgsMap.get(entry.getKey());
                        arglabel.setText(entry.getValue());
                    }
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
            private final Button editBtn = createIconButton("/com/module_service_insert/icons/pencil.png", 20, 20);
            private final HBox btnBox = new HBox(5, startBtn, stopBtn, editBtn);
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

                editBtn.setOnAction(e -> {
                   ConfigRunModuleTableData item = getTableView().getItems().get(getIndex());
                   createFormAddNewOrEdit(item);
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
                createFormAddNewOrEdit(null);
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
                File fileChose = ChooseLocationFile.choosesFolderFile("module_config");
                if(fileChose == null) return;
                String outputFile = fileChose.getAbsolutePath();
                try {
                    saveFile(outputFile);
                    AlertUtils.showAlert("Thành công", "Xuất file config thành công.", "INFORMATION");
                }
                catch (Exception e) {
                    AlertUtils.showAlert("Lỗi", "Xuất file thất bại, vui lòng thử lại sau.", "ERROR");
                }
            }

            @Override
            public void onImport() {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Chọn file để import.");
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Json Files", "*.json"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
                );

                Window owner = null;
                for(Window w : Window.getWindows()) {
                    if(w.isShowing()) {
                        owner = w;
                        break;
                    }
                }

                File selectedFile = fileChooser.showOpenDialog(owner);
                if(selectedFile != null) {
                    try {
                        List<ConfigRunModuleTableData> configRunModuleTableData = new ArrayList<>();
                        String moduleContent = Files.readString(selectedFile.toPath());
                        JSONArray jsonArray = new JSONArray(moduleContent);
                        for(Object o : jsonArray) {
                            JSONObject configModule = (JSONObject) o;
                            ConfigRunModuleTableData moduleConfig = new ConfigRunModuleTableData(
                                configModule.getString("module_name"),
                                configModule.getString("status"),
                                configModule.getString("command"),
                                configModule.getString("interface_name"),
                                ""
                            );

                            JSONObject argJson = (JSONObject) configModule.get("args");
                            ArgumentsTableData arg = new ArgumentsTableData(
                                argJson.getString("queue"),
                                argJson.getString("cpu binding"),
                                argJson.getString("output"),
                                argJson.getString("thread"),
                                argJson.getString("active wait")
                            );
                            moduleConfig.setArgs(arg);
                            configRunModuleTableData.add(moduleConfig);
                        }
                        configRunModuleTableDatas.setAll(configRunModuleTableData);
                        filterConfigRunModuleTableDatas.setAll(configRunModuleTableData);
                        paginationUtil.updatePagination(pagination,
                                filterConfigRunModuleTableDatas,
                                configRunModuleTableDatas,
                                configRunModuleTable
                        );
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        AlertUtils.showAlert("Lỗi", "Cấu trúc file không đúng, vui lòng nhập file khác.", "ERROR");
                    }
                }
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

    private void createFormAddNewOrEdit(ConfigRunModuleTableData item) {
        Stage dialogStage = new Stage();
        dialogStage.setResizable(false);
        Map<String, TextField> inputArgs = new HashMap<>();

        // ====== Tiêu đề ======
        Label titleLabel = new Label("Cấu hình module");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);

        // ====== Hàng 1 (Module, Interface, Command, Node) ======
        VBox moduleBox = createLabeledCombo("Module", "Chọn module",
                "Module A", "Module B", "Module C");

        VBox interfaceBox = createLabeledCombo("Interface", "Chọn interface",
                "Interface 1", "Interface 2");

        VBox nodeBox = createLabeledCombo("Node", "Chọn node",
                "Node X", "Node Y");

        Label lbl = new Label("Command");
        lbl.setStyle("-fx-font-size: 14;");
        TextField tfCommand = new TextField();
        tfCommand.setPrefHeight(30);
        tfCommand.setStyle("-fx-font-size: 14;");
        tfCommand.setPromptText("Cấu hình Command");
        VBox commandBox = new VBox(5, lbl, tfCommand);
        GridPane.setHgrow(commandBox, Priority.ALWAYS);
        commandBox.setMaxWidth(Double.MAX_VALUE);

        GridPane topRow = new GridPane();
        topRow.setHgap(20);
        topRow.setVgap(10);
        topRow.add(moduleBox, 0, 0);
        topRow.add(interfaceBox, 1, 0);
        topRow.add(commandBox, 2, 0, 2, 1); // chiếm 2 cột
        topRow.add(nodeBox, 4, 0);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(16.7);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(16.7);
        ColumnConstraints cc3 = new ColumnConstraints();
        cc3.setPercentWidth(33.3);
        ColumnConstraints cc4 = new ColumnConstraints();
        cc4.setPercentWidth(16.7);
        ColumnConstraints cc5 = new ColumnConstraints();
        cc5.setPercentWidth(16.7);
        topRow.getColumnConstraints().addAll(cc1, cc2, cc3, cc4, cc5);

        // ====== Hàng 2 (Dynamic Args) ======
        VBox argsContainer = new VBox(10);
        argsContainer.setMaxWidth(Double.MAX_VALUE);

        ImageView addIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/module_service_insert/icons/add.png")));
        addIcon.setFitHeight(16);
        addIcon.setPreserveRatio(true);
        Button addArgBtn = new Button("Add", addIcon);
        AddCssForBtnUtil.addCssStyleForBtn(addArgBtn);
        addArgBtn.setPrefWidth(120);

        List<String> argTypes = Arrays.asList("Queue", "Thread", "CPU Binding", "Output", "Active Wait");

        if(item != null) {
            comboBoxByName.get("Module").setValue(item.getModuleName());
            comboBoxByName.get("Interface").setValue(item.getInterfaceName());
            comboBoxByName.get("Node").setValue(item.getClusterName());
            tfCommand.setText(item.getCommand());

            for(Map.Entry<String, String> entry : item.getArgs().getArgsMap().entrySet()) {
                String argType =
                        switch (entry.getKey()) {
                            case "queue" -> "Queue";
                            case "thread" -> "Thread";
                            case "cpu binding" -> "CPU Binding";
                            case "output" -> "Output";
                            case "active wait" -> "Active Wait";
                            default -> "Unknown";
                        };
                addItemArgForm(argsContainer, argTypes, inputArgs, argType, entry.getValue());
            }
        }
        addArgBtn.setOnAction(e -> {
            // Tạo ComboBox
            addItemArgForm(argsContainer, argTypes, inputArgs, null, null);
        });

        ScrollPane argsContainerScroll = new ScrollPane(argsContainer);
        argsContainerScroll.setFitToWidth(true);
        argsContainerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        argsContainerScroll.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0;"
        );
        HBox.setHgrow(argsContainerScroll, Priority.ALWAYS);

        HBox argsRowWrapper = new HBox(20, argsContainerScroll, addArgBtn);
        argsRowWrapper.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(argsContainer, Priority.ALWAYS);
        argsRowWrapper.setPadding(new Insets(0, 30, 0, 0));

        VBox midSection = new VBox(10, argsRowWrapper);

        // ====== Button ======
        ImageView saveIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/module_service_insert/icons/save.png")));
        ImageView cancelIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/module_service_insert/icons/stop.png")));
        saveIcon.setPreserveRatio(true);
        saveIcon.setFitHeight(16);
        cancelIcon.setPreserveRatio(true);
        cancelIcon.setFitHeight(16);
        Button saveBtn = new Button("Save", saveIcon);
        Button cancelBtn = new Button("Cancel", cancelIcon);
        AddCssForBtnUtil.addCssStyleForBtn(saveBtn);
        AddCssForBtnUtil.addCssStyleForBtn(cancelBtn);
        HBox bottomRow = new HBox(10, saveBtn, cancelBtn);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnMouseClicked(e -> dialogStage.close());

        saveBtn.setOnMouseClicked(e -> {
            inputArgs.forEach((k, v) -> {
                System.out.println(k + " = " + v.getText());
            });
        });

        // ====== Root Layout ======
        VBox root = new VBox(20, titleBox, topRow, midSection, bottomRow);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(midSection, Priority.ALWAYS);
        root.setStyle(
                "-fx-padding: 10;" +
                "-fx-background-insets: 0;" +
                "-fx-background-color: #f2f2f2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;"
        );

        // ====== Stage ======
        dialogStage.setTitle("Thêm / Sửa Config");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root, 1000, 400));
        dialogStage.showAndWait();
    }

    private VBox createLabeledCombo(String label, String prompt, String... items) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 14;");
        ComboBox<String> combo = new ComboBox<>();
        combo.setPrefHeight(30);
        combo.setStyle("-fx-font-size: 14;");
        combo.setPromptText(prompt);
        combo.getItems().addAll(items);
        VBox box = new VBox(5, lbl, combo);
        GridPane.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        comboBoxByName.put(label, combo);
        return box;
    }

    private VBox createLabeledField(String label, String prompt) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 14;");
        TextField tf = new TextField();
        tf.setPrefHeight(30); // đồng bộ với ComboBox
        tf.setStyle("-fx-font-size: 14;");
        tf.setPromptText(prompt);
        VBox box = new VBox(5, lbl, tf);
        GridPane.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private void initData() {
        for(int i = 0; i < 20; i++) {
            ConfigRunModuleTableData configRunModuleTableData = new ConfigRunModuleTableData(
                "Module_" + i,
                    i % 2 == 0 ? "active" : "inactive",
                    "java -jar Module_" + i,
                    "interface_" + i,
                    "Node_" + i
            );
            configRunModuleTableData.setArgs(new ArgumentsTableData(
                    String.valueOf(i + 10), String.format("%s, %s, %s", (i+1), (i+2), (i+3)),
                    "/var/log/cem100Gb/Module_" + i,
                    "8",
                    "10"
            ));
            configRunModuleTableDatas.add(configRunModuleTableData);
            filterConfigRunModuleTableDatas.add(configRunModuleTableData);
        }
    }

    private Node createArgsBox(ConfigRunModuleTableData item) {
        Map<String, String> args = item.getArgs().getArgsMap();

        // ====== Label tiêu đề ======
        argTitle = new Label("Các tham số đầu vào của module: Unknown");
        argTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1f2937;");
        argTitle.setAlignment(Pos.CENTER);

        StackPane card = new StackPane();
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 14, 0.2, 0, 2);"
        );

        int cols = 3;
        List<Map.Entry<String, String>> list = new ArrayList<>(args.entrySet());
        int rows = (int) Math.ceil(list.size() / (double) cols);
        rows = Math.max(rows, 1);

        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);

        for (int c = 0; c < cols; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / cols);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        for (int r = 0; r < rows; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / rows);
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }

        for (int i = 0; i < list.size(); i++) {
            int r = i / cols, c = i % cols;

            Map.Entry<String, String> e = list.get(i);
            VBox cell = new VBox(10);
            cell.setAlignment(Pos.CENTER);

            HBox iconValueBox = new HBox(20);
            iconValueBox.setAlignment(Pos.CENTER);

            ImageView icon = new ImageView(new Image(
                    getClass().getResourceAsStream("/com/module_service_insert/icons/" + e.getKey() + ".png")
            ));
            icon.setFitWidth(40);
            icon.setFitHeight(40);
            if (e.getKey().equals("thread") || e.getKey().equals("queue")) {
                icon.setFitWidth(50);
                icon.setFitHeight(50);
            }
            if (e.getKey().equals("active wait")) {
                icon.setFitWidth(30);
                icon.setFitHeight(30);
            }
            icon.setPreserveRatio(true);

            Label value = new Label("Unknown");
            valueArgsMap.put(e.getKey(), value);
            value.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #2d346d;");

            iconValueBox.getChildren().addAll(icon, value);
            iconValueBox.setMinHeight(65);

            Label key = new Label(e.getKey());
            key.setStyle("-fx-font-size: 16px; -fx-text-fill: #595757;");

            cell.getChildren().addAll(iconValueBox, key);
            grid.add(cell, c, r);
        }

        card.getChildren().add(grid);

        Pane lines = new Pane();
        lines.setMouseTransparent(true);
        Color stroke = Color.web("#E5E7EB");
        double strokeWidth = 1.0;

        for (int i = 1; i < cols; i++) {
            Line v = new Line();
            v.startXProperty().bind(lines.widthProperty().multiply(i / (double) cols));
            v.endXProperty().bind(v.startXProperty());
            v.startYProperty().set(0);
            v.endYProperty().bind(lines.heightProperty());
            v.setStroke(stroke);
            v.setStrokeWidth(strokeWidth);
            lines.getChildren().add(v);
        }
        for (int j = 1; j < rows; j++) {
            Line h = new Line();
            h.startYProperty().bind(lines.heightProperty().multiply(j / (double) rows));
            h.endYProperty().bind(h.startYProperty());
            h.startXProperty().set(0);
            h.endXProperty().bind(lines.widthProperty());
            h.setStroke(stroke);
            h.setStrokeWidth(strokeWidth);
            lines.getChildren().add(h);
        }

        card.getChildren().add(lines);

        // ====== Bọc vào VBox ======
        VBox container = new VBox(12, argTitle, card);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(15, 0, 0, 0));
        container.setStyle("-fx-border-width: 1 0 0 0; -fx-border-color: #bdbdbf;");
        return container;
    }
    private void addItemArgForm(VBox argsContainer, List<String> argTypes, Map<String, TextField> inputArgs, String comboBoxVal, String inputVal) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(argTypes);
        combo.setPromptText("Chọn loại arg");
        combo.setPrefWidth(200);
        combo.setPrefHeight(30);
        combo.setStyle("-fx-font-size: 14px;");
        if(comboBoxVal != null) {
            combo.setValue(comboBoxVal);
            combo.setDisable(true);
        }

        // Tạo TextField
        TextField input = new TextField();
        input.setPromptText("Cấu hình argument");
        input.setPrefWidth(400);
        input.setPrefHeight(30);
        input.setStyle("-fx-font-size: 14px;");
        if(inputVal != null) {
            input.setText(inputVal);
        }

        // Button xóa
        ImageView deleteRowIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/module_service_insert/icons/delete_row.png")));
        deleteRowIcon.setFitHeight(24);
        deleteRowIcon.setPreserveRatio(true);
        Button removeBtn = new Button("", deleteRowIcon);
        removeBtn.setPrefHeight(30);
        removeBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 0;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );

        HBox rowBox = new HBox(30, combo, input, removeBtn);
        rowBox.setAlignment(Pos.CENTER_LEFT);
        removeBtn.setOnAction(ev -> {
            argsContainer.getChildren().remove(rowBox);
            inputArgs.remove(combo.getValue() != null ? combo.getValue().toLowerCase() : null);
            if(argsContainer.getChildren().isEmpty()) {
                argsContainer.setStyle("");
            }
        });

        combo.setOnAction(ev -> {
            String selected = combo.getValue();
            input.setPromptText("Cấu hình " + selected);

            if ("Output".equals(selected)) {
                input.setEditable(false);
                input.setOnMouseClicked(ev2 -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Chọn file output");
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("All Files", "*.*"),
                            new FileChooser.ExtensionFilter("Text Files", "*.txt")
                    );
                    Window window = ((Node) ev2.getSource()).getScene().getWindow();
                    File file = fileChooser.showSaveDialog(window);
                    if (file != null) {
                        input.setText(file.getAbsolutePath());
                    }
                });
            }
            else {
                input.setEditable(true);
                input.setOnMouseClicked(null);
            }
            inputArgs.put(selected.toLowerCase(), input);
        });
        if(argsContainer.getChildren().isEmpty()) {
            argsContainer.setStyle(
                    "-fx-padding: 10px;" +
                    "-fx-background-insets: 0;" +
                    "-fx-background-color: #ffffff;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                    "-fx-border-radius: 5;" +
                    "-fx-background-radius: 5;"
            );
        }
        argsContainer.getChildren().add(rowBox);
    }

    private void saveFile(String filePath) {
        JSONArray jsonArray = new JSONArray();
        try {
            for(ConfigRunModuleTableData moduleConfig : configRunModuleTableDatas) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("module_name", moduleConfig.getModuleName());
                jsonObject.put("status", moduleConfig.getStatus());
                jsonObject.put("command", moduleConfig.getCommand());
                jsonObject.put("interface_name", moduleConfig.getInterfaceName());
                jsonObject.put("args", moduleConfig.getArgs().getArgsMap());
                jsonArray.put(jsonObject);
            }
            FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8);
            fileWriter.write(jsonArray.toString());
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
