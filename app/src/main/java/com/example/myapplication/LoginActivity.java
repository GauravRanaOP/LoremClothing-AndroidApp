package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button signIn;
    private Button register;
    private TextView emailView;
    private TextView passwordView;
    private Switch remember;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        signIn = findViewById(R.id.signInButtonLogin);
        register = findViewById(R.id.registerButtonLogin);
        emailView = findViewById(R.id.emailLogin);
        passwordView = findViewById(R.id.passwordLogin);
//        remember = findViewById(R.id.rememberSwitch);
        progressBar = findViewById(R.id.progressBar2);

//        remember.setOnClickListener(v -> {
//            boolean remb = remember.isChecked();
//            if(remb){
//                 user = FirebaseAuth.getInstance().getCurrentUser();
//            }
//        });

        signIn.setOnClickListener(v -> {
            String email = String.valueOf(emailView.getText());
            String password = String.valueOf(passwordView.getText());

            progressBar.setVisibility(View.VISIBLE);

            if(TextUtils.isEmpty(email)){
                Toast.makeText(LoginActivity.this, "Enter the Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(LoginActivity.this, "Enter the Password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
//                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Logged In successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        register.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

    }
}