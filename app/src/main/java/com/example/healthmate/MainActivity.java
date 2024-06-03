package com.example.healthmate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.List;


import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sample data arrays
        String[] medicationData = {"Med1: 10mg", "Med2: 5mg", "Med3: 20mg"};
        float[] heartRateData = {72f, 75f, 78f, 80f, 76f};
        float[] bloodPressureData = {120f, 130f, 125f, 128f, 122f};
        float weightData = 70f;

        // Medication List
        ListView medicationListView = findViewById(R.id.medicationListView);
        ArrayAdapter<String> medicationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicationData);
        medicationListView.setAdapter(medicationAdapter);

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