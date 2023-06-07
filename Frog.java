package com.example.projekt;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * The Frog class represents the frog character in the Frogger game.
 */
public class Frog extends ImageView {
    Frog(double FROG_START_X, double FROG_START_Y, double FROG_SIZE, Pane root){
        setImage(new Image("file:frog.png"));
        setFitWidth(FROG_SIZE);
        setFitHeight(FROG_SIZE);
        setX(FROG_START_X);
        setY(FROG_START_Y);
        setRotate(180);
        root.getChildren().add(this);
    }
}
