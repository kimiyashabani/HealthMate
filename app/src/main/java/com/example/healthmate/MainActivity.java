package com.example.healthmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;


import com.example.healthmate.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

public class MainActivity extends AppCompatActivity {



    //Firebase configuration
    ActivityMainBinding binding;
    private PieChart pieChart;
    FirebaseDatabase db;
    DatabaseReference reference;
    DatabaseReference healthReference;
    private boolean isPill1Taken = false;
    private boolean isPill1NotTaken = false;

    private boolean isPill2Taken = false;
    private boolean isPill2NotTaken = false;
    private String pill1IsTakenKey = null;
    private String pill1NotTakenKey = null;
    private String pill2IsTakenKey = null;
    private String pill2NotTakenKey = null;

    private int AspirinTaken = 0;
    private int AspirinNotTaken = 0;
    private int IbuprofenTaken = 0;
    private int IbuprofenNotTaken = 0;
    private String specificDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("PillIntake");
        healthReference = db.getReference("HealthData");
        pieChart = findViewById(R.id.pie_chart);
        TextView medication1 = findViewById(R.id.Med1);
        TextView medication2 = findViewById(R.id.Med2);
        medication1.setText("Aspirin");
        medication2.setText("Ibuprofen");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // First Medication
        ImageButton pill_1_is_taken = findViewById(R.id.pills1_taken);
        ImageButton pill_1_not_taken = findViewById(R.id.pills1_not_taken);
        ImageButton pill_2_is_taken = findViewById(R.id.pills2_taken);
        ImageButton pill_2_not_taken = findViewById(R.id.pills2_not_taken);
        Button questionButton = findViewById(R.id.questions);

        SharedPreferences sharedPreferences = getSharedPreferences("PillIntakePref", MODE_PRIVATE);
        boolean ispill1taken = sharedPreferences.getBoolean("isPill1Taken", false);
        boolean ispill1nottaken = sharedPreferences.getBoolean("isPill1NotTaken", false);

        boolean ispill2taken = sharedPreferences.getBoolean("isPill2Taken", false);
        boolean ispill2nottaken = sharedPreferences.getBoolean("isPill2NotTaken", false);

