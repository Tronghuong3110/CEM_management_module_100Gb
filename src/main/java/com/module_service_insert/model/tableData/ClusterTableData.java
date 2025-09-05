package com.module_service_insert.model.tableData;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author Trọng Hướng
 */
public class ClusterTableData {
    private final SimpleLongProperty id = new SimpleLongProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleIntegerProperty status = new SimpleIntegerProperty();
    private final SimpleStringProperty statusStr = new SimpleStringProperty();
    private final SimpleIntegerProperty numberModule = new SimpleIntegerProperty();
    private final SimpleStringProperty baseFolder = new SimpleStringProperty();

    public ClusterTableData(long id, String name, String description, int status, String statusStr, int numberModule, String baseFolder) {
        this.name.set(name);
        this.description.set(description);
        this.status.set(status);
        this.statusStr.set(statusStr);
        this.id.set(id);
        this.numberModule.set(numberModule);
        this.baseFolder.set(baseFolder);
    }

    public ClusterTableData(long id, String name, String status, int numberModule, String baseFolder) {
        this.id.set(id);
        this.name.set(name);
        this.statusStr.set(status);
        this.numberModule.set(numberModule);
        this.baseFolder.set(baseFolder);
    }

    public ClusterTableData(String description, String clusterName, String status) {
        this.name.set(clusterName);
        this.statusStr.set(status);
        this.description.set(description);
    }

    public SimpleStringProperty nameProperty() {return this.name;}
    public SimpleStringProperty descriptionProperty() {return this.description;}
    public SimpleIntegerProperty statusProperty() {return this.status;}
    public SimpleStringProperty statusStrProperty() {return this.statusStr;}
    public SimpleLongProperty idProperty() {return this.id;}
    public SimpleIntegerProperty numberModuleProperty() {return this.numberModule;}
    public SimpleStringProperty baseFolderProperty() {return this.baseFolder;}

    public String getName() {return this.name.getValue();}
    public String getDescription() {return this.description.getValue();}
    public int getStatus() {return this.status.getValue();}
    public String getStatusStr() {return this.statusStr.getValue();}
    public Long getId() {
        return id.get();
    }
    public int getNumberModule() {return this.numberModule.get();}
    public String getBaseFolder() {return this.baseFolder.get();}

    public void setName(String name) {this.name.setValue(name);}
    public void setDescription(String description) {this.description.setValue(description);}
    public void setStatus(int status) {this.status.setValue(status);}
    public void setStatusStr(String statusStr) {this.statusStr.setValue(statusStr);}
    public void setNumberModule(int numberModule) {this.numberModule.setValue(numberModule);}
    public void setBaseFolder(String baseFolder) {this.baseFolder.setValue(baseFolder);}

}
