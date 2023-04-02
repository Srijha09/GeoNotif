package edu.northeastern.numadsp23_team20;

public class Groups implements ItemClickListener{
    String username;
    String imageURL;

    public Groups(){
    }
    public Groups(String username, String imageURL){
        this.username = username;
        this.imageURL = imageURL;
    }
    public String getUsername(){
        return username;
    }
    public String getImageURL(){
        return imageURL;
    }


    @Override
    public void onItemClick(int position) {}
}
