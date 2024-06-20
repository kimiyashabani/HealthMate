package com.example.healthmate;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ArrayAdapter;
import java.util.HashMap;
import java.util.Map;
import com.google.android.material.appbar.MaterialToolbar;

public class EducationalActivity extends AppCompatActivity {
    private GridView gridView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational);

        gridView = findViewById(R.id.gridView);

        String[] values = new String[] {
                "What is hypertension?",
                "What are the symptoms of a heart attack?",
                "What is the function of insulin in the body?",
                "How is depression treated?",
                "What are ACE inhibitors?",
                "How can one manage high cholesterol",
                "Who is a cardiologist?",
                "What is chronic obstructive pulmonary disease (COPD)?",
                "What is a stress test?"
        };

        Map<String, String> detailedTerms = new HashMap<>();
        detailedTerms.put("What is hypertension?", "High blood pressure is a common condition that affects the body's arteries. It's also called hypertension. If you have high blood pressure, the force of the blood pushing against the artery walls is consistently too high. The heart has to work harder to pump blood.\n" +
                "\n" +
                "Blood pressure is measured in millimeters of mercury (mm Hg). In general, hypertension is a blood pressure reading of 130/80 millimeters of mercury (mm Hg) or higher.");
        detailedTerms.put("What are the symptoms of a heart attack?", "Symptoms of a heart attack include chest pain, shortness of breath, and pain in the arms, back, neck, or jaw. If you experience these symptoms, seek medical attention immediately.");
        detailedTerms.put("What is the function of insulin in the body?", "Insulin is a hormone that helps regulate blood sugar levels. It allows your body to use glucose for energy and helps keep your blood sugar levels stable.");
        detailedTerms.put("How is depression treated?", "Depression can be treated with therapy, medication, or a combination of both. It is important to seek help from a mental health professional if you are experiencing symptoms of depression.");
        detailedTerms.put("What are ACE inhibitors?", "ACE inhibitors are a type of medication used to treat high blood pressure, heart failure, and other conditions. They work by relaxing blood vessels and reducing the workload on the heart.");
        detailedTerms.put("How can one manage high cholesterol", "High cholesterol can be managed through lifestyle changes such as eating a healthy diet, exercising regularly, and quitting smoking. Medications may also be prescribed to help lower cholesterol levels.");
        detailedTerms.put("Who is a cardiologist?", "A cardiologist is a doctor who specializes in diagnosing and treating heart conditions. They may perform tests such as EKGs, echocardiograms, and stress tests to evaluate heart health.");
        detailedTerms.put("What is chronic obstructive pulmonary disease (COPD)?", "COPD is a chronic lung disease that makes it difficult to breathe. It includes conditions such as emphysema and chronic bronchitis. COPD is often caused by smoking.");
        detailedTerms.put("What is a stress test?", "A stress test is a test used to evaluate how well your heart functions during physical activity. It involves walking on a treadmill or riding a stationary bike while your heart rate and blood pressure are monitored.");


        int[] imageIds = new int[] {
                R.drawable.artboard2,
                R.drawable.artboard3,
                R.drawable.artboard7,
                R.drawable.artboard8,
                R.drawable.artboard9,
                R.drawable.artboard10,
                R.drawable.artboard11,
                R.drawable.artboard13,
                R.drawable.artboard17
        };

        GridAdapter adapter = new GridAdapter(this, values,imageIds, detailedTerms);
        gridView.setAdapter(adapter);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
