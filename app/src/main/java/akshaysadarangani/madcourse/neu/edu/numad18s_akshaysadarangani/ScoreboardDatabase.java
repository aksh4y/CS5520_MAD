package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "scoreboard_db";
    public static final String SCOREBOARD_TABLE_NAME = "scoreboard";
    public static final String SCOREBOARD_COLUMN_ID = "_id";
    public static final String SCOREBOARD_COLUMN_NAME = "name";
    public static final String SCOREBOARD_COLUMN_SCORE = "score";
    public static final String SCOREBOARD_COLUMN_TIMESTAMP = "timestamp";

    public ScoreboardDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                lang.setId(Integer.parseInt(cursor.getString(0)));
                lang.setName(cursor.getString(1));
                lang.setScore(cursor.getInt(2));
                /*lang.setTimestamp();*/
                languageList.add(lang);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return languageList;
    }
}