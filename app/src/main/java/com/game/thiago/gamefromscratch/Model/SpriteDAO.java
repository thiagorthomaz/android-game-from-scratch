package com.game.thiago.gamefromscratch.Model;

import android.content.Context;
import android.content.res.Resources;

import com.game.thiago.gamefromscratch.Exceptions.SpriteException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by thiago on 31/03/16.
 */
abstract public class SpriteDAO extends DAO {

    private Context context;
    protected Sprite s;

    public SpriteDAO(Context context) {
        this.context = context;
    }


    public Context getContext() {
        return context;
    }

    abstract public Sprite getModel();

    public Sprite getModel(Sprite s) throws SpriteException {
        return this.findSprite(s.getId());
    }

    public Sprite findSprite(int id) throws SpriteException {

        for (Persistivel s : this.getPersistivelArrayList()) {
            if (s.getId() == id){
                return (Sprite)s;
            }
        }
        throw new SpriteException("Sprite not found!");

    }

    public Sprite updateSprite(int id, Sprite sprite) throws SpriteException{

        int i = this.getPersistivelArrayList().indexOf(sprite);
        this.getPersistivelArrayList().set(i, sprite);
        try{
            return this.findSprite(sprite.getId());
        }catch (SpriteException se) {
            throw new SpriteException("Sprite not found, update failed!");
        }

    }

    public Sprite updateSprite(Sprite sprite) throws SpriteException{
        return this.updateSprite(sprite.getId(), sprite);
    }

    public void addSprite(Sprite sprite){
        this.getPersistivelArrayList().add(sprite);
    }

    public boolean removeSprite(Sprite s) throws SpriteException{

        Iterator<Persistivel> sprite_iterator = this.getPersistivelArrayList().iterator();

        while(sprite_iterator.hasNext()){
            Persistivel sp = sprite_iterator.next();
            if (sp.getId() == s.getId()){
                sprite_iterator.remove();
            }
        }

        throw new SpriteException("Sprite not found, remove failed!");

    }

    public ArrayList<Sprite> getSprites(){
        ArrayList<Sprite> sprites =  (ArrayList<Sprite>) ((ArrayList<?>) this.getPersistivelArrayList());
        return sprites;
    }

}
