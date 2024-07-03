package com.example.healthmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.example.healthmate.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.Manifest;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
public class MainActivity extends AppCompatActivity {
    //Firebase configuration
    ActivityMainBinding binding;
    private PieChart pieChart;
    FirebaseDatabase db;
    DatabaseReference reference;
    DatabaseReference healthReference;
    DatabaseReference locationReference;
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
    private String specificDataDate;
    private static final String EMERGENCY_NUMBER = "+34614140621";
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("PillIntake");
        healthReference = db.getReference("HealthData");
        locationReference = db.getReference("Location");
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
        LineChart heartRateLineChart = findViewById(R.id.heartRateLineChart);
        BarChart bloodPressureBarChart = findViewById(R.id.bloodPressureBarChart);
        LineChart temperatureLineChart = findViewById(R.id.temperatureLineChart);
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
        ArrayList<Entry> heartRateEntries = new ArrayList<>();
        ArrayList<BarEntry> bloodPressureEntries = new ArrayList<>();
        ArrayList<Entry> temperatureEntries = new ArrayList<>();
        List<String> dates = new ArrayList<>();



        for (int i=0 ; i<30 ; i++) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            specificDate = sdf.format(calendar.getTime());
            DatabaseReference healthDataReference = healthReference.child("users").child("1").child(specificDate);
            DatabaseReference healthDataDateRef = healthReference.child("users").child("1");
            healthDataDateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Entry> heartRateEntries = new ArrayList<>();
                    ArrayList<BarEntry> bloodPressureEntriesForDate = new ArrayList<>();
                    ArrayList<Entry> temperatureEntries= new ArrayList<>();
                    List<String> dates = new ArrayList<>();
                    int index = 0;
                    int bloodIndex = 0;
                    int temperatureIndex = 0;
                    // Iterate through all dates (keys) under the user's node
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String specificDataDate = dateSnapshot.getKey();
                        if (specificDataDate != null) {
                            dates.add(specificDataDate);
                            // Iterate through the health data entries under each date
                            for (DataSnapshot dataSnapshot : dateSnapshot.getChildren()) {
                                HealthData healthData = dataSnapshot.getValue(HealthData.class);
                                if (healthData != null && healthData.getType().equals("heartrate")) {
                                    String valueString = healthData.getValue().replace(",", ".");
                                    try {
                                        float heartValue = Float.parseFloat(valueString);
                                        heartRateEntries.add(new Entry(index, heartValue));
                                    } catch (NumberFormatException e) {
                                        System.out.println("Error parsing heart rate value: " + healthData.getValue());
                                    }
                                }else if (healthData != null && healthData.getType().equals("temperature")) {
                                    String valueString = healthData.getValue().replace(",", ".");
                                    try {
                                        float temperature = Float.parseFloat(valueString);
                                        temperatureEntries.add(new Entry(temperatureIndex, temperature));
                                        temperatureIndex += 1;
                                    } catch (NumberFormatException e) {
                                        System.out.println("Error parsing temperature value: " + healthData.getValue());
                                    }
                                } else if (healthData != null && healthData.getType().equals("bloodPressure")) {
                                    String valueString = healthData.getValue().replace(",", ".");
                                    try {
                                        float bloodPressure = Float.parseFloat(valueString);
                                        bloodPressureEntriesForDate.add(new BarEntry(bloodIndex, bloodPressure));
                                        bloodIndex += 1;
                                    } catch (NumberFormatException e) {
                                        System.out.println("Error parsing blood pressure value: " + healthData.getValue());
                                    }
                                }
                            }
                            index++;
                        }
                    }

                    // Create and set the data set for the chart
                    LineDataSet heartRateDataSet = new LineDataSet(heartRateEntries, "Heart Rate");
                    heartRateDataSet.setColor(ColorTemplate.rgb("FFCE59"));
                    heartRateDataSet.setLineWidth(2);
                    heartRateDataSet.setCircleColor(ColorTemplate.rgb("656853"));

                    LineData heartRateLineData = new LineData(heartRateDataSet);
                    heartRateLineChart.setData(heartRateLineData);

                    // Set the custom formatter for the x-axis
                    XAxis xAxis = heartRateLineChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                    xAxis.setGranularity(1f); // Ensure labels are shown for each date
                    xAxis.setGranularityEnabled(true);

                    heartRateLineChart.invalidate(); // Refresh chart

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
                    xAxisTemp.setGranularity(1f); // Ensure labels are shown for each date
                    xAxisTemp.setGranularityEnabled(true);

                    temperatureLineChart.invalidate(); // refresh

                    //SETTING UP BAR CHART FOR BLOOD PRESSURE
                    bloodPressureEntries.addAll(bloodPressureEntriesForDate);
                    BarDataSet bloodPressureDataSet = new BarDataSet(bloodPressureEntries, "Blood Pressure");
                    BarData bloodPressureBarData = new BarData(bloodPressureDataSet);
                    XAxis xAxisBlood = temperatureLineChart.getXAxis();
                    xAxisBlood.setValueFormatter(new IndexAxisValueFormatter(dates));
                    xAxisBlood.setGranularity(1f); // Ensure labels are shown for each date
                    xAxisBlood.setGranularityEnabled(true);
                    bloodPressureBarChart.setData(bloodPressureBarData);
                    bloodPressureDataSet.setColors(ColorTemplate.rgb("FFCE59"));
                    bloodPressureBarChart.setBorderColor(ColorTemplate.rgb("656853"));
                    bloodPressureBarChart.invalidate(); // refresh
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

        //CHECK IF THE APP HAS LOCATION PERMISSION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // REQUEST LOCATION PERMISSION
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        saveUserLocation();
        //SHOWING WARNING DIALOG FOR HEALTH CHECK
        showWarningDialog();
    }
    // HANDLE LOCATION PERMISSION REQUEST RESULT
    private void saveUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // REQUEST LOCATION PERMISSION
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                locationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        UserLocation userLocation = new UserLocation(latitude, longitude);
                                        locationReference.child(specificDate).push().setValue(userLocation);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println("Error: " + error.getMessage());
                                    }
                                });
                            } else {
                                System.out.println("Location is null");
                            }
                        }
                    });
        }
    }
    private void showWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Health Check")
                .setMessage("Are you feeling shortness of breath?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("User is feeling dizziness or cardio-related symptoms");
                        checkCallingPermission();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void checkCallingPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            callEmergencyNumber();
        }
    }
    private void callEmergencyNumber() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER));
        startActivity(callIntent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callEmergencyNumber();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("Call permission is required to contact the emergency services. Please enable it in settings.")
                        .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openAppSettings();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // PERMISSION GRANTED
                saveUserLocation();
            } else {
                // PERMISSION DENIED
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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