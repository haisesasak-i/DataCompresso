package com.example.datacompresso.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class InfoScene {

    public static Scene create(Stage primaryStage, Runnable onBack) {
        // Title with neon glow
        Label titleLabel = new Label("About Data Compression");
        titleLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.web("#00fff7"));
        DropShadow neonGlow = new DropShadow(30, Color.web("#00fff7"));
        neonGlow.setSpread(0.4);
        titleLabel.setEffect(neonGlow);

        // Info text
        Label infoLabel = new Label(
                "Data compression reduces file sizes to save storage space\n" +
                        "and speed up data transfer. It’s everywhere — from streaming\n" +
                        "videos to storing photos on your phone.\n\n" +

                        "Two popular methods:\n" +
                        "• Huffman Coding: Uses frequency of characters to assign shorter codes.\n" +
                        "• LZW (Lempel-Ziv-Welch): Builds a dictionary of repeated patterns.\n\n" +

                        "Example:\n" +
                        "Original text: \"aaaaabbbbcccdde\"\n" +
                        "Huffman Encoding compresses repeated characters\n" +
                        "LZW finds repeated patterns like \"aaa\", \"bbb\" and replaces with codes\n\n" +

                        "Combining these can improve compression efficiency\n" +
                        "especially with large or repetitive data."
        );
        infoLabel.setFont(Font.font("Montserrat", 18));
        infoLabel.setTextFill(Color.web("#a3f6f5"));
        infoLabel.setWrapText(true);
        infoLabel.setMaxWidth(560);
        infoLabel.setAlignment(Pos.CENTER);

        // Back button
        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        backButton.setTextFill(Color.WHITE);
        backButton.setPrefWidth(140);
        backButton.setPrefHeight(40);
        backButton.setStyle(
                "-fx-background-radius: 20; " +
                        "-fx-background-color: linear-gradient(to bottom, #00b29e, #007d73); " +
                        "-fx-cursor: hand;"
        );
        backButton.setOnMouseEntered(e -> {
            backButton.setStyle(
                    "-fx-background-radius: 20; " +
                            "-fx-background-color: linear-gradient(to bottom, #00f7d9, #00a98f); " +
                            "-fx-cursor: hand;"
            );
            backButton.setScaleX(1.05);
            backButton.setScaleY(1.05);
            backButton.setEffect(new DropShadow(15, Color.web("#00fff7")));
        });
        backButton.setOnMouseExited(e -> {
            backButton.setStyle(
                    "-fx-background-radius: 20; " +
                            "-fx-background-color: linear-gradient(to bottom, #00b29e, #007d73); " +
                            "-fx-cursor: hand;"
            );
            backButton.setScaleX(1.0);
            backButton.setScaleY(1.0);
            backButton.setEffect(null);
        });
        backButton.setOnAction(e -> onBack.run());

        // Layout
        VBox root = new VBox(25, titleLabel, infoLabel, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setPrefSize(720, 480);

        // Background radial gradient matching welcome scene
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#001F26")),
                new Stop(1, Color.web("#000000"))
        };
        RadialGradient backgroundGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.7, true,
                javafx.scene.paint.CycleMethod.NO_CYCLE, stops);
        root.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(backgroundGradient, null, null)));

        return new Scene(root);
    }
}