        String pill1Date = sharedPreferences.getString("pill1Date", "");
        String pill2Date = sharedPreferences.getString("pill2Date", "");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //check if 24 hours have passed for pill 1
        if (!pill1Date.isEmpty() && !currentDate.equals(pill1Date)){
            editor.putBoolean("isPill1Taken", false);
            editor.putBoolean("isPill1NotTaken", false);
            editor.putString("pill1Date", "");
            editor.apply();
        }
        // check if 24 hours have passed for pill 2
        if (!pill2Date.isEmpty() && !currentDate.equals(pill2Date)){
            editor.putBoolean("isPill2Taken", false);
            editor.putBoolean("isPill2NotTaken", false);
            editor.putString("pill2Date", "");
            editor.apply();
        }
        //Shared preferences for pill 1
        if (ispill1taken) {
            pill_1_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_rounded_button));
            pill_1_not_taken.setEnabled(false);
        }else{
            pill_1_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
            pill_1_not_taken.setEnabled(true);
        }

        if (ispill1nottaken) {
            pill_1_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.deselected_rounded_button));
            pill_1_is_taken.setEnabled(false);
        }else{
            pill_1_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
            pill_1_is_taken.setEnabled(true);
        }

        // shared preferences for pill 2
        if (ispill2taken) {
            pill_2_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_rounded_button));
            pill_2_not_taken.setEnabled(false);
        }else{
            pill_2_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
            pill_2_not_taken.setEnabled(true);
        }

        if (ispill2nottaken) {
            pill_2_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.deselected_rounded_button));
            pill_2_is_taken.setEnabled(false);
        }else{
            pill_2_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
            pill_2_is_taken.setEnabled(true);
        }
        pill_1_is_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPill1Taken){
                    if (pill1IsTakenKey != null){
                        reference.child("users").child(currentDate).child(pill1IsTakenKey).removeValue();
                        pill1IsTakenKey = null;
                    }
                    pill_1_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
                    pill_1_not_taken.setEnabled(true);
                    editor.putBoolean("isPill1Taken", false);
                    editor.remove("pill1Date");
                }else {
                    PillIntake pillIntake = new PillIntake("Aspirin", true);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill1IsTakenKey = newEntryRef.getKey();
                    pill_1_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_rounded_button));
                    pill_1_not_taken.setEnabled(false);
                    editor.putBoolean("isPill1Taken", true);
                    editor.putString("pill1Date",currentDate);
                }
                isPill1Taken = !isPill1Taken;
                editor.apply();
            }
        });
        pill_1_not_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPill1NotTaken){
                    if (pill1NotTakenKey != null){
                        reference.child("users").child(currentDate).child(pill1NotTakenKey).removeValue();
                        pill1NotTakenKey = null;
                    }
                    pill_1_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
                    pill_1_is_taken.setEnabled(true);
                    editor.putBoolean("isPill1NotTaken", false);
                    editor.remove("pill1Date");
                }else {
                    PillIntake pillIntake = new PillIntake("Aspirin", false);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill1NotTakenKey = newEntryRef.getKey();
                    pill_1_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.deselected_rounded_button));
                    pill_1_is_taken.setEnabled(false);
                    editor.putBoolean("isPill1NotTaken", true);
                    editor.putString("pill1Date", currentDate);
                }
               isPill1NotTaken =! isPill1NotTaken;
                editor.apply();
            }
        });

        pill_2_is_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPill2Taken){
                    if (pill2IsTakenKey != null){
                        reference.child("users").child(currentDate).child(pill2IsTakenKey).removeValue();
                        pill2IsTakenKey = null;
                    }
                    pill_2_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
                    pill_2_not_taken.setEnabled(true);
                    editor.putBoolean("isPill2Taken", false);
                    editor.remove("pill2Date");
                }else {
                    PillIntake pillIntake = new PillIntake("Ibuprofen", true);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill2IsTakenKey = newEntryRef.getKey();
                    pill_2_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_rounded_button));
                    pill_2_not_taken.setEnabled(false);
                    editor.putBoolean("isPill2Taken", true);
                    editor.putString("pill2Date", currentDate);
                }
                isPill2Taken = !isPill2Taken;
                editor.apply();
            }
        });
        pill_2_not_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPill2NotTaken){
                    if (pill2NotTakenKey != null){
                        reference.child("users").child(currentDate).child(pill2NotTakenKey).removeValue();
                        pill2NotTakenKey = null;
                    }
                    pill_2_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_button));
                    pill_2_is_taken.setEnabled(true);
                    editor.putBoolean("isPill2NotTaken", false);
                    editor.remove("pill2Date");
                }else {
                    PillIntake pillIntake = new PillIntake("Ibuprofen", false);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill2NotTakenKey = newEntryRef.getKey();
                    pill_2_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.deselected_rounded_button));
                    pill_2_is_taken.setEnabled(false);
                    editor.putBoolean("isPill2NotTaken", true);
                    editor.putString("pill2Date", currentDate);
                }
                isPill2NotTaken =! isPill2NotTaken;
                editor.apply();
            }
        });
        // Heart Rate Line Chart
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i=0 ; i<30 ; i++) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            specificDate = sdf.format(calendar.getTime());
            LineChart heartRateLineChart = findViewById(R.id.heartRateLineChart);
            BarChart bloodPressureBarChart = findViewById(R.id.bloodPressureBarChart);
            DatabaseReference healthDataReference = healthReference.child("users").child("1").child(specificDate);
            healthDataReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Entry> heartRateEntries = new ArrayList<>();
                    ArrayList<BarEntry> bloodPressureEntries = new ArrayList<>();
                    int heartindexOffset = 0;
                    int bloodindexOffset = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        HealthData healthData = child.getValue(HealthData.class);
                        if (healthData != null && healthData.getType().equals("heartrate")) {
                            String valueString = healthData.getValue().replace(",", ".");
                            try {
                                float heartvalue = Float.parseFloat(valueString);
                                heartRateEntries.add(new Entry(heartindexOffset, heartvalue));
                                heartindexOffset += 1;
                                System.out.println("Heart rate is: " + healthData.getValue());
                            } catch (NumberFormatException e) {
                                System.out.println("Error parsing heart rate value: " + healthData.getValue());
                            }
                            LineDataSet heartRateDataSet = new LineDataSet(heartRateEntries, "Heart Rate");
                            heartRateDataSet.setColors(ColorTemplate.rgb("FFCE59"));
                            heartRateDataSet.setLineWidth(2);
                            heartRateDataSet.setCircleColors(ColorTemplate.rgb("656853"));

                            LineData heartRateLineData = new LineData(heartRateDataSet);
                            heartRateLineChart.setData(heartRateLineData);
                            heartRateLineChart.invalidate(); // refresh

                        } else if (healthData != null && healthData.getType().equals("bloodPressure")) {
                            String valueString = healthData.getValue().replace(",", ".");
                            try {
                                float bloodPressure = Float.parseFloat(valueString);
                                bloodPressureEntries.add(new BarEntry(bloodindexOffset, bloodPressure));
                                bloodindexOffset += 1;
                                System.out.println("Blood pressure is: " + healthData.getValue());
                            } catch (NumberFormatException e) {
                                System.out.println("Error parsing blood pressure value: " + healthData.getValue());
                            }
                            BarDataSet bloodPressureDataSet = new BarDataSet(bloodPressureEntries, "Blood Pressure");
                            BarData bloodPressureBarData = new BarData(bloodPressureDataSet);
                            bloodPressureBarChart.setData(bloodPressureBarData);
                            bloodPressureDataSet.setColors(ColorTemplate.rgb("FFCE59"));
                            bloodPressureBarChart.setBorderColor(ColorTemplate.rgb("656853"));
                            bloodPressureBarChart.invalidate(); // refresh
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Failed to read value.");
                }
            });
            DatabaseReference pillIntakeDataReference = reference.child("users").child(specificDate);
            pillIntakeDataReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        PillIntake pillIntake = child.getValue(PillIntake.class);
                        if (pillIntake != null) {
                            if (pillIntake.getMedicationName().equals("Aspirin")){
                                if (pillIntake.isTaken()){
                                    AspirinTaken++;
                                }else {
                                    AspirinNotTaken++;
                                }
                            }else if (pillIntake.getMedicationName().equals("Ibuprofen")){
                                if (pillIntake.isTaken()){
                                    IbuprofenTaken++;
                                }else {
                                    IbuprofenNotTaken++;
                                }
                            }
                        }
                    }
                    setupPieChart();
                    loadPieChartData();
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    System.out.println("Failed to read value.");
                }
            });

        }
        // Weight TextView
        Intent intent = getIntent();
        int weight = intent.getIntExtra("weight", 0);
        TextView weightTextView = findViewById(R.id.weightTextView);
        weightTextView.setText(weight + "kg");
        findViewById(R.id.dataimport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputDataActivity.class);
                startActivity(intent);
            }
        });

        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EducationalActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setupPieChart() {
        // Configure pie chart settings
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
    }

    private void loadPieChartData() {
        List<PieEntry> entries = new ArrayList<>();
        // Add data entries for pills taken
        entries.add(new PieEntry(AspirinTaken, "Aspirin Taken"));
        entries.add(new PieEntry(IbuprofenTaken, "Ibuprofen Taken"));
        PieDataSet dataSet = new PieDataSet(entries, "Pills Intake");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}