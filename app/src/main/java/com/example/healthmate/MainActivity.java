package com.example.healthmate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.List;


import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity {

    private boolean isPill1Taken = false;
    private boolean isPill1NotTaken = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sample data arrays
        String[] medicationData = {"Med1: 10mg", "Med2: 5mg", "Med3: 20mg"};
        float[] heartRateData = {72f, 75f, 78f, 80f, 76f};
        float[] bloodPressureData = {120f, 130f, 125f, 128f, 122f};
        float weightData = 70f;

        // First Medication
        ImageButton pill_1_is_taken = findViewById(R.id.pills1_taken);
        ImageButton pill_1_not_taken = findViewById(R.id.pills1_not_taken);
        TextView med1 = findViewById(R.id.Med1);
        med1.setText(medicationData[0]);

        pill_1_is_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPill1Taken){
                    pill_1_is_taken.setBackgroundColor(Color.parseColor("#804CAF50"));
                }else {
                    pill_1_is_taken.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                }
                isPill1Taken = !isPill1Taken;
            }
        });
        pill_1_not_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPill1NotTaken){
                    pill_1_not_taken.setBackgroundColor(Color.parseColor("#80AF4C4C"));
                }else{
                    pill_1_not_taken.setBackgroundColor(Color.parseColor("#FFAF4C4C"));
                }
               isPill1NotTaken =! isPill1NotTaken;
            }
        });

        // Second Medication
        ImageButton pill_2_is_taken = findViewById(R.id.pills2_taken);
        ImageButton pill_2_not_taken = findViewById(R.id.pills2_not_taken);
        TextView med2 = findViewById(R.id.Med2);
        med2.setText(medicationData[1]);

        // Heart Rate Line Chart
        LineChart heartRateLineChart = findViewById(R.id.heartRateLineChart);
        ArrayList<Entry> heartRateEntries = new ArrayList<>();
        for (int i = 0; i < heartRateData.length; i++) {
            heartRateEntries.add(new Entry(i, heartRateData[i]));
        }
        LineDataSet heartRateDataSet = new LineDataSet(heartRateEntries, "Heart Rate");
        LineData heartRateLineData = new LineData(heartRateDataSet);
        heartRateLineChart.setData(heartRateLineData);
        heartRateDataSet.setColors(ColorTemplate.rgb("FFCE59"));
        heartRateDataSet.setLineWidth(2);
        heartRateDataSet.setCircleColors(ColorTemplate.rgb("656853"));
        heartRateLineChart.invalidate(); // refresh


        // Blood Pressure Bar Chart
        BarChart bloodPressureBarChart = findViewById(R.id.bloodPressureBarChart);
        ArrayList<BarEntry> bloodPressureEntries = new ArrayList<>();
        for (int i = 0; i < bloodPressureData.length; i++) {
            bloodPressureEntries.add(new BarEntry(i, bloodPressureData[i]));
        }
        BarDataSet bloodPressureDataSet = new BarDataSet(bloodPressureEntries, "Blood Pressure");
        BarData bloodPressureBarData = new BarData(bloodPressureDataSet);
        bloodPressureBarChart.setData(bloodPressureBarData);
        bloodPressureDataSet.setColors(ColorTemplate.rgb("FFCE59"));
        bloodPressureBarChart.setBorderColor(ColorTemplate.rgb("656853"));
        bloodPressureBarChart.invalidate(); // refresh

        // Weight TextView
        TextView weightTextView = findViewById(R.id.weightTextView);
        weightTextView.setText(weightData + "kg");

        findViewById(R.id.dataimport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputDataActivity.class);
                startActivity(intent);
            }
        });
    }
}