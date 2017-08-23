package com.james.android.boardgamecollection;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by 100599223 on 8/18/2017.
 */

public class PlayerCountOverview extends AppCompatActivity {

    private PlayerAdapter pcAdapter;
    ArrayList<Players> playerlist = new ArrayList<Players>();
    PCDBHelper mDbHelper;
    SQLiteDatabase database;
    private static String count;
    private boolean tableexist;
    Cursor mCursor;

    @Override
    protected void onResume() {
        super.onResume();
        count = "SELECT count (*) FROM " + PCContract.PCEntry.TABLE_NAME;
        playerlist.clear();
        pcAdapter.clear();
        //populate from database
        mCursor = database.rawQuery(count,null);
        Cursor cursor = database.rawQuery("SELECT * FROM " + PCContract.PCEntry.TABLE_NAME,null);
        int idColumnIndex = cursor.getColumnIndex(PCContract.PCEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_NAME);
        int scoreColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
        int timeColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_TOTALTIME);

        //iterate through and retrieve playerlist data from cursor
        mCursor.moveToFirst();
        try {
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentname = cursor.getString(nameColumnIndex);
                int currentscore = cursor.getInt(scoreColumnIndex);
                int currenttime = cursor.getInt(timeColumnIndex);

                playerlist.add(new Players(currentname,currentscore,currenttime));
            }
        }finally { cursor.close();}


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_overview);
        mDbHelper = new PCDBHelper(this);
        database = mDbHelper.getReadableDatabase();
        count = "SELECT count (*) FROM " + PCContract.PCEntry.TABLE_NAME;


        try {mCursor = database.rawQuery(count,null);
            mCursor.moveToFirst();
            int icount = mCursor.getInt(0);
            if(icount>0){
                tableexist=true;
            }
            mCursor.close();
        }// try to query table
        catch( Exception e){tableexist = false;}//if you cant query table b/c it doesnt exit create a new table

        if (!tableexist){
            database.execSQL("CREATE TABLE " + PCContract.PCEntry.TABLE_NAME + " ( " + PCContract.PCEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PCContract.PCEntry.COLUMN_PC_NAME + " TEXT NOT NULL,"
                    + PCContract.PCEntry.COLUMN_PC_SCORE + " INTEGER NOT NULL, "
                    + PCContract.PCEntry.COLUMN_PC_TOTALTIME + " REAL);");

            }


            pcAdapter = new PlayerAdapter(this,playerlist);


            ListView listView = (ListView) findViewById(R.id.listview_of_players);
        listView.setAdapter(pcAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Players currentPlayer = pcAdapter.getItem(position);
                Intent playerProfile = new Intent(PlayerCountOverview.this, PlayerCounter.class);
                playerProfile.putExtra("playerToLoad",pcAdapter.getPosition(currentPlayer));


                SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("totalplayers", String.valueOf(pcAdapter.getCount()));
                editor.apply();

                startActivity(playerProfile);



            }
        }


        );




        Button addPlayer = (Button) findViewById(R.id.add_new_player);
        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                database = mDbHelper.getWritableDatabase();
                cv.put(PCContract.PCEntry.COLUMN_PC_NAME,"player "+String.valueOf(pcAdapter.getCount()+1));
                cv.put(PCContract.PCEntry.COLUMN_PC_SCORE,0);
                cv.put(PCContract.PCEntry.COLUMN_PC_TOTALTIME, 0);
                Uri uri = getContentResolver().insert(PCContract.PCEntry.CONTENT_URI, cv);
                Log.i("new player added", cv.keySet() + cv.valueSet().toString());
                pcAdapter.notifyDataSetChanged();
                pcAdapter.add(new Players(cv.getAsString("name"),cv.getAsInteger("score"),cv.getAsFloat("totaltime")));

            }
        });


        }
    @Override
    public void onBackPressed(){

        super.onBackPressed();
        Intent backToMain = new Intent(PlayerCountOverview.this, MainActivity.class);
        startActivity(backToMain);


    }
    }

