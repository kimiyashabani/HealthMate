package com.example.healthmate;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
// Importing FireBase
import com.example.healthmate.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.FirebaseApp;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
public class InputDataActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    //Firebase configuration
    ActivityMainBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CODE_SPEECH_INPUT_CONFIRMATION = 1001;

    private String currentDataType;

    private String initialSpeechInput;
    private String confirmationSpeechInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_data_input);
        // INITIALIZE TEXT TO SPEECH ENGINE
        tts = new TextToSpeech(this, this);
        // INITIALIZE FIREBASE
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("HealthData");
        // DEFINING TEXT VIEWS THAT ARE GOING TO TOGGLE THE SPEECH INPUT
        TextView heartRate = findViewById(R.id.heartrate);
        TextView bloodPressure = findViewById(R.id.bloodpressure);
        TextView weight = findViewById(R.id.weight);
        TextView temperature = findViewById(R.id.temperature);

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

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This language is not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        if (tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    private void showConfirmationDialog(String spokenText) {
        String confirmationMessage = "Is" + spokenText + "correct? \n Please say yes or no";
        tts.speak(confirmationMessage, TextToSpeech.QUEUE_FLUSH, null, "confirm");
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Do nothing
            }

            @Override
            public void onDone(String utteranceId) {
                // Start listening for user's response after the TTS engine has finished speaking
                promptSpeechInputForConfirmation();
            }

            @Override
            public void onError(String utteranceId) {
            }
        });

    }
    private void promptSpeechInputForConfirmation(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Yes or No...");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_CONFIRMATION);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(InputDataActivity.this, "Speech input is not supported on this device.", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0).toLowerCase();
                initialSpeechInput = spokenText;
                showConfirmationDialog(spokenText);
            }
        }
        else if (requestCode == REQUEST_CODE_SPEECH_INPUT_CONFIRMATION && resultCode == RESULT_OK && data != null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0).toLowerCase();
                confirmationSpeechInput = spokenText;
                if (confirmationSpeechInput.contains("yes")) {
                    // USER CONFIREMED --> SAVE DATA TO FIREBASE
                    saveDataToFirebase("1", currentDataType, initialSpeechInput);
                    Toast.makeText(InputDataActivity.this, "Data saved!", Toast.LENGTH_SHORT).show();
                    if (currentDataType.equals("temperature")) {
                        int temperature = Integer.parseInt(initialSpeechInput);
                        if (temperature > 37.5) {
                        }
                    } else if (currentDataType.equals("heartrate")) {
                        int heartrate = Integer.parseInt(initialSpeechInput);
                        if (heartrate > 80) {
                        }
                    } else if (currentDataType.equals("weight")) {
                        int weight = Integer.parseInt(initialSpeechInput);
                        Intent intent = new Intent(InputDataActivity.this, MainActivity.class);
                        intent.putExtra("weight", weight);
                        startActivity(intent);
                    }
                } else if (confirmationSpeechInput.contains("no")) {
                    // User DECLINED
                    Toast.makeText(InputDataActivity.this, "Data not saved.", Toast.LENGTH_SHORT).show();
                } else {
                    // HANDLE UNRECOGNIZED INPUT
                    Toast.makeText(InputDataActivity.this, "Please say Yes or No.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    public void saveDataToFirebase(String userId, String type, String value) {
        HealthData healthData = new HealthData(type, value);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        reference.child("users").child(userId).child(currentDate).push().setValue(healthData);
    }
}