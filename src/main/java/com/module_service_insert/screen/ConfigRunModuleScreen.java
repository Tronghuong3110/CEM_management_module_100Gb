package com.module_service_insert.screen;

import com.module_service_insert.action.TopBarHandler;
import com.module_service_insert.constant.VariableCommon;
import com.module_service_insert.exception.FileException;
import com.module_service_insert.exception.RunException;
import com.module_service_insert.model.ConfigModel;
import com.module_service_insert.model.tableData.ArgumentsTableData;
import com.module_service_insert.model.tableData.ConfigClusterModuleTableData;
import com.module_service_insert.model.tableData.ConfigRunModuleTableData;
import com.module_service_insert.presenter.ConfigPresenter;
import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.functionUtils.ChooseLocationFile;
import com.module_service_insert.utils.functionUtils.NormalizeString;
import com.module_service_insert.utils.screenUtils.*;
import info.aduna.io.IndentingWriter;
import javafx.application.Platform;
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
import javafx.scene.shape.LineTo;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.Variable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Trọng Hướng
 */
public class ConfigRunModuleScreen extends VBox {

    private final Logger logger = LoggerFactory.getLogger(ConfigRunModuleScreen.class);

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
    private String configName;

    private HashMap<String, Label> valueArgsMap = new HashMap<>();
    private Label argTitle;
    private final HashMap<String, ComboBox<String>> comboBoxByName = new HashMap<>();
    private final List<ConfigRunModuleTableData> configRunModuleChecked;
    private ScheduledExecutorService scheduler;

    private StackPane overLay;

