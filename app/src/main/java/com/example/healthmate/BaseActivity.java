package com.example.healthmate;

import android.content.ActivityNotFoundException;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
// Importing FireBase
import com.example.healthmate.databinding.ActivityMainBinding;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.FirebaseApp;

import android.speech.tts.UtteranceProgressListener;

public abstract class BaseActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    //Firebase configuration
    ActivityMainBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CODE_SPEECH_INPUT_CONFIRMATION = 1001;

    private String currentDataType;

    protected String initialSpeechInput;
    private String confirmationSpeechInput;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // INITIALIZE FIREBASE
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("HealthData");
        // DEFINING TEXT VIEWS THAT ARE GOING TO TOGGLE THE SPEECH INPUT

        TextView bloodPressure = findViewById(R.id.bloodpressure);
        TextView weight = findViewById(R.id.weight);

        // INITIALIZE TEXT TO SPEECH ENGINE
        tts = new TextToSpeech(this, this);

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
    public void promptSpeechInput() {
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
            Toast.makeText(BaseActivity.this, "Speech input is not supported on this device.", Toast.LENGTH_SHORT).show();
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
                handleConfirmationResponse();
            }

        }
    }
    protected abstract void handleConfirmationResponse();
    public void saveDataToFirebase(String userId, String type, String value) {
        HealthData healthData = new HealthData(type, value);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        reference.child("users").child(userId).child(currentDate).push().setValue(healthData);
    }
}