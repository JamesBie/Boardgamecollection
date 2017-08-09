package com.james.android.boardgamecollection;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.entries;
import static android.R.attr.name;
import static android.R.attr.x;

/**
 * Created by 100599223 on 8/7/2017.
 */

public class BgCollectionActivity extends AppCompatActivity implements AsyncResponse {

    private BoardGameAdapter bAdapter;


public boolean onCreateOptionsMenu (Menu menu){
    //inflate the menu option from the res/menu and
    //add this menu item to the otp of the app bar

    getMenuInflater().inflate(R.menu.boardgame_menu, menu);
    return true;
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "update board game" menu option
            case R.id.action_update_boardgame_collection:
                DownloadXmlTask task = new DownloadXmlTask();
                task.delegate = this;
                task.execute();

                Log.i("task", task.toString());
                return true;
            case R.id.action_randompick_boardgame:
                // TODO: 8/9/2017
                Random randompicker = new Random();
                if (bAdapter != null) {
                    int randomNum = (int) (randompicker.nextDouble() * (bAdapter.getCount() + 2));
                    Toast.makeText(this, "board game picked is " + bAdapter.getItem(randomNum).getTitle(), Toast.LENGTH_SHORT).show();
                }
                return true;
            
            
            // Respond to a click on the "Delete board game" menu option
            case R.id.action_delete_boardgame_collection:
               bAdapter.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);



// create a list of boardgames

    ArrayList<BoardGame> boardgames = new ArrayList<BoardGame>();


        //create a wordadapter whos data source is the list of BoardGames.
        //the adapter knows how to create list items for each item int he list

        bAdapter = new BoardGameAdapter(this, boardgames);

        //Find the listview object in the view hierarchy of the activity
        //there hsould be a list view with the view id list in the world_list.xml file

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(bAdapter);

    }
    @Override
    public void processFinish (String output){
        //Here you receive the results fired from asynctask of on post execute method
        try {
            List<BoardGame> parsedoutput = ParseXML.parse(output);
            bAdapter.addAll(parsedoutput);
            Log.i("process Finish", "Parsed!!");
        } catch (Exception e){
            Log.e("process Finish", "exception trying to parse xml", e);
        }

    }
}
