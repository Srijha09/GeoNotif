package edu.northeastern.numadsp23_team20;

public class Task {
    public Task(String taskName, String description, LocationItem location) {
        this.taskName = taskName;
        this.description = description;
        this.location = location;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationItem getLocation() {
        return location;
    }

    public void setLocation(LocationItem location) {
        this.location = location;
    }

    private String taskName;
    private String description;
    private LocationItem location;
}
