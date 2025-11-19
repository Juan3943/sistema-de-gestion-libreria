module org.example.libreria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;


    opens com.libreria to javafx.fxml;
    exports com.libreria;
    exports com.libreria.controller;
    opens com.libreria.controller to javafx.fxml;
    exports com.libreria.util;
    opens com.libreria.util to javafx.fxml;
    exports com.libreria.model;
    opens com.libreria.model to javafx.fxml;
    exports com.libreria.model.reportes;
    opens com.libreria.model.reportes to javafx.fxml;
    opens com.libreria.dao to javafx.base;
}