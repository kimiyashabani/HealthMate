package com.example.healthmate;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ArrayAdapter;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import androidx.annotation.Nullable;


public class EducationalActivity extends AppCompatActivity {
    private GridView gridView;
    private static final int REQ_CODE_SPEECH_INPUT = 1000;
    private TextView airesponse;
    private Button speakButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational);

        gridView = findViewById(R.id.gridView);
        airesponse = findViewById(R.id.response);
        speakButton = findViewById(R.id.askquestion);

        speakButton.setOnClickListener(v -> {
            promptSpeechInput();
        });
        // SETTING UP THE GRIDVIEW THAT CONTAINS THE EDUCATIONAL TERMS
        String[] values = new String[] {
                "What is hypertension?",
                "What is the function of insulin in the body?",
                "How is depression treated?",
                "What are ACE inhibitors?",
                "How can one manage high cholesterol",
                "What is chronic obstructive pulmonary disease (COPD)?",
                "What is a stress test?"
        };

        Map<String, String> detailedTerms = new HashMap<>();
        detailedTerms.put("What is hypertension?", "High blood pressure is a common condition that affects the body's arteries. It's also called hypertension. If you have high blood pressure, the force of the blood pushing against the artery walls is consistently too high. The heart has to work harder to pump blood.\n" +
                "\n" +
                "Blood pressure is measured in millimeters of mercury (mm Hg). In general, hypertension is a blood pressure reading of 130/80 millimeters of mercury (mm Hg) or higher.");
        detailedTerms.put("What is the function of insulin in the body?", "Insulin is a hormone that helps regulate blood sugar levels. It allows your body to use glucose for energy and helps keep your blood sugar levels stable.");
        detailedTerms.put("How is depression treated?", "Depression can be treated with therapy, medication, or a combination of both. It is important to seek help from a mental health professional if you are experiencing symptoms of depression.");
        detailedTerms.put("What are ACE inhibitors?", "ACE inhibitors are a type of medication used to treat high blood pressure, heart failure, and other conditions. They work by relaxing blood vessels and reducing the workload on the heart.");
        detailedTerms.put("How can one manage high cholesterol", "High cholesterol can be managed through lifestyle changes such as eating a healthy diet, exercising regularly, and quitting smoking. Medications may also be prescribed to help lower cholesterol levels.");
        detailedTerms.put("What is chronic obstructive pulmonary disease (COPD)?", "COPD is a chronic lung disease that makes it difficult to breathe. It includes conditions such as emphysema and chronic bronchitis. COPD is often caused by smoking.");
        detailedTerms.put("What is a stress test?", "A stress test is a test used to evaluate how well your heart functions during physical activity. It involves walking on a treadmill or riding a stationary bike while your heart rate and blood pressure are monitored.");

        // LOAD IMAGES FOR THE EDUCATIONAL TERMS
        int[] imageIds = new int[] {
                R.drawable.artboard2,
                R.drawable.artboard7,
                R.drawable.artboard8,
                R.drawable.artboard9,
                R.drawable.artboard10,
                R.drawable.artboard13,
                R.drawable.artboard17
        };

        GridAdapter adapter = new GridAdapter(this, values,imageIds, detailedTerms);
        gridView.setAdapter(adapter);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    // USER ASKS A QUESTION FROM AI ASSISTANT
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask your question...");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            airesponse.setText("Speech not supported");
        }
    }
    // CONNNECT TO THE API TO GET A RESPONSE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String question = result.get(0);
                    //sendQuestionToAPI(question);
                    makePostRequest(question);
                }
                break;
            }
        }
    }
    private void makePostRequest(String query) {
        new PostTask(airesponse).execute(query);
    }
}

