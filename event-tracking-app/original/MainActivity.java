package com.example.cs360projectonelynneamarihugh;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton, createAccountButton;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Initialize database
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // LOGIN BUTTON
        loginButton.setOnClickListener(view -> loginUser());

        // CREATE ACCOUNT BUTTON
        createAccountButton.setOnClickListener(view -> createUser());
    }
    private void loginUser() {

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password});

        if (cursor.getCount() > 0) {
            currentUser = username;

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            // dash
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("username", currentUser);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void createUser() {

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username});

        if (cursor.getCount() > 0) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
        } else {
            db.execSQL(
                    "INSERT INTO users (username, password) VALUES (?, ?)",
                    new Object[]{username, password});

            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }
}