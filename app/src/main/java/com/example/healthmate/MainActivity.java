package com.example.healthmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.ImageButton;
import android.widget.TextView;

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
import com.google.firebase.database.DataSnapshot;
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

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private OpenFDAApi openFDAApi;
    //Firebase configuration
    ActivityMainBinding binding;
    private PieChart pieChart;
    FirebaseDatabase db;
    DatabaseReference reference;
    private boolean isPill1Taken = false;
    private boolean isPill1NotTaken = false;

    private boolean isPill2Taken = false;
    private boolean isPill2NotTaken = false;
    private String pill1IsTakenKey = null;
    private String pill1NotTakenKey = null;
    private String pill2IsTakenKey = null;
    private String pill2NotTakenKey = null;

    private int AspirinTaken = 5;
    private int AspirinNotTaken = 10;
    private int IbuprofenTaken = 15;
    private int IbuprofenNotTaken = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("PillIntake");
        // Retrofit setup
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.fda.gov/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        openFDAApi = retrofit.create(OpenFDAApi.class);
        //getDrugLabels("cardio");
        // Sample data arrays
        float[] heartRateData = {72f, 75f, 78f, 80f, 76f};
        float[] bloodPressureData = {120f, 130f, 125f, 128f, 122f};
        float weightData = 70f;

        pieChart = findViewById(R.id.pie_chart);
        setupPieChart();
        loadPieChartData();

        TextView medication1 = findViewById(R.id.Med1);
        TextView medication2 = findViewById(R.id.Med2);
        medication1.setText("Aspirin");
        medication2.setText("Ibuprofen");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // First Medication
        ImageButton pill_1_is_taken = findViewById(R.id.pills1_taken);
        ImageButton pill_1_not_taken = findViewById(R.id.pills1_not_taken);
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
                }else {
                    PillIntake pillIntake = new PillIntake("Aspirin", true);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill1IsTakenKey = newEntryRef.getKey();
                    pill_1_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_rounded_button));
                    pill_1_not_taken.setEnabled(false);
                }
                isPill1Taken = !isPill1Taken;
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
                }else {
                    PillIntake pillIntake = new PillIntake("Aspirin", false);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill1NotTakenKey = newEntryRef.getKey();
                    pill_1_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.deselected_rounded_button));
                    pill_1_is_taken.setEnabled(false);

                }
               isPill1NotTaken =! isPill1NotTaken;
            }
        });

        // Second Medication
        ImageButton pill_2_is_taken = findViewById(R.id.pills2_taken);
        ImageButton pill_2_not_taken = findViewById(R.id.pills2_not_taken);
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
                }else {
                    PillIntake pillIntake = new PillIntake("Ibuprofen", true);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill2IsTakenKey = newEntryRef.getKey();
                    pill_2_is_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_rounded_button));
                    pill_2_not_taken.setEnabled(false);
                }
                isPill2Taken = !isPill2Taken;
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
                }else {
                    PillIntake pillIntake = new PillIntake("Ibuprofen", false);
                    DatabaseReference newEntryRef = reference.child("users").child(currentDate).push();
                    newEntryRef.setValue(pillIntake);
                    pill2NotTakenKey = newEntryRef.getKey();
                    pill_2_not_taken.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.deselected_rounded_button));
                    pill_2_is_taken.setEnabled(false);

                }
                isPill2NotTaken =! isPill2NotTaken;
            }
        });

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
        Intent intent = getIntent();
        int weight = intent.getIntExtra("weight", 0);
        TextView weightTextView = findViewById(R.id.weightTextView);
        weightTextView.setText(weight + "kg");
        String specificDate = "2024-06-06";
        DatabaseReference myDataReference = reference.child("users").child(specificDate);

        myDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String value = String.valueOf(dataSnapshot.getValue(PillIntake.class));
                //System.out.println("Value is: " + value);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    PillIntake pillIntake = child.getValue(PillIntake.class);
                    if (pillIntake != null) {
                        //System.out.println("Medication Name: " + pillIntake.getMedicationName());
                        //System.out.println("Taken: " + pillIntake.isTaken());
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value.");
            }
        });
        findViewById(R.id.dataimport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputDataActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setupPieChart() {
        // Configure pie chart settings
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        // More configuration...

        // Optional: Customize the legend
        // Legend legend = pieChart.getLegend();
        // More customization...
    }

    private void loadPieChartData() {
        List<PieEntry> entries = new ArrayList<>();

        // Add data entries for pills taken
        entries.add(new PieEntry(AspirinTaken, "Aspirin Taken"));
        entries.add(new PieEntry(IbuprofenTaken, "Ibuprofen Taken"));

        // Add data entries for pills not taken
        entries.add(new PieEntry(AspirinNotTaken, "Aspirin Not Taken"));
        entries.add(new PieEntry(IbuprofenNotTaken, "Ibuprofen Not Taken"));

        PieDataSet dataSet = new PieDataSet(entries, "Pills Intake");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(pieData);
        pieChart.invalidate(); // refresh chart
    }

    /*private void getDrugLabels(String query){
    Call<DrugLabelResponse> call = openFDAApi.getDrugLabels("generic_name:" + query, 10);
    call.enqueue(new Callback<DrugLabelResponse>() {
        @Override
        public void onResponse(Call<DrugLabelResponse> call, Response<DrugLabelResponse> response) {
            if (response.isSuccessful()) {
                DrugLabelResponse drugLabelResponse = response.body();
                if (drugLabelResponse != null) {
                    List<String> drugNames = new ArrayList<>();
                    for (DrugLabelResponse.DrugLabelResult result : drugLabelResponse.results) {
                        drugNames.add(result.openfda.generic_name[0]);
                    }
                    getRandomMedicationNames(drugNames);
                    System.out.println("got drug information successfully");
                }
            } else {
                System.out.println("Failed to get drug information");
            }
        }

        @Override
        public void onFailure(Call<DrugLabelResponse> call, Throwable t) {
            System.out.println("Failed to get drug information: " + t.getMessage());
        }
    });
}
    private void getRandomMedicationNames(List<String> medicationNames){
        if (medicationNames.size() >= 3){
            TextView medication1 = findViewById(R.id.Med1);
            TextView medication2 = findViewById(R.id.Med2);
            medication1.setText(medicationNames.get(0));
            medication2.setText(medicationNames.get(1));
        }
    }*/

}