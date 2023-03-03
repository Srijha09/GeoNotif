package edu.northeastern.numadsp23_team20;

public class Message {
    public String timestamp;
    public String userId;
    public String stickerName;

    public Message() {

    }

    public Message(String userId, String stickerName) {
        this.userId = userId;
        this.stickerName = stickerName;
    }

    public Message(String userId, String stickerName, String time) {
        this.userId = userId;
        this.stickerName = stickerName;
        this.timestamp = time;
    }

    public String getUserId() {
        return userId;
    }

    public String getStickerName() {
        return stickerName;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
