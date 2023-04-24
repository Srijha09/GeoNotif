package edu.northeastern.numadsp23_team20;

import android.view.View;

public class Chat implements ItemClickListener{
    String username;

    public Chat(){
    }
    public Chat(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }


    @Override
    public void onItemClick(View v, int position) {}
}
