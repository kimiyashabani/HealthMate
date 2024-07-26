package com.example.healthmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.location.Geocoder;
import android.location.Address;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.Manifest;
import android.location.Location;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements OnItemClickListener{
    // CONFIGURATION
    DatabaseReference locationReference;
    FirebaseDatabase db;

    DatabaseReference healthReference;
    private static final String EMERGENCY_NUMBER = "+34614140621";
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    Button askAIBtn;

    private ArrayList<CategoryItem> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        locationReference = db.getReference("Location");
        categoryList=  new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        RecyclerView recyclerView = findViewById(R.id.overview_recyclerview);
        askAIBtn = findViewById(R.id.askai);
        // Create and set the LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<OverviewItem> listItems = new ArrayList<>();
        listItems.add(new OverviewItem(R.drawable.heartrate, "Heart Rate", "100"));
        listItems.add(new OverviewItem(R.drawable.bloodpressure, "Blood Pressure", "122"));
        OverviewAdapter adapter = new OverviewAdapter(this, listItems);
        recyclerView.setAdapter(adapter);
        //CHECK IF THE APP HAS LOCATION PERMISSION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // REQUEST LOCATION PERMISSION
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        askAIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EducationalActivity.class);
                startActivity(intent);
            }
        });

        initRecyclerView();
        saveUserLocation();
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
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                locationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                            if (addresses != null && !addresses.isEmpty()) {
                                                String city = addresses.get(0).getLocality();
                                                String country = addresses.get(0).getCountryName();
                                                UserLocation userLocation = new UserLocation(latitude, longitude, city, country);
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                String formattedNow = sdf.format(new Date());
                                                locationReference.child(formattedNow).push().setValue(userLocation);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
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
    //CHECK IF THE USER IS FEELING SHORTNESS OF BREATH AND SENDING ALARM
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
    // CHECK IF THE APP HAS THE CALL PERMISSION
    private void checkCallingPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            callEmergencyNumber();
        }
    }
    // EMERGENCY CALL
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

    private void initRecyclerView(){

        categoryList.add(new CategoryItem("Heart rate", R.drawable.heartrate,R.color.heartrate));
        categoryList.add(new CategoryItem("Sleep",R.drawable.sleep,R.color.sleep));
        categoryList.add(new CategoryItem("Medication",R.drawable.medicine,R.color.medicine));
        categoryList.add(new CategoryItem("Weight",R.drawable.scales,R.color.weight ));
        categoryList.add(new CategoryItem("Blood Pressure",R.drawable.bloodpressure,R.color.bloodpressure ));
        categoryList.add(new CategoryItem("Temperature",R.drawable.thermometer,R.color.temperature ));

        RecyclerView recyclerView = findViewById(R.id.healthMetricsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryList,this);
        recyclerView.setAdapter(categoryAdapter);
    }

    @Override
    public void onItemClick(int position){

        CategoryItem clickedItem = categoryList.get(position);
        Intent intent ;
        String categoryName = clickedItem.getCategory();
        if (categoryName.equals("Heart rate")) {
             intent = new Intent(this, HeartRateActivity.class);

        }else if (categoryName.equals("Weight")) {
             intent = new Intent(this, WeightActivity.class);

        }else if (categoryName.equals("Medication")) {
             intent = new Intent(this, MedicationActivity.class);
        }else if (categoryName.equals("Blood Pressure")) {
             intent = new Intent(this, BloodPressureActivity.class);
        } else if (categoryName.equals("Temperature")) {
             intent = new Intent(this, TemperatureActivity.class);
        } else {
                intent = new Intent(this, MainActivity.class);

        }
        startActivity(intent);
    }
}