package com.socialgaming.androidtutorial;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

        final TextView id = findViewById(R.id.id_textView);
        final TextView name = findViewById(R.id.name_textView);
        final TextView xp = findViewById(R.id.xp_textView);
        final TextView lvl = findViewById(R.id.lvl_textView);

        final TextView description = findViewById(R.id.description_textView);
        final TextView descriptionText = findViewById(R.id.description_text_textView);
        final TextView achievement = findViewById(R.id.achievement_textView);

        //können in ne liste umgewandelt werden oder so, je nach geschmack, ist nur als platzhalter da
        final ImageView achievement1 = findViewById(R.id.achievement1_image);
        final ImageView achievement2 = findViewById(R.id.achievement2_image);

        final TextView preferences = findViewById(R.id.preferences_textView);
        final RecyclerView preferencesList = findViewById(R.id.preferences_recyclerView);
        final Button editPreferences = findViewById(R.id.remove_friend_button);

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
                descriptionText.setText(user.description);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        editPreferences.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, EditPreferencesActivity.class);
            startActivity(intent);
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
}