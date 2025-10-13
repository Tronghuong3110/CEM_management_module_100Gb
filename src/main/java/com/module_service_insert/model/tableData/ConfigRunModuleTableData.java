package com.module_service_insert.model.tableData;

import javafx.beans.property.*;

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
    private ArgumentsTableData argumentsTableData;
    private String id;
    private boolean isUpdate;

    public ConfigRunModuleTableData() {
        argumentsTableData = new ArgumentsTableData();
    }

    public ConfigRunModuleTableData(String moduleName, String status, String command, String interfaceName, String clusterName) {
        this.moduleName.setValue(moduleName);
        this.status.setValue(status);
        this.command.setValue(command);
        this.interfaceName.setValue(interfaceName);
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

    public void setArgs(ArgumentsTableData argumentsTableData) {
        this.argumentsTableData = argumentsTableData;
    }
    public ArgumentsTableData getArgs() {return this.argumentsTableData;}

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
}
