package com.example.cs360projectonelynneamarihugh;

public class Event {

    private final int id;
    private final String title;
    private final String date;
    private final String time;
    private final String description;
    private final String username;

    public Event(int id, String title, String date, String time, String description, String username) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }
}