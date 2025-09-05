package com.module_service_insert.model.tableData;

import javafx.beans.property.*;

/**
 * @author Trọng Hướng
 */
public class ConfigClusterModuleTableData {
    private final StringProperty moduleName = new SimpleStringProperty();
    private final StringProperty command = new SimpleStringProperty();
    private final StringProperty cpuList = new SimpleStringProperty();
    private final StringProperty logFile = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty log = new SimpleStringProperty();
    private final StringProperty clusterName = new SimpleStringProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final LongProperty pid = new SimpleLongProperty();

    public ConfigClusterModuleTableData(String moduleName, String command, String cpuList, String logFile, String status, String log, String clusterName, long pid) {
        this.moduleName.setValue(moduleName);
        this.command.setValue(command);
        this.cpuList.setValue(cpuList);
        this.logFile.setValue(logFile);
        this.status.setValue(status);
        this.log.setValue(log);
        this.clusterName.setValue(clusterName);
        this.pid.setValue(pid);
    }

    public StringProperty moduleNameProperty() {return this.moduleName;}
    public StringProperty commandProperty() {return this.command;}
    public StringProperty cpuListProperty() {return this.cpuList;}
    public StringProperty logFileProperty() {return this.logFile;}
    public StringProperty statusProperty() {return this.status;}
    public StringProperty logProperty() {return this.log;}
    public StringProperty clusterNameProperty() {return this.clusterName;}
    public  BooleanProperty selectedProperty() {return this.selected;}

    public String getModuleName() {return this.moduleName.getValue();}
    public String getCommand() {return this.command.getValue();}
    public String getCpuList() {return this.cpuList.getValue();}
    public String getLogFile() {return this.logFile.getValue();}
    public String getStatus() {return this.status.getValue();}
    public String getLog() {return this.log.getValue();}

    public void setModuleName(String moduleName) {this.moduleName.setValue(moduleName);}
    public void setCommand(String command) {this.command.setValue(command);}
    public void setCpuList(String cpuList) {this.cpuList.setValue(cpuList);}
    public void setLogFile(String logFile) {this.logFile.setValue(logFile);}
    public void setStatus(String status) {this.status.setValue(status);}
    public void setLog(String log) {this.log.setValue(log);}

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public long getPid() {return pid.getValue();}
    public void setPid(long pid) {this.pid.setValue(pid);}
}
