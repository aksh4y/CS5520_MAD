package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani.utils.SendNotification;

public class ScoreboardDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "scoreboard_db";
    public static final String SCOREBOARD_TABLE_NAME = "scoreboard";
    public static final String SCOREBOARD_COLUMN_ID = "_id";
    public static final String SCOREBOARD_COLUMN_NAME = "name";
    public static final String SCOREBOARD_COLUMN_SCORE = "score";
    public static final String SCOREBOARD_COLUMN_TIMESTAMP = "timestamp";
    private Context c;

    public ScoreboardDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + SCOREBOARD_TABLE_NAME + " (" +
                SCOREBOARD_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCOREBOARD_COLUMN_NAME + " TEXT, " +
                SCOREBOARD_COLUMN_SCORE + " INT, " +
                SCOREBOARD_COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SCOREBOARD_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // Getting All data
    public List<Score> getAllvalues() {
        List<Score> languageList = new ArrayList<Score>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + SCOREBOARD_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Score lang = new Score();
                lang.setId(cursor.getString(0));
                lang.setName(cursor.getString(1));
                lang.setScore(cursor.getInt(2));
                /*lang.setTimestamp();*/
                languageList.add(lang);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return languageList;
    }

    public List<String> readScores() {
        ScoreboardDatabase db = new ScoreboardDatabase(c);
        List<Score> val = db.getAllvalues();
        List<String> scores = new ArrayList<>();
        for (Score cn : val) {
           /* String log = "Id: " + cn.getId() + " ,values: "
                    + cn.getName() + ", " + cn.getScore() + ", " + cn.getTimestamp();*/
            scores.add(Integer.toString(cn.getScore()));
        }
        Collections.reverse(scores);
        return scores;
    }

    public void deleteScore(int score) {
        //String deleteQuery = "DELETE FROM " + SCOREBOARD_TABLE_NAME + " WHERE " + SCOREBOARD_COLUMN_SCORE + " = " + score;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SCOREBOARD_TABLE_NAME, SCOREBOARD_COLUMN_SCORE + "=" + score, null);
        db.close();
    }

    public void updateScoreToFirebase(int score) {
        PrefManager prefManager = new PrefManager(c);
        String uid = prefManager.getUID();
        DatabaseReference myRef;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("scores");
        if(uid != null)
            myRef.child(uid).child("score").setValue(score)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            SendNotification notif = new SendNotification();
                            notif.sendMessageToGlobal();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            // ...
                        }
                    });
    }
}