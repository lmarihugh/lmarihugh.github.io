package com.example.cs360projectonelynneamarihugh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    private final SQLiteDatabase db;

    public EventRepository(Context context) {
        this(new DatabaseHelper(context));
    }

    public EventRepository(DatabaseHelper dbHelper) {
        db = dbHelper.getWritableDatabase();
    }

    public List<Event> getEventsForUser(String username) {
        List<Event> events = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT id, title, date, time, description FROM events WHERE username=?",
                new String[]{username});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String date = cursor.getString(2);
            String time = cursor.getString(3);
            String description = cursor.getString(4);
            events.add(new Event(id, title, date, time, description, username));
        }
        cursor.close();

        return events;
    }

    public long insertEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("date", event.getDate());
        values.put("time", event.getTime());
        values.put("description", event.getDescription());
        values.put("username", event.getUsername());
        return db.insert("events", null, values);
    }

    public int updateEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("date", event.getDate());
        values.put("time", event.getTime());
        values.put("description", event.getDescription());
        return db.update("events", values, "id=?",
                new String[]{String.valueOf(event.getId())});
    }

    public void deleteEvent(int eventId) {
        db.delete("events", "id=?", new String[]{String.valueOf(eventId)});
    }
}