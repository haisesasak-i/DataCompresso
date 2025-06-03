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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeScene extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(900);
        primaryStage.setWidth(900);

        // Create animated background
        StackPane backgroundPane = createAnimatedBackground();

        // App Title with modern gradient text and enhanced glow
        Label titleLabel = new Label("Data Compresso");

        titleLabel.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 60));

        // Create gradient text effect
        LinearGradient titleGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#667eea")),
                new Stop(0.5, Color.web("#764ba2")),
                new Stop(1, Color.web("#f093fb"))
        );
        titleLabel.setTextFill(titleGradient);

        // Enhanced glow effect
        DropShadow titleGlow = new DropShadow(25, Color.web("#667eea"));
        titleGlow.setSpread(0.3);
        Bloom bloom = new Bloom(0.2);
        bloom.setInput(titleGlow);
        titleLabel.setEffect(bloom);

        // Subtle breathing animation for title
        Timeline titleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(titleLabel.scaleXProperty(), 1.0),
                        new KeyValue(titleLabel.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(titleLabel.scaleXProperty(), 1.02),
                        new KeyValue(titleLabel.scaleYProperty(), 1.02))
        );
        titleAnimation.setAutoReverse(true);
        titleAnimation.setCycleCount(Animation.INDEFINITE);
        titleAnimation.play();

        // Modern tagline with subtle animation
        Label taglineLabel = new Label("Intelligent compression algorithms at your fingertips");
        taglineLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 18));
        taglineLabel.setTextFill(Color.web("#e2e8f0"));
        taglineLabel.setEffect(new DropShadow(3, Color.web("#1a202c")));

        // Fade in animation for tagline
        FadeTransition taglineFade = new FadeTransition(Duration.seconds(2), taglineLabel);
        taglineFade.setFromValue(0);
        taglineFade.setToValue(1);
        taglineFade.setDelay(Duration.seconds(0.5));
        taglineFade.play();

        // Enhanced explanation with better typography
        Label explanationLabel = new Label(
                "Transform your data with cutting-edge Huffman and LZW algorithms.\n" +
                        "Optimize storage, reduce bandwidth, and boost performance effortlessly."
        );
        explanationLabel.setFont(Font.font("Inter", FontWeight.NORMAL, 16));
        explanationLabel.setTextFill(Color.web("#cbd5e0"));
        explanationLabel.setWrapText(true);
        explanationLabel.setMaxWidth(500);
        explanationLabel.setLineSpacing(4);
        explanationLabel.setStyle("-fx-text-alignment: center;");

        // Slide up animation for explanation
        TranslateTransition explanationSlide = new TranslateTransition(Duration.seconds(1.5), explanationLabel);
        explanationSlide.setFromY(30);
        explanationSlide.setToY(0);
        explanationSlide.setDelay(Duration.seconds(1));

        FadeTransition explanationFade = new FadeTransition(Duration.seconds(1.5), explanationLabel);
        explanationFade.setFromValue(0);
        explanationFade.setToValue(1);
        explanationFade.setDelay(Duration.seconds(1));

        ParallelTransition explanationAnimation = new ParallelTransition(explanationSlide, explanationFade);
        explanationAnimation.play();

        // Create modern glass-morphism buttons
        Button startButton = createModernButton("Get Started",
                "#667eea", "#764ba2", "#f093fb", true);
        Button infoButton = createModernButton("Learn More",
                "#4a5568", "#2d3748", "#667eea", false);

        // Add icon-like shapes to buttons (simple geometric indicators)
        addButtonIcon(startButton, "►");
        addButtonIcon(infoButton, "i");

        // Button container with staggered animation
        HBox buttons = new HBox(25, startButton, infoButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(35, 0, 0, 0));

        // Staggered button animations
        createButtonAnimation(startButton, 1.8);
        createButtonAnimation(infoButton, 2.1);

        // Content container with glass effect
        VBox contentBox = new VBox(22, titleLabel, taglineLabel, explanationLabel, buttons);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(50));
        contentBox.setMaxWidth(600);

        // Glass morphism effect for content
        Rectangle glassPane = new Rectangle(650, 500);
        glassPane.setFill(Color.web("#ffffff", 0.08));
        glassPane.setStroke(Color.web("#ffffff", 0.2));
        glassPane.setStrokeWidth(1);
        glassPane.setArcWidth(30);
        glassPane.setArcHeight(30);

        GaussianBlur glassBlur = new GaussianBlur(10);
        glassPane.setEffect(glassBlur);

        // Root layout with centered content
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundPane, glassPane, contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);
        StackPane.setAlignment(glassPane, Pos.CENTER);

        Scene welcomeScene = new Scene(root, 900, 900);
        primaryStage.setScene(welcomeScene);

        // Keep your original scene navigation logic
        infoButton.setOnAction(e -> {
            Scene infoScene = InfoScene.create(primaryStage, () -> {
                primaryStage.setScene(welcomeScene);
            });
            primaryStage.setScene(infoScene);
        });

        startButton.setOnAction(e -> {
            Scene algorithmSelectionScene = AlgorithmSelectionScene.create(
                    primaryStage,
                    () -> {  // Huffman selected
                        Scene huffmanScene = HuffmanScene.create(primaryStage, () -> {
                            Scene algorithmSelectionSceneAgain = AlgorithmSelectionScene.create(
                                    primaryStage,
                                    () -> primaryStage.setScene(HuffmanScene.create(primaryStage, () -> {})),
                                    () -> primaryStage.setScene(LZWScene.create(primaryStage, () -> {})),
                                    () -> primaryStage.setScene(welcomeScene)
                            );
                            primaryStage.setScene(algorithmSelectionSceneAgain);
                        });
                        primaryStage.setScene(huffmanScene);
                    },
                    () -> {  // LZW selected
                        Scene lzwScene = LZWScene.create(primaryStage, () -> {
                            Scene algorithmSelectionSceneAgain = AlgorithmSelectionScene.create(
                                    primaryStage,
                                    () -> primaryStage.setScene(HuffmanScene.create(primaryStage, () -> {})),
                                    () -> primaryStage.setScene(LZWScene.create(primaryStage, () -> {})),
                                    () -> primaryStage.setScene(welcomeScene)
                            );
                            primaryStage.setScene(algorithmSelectionSceneAgain);
                        });
                        primaryStage.setScene(lzwScene);
                    },
                    () -> {  // Back to Welcome
                        primaryStage.setScene(welcomeScene);
                    }
            );
            primaryStage.setScene(algorithmSelectionScene);
        });

        primaryStage.setTitle("Data Compresso - Professional Compression Suite");
        primaryStage.show();
    }

    private StackPane createAnimatedBackground() {
        StackPane bgPane = new StackPane();

        // Create gradient background
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#1a202c")),
                new Stop(0.3, Color.web("#2d3748")),
                new Stop(0.7, Color.web("#1a202c")),
                new Stop(1, Color.web("#0f1419"))
        };
        LinearGradient bg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        bgPane.setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));

        // Add floating circles for visual interest
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

    private Button createModernButton(String text, String color1, String color2, String hoverColor, boolean primary) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 16));
        btn.setTextFill(Color.WHITE);
        btn.setPrefWidth(primary ? 200 : 160);
        btn.setPrefHeight(50);

        // Initial style with gradient and glass effect
        String baseStyle = String.format(
                "-fx-background-radius: 25;" +
                        "-fx-border-radius: 25;" +
                        "-fx-border-color: rgba(255,255,255,0.2);" +
                        "-fx-border-width: 1;" +
                        "-fx-background-color: linear-gradient(to bottom right, %s, %s);" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);",
                color1, color2
        );
        btn.setStyle(baseStyle);

        // Enhanced hover effects
        btn.setOnMouseEntered(e -> {
            btn.setStyle(String.format(
                    "-fx-background-radius: 25;" +
                            "-fx-border-radius: 25;" +
                            "-fx-border-color: rgba(255,255,255,0.4);" +
                            "-fx-border-width: 1;" +
                            "-fx-background-color: linear-gradient(to bottom right, %s, %s);" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, %s99, 15, 0, 0, 3);",
                    hoverColor, color2, hoverColor
            ));

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        return btn;
    }

    private void addButtonIcon(Button btn, String icon) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        iconLabel.setTextFill(Color.web("#ffffff", 0.8));
        iconLabel.setTranslateX(-8);

        HBox content = new HBox(8, iconLabel, new Label(btn.getText()));
        content.setAlignment(Pos.CENTER);
        btn.setText("");
        btn.setGraphic(content);
    }

    private void createButtonAnimation(Button btn, double delay) {
        btn.setOpacity(0);
        btn.setTranslateY(20);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), btn);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.seconds(delay));

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), btn);
        slide.setFromY(20);
        slide.setToY(0);
        slide.setDelay(Duration.seconds(delay));

        ParallelTransition animation = new ParallelTransition(fade, slide);
        animation.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}