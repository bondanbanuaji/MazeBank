module com.jmc.mazebank {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.jmc.mazebank to javafx.fxml;
    exports com.jmc.mazebank;
    exports com.jmc.mazebank.Controllers;
    exports com.jmc.mazebank.Controllers.Admin;
    exports com.jmc.mazebank.Controllers.Client;
    exports com.jmc.mazebank.Models;
    exports com.jmc.mazebank.Views;



//    requires javafx.controls;
//    requires javafx.fxml;

//    opens com.jmc.mazebank to javafx.fxml;
    opens com.jmc.mazebank.Controllers to javafx.fxml;
    opens com.jmc.mazebank.Controllers.Client to javafx.fxml;
//
//    exports com.jmc.mazebank;
//    exports com.jmc.mazebank.Controllers;
//    exports com.jmc.mazebank.Controllers.Client;
}