package com.example.datacompresso;

import javafx.application.Application;
import javafx.stage.Stage;

import com.example.datacompresso.ui.WelcomeScene;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        new WelcomeScene().start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}