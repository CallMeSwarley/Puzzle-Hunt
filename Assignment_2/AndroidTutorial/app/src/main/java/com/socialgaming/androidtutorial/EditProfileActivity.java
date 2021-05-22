package com.socialgaming.androidtutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {
    private Gson gson = new Gson();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        final TextView idView = findViewById(R.id.idView);
        final EditText nickNameEdit = findViewById(R.id.nickNameEdit);
        final EditText descriptionEdit = findViewById(R.id.descriptionEdit);
        HTTPGetter get = new HTTPGetter();
        get.execute("user", FirebaseAuth.getInstance().getUid(), "getUser");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                user = gson.fromJson(getUserResult, User.class);
                idView.setText(user.id);
                nickNameEdit.setText(user.nickName);
                descriptionEdit.setText(user.description);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Button saveChanges = findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(v -> {
            user.nickName = nickNameEdit.getText().toString();
            user.description = descriptionEdit.getText().toString();
            new HTTPPoster().execute(
                    "user",
                    Uri.encode(gson.toJson(user, User.class)),//necessary to escape "unsafe" characters, otherwise error in play framework
                    "update");
            Intent returnIntent = new Intent(EditProfileActivity.this, MyProfileActivity.class);
            startActivity(returnIntent);
        });

    }
}