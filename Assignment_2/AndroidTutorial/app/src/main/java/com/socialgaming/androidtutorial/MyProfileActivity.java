package com.socialgaming.androidtutorial;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MyProfileActivity extends AppCompatActivity {
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        final ImageView profilePic = findViewById(R.id.profile_pic_image);
        profilePic.setImageResource(R.drawable.profile_pic2);

        final TextInputLayout idView = findViewById(R.id.id_textView);
        final EditText id = idView.getEditText();

        final TextInputLayout nameView = findViewById(R.id.name_textView);
        final EditText name = nameView.getEditText();

        final TextInputLayout xpView = findViewById(R.id.xp_textView);
        final EditText xp = xpView.getEditText();

        final TextInputLayout lvlView = findViewById(R.id.lvl_textView);
        final EditText lvl = lvlView.getEditText();

        final TextInputLayout descriptionView = findViewById(R.id.description_textView);
        final EditText description = descriptionView.getEditText();


        final Button editProfile = findViewById(R.id.edit_profile_button);
        HTTPGetter get = new HTTPGetter();
        get.execute("user", FirebaseAuth.getInstance().getUid(), "getUser");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                User user = gson.fromJson(getUserResult, User.class);
                id.setText(user.id);
                name.setText(user.nickName);
                xp.setText(String.format(Locale.GERMANY, "%,d", user.xp));
                description.setText(user.description);
                lvl.setText(String.format(Locale.GERMANY, "%,d", user.xp));//todo xp in lvl umwandeln
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
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
        Intent intent = new Intent(MyProfileActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }
}