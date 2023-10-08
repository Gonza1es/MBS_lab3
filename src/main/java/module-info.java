module com.example.lab2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;

    opens com.example.lab2 to javafx.fxml;
    exports com.example.lab2;
}