package com.module_service_insert.model.tableData;

import javafx.beans.property.*;

/**
 * @author Trọng Hướng
 */
public class ModuleTableData {
    private final LongProperty moduleId = new SimpleLongProperty();
    private final StringProperty moduleName = new SimpleStringProperty();
    private final StringProperty command = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty cpuList = new SimpleStringProperty();
    private final StringProperty runFolder = new SimpleStringProperty();
    private final StringProperty configFolder = new SimpleStringProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final BooleanProperty isChange = new SimpleBooleanProperty(false);
    private final LongProperty clusterModuleId = new SimpleLongProperty();
    private final StringProperty logPath = new SimpleStringProperty();
    private final StringProperty clusterName = new SimpleStringProperty();

    public ModuleTableData(String moduleName, String command, String description, String cpuList, String runFolder, String configFolder, long clusterModuleId, String logPath, String clusterName) {
        this.moduleName.setValue(moduleName);
        this.command.setValue(command);
        this.description.setValue(description);
        this.cpuList.setValue(cpuList);
        this.runFolder.setValue(runFolder);
        this.configFolder.setValue(configFolder);
        this.isChange.setValue(false);
        this.clusterModuleId.setValue(clusterModuleId);
        this.logPath.setValue(logPath);
        this.clusterName.setValue(clusterName);
    }

    public ModuleTableData(long moduleId, String moduleName, String command, String description) {
        this.moduleId.set(moduleId);
        this.moduleName.setValue(moduleName);
        this.command.setValue(command);
        this.description.setValue(description);
    }

    public ModuleTableData(String moduleName, String cpuList, String runPath, String configPath, String command) {
        this.moduleName.setValue(moduleName);
        this.cpuList.setValue(cpuList);
        this.runFolder.setValue(runPath);
        this.configFolder.setValue(configPath);
        this.command.setValue(command);
        this.isChange.setValue(false);
    }

    public ModuleTableData(long id, String moduleName, String cpuList, String runPath, String configPath, String command, long moduleId) {
        this.setClusterModuleId(id);
        this.moduleName.setValue(moduleName);
        this.cpuList.setValue(cpuList);
        this.runFolder.setValue(runPath);
        this.configFolder.setValue(configPath);
        this.command.setValue(command);
        this.isChange.setValue(false);
        this.moduleId.setValue(moduleId);
    }

    public StringProperty moduleNameProperty() {return moduleName;}
    public StringProperty commandProperty() {return command;}
    public BooleanProperty selectedProperty() {return selected;}
    public StringProperty descriptionProperty() {return description;}
    public StringProperty  cpuListProperty() {return cpuList;}
    public StringProperty runFolderProperty() {return runFolder;}
    public StringProperty configFolderProperty() {return configFolder;}
    public StringProperty logPathProperty() {return logPath;}

    public String getModuleName() {return this.moduleName.getValue();}
    public String getCommand() {return this.command.getValue();}
    public String getStatus() {return this.status.getValue();}
    public boolean isSelected() {return this.selected.getValue();}
    public String getDescription() {return this.description.getValue();}
    public String getCpuList() {return this.cpuList.getValue();}
    public String getConfigPath() {return this.configFolder.getValue();}
    public String getRunFolder() {return this.runFolder.getValue();}
    public Long getModuleId() {return this.moduleId.getValue();}
    public Boolean isChange() {return this.isChange.getValue();}
    public Long getClusterModuleId() {return this.clusterModuleId.getValue();}
    public String getLogPath() {return this.logPath.getValue();}
    public String getClusterName() {return this.clusterName.getValue();}

    public void setDescription(String description) {this.description.setValue(description);}
    public void setCommand(String command) {this.command.setValue(command);}
    public void setStatus(String status) {this.status.setValue(status);}
    public void setSelected(boolean selected) {this.selected.setValue(selected);}
    public void setModuleId(long moduleId) {this.moduleId.setValue(moduleId);}
    public void setIsChange(boolean isChange) {this.isChange.setValue(isChange);}
    public void setClusterModuleId(long clusterModuleId) {this.clusterModuleId.setValue(clusterModuleId);}
    public void setLogPath(String logPath) {this.logPath.setValue(logPath);}
    public void setClusterName(String clusterName) {this.clusterName.setValue(clusterName);}
    public void setModuleName(String moduleName) {this.moduleName.setValue(moduleName);}
}
