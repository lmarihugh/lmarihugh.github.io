package com.example.cs360projectonelynneamarihugh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    EditText titleInput, dateInput, timeInput, descInput;
    Button saveButton, backButton;

    EventRepository eventRepository;

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

        eventRepository = new EventRepository(this);

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

    private boolean isInputValid(String title, String date, String time) {
        if (!EventValidator.isValidTitle(title)) {
            Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!EventValidator.isValidDate(date)) {
            Toast.makeText(this, "Enter a valid date (MM/DD/YYYY)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!EventValidator.isValidTime(time)) {
            Toast.makeText(this, "Enter a valid time (e.g. 2:30 PM)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveEvent() {
        String title = titleInput.getText().toString();
        String date = dateInput.getText().toString();
        String time = timeInput.getText().toString();
        String desc = descInput.getText().toString();

        if (!isInputValid(title, date, time)) {
            return;
        }

        if (eventId == -1) {
            Event newEvent = new Event(-1, title, date, time, desc, currentUser);
            eventRepository.insertEvent(newEvent);

            Toast.makeText(this, "Event Saved", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("smsMessage", "Reminder: " + title + " is scheduled for " + date + " at " + time + ".");
            setResult(RESULT_OK, resultIntent);
        } else {
            Event updatedEvent = new Event(eventId, title, date, time, desc, currentUser);
            eventRepository.updateEvent(updatedEvent);

            Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}