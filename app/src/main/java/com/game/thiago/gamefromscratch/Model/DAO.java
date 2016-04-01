package com.game.thiago.gamefromscratch.Model;

import java.util.ArrayList;

/**
 * Created by thiago on 31/03/16.
 */
abstract public class DAO {
    private static ArrayList<Persistivel> persistivelArrayList = new ArrayList<Persistivel>();

    protected ArrayList<Persistivel> getPersistivelArrayList() {
        return DAO.persistivelArrayList;
    }

    public int getNextId(){
        return DAO.persistivelArrayList.size() + 1;
    }

}
