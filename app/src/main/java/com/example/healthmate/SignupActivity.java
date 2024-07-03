package com.example.healthmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText name;
    EditText familyName;
    EditText username;
    EditText password;
    Button signup;
    Button login;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = db.getReference("Users");

        name = findViewById(R.id.name);
        familyName = findViewById(R.id.familyName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase(name.getText().toString(), familyName.getText().toString(), username.getText().toString(), password.getText().toString());
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveDataToFirebase(String name, String familyName, String username, String password) {
        User new_user = new User(name, familyName, username, password);
        reference.push().setValue(new_user);
    }

}
