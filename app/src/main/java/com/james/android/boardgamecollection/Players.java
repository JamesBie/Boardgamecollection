package com.james.android.boardgamecollection;

import android.util.Log;

/**
 * Created by 100599223 on 8/18/2017.
 */

public class Players {
    private String mName;
    private int mScore;
    private float mTime;

    public Players (String name, int score, float time){
        mName = name;
        mScore = score;
        mTime = time;

        Log.i("Players", "Name " + mName+ " Score: "+ mScore + " Time: "+ mTime );



    }
    public String getPlayerName() {return mName;}
    public int getScore() {return mScore;}
    public float getTime() {return mTime;}
}
