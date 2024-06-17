package com.example.healthmate;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.net.Uri;

public class InputDataActivity extends AppCompatActivity {

    //Firebase configuration
    ActivityMainBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private String currentDataType;
    private static final int SMS_PERMISSION_CODE = 100;
    private static final int CALL_PERMISSION_CODE = 200;

    public static final String ACCOUNT_SID = System.getenv("AC2d14c6c12b9510c7025713eaba7c74be");
    public static final String AUTH_TOKEN = System.getenv("36b78e2ae85d7415da1dc52c7b362fc1");

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
        TextView temperature = findViewById(R.id.temperature);

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
        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = "temperature";
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
                showConfirmationDialog(spokenText);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS(200, "this is a test");
            } else {
                // Permission was denied. Disable the functionality that depends on this permission.
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
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
                        ActivityCompat.requestPermissions(InputDataActivity.this, new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                        saveDataToFirebase("1", currentDataType, spokenText);
                        if (currentDataType.equals("heartrate")) {
                            int heartRate = Integer.parseInt(spokenText);
                            if (heartRate > 100 || heartRate < 60) {
                                sendSMS(heartRate, "heart rate");
                            }
                        } else if (currentDataType.equals("bloodPressure")) {
                            int bloodPressure = Integer.parseInt(spokenText);
                            if (bloodPressure > 140 ) {
                                sendSMS(bloodPressure, "blood pressure");
                            }
                        } else if (currentDataType.equals("tempreture")) {
                            int bloodPressure = Integer.parseInt(spokenText);
                            if (bloodPressure > 38.5 ) {
                                sendSMS(bloodPressure, "tempreture");
                            }
                        }else if (currentDataType.equals("weight")) {
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
//    private void sendSMS(int heartRate, String symptom) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage("+34614140621", null, "Your heart rate is " + heartRate + ". This is a warning message from HealthMate. Please consult a doctor.", null, null);
//            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
//        }
//    }

    private void sendSMS(int heartRate, String symptom) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            String caregiverNumber = "caregiver's phone number";
            String message = "The patient's heart rate is " + heartRate + ". This is a warning message from HealthMate. Please check on the patient.";
            smsManager.sendTextMessage(caregiverNumber, null, message, null, null);
            Toast.makeText(this, "SMS sent to caregiver", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    private void callAmbulance() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            String ambulanceNumber = "+34614140621";
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + ambulanceNumber));
            startActivity(callIntent);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
        }
    }
}

