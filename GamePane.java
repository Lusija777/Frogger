package com.example.projekt;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.scene.media.MediaPlayer;

/**
 * The `GamePane` class represents the game pane for the Frogger game.
 * It extends the `Pane` class and contains the game elements and logic.
 */
public class GamePane extends Pane {
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 700;

    private static final double FROG_SIZE = 40;
    private static final double FROG_START_X = WINDOW_WIDTH / 2 - FROG_SIZE / 2;
    private static final double FROG_START_Y = WINDOW_HEIGHT - FROG_SIZE-50;

    private static final double LANE_HEIGHT = 60;
    private static final double LANE_OFFSET = 350;
    private static final double CAR_HEIGHT = 40;
    private static final double CAR_SPEED = 1.5;
    private static final int NUM_CARS_PER_LANE = 3;

    private static final double WATER_OFFSET = 130;
    private static final int NUM_LOGS_PER_LANE = 3;
    private static final int NUM_WATER_LINES = 3;
    private ImageView winImage;
    private ImageView lostImage;
    private ImageView tryAgainImage;
    private ImageView deathImage;
    private ImageView exitImage;
    private ImageView restartImage;
    private Frog frog;
    private Rectangle[] lanes = new Rectangle[4];
    private List<ImageView> cars;
    private Rectangle water;
    private List<ImageView> logs;
    private boolean gameRunning;
    private AnimationTimer gameLoop;

    private Rectangle timerBar;
    private double totalTime;
    private double startTime;

    private List<ImageView> waterLilies;
    private List<ImageView> frogs;
    private List<Integer> frogsOnLilies = new ArrayList<>(Collections.nCopies(5, 0));
    private List<ImageView> frogsOnLiliesImage = new ArrayList<>();

    private final Random rn = new Random();
    private Stage primaryStage;
    private Scene mainMenuScene;

    private Text scoreText;
    private Text highScoreText;
    private int score = 0;
    private int highScore = 0;
    private List<Integer> newSteps = new ArrayList<>(Collections.nCopies(1, 0));
    private int stepForward = 0;

    private List<MediaPlayer> sounds; // {appear, click, crash, splash,  time_done, you_win, game_over}
    /**
     * Constructs a `GamePane` object with the specified primaryStage and mainMenuScene.
     *
     * @param primaryStage The primary stage of the JavaFX application.
     * @param mainMenuScene The main menu scene to navigate back when needed.
     * @param sounds        The list of sound players for various game events.
     */
    public GamePane(Stage primaryStage, Scene mainMenuScene, List<MediaPlayer> sounds) {
        this.mainMenuScene = mainMenuScene;
        this.primaryStage = primaryStage;
        this.sounds = sounds;
        setStyle("-fx-background-color: mediumseagreen;");
    }

