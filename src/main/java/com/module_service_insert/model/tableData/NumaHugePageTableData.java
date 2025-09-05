package com.module_service_insert.model.tableData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Trọng Hướng
 */
public class NumaHugePageTableData {
    private final StringProperty clusterName = new SimpleStringProperty();
    private final StringProperty hugePagesTotal = new SimpleStringProperty();
    private final StringProperty hugePagesFree = new SimpleStringProperty();
    private final StringProperty cpuBinding = new SimpleStringProperty();

    public NumaHugePageTableData(String clusterName, String hugePagesTotal, String hugePagesFree, String cpuBinding) {
        this.clusterName.set(clusterName);
        this.hugePagesTotal.set(hugePagesTotal);
        this.hugePagesFree.set(hugePagesFree);
        this.cpuBinding.set(cpuBinding);
    }

    public StringProperty clusterNameProperty() {return this.clusterName;}
    public StringProperty hugePagesTotalProperty() {return this.hugePagesTotal;}
    public StringProperty hugePagesFreeProperty() {return this.hugePagesFree;}
    public StringProperty cpuBindingProperty() {return this.cpuBinding;}

    public String getClusterName() {return this.clusterName.get();}
    public void setClusterName(String clusterName) {this.clusterName.set(clusterName);}

    public String getHugePagesTotal() {return this.hugePagesTotal.get();}
    public void setHugePagesTotal(String hugePagesTotal) {this.hugePagesTotal.set(hugePagesTotal);}

    public String getHugePagesFree() {return this.hugePagesFree.get();}
    public void setHugePagesFree(String hugePagesFree) {this.hugePagesFree.set(hugePagesFree);}

    public String getCpuBinding() {return this.cpuBinding.get();}
    public void setCpuBinding(String cpuBinding) {this.cpuBinding.set(cpuBinding);}


}
