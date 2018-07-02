package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        scores = new ArrayList<>();
        scores = readScores();
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(scores);
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }


    public List<String> readScores() {
        ScoreboardDatabase db = new ScoreboardDatabase(getApplicationContext());
        List<Score> val = db.getAllvalues();
        List<String> scores = new ArrayList<>();

        for (Score cn : val) {
           /* String log = "Id: " + cn.getId() + " ,values: "
                    + cn.getName() + ", " + cn.getScore() + ", " + cn.getTimestamp();*/
            scores.add(Integer.toString(cn.getScore()));
        }
        scores.add("20");

        Collections.sort(scores);
        return scores;
    }
}
