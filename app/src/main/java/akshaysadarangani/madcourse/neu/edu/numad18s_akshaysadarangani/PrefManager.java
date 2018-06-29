package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Akshay on 06/27/18.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "scroggle-howto";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String IS_GAME_SAVED = "IsGameSaved";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setGameSave(boolean save) {
        editor.putBoolean(IS_GAME_SAVED, save);
        editor.commit();
    }

    public  boolean isGameSaved() { return pref.getBoolean(IS_GAME_SAVED, false); }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}