package com.socialgaming.androidtutorial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final Button login = findViewById(R.id.login_button);
        final Button register = findViewById(R.id.register_button);

        final TextInputLayout emailView = findViewById(R.id.editTextTextEmailAddress);
        final TextInputLayout passwordView = findViewById(R.id.editTextTextPassword);

        final EditText emailText = emailView.getEditText();
        final EditText passwordText = passwordView.getEditText();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                if (email == null || email.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else if (password == null || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
                } else
                    signIn(emailText.getText().toString(), passwordText.getText().toString());
            }
        });

        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            Log.d(TAG, "signedInUser:\t" + currentUser);
            Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
            new HTTPPoster().execute(
                    "user",
                    FirebaseAuth.getInstance().getUid(),
                    "prepare");
        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "loggedInUser: success");
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "loggedInUser: no success");
                    Toast.makeText(LoginActivity.this, "LogIn failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}