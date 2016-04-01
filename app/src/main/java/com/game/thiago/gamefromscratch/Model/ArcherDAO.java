package com.game.thiago.gamefromscratch.Model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.game.thiago.gamefromscratch.R;

/**
 * Created by thiago on 31/03/16.
 */
public class ArcherDAO extends SpriteDAO {

    public ArcherDAO(Context context) {
        super(context);
        this.s = new Archer(this.getNextId(), 1, 60, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.archer));
        this.addSprite(this.s);
    }

    @Override
    public Archer getModel() {
        return (Archer) this.s;
    }
}
