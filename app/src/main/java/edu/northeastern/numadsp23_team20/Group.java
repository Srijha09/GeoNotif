package edu.northeastern.numadsp23_team20;

import java.util.List;

public class Group {

    private String groupName;
    private List<String> groupParticipants;

    public Group(String groupName, List<String> groupParticipants) {
        this.groupName = groupName;
        this.groupParticipants = groupParticipants;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getGroupParticipants() {
        return groupParticipants;
    }

    public void setGroupParticipants(List<String> groupParticipants) {
        this.groupParticipants = groupParticipants;
    }
}
