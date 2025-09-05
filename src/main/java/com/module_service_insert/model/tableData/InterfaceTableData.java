package com.module_service_insert.model.tableData;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Trọng Hướng
 */
public class InterfaceTableData {
    private final StringProperty interfaceName = new SimpleStringProperty();
    private final IntegerProperty rssCount = new SimpleIntegerProperty();
    private final StringProperty driver = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();


    public  InterfaceTableData(String interfaceName, int rssCount, String driver, String description) {
        this.interfaceName.set(interfaceName);
        this.rssCount.set(rssCount);
        this.driver.set(driver);
        this.description.set(description);
    }

    public StringProperty interfaceNameProperty() {return this.interfaceName;}
    public IntegerProperty rssCountProperty() {return this.rssCount;}
    public StringProperty driverProperty() {return this.driver;}
    public StringProperty descriptionProperty() {return this.description;}

    public String getInterfaceName() {return this.interfaceName.get();}
    public int getRssCount() {return this.rssCount.get();}
    public String getDriver() {return this.driver.get();}
    public String getDescription() {return this.description.get();}

}
