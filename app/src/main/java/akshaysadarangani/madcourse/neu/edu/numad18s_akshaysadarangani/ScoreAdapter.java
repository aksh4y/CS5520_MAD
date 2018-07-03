package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Akshay on 7/3/2018.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.MyViewHolder> {
    public List<User> scoreList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView rank, name, score;

        private MyViewHolder(View view) {
            super(view);
            rank = view.findViewById(R.id.rank);
            name = view.findViewById(R.id.name);
            score = view.findViewById(R.id.score);
        }
    }

    public ScoreAdapter(List<User> scoreList) {
        this.scoreList = scoreList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        User score = scoreList.get(position);
        holder.rank.setText(Integer.toString(position + 1));
        holder.name.setText(score.name);
        holder.score.setText(Integer.toString(score.score));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}

