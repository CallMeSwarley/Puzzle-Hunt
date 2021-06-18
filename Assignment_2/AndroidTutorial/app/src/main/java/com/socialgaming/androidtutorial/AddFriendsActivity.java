package com.socialgaming.androidtutorial;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.socialgaming.androidtutorial.Models.User;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import java.util.concurrent.ExecutionException;

public class AddFriendsActivity extends AppCompatActivity {
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);


        final Button addFriendToList = findViewById(R.id.saveChanges);
        //EditText nicknameFeld = findViewById(R.id.nickNameTextFeld);

        final TextInputLayout nickName = findViewById(R.id.nickNameTextFeld);
        final EditText nicknameFeld = nickName.getEditText();

        addFriendToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nicknameFreund = nicknameFeld.getText().toString();
                if (nicknameFreund == "") {
                    Toast.makeText(AddFriendsActivity.this, "Nickname can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog alertDialog = new AlertDialog.Builder(AddFriendsActivity.this).create();
                alertDialog.setTitle("Add friend");
                alertDialog.setMessage("add " + nicknameFreund + "?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sent friendrequest to " + nicknameFreund + "!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Schauen obs den anderen gibt falls ja neues Freundschaftsobjekt erstellen, falls nein abbruch
                        HTTPGetter get = new HTTPGetter();
                        get.execute("user", nicknameFreund, "getUser");
                        try {
                            String getUserResult = get.get();
                            if (!getUserResult.equals("{ }")) {
                                User user = gson.fromJson(getUserResult, User.class);
                                //Meine Daten aus der DB holen
                                System.out.println(getUserResult);
                                HTTPGetter getMe = new HTTPGetter();
                                getMe.execute("user", FirebaseAuth.getInstance().getUid(), "getUser");
                                try {
                                    String erg = getMe.get();
                                    System.out.println(erg);
                                    if (!erg.equals("{ }")) {
                                        User me = gson.fromJson(erg, User.class);
                                        new HTTPGetter().execute("user",
                                                me.id,
                                                user.id,
                                                "prepareFriend");
                                    }
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //Freund wurde der liste geaddet jetzt können wir zurück zur freundesliste
                                Intent intent = new Intent(AddFriendsActivity.this, FriendsActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(AddFriendsActivity.this, "User " + nicknameFreund + " doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

    }
}