package com.example.projekt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class for the Frogger game application.
 */
public class FroggerGame extends Application {
    private Stage primaryStage;
    private Scene mainMenuScene;
    private Scene gameScene;
    private GamePane gamePane;
    private List<MediaPlayer> sounds; // {appear, click, crash, splash,  time_done, you_win, game_over}

    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 700;

    /**
     * The main entry for the Frogger game application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The entry point for the Frogger game application.
     *
     * @param primaryStage The primary stage of the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //inicializeSounds();
        createMainMenuScene();
        primaryStage.setTitle("Frogger");
        primaryStage.setScene(mainMenuScene);
        primaryStage.setResizable(false); // Set resizable to false
        primaryStage.show();
    }
    private void inicializeSounds() {
        sounds = new ArrayList<>(); // {appear, click, crash, splash,  time_done, you_win, game_over}

        for( String res: new String[]{"appear.mp3", "click.mp3", "crash.mp3", "splash.mp3",
                "time_done.mp3", "you_win.mp3", "game_over.mp3"}){
            Media media = new Media(getClass().getResource(res).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            sounds.add(mediaPlayer);
        }
    }

    private void createMainMenuScene() {
        Pane mainMenuPane = new Pane();
        mainMenuPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainMenuPane.setStyle("-fx-background-color: mediumseagreen;");
        initializePlay(mainMenuPane);
        initializeName(mainMenuPane);
        initializeExit(mainMenuPane);
        initializeSetting(mainMenuPane);

        mainMenuScene = new Scene(mainMenuPane, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
    private void initializeName(Pane mainMenuPane) {
        ImageView nameImage = new ImageView(new Image("file:frogger.png"));
        nameImage.setFitWidth(600);
        nameImage.setFitHeight(200);
        nameImage.setLayoutX((WINDOW_WIDTH - nameImage.getFitWidth()) / 2);
        nameImage.setLayoutY(nameImage.getFitHeight() / 2);
        mainMenuPane.getChildren().add(nameImage);
    }
    private void initializePlay(Pane mainMenuPane) {
        ImageView startImage = new ImageView(new Image("file:play.png"));
        startImage.setFitWidth(200);
        startImage.setFitHeight(100);
        startImage.setLayoutX((WINDOW_WIDTH - startImage.getFitWidth()) / 2);
        startImage.setLayoutY((WINDOW_HEIGHT - startImage.getFitHeight()) / 2);
        startImage.setOnMouseEntered(event -> mainMenuPane.setCursor(Cursor.HAND));
        startImage.setOnMouseExited(event -> mainMenuPane.setCursor(Cursor.DEFAULT));
        startImage.setOnMouseClicked(event -> {
            startGame();
            //sounds.get(1).play();
        });
        mainMenuPane.getChildren().add(startImage);
    }
    private void initializeSetting(Pane mainMenuPane) {
        ImageView settingsImage = new ImageView(new Image("file:settings.png"));
        settingsImage.setFitWidth(200);
        settingsImage.setFitHeight(100);
        settingsImage.setLayoutX((WINDOW_WIDTH - settingsImage.getFitWidth()) /2);
        settingsImage.setLayoutY((WINDOW_HEIGHT - settingsImage.getFitHeight()) / 2 + 125);
        settingsImage.setOnMouseEntered(event -> mainMenuPane.setCursor(Cursor.HAND));
        settingsImage.setOnMouseExited(event -> mainMenuPane.setCursor(Cursor.DEFAULT));
        settingsImage.setOnMouseClicked(event -> {
            showLevels();
            //sounds.get(1).play();
        });
        mainMenuPane.getChildren().add(settingsImage);
    }
    private void initializeExit(Pane mainMenuPane) {
        ImageView exitImage = new ImageView(new Image("file:exit.png"));
        exitImage.setFitWidth(200);
        exitImage.setFitHeight(100);
        exitImage.setLayoutX((WINDOW_WIDTH - exitImage.getFitWidth()) / 2);
        exitImage.setLayoutY((WINDOW_HEIGHT - exitImage.getFitHeight()) / 2 + 250);
        exitImage.setOnMouseEntered(event -> mainMenuPane.setCursor(Cursor.HAND));
        exitImage.setOnMouseExited(event -> mainMenuPane.setCursor(Cursor.DEFAULT));
        exitImage.setOnMouseClicked(event -> {
            Platform.exit();
            //sounds.get(1).play();
        });
        mainMenuPane.getChildren().add(exitImage);
    }

    private void showLevels() {
        Pane levelPane = new Pane();
        levelPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        levelPane.setStyle("-fx-background-color: mediumseagreen;");

        // Display basic level text
        Text levelText = new Text("Basic Level");
        levelText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        levelText.setFill(Color.BLACK);
        levelText.setLayoutX(100);
        levelText.setLayoutY(100);

        // Display checked checkbox
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(true);
        checkBox.setLayoutX(levelText.getLayoutX() + levelText.getLayoutBounds().getWidth() + 15);
        checkBox.setLayoutY(levelText.getLayoutY()- 15);

        // Create ImageView to return to the main menu
        ImageView returnImage = new ImageView(new Image("file:return.png"));
        returnImage.setFitWidth(120);
        returnImage.setFitHeight(80);
        returnImage.setLayoutX(WINDOW_WIDTH/2 - 200);
        returnImage.setLayoutY(WINDOW_HEIGHT- 100);
        returnImage.setOnMouseEntered(event -> levelPane.setCursor(Cursor.HAND));
        returnImage.setOnMouseExited(event -> levelPane.setCursor(Cursor.DEFAULT));
        returnImage.setOnMouseClicked(event -> {
            primaryStage.setScene(mainMenuScene);
            //sounds.get(1).play();
        });

        // Create ImageView to apply and then return to the main menu
        ImageView applyImage = new ImageView(new Image("file:apply.png"));
        applyImage.setFitWidth(120);
        applyImage.setFitHeight(80);
        applyImage.setLayoutX(WINDOW_WIDTH/2 + 50);
        applyImage.setLayoutY(WINDOW_HEIGHT- 100);
        applyImage.setOnMouseEntered(event -> levelPane.setCursor(Cursor.HAND));
        applyImage.setOnMouseExited(event -> levelPane.setCursor(Cursor.DEFAULT));
        applyImage.setOnMouseClicked(event -> {
            primaryStage.setScene(mainMenuScene);
            //sounds.get(1).play();
        });

        levelPane.getChildren().addAll(levelText, checkBox, returnImage, applyImage);
        Scene levelScene = new Scene(levelPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(levelScene);
    }

    private void startGame() {
        // Switch to new Scene, to game
        gamePane = new GamePane(primaryStage, mainMenuScene, sounds);
        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        gamePane.requestFocus();
        primaryStage.setScene(gameScene);
        //sounds.get(0).play();
        gamePane.start();
    }
}



