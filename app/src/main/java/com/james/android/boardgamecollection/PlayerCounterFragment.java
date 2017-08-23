package com.james.android.boardgamecollection;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 100599223 on 8/14/2017.
 */
/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerCounterFragment extends android.support.v4.app.Fragment{
    //argument keyfor the page number this fragment represents
    public static final String ARG_PAGE = "page";
    private int mPlayerCount = 0; //value thats added or subtracted from players current score tracker
    private int mPageNumber; //current page fragment beign viewed
    private int mTotalFragments;
    private String baseScore="0";
    private String playerScoreText;
    private static boolean tableexist;
    private static boolean tabletest = false;
    private static SQLiteDatabase database;
    private static PCDBHelper mDbHelper;
    private static Cursor mCursor;
    private static String count;

    //occurs before create so we can ensure we get the context for the database
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mDbHelper = new PCDBHelper(activity);
        database = mDbHelper.getReadableDatabase();
        count = "SELECT count (*) FROM " + PCContract.PCEntry.TABLE_NAME;


    }


    // fragments page number which is set to the argument value for argpage
    public static PlayerCounterFragment create(int pageNumber, int totalpages) {
        PlayerCounterFragment fragment = new PlayerCounterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putInt("totalpage", totalpages);
        fragment.setArguments(args);


        return fragment;
    }

    public PlayerCounterFragment() {
        //empty public constructor
    }



    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageNumber = getArguments().getInt(ARG_PAGE);
        mTotalFragments = getArguments().getInt("totalpage");

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

            for(int i= 0 ; i < mTotalFragments; i++){
                ContentValues cv = new ContentValues();
                String mUserName = "Player "+ String.valueOf(i+1);
                Log.i("creating database", "username is! " + mUserName);
                cv.put(PCContract.PCEntry.COLUMN_PC_NAME,(mUserName));
                cv.put(PCContract.PCEntry.COLUMN_PC_SCORE,0);
                cv.put(PCContract.PCEntry.COLUMN_PC_TOTALTIME,0);

                Log.i("end ofcreatingdatabase", "username in cv is " + cv.get(PCContract.PCEntry.COLUMN_PC_NAME));
                Uri uri = getActivity().getContentResolver().insert(PCContract.PCEntry.CONTENT_URI, cv);
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.player_counter, container, false);
        ImageView incrementone = (ImageView) rootView.findViewById(R.id.increment_one_button);
        ImageView decrementone = (ImageView) rootView.findViewById(R.id.decrement_one_button);
        final TextView playerScore = (TextView) rootView.findViewById(R.id.current_points);

        Log.i("beforetestprojection", "testing projection");
        String[] testprojection = {
                PCContract.PCEntry.COLUMN_PC_NAME,
                PCContract.PCEntry.COLUMN_PC_SCORE,
                PCContract.PCEntry.COLUMN_PC_TOTALTIME
        };

        Log.i("beforecursor", "querying entire database");

        Cursor testcursor = getActivity().getContentResolver().query(
                PCContract.PCEntry.CONTENT_URI,  //content uri of the words table
                testprojection,             //columns to return for each row
                null,                   //selection criteria
                null,                   //selection criteria
                null);
        try{
            Log.i("after cursor","there are " + testcursor.getCount() + " rows");
            //int idColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry._ID);
            //Log.i("after idcolumnindex", "idColumn index is" + idColumnIndex);
            int nameColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_NAME);
            Log.i("after namecolumnindex", "nameColumn index is " + nameColumnIndex);
            int scoreColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
            Log.i("after scorecolumnindex", "scoreColumn index is " + scoreColumnIndex);
            int timeColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_TOTALTIME);
            Log.i("after timecolumnindex", "timeColumn index is " + timeColumnIndex);


            while (testcursor.moveToNext()){
                //int currentID = testcursor.getInt(idColumnIndex);
                String currentName = testcursor.getString(nameColumnIndex);
                int currentScore = testcursor.getInt(scoreColumnIndex);
                float currentTime = testcursor.getFloat(timeColumnIndex);

                Log.i("values of database are", /*"current id"+ currentID+*/ "current name " + currentName
                + " current Score " + currentScore + " currentTime " + currentTime);


            }

        }finally{ testcursor.close();}




        TextView playerName = (TextView) rootView.findViewById(R.id.current_player);// Title at tope for who player is
        final String currentPlayer ="Player " + String.valueOf(mPageNumber +1); // finds player from fragment
        playerName.setText(currentPlayer);

        // Retrieve the values from database
        String[] projection = {
                PCContract.PCEntry.COLUMN_PC_NAME,
                PCContract.PCEntry.COLUMN_PC_SCORE,
                };

        String [] selectArg = {"Player "+String.valueOf(mPageNumber+1)};


        Cursor cursor = getActivity().getContentResolver().query(PCContract.PCEntry.CONTENT_URI, projection, PCContract.PCEntry.COLUMN_PC_NAME + "=?",selectArg,null);




        if( cursor != null && cursor.moveToFirst() ){ //check if cursor is null
        int scoreColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
        int columntext = cursor.getCount();
        cursor.moveToFirst();
        int currentScore = cursor.getInt(scoreColumnIndex);

        playerScore.setText(String.valueOf(currentScore));
            baseScore = playerScore.getText().toString().split(" ")[0];

        cursor.close();}
        TextView confirmButton = (TextView) rootView.findViewById(R.id.confirmation_score_button);
        TextView cancelButton = (TextView) rootView.findViewById(R.id.cancel_score_button);

        //Clicking confirm will bring the score into the database and set the textview.

