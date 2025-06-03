package com.example.datacompresso.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AlgorithmSelectionScene {

    public static Scene create(Stage primaryStage, Runnable onSelectHuffman, Runnable onSelectLZW, Runnable onBack) {
        // Create animated background matching welcome scene
        StackPane backgroundPane = createAnimatedBackground();

        // Enhanced title with gradient text and improved glow
        Label titleLabel = new Label("Choose Your Algorithm");
        titleLabel.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 48));

        LinearGradient titleGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#667eea")),
                new Stop(0.5, Color.web("#764ba2")),
                new Stop(1, Color.web("#f093fb"))
        );
        titleLabel.setTextFill(titleGradient);

        DropShadow titleGlow = new DropShadow(20, Color.web("#667eea"));
        titleGlow.setSpread(0.3);
        Bloom bloom = new Bloom(0.15);
        bloom.setInput(titleGlow);
        titleLabel.setEffect(bloom);

        // Subtitle with fade animation
        Label subtitleLabel = new Label("Select the compression method that suits your needs");
        subtitleLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 16));
        subtitleLabel.setTextFill(Color.web("#cbd5e0"));
        subtitleLabel.setOpacity(0);

        // Create algorithm cards instead of simple buttons
        VBox huffmanCard = createAlgorithmCard(
                "Huffman Compression",
                "Perfect for text files and documents",
                "• Variable-length encoding\n• Optimal for known frequencies\n• Lossless compression",
                "#667eea", "#764ba2", "#f093fb",
                onSelectHuffman
        );

        VBox lzwCard = createAlgorithmCard(
                "LZW Compression",
                "Ideal for images and binary data",
                "• Dictionary-based encoding\n• Adaptive algorithm\n• Excellent for repetitive data",
                "#48bb78", "#38a169", "#68d391",
                onSelectLZW
        );

        // Cards container with responsive layout
        HBox cardsContainer = new HBox(40, huffmanCard, lzwCard);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(30, 0, 30, 0));

        // Back button with modern design
        Button backBtn = createModernBackButton("← Back to Welcome", onBack);

        // Main content container
        VBox contentBox = new VBox(25, titleLabel, subtitleLabel, cardsContainer, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(50));
        contentBox.setMaxWidth(900);

        // Glass morphism background for content
        Rectangle glassPane = new Rectangle(950, 650);
        glassPane.setFill(Color.web("#ffffff", 0.06));
        glassPane.setStroke(Color.web("#ffffff", 0.15));
        glassPane.setStrokeWidth(1);
        glassPane.setArcWidth(35);
        glassPane.setArcHeight(35);

        GaussianBlur glassBlur = new GaussianBlur(12);
        glassPane.setEffect(glassBlur);

        // Root layout
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundPane, glassPane, contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);
        StackPane.setAlignment(glassPane, Pos.CENTER);

        // Entrance animations
        createEntranceAnimations(titleLabel, subtitleLabel, huffmanCard, lzwCard, backBtn);

        return new Scene(root, 900, 900);
    }

    private static StackPane createAnimatedBackground() {
        StackPane bgPane = new StackPane();

        // Gradient background matching welcome scene
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#1a202c")),
                new Stop(0.3, Color.web("#2d3748")),
                new Stop(0.7, Color.web("#1a202c")),
                new Stop(1, Color.web("#0f1419"))
        };
        LinearGradient bg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        bgPane.setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));

        // Floating particles for visual interest
        for (int i = 0; i < 6; i++) {
            Circle particle = new Circle();
            particle.setRadius(Math.random() * 40 + 15);
            particle.setFill(Color.web("#667eea", 0.04));
            particle.setTranslateX((Math.random() - 0.5) * 700);
            particle.setTranslateY((Math.random() - 0.5) * 700);

            Timeline floatAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(particle.translateXProperty(), particle.getTranslateX()),
                            new KeyValue(particle.translateYProperty(), particle.getTranslateY())),
                    new KeyFrame(Duration.seconds(8 + Math.random() * 4),
                            new KeyValue(particle.translateXProperty(), particle.getTranslateX() + (Math.random() - 0.5) * 200),
                            new KeyValue(particle.translateYProperty(), particle.getTranslateY() + (Math.random() - 0.5) * 200))
            );
            floatAnimation.setAutoReverse(true);
            floatAnimation.setCycleCount(Animation.INDEFINITE);
            floatAnimation.play();

            bgPane.getChildren().add(particle);
        }

        return bgPane;
    }

    private static VBox createAlgorithmCard(String title, String subtitle, String features,
                                            String color1, String color2, String hoverColor, Runnable onSelect) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(35, 25, 35, 25));
        card.setPrefWidth(320);
        card.setPrefHeight(280);
        card.setMaxWidth(320);

        // Card styling with glass effect
        card.setStyle(String.format(
                "-fx-background-color: rgba(255, 255, 255, 0.08);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;" +
                        "-fx-cursor: hand;"
        ));

        card.setEffect(new DropShadow(15, Color.web("#000000", 0.3)));

        // Card title
        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("Inter", FontWeight.BOLD, 22));
        cardTitle.setTextFill(Color.web("#ffffff"));
        cardTitle.setWrapText(true);

        // Card subtitle
        Label cardSubtitle = new Label(subtitle);
        cardSubtitle.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        cardSubtitle.setTextFill(Color.web("#a0aec0"));
        cardSubtitle.setWrapText(true);

        // Features list
        Label featuresLabel = new Label(features);
        featuresLabel.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        featuresLabel.setTextFill(Color.web("#e2e8f0"));
        featuresLabel.setWrapText(true);
        featuresLabel.setLineSpacing(3);

        // Select button
        Button selectBtn = new Button("Select Algorithm");
        selectBtn.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 14));
        selectBtn.setTextFill(Color.WHITE);
        selectBtn.setPrefWidth(200);
        selectBtn.setPrefHeight(35);

        selectBtn.setStyle(String.format(
                "-fx-background-color: linear-gradient(to right, %s, %s);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-cursor: hand;",
                color1, color2
        ));

        selectBtn.setOnAction(e -> onSelect.run());

        card.getChildren().addAll(cardTitle, cardSubtitle, featuresLabel, selectBtn);

        // Enhanced hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle(String.format(
                    "-fx-background-color: rgba(255, 255, 255, 0.12);" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: %s;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 20;" +
                            "-fx-cursor: hand;",
                    color1
            ));

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();

            DropShadow glow = new DropShadow(20, Color.web(color1, 0.4));
            card.setEffect(glow);
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.08);" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 20;" +
                            "-fx-cursor: hand;"
            );

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            card.setEffect(new DropShadow(15, Color.web("#000000", 0.3)));
        });

        card.setOnMouseClicked(e -> onSelect.run());

        return card;
    }

    private static Button createModernBackButton(String text, Runnable onBack) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Inter", FontWeight.MEDIUM, 16));
        btn.setTextFill(Color.web("#cbd5e0"));
        btn.setPrefWidth(200);
        btn.setPrefHeight(45);

        btn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 22;" +
                        "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                            "-fx-background-radius: 22;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 22;" +
                            "-fx-cursor: hand;"
            );
            btn.setTextFill(Color.web("#ffffff"));
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                            "-fx-background-radius: 22;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 22;" +
                            "-fx-cursor: hand;"
            );
            btn.setTextFill(Color.web("#cbd5e0"));
        });

        btn.setOnAction(e -> onBack.run());
        return btn;
    }

    private static void createEntranceAnimations(Label title, Label subtitle, VBox card1, VBox card2, Button backBtn) {
        // Title animation
        title.setOpacity(0);
        title.setTranslateY(-30);

        FadeTransition titleFade = new FadeTransition(Duration.seconds(0.8), title);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);

        TranslateTransition titleSlide = new TranslateTransition(Duration.seconds(0.8), title);
        titleSlide.setFromY(-30);
        titleSlide.setToY(0);

        ParallelTransition titleAnimation = new ParallelTransition(titleFade, titleSlide);
        titleAnimation.setDelay(Duration.seconds(0.2));
        titleAnimation.play();

        // Subtitle animation
        FadeTransition subtitleFade = new FadeTransition(Duration.seconds(0.8), subtitle);
        subtitleFade.setFromValue(0);
        subtitleFade.setToValue(1);
        subtitleFade.setDelay(Duration.seconds(0.5));
        subtitleFade.play();

        // Cards staggered animation
        animateCard(card1, 0.8);
        animateCard(card2, 1.0);

        // Back button animation
        backBtn.setOpacity(0);
        FadeTransition backFade = new FadeTransition(Duration.seconds(0.6), backBtn);
        backFade.setFromValue(0);
        backFade.setToValue(1);
        backFade.setDelay(Duration.seconds(1.4));
        backFade.play();
    }

    private static void animateCard(VBox card, double delay) {
        card.setOpacity(0);
        card.setTranslateY(50);
        card.setScaleX(0.9);
        card.setScaleY(0.9);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.8), card);
        slide.setFromY(50);
        slide.setToY(0);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.8), card);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition animation = new ParallelTransition(fade, slide, scale);
        animation.setDelay(Duration.seconds(delay));
        animation.play();
    }

    public static Button createGradientButton(String compress, String hashtag, String hashtag1, String hashtag2) {
        return new Button("lol");
    }
}