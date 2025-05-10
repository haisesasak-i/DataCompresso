module com.example.datacompresso {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.datacompresso to javafx.fxml;
    exports com.example.datacompresso;
}