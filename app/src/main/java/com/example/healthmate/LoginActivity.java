package com.example.healthmate;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

public class LoginActivity extends AppCompatActivity{
    private EditText username;
    private EditText password;
    private Button login;
    private Button signup;
    FirebaseDatabase db;
    DatabaseReference loginReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        Intent intent = getIntent();
        db = FirebaseDatabase.getInstance("https://healthmate-37101-default-rtdb.europe-west1.firebasedatabase.app/");
        loginReference = db.getReference("Users");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                if (usernameText.isEmpty()) {
                    username.setError("Please fill in the required fields");
                    username.requestFocus();
                    return;
                } else if (passwordText.isEmpty()) {
                    password.setError("Please fill in the required fields");
                    password.requestFocus();
                    return;
                }
                loginReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean userExists = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User userLogin = userSnapshot.getValue(User.class);
                            if (userLogin != null && userLogin.getName() != null && userLogin.getPassword() != null) {
                                if (userLogin.getUsername().equals(usernameText) && userLogin.getPassword().equals(passwordText)) {
                                    System.out.println("User exists");
                                    userExists = true;
                                    System.out.println(userExists);
                                } else {
                                    System.out.println("User does not exist");
                                }
                            }
                            if (userExists) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Error: " + error.getMessage());
                    }

                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
    private void userLogin(String username, String password){
        loginReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userExists = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User userLogin = userSnapshot.getValue(User.class);
                    if (userLogin != null && userLogin.getName() != null && userLogin.getPassword() != null){
                        if (userLogin.getName().equals(username) && userLogin.getPassword().equals(password))
                        {
                            userExists = true;
                            break;
                        }
                    }
                }
                if(userExists){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               System.out.println("Error: " + error.getMessage());
            }
        });
    }
}
