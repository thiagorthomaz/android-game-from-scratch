package com.game.thiago.gamefromscratch.Model;

import android.graphics.Bitmap;

/**
 * Created by thiago on 28/03/16.
 */
public class Sprite extends Persistivel{

    int x;
    int y;
    public int directionX = 1;
    public int directionY = 1;
    private int speed = 10;
    private boolean visible = true;
    private Bitmap image;

    public Sprite(int id, int x, int y) {
        this.setId(id);
        this.x = x;
        this.y = y;
    }

    public Sprite(int id, int x, int y, Bitmap image) {
        this(id, x, y);
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
        return this.image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getHeight() {
        return this.image.getHeight();
    }

    public int getWidth() {
        return this.image.getWidth();
    }

}
