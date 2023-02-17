package edu.northeastern.numadsp23_team20;

public class Movie {
    private String title;
    private String releaseYear;

    public Movie(String title, String releaseDate) {
        this.title = title;
        this.releaseYear = releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getReleaseYear() {
        return releaseYear;
    }
}
