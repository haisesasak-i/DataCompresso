package com.example.datacompresso.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InfoScene {

    public static Scene create(Stage primaryStage, Runnable onBack) {
        // Create animated background matching welcome scene
        StackPane backgroundPane = createAnimatedBackground();

        // Title with modern gradient and glow
        Label titleLabel = new Label("About Data Compression");
        titleLabel.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 42));

        LinearGradient titleGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#667eea")),
                new Stop(0.5, Color.web("#764ba2")),
                new Stop(1, Color.web("#f093fb"))
        );
        titleLabel.setTextFill(titleGradient);

        DropShadow titleGlow = new DropShadow(20, Color.web("#667eea"));
        titleGlow.setSpread(0.2);
        titleLabel.setEffect(titleGlow);

        // Subtle title animation
        Timeline titlePulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(titleGlow.radiusProperty(), 20)),
                new KeyFrame(Duration.seconds(2), new KeyValue(titleGlow.radiusProperty(), 30))
        );
        titlePulse.setAutoReverse(true);
        titlePulse.setCycleCount(Animation.INDEFINITE);
        titlePulse.play();

        // Create styled content sections
        VBox contentContainer = new VBox(25);
        contentContainer.setAlignment(Pos.CENTER_LEFT);
        contentContainer.setMaxWidth(650);

        // Introduction section
        VBox introSection = createContentSection(
                "What is Data Compression?",
                "Data compression is the art of reducing file sizes to optimize storage space and accelerate data transfer. " +
                        "From streaming your favorite videos to storing thousands of photos on your device, compression algorithms " +
                        "work behind the scenes to make our digital world more efficient."
        );

        // Algorithms section
        VBox algorithmsSection = createContentSection(
                "Compression Algorithms",
                ""
        );

        // Create interactive algorithm cards
        HBox algorithmCards = new HBox(20);
        algorithmCards.setAlignment(Pos.CENTER);

        VBox huffmanCard = createAlgorithmCard(
                "Huffman Coding",
                "• Frequency-based encoding\n• Assigns shorter codes to common characters\n• Optimal for text compression\n• Variable-length encoding",
                "#667eea", "#764ba2"
        );

        VBox lzwCard = createAlgorithmCard(
                "LZW Algorithm",
                "• Dictionary-based compression\n• Builds patterns dynamically\n• Great for repetitive data\n• Used in GIF and TIFF formats",
                "#f093fb", "#f5576c"
        );

        algorithmCards.getChildren().addAll(huffmanCard, lzwCard);

        // Example section with visual representation
        VBox exampleSection = createExampleSection();

        // Benefits section
        VBox benefitsSection = createContentSection(
                "Why Use Data Compresso?",
                "Our application combines the power of both algorithms to provide optimal compression ratios. " +
                        "Whether you're dealing with text files, documents, or any data type, Data Compresso intelligently " +
                        "applies the most suitable compression technique for maximum efficiency."
        );

        contentContainer.getChildren().addAll(introSection, algorithmsSection, algorithmCards, exampleSection, benefitsSection);

        // Create scrollable content area
        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefHeight(500);
        scrollPane.setPadding(new Insets(20));

        // Modern back button
        Button backButton = createModernButton("← Back to Home");
        backButton.setOnAction(e -> onBack.run());

        // Main content with glass effect
        VBox mainContent = new VBox(30);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(titleLabel, scrollPane, backButton);
        mainContent.setPadding(new Insets(40, 50, 40, 50));
        mainContent.setMaxWidth(750);

        // Glass morphism container
        Rectangle glassPane = new Rectangle(800, 650);
        glassPane.setFill(Color.web("#ffffff", 0.08));
        glassPane.setStroke(Color.web("#ffffff", 0.15));
        glassPane.setStrokeWidth(1);
        glassPane.setArcWidth(25);
        glassPane.setArcHeight(25);

        GaussianBlur glassBlur = new GaussianBlur(8);
        glassPane.setEffect(glassBlur);

        // Root layout
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundPane, glassPane, mainContent);
        StackPane.setAlignment(mainContent, Pos.CENTER);
        StackPane.setAlignment(glassPane, Pos.CENTER);

        // Entry animation
        createEntryAnimation(mainContent);

        return new Scene(root, 900, 900);
    }

    private static StackPane createAnimatedBackground() {
        StackPane bgPane = new StackPane();

        // Gradient background
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#1a202c")),
                new Stop(0.3, Color.web("#2d3748")),
                new Stop(0.7, Color.web("#1a202c")),
                new Stop(1, Color.web("#0f1419"))
        };
        LinearGradient bg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        bgPane.setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));

        // Floating elements
        for (int i = 0; i < 6; i++) {
            Circle circle = new Circle();
            circle.setRadius(Math.random() * 40 + 15);
            circle.setFill(Color.web("#667eea", 0.04));
            circle.setTranslateX((Math.random() - 0.5) * 700);
            circle.setTranslateY((Math.random() - 0.5) * 700);

            Timeline floatAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(circle.translateYProperty(), circle.getTranslateY())),
                    new KeyFrame(Duration.seconds(3 + Math.random() * 3),
                            new KeyValue(circle.translateYProperty(), circle.getTranslateY() + (Math.random() - 0.5) * 80))
            );
            floatAnimation.setAutoReverse(true);
            floatAnimation.setCycleCount(Animation.INDEFINITE);
            floatAnimation.play();

            bgPane.getChildren().add(circle);
        }

        return bgPane;
    }

    private static VBox createContentSection(String title, String content) {
        VBox section = new VBox(12);

        if (!title.isEmpty()) {
            Label sectionTitle = new Label(title);
            sectionTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
            sectionTitle.setTextFill(Color.web("#e2e8f0"));
            sectionTitle.setEffect(new DropShadow(3, Color.web("#667eea")));
            section.getChildren().add(sectionTitle);
        }

        if (!content.isEmpty()) {
            Label sectionContent = new Label(content);
            sectionContent.setFont(Font.font("Inter", FontWeight.NORMAL, 16));
            sectionContent.setTextFill(Color.web("#cbd5e0"));
            sectionContent.setWrapText(true);
            sectionContent.setLineSpacing(3);
            section.getChildren().add(sectionContent);
        }

        return section;
    }

    private static VBox createAlgorithmCard(String title, String features, String color1, String color2) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(25, 20, 25, 20));
        card.setMaxWidth(280);
        card.setPrefHeight(200);

        // Card styling with gradient
        card.setStyle(String.format(
                "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: rgba(255,255,255,0.1);" +
                        "-fx-border-width: 1;" +
                        "-fx-background-color: linear-gradient(to bottom, %s22, %s11);",
                color1, color2
        ));

        // Card title
        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        cardTitle.setTextFill(Color.web("#f7fafc"));

        // Features list
        Label cardFeatures = new Label(features);
        cardFeatures.setFont(Font.font("Inter", FontWeight.NORMAL, 14));
        cardFeatures.setTextFill(Color.web("#e2e8f0"));
        cardFeatures.setWrapText(true);
        cardFeatures.setLineSpacing(2);

        card.getChildren().addAll(cardTitle, cardFeatures);

        // Hover effect
        card.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();

            card.setEffect(new DropShadow(15, Color.web(color1, 0.4)));
        });

        card.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            card.setEffect(null);
        });

        return card;
    }

    private static VBox createExampleSection() {
        VBox section = new VBox(15);

        Label exampleTitle = new Label("Compression Example");
        exampleTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        exampleTitle.setTextFill(Color.web("#e2e8f0"));
        exampleTitle.setEffect(new DropShadow(3, Color.web("#667eea")));

        // Example container with background
        VBox exampleContainer = new VBox(10);
        exampleContainer.setPadding(new Insets(20));
        exampleContainer.setStyle(
                "-fx-background-radius: 10;" +
                        "-fx-background-color: rgba(255,255,255,0.05);" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: rgba(255,255,255,0.1);" +
                        "-fx-border-width: 1;"
        );

        Label originalText = new Label("Original: \"aaaaabbbbcccdde\" (15 characters)");
        originalText.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        originalText.setTextFill(Color.web("#f093fb"));

        Label huffmanResult = new Label("Huffman: Assigns 'a'→1, 'b'→01, 'c'→001, etc.");
        huffmanResult.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        huffmanResult.setTextFill(Color.web("#667eea"));

        Label lzwResult = new Label("LZW: Creates dictionary entries for repeated patterns");
        lzwResult.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        lzwResult.setTextFill(Color.web("#764ba2"));

        exampleContainer.getChildren().addAll(originalText, huffmanResult, lzwResult);
        section.getChildren().addAll(exampleTitle, exampleContainer);

        return section;
    }

    private static Button createModernButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
        btn.setTextFill(Color.WHITE);
        btn.setPrefWidth(200);
        btn.setPrefHeight(45);

        String baseStyle =
                "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: rgba(255,255,255,0.2);" +
                        "-fx-border-width: 1;" +
                        "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);";
        btn.setStyle(baseStyle);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-radius: 22;" +
                            "-fx-border-radius: 22;" +
                            "-fx-border-color: rgba(255,255,255,0.4);" +
                            "-fx-border-width: 1;" +
                            "-fx-background-color: linear-gradient(to right, #f093fb, #f5576c);" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, #667eea99, 12, 0, 0, 3);"
            );

            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        return btn;
    }

    private static void createEntryAnimation(VBox content) {
        content.setOpacity(0);
        content.setTranslateX(-30);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), content);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), content);
        slide.setFromX(-30);
        slide.setToX(0);

        ParallelTransition entrance = new ParallelTransition(fade, slide);
        entrance.play();
    }
}