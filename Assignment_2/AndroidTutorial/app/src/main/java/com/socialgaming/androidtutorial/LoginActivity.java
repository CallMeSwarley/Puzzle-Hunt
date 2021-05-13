package com.socialgaming.androidtutorial;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            final Button login = findViewById(R.id.login_button);
            final Button register = findViewById(R.id.register_button);
            final TextView view = findViewById(R.id.text);

            final EditText emailText = findViewById(R.id.editTextTextEmailAddress);
            final EditText passwordText = findViewById(R.id.editTextTextPassword);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn(emailText.getText().toString(), passwordText.getText().toString());
                }
            });
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
            Log.d(TAG, "signedInUser:\t" + currentUser);
            Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "loggedInUser: success");
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "loggedInUser: no success");
                    Toast.makeText(LoginActivity.this, "LogIn failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}