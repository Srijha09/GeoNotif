package edu.northeastern.numadsp23_team20;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FriendsData implements Parcelable {

    //private String imageUrl;
    private String userName;
    private String buttonDetails;

    //intialize the variables of our link object.
    public FriendsData(String userName, String buttonDetails) {
        //this.imageUrl = imageURL;
        this.userName = userName;
        this.buttonDetails = buttonDetails;
    }

    protected FriendsData(Parcel in) {
        userName = in.readString();
        buttonDetails = in.readString();
    }

    public static final Creator<FriendsData> CREATOR = new Creator<FriendsData>() {
        @Override
        public FriendsData createFromParcel(Parcel in) {
            return new FriendsData(in);
        }

        @Override
        public FriendsData[] newArray(int size) {
            return new FriendsData[size];
        }
    };

    //getters
    public String getUserName() {
        return userName;
    }

    public String getButtonDetails() {
        return buttonDetails;
    }

    //setters
    public void setButtonDetails(String buttonDetails) {
        this.buttonDetails = buttonDetails;
    }

    /*
    public String getImageUrl() {
        return imageUrl;
    }
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(buttonDetails);
    }
}
