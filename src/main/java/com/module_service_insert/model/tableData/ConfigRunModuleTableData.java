package com.module_service_insert.model.tableData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
}
