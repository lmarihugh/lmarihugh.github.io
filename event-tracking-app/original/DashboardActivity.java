package com.example.cs360projectonelynneamarihugh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DashboardActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    String currentUser;
    Button addEventButton;
    Button enableSmsButton;
    TextView smsStatusText;

    TextView event1Title, event1Date, event1Time;
    TextView event2Title, event2Date, event2Time;
    TextView event3Title, event3Date, event3Time;
    TextView event4Title, event4Date, event4Time;

    int[] eventIds = new int[4];
    String[] eventTitles = new String[4];
    String[] eventDates = new String[4];
    String[] eventTimes = new String[4];

    ActivityResultLauncher<String> smsPermissionLauncher;
    ActivityResultLauncher<Intent> addEventLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        currentUser = getIntent().getStringExtra("username");

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        addEventButton = findViewById(R.id.addEventButton);
        enableSmsButton = findViewById(R.id.enableSmsButton);
        smsStatusText = findViewById(R.id.smsStatusText);

        event1Title = findViewById(R.id.event1Title);
        event1Date = findViewById(R.id.event1Date);
        event1Time = findViewById(R.id.event1Time);

        event2Title = findViewById(R.id.event2Title);
        event2Date = findViewById(R.id.event2Date);
        event2Time = findViewById(R.id.event2Time);

        event3Title = findViewById(R.id.event3Title);
        event3Date = findViewById(R.id.event3Date);
        event3Time = findViewById(R.id.event3Time);

        event4Title = findViewById(R.id.event4Title);
        event4Date = findViewById(R.id.event4Date);
        event4Time = findViewById(R.id.event4Time);

        Button delete1 = findViewById(R.id.event1DeleteButton);
        Button delete2 = findViewById(R.id.event2DeleteButton);
        Button delete3 = findViewById(R.id.event3DeleteButton);
        Button delete4 = findViewById(R.id.event4DeleteButton);

        Button edit1 = findViewById(R.id.event1EditButton);
        Button edit2 = findViewById(R.id.event2EditButton);
        Button edit3 = findViewById(R.id.event3EditButton);
        Button edit4 = findViewById(R.id.event4EditButton);

        smsPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    updateSmsStatus();

                    if (isGranted) {
                        Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "SMS permission denied. App will continue without SMS alerts.", Toast.LENGTH_LONG).show();
                    }
                });

        addEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String smsMessage = result.getData().getStringExtra("smsMessage");
                        if (smsMessage != null) {
                            sendSmsAlert(smsMessage);
                        }
                    }
                    loadEvents();
                });

        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, EventDetailsActivity.class);
            intent.putExtra("username", currentUser);
            addEventLauncher.launch(intent);
        });

        enableSmsButton.setOnClickListener(v -> requestSmsPermission());

        delete1.setOnClickListener(v -> deleteEvent(0));
        delete2.setOnClickListener(v -> deleteEvent(1));
        delete3.setOnClickListener(v -> deleteEvent(2));
        delete4.setOnClickListener(v -> deleteEvent(3));

        edit1.setOnClickListener(v -> editEvent(0));
        edit2.setOnClickListener(v -> editEvent(1));
        edit3.setOnClickListener(v -> editEvent(2));
        edit4.setOnClickListener(v -> editEvent(3));

        loadEvents();
        updateSmsStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
        updateSmsStatus();
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission already granted", Toast.LENGTH_SHORT).show();
            updateSmsStatus();
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void updateSmsStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            smsStatusText.setText("SMS permission granted. Event reminders are on.");
            enableSmsButton.setText("SMS Permission Enabled");
        } else {
            smsStatusText.setText("SMS permission not granted. Event reminders are off.");
            enableSmsButton.setText("Enable SMS Permission");
        }
    }

    private void sendSmsAlert(String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = "2535551234";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS alert sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed to send", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadEvents() {
        clearEventCards();

        Cursor cursor = db.rawQuery(
                "SELECT id, title, date, time FROM events WHERE username=?",
                new String[]{currentUser});

        int count = 0;

        while (cursor.moveToNext() && count < 4) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String date = cursor.getString(2);
            String time = cursor.getString(3);

            eventIds[count] = id;
            eventTitles[count] = title;
            eventDates[count] = date;
            eventTimes[count] = time;

            if (count == 0) {
                event1Title.setText(title);
                event1Date.setText(date);
                event1Time.setText(time);
            } else if (count == 1) {
                event2Title.setText(title);
                event2Date.setText(date);
                event2Time.setText(time);
            } else if (count == 2) {
                event3Title.setText(title);
                event3Date.setText(date);
                event3Time.setText(time);
            } else if (count == 3) {
                event4Title.setText(title);
                event4Date.setText(date);
                event4Time.setText(time);
            }

            count++;
        }

        if (count == 0) {
            Toast.makeText(this, "No events yet", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void clearEventCards() {
        for (int i = 0; i < 4; i++) {
            eventIds[i] = 0;
            eventTitles[i] = "";
            eventDates[i] = "";
            eventTimes[i] = "";
        }

        event1Title.setText("");
        event1Date.setText("");
        event1Time.setText("");

        event2Title.setText("");
        event2Date.setText("");
        event2Time.setText("");

        event3Title.setText("");
        event3Date.setText("");
        event3Time.setText("");

        event4Title.setText("");
        event4Date.setText("");
        event4Time.setText("");
    }

    private void deleteEvent(int index) {
        if (eventIds[index] == 0) {
            Toast.makeText(this, "No event to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        db.execSQL("DELETE FROM events WHERE id=?", new Object[]{eventIds[index]});

        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();

        loadEvents();
    }

    private void editEvent(int index) {
        if (eventIds[index] == 0) {
            Toast.makeText(this, "No event to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DashboardActivity.this, EventDetailsActivity.class);
        intent.putExtra("username", currentUser);
        intent.putExtra("eventId", eventIds[index]);
        intent.putExtra("title", eventTitles[index]);
        intent.putExtra("date", eventDates[index]);
        intent.putExtra("time", eventTimes[index]);
        addEventLauncher.launch(intent);
    }
}