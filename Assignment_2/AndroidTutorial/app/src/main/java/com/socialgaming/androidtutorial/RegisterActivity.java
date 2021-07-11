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
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth myAuth;
    private static final String TAG = "RegisterActivity";
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myAuth = FirebaseAuth.getInstance();

        //final EditText emailText = findViewById(R.id.emailEdit);
        //final EditText passwordText = findViewById(R.id.passwordEdit);
        final Button registerButton = findViewById(R.id.register);
        final TextInputLayout emailView = findViewById(R.id.emailEdit);
        final TextInputLayout passwordView = findViewById(R.id.passwordEdit);
        final EditText emailText = emailView.getEditText();
        final EditText passwordText = passwordView.getEditText();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(emailText.getText().toString(), passwordText.getText().toString());
            }
        });
    }

    private void register(String email, String password) {
        final TextInputLayout nickNameView = findViewById(R.id.nickNameText);
        final EditText nicknameText = nickNameView.getEditText();
        String preferred = nicknameText.getText().toString();
        HTTPGetter check = new HTTPGetter();
        check.execute("check", preferred, "checkNickname");
        try {
            if (!gson.fromJson(check.get(), Boolean.class)) {
                Toast.makeText(RegisterActivity.this, "Nickname is already taken, please choose another one", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    new HTTPPoster().execute(
                            "user",
                            FirebaseAuth.getInstance().getUid(),
                            nicknameText.getText().toString(),
                            "prepare");
                    Log.d(TAG, "createdUser:\tsuccess");
                    Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "createdUser:\tno success");
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}