package edu.northeastern.numadsp23_team20;

import android.view.View;

public class Groups implements ItemClickListener{
    String groupname;
    Integer participants_no;

    public Groups(){
    }
    public Groups(String groupname, Integer participants_no){
        this.groupname = groupname;
        this.participants_no = participants_no;
    }


    public String getGroupname(){
        return groupname;
    }
    public Integer getParticipants_no(){
        return participants_no;
    }


    @Override
    public void onItemClick(View v, int position) {}
}
