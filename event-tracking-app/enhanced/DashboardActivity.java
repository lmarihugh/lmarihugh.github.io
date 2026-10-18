package com.example.cs360projectonelynneamarihugh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements EventAdapter.OnEventActionListener {

    EventRepository eventRepository;
    String currentUser;
    Button addEventButton;
    Button enableSmsButton;
    TextView smsStatusText;

    RecyclerView eventRecyclerView;
    EventAdapter eventAdapter;
    List<Event> events = new ArrayList<>();

    ActivityResultLauncher<String> smsPermissionLauncher;
    ActivityResultLauncher<Intent> addEventLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        currentUser = getIntent().getStringExtra("username");
        eventRepository = new EventRepository(this);

        addEventButton = findViewById(R.id.addEventButton);
        enableSmsButton = findViewById(R.id.enableSmsButton);
        smsStatusText = findViewById(R.id.smsStatusText);

        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(events, this);
        eventRecyclerView.setAdapter(eventAdapter);

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
        List<Event> freshEvents = eventRepository.getEventsForUser(currentUser);
        eventAdapter.setEvents(freshEvents);

        if (freshEvents.isEmpty()) {
            Toast.makeText(this, "No events yet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEdit(Event event) {
        Intent intent = new Intent(DashboardActivity.this, EventDetailsActivity.class);
        intent.putExtra("username", currentUser);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("date", event.getDate());
        intent.putExtra("time", event.getTime());
        intent.putExtra("description", event.getDescription());
        addEventLauncher.launch(intent);
    }

    @Override
    public void onDelete(Event event) {
        eventRepository.deleteEvent(event.getId());
        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
        loadEvents();
    }
}