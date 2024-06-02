package com.example.healthmate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.analytics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Button 1 click
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
}