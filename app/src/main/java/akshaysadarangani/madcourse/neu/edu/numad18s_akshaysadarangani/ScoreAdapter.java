package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani.utils.SendNotification;


/**
 * Created by Akshay on 7/3/2018.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.MyViewHolder> {
    public List<User> scoreList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView rank, name, score;
        private ImageView like;

        private MyViewHolder(View view) {
            super(view);
            rank = view.findViewById(R.id.rank);
            name = view.findViewById(R.id.name);
            score = view.findViewById(R.id.score);
            like = view.findViewById(R.id.like);
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final User score = scoreList.get(position);
        holder.rank.setText(Integer.toString(position + 1));
        holder.name.setText(score.name);
        holder.score.setText(Integer.toString(score.score));

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager mgr = new PrefManager(holder.like.getContext());
                String sender = mgr.getUserName();
                SendNotification notif = new SendNotification();
                notif.sendMessageToDevice(score.fID, sender);
                Toast.makeText(holder.like.getContext(), "Congrats sent to " + score.name + "!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}

