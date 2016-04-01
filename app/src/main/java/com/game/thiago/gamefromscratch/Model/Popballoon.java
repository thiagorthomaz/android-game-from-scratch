package com.game.thiago.gamefromscratch.Model;

import android.graphics.Bitmap;

/**
 * Created by thiago on 31/03/16.
 */
public class Popballoon extends Sprite {

    public Popballoon(int id, int x, int y, Bitmap image) {
        super(id, x, y, image);
        this.setVisible(false);
    }
}
