package com.game.thiago.gamefromscratch.Model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.game.thiago.gamefromscratch.R;

/**
 * Created by thiago on 31/03/16.
 */
public class ArrowDAO extends SpriteDAO {

    Arrow arrow;

    public ArrowDAO(Context context) {

        super(context);
        //this.s = new Arrow(this.getNextId(), 1, 1, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.arrow));
        //this.addSprite(this.s);

    }

    @Override
    public Arrow getModel() {
        return (Arrow) this.s;
    }

    public Arrow createArrow(Archer archer){
        Arrow a = new Arrow(this.getNextId(), archer.getX(), archer.getY(), BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.arrow));
        this.addSprite(a);
        return a;
    }

}
