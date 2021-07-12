package com.socialgaming.androidtutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //final TextView idView = findViewById(R.id.idView);
        //final EditText nickNameEdit = findViewById(R.id.nickNameEdit);
        //final EditText descriptionEdit = findViewById(R.id.descriptionEdit);
        final TextInputLayout id = findViewById(R.id.idView);
        final EditText idView = id.getEditText();
        final TextInputLayout nickName = findViewById(R.id.nickNameEdit);
        final EditText nickNameEdit = nickName.getEditText();
        final TextInputLayout descrView = findViewById(R.id.descriptionEdit);
        final EditText descriptionEdit = descrView.getEditText();


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
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        final Button saveChanges = findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(v -> {
            String preferred = nickNameEdit.getText().toString();
            HTTPGetter check = new HTTPGetter();
            check.execute("check", preferred, "checkNickname");
            try {
                if (!gson.fromJson(check.get(), Boolean.class) && !user.nickName.equals(nickNameEdit.getText().toString())) {
                    Toast.makeText(EditProfileActivity.this, "Nickname is already taken, please choose another one", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            user.nickName = preferred;
            user.description = descriptionEdit.getText().toString();
            new HTTPPoster().execute(
                    "user",
                    Uri.encode(gson.toJson(user, User.class)),//necessary to escape "unsafe" characters, otherwise error in play framework
                    "update");
            Intent returnIntent = new Intent(EditProfileActivity.this, MyProfileActivity.class);
            startActivity(returnIntent);
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(Build.VERSION.SDK) > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, MyProfileActivity.class);
        startActivity(intent);
    }
}