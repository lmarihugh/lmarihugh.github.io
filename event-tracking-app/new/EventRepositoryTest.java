package com.example.cs360projectonelynneamarihugh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EventRepositoryTest {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private EventRepository repository;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new DatabaseHelper(context, null);
        db = dbHelper.getWritableDatabase();
        repository = new EventRepository(dbHelper);
        addUser("alice");
        addUser("bob");
    }

    @After
    public void tearDown() {
        dbHelper.close();
    }

    private void addUser(String username) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password_hash", "hash");
        values.put("salt", "salt");
        db.insert("users", null, values);
    }

    @Test
    public void insertEvent_thenReadBack_returnsSameValues() {
        long result = repository.insertEvent(
                new Event(-1, "Dentist", "10/17/2026", "3:30 PM", "Cleaning", "alice"));

        List<Event> events = repository.getEventsForUser("alice");

        assertTrue(result > 0);
        assertEquals(1, events.size());
        assertEquals("Dentist", events.get(0).getTitle());
        assertEquals("10/17/2026", events.get(0).getDate());
        assertEquals("3:30 PM", events.get(0).getTime());
        assertEquals("Cleaning", events.get(0).getDescription());
    }

    @Test
    public void updateEvent_changesStoredValues() {
        repository.insertEvent(
                new Event(-1, "Dentist", "10/17/2026", "3:30 PM", "Cleaning", "alice"));
        int id = repository.getEventsForUser("alice").get(0).getId();

        int rows = repository.updateEvent(
                new Event(id, "Dentist moved", "10/18/2026", "4:00 PM", "Rescheduled", "alice"));
        Event updated = repository.getEventsForUser("alice").get(0);

        assertEquals(1, rows);
        assertEquals("Dentist moved", updated.getTitle());
        assertEquals("10/18/2026", updated.getDate());
        assertEquals("4:00 PM", updated.getTime());
        assertEquals("Rescheduled", updated.getDescription());
    }

    @Test
    public void deleteEvent_removesOnlyThatEvent() {
        repository.insertEvent(new Event(-1, "One", "10/17/2026", "1:00 PM", "", "alice"));
        repository.insertEvent(new Event(-1, "Two", "10/18/2026", "2:00 PM", "", "alice"));
        int firstId = repository.getEventsForUser("alice").get(0).getId();

        repository.deleteEvent(firstId);
        List<Event> remaining = repository.getEventsForUser("alice");

        assertEquals(1, remaining.size());
        assertEquals("Two", remaining.get(0).getTitle());
    }

    @Test
    public void getEventsForUser_returnsOnlyThatUsersEvents() {
        repository.insertEvent(new Event(-1, "Alice 1", "10/17/2026", "1:00 PM", "", "alice"));
        repository.insertEvent(new Event(-1, "Bob 1", "10/17/2026", "2:00 PM", "", "bob"));
        repository.insertEvent(new Event(-1, "Bob 2", "10/18/2026", "3:00 PM", "", "bob"));

        assertEquals(1, repository.getEventsForUser("alice").size());
        assertEquals(2, repository.getEventsForUser("bob").size());
    }

    @Test
    public void deletingUser_cascadesToTheirEventsOnly() {
        repository.insertEvent(new Event(-1, "Alice 1", "10/17/2026", "1:00 PM", "", "alice"));
        repository.insertEvent(new Event(-1, "Alice 2", "10/18/2026", "2:00 PM", "", "alice"));
        repository.insertEvent(new Event(-1, "Bob 1", "10/17/2026", "3:00 PM", "", "bob"));

        db.delete("users", "username=?", new String[]{"alice"});

        assertEquals(0, repository.getEventsForUser("alice").size());
        assertEquals(1, repository.getEventsForUser("bob").size());
    }

    @Test
    public void insertEvent_forUnknownUser_isRejectedByForeignKey() {
        long result = repository.insertEvent(
                new Event(-1, "Ghost", "10/17/2026", "1:00 PM", "", "nobody"));

        assertEquals(-1, result);
        assertEquals(0, repository.getEventsForUser("nobody").size());
    }
}
