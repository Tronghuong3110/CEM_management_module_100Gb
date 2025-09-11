package com.module_service_insert.screen;

import com.module_service_insert.action.TopBarHandler;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.model.tableData.InterfaceTableData;
import com.module_service_insert.model.tableData.ClusterTableData;
import com.module_service_insert.model.tableData.NumaHugePageTableData;
import com.module_service_insert.utils.functionUtils.AlertUtils;
import com.module_service_insert.utils.functionUtils.ChooseLocationFile;
import com.module_service_insert.utils.functionUtils.NormalizeString;
import com.module_service_insert.utils.screenUtils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Trọng Hướng
 */
public class ConfigGeneralScreen extends VBox {

    private final VBox splitPane = new VBox(15);
    private final PaginationUtil<InterfaceTableData> interfacePaginationUtil = new PaginationUtil();
    private final PaginationUtil<NumaHugePageTableData> numaPaginationUtil = new PaginationUtil();

    // các biến cho quản lý cụm
    private TableView<InterfaceTableData> interfaceTable;
    private Pagination interfacePagination;
    private ClusterTableData selectedNode;
    private TextField tfName, tfDriver;
    private ObservableList<InterfaceTableData> interfaceTableDatas = FXCollections.observableArrayList();
    private ObservableList<InterfaceTableData> filteredInterfaceTableDatas = FXCollections.observableArrayList();
    private ComboBox<String> comboBoxStatusNode;

    // các biến cho quản lý module theo từng cụm
    private TableView<NumaHugePageTableData> numaHugePageTable;
    private Pagination numaHugePagePagination;
    private TextField tfModuleName;
    private ObservableList<NumaHugePageTableData> numaHugePageTableDatas = FXCollections.observableArrayList();
    private ObservableList<NumaHugePageTableData> filteredNumaHugePagetableTableDatas = FXCollections.observableArrayList();

    private ObservableList<NumaHugePageTableData> workingData = FXCollections.observableArrayList();
    private Map<Long, NumaHugePageTableData> selectedModules;

