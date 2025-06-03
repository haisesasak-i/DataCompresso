package com.example.datacompresso.ui;

import com.example.datacompresso.LZW.LZWAlgorithm;
import com.example.datacompresso.LZW.MyList;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class LZWScene {

    private static MyList sharedCodeList;
    private static LZWAlgorithm lzwInstance; // shared instance between compress/decompress
    private static Runnable run ;
    public static Scene create(Stage stage, Runnable onBack) {
        run = onBack;
        Label titleLabel = new Label("LZW Compression");
        titleLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 40));
        titleLabel.setTextFill(Color.web("#00fff7"));
        DropShadow neonGlow = new DropShadow(30, Color.web("#00fff7"));
        neonGlow.setSpread(0.4);
        titleLabel.setEffect(neonGlow);

        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(neonGlow.radiusProperty(), 30)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(neonGlow.radiusProperty(), 50))
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        Button compressBtn = AlgorithmSelectionScene.createGradientButton("Compress", "#00ffd5", "#00b29e", "#00ffc2");
        Button decompressBtn = AlgorithmSelectionScene.createGradientButton("Decompress", "#00ffd5", "#00b29e", "#00ffc2");
        Button backBtn = AlgorithmSelectionScene.createGradientButton("Back", "#006666", "#004d4d", "#008080");

        compressBtn.setOnAction(e -> showCompressUI(stage));
        decompressBtn.setOnAction(e -> showDecompressUI(stage));
        backBtn.setOnAction(e -> onBack.run());

        VBox root = new VBox(30, titleLabel, compressBtn, decompressBtn, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 70%, #001F26 0%, #000000 100%);");

        return new Scene(root, 720, 480);
    }

    private static void showCompressUI(Stage stage) {
        Label messageLabel = new Label("Enter Message or Choose File:");
        messageLabel.setTextFill(Color.web("#00ffc8"));

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type your message here...");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(5);
        messageArea.setStyle("-fx-control-inner-background: #111111; -fx-text-fill: #00ffc8; -fx-font-family: 'Consolas'; -fx-border-color: #00ffd5;");

        Button fileBtn = new Button("Select File");
        fileBtn.setStyle("-fx-background-color: #004d4d; -fx-text-fill: white; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(300);

        Label progressLabel = new Label("0%");
        progressLabel.setTextFill(Color.web("#00ffc8"));
        progressLabel.setVisible(false);

        HBox progressBox = new HBox(10, progressBar, progressLabel);
        progressBox.setAlignment(Pos.CENTER);

        Button compressBtn = new Button("Compress");
        compressBtn.setStyle("-fx-background-color: linear-gradient(to right, #00b3b3, #006666); -fx-text-fill: white; -fx-font-weight: bold;");

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setVisible(false);
        resultArea.setPrefRowCount(6);
        resultArea.setStyle("-fx-control-inner-background: #111111; -fx-text-fill: #00ffc8; -fx-font-family: 'Consolas'; -fx-border-color: #00ffd5;");

        Button saveBtn = new Button("Save Output");
        saveBtn.setVisible(false);
        saveBtn.setStyle("-fx-background-color: #004d4d; -fx-text-fill: white; -fx-font-weight: bold;");

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #222; -fx-text-fill: white;");

        fileBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    messageArea.setText(br.lines().reduce("", (a, b) -> a + "\n" + b));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        compressBtn.setOnAction(e -> {
            String message = messageArea.getText().trim();

            if (message.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Input", "Please enter a message or select a file.");
                return;
            }

            progressBar.setProgress(0);
            progressBar.setVisible(true);
            progressLabel.setVisible(true);
            resultArea.setVisible(false);
            saveBtn.setVisible(false);

            Timeline timeline = new Timeline();
            KeyFrame start = new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0));
            KeyFrame end = new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 1));
            timeline.getKeyFrames().addAll(start, end);

            progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
                int percent = (int) (newVal.doubleValue() * 100);
                progressLabel.setText(percent + "%");
            });

            timeline.setOnFinished(ev -> {
                try {
                    byte[] inputBytes = message.getBytes("UTF-8");
                    sharedCodeList = new MyList();
                    lzwInstance = new LZWAlgorithm(); // Create and save instance
                    byte[] compressedBytes = lzwInstance.compress(inputBytes, sharedCodeList);

                    StringBuilder sb = new StringBuilder();
                    for (byte b : compressedBytes) {
                        sb.append(String.format("%02X ", b));
                    }

                    resultArea.setText(sb.toString().trim());
                    resultArea.setVisible(true);
                    saveBtn.setVisible(true);
                } catch (Exception ex) {
                    resultArea.setText("Compression failed: " + ex.getMessage());
                    resultArea.setVisible(true);
                    ex.printStackTrace();
                }
            });

            timeline.play();
        });

        saveBtn.setOnAction(ev -> {
            FileChooser fc = new FileChooser();
            File outFile = fc.showSaveDialog(stage);
            if (outFile != null) {
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    String[] hexBytes = resultArea.getText().split(" ");
                    byte[] bytesToWrite = new byte[hexBytes.length];
                    for (int i = 0; i < hexBytes.length; i++) {
                        bytesToWrite[i] = (byte) Integer.parseInt(hexBytes[i], 16);
                    }
                    fos.write(bytesToWrite);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        backBtn.setOnAction(e -> stage.setScene(create(stage, run)));

        VBox layout = new VBox(12, messageLabel, messageArea, fileBtn, compressBtn, progressBox, resultArea, saveBtn, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 70%, #001F26 0%, #000000 100%);");

        Scene compressScene = new Scene(layout, 720, 680);
        stage.setScene(compressScene);
    }

    private static void showDecompressUI(Stage stage) {
        Label label = new Label("Select File to Decompress:");
        label.setTextFill(Color.web("#00ffc8"));

        Button fileBtn = new Button("Select File");
        fileBtn.setStyle("-fx-background-color: #004d4d; -fx-text-fill: white; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setPrefWidth(300);

        Button decompressBtn = new Button("Decompress");
        decompressBtn.setDisable(true);
        decompressBtn.setStyle("-fx-background-color: linear-gradient(to right, #00b3b3, #006666); -fx-text-fill: white; -fx-font-weight: bold;");

        Button backBtn = AlgorithmSelectionScene.createGradientButton("Back", "#006666", "#004d4d", "#008080");

        final File[] selectedFile = new File[1];

        fileBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                selectedFile[0] = file;
                decompressBtn.setDisable(false);
            }
        });

        decompressBtn.setOnAction(ev -> {
            if (selectedFile[0] == null) {
                showAlert(Alert.AlertType.WARNING, "No File Selected", "Please select a file to decompress.");
                return;
            }

            progressBar.setVisible(true);

            new Thread(() -> {
                try {
                    byte[] compressedBytes = java.nio.file.Files.readAllBytes(selectedFile[0].toPath());
                    int codeCount = (compressedBytes.length * 8) / 12;

                    if (lzwInstance == null) lzwInstance = new LZWAlgorithm();
                    byte[] decompressedBytes = lzwInstance.decompress(compressedBytes, codeCount);
                    String output = new String(decompressedBytes, "UTF-8");

                    javafx.application.Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        FileChooser saveChooser = new FileChooser();
                        File saveFile = saveChooser.showSaveDialog(stage);
                        if (saveFile != null) {
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                                writer.write(output);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() ->
                            showAlert(Alert.AlertType.ERROR, "Decompression Failed", "Could not decompress the file.")
                    );
                }
            }).start();
        });

        backBtn.setOnAction(e -> stage.setScene(create(stage, run)));

        VBox layout = new VBox(15, label, fileBtn, decompressBtn, progressBar, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 70%, #001F26 0%, #000000 100%);");

        Scene decompressScene = new Scene(layout, 720, 480);
        stage.setScene(decompressScene);
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
