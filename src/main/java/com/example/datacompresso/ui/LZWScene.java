package com.example.datacompresso.ui;
import java.io.FileOutputStream;
import com.example.datacompresso.LZW.LZWAlgorithm;
import com.example.datacompresso.LZW.MyList;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LZWScene {
    private static MyList sharedCodeList;
    private static LZWAlgorithm lzwInstance;
    private static Runnable backCallback;
    private static List<File> selectedFiles = new ArrayList<>();

    public static Scene create(Stage stage, Runnable onBack) {
        backCallback = onBack;
        if (lzwInstance == null) {
            lzwInstance = new LZWAlgorithm();
        }
        if (sharedCodeList == null) {
            sharedCodeList = new MyList();
        }
        StackPane backgroundPane = createAnimatedBackground();
        Label titleLabel = new Label("LZW Compression Studio");
        titleLabel.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 42));
        LinearGradient titleGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#667eea")), new Stop(0.5, Color.web("#764ba2")), new Stop(1, Color.web("#f093fb")));
        titleLabel.setTextFill(titleGradient);
        DropShadow titleGlow = new DropShadow(25, Color.web("#667eea"));
        titleGlow.setSpread(0.4);
        titleLabel.setEffect(titleGlow);
        HBox actionCards = new HBox(30);
        actionCards.setAlignment(Pos.CENTER);
        VBox compressCard = createActionCard("Compress Files", "Transform your data with LZW encoding", "#667eea", "#764ba2", () -> showCompressUI(stage));
        VBox decompressCard = createActionCard("Decompress Files", "Restore your original data perfectly", "#764ba2", "#f093fb", () -> showDecompressUI(stage));
        actionCards.getChildren().addAll(compressCard, decompressCard);
        Button backBtn = createStyledButton("← Back to Selection", "#6c757d", "#5a6268");
        backBtn.setOnAction(e -> backCallback.run());
        VBox mainContent = new VBox(40, titleLabel, actionCards, backBtn);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(60));
        Rectangle glassPane = createGlassPane(800, 500);
        StackPane root = new StackPane(backgroundPane, glassPane, mainContent);
        createEntranceAnimation(titleLabel, actionCards, backBtn);
        return new Scene(root, 900, 700);
    }

    private static void showCompressUI(Stage stage) {
        StackPane backgroundPane = createAnimatedBackground();
        selectedFiles.clear();
        Label titleLabel = new Label("Compress Your Files");
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#ffffff"));
        VBox fileArea = createFileSelectionArea(stage, true);
        VBox progressArea = createProgressArea();
        VBox statsArea = createStatsArea();
        HBox controlButtons = new HBox(20);
        controlButtons.setAlignment(Pos.CENTER);
        Button compressBtn = createStyledButton("🚀 Start Compression", "#667eea", "#764ba2");
        Button clearBtn = createStyledButton("🗑️ Clear All", "#dc3545", "#c82333");
        Button backBtn = createStyledButton("← Back", "#6c757d", "#5a6268");
        controlButtons.getChildren().addAll(compressBtn, clearBtn, backBtn);
        VBox layout = new VBox(25, titleLabel, fileArea, controlButtons, progressArea, statsArea);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setMaxWidth(700);
        compressBtn.setOnAction(e -> performCompression(stage, (ProgressBar) progressArea.getChildren().get(0), (Label) progressArea.getChildren().get(1), statsArea));
        clearBtn.setOnAction(e -> {
            clearSelection(fileArea);
            statsArea.setVisible(false);
        });
        backBtn.setOnAction(e -> stage.setScene(create(stage, backCallback)));
        Rectangle glassPane = createGlassPane(750, 700);
        StackPane root = new StackPane(backgroundPane, glassPane, layout);
        stage.setScene(new Scene(root, 900, 800));
    }

    private static void showDecompressUI(Stage stage) {
        StackPane backgroundPane = createAnimatedBackground();
        selectedFiles.clear();
        Label titleLabel = new Label("Decompress Your Files");
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#ffffff"));
        VBox fileArea = createFileSelectionArea(stage, false);
        VBox progressArea = createProgressArea();
        VBox statsArea = createStatsArea();
        HBox controlButtons = new HBox(20);
        controlButtons.setAlignment(Pos.CENTER);
        Button decompressBtn = createStyledButton("🔓 Start Decompression", "#764ba2", "#f093fb");
        Button clearBtn = createStyledButton("🗑️ Clear All", "#dc3545", "#c82333");
        Button backBtn = createStyledButton("← Back", "#6c757d", "#5a6268");
        controlButtons.getChildren().addAll(decompressBtn, clearBtn, backBtn);
        VBox layout = new VBox(25, titleLabel, fileArea, controlButtons, progressArea, statsArea);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setMaxWidth(700);
        decompressBtn.setOnAction(e -> performDecompression(stage, (ProgressBar) progressArea.getChildren().get(0), (Label) progressArea.getChildren().get(1), statsArea));
        clearBtn.setOnAction(e -> {
            clearSelection(fileArea);
            statsArea.setVisible(false);
        });
        backBtn.setOnAction(e -> stage.setScene(create(stage, backCallback)));
        Rectangle glassPane = createGlassPane(750, 700);
        StackPane root = new StackPane(backgroundPane, glassPane, layout);
        stage.setScene(new Scene(root, 900, 800));
    }

    private static VBox createActionCard(String title, String description, String color1, String color2, Runnable action) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefWidth(300);
        card.setPrefHeight(180);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 20; " + "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 20; -fx-cursor: hand;");
        card.setEffect(new DropShadow(15, Color.web("#000000", 0.3)));
        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        cardTitle.setTextFill(Color.web("#ffffff"));
        Label cardDesc = new Label(description);
        cardDesc.setFont(Font.font("Inter", FontWeight.NORMAL, 14));
        cardDesc.setTextFill(Color.web("#cbd5e0"));
        cardDesc.setWrapText(true);
        cardDesc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        card.getChildren().addAll(cardTitle, cardDesc);
        card.setOnMouseEntered(e -> {
            card.setStyle(String.format("-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 20; " + "-fx-border-color: %s; -fx-border-width: 2; -fx-border-radius: 20; -fx-cursor: hand;", color1));
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.05); scale.setToY(1.05); scale.play();
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 20; " + "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 20; -fx-cursor: hand;");
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0); scale.setToY(1.0); scale.play();
        });
        card.setOnMouseClicked(e -> action.run());
        return card;
    }

    private static VBox createFileSelectionArea(Stage stage, boolean isCompress) {
        VBox fileArea = new VBox(15);
        fileArea.setAlignment(Pos.CENTER);
        Label fileLabel = new Label("📁 Select Files");
        fileLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 18));
        fileLabel.setTextFill(Color.web("#ffffff"));
        ListView<String> fileList = new ListView<>();
        fileList.setPrefHeight(120);
        fileList.setStyle("-fx-control-inner-background: rgba(255,255,255,0.1); -fx-text-fill: #ffffff; " + "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox fileButtons = new HBox(15);
        fileButtons.setAlignment(Pos.CENTER);
        Button addFilesBtn = createStyledButton("+ Add Files", "#28a745", "#218838");
        Button removeBtn = createStyledButton("- Remove Selected", "#dc3545", "#c82333");
        fileButtons.getChildren().addAll(addFilesBtn, removeBtn);
        addFilesBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Files to " + (isCompress ? "Compress" : "Decompress"));
            if (!isCompress) {
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("LZW Compressed Files", "*.lzw"), new FileChooser.ExtensionFilter("All Files", "*.*"));
            } else {
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.log", "*.csv", "*.json", "*.xml"), new FileChooser.ExtensionFilter("All Files", "*.*"));
            }
            List<File> files = fc.showOpenMultipleDialog(stage);
            if (files != null) {
                if (!isCompress) {
                    List<File> validFiles = new ArrayList<>();
                    List<String> invalidFiles = new ArrayList<>();
                    for (File file : files) {
                        if (isValidLZWFile(file)) {
                            validFiles.add(file);
                        } else {
                            invalidFiles.add(file.getName());
                        }
                    }
                    selectedFiles.addAll(validFiles);
                    if (!invalidFiles.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Invalid Files Skipped", "The following files were skipped (not valid .lzw files):\n" + String.join("\n", invalidFiles));
                    }
                } else {
                    selectedFiles.addAll(files);
                }
                updateFileList(fileList);
            }
        });
        removeBtn.setOnAction(e -> {
            int selected = fileList.getSelectionModel().getSelectedIndex();
            if (selected >= 0) {
                selectedFiles.remove(selected);
                updateFileList(fileList);
            }
        });
        fileArea.getChildren().addAll(fileLabel, fileList, fileButtons);
        return fileArea;
    }

    private static VBox createProgressArea() {
        VBox progressArea = new VBox(10);
        progressArea.setAlignment(Pos.CENTER);
        progressArea.setVisible(false);
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);
        progressBar.setStyle("-fx-accent: #667eea;");
        Label progressLabel = new Label("Ready to start...");
        progressLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        progressLabel.setTextFill(Color.web("#cbd5e0"));
        progressArea.getChildren().addAll(progressBar, progressLabel);
        return progressArea;
    }

    private static VBox createStatsArea() {
        VBox statsArea = new VBox(15);
        statsArea.setAlignment(Pos.CENTER);
        statsArea.setVisible(false);
        statsArea.setPadding(new Insets(20));
        statsArea.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 15; " + "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 15;");
        Label statsTitle = new Label("📊 Processing Statistics");
        statsTitle.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        statsTitle.setTextFill(Color.web("#ffffff"));
        Label totalFilesLabel = new Label("Total Files: 0");
        totalFilesLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        totalFilesLabel.setTextFill(Color.web("#cbd5e0"));
        Label originalSizeLabel = new Label("Original Size: 0 bytes");
        originalSizeLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        originalSizeLabel.setTextFill(Color.web("#cbd5e0"));
        Label processedSizeLabel = new Label("Processed Size: 0 bytes");
        processedSizeLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        processedSizeLabel.setTextFill(Color.web("#cbd5e0"));
        Label compressionRatioLabel = new Label("Compression Ratio: 0%");
        compressionRatioLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        compressionRatioLabel.setTextFill(Color.web("#cbd5e0"));
        Label spaceSavedLabel = new Label("Space Saved: 0 bytes");
        spaceSavedLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        spaceSavedLabel.setTextFill(Color.web("#cbd5e0"));
        statsArea.getChildren().addAll(statsTitle, totalFilesLabel, originalSizeLabel, processedSizeLabel, compressionRatioLabel, spaceSavedLabel);
        return statsArea;
    }

    private static void performCompression(Stage stage, ProgressBar progressBar, Label progressLabel, VBox statsArea) {
        if (selectedFiles.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Input", "Please select files to compress.");
            return;
        }

        VBox progressAreaContainer = (VBox) progressBar.getParent();
        progressAreaContainer.setVisible(true);
        progressBar.setProgress(0);

        Task<Void> compressionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int totalFiles = selectedFiles.size();
                long totalOriginalSize = 0;
                long totalCompressedSize = 0;
                DecimalFormat df = new DecimalFormat("#.##");

                for (int i = 0; i < totalFiles; i++) {
                    final int fileIndex = i;
                    Platform.runLater(() -> {
                        double progress = (double) fileIndex / totalFiles;
                        progressBar.setProgress(progress);
                        progressLabel.setText(String.format("Processing file %d of %d...", fileIndex + 1, totalFiles));
                    });

                    File file = selectedFiles.get(i);
                    byte[] inputBytes = Files.readAllBytes(file.toPath());
                    long originalSize = inputBytes.length;
                    totalOriginalSize += originalSize;

                    // Create a new MyList for each file compression
                    sharedCodeList = new MyList();
                    byte[] compressedBytes = lzwInstance.compress(inputBytes, sharedCodeList);
                    totalCompressedSize += compressedBytes.length;

                    // Save compressed file
                    File outputFile = new File(file.getParent(), file.getName() + ".lzw");
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        fos.write(compressedBytes);
                    }

                    final long currentOriginalSize = totalOriginalSize;
                    final long currentCompressedSize = totalCompressedSize;
                    Platform.runLater(() -> updateCompressionStats(statsArea, fileIndex + 1, currentOriginalSize, currentCompressedSize, df));

                    Thread.sleep(300); // Visual delay
                }

                Platform.runLater(() -> {
                    progressBar.setProgress(1.0);
                    progressLabel.setText("Compression completed successfully!");
                    showAlert(Alert.AlertType.INFORMATION, "Success", "All files compressed successfully!");
                });
                return null;
            }
        };
        new Thread(compressionTask).start();
    }

    private static void performDecompression(Stage stage, ProgressBar progressBar, Label progressLabel, VBox statsArea) {
        if (selectedFiles.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Input", "Please select files to decompress.");
            return;
        }

        VBox progressAreaContainer = (VBox) progressBar.getParent();
        progressAreaContainer.setVisible(true);
        progressBar.setProgress(0);

        Task<Void> decompressionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int totalFiles = selectedFiles.size();
                long totalCompressedSize = 0;
                long totalDecompressedSize = 0;
                int successfulFiles = 0;
                DecimalFormat df = new DecimalFormat("#.##");

                for (int i = 0; i < totalFiles; i++) {
                    final int fileIndex = i;
                    Platform.runLater(() -> {
                        double progress = (double) fileIndex / totalFiles;
                        progressBar.setProgress(progress);
                        progressLabel.setText(String.format("Processing file %d of %d...", fileIndex + 1, totalFiles));
                    });

                    File file = selectedFiles.get(i);
                    if (!isValidLZWFile(file)) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.ERROR, "Invalid File",
                                    file.getName() + " is not a valid LZW compressed file (.lzw extension required).");
                        });
                        continue;
                    }

                    try {
                        byte[] compressedBytes = Files.readAllBytes(file.toPath());
                        long compressedSize = compressedBytes.length;
                        totalCompressedSize += compressedSize;

                        // Use the LZW algorithm's decompress method
                        byte[] decompressedBytes = lzwInstance.decompress(compressedBytes);

                        if (decompressedBytes == null) {
                            throw new Exception("Decompression returned null - invalid file format");
                        }

                        long decompressedSize = decompressedBytes.length;
                        totalDecompressedSize += decompressedSize;
                        successfulFiles++;

                        // Create output file name
                        String originalName = file.getName().replace(".lzw", "");
                        if (originalName.equals(file.getName())) {
                            originalName = file.getName() + "_decompressed";
                        }
                        File outputFile = new File(file.getParent(), "decompressed_" + originalName);
                        Files.write(outputFile.toPath(), decompressedBytes);

                        final long currentCompressedSize = totalCompressedSize;
                        final long currentDecompressedSize = totalDecompressedSize;
                        final int currentSuccessful = successfulFiles;
                        Platform.runLater(() -> updateDecompressionStats(statsArea, currentSuccessful,
                                currentCompressedSize, currentDecompressedSize, df));

                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.ERROR, "Decompression Error",
                                    "Failed to decompress " + file.getName() + "\n\nError: " + ex.getMessage());
                        });
                        continue;
                    }

                    Thread.sleep(300); // Visual delay
                }

                final int finalSuccessful = successfulFiles;
                final int finalTotal = totalFiles;
                Platform.runLater(() -> {
                    progressBar.setProgress(1.0);
                    if (finalSuccessful == finalTotal) {
                        progressLabel.setText("All files decompressed successfully!");
                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "All " + finalSuccessful + " files decompressed successfully!");
                    } else {
                        progressLabel.setText(String.format("Completed: %d/%d files successful", finalSuccessful, finalTotal));
                        showAlert(Alert.AlertType.WARNING, "Partial Success",
                                String.format("Decompressed %d out of %d files successfully.", finalSuccessful, finalTotal));
                    }
                });
                return null;
            }
        };
        new Thread(decompressionTask).start();
    }

    private static void updateCompressionStats(VBox statsArea, int filesProcessed, long originalSize, long compressedSize, DecimalFormat df) {
        statsArea.setVisible(true);
        Label totalFilesLabel = (Label) statsArea.getChildren().get(1);
        Label originalSizeLabel = (Label) statsArea.getChildren().get(2);
        Label processedSizeLabel = (Label) statsArea.getChildren().get(3);
        Label compressionRatioLabel = (Label) statsArea.getChildren().get(4);
        Label spaceSavedLabel = (Label) statsArea.getChildren().get(5);
        totalFilesLabel.setText("Total Files: " + filesProcessed);
        originalSizeLabel.setText("Original Size: " + formatFileSize(originalSize));
        processedSizeLabel.setText("Compressed Size: " + formatFileSize(compressedSize));
        double ratio = originalSize > 0 ? ((double) (originalSize - compressedSize) / originalSize) * 100 : 0;
        compressionRatioLabel.setText("Compression Ratio: " + df.format(ratio) + "%");
        long spaceSaved = originalSize - compressedSize;
        spaceSavedLabel.setText("Space Saved: " + formatFileSize(spaceSaved));
        if (ratio > 0) {
            compressionRatioLabel.setTextFill(Color.web("#48bb78"));
            spaceSavedLabel.setTextFill(Color.web("#48bb78"));
        } else {
            compressionRatioLabel.setTextFill(Color.web("#f56565"));
            spaceSavedLabel.setTextFill(Color.web("#f56565"));
        }
    }

    private static void updateDecompressionStats(VBox statsArea, int filesProcessed, long compressedSize, long decompressedSize, DecimalFormat df) {
        statsArea.setVisible(true);
        Label totalFilesLabel = (Label) statsArea.getChildren().get(1);
        Label originalSizeLabel = (Label) statsArea.getChildren().get(2);
        Label processedSizeLabel = (Label) statsArea.getChildren().get(3);
        Label compressionRatioLabel = (Label) statsArea.getChildren().get(4);
        Label spaceSavedLabel = (Label) statsArea.getChildren().get(5);
        totalFilesLabel.setText("Total Files: " + filesProcessed);
        originalSizeLabel.setText("Compressed Size: " + formatFileSize(compressedSize));
        processedSizeLabel.setText("Decompressed Size: " + formatFileSize(decompressedSize));
        double expansionRatio = compressedSize > 0 ? ((double) (decompressedSize - compressedSize) / compressedSize) * 100 : 0;
        compressionRatioLabel.setText("Expansion Ratio: " + df.format(expansionRatio) + "%");
        compressionRatioLabel.setTextFill(Color.web("#cbd5e0"));
        long dataRestored = decompressedSize - compressedSize;
        spaceSavedLabel.setText("Data Restored: " + formatFileSize(dataRestored));
        spaceSavedLabel.setTextFill(Color.web("#48bb78"));
    }

    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        else if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        else return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private static void updateFileList(ListView<String> fileList) {
        fileList.getItems().clear();
        for (File file : selectedFiles) {
            fileList.getItems().add(file.getName());
        }
    }

    private static void clearSelection(VBox fileArea) {
        selectedFiles.clear();
        ListView<String> fileList = (ListView<String>) fileArea.getChildren().get(1);
        updateFileList(fileList);
    }

    private static StackPane createAnimatedBackground() {
        StackPane bgPane = new StackPane();

        // Create gradient background (same as Welcome)
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#1a202c")),
                new Stop(0.3, Color.web("#2d3748")),
                new Stop(0.7, Color.web("#1a202c")),
                new Stop(1, Color.web("#0f1419"))
        };
        LinearGradient bg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        bgPane.setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));

        // Add floating circles for visual interest (same as Welcome)
        for (int i = 0; i < 8; i++) {
            Circle circle = new Circle();
            circle.setRadius(Math.random() * 60 + 20);
            circle.setFill(Color.web("#667eea", 0.05));
            circle.setTranslateX((Math.random() - 0.5) * 800);
            circle.setTranslateY((Math.random() - 0.5) * 800);

            // Floating animation
            Timeline floatAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(circle.translateYProperty(), circle.getTranslateY())),
                    new KeyFrame(Duration.seconds(4 + Math.random() * 4),
                            new KeyValue(circle.translateYProperty(), circle.getTranslateY() + (Math.random() - 0.5) * 100))
            );
            floatAnimation.setAutoReverse(true);
            floatAnimation.setCycleCount(Animation.INDEFINITE);
            floatAnimation.play();

            bgPane.getChildren().add(circle);
        }

        return bgPane;
    }

    private static Rectangle createGlassPane(double width, double height) {
        Rectangle glass = new Rectangle(width, height);
        glass.setFill(Color.web("#ffffff", 0.06));
        glass.setStroke(Color.web("#ffffff", 0.15));
        glass.setStrokeWidth(1);
        glass.setArcWidth(25);
        glass.setArcHeight(25);
        glass.setEffect(new GaussianBlur(10));
        return glass;
    }

    private static Button createStyledButton(String text, String color1, String color2) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 14));
        btn.setTextFill(Color.WHITE);
        btn.setPrefHeight(40);
        btn.setStyle(String.format("-fx-background-color: linear-gradient(to right, %s, %s); " + "-fx-background-radius: 20; -fx-cursor: hand;", color1, color2));
        btn.setOnMouseEntered(e -> btn.setStyle(String.format("-fx-background-color: linear-gradient(to right, %s, %s); " + "-fx-background-radius: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);", color2, color1)));
        btn.setOnMouseExited(e -> btn.setStyle(String.format("-fx-background-color: linear-gradient(to right, %s, %s); " + "-fx-background-radius: 20; -fx-cursor: hand;", color1, color2)));
        return btn;
    }

    private static void createEntranceAnimation(Label title, HBox cards, Button backBtn) {
        title.setOpacity(0);
        FadeTransition titleFade = new FadeTransition(Duration.seconds(0.8), title);
        titleFade.setFromValue(0); titleFade.setToValue(1); titleFade.play();
        cards.setOpacity(0);
        FadeTransition cardsFade = new FadeTransition(Duration.seconds(0.8), cards);
        cardsFade.setFromValue(0); cardsFade.setToValue(1);
        cardsFade.setDelay(Duration.seconds(0.3)); cardsFade.play();
        backBtn.setOpacity(0);
        FadeTransition backFade = new FadeTransition(Duration.seconds(0.6), backBtn);
        backFade.setFromValue(0); backFade.setToValue(1);
        backFade.setDelay(Duration.seconds(0.6));
        backFade.play();
    }

    private static boolean isValidLZWFile(File file) {
        if (!file.getName().toLowerCase().endsWith(".lzw")) {
            return false;
        }
        try {
            byte[] header = new byte[4]; // Read first 4 bytes for basic validation
            try (FileInputStream fis = new FileInputStream(file)) {
                int bytesRead = fis.read(header);
                return bytesRead > 0; // Basic check - file is readable and not empty
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2d3748; -fx-text-fill: white;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");

        alert.showAndWait();
    }
}