package edu.northeastern.numadsp23_team20;

public class FriendsData {


    private String userName;
    private String buttonDetails;

    //intialize the variables of our link object.
    public FriendsData(String userName, String buttonDetails) {
        this.userName = userName;
        this.buttonDetails = buttonDetails;
    }

    //when the unit is clicked, do something.
    /**
    public void onLinkUnitClicked(Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
        context.startActivity(browserIntent);
    }
     **/

    //getters
    public String getUserName() {
        return userName;
    }

    public String getButtonDetails() {
        return buttonDetails;
    }

    //setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setButtonDetails(String buttonDetails) {
        this.buttonDetails = buttonDetails;
    }
}
