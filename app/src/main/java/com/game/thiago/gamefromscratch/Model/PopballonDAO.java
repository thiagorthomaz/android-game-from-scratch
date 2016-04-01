package com.game.thiago.gamefromscratch.Model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.game.thiago.gamefromscratch.R;

/**
 * Created by thiago on 31/03/16.
 */
public class PopballonDAO extends SpriteDAO {
    public PopballonDAO(Context context) {

        super(context);
        this.s = new Popballoon(this.getNextId(), 1, 1, BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.popballoon));
        this.addSprite(this.s);

    }

    @Override
    public Popballoon getModel() {
        return (Popballoon) this.s;
    }
}
