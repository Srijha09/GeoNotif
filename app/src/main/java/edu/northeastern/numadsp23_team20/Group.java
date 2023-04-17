package edu.northeastern.numadsp23_team20;

import java.util.List;

public class Group {

    private String uuid;
    private String groupName;
    private List<String> groupParticipants;
    private Integer groupParticipantsNo;

    public Group() {
    }

    public Group(String groupName, Integer groupParticipantsNo) {
        this.groupName = groupName;
        this.groupParticipantsNo = groupParticipantsNo;
    }

    public Group(String groupName, List<String> groupParticipants) {
        this.groupName = groupName;
        this.groupParticipants = groupParticipants;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
    public Integer getGroupParticipantsNo(){
        if(groupParticipants!=null) {
            return groupParticipants.size();
        }
        return 1;
    }
}
