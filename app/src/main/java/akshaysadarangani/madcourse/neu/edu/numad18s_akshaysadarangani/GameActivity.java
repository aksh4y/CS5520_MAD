package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    public boolean PHASE2 = false;
    private boolean VOLUME_ON = true;
    public boolean PAUSED = false;
    private GameFragment mGameFragment;
    public int timer = 90;
    public CountDownTimer cTimer;
    private TextView tv;
    public TextView sV;
    public MediaPlayer mMediaPlayer;
    PrefManager pref;
    public String gameData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        pref = new PrefManager(this);
        // Restore game here...
        mGameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        else
            pref.setGameSave(false);
        tv = findViewById(R.id.timer);
        timerStart(timer * 1000);
        Log.d("Scroggle", "restore = " + restore);
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void playMusic() {
        if(mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = MediaPlayer.create(this, R.raw.bg_loop);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    public void timerStart(long duration) {
        cTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                if(timer <= 10) {
                    tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    if(timer == 9 && VOLUME_ON) {
                        try {
                                mMediaPlayer.stop();
                                mMediaPlayer.reset();
                                mMediaPlayer.release();
                        }
                        catch (IllegalStateException e) {
                            //mMediaPlayer = MediaPlayer.create(getApplication(), R.raw.timer);
                        }
                        mMediaPlayer = MediaPlayer.create(getApplication(), R.raw.timer);
                        mMediaPlayer.start();
                    }
                }
                tv.setText("TIME LEFT 0:" + checkDigit(timer));
                if(!PAUSED) timer--;
            }
            public void onFinish() {
                tv.setText("TIME UP!");
                //makeTilesUnavailable(rootView);
                if(!PHASE2)
                    finishPhase1();
                else
                    finishGame();
                cancel();
            }

        }.start();
    }

    public void restartGame() {
        if(cTimer != null)
            cTimer.cancel();
        try {
            playMusic();
        } catch (Exception e) {}
        timer = 90;
        mGameFragment.restartGame();
        tv.setTextColor(getResources().getColor(android.R.color.white));
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(mGameFragment)
                .commit();
        timerStart(timer * 1000);
    }

    public void setScore(int score) {
        sV = findViewById(R.id.score);
        sV.setText("SCORE: " + Integer.toString(score));
    }

    public void toggleVolume() {
        ImageButton volume = findViewById(R.id.button_volume_on);
        VOLUME_ON = !VOLUME_ON;
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                volume.setImageResource(R.drawable.volume_off);
                mMediaPlayer.pause();
            }
            else {
                volume.setImageResource(R.drawable.volume_on);
                mMediaPlayer.start();
            }
        }
        catch (Exception e) {
            mMediaPlayer = MediaPlayer.create(getApplication(), R.raw.timer);
        }
    }

    public void pauseGame() {
        FragmentManager fm = getFragmentManager();
        ImageButton pButton = findViewById(R.id.button_pause);
        try {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.pause();
        }
        catch (Exception e) {

        }
        if(mGameFragment.isVisible()) { // Pause
            cTimer.cancel();
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .hide(mGameFragment)
                    .commit();
            pButton.setImageResource(R.drawable.play);
        }
        else {  // Resume
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(mGameFragment)
                    .commit();
            pButton.setImageResource(R.drawable.pause);
            if(VOLUME_ON)
                mMediaPlayer.start();
            timerStart(timer * 1000);
        }
    }

    public void finishPhase1() {
        try {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            cTimer.cancel();
        }
        catch (Exception e) {

        }
        mGameFragment.removeIncompleteTiles();
        PHASE2 = true;
        if(timer > 10) {
            sV = findViewById(R.id.score);
            String s = sV.getText().toString().substring(6).trim();
            int score = 0;
            if(!s.isEmpty())
                score = Integer.parseInt(s);
            score += 5; // 5 bonus points for finishing quick
            setScore(score);
            Toast.makeText(getApplicationContext(), "Good Job! Bonus Points Earned For Finishing Quick.", Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Phase 2 Begins!\nEarn Double Points Here\nReuse letters from different tiles in your words");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                        // start phase 2
                        mGameFragment.setAvailableForPhase2();
                        if(VOLUME_ON)
                            playMusic();
                        timer = 30;
                        tv.setTextColor(getResources().getColor(android.R.color.white));
                        timerStart(timer * 1000);
                    }
                });
        final Dialog dialog = builder.create();
        dialog.show();

        // Reset the board to the initial position
        mGameFragment.initGame();

    }

    @Override
    protected  void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PAUSED = true;
        try {
            cTimer.cancel();
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        catch (Exception e) {
            //mMediaPlayer.release();
        }
        pref.setGameSave(true);
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("UT3", "state = " + gameData);
    }

    private void finishGame() {
        if(mMediaPlayer != null)
            mMediaPlayer.release();
        sV = findViewById(R.id.score);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Game Over!\n" + sV.getText() + " points.");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        final Dialog dialog = builder.create();
        dialog.show();
        PrefManager pref = new PrefManager(this);
        pref.setGameSave(false);
    }
}
