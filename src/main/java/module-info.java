module com.manager.stock.module_service_insert {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.javadoc;
    requires org.mariadb.jdbc;
    requires org.slf4j;
    requires apache.any23.encoding;
    requires org.json;

    opens com.module_service_insert to javafx.fxml;
    exports com.module_service_insert;
}