    public ConfigGeneralScreen() {
        selectedModules = new HashMap<>();
        setSpacing(15);
        setStyle("-fx-padding: 5 15 15 15; -fx-background-insets: 0; -fx-background-color: #f3f3f3");

        HBox topBar = createTopBar();
        HBox inputSearch = createFilterRow();

        createInterfaceTable();
        interfacePagination = interfacePaginationUtil.createPagination(interfaceTable, filteredInterfaceTableDatas, interfaceTableDatas);

        VBox nodeSection = new VBox(inputSearch, interfaceTable, interfacePagination);
        nodeSection.setSpacing(0);
        nodeSection.setPadding(Insets.EMPTY);
        nodeSection.setStyle(
                "-fx-padding: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-color: #ffffff;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );

        interfaceTable.setPadding(Insets.EMPTY);
        interfacePagination.setPadding(Insets.EMPTY);
        interfacePagination.setStyle("-fx-padding: 0; -fx-background-insets: 0;");

        VBox moduleSection = createNumaHugePageTable();
        moduleSection.setPadding(Insets.EMPTY);
        moduleSection.setStyle("-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-background-color: #ffffff;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 2);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;");
        numaHugePagePagination.setPadding(Insets.EMPTY);
        numaHugePagePagination.setStyle("-fx-padding: 0; -fx-background-insets: 0;");

        splitPane.getChildren().addAll(nodeSection, moduleSection);
        splitPane.setPadding(Insets.EMPTY);
        splitPane.setStyle("""
            -fx-padding: 0;
            -fx-background-insets: 0;
            -fx-border-width: 0;
            -fx-divider-width: 1;
        """);

        VBox.setVgrow(splitPane, Priority.ALWAYS);

        getChildren().addAll(topBar, splitPane);
    }

    private void createInterfaceTable() {
        interfaceTable = new TableView<>();
        TableColumn<InterfaceTableData, Number> sttCol = new TableColumn<>("STT");
        TableColumn<InterfaceTableData, String> nameColumn = CreateColumnTableUtil.createColumn("Name", InterfaceTableData::interfaceNameProperty);
        TableColumn<InterfaceTableData, String> driverCol = CreateColumnTableUtil.createColumn("Driver", InterfaceTableData::driverProperty);
        TableColumn<InterfaceTableData, Number> rssCountCol = CreateColumnTableUtil.createColumn("RSS Count", InterfaceTableData::rssCountProperty);
        TableColumn<InterfaceTableData, String> descriptionCol =CreateColumnTableUtil.createColumn("Description", InterfaceTableData::descriptionProperty);

        setColumnPercentWidth(sttCol, 8);
        setColumnPercentWidth(nameColumn, 32);
        setColumnPercentWidth(driverCol, 20);
        setColumnPercentWidth(rssCountCol, 20);
        setColumnPercentWidth(descriptionCol, 20);

        interfaceTable.getColumns().addAll(sttCol, nameColumn, driverCol, rssCountCol, descriptionCol);

        interfaceTable.setItems(filteredInterfaceTableDatas);
        interfaceTable.setPrefHeight(600);
        interfaceTable.setMaxHeight(300);
        interfaceTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        sttCol.setCellFactory(col -> new TableCell<InterfaceTableData, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    int rowIndex = getIndex();
                    int pageIndex = interfacePagination.getCurrentPageIndex();
                    int offset = pageIndex * 5;

                    setText(String.valueOf(offset + rowIndex + 1));
                }
            }
        });


        interfaceTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(InterfaceTableData item, boolean empty) {
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

        interfaceTable.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/module_service_insert/css/table_row.css")
                ).toExternalForm()
        );
    }

    protected void setColumnPercentWidth(TableColumn<?, ?> col, double percent) {
        interfaceTable.widthProperty().addListener((obs, oldVal, newVal) -> {
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
                try {
                    File file = ChooseLocationFile.choosesFolderFile("config");
                    if(file == null) return;
                    String outputPath = file.getAbsolutePath();
                    // gọi hàm tạo file json
                    convertJson(Paths.get(outputPath));
                    AlertUtils.showAlert("Thành công", "Xuất file thành công:\n" + file.getAbsolutePath(),
                            "INFORMATION");
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.showAlert("Lỗi",
                            "Xuất dữ liệu thất bại", "ERROR");
                }
            }

            @Override
            public void onImport() {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Chọn file để import");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Json Files", "*.json", "*.json"),
                        new FileChooser.ExtensionFilter("All Files", "*.*")
                );

                Window owner = null;
                for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
                    if (w.isShowing()) { owner = w; break; }
                }

                File selectedFile = fileChooser.showOpenDialog(owner);
                if (selectedFile != null) {
                    try {
                        List<InterfaceTableData> interfaces = new ArrayList<>();
                        List<NumaHugePageTableData> numaHugePages = new ArrayList<>();
                        String configContent = Files.readString(selectedFile.toPath(), StandardCharsets.UTF_8);
                        JSONObject obj = new JSONObject(configContent);
                        JSONArray rss = obj.getJSONArray("RSS");
                        for(Object o : rss) {
                            JSONObject rssObj = (JSONObject) o;
                            interfaces.add(new InterfaceTableData(
                               rssObj.getString("name"),
                               rssObj.getInt("rss"),
                               rssObj.getString("driver"),
                               rssObj.getString("comment")
                            ));
                        }

                        JSONArray hugePages = obj.getJSONArray("HugePage");
                        for(Object o : hugePages) {
                            JSONObject numaHugePage = (JSONObject) o;
                            numaHugePages.add(new NumaHugePageTableData(
                                numaHugePage.getString("name"),
                                numaHugePage.getString("HugePage_total"),
                                numaHugePage.getString("HugePage_free"),
                                numaHugePage.getString("Cpu(s)")
                            ));
                        }

                        interfaceTableDatas.setAll(interfaces);
                        filteredInterfaceTableDatas.setAll(interfaces);
                        updateInterfacePagination();

                        numaHugePageTableDatas.setAll(numaHugePages);
                        filteredNumaHugePagetableTableDatas.setAll(numaHugePages);
                        updateNumaHugePagePagination();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    protected HBox createFilterRow() {
        tfName = new TextField();
        tfName.setPromptText("Tìm theo tên interface");
        tfName.textProperty().addListener((obs, o, n) -> filterNode());
        tfName.getStyleClass().add("filter-text-field");

        tfDriver = new TextField();
        tfDriver.setPromptText("Tìm theo driver");
        tfDriver.textProperty().addListener((obs, o, n) -> filterNode());
        tfDriver.getStyleClass().add("filter-text-field");

        // Tạo container
        HBox filterRow = new HBox(tfName, tfDriver);
        filterRow.getStyleClass().add("filter-row");
        filterRow.setAlignment(Pos.CENTER_LEFT); // canh giữa theo chiều dọc
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));

        // Load CSS CHỈ cho filter này
        filterRow.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/module_service_insert/css/form_style.css")
                ).toExternalForm()
        );

        return filterRow;
    }

    private void filterNode() {
        ObservableList<InterfaceTableData> filteredData = FXCollections.observableArrayList();
        String nameFilter = NormalizeString.normalizeString(tfName.getText() != null ? tfName.getText().trim().toLowerCase() : "");
        String driverFilter = NormalizeString.normalizeString(tfDriver.getText() != null ? tfDriver.getText().toLowerCase() : "");
        for (InterfaceTableData item : interfaceTableDatas) {
            boolean match = true;
            String name = NormalizeString.normalizeString(item.getInterfaceName() != null ? item.getInterfaceName().toLowerCase() : "");
            String driver = NormalizeString.normalizeString(item.getDriver() != null ? item.getDriver().toLowerCase() : "");

            if (!nameFilter.isEmpty() && !name.contains(nameFilter)) match = false;
            if (!driverFilter.isEmpty() && !driver.contains(driverFilter)) match = false;
            if (match) filteredData.add(item);
        }
        interfacePaginationUtil.updatePagination(interfacePagination, filteredInterfaceTableDatas, filteredData, interfaceTable);
    }

    public void showTable() {
        try {
            this.getStylesheets().add(this.getClass().getResource("/com/module_service_insert/css/header_table.css").toExternalForm());
            // lấy danh sách interface
            List<String> lines = Arrays.asList(
                    "Name: enp4s0f0np0          Driver: bnxt_en    RSS:     48   [Linux Driver]",
                    "Name: ens3f0np0            Driver: i40e       RSS:     96   [Supported by ZC]",
                    "Name: enp4s0f1np1          Driver: bnxt_en    RSS:     48   [Linux Driver]",
                    "Name: ens3f1np1            Driver: i40e       RSS:     96   [Supported by ZC]",
                    "Name: ens3f2np2            Driver: i40e       RSS:     96   [Supported by ZC]",
                    "Name: ens3f3np3            Driver: i40e       RSS:     96   [Supported by ZC]",
                    "Name: enxbe3af2b6059f      Driver: rndis_host RSS:     1    [Linux Driver]",
                    "Name: ens5f0np0            Driver: ice        RSS:     4    [Running ZC]",
                    "Name: ens5f1np1            Driver: ice        RSS:     4    [Running ZC]",
                    "Name: ens4f0np0            Driver: ice        RSS:     4    [Running ZC]",
                    "Name: ens4f1np1            Driver: ice        RSS:     1    [Running ZC]"
            );
            List<InterfaceTableData> interfaces = parseInterfaces(lines);
            System.out.println("Interface count: " + interfaces.size());
            interfaceTableDatas.setAll(interfaces);
            filteredInterfaceTableDatas.setAll(interfaces);
            interfaceTable.setItems(filteredInterfaceTableDatas);

            updateInterfacePagination();

            // Cập nhật danh sách hugepage và RSS
            Map<String, NumaHugePageTableData> hugePagesAndRss = hugePageParser();
            numaHugePageTableDatas.setAll(hugePagesAndRss.values());
            filteredNumaHugePagetableTableDatas.setAll(hugePagesAndRss.values());
            numaHugePageTable.setItems(filteredNumaHugePagetableTableDatas);
            updateNumaHugePagePagination();
        }
        catch (DaoException e) {
            AlertUtils.showAlert("Lỗi", e.getMessage(), "ERROR");
        }
    }
    private void showModuleByNode(long nodeId) {
        try {
//            List<ModuleTableData> moduleTableDatas = clusterPresenter.findAllByCluster(nodeId);
//            moduleTableDatas.forEach(item -> {
//                if(selectedModules.containsKey(item.getClusterModuleId())) {
//                    item.setSelected(true);
//                }
//            });
//            moduletableTableDatas.setAll(moduleTableDatas);
//            filteredModuletableTableDatas.setAll(moduleTableDatas);
//            moduleTable.setItems(filteredModuletableTableDatas);
//            modulePaginationUtil.updatePagination(modulePagination, filteredModuletableTableDatas, moduletableTableDatas, moduleTable);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private VBox createNumaHugePageTable() {
        numaHugePageTable = new TableView<>();
        TableColumn<NumaHugePageTableData, Number> sttCol = new TableColumn<>("STT");
        TableColumn<NumaHugePageTableData, String> clusterNameCol = CreateColumnTableUtil.createColumn("Cluster name", NumaHugePageTableData::clusterNameProperty);
        TableColumn<NumaHugePageTableData, String> hugePagesTotal = CreateColumnTableUtil.createColumn("HugePages total", NumaHugePageTableData::hugePagesTotalProperty);
        TableColumn<NumaHugePageTableData, String> hugePagesFree = CreateColumnTableUtil.createColumn("HugePages free", NumaHugePageTableData::hugePagesFreeProperty);
        TableColumn<NumaHugePageTableData, String> cpuBinding = CreateColumnTableUtil.createColumn("Cpu(s)", NumaHugePageTableData::cpuBindingProperty);

        setColumnPercentWidth(sttCol, 8);
        setColumnPercentWidth(clusterNameCol, 23);
        setColumnPercentWidth(hugePagesTotal, 23);
        setColumnPercentWidth(hugePagesFree, 23);
        setColumnPercentWidth(cpuBinding, 23);

        numaHugePageTable.getColumns().addAll(sttCol, clusterNameCol, hugePagesFree, hugePagesTotal, cpuBinding);

        numaHugePageTable.setPrefHeight(400);
        numaHugePageTable.setMaxHeight(300);
        numaHugePageTable.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccccccc; -fx-border-width: 1px;");

        numaHugePageTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(NumaHugePageTableData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #cccccc; -fx-text-fill: white;");
                } else {
                    setStyle(getIndex() % 2 == 0 ? "" : "-fx-background-color: #cccccc;");
                }
            }
            {
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    updateItem(getItem(), isEmpty());
                });
            }
        });

        numaHugePageTable.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/module_service_insert/css/table_row.css")
                ).toExternalForm()
        );

        numaHugePageTable.setRowFactory(tv -> new TableRow<NumaHugePageTableData>() {
            @Override
            protected void updateItem(NumaHugePageTableData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                } else {
                    // Zebra style: chẵn trắng, lẻ xám
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #ffffff;");
                    } else {
                        setStyle("-fx-background-color: #f1f1f1;");
                    }
                }
            }

            {
                // Đảm bảo khi click chọn/deselect thì màu cũng update lại
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    updateItem(getItem(), isEmpty());
                });
            }
        });

        sttCol.setCellFactory(col -> new TableCell<NumaHugePageTableData, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    int rowIndex = getIndex();
                    int pageIndex = interfacePagination.getCurrentPageIndex();
                    int offset = pageIndex * 5;

                    setText(String.valueOf(offset + rowIndex + 1));
                }
            }
        });

        numaHugePagePagination = numaPaginationUtil.createPagination(numaHugePageTable, filteredNumaHugePagetableTableDatas, numaHugePageTableDatas);
        HBox inputSearch = createFilterModuleTable();
        VBox box = new VBox(inputSearch, numaHugePageTable, numaHugePagePagination);
        box.setSpacing(0);
        box.setPadding(Insets.EMPTY);
        box.setStyle("-fx-padding: 0; -fx-background-insets: 0;");
        updateNumaHugePagePagination();
        return box;
    }

    private HBox createFilterModuleTable() {
        tfModuleName = new TextField();
        tfModuleName.setPromptText("Tìm theo tên cụm");
        tfModuleName.textProperty().addListener((obs, oldVal, newVal) -> filterModuleNameTables());
        tfModuleName.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        HBox filterRow = new HBox(tfModuleName);
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));
        filterRow.setStyle("-fx-background-color: #ffffff; -fx-padding: 10 5; -fx-spacing: 5px; -fx-border-width: 0 0 1px 0; -fx-border-color: #cccccc");
        return filterRow;
    }

    private void filterModuleNameTables() {
        ObservableList<NumaHugePageTableData> filteredNumaHugeData = FXCollections.observableArrayList();
        String moduleNameFilter = NormalizeString.normalizeString(tfModuleName.getText() != null ? tfModuleName.getText().trim().toLowerCase() : "");
        filteredNumaHugeData.setAll(
                numaHugePageTableDatas.stream()
                        .filter(item -> {
                            String moduleName = NormalizeString.normalizeString(item.getClusterName() != null ? item.getClusterName().toLowerCase() : "");
                            return moduleName.trim().contains(moduleNameFilter);
                        })
                        .toList()
        );
        numaPaginationUtil.updatePagination(
                numaHugePagePagination,
                filteredNumaHugePagetableTableDatas,
                filteredNumaHugeData,
                numaHugePageTable
        );
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

    private List<InterfaceTableData> parseInterfaces(List<String> lines) {
        List<InterfaceTableData> list = new ArrayList<>();

        for (String line : lines) {
            if (line == null || line.isBlank()) continue;

            // Tách theo regex: Name: ... Driver: ... RSS: ... [description]
            Pattern p = Pattern.compile(
                    "Name:\\s+(\\S+)\\s+Driver:\\s+(\\S+)\\s+RSS:\\s+(\\d+)\\s+\\[(.+)]"
            );
            Matcher m = p.matcher(line);
            if (m.find()) {
                String name = m.group(1);
                String driver = m.group(2);
                int rss = Integer.parseInt(m.group(3));
                String description = m.group(4);

                list.add(new InterfaceTableData(name, rss, driver, description));
            }
        }

        return list;
    }

    private Map<String, NumaHugePageTableData> hugePageParser() {
        String input = """
            Node 0 HugePages_Total:   256
            Node 0 HugePages_Free:    256
            Node 1 HugePages_Total: 10240
            Node 1 HugePages_Free:   5620
            Node 2 HugePages_Total:   256
            Node 2 HugePages_Free:    256
            Node 3 HugePages_Total:  4196
            Node 3 HugePages_Free:   2195
            """;

        Map<String, NumaHugePageTableData> result = new HashMap<>();
        for(String line : input.split("\\n")) {
            if(line.isBlank()) continue;
            String[] parts = line.split("\\s+");
            String clusterName = parts[0] + parts[1];
            String metric = parts[2].replace(":", "");
            String value = parts[3];

            NumaHugePageTableData numaHugePageTableData = result.computeIfAbsent(clusterName.toLowerCase(), k -> new NumaHugePageTableData(clusterName, "", "", ""));
            if(metric.equals("HugePages_Total")) {
                numaHugePageTableData.setHugePagesTotal(value);
            }
            else if(metric.equals("HugePages_Free")) {
                numaHugePageTableData.setHugePagesFree(value);
            }
        }

        String rssInput = """
                NUMA node0 CPU(s):                    0-23
                NUMA node1 CPU(s):                    24-47
                NUMA node2 CPU(s):                    48-71
                NUMA node3 CPU(s):                    72-95
                """;
        for(String line : rssInput.split("\\n")) {
            String[] parts = line.split("\\s+");
            if(line.isBlank() || parts.length < 4) continue;
            String clusterName = parts[1];
            String metric = parts[2].replace(":", "");
            String cpuBinding = parts[3];

            NumaHugePageTableData numaHugePageTableData = result.computeIfAbsent(clusterName, k -> new NumaHugePageTableData(clusterName, "", "", ""));
            numaHugePageTableData.setCpuBinding(cpuBinding);
        }
        return result;
    }

    // convert data to json
    private void convertJson(Path pathFile) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray rssInterfaceArr = new JSONArray();
            JSONArray hugePageArr = new JSONArray();
            interfaceTableDatas.stream().forEach(i -> {
                JSONObject rssInterface = new JSONObject();
                rssInterface.put("name", i.getInterfaceName());
                rssInterface.put("driver", i.getDriver());
                rssInterface.put("rss", i.getRssCount());
                rssInterface.put("comment", i.getDescription());
                rssInterfaceArr.put(rssInterface);
            });
            numaHugePageTableDatas.stream().forEach(numa -> {
                JSONObject hugePage = new JSONObject();
                hugePage.put("name", numa.getClusterName());
                hugePage.put("HugePage_total", numa.getHugePagesTotal());
                hugePage.put("HugePage_free", numa.getHugePagesFree());
                hugePage.put("Cpu(s)", numa.getCpuBinding());
                hugePageArr.put(hugePage);
            });

            jsonObject.put("RSS", rssInterfaceArr);
            jsonObject.put("HugePage", hugePageArr);

            FileWriter fileWriter = new FileWriter(pathFile.toString(), StandardCharsets.UTF_8);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateInterfacePagination() {
        interfacePaginationUtil.updatePagination(
                interfacePagination,
                filteredInterfaceTableDatas,
                interfaceTableDatas,
                interfaceTable
        );
    }

    private void updateNumaHugePagePagination() {
        numaPaginationUtil.updatePagination(
                numaHugePagePagination,
                filteredNumaHugePagetableTableDatas,
                numaHugePageTableDatas,
                numaHugePageTable
        );
    }
}
