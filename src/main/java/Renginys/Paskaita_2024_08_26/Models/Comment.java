package Renginys.Paskaita_2024_08_26.Models;

import java.time.LocalDateTime;

public class Comment {

    private int id;
    private int eventID;
    private String comment;
    private String name;
    private LocalDateTime date;

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getEventID() {
        return eventID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
