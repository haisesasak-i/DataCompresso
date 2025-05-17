package com.example.datacompresso.ui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeScene extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(900);
        primaryStage.setWidth(900);
        // App Title with neon glow and pulse animation
        Label titleLabel = new Label("Data Compresso");
        titleLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 60));
        titleLabel.setTextFill(Color.web("#00fff7"));
        DropShadow neonGlow = new DropShadow(30, Color.web("#00fff7"));
        neonGlow.setSpread(0.4);
        titleLabel.setEffect(neonGlow);

        // Pulse animation for neon glow
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(neonGlow.radiusProperty(), 30)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(neonGlow.radiusProperty(), 50))
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        // Tagline Label
        Label taglineLabel = new Label("Compress your data smartly with Huffman & LZW");
        taglineLabel.setFont(Font.font("Montserrat", FontWeight.SEMI_BOLD, 20));
        taglineLabel.setTextFill(Color.web("#5efff7"));
        taglineLabel.setEffect(new DropShadow(5, Color.web("#00b7b1")));

        // Explanation Label
        Label explanationLabel = new Label(
                "Effortlessly reduce file sizes and boost efficiency.\n" +
                        "Save bandwidth and storage with powerful algorithms."
        );
        explanationLabel.setFont(Font.font("Montserrat", 16));
        explanationLabel.setTextFill(Color.web("#90f0f8"));
        explanationLabel.setWrapText(true);
        explanationLabel.setMaxWidth(480);
        explanationLabel.setAlignment(Pos.CENTER);

        // Buttons
        Button startButton = createGradientButton("Get Started",
                "#00ffd5", "#00b29e", "#00ffc2");
        Button infoButton = createGradientButton("Info",
                "#006666", "#004d4d", "#008080");


        // Button container
        HBox buttons = new HBox(30, startButton, infoButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(25, 0, 0, 0));

        // Root layout
        VBox root = new VBox(18, titleLabel, taglineLabel, explanationLabel, buttons);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setPrefSize(720, 480);

        // Background with radial gradient and subtle vignette
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#001F26")),
                new Stop(1, Color.web("#000000"))
        };
        RadialGradient backgroundGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.7, true, CycleMethod.NO_CYCLE, stops);
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene welcomeScene = new Scene(root);
        primaryStage.setScene(welcomeScene);
        infoButton.setOnAction(e -> {
            Scene infoScene = InfoScene.create(primaryStage, () -> {
                primaryStage.setScene(welcomeScene);
            });
            primaryStage.setScene(infoScene);
        });
        startButton.setOnAction(e -> {
            Scene algorithmSelectionScene = AlgorithmSelectionScene.create(
                    primaryStage,
                    () -> primaryStage.setScene(welcomeScene), // Temporary: go back on Huffman click
                    () -> primaryStage.setScene(welcomeScene), // Temporary: go back on LZW click
                    () -> primaryStage.setScene(welcomeScene)  // Back button
            );
            primaryStage.setScene(algorithmSelectionScene);
        });
        primaryStage.setTitle("Data Compresso - Welcome");
        primaryStage.show();
    }

    private Button createGradientButton(String text, String baseColor, String darkColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
        btn.setTextFill(Color.WHITE);
        btn.setPrefWidth(180);
        btn.setPrefHeight(45);
        btn.setStyle("-fx-background-radius: 25; -fx-cursor: hand;");
        setButtonGradient(btn, baseColor, darkColor);

        // Hover effect: brighten + scale up smoothly
        btn.setOnMouseEntered(e -> {
            setButtonGradient(btn, hoverColor, darkColor);
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
            btn.setEffect(new DropShadow(15, Color.web("#00fff7")));
        });

        btn.setOnMouseExited(e -> {
            setButtonGradient(btn, baseColor, darkColor);
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setEffect(null);
        });

        return btn;
    }

    private void setButtonGradient(Button btn, String topColor, String bottomColor) {
        btn.setStyle("-fx-background-radius: 25;" +
                "-fx-background-insets: 0;" +
                "-fx-background-color: linear-gradient(to bottom, " + topColor + ", " + bottomColor + ");" +
                "-fx-cursor: hand;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
