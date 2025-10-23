package com.module_service_insert.model.tableData;

import com.module_service_insert.utils.functionUtils.FileUtils;
import javafx.beans.property.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ConfigRunModuleTableData {
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final StringProperty moduleName = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty command = new SimpleStringProperty();
    private final StringProperty interfaceName = new SimpleStringProperty();
    private final StringProperty clusterName = new SimpleStringProperty();
    private final LongProperty pId = new SimpleLongProperty();
    private HashMap<String, Object> argsMap;
    private String id;
    private boolean isUpdate;

    public ConfigRunModuleTableData() {
        this.argsMap = new LinkedHashMap<>();
    }

    public ConfigRunModuleTableData(String moduleName, String status, String command, String interfaceName, String clusterName) {
        this.moduleName.setValue(moduleName);
        this.status.setValue(status);
        this.command.setValue(command);
        this.interfaceName.setValue(interfaceName);
        this.argsMap = new HashMap<>();
    }

    public StringProperty moduleNameProperty() {return moduleName;}
    public StringProperty statusProperty() {return status;}
    public StringProperty commandProperty() {return command;}
    public StringProperty interfaceNameProperty() {return interfaceName;}
    public StringProperty clusterNameProperty() {return clusterName;}
    public BooleanProperty selectedProperty() {return selected;}

    public void setSelected(Boolean selected) {this.selected.set(selected);}

    public Boolean isSelected() {return selected.get();}
    public String getModuleName() {return this.moduleName.get();}
    public String getStatus() {return this.status.get();}
    public String getInterfaceName() {return this.interfaceName.get();}
    public String getClusterName() {return this.clusterName.get();}
    public String getCommand() {return this.command.get();}

    public void setPid(long pId) {this.pId.set(pId);}
    public long getPid() {return this.pId.get();}
    public void setId (String id) {this.id=id;}
    public String getId() {return this.id;}
    public void setCommand(String command) { this.command.set(command); }
    public void setModuleName(String moduleName) {this.moduleName.set(moduleName);}
    public void setStatus(String status) {this.status.set(status);}
    public void setInterfaceName(String interfaceName) {this.interfaceName.set(interfaceName);}

    public void setIsUpdate(boolean isUpdate) {this.isUpdate = isUpdate;}
    public boolean isUpdate() {return this.isUpdate;}
    public HashMap<String, Object> getArgsMap() {return this.argsMap;}

    // dùng để export ra file + tạo arg cho lệnh chạy
    public String getArgsAsString() {
        JSONObject arguments = FileUtils.readFileArgumentsConfig();
        Map<String, Object> args = new LinkedHashMap<>();
        for(String key : arguments.keySet()) {
            JSONObject argument = arguments.getJSONObject(key);
            String flag = argument.getString("command_name");
            args.put(flag, flag.equalsIgnoreCase("-a") && this.argsMap.getOrDefault(key, "").equals("true") ? "" : this.argsMap.getOrDefault(key, "unknown"));
        }
//        args.put("-q", queue.get() != null ? queue.get() : this.argsMap.getOrDefault("queue", "unknown"));
//        args.put("-g", cpuBinding.get() != null ? cpuBinding.get() : this.argsMap.getOrDefault("cpu worker", "unknown"));
//        args.put("-o", output.get() != null ? output.get() : this.argsMap.getOrDefault("output", "unknown"));
//        args.put("-l", thread.get() != null ? thread.get() : this.argsMap.getOrDefault("thread", "unknown"));
//        args.put("-a", ativeWait.get() != null ? ativeWait.get() : this.argsMap.getOrDefault("active wait", "unknown"));
//        args.put("-b", macAddress.get() != null ? macAddress.get() : this.argsMap.getOrDefault("mac address", "unknown"));
//        args.put("-c", id.get() != null ? id.get() : this.argsMap.getOrDefault("id", "unknown"));
//        args.put("-t", minOutputSize.get() != null ? minOutputSize.get() : this.argsMap.getOrDefault("min output size", "unknown"));
//        args.put("-r", cpuDispatch.get() != null ? cpuDispatch.get() : this.argsMap.getOrDefault("cpu dispatcher", "unknown"));
//        args.put("-j", cpuOutput.get() != null ? cpuOutput.get() : this.argsMap.getOrDefault("cpu output", "unknown"));

        String cmd = args.entrySet().stream()
                .filter(e -> e.getValue() != null && !"unknown".equalsIgnoreCase((String) e.getValue()))
                .map(e -> String.format("%s %s", e.getKey(), e.getValue()))
                .collect(Collectors.joining(" "));
        return cmd;
    }
    public void setArgsMap(HashMap<String, Object> argsMap) {this.argsMap = argsMap;}

    private void addIfValid(Map<String, String> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value);
        }
    }
}
