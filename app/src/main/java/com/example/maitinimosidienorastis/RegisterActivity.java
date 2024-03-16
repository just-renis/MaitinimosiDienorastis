package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, editTextRepeatPassword;
    private Button registerButton;
    private FirebaseAuth auth;
    private ProgressBar registerProgressBar;
    private TextView clickToLoginText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextRepeatPassword = findViewById(R.id.repeatPassword);
        registerButton = findViewById(R.id.registerButton);
        registerProgressBar = findViewById(R.id.registerProgressBar);
        clickToLoginText = findViewById(R.id.loginNow);
        setupRegisterButton();
        setupClickToLoginText();
    }
    private void setupRegisterButton() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                if (canUserBeRegistered(email, password)) registerUser(email, password);
            }
        });
    }
    private void setupClickToLoginText() {
        clickToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private boolean canUserBeRegistered(String email, String password) {
        String repeatPassword = String.valueOf(editTextRepeatPassword.getText());
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Įrašykite el. paštą", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Įrašykite slaptažodį", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Neteisingas el. pašto formatas", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(repeatPassword)) {
            Toast.makeText(RegisterActivity.this, "Įrašykite slaptažodžio pakartojimą", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(repeatPassword)) {
            Toast.makeText(RegisterActivity.this, "Slaptažodžiai nesutampa", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Slaptažodis turi būti nors 6 simbolių", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void registerUser(String email, String password) {
        registerProgressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                registerProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    sendVerificationEmail();
                    Toast.makeText(RegisterActivity.this, "Paskyra sukurta, patvirtinkite el. paštu", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else Toast.makeText(RegisterActivity.this, "Nepavyko sukurti paskyros", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendVerificationEmail() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) Toast.makeText(RegisterActivity.this, "Nepavyko išsiųsti patvirtinimo laiško", Toast.LENGTH_SHORT).show();
                }
            });
    }
}