//increase scorecounter by one
        incrementone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerCount+=1;
                baseScore = playerScore.getText().toString().split(" ")[0];
                if (mPlayerCount>0) {
                    playerScoreText = baseScore + " (+" + String.valueOf(mPlayerCount) + ")";
                } else{

                    playerScoreText = baseScore + " (" + String.valueOf(mPlayerCount) + ")";
                }
                playerScore.setText(String.valueOf(playerScoreText));
            }
        });

        //decrease scorecounter by one
        decrementone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayerCount-=1;
                baseScore = playerScore.getText().toString().split(" ")[0];
                if (mPlayerCount>0) {
                    playerScoreText = baseScore + " (+" + String.valueOf(mPlayerCount) + ")";
                } else{

                    playerScoreText = baseScore + " (" + String.valueOf(mPlayerCount) + ")";
                }
                playerScore.setText(String.valueOf(playerScoreText));
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float totaltimeplaceholder = 1;
                mDbHelper = new PCDBHelper(v.getContext());
                database = mDbHelper.getWritableDatabase();
                baseScore = playerScore.getText().toString().split(" ")[0];

                playerScoreText = String.valueOf(Integer.parseInt(baseScore) + mPlayerCount);
                baseScore = playerScoreText;

                playerScore.setText(playerScoreText);
                mPlayerCount = 0;

                //update database wiht new values for player
                ContentValues cv = new ContentValues();
                cv.put (PCContract.PCEntry.COLUMN_PC_NAME, "Player "+ String.valueOf(mPageNumber + 1));// insert player number into column based on current page
                cv.put (PCContract.PCEntry.COLUMN_PC_SCORE, Integer.valueOf(playerScoreText));
                cv.put (PCContract.PCEntry.COLUMN_PC_TOTALTIME,totaltimeplaceholder);

                Log.i("Confirmbutonclicked", "Current Name file is "+ cv.get(PCContract.PCEntry.COLUMN_PC_NAME));
                Log.i("confirmbuttonclicked", "about to update database");
                Uri currentPlayerUri = PCContract.PCEntry.CONTENT_URI;
                currentPlayerUri= Uri.withAppendedPath(PCContract.PCEntry.CONTENT_URI,(String.valueOf(mPageNumber+1)));
                Log.i("confirm button", "Currentplayeruri " + currentPlayerUri.toString());
                int rowsAffected = getActivity().getContentResolver().update(currentPlayerUri, cv, null, null);
                if (rowsAffected==0){
                    Toast.makeText(v.getContext(), "no row affected update failed", Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(v.getContext(), "update successful", Toast.LENGTH_SHORT).show();
                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mPlayerCount = 0;
                playerScore.setText(baseScore);

            }
        });
/*        final EditText timerText = (EditText) rootView.findViewById(R.id.timer_edittext);
        new CountDownTimer(30000,1000){
            public void onTick(long millisUntilFinished){
                timerText.setText("seconds remaining: " + millisUntilFinished/10000);
            }
            public void onFinish(){
                timerText.setText("Done");
            }
        }.start();*/
        return rootView;



    }
    public int getPageNumber() {
        return mPageNumber;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        database.close();
    }
}
