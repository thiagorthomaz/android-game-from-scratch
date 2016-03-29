package com.game.thiago.gamefromscratch;

import android.graphics.Bitmap;

/**
 * Created by thiago on 28/03/16.
 */
public class Sprite {

    int x;
    int y;
    int directionX = 1;
    int directionY = 1;
    private int speed = 10;
    private boolean visible = false;
    private Bitmap image;
    private int height;
    private int width;

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Sprite(int x, int y, Bitmap image) {
        this(x, y);
        this.image = image;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDirectionX() {
        return directionX;
    }

    public void setDirectionX(int directionX) {
        this.directionX = directionX;
    }

    public int getDirectionY() {
        return directionY;
    }

    public void setDirectionY(int directionY) {
        this.directionY = directionY;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}