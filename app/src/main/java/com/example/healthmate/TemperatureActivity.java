package com.example.healthmate;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Calendar;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TemperatureActivity extends AppCompatActivity{
    FirebaseDatabase db;

    DatabaseReference healthReference;
    private String specificDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        FirebaseApp.initializeApp(this);

        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        healthReference = db.getReference("HealthData");

        LineChart temperatureLineChart = findViewById(R.id.temperatureLineChart);


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
                    ArrayList<Entry> temperatureEntries = new ArrayList<>();
                    List<String> dates = new ArrayList<>();
                    int temperatureIndex = 0;
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String specificDataDate = dateSnapshot.getKey();
                        if (specificDataDate != null) {
                            dates.add(specificDataDate);
                            // Iterate through the health data entries under each date
                            for (DataSnapshot dataSnapshot : dateSnapshot.getChildren()) {
                                HealthData healthData = dataSnapshot.getValue(HealthData.class);
                                if (healthData != null && healthData.getType().equals("temperature")) {
                                    String valueString = healthData.getValue().replace(",", ".");
                                    try {
                                        float temperature = Float.parseFloat(valueString);
                                        temperatureEntries.add(new Entry(temperatureIndex, temperature));
                                        temperatureIndex += 1;
                                    } catch (NumberFormatException e) {
                                        System.out.println("Error parsing temperature value: " + healthData.getValue());
                                    }
                                }
                            }

                        }
                    }

                    // SETTING UP LINE CHART FOR TEMPERATURE
                    temperatureEntries.addAll(temperatureEntries);
                    LineDataSet temperatureDataSet = new LineDataSet(temperatureEntries, "Temperature");
                    temperatureDataSet.setColors(ColorTemplate.rgb("FFCE59"));
                    temperatureDataSet.setLineWidth(2);
                    temperatureDataSet.setCircleColors(ColorTemplate.rgb("656853"));
                    LineData temperatureLineData = new LineData(temperatureDataSet);
                    temperatureLineChart.setData(temperatureLineData);
                    XAxis xAxisTemp = temperatureLineChart.getXAxis();
                    xAxisTemp.setValueFormatter(new IndexAxisValueFormatter(dates));
                    xAxisTemp.setGranularity(1f);
                    xAxisTemp.setGranularityEnabled(true);
                    temperatureLineChart.invalidate();
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
