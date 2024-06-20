package com.example.healthmate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//CAMERA
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.widget.Toast;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
// Importing FireBase
import com.example.healthmate.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.FirebaseApp;

import com.google.android.material.appbar.MaterialToolbar;

public class HeartRateActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;
    private SurfaceView preview;
    private SurfaceHolder previewHolder;
    private Camera camera;
    private boolean isHeartRateMeasurementRunning = false;
    private long startTime = 0;
    private ArrayList<Long> redIntensities;
    private Button startMeasurementButton;
    private TextView heartRateTextView;
    private static final int CAMERA_PERMISSION_CODE = 200;
    private ProgressBar progressBar;
    private TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // The surface is created. You can now tell the camera where to draw the preview.
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // The surface has changed size. Update the camera preview size.
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(width, height);
                camera.setParameters(parameters);
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // The surface is about to be destroyed. Stop the camera preview.
                camera.stopPreview();
            }
        };
        preview = findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                // The Surface has been created, acquire the camera and tell it where to draw.
                camera = Camera.open();
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        startMeasurementButton = findViewById(R.id.startMeasurementButton);
        heartRateTextView = findViewById(R.id.heartRateText);
        progressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.timerText);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("HealthData");
        startMeasurementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHeartRateMeasurement();
            }
        });
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optionally, add any custom logic here if needed
    }
    private void startHeartRateMeasurement() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        timerText.setVisibility(View.VISIBLE);
        heartRateTextView.setVisibility(View.VISIBLE);

        redIntensities = new ArrayList<>();
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(previewHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setPreviewCallback(previewCallback);
        camera.startPreview();
        startTime = System.currentTimeMillis();
        isHeartRateMeasurementRunning = true;
        //Running the measurement for 30 seconds
        new Handler().postDelayed(this::stopHeartRateMeasurement,60000);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / 600);
                progressBar.setProgress(progress);
                timerText.setText(millisUntilFinished / 1000 + " Seconds remaining");
            }
            public void onFinish() {
                timerText.setText("Measurement completed");
            }
        }.start();
    }
    private void stopHeartRateMeasurement() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        isHeartRateMeasurementRunning = false;
        calculateHeartRate();

        progressBar.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);

    }

    private void calculateHeartRate() {
        if (redIntensities == null || redIntensities.isEmpty()) {
            Toast.makeText(this, "No data collected for heart rate measurement.", Toast.LENGTH_SHORT).show();
            return;
        }
        long sum= 0;
        for (Long redIntensity : redIntensities) {
            sum += redIntensity;
        }
        long averageRedIntensity = sum / redIntensities.size();
        // Detect peaks using a threshold
        long threshold = (long) (averageRedIntensity * 0.7); //0.7
        List<Long> peaks = new ArrayList<>();
        for (int i = 1; i < redIntensities.size() - 1; i++) {
            if (redIntensities.get(i) > redIntensities.get(i - 1) &&
                    redIntensities.get(i) > redIntensities.get(i + 1) &&
                    redIntensities.get(i) > threshold){
                peaks.add((long)(startTime + (i * 1000 / redIntensities.size())));
            }
        }
        if (peaks.size() < 2) {
            Toast.makeText(this, "Not enough peaks detected for heart rate measurement.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Calculating the average time between peaks in milliseconds
        long sumOfDifferences = 0;
        for (int i = 1; i < peaks.size(); i++) {
            sumOfDifferences += (peaks.get(i) - peaks.get(i - 1));
        }
        long averageTimeBetweenPeaks = sumOfDifferences / (peaks.size() - 1);
        // calculating heartrate based on the red intensities
        //float heartRate = (60000.0f / (averageTimeBetweenPeaks * measurementDurationInMillis)) * redIntensities.size();
        float heartRate = peaks.size()/(60.0f)*100;
        //float heartRate = (60000.0f / averageTimeBetweenPeaks);
        String heartRateString = String.format("%.1f", heartRate);
        heartRateTextView.setText("Heart rate: " + Math.round(heartRate) + " bpm");
        heartRateTextView.setVisibility(View.VISIBLE);

        // Saving the data to Firebase
        saveDataToFirebase("1", "heartrate", heartRateString);
    }
    private long calculateThreshold(ArrayList<Long> redIntensities) {
        long sum = 0;
        for (Long redIntensity : redIntensities) {
            sum += redIntensity;
        }
        long average = sum / redIntensities.size();
        return average;
    }
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (!isHeartRateMeasurementRunning) {
                return;
            }

            Camera.Size size = camera.getParameters().getPreviewSize();
            int width = size.width;
            int height = size.height;

            // Convert the NV21 image format to RGB
            int frameSize = width * height;
            int[] rgb = new int[frameSize];
            decodeYUV420SP(rgb, data, width, height);

            // Calculate the average red intensity
            long redSum = 0;
            for (int i = 0; i < rgb.length; i++) {
                redSum += (rgb[i] >> 16) & 0xFF;
            }
            long redAverage = redSum / rgb.length;

            redIntensities.add(redAverage);
        }
    };
    private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // No-op
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (camera != null) {
                camera.stopPreview();
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // No-op
        }
    };

    public void saveDataToFirebase(String userId, String type, String value) {
        HealthData healthData = new HealthData(type, value);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        reference.child("users").child(userId).child(currentDate).push().setValue(healthData);
    }
}