    /**
     * Starts the Frogger game by initializing game elements, setting up event handlers for frog,
     * and starting the game loop.
     */
    public void start() {
        inicializeWinImage();
        inicializeLostImage();
        inicializeTryAgainImage();
        inicializeDeathImage();

        initializeLanes();
        initializeCars();

        initializeWater();
        initializeLogs();


        initializeWaterLilies();
        initializeFrogs();
        frog = new Frog(FROG_START_X, FROG_START_Y, FROG_SIZE, this);

        initializeExit();
        initializeRestart();
        initializeTimer();

        initializeScore();
        loadHighScore();
        initializeHighScore();

        // Event handler for pressed key
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP && frog.getY() > 0) {
                frog.setY(frog.getY() - FROG_SIZE - 20);
                stepForward++;
                if (!newSteps.contains(stepForward)){
                    newSteps.add(stepForward);
                    updateScore(10);
                }
            }
            else if (event.getCode() == KeyCode.DOWN && frog.getY() < FROG_START_Y) {
                frog.setY(frog.getY() + FROG_SIZE +20);
                stepForward--;
            }
            else if (event.getCode() == KeyCode.LEFT && frog.getX() - FROG_SIZE > 0) {
                frog.setX(frog.getX() - FROG_SIZE);
            }
            else if (event.getCode() == KeyCode.RIGHT && frog.getX() + 2*FROG_SIZE < WINDOW_WIDTH) {
                frog.setX(frog.getX() + FROG_SIZE);
            }
        });

        // Starts animation
        gameLoop = new AnimationTimer() {
            /**
             * The handle method is invoked on each frame of the game loop.
             *
             * @param now The timestamp of the current frame in nanoseconds.
             */
            @Override
            public void handle(long now) {
                moveCars();
                moveLogs();
                checkCollision();
                updateTimer();

            }
        };

        gameRunning = true;
        requestFocus();
        gameLoop.start();
    }

    private void initializeHighScore() {
        highScoreText = new Text("Highscore: " + highScore);
        highScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        highScoreText.setFill(Color.DARKGREEN);
        getChildren().add(highScoreText);
        highScoreText.setLayoutX(25);
        highScoreText.setLayoutY(50);
    }

    private void initializeScore() {
        scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreText.setFill(Color.DARKGREEN);
        getChildren().add(scoreText);
        scoreText.setLayoutX(25);
        scoreText.setLayoutY(25);
    }

    private void initializeExit() {
        exitImage = new ImageView(new Image("file:x.png"));
        exitImage.setFitWidth(LANE_HEIGHT);
        exitImage.setFitHeight(LANE_HEIGHT-5);
        exitImage.setLayoutX((WINDOW_WIDTH - exitImage.getFitWidth()) - 20);
        exitImage.setLayoutY(10);
        exitImage.setOnMouseEntered(event -> setCursor(Cursor.HAND));
        exitImage.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
        exitImage.setOnMouseClicked(event -> {
            primaryStage.setScene(mainMenuScene);
            //sounds.get(1).play();
        });
        getChildren().add(exitImage);
    }
    private void initializeRestart() {
        restartImage = new ImageView(new Image("file:repeat.png"));
        restartImage.setFitWidth(LANE_HEIGHT);
        restartImage.setFitHeight(LANE_HEIGHT-5);
        restartImage.setLayoutX((WINDOW_WIDTH - restartImage.getFitWidth()) - LANE_HEIGHT -40);
        restartImage.setLayoutY(10);
        restartImage.setOnMouseEntered(event -> setCursor(Cursor.HAND));
        restartImage.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
        restartImage.setOnMouseClicked(event -> {
            toInitState();
            //sounds.get(1).play();
        });
        getChildren().add(restartImage);
    }

    private void initializeFrogs() {
        //baby frogs
        frogs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ImageView frog = new ImageView(new Image("file:frog.png"));
            frog.setFitWidth(FROG_SIZE/2);
            frog.setFitHeight(FROG_SIZE/2);
            frog.setX(i * (FROG_SIZE/2) + ((i+1) * 5));
            frog.setY(FROG_START_Y);
            frog.setRotate(180);
            frogs.add(frog);
            getChildren().add(frog);
        }
    }
    private void initializeWaterLilies() {
        waterLilies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ImageView waterLily = new ImageView(new Image("file:waterlily.png"));
            waterLily.setFitWidth(50);
            waterLily.setFitHeight(50);
            waterLily.setX(i * (WINDOW_WIDTH / 5)+50);
            waterLily.setY(WATER_OFFSET-60);
            waterLilies.add(waterLily);
            getChildren().add(waterLily);
        }
    }
    private void initializeLanes() {
        for (int i = 0; i < lanes.length; i++) {
            Rectangle lane = new Rectangle(WINDOW_WIDTH, LANE_HEIGHT);
            lane.setFill(Color.GRAY);
            lane.setStroke(Color.WHITE);
            lane.setY(i * LANE_HEIGHT + LANE_OFFSET);
            lanes[i] = lane;
            getChildren().add(lane);
        }
    }
    private void initializeCars() {
        cars = new ArrayList<>();
        for (int i = 0; i < lanes.length; i++) {
            for (int j = 0; j < NUM_CARS_PER_LANE; j++) {
                ImageView car = new ImageView(new Image("file:car" + (rn.nextInt(5)+1) + ".png"));
                car.setFitHeight(CAR_HEIGHT);
                car.setFitWidth(rn.nextInt(50) + 80);
                car.setX(j * (WINDOW_WIDTH / NUM_CARS_PER_LANE) + ((WINDOW_WIDTH / NUM_CARS_PER_LANE) - car.getFitWidth()) / 2);
                car.setY(i * LANE_HEIGHT + (LANE_HEIGHT - CAR_HEIGHT) / 2 + LANE_OFFSET);
                if (i %2 == 0){
                    car.setRotate(180);
                }
                cars.add(car);
                getChildren().add(car);
            }
        }
    }
    private void initializeWater() {
        water = new Rectangle(WINDOW_WIDTH, LANE_HEIGHT * 4, Color.ROYALBLUE);
        water.setY(WATER_OFFSET- LANE_HEIGHT);
        getChildren().add(water);
    }

    private void initializeLogs() {
        logs = new ArrayList<>();
        for (int i = 0; i < NUM_WATER_LINES; i++) {
            for (int j = 0; j < NUM_LOGS_PER_LANE; j++) {
                double spaceBetweenLogs = (rn.nextDouble(200) + WINDOW_WIDTH) / NUM_LOGS_PER_LANE;
                ImageView log = new ImageView(new Image("file:wood1.png"));
                log.setFitWidth(rn.nextInt(70) + 100);
                log.setFitHeight(CAR_HEIGHT);
                log.setX(j * spaceBetweenLogs + (spaceBetweenLogs - CAR_HEIGHT * 2) / 2);
                log.setY(LANE_HEIGHT * i + (LANE_HEIGHT - CAR_HEIGHT) / 2 + WATER_OFFSET);
                logs.add(log);
                getChildren().add(log);
            }
        }
    }
    private void initializeTimer() {
        totalTime = 60.0; // Total time in seconds
        startTime = System.nanoTime();
        timerBar = new Rectangle(WINDOW_WIDTH, 10, Color.GREEN);
        timerBar.setY(WINDOW_HEIGHT-10);
        getChildren().add(timerBar);
    }

    private void updateTimer() {
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000;
        double remainingTime = Math.max(totalTime - elapsedTime, 0);
        double barWidth = (remainingTime / totalTime) * WINDOW_WIDTH;
        timerBar.setWidth(barWidth);

        if (remainingTime <= 0) {
            gameOver();
            //sounds.get(4).play();
        }
    }
    private void inicializeWinImage(){
        winImage = new ImageView(new Image("file:you win2.png"));
        winImage.setFitWidth(400);
        winImage.setFitHeight(150);
        winImage.setLayoutX((WINDOW_WIDTH - winImage.getFitWidth()) / 2);
        winImage.setLayoutY((WINDOW_HEIGHT - winImage.getFitHeight()) / 2);
    }
    private void inicializeLostImage(){
        lostImage = new ImageView(new Image("file:game over.png"));
        lostImage.setFitWidth(400);
        lostImage.setFitHeight(150);
        lostImage.setLayoutX((WINDOW_WIDTH - lostImage.getFitWidth()) / 2);
        lostImage.setLayoutY((WINDOW_HEIGHT - lostImage.getFitHeight()) / 2);
    }
    private void inicializeTryAgainImage(){
        tryAgainImage = new ImageView(new Image("file:try again.png"));
        tryAgainImage.setFitWidth(200);
        tryAgainImage.setFitHeight(100);
        tryAgainImage.setLayoutX((WINDOW_WIDTH - tryAgainImage.getFitWidth()) / 2);
        tryAgainImage.setLayoutY((WINDOW_HEIGHT - tryAgainImage.getFitHeight()) / 2 + 150);
        tryAgainImage.setOnMouseEntered(event -> setCursor(Cursor.HAND));
        tryAgainImage.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
        tryAgainImage.setOnMouseClicked(event -> {
            //sounds.get(1).play();
            if (getChildren().contains(deathImage)){
                getChildren().add(frog);
            }
            getChildren().removeAll(tryAgainImage, winImage, lostImage, deathImage);
            toInitState();
            gameLoop.start();
            gameRunning = true;
            restartImage.setDisable(false);
            exitImage.setDisable(false);
        });
    }
    private void inicializeDeathImage(){
        deathImage = new ImageView(new Image("file:death.png"));
        deathImage.setFitWidth(FROG_SIZE);
        deathImage.setFitHeight(FROG_SIZE);
    }

    private void moveCars() {
        for (ImageView car : cars) {
            // Move cars to the left in even lanes, to the right in odd lanes
            double newX = car.getX() + ((cars.indexOf(car) / NUM_CARS_PER_LANE) % 2 == 0 ? -CAR_SPEED : CAR_SPEED);
            if (newX < -130) {
                newX = WINDOW_WIDTH;
            }
            if (newX > WINDOW_WIDTH) {
                newX = -130;
            }
            car.setX(newX);
        }
    }
    private void moveLogs() {
        for (ImageView log : logs) {
            // Move logs to the left in even lanes, to the right in odd lanes
            double newX = log.getX() + ((logs.indexOf(log) / NUM_LOGS_PER_LANE) % 2 == 0 ? -CAR_SPEED / 2 : CAR_SPEED / 2);

            if (newX < -170) {
                newX = WINDOW_WIDTH;
            } else if (newX > WINDOW_WIDTH) {
                newX = -170;
            }
            if (frog.getBoundsInParent().intersects(log.getBoundsInParent())) {
                // Frog is riding on a log
                if (frog.getX() < 0 || frog.getX() > WINDOW_WIDTH - FROG_SIZE){
                    gameOver();
                }
                frog.setX(frog.getX() + ((logs.indexOf(log) / NUM_LOGS_PER_LANE) % 2 == 0 ? -CAR_SPEED / 2 : CAR_SPEED / 2));
            }
            log.setX(newX);
        }
    }
    private boolean checkOnLog() {
        for (ImageView log : logs) {
            if (frog.getBoundsInParent().intersects(log.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }
    private int checkOnLily() {
        for (ImageView lily : waterLilies) {
            if (frog.getBoundsInParent().intersects(lily.getBoundsInParent())) {
                frog.setY(lily.getY());
                return waterLilies.indexOf(lily);
            }
        }
        return -1;
    }
    private boolean checkWinCondition() {
        if (!frogsOnLilies.contains(0)) {
            // Frogs are on all lilies
            gameLoop.stop();
            gameRunning = false;
            System.out.println("win!");
            updateScore(1000);
            showWinMessage();
            //sounds.get(5).play();
            return true;
        }
        int n = checkOnLily();
        if(n != -1){
            // Frog is on lily
            if (frogsOnLilies.get(n).equals(1)){
                // Only one frog on one lily
                toInitState();
                return true;
            }
            // Count points for frog on lily and for remaining time
            int points = 50;
            double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000;
            int remainingTime = (int) Math.max(totalTime - elapsedTime, 0);
            int unusedTime = remainingTime * 2;
            points += unusedTime * 10;
            updateScore(points);

            frogsOnLilies.set(n, 1);
            if (!frogs.isEmpty()){
                // Remove one baby frog
                getChildren().remove(frogs.remove(0));
            }
            ImageView lilyFrog = new ImageView();
            lilyFrog.setImage(frog.getImage());
            lilyFrog.setFitWidth(frog.getFitWidth());
            lilyFrog.setFitHeight(frog.getFitHeight());
            lilyFrog.setY(waterLilies.get(n).getY()+5);
            lilyFrog.setX(waterLilies.get(n).getX()+5);
            frogsOnLiliesImage.add(lilyFrog);
            getChildren().add(lilyFrog);
            restartGame();
            return true;
        }
        return false;
    }

    private void restartGame(){
        frog.setX(FROG_START_X);
        frog.setY(FROG_START_Y);
        startTime = System.nanoTime();
        stepForward = 0;
        newSteps.clear();
    }
    private void toInitState(){
        // Set game to init state
        score = 0;
        updateScore(-score);
        getChildren().remove(highScoreText);
        initializeHighScore();
        Collections.fill(frogsOnLilies, 0);
        getChildren().removeAll(frogs);
        getChildren().removeAll(frogsOnLiliesImage);
        frogsOnLiliesImage = new ArrayList<>();
        initializeFrogs();
        restartGame();
    }

    private void checkCollision() {
        if(checkWinCondition()){
            return;
        }
        Rectangle2D frogBounds = new Rectangle2D(frog.getX(), frog.getY(), FROG_SIZE, FROG_SIZE);
        Rectangle2D waterBounds = new Rectangle2D(water.getX(), water.getY(), WINDOW_WIDTH, LANE_HEIGHT * 4);
        if (frogBounds.intersects(waterBounds)) {
            // Collision with water, check if frog is on log
            if (!checkOnLog()) {
                gameOver();
                //sounds.get(3).play();
                return;
            }
        }

        for (ImageView car : cars) {
            // Collision with car
            Rectangle2D carBounds = new Rectangle2D(car.getX(), car.getY(), car.getFitWidth(), CAR_HEIGHT);
            if (frogBounds.intersects(carBounds)) {
                gameOver();
                //sounds.get(2).play();
                return;
            }
        }
    }

    private void gameOver() {
        gameRunning = false;
        restartImage.setDisable(true);
        exitImage.setDisable(true);
        gameLoop.stop();
        System.out.println("Game Over!");

        // Death image instead of frog image
        getChildren().remove(frog);
        deathImage.setX(frog.getX());
        deathImage.setY(frog.getY());
        getChildren().add(deathImage);

        saveHighScore();
        showLostMessage();
        //sounds.get(6).play();
    }
    private void updateScore(int points) {
        score += points;
        scoreText.setText("Score: " + score);

        if (score > highScore) {
            highScore = score;
            highScoreText.setText("Highscore: " + highScore);
        }
    }
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException ignored) {
        }
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException ignored) {
        }
    }
    private void showWinMessage() {
        getChildren().add(winImage);
        getChildren().add(tryAgainImage);
    }
    private void showLostMessage() {
        getChildren().add(lostImage);
        getChildren().add(tryAgainImage);
    }
}
