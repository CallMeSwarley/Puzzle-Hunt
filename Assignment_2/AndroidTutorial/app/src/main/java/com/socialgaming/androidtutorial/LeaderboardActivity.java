package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Adapters.LeaderboardAdapter;
import com.socialgaming.androidtutorial.Adapters.SetListAdapter;
import com.socialgaming.androidtutorial.Models.Friendship;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.SetViewItem;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private LeaderboardAdapter mRecyclerAdapter;
    List<SetViewItem> items = new ArrayList<>();
    ArrayList<User> users = new ArrayList<User>();
    String name = "",xp = "";
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        mRecyclerView = (RecyclerView) findViewById(R.id.leaderboard_recyclerview);
        mRecyclerAdapter = new LeaderboardAdapter(users);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        fetchUsers();
        sortUsersByXp();
        mRecyclerAdapter.notifyData(users);

    }

    private void fetchUsers() {
        HTTPGetter get = new HTTPGetter();
        get.execute("user", "getAll");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                User[]userArr= gson.fromJson(getUserResult, User[].class);
                for (User user : userArr){
                    users.add(user);
                }

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sortUsersByXp(){
        Collections.sort(users);
    }
}