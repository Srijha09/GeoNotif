package edu.northeastern.numadsp23_team20;

public class Groups implements ItemClickListener{
    String groupname;
    String imageURL;

    public Groups(){
    }
    public Groups(String groupname, String imageURL){
        this.groupname = groupname;
        this.imageURL = imageURL;
    }


    public String getUsername(){
        return groupname;
    }
    public String getImageURL(){
        return imageURL;
    }


    @Override
    public void onItemClick(int position) {}
}
