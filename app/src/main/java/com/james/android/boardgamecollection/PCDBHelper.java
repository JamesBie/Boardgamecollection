package com.james.android.boardgamecollection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 100599223 on 8/14/2017.
 * Player Counter Database helper
 */

public class PCDBHelper extends SQLiteOpenHelper {



    private static final String DATABASE_NAME = "playerscore.db";
    /*
    database version. If you change the database schema you must incremend the version

     */
    private static final int DATABASE_VERSION = 1;

    public PCDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PC_TABLE = "CREATE TABLE " + PCContract.PCEntry.TABLE_NAME + " ( " + PCContract.PCEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PCContract.PCEntry.COLUMN_PC_NAME + " TEXT NOT NULL,"
                + PCContract.PCEntry.COLUMN_PC_SCORE + " INTEGER NOT NULL, "
                + PCContract.PCEntry.COLUMN_PC_TOTALTIME + " REAL);";
        Log.i("database name", SQL_CREATE_PC_TABLE);

        //Execute the SQL statement
        db.execSQL(SQL_CREATE_PC_TABLE);
    }
    /*
        this is called when the database needs to be upgraded
    */
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){

        //database is still at version 1 so nothing to be done here
        // TODO: 8/10/2017
    }
}
