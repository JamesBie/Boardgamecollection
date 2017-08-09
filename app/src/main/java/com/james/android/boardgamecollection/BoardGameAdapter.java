package com.james.android.boardgamecollection;

import android.app.Activity;
import android.util.Log;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 100599223 on 8/7/2017.
 */

public class BoardGameAdapter extends ArrayAdapter<BoardGame> {

    public BoardGameAdapter(Context context, ArrayList<BoardGame> boardGames){
        super(context, 0 , boardGames);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        //checks if the exisitng view is being reused. if not inflate it

        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        //get the current object
        BoardGame currentBoardGame = getItem(position);

        //find the TextView in the list_item.xml layout with the id mboardgame text view
        TextView boardgameTitle = (TextView) listItemView.findViewById(R.id.bgTitleTextView);
        boardgameTitle.setText(currentBoardGame.getTitle());

        TextView boardgameMin = (TextView) listItemView.findViewById(R.id.minPlayerTextView);
        boardgameMin.setText(String.valueOf(currentBoardGame.getMinPlayer()));

        TextView boardgameMax = (TextView) listItemView.findViewById(R.id.maxPlayerTextView);
        boardgameMax.setText(String.valueOf(currentBoardGame.getmMaxPlayer()));

        //return the whole list item layout containing the 3 textview so it can be shown in the list view
        return listItemView;

    }
}
