package edu.northeastern.numadsp23_team20;

public class Message {
    private int timestamp;

    public int getUserId() {
        return userId;
    }

    public String getStickerName() {
        return stickerName;
    }

    private int userId;
    private String stickerName;

    public Message(int userId, String stickerName) {
        this.userId = userId;
        this.stickerName = stickerName;
    }
}
