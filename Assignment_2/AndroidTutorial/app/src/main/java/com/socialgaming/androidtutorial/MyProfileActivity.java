package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileActivity extends AppCompatActivity {

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

        final Button editProfile = findViewById(R.id.edit_profile_button);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}