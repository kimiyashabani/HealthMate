package com.example.healthmate;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
// Importing FireBase
import com.example.healthmate.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.FirebaseApp;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class InputDataActivity extends AppCompatActivity {

    //Firebase configuration
    ActivityMainBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private String currentDataType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_data_input);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("HealthData");

        //Defining Textviews to toggle:
        TextView heartRate = findViewById(R.id.heartrate);
        TextView bloodPressure = findViewById(R.id.bloodpressure);
        TextView weight = findViewById(R.id.weight);
        TextView temperature = findViewById(R.id.temperature);
        Button questionButton = findViewById(R.id.questions);

        // Setting Click Listeners:
        heartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputDataActivity.this, HeartRateActivity.class);
                startActivity(intent);

            }
        });
        bloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = "bloodPressure";
                promptSpeechInput();
            }
        });
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = "weight";
                promptSpeechInput();
            }

        });
        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = "temperature";
                promptSpeechInput();
            }
        });

        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputDataActivity.this, EducationalActivity.class);
                startActivity(intent);
            }
        });


        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optionally, add any custom logic here if needed
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Speech input is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                showConfirmationDialog(spokenText);
            }
        }
    }

    private void showConfirmationDialog(String spokenText) {

        new AlertDialog.Builder(this)
                .setTitle("Confirm your input")
                .setMessage("Is this correct? \n" + spokenText)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(InputDataActivity.this, "You said: " + spokenText, Toast.LENGTH_LONG).show();
                        saveDataToFirebase("1", currentDataType, spokenText);
                        if (currentDataType.equals("heartrate")) {
                            int heartRate = Integer.parseInt(spokenText);
                            if (heartRate > 100 || heartRate < 60) {
                                //sendSMS(heartRate, "heart rate");
                                //callAmbulance();
                            }
                        } else if (currentDataType.equals("bloodPressure")) {
                            int bloodPressure = Integer.parseInt(spokenText);
                            if (bloodPressure > 140) {
                                //sendSMS(bloodPressure, "blood pressure");
                            }
                        } else if (currentDataType.equals("tempreture")) {
                            int temperature = Integer.parseInt(spokenText);
                            if (temperature > 38.5) {
                                //sendSMS(bloodPressure, "tempreture");
                            }
                        } else if (currentDataType.equals("weight")) {
                            int weight = Integer.parseInt(spokenText);
                            Intent intent = new Intent(InputDataActivity.this, MainActivity.class);
                            intent.putExtra("weight", weight);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void saveDataToFirebase(String userId, String type, String value) {
        HealthData healthData = new HealthData(type, value);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        reference.child("users").child(userId).child(currentDate).push().setValue(healthData);
    }
}

