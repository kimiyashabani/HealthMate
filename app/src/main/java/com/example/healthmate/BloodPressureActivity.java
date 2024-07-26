package com.example.healthmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BloodPressureActivity extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference reference;
    DatabaseReference healthReference;
    private String specificDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodpressure);
        // INITIALIZATION OF FIREBASE DATABASE
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        healthReference = db.getReference("HealthData");
        BarChart bloodPressureBarChart = findViewById(R.id.bloodPressureBarChart);


        // READING DATA FROM FIREBASE AND DISPLAYING IT IN THE CHARTS
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i=0 ; i<30 ; i++) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            specificDate = sdf.format(calendar.getTime());
            DatabaseReference healthDataReference = healthReference.child("users").child("1").child(specificDate);
            DatabaseReference healthDataDateRef = healthReference.child("users").child("1");
            healthDataDateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<BarEntry> bloodPressureEntries = new ArrayList<>();
                    List<String> dates = new ArrayList<>();
                    int bloodIndex = 0;
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String specificDataDate = dateSnapshot.getKey();
                        if (specificDataDate != null) {
                            dates.add(specificDataDate);
                            // Iterate through the health data entries under each date
                            for (DataSnapshot dataSnapshot : dateSnapshot.getChildren()) {
                                HealthData healthData = dataSnapshot.getValue(HealthData.class);
                                 if (healthData != null && healthData.getType().equals("bloodPressure")) {
                                    String valueString = healthData.getValue().replace(",", ".");
                                    try {
                                        float bloodPressure = Float.parseFloat(valueString);
                                        bloodPressureEntries.add(new BarEntry(bloodIndex, bloodPressure));
                                        bloodIndex += 1;
                                    } catch (NumberFormatException e) {
                                        System.out.println("Error parsing blood pressure value: " + healthData.getValue());
                                    }
                                }
                            }
                        }
                    }

                    //SETTING UP BAR CHART FOR BLOOD PRESSURE
                    bloodPressureEntries.addAll(bloodPressureEntries);
                    BarDataSet bloodPressureDataSet = new BarDataSet(bloodPressureEntries, "Blood Pressure");
                    BarData bloodPressureBarData = new BarData(bloodPressureDataSet);
                    XAxis xAxisBlood = bloodPressureBarChart.getXAxis();
                    xAxisBlood.setValueFormatter(new IndexAxisValueFormatter(dates));
                    xAxisBlood.setGranularity(1f);
                    xAxisBlood.setGranularityEnabled(true);
                    bloodPressureBarChart.setData(bloodPressureBarData);
                    bloodPressureDataSet.setColors(ColorTemplate.rgb("FFCE59"));
                    bloodPressureBarChart.setBorderColor(ColorTemplate.rgb("656853"));
                    bloodPressureBarChart.invalidate();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Failed to read value.");
                }
            });

        }

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


    }
}
