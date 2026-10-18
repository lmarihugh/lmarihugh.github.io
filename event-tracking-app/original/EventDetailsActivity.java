package com.example.cs360projectonelynneamarihugh;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    EditText titleInput, dateInput, timeInput, descInput;
    Button saveButton, backButton;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    String currentUser;
    int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        currentUser = getIntent().getStringExtra("username");

        titleInput = findViewById(R.id.eventTitleInput);
        dateInput = findViewById(R.id.eventDateInput);
        timeInput = findViewById(R.id.eventTimeInput);
        descInput = findViewById(R.id.eventDescriptionInput);
        saveButton = findViewById(R.id.saveEventButton);
        backButton = findViewById(R.id.backButton);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        eventId = getIntent().getIntExtra("eventId", -1);

        if (eventId != -1) {
            String title = getIntent().getStringExtra("title");
            String date = getIntent().getStringExtra("date");
            String time = getIntent().getStringExtra("time");
            String desc = getIntent().getStringExtra("description");

            titleInput.setText(title);
            dateInput.setText(date);
            timeInput.setText(time);
            descInput.setText(desc);
        }

        saveButton.setOnClickListener(v -> saveEvent());
        backButton.setOnClickListener(v -> finish());
    }

    private void saveEvent() {
        String title = titleInput.getText().toString();
        String date = dateInput.getText().toString();
        String time = timeInput.getText().toString();
        String desc = descInput.getText().toString();

        if (eventId == -1) {
            db.execSQL("INSERT INTO events (title, date, time, description, username) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{title, date, time, desc, currentUser});

            Toast.makeText(this, "Event Saved", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("smsMessage", "Reminder: " + title + " is scheduled for " + date + " at " + time + ".");
            setResult(RESULT_OK, resultIntent);
        } else {
            db.execSQL("UPDATE events SET title=?, date=?, time=?, description=? WHERE id=?",
                    new Object[]{title, date, time, desc, eventId});

            Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}