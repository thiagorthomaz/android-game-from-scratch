package com.game.thiago.gamefromscratch.Model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.game.thiago.gamefromscratch.R;

/**
 * Created by thiago on 31/03/16.
 */
public class BlueballoonDAO extends SpriteDAO {

    public BlueballoonDAO(Context context) {
        super(context);
        this.s = new Blueballoon(this.getNextId(), 900, 10, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.blueballoon));
        this.addSprite(this.s);
        this.addSprite(new Blueballoon(this.getNextId(), 1000, 10, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.blueballoon)));
        this.addSprite(new Blueballoon(this.getNextId(), 1000, 200, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.blueballoon)));
        this.addSprite(new Blueballoon(this.getNextId(), 1000, 300, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.blueballoon)));
        this.addSprite(new Blueballoon(this.getNextId(), 1000, 400, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.blueballoon)));

    }

    @Override
    public Sprite getModel() {
        return null;
    }
}
