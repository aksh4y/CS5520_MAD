package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private GameFragment mGameFragment;
    private int timer = 90;
    private CountDownTimer cTimer;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Restore game here...
        mGameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        tv = findViewById(R.id.timer);
        timerStart(timer * 1000);
        Log.d("UT3", "restore = " + restore);
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void timerStart(long duration) {
        cTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                if(timer <= 10)
                    tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tv.setText("TIME LEFT 0:" + checkDigit(timer));
                timer--;
            }
            public void onFinish() {
                tv.setText("TIME UP!");
                //makeTilesUnavailable(rootView);
                finishPhase1();
                cancel();
            }

        }.start();
    }

    public void restartGame() {
        if(cTimer != null)
            cTimer.cancel();
        timer = 90;
        mGameFragment.restartGame();
        tv.setTextColor(getResources().getColor(android.R.color.white));
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(mGameFragment)
                .commit();
        timerStart(timer * 1000);
    }

    public void pauseGame() {
        FragmentManager fm = getFragmentManager();

        if(mGameFragment.isVisible()) {
            cTimer.cancel();
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .hide(mGameFragment)
                    .commit();
        }
        else {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(mGameFragment)
                    .commit();
            timerStart(timer * 1000);
        }
    }

    public void finishPhase1(){//final Tile.Owner winner) {
        cTimer.cancel();

        if(timer > 10) {
            TextView sV = findViewById(R.id.score);
            int score = Integer.parseInt(sV.getText().toString());
            score += 5; // 5 bonus points for finishing quick
            sV.setText(score);
            Toast.makeText(getApplicationContext(), "Good Job! Bonus Points Earned For Finishing Quick.", Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Phase 1 Over! Moving On To Phase 2.");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        // start phase 2
                        mGameFragment.setAvailableForPhase2();
                        timerStart(30000);
                    }
                });
        final Dialog dialog = builder.create();
        dialog.show();

        // Reset the board to the initial position
        mGameFragment.initGame();

    }



    @Override
    protected void onPause() {
        super.onPause();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("UT3", "state = " + gameData);
    }
}
