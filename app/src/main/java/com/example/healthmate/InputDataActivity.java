package com.example.healthmate;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.AdapterView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

// Importing FireBase
import com.example.healthmate.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.FirebaseApp;

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
        setContentView(R.layout.data_input);

        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("HealthData");

        //Defining Textviews to toggle:
        TextView heartRate = findViewById(R.id.heartrate);
        TextView bloodPressure = findViewById(R.id.bloodpressure);
        TextView weight = findViewById(R.id.weight);

        // Setting Click Listeners:
        heartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = "heartrate";
                promptSpeechInput();
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

        findViewById(R.id.analytics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputDataActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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
                Toast.makeText(this, "You said: " + spokenText, Toast.LENGTH_LONG).show();
                Log.d("InputDataActivity", "Recognized speech: " + spokenText);
                // Saving data to Firebase
                saveDataToFirebase("1",currentDataType, spokenText);
                if (currentDataType.equals("weight")) {
                    Intent intent = new Intent(InputDataActivity.this, MainActivity.class);
                    intent.putExtra("weight", spokenText);
                    startActivity(intent);
                }
            }
        }
    }
    public void saveDataToFirebase(String userId,String type, String value){
        HealthData healthData = new HealthData(type, value);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        reference.child("users").child(userId).child(currentDate).push().setValue(healthData);
    }
}
