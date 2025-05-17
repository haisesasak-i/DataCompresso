package com.example.datacompresso.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AlgorithmSelectionScene {

    public static Scene create(Stage primaryStage, Runnable onSelectHuffman, Runnable onSelectLZW, Runnable onBack) {
        // Title Label with neon glow + pulse animation
        Label titleLabel = new Label("Choose Compression Algorithm");
        titleLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 40));
        titleLabel.setTextFill(Color.web("#00fff7"));
        DropShadow neonGlow = new DropShadow(30, Color.web("#00fff7"));
        neonGlow.setSpread(0.4);
        titleLabel.setEffect(neonGlow);

        // Pulse animation for neon glow on title
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(neonGlow.radiusProperty(), 30)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(neonGlow.radiusProperty(), 50))
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        // Buttons with same gradient style and hover animation from welcome scene
        Button huffmanBtn = createGradientButton("Huffman Compression",
                "#00ffd5", "#00b29e", "#00ffc2");
        huffmanBtn.setOnAction(e -> onSelectHuffman.run());

        Button lzwBtn = createGradientButton("LZW Compression",
                "#00ffd5", "#00b29e", "#00ffc2");
        lzwBtn.setOnAction(e -> onSelectLZW.run());

        Button backBtn = createGradientButton("Back",
                "#006666", "#004d4d", "#008080");
        backBtn.setOnAction(e -> onBack.run());

        VBox root = new VBox(30, titleLabel, huffmanBtn, lzwBtn, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));

        // Background with radial gradient, same as welcome scene
        root.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 70%, #001F26 0%, #000000 100%);");

        return new Scene(root, 720, 480);
    }

    private static Button createGradientButton(String text, String baseColor, String darkColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
        btn.setTextFill(Color.WHITE);
        btn.setPrefWidth(250);
        btn.setPrefHeight(50);
        btn.setStyle("-fx-background-radius: 25; -fx-cursor: hand;");
        setButtonGradient(btn, baseColor, darkColor);

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

    private static void setButtonGradient(Button btn, String topColor, String bottomColor) {
        btn.setStyle("-fx-background-radius: 25;" +
                "-fx-background-insets: 0;" +
                "-fx-background-color: linear-gradient(to bottom, " + topColor + ", " + bottomColor + ");" +
                "-fx-cursor: hand;");
    }
}
