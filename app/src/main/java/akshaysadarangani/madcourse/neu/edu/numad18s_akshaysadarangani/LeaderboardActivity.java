package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    DatabaseReference myRef;
    RecyclerView recyclerView;
    ScoreAdapter mAdapter;
    ProgressBar progressBar;
    List<User> scoreList = new ArrayList<>();
    String userName, userID, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userID = intent.getStringExtra("uid");
        email = intent.getStringExtra("email");

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mAdapter = new ScoreAdapter(scoreList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("scores");
        //readScores();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (mAdapter.getItemCount() > 0) {
                    scoreList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String fID = postSnapshot.child("fID").getValue().toString();
                        String name = postSnapshot.child("name").getValue().toString();
                        String email = postSnapshot.child("email").getValue().toString();
                        int score = Integer.parseInt(postSnapshot.child("score").getValue().toString());
                        User w = new User(fID, name, email, score);
                        scoreList.add(w);
                        Collections.sort(scoreList, Collections.reverseOrder(new Comparator<User>() {
                            @Override
                            public int compare(User o1, User o2) {
                                return o1.getScore() - o2.getScore();
                            }
                        }));
                        mAdapter.notifyItemInserted(scoreList.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Log.e("Leaderboard", "Failed to read app title value.", error.toException());
            }
        });
    }
}
