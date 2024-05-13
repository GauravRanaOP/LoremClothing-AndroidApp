package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private Button register;
    private Button signIn;
    private TextView emailview;
    private TextView passwordview;
    private TextView confirmPassView;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        register = findViewById(R.id.registerButton);
        signIn = findViewById(R.id.signInButton);
        emailview = findViewById(R.id.email);
        passwordview = findViewById(R.id.password);
        confirmPassView = findViewById(R.id.confirmPassword);
        progressBar = findViewById(R.id.progressBar);


        signIn.setOnClickListener(v-> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });



        register.setOnClickListener(v -> {
            String email = String.valueOf(emailview.getText());
            String password = String.valueOf(passwordview.getText());
            String confirmPass = String.valueOf(confirmPassView.getText());

            progressBar.setVisibility(View.VISIBLE);
            if(TextUtils.isEmpty(email)){
                Toast.makeText(RegisterActivity.this, "Enter the Email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(RegisterActivity.this, "Enter the Password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(confirmPass)){
                Toast.makeText(RegisterActivity.this, "Enter the Confirm Password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if(!confirmPass.equals(password)){
                Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if(password.length() < 6){
                Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if(!password.matches(".*[A-Z].*")){
                Toast.makeText(RegisterActivity.this, "Password should contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if(!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")){
                Toast.makeText(RegisterActivity.this, "Password should contain at least one special character", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE); // Hide progress bar after completion
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
        });
    }
}