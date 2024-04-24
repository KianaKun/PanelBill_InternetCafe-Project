module org.javastart.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires mysql.connector.j;

    exports org.javastart.main.Controller;
    opens org.javastart.main.Controller to javafx.fxml;
    exports org.javastart.main.Database;
    opens org.javastart.main.Database to javafx.fxml;
    exports org.javastart.main.Login;
    opens org.javastart.main.Login to javafx.fxml;
}