    public ConfigRunModuleScreen(String name) {
        configPresenter = ConfigPresenter.getInstance();
        configName = name;
        configRunModuleChecked = new ArrayList<>();
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

        overLay = new StackPane(new ProgressIndicator());
        overLay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        overLay.setVisible(false);
        getChildren().add(overLay);
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
                if(newVal) {
                    configRunModuleChecked.add(item);
                }
                else {
                    configRunModuleChecked.remove(item);
                }
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
                    if(item.isSelected()){
                        configRunModuleChecked.add(item);
                    }
                    else {
                        configRunModuleChecked.remove(item);
                    }
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
                    overLay.setVisible(true);
                    ConfigRunModuleTableData item = getTableView().getItems().get(getIndex());
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            return startModule(item.getCommand(), item.getModuleName(), item.getInterfaceName());
                        }
                        catch (RunException ex) {
                            throw ex;
                        }
                    })
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            if(message.isEmpty()) {
                                item.setStatus("active");
                                System.out.println("Chạy module thành công.");
                                AlertUtils.showAlert("Thành công", "Chạy module: " + item.getModuleName() + " thành công.", "INFORMATION");
                            }
                            else {
                                item.setStatus("error");
                                AlertUtils.showAlertWithTextArea("Lỗi", message.toString(), "ERROR");
                            }
                            overLay.setVisible(false);
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            item.setStatus("error");
                            AlertUtils.showAlert("Lỗi", ex.getMessage(), "ERROR");
                            overLay.setVisible(false);
                        });
                        return null;
                    });
                });

                stopBtn.setOnAction(e -> {
                    ConfigRunModuleTableData item = getTableView().getItems().get(getIndex());
                    overLay.setVisible(true);
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            return killProcess(item);
                        }
                        catch (RunException ex) {
                            throw ex;
                        }
                    })
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            overLay.setVisible(false);
                            if(message.isEmpty()) {
                                item.setStatus("inactive");
                                AlertUtils.showAlert("Thành công", "Dừng module " + item.getModuleName() + " thành công.", "INFORMATION");
                            }
                            else {
                                AlertUtils.showAlert("Thất bại", "Dừng module " + item.getModuleName() + " thất bại.", "ERROR");
                                boolean isModuleRunning = isProcessRunning(item.getCommand());
                                if(!isModuleRunning) {
                                    item.setStatus("inactive");
                                }
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            overLay.setVisible(false);
                            AlertUtils.showAlert("Lỗi", ex.getMessage(), "ERROR");
                            // Dừng module lỗi ==> có cần check lại xem module có đang chạy nữa không
                            boolean isModuleRunning = isProcessRunning(item.getCommand());
                            if(!isModuleRunning){
                                item.setStatus("inactive");
                            }
                        });
                        return null;
                    });
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
                    readFileConfig(selectedFile.toPath());
                }
            }
        };
    }

    protected HBox createFilterRow() {
        tfModuleName = new TextField();
        tfModuleName.setPromptText("Tìm theo tên module");
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

        List<ConfigModel> configModels = readAllConfigFileFromFolder();
        cbConfigByName = new ComboBox<>();
        cbConfigByName.getItems().addAll(configModels);
        cbConfigByName.setPromptText("Chọn bản cấu hình theo tên");
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
                Path newPath = Paths.get(newVal.getPath());
                boolean isFinishCopy = copyFile(newPath);
                if(isFinishCopy) {
                    readFileConfig(newPath);
                }
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
            if(configRunModuleChecked.isEmpty()) {
                AlertUtils.showAlert("Cảnh báo", "Vui lòng chọn ít nhất một module cần chạy", "WARNING");
                return;
            }
            overLay.setVisible(true);
            CompletableFuture.supplyAsync(() -> {
                StringBuilder messageRunModuleError = new StringBuilder();
                for (ConfigRunModuleTableData item : configRunModuleChecked) {
                    try {
                        StringBuilder message = startModule(item.getCommand(), item.getModuleName(), item.getInterfaceName());
                        Platform.runLater(() -> {
                            if(message.isEmpty()) {
                                item.setStatus("active");
                            }
                            else {
                                messageRunModuleError.append(message).append("\n");
                                item.setStatus("error");
                            }
                        });
                    }
                    catch (Exception ex) {
                        Platform.runLater(() -> {
                            item.setStatus("error");
                        });
                        ex.printStackTrace();
                    }
                }
                return messageRunModuleError;
            })
            .thenAccept(message -> {
                Platform.runLater(() -> {
                    overLay.setVisible(false);
                    if(!message.isEmpty()) {
                        AlertUtils.showAlertWithTextArea("Lỗi", message.toString(), "ERROR");
                    }
                    else {
                        AlertUtils.showAlert("Thành công", "Chạy module thành công.", "INFORMATION");
                    }
                });
            });
        });

        Button stopAllBtn = ButtonUtil.createBtn("Stop all", "/com/module_service_insert/icons/stop.png");
        AddCssForBtnUtil.addCssStyleForBtn(stopAllBtn);
        stopAllBtn.setOnAction(e -> {
            overLay.setVisible(true);
            StringBuilder messageRunModuleError = new StringBuilder();
            CompletableFuture.supplyAsync(() -> {
                for (ConfigRunModuleTableData item : configRunModuleChecked) {
                    try {
                        System.out.println("Dừng module: " + item.getModuleName());
                        StringBuilder messageRunStopModule = killProcess(item);
                        Platform.runLater(() -> {
                            if(messageRunStopModule.isEmpty()) {
                                item.setStatus("inactive");
                            }
                            else {
                                messageRunModuleError.append(messageRunStopModule);
                                boolean isModuleRunning = isProcessRunning(item.getCommand());
                                if(!isModuleRunning) {
                                    item.setStatus("inactive");
                                }
                            }
                        });
                    }
                    catch (Exception ex) {
                        boolean isModuleRunning = isProcessRunning(item.getCommand());
                        if(!isModuleRunning) {
                            item.setStatus("inactive");
                        }
                    }
                }
                return messageRunModuleError;
            })
            .thenAccept(message -> {
                Platform.runLater(() -> {
                   overLay.setVisible(false);
                    AlertUtils.showAlert("Thành công", "Dừng module thành công.", "INFORMATION");
                });
            });
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
        }

        paginationUtil.updatePagination(pagination, filterConfigRunModuleTableDatas, filteredData, configRunModuleTable);
    }

    public void showTable() {
        try {
            this.getStylesheets().add(this.getClass().getResource("/com/module_service_insert/css/header_table.css").toExternalForm());
            Path path = Paths.get(VariableCommon.ACTIVE_CONFIG_DIRECTORY_PATH + "active_config_file.json");
            readFileConfig(path);
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
        List<String> modules = readFileModules();
        VBox moduleBox = createLabeledCombo("Module", "Chọn module",
                modules);

        List<String> interfaces = getAllInterface();
        VBox interfaceBox = createLabeledCombo("Interface", "Chọn interface",
                interfaces);

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
        topRow.add(commandBox, 2, 0, 2, 1);

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
//                System.out.println("Args: " + inputArgs.size());
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
            try {
                System.out.println("Start update config");
                HashMap<String, String> args = new HashMap<>();
                inputArgs.forEach((k, v) -> {
                    args.put(k, v.getText());
                });
                String interfaceName = comboBoxByName.get("Interface").getValue();
                String moduleName = comboBoxByName.get("Module").getValue();
                if(interfaceName == null || moduleName == null) {
                    AlertUtils.showAlert("Cảnh báo", "Vui lòng chọn interface và module.", "WARNING");
                    return;
                }
                // đang chọn file cấu hình để hiển thị
                if(cbConfigByName.getValue() != null) {
                    System.out.println("Cập nhật cấu hình đang chọn.");
                    updateConfig(cbConfigByName.getValue().getPath(),  moduleName,
                            cbStatus.getValue(), tfCommand.getText(), interfaceName, args,
                            item.getId());
                }
                String path = VariableCommon.ACTIVE_CONFIG_DIRECTORY_PATH + "active_config_file.json";
                updateConfig(path, moduleName,
                        cbStatus.getValue(), tfCommand.getText(), interfaceName, args,
                        item != null ? item.getId() : null);
                AlertUtils.showAlert("Thành công", "Cập nhật cấu hình thành công.", "INFORMATION");
                if(item != null) {
                    item.setModuleName(moduleName);
                    item.setInterfaceName(interfaceName);
                    item.setStatus(cbStatus.getValue());
                    item.setCommand(tfCommand.getText());
                    item.getArgs().setArgsMap(args);
                    for(Map.Entry<String, String> entry : args.entrySet()) {
                        Label arglabel = valueArgsMap.get(entry.getKey());
                        arglabel.setText(entry.getValue());
                    }
                }
                else {
                    ConfigRunModuleTableData configRunModuleTableData = new ConfigRunModuleTableData(moduleName, cbStatus.getValue(), tfCommand.getText(), interfaceName, "");
                    configRunModuleTableData.getArgs().setArgsMap(args);
                    configRunModuleTableDatas.add(configRunModuleTableData);
                    filterConfigRunModuleTableDatas.add(configRunModuleTableData);
                }
                configRunModuleTable.refresh();
                dialogStage.close();
            }
            catch(Exception ex){
                AlertUtils.showAlertWithTextArea("Lỗi", ex.getMessage(), "ERROR");
            }
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

    private VBox createLabeledCombo(String label, String prompt, List<String> items) {
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
        if(combo.getValue() != null) {
            inputArgs.put(combo.getValue().toLowerCase(), input);
        }
        argsContainer.getChildren().add(rowBox);
    }

    private void saveFile(String filePath) {
        JSONObject root = new JSONObject();
        try {
            for(ConfigRunModuleTableData moduleConfig : configRunModuleTableDatas) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("module_name", moduleConfig.getModuleName());
                jsonObject.put("status", moduleConfig.getStatus());
                jsonObject.put("command", moduleConfig.getCommand());
                jsonObject.put("interface_name", moduleConfig.getInterfaceName());
                jsonObject.put("args", moduleConfig.getArgs().getArgsMap());
                root.put(moduleConfig.getId() == null ? String.valueOf(System.nanoTime()) : moduleConfig.getId(), jsonObject);
            }
            FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8);
            fileWriter.write(root.toString());
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private StringBuilder startModule(String command, String moduleName, String interfaceName) {
        try {
            String[] exec = {"gnome-terminal", "--title=" + moduleName, "--", "bash", "-c", command + "; exec bash"};
            logger.info("Run module: {}", Arrays.toString(exec));
            boolean isModuleRunning = isProcessRunning(command);
            if(isModuleRunning) {
                System.out.println(String.format("Module: %s đang được chạy với command: %s.",moduleName, command));
                return new StringBuilder();
            }
            System.out.println(Arrays.toString(exec));
            Process p = Runtime.getRuntime().exec(exec);
//            BufferedReader readError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder messageError = new StringBuilder();
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                try {
//                    String error;
//                    while((error = readError.readLine()) != null) {
//                        logger.error();
//                        messageError.append(error);
//                    }
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//            future.orTimeout(10, TimeUnit.SECONDS);
//            future.join();
            return messageError;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Run module error, details: ", e);
            throw new RunException("Chạy module: " + moduleName + " trên interface: " + interfaceName + " xảy ra lỗi.");
        }
    }

    private StringBuilder killProcess(ConfigRunModuleTableData item) {
        try {
            String[] cmd = {"pkill", "-f", item.getCommand()};
            logger.info("Stop module: {}", Arrays.toString(cmd));
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader readError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder messageError = new StringBuilder();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String line;
                    while((line = readError.readLine()) != null) {
                        messageError.append("- ").append(line).append("\n");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            future.orTimeout(10, TimeUnit.SECONDS);
            future.join();
            return messageError;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Stop module error, details: ", e);
            throw new RunException("Dừng module: " + item.getModuleName() + " trên interface: " + item.getInterfaceName() + " đã xảy ra lỗi, vui lòng thử lại sau.");
        }
    }

    private List<ConfigModel> readAllConfigFileFromFolder() {
        List<ConfigModel> configModels = new ArrayList<>();
        File folderRoot = new File(VariableCommon.CONFIG_DIRECTORY_PATH);
        File[] configFiles = folderRoot.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        });
        assert configFiles != null;
        for(File configFile : configFiles) {
            configModels.add(new ConfigModel(configFile.getName(), configFile.getPath()));
        }
        return configModels;
    }

    private void readFileConfig(Path path) {
        try {
            logger.info("Read config file: {}", path);
            List<ConfigRunModuleTableData> configRunModuleTableData = new ArrayList<>();
            String moduleContent = Files.readString(path);
            JSONObject root = new JSONObject(moduleContent);
            for(String key : root.keySet()) {
                JSONObject configModule = root.getJSONObject(key);
                ConfigRunModuleTableData moduleConfig = new ConfigRunModuleTableData(
                        configModule.getString("module_name"),
                        configModule.getString("status"),
                        configModule.getString("command"),
                        configModule.getString("interface_name"),
                        ""
                );

                JSONObject argJson = (JSONObject) configModule.get("args");
                ArgumentsTableData arg = new ArgumentsTableData(
                        !argJson.has("queue") ? "Unknown" : argJson.getString("queue"),
                        !argJson.has("cpu binding") ? "Unknown" : argJson.getString("cpu binding"),
                        !argJson.has("output") ? "Unknown" : argJson.getString("output"),
                        !argJson.has("thread") ? "Unknown" : argJson.getString("thread"),
                        !argJson.has("active wait") ? "Unknown" : argJson.getString("active wait")
                );
                moduleConfig.setArgs(arg);
                moduleConfig.setId(key);
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
        catch (NoSuchFileException exception) {
            AlertUtils.showAlert("Lỗi", "Chưa từng chọn file cấu hình nào.", "ERROR");
            logger.error("Read file config error: {}, details: ", path, exception);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Read file config error: {}, details: ", path, e);
            AlertUtils.showAlert("Lỗi", "Cấu trúc file không đúng, vui lòng nhập file khác.", "ERROR");
        }
    }

    private boolean copyFile(Path path) {
        try {
            File srcFile = new File(path.toString());
            File destFile = new File(VariableCommon.ACTIVE_CONFIG_DIRECTORY_PATH + "active_config_file.json");
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Copy file error, details: ", e);
            AlertUtils.showAlert("Lỗi", "Cấu hình file active lỗi.", "ERROR");
        }
        return false;
    }
    private void updateConfig(String path, String moduleName, String status, String command, String interfaceName, HashMap<String, String> args, String configId) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            JSONObject root = new JSONObject(content);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("module_name", moduleName);
            jsonObject.put("status", status);
            jsonObject.put("command", command);
            jsonObject.put("interface_name", interfaceName);
            jsonObject.put("args", args);
            root.put(configId == null ? String.valueOf(System.nanoTime()) : configId, jsonObject);
            FileWriter fileWriter = new FileWriter(path.toString(), StandardCharsets.UTF_8);
            fileWriter.write(root.toString());
            fileWriter.flush();
            fileWriter.close();
        }
        catch (NoSuchFileException e) {
            logger.error("update config error, details: ", e);
            throw new FileException(e.getMessage());
        }
        catch (Exception e) {
            logger.error("update config error, details: ", e);
            throw new FileException("Không thể xử lý file: " + path);
        }
    }

    private List<String> readFileModules() {
        try {
            File file = new File("./module_name.txt");
            if(!file.exists()) {
                AlertUtils.showAlert("Cảnh báo", "File danh sách tên module không tồn tại.", "WARNING");
                return new ArrayList<>();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            List<String> moduleNames = new ArrayList<>();
            while((line = reader.readLine()) != null) {
                moduleNames.add(line);
            }
            return moduleNames;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new FileException("Không thể xử lý file danh sách các modules.");
        }
    }

    private List<String> getAllInterface() {
        Process process = null;
        StringBuilder errorStr = new StringBuilder();
        try {
            String command = "pf_ringcfg --list-interfaces";
            logger.info("Start get list interfaces, command: {}", command);
            process = Runtime.getRuntime().exec(command);
            List<String> interfaces = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new  InputStreamReader(process.getErrorStream()));
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String line;
                    Pattern p = Pattern.compile (
                            "Name:\\s*(\\S*)\\s+Driver:\\s*(\\S*)\\s+RSS:\\s*(\\d*|Unknown)\\s+\\[(.*)]"
                    );
                    while((line = reader.readLine()) != null) {
                        Matcher m = p.matcher(line);
                        if(m.find()) {
                            String interfaceName = m.group(1);
                            interfaces.add(interfaceName);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            futures.add(future);
            CountDownLatch countDownLatch = new CountDownLatch(1);
            CompletableFuture<Void> futureError = CompletableFuture.runAsync(() -> {
                try {
                   countDownLatch.countDown();
                   String errorMessage;
                   while((errorMessage = error.readLine()) != null) {
                       errorStr.append("- ").append(errorMessage).append("\n");
                   }
               }
               catch (Exception e) {
                   e.printStackTrace();
               }
            });
            futures.add(futureError);
            countDownLatch.await();
            futureError.get(5, TimeUnit.SECONDS);
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return interfaces;
        }
        catch (TimeoutException e) {
            e.printStackTrace();
            process.destroy();
            logger.error("Get list interface error, details: ", e);
            if(!errorStr.isEmpty()) {
                AlertUtils.showAlertWithTextArea("Lỗi", errorStr.toString(), "ERROR");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.error("Get list interface error, details: ", e);
            AlertUtils.showAlert("Lỗi", e.getMessage(), "ERROR");
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Get list interface error, details: ", e);
        }
        return new ArrayList<>();
    }

    private boolean isProcessRunning(String command) {
        try {
            String[] exec = {"bash", "-c",  "pgrep -f \"" + command + "\""};
            Process process = new ProcessBuilder(exec).start();
            return process.waitFor() == 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateStatusModuleRun(List<ConfigRunModuleTableData> configRunModuleTableDatas) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(VariableCommon.ACTIVE_CONFIG_DIRECTORY_PATH + "active_config_file.json")));
            JSONObject contentObject = new JSONObject(content);
            FileWriter writer = new FileWriter(VariableCommon.ACTIVE_CONFIG_DIRECTORY_PATH + "active_config_file.json");
            for(ConfigRunModuleTableData configRunModuleTableData : configRunModuleTableDatas) {
                if(!configRunModuleTableData.isUpdate()) continue;
                String moduleId = configRunModuleTableData.getId();
                if (contentObject.has(moduleId)) {
                    JSONObject config = contentObject.getJSONObject(moduleId);
                    config.put("status", configRunModuleTableData.getStatus());
                    contentObject.put(moduleId, config);
                }
            }
            writer.write(contentObject.toString());
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("update status module run error, details: ", e);
        }
    }

    private void checkStatusModuleInterval() {
        Set<String> interfaces = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for(ConfigRunModuleTableData configRunModuleTableData : configRunModuleTableDatas) {
            String interfaceName = configRunModuleTableData.getInterfaceName();
            interfaces.add(interfaceName);
        }
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Checking status module interval");
            String folderLog = "/var/log/cem100Gb/";
            File folderRoot = new File(folderLog);
            if(!folderRoot.exists()) {
                System.out.println("Folder: " + folderLog + " không tồn tại.");
                return;
            }
            Map<String, Long> lastModifiedFiles = interfaces.stream()
                    .map(key -> {
                        File latest = Arrays.stream(Objects.requireNonNull(folderRoot.listFiles()))
                                .filter(file -> file.getName().contains(key))
                                .max(Comparator.comparingLong(File::lastModified))
                                .orElse(null);
                        return new AbstractMap.SimpleEntry<>(key, latest);
                    })
                    .filter(entry -> entry.getValue() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().lastModified()));
            for(ConfigRunModuleTableData configRunModuleTableData : configRunModuleTableDatas) {
                String interfaceName = configRunModuleTableData.getInterfaceName();
                // TH không có log file và status là active ==> báo lỗi
                Platform.runLater(() -> {
                    // && configRunModuleTableData.getStatus().equals("active")
                    if(!lastModifiedFiles.containsKey(interfaceName)) {
                        boolean isModuleRunning = isProcessRunning(configRunModuleTableData.getCommand());
                        // module vẫn đang chạy nhưng không có log
                        if(isModuleRunning) {
                            configRunModuleTableData.setStatus("error");
                            configRunModuleTableData.setIsUpdate(true);
                            logger.debug("Module: {} đang chạy nhưng không có log file.", configRunModuleTableData.getModuleName());
                        }
                        // TH không có log và module đang không chạy
                        else if(!configRunModuleTableData.getStatus().equals("inactive")){
                            configRunModuleTableData.setStatus("inactive");
                            configRunModuleTableData.setIsUpdate(true);
                        }
                    }
                    else {
                        long lastModifiedFile = lastModifiedFiles.get(interfaceName);
                        long currentTime = System.currentTimeMillis();
                        // check trong 2 chu kì không thấy có log ==> cần check lại xem có đang chạy hay không
                        if(Math.abs(lastModifiedFile - currentTime) > 10000) {
                            boolean isModuleRunning = isProcessRunning(configRunModuleTableData.getCommand());
                            // Sau 2 chu kì không có log, nhưng module vẫn đang chạy
                            if(isModuleRunning) {
                                configRunModuleTableData.setStatus("error");
                            }
                            // sau 2 chu kì không có log và module cũng đang không chạy
                            else if(!configRunModuleTableData.getStatus().equals("inactive")) {
                                configRunModuleTableData.setIsUpdate(true);
                                configRunModuleTableData.setStatus("inactive");
                            }
                            logger.debug("Module: {} không có số liệu, lần có số liệu gần nhất: {}", configRunModuleTableData.getModuleName(), formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModifiedFile),  ZoneId.of("UTC"))));
                        }
                        else if(!configRunModuleTableData.getStatus().equals("active")) {
                            configRunModuleTableData.setStatus("active");
                            configRunModuleTableData.setIsUpdate(true);
                        }
                    }
                });
            }
            updateStatusModuleRun(configRunModuleTableDatas);
        }, 0, 5, TimeUnit.SECONDS);
    }
    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        checkStatusModuleInterval();
    }
}
