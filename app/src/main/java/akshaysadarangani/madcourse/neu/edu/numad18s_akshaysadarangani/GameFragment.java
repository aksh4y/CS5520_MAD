package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameFragment extends Fragment {
    // Data structures go here...
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    public boolean PHASE2 = false;
    public boolean RESUME;
    private Tile mEntireBoard = new Tile(this);
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private Set<Tile> mAvailable = new HashSet<>();
    private int mLastLarge;
    private int mLastSmall;
    private int score = 0;
    private ImageButton submit;
    private ImageButton volume;
    private TextView words;
    private TextView scoreView;
    private String word = "";
    private Search search;
    private View view;
    private ArrayList<Tile> list;
    private Set<String> wordList = new HashSet<>();
    private ToneGenerator toneGen1;
    private Vibrator vibe;
    private char letters[][] = new char[9][9];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        search = new Search(inputStream);
        initGame();
        ((GameActivity) getActivity()).playMusic();
        PrefManager pref = new PrefManager(getActivity().getApplicationContext());
        RESUME = pref.isGameSaved();
    }

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void addAvailable(Tile tile) {
        mAvailable.add(tile);
    }

    public boolean isAvailable(Tile tile) {
        return mAvailable.contains(tile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.large_board, container, false);
        view = rootView;
        initViews(rootView);
        updateAllTiles();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews(final View rootView) {      // @TODO: Set up letters here
        mEntireBoard.setView(rootView);
        words = rootView.findViewById(R.id.word);
        words.setText("");
        submit = rootView.findViewById(R.id.button_submit);
        volume = rootView.findViewById(R.id.button_volume_on);
        final ImageButton clear = rootView.findViewById(R.id.button_clear);
        view = rootView;
        WordsGenerator w = new WordsGenerator(this.getActivity().getApplicationContext());

        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            char[] wordLetters;

            if(!RESUME) {
                wordLetters = w.shuffle(w.getRandomWord()).toCharArray();
                buildLettersArray(wordLetters, large);
            }
            else {
                wordLetters = letters[large];
            }
            for (int small = 0; small < 9; small++) {
                final Button inner = outer.findViewById
                        (mSmallIds[small]);
                final int fLarge = large;
                final int fSmall = small;
                final Tile smallTile = mSmallTiles[large][small];
                if(smallTile.ismAvailable() && smallTile.letter == ' ')
                    smallTile.letter = wordLetters[small];
                inner.setText(Character.toString(smallTile.letter));
                smallTile.setView(inner);
                smallTile.small = fSmall;
                smallTile.large = fLarge;
                inner.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                // if(smallTile.ismAvailable())
                inner.setEnabled(true);
                inner.setVisibility(View.VISIBLE);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if (isAvailable(smallTile)) {
                        vibe.vibrate(100);
                        if(smallTile.ismAvailable()) {
                            inner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            word += inner.getText().toString();
                            //smallTile.setmAvailable(false);
                            makeMove(fLarge, fSmall);
                            if(!PHASE2)
                                makeTilesUnavailable(view);
                            else
                                setTileAvailable();
                            //switchTurns();
                        }
                    }
                });
                inner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(isAvailable(smallTile))
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN: {
                                    inner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    v.invalidate();
                                    break;
                                }
                                case MotionEvent.ACTION_UP: {
                                    inner.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    v.getBackground().clearColorFilter();
                                    v.invalidate();
                                    break;
                                }
                            }
                        return false;
                    }
                });

            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout fl = getActivity().findViewById(R.id.frameLayout1);
                LinearLayout item = fl.findViewById(R.id.linearLayout1);
                scoreView = item.findViewById(R.id.score);
                if(word.length() < 3) {
                    Toast.makeText(getActivity().getApplicationContext(), "Make sure word has at least 3 letters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(wordList.contains(word)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Word already scored!", Toast.LENGTH_SHORT).show();
                    shakeBoard(view);
                    setAvailableForPhase2();
                    return;
                }
                if(search.isWord(word)) {
                    try {
                        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150); // beep
                    } catch (Exception e) {
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
                            r.play();
                        } catch (Exception x) {x.printStackTrace();}
                    }
                    wordList.add(word);
                    String str = words.getText().toString() + " " + word.toUpperCase();
                    words.setText(str);
                    if(!PHASE2)
                        lockTile(view);
                    else
                        resetButtonColor();
                    addTileToList();
                    setAllAvailable();
                    makeTilesAvailable(view);
                    mLargeTiles[mLastLarge].setmCompleted(true);
                    if(scoreView.getText().toString().substring(6).trim().equals(""))
                        score = 0;
                    else
                        score = Integer.parseInt(scoreView.getText().toString().substring(6).trim());
                    for(char ch : word.toCharArray()) {
                        if(PHASE2)  // double points in phase 2
                            score += (2 * getPoints(Character.toUpperCase(ch)));
                        else
                            score += getPoints(Character.toUpperCase(ch));
                    }
                    if(word.length() == 9) {  // 5 bonus points for finding longest word
                        score += 5;
                        Toast.makeText(getActivity().getApplicationContext(), "Good Job! Bonus Points Earned.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid word! -2 Points", Toast.LENGTH_SHORT).show();
                    score -= 2;
                    shakeBoard(view);   // shake board
                    try {
                        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,150); // beep
                    } catch (Exception e) {
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
                            r.play();
                        } catch (Exception x) {x.printStackTrace();}
                    }
                }
                String scoreText = "SCORE: " + Integer.toString(score);
                scoreView.setText(scoreText);
                checkBoardOver();
                word = "";
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLastLarge != -1 && !mLargeTiles[mLastLarge].getmCompleted()) {
                    resetTile(view);
                    makeTilesAvailable(view);
                }
                if(PHASE2)
                    resetButtonColor();
            }
        });

        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity) getActivity()).toggleVolume();
            }
        });
    }

    private void buildLettersArray(char[] wordLetters, int large) {
        for(int i = 0; i < 9; i++)
            letters[large][i] = wordLetters[i];
    }

    public void removeIncompleteTiles() {
        for(int i = 0; i < 9; i++) {
            if (!mLargeTiles[i].getmCompleted()) {
                View outer = view.findViewById(mLargeIds[i]);
                for (int j = 0; j < 9; j++) {
                    final Button inner = outer.findViewById(mSmallIds[j]);
                    final Tile smallTile = mSmallTiles[i][j];
                    smallTile.setView(inner);
                    inner.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private  void checkBoardOver() {
        for(int i = 0; i < 9; i++) {
            if (!mLargeTiles[i].getmCompleted())
                return;
        }
        // board is over
        if(!PHASE2)
            ((GameActivity)getActivity()).finishPhase1();
    }

    private void shakeBoard(View view) {
        final float FREQ = 3f;
        final float DECAY = 2f;
        // interpolator that goes 1 -> -1 -> 1 -> -1 in a sine wave pattern.
        TimeInterpolator decayingSineWave = new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                double raw = Math.sin(FREQ * input * 2 * Math.PI);
                return (float)(raw * Math.exp(-input * DECAY));
            }
        };
        if(mLastLarge != -1)
            view.findViewById(mLargeIds[mLastLarge]).animate()
                    .xBy(-100)
                    .setInterpolator(decayingSineWave)
                    .setDuration(500)
                    .start();
    }
    private void lockTile(View v) {
        View outer = v.findViewById(mLargeIds[mLastLarge]);
        for(int j = 0; j < 9; j++) {
            final Button inner = outer.findViewById(mSmallIds[j]);
            final Tile smallTile = mSmallTiles[mLastLarge][j];
            smallTile.setView(inner);
            if(smallTile.ismAvailable())
                inner.setVisibility(View.INVISIBLE);
        }
    }

    private void addTileToList() {
        View outer = view.findViewById(mLargeIds[mLastLarge]);
        for(int j = 0; j < 9; j++) {
            final Button inner = outer.findViewById(mSmallIds[j]);
            final Tile smallTile = mSmallTiles[mLastLarge][j];
            smallTile.setView(inner);
            if(!smallTile.ismAvailable())
                list.add(smallTile);
        }
    }

    private void resetTile(View v) {
        View outer = v.findViewById(mLargeIds[mLastLarge]);
        for(int j = 0; j < 9; j++) {
            final Button inner = outer.findViewById(mSmallIds[j]);
            final Tile smallTile = mSmallTiles[mLastLarge][j];
            smallTile.setmAvailable(true);
            smallTile.setView(inner);
            inner.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            word = "";
        }
        clearAvailable();
        setAllAvailable();
    }


    private void makeMove(int large, int small) {
        mLastLarge = large;
        mLastSmall = small;
        Tile smallTile = mSmallTiles[large][small];
        Tile largeTile = mLargeTiles[large];
        smallTile.setmAvailable(false);
        //smallTile.setOwner(mPlayer);
        setAvailableFromLastMove(large);

    }

    public void restartGame() {
        initGame();
        initViews(getView());

        updateAllTiles();
    }

    public void initGame() {
        PHASE2 = false;
        mEntireBoard = new Tile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new Tile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new Tile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // If the player moves first, set which spots are available
        mLastSmall = -1;
        mLastLarge = -1;
        setAvailableFromLastMove(mLastSmall);
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                Tile tile = mSmallTiles[small][dest];
                if (tile.ismAvailable()) {
                    addAvailable(tile);

                }
            }
        }
        // If there were none available, make all squares available
        if (mAvailable.isEmpty()) {
            setAllAvailable();
        }
    }

    private void setTileAvailable() {   // Phase 2
        clearAvailable();
        makeTileUnavailable(view);
    }

    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile tile = mSmallTiles[large][small];
                if (tile.ismAvailable())
                    addAvailable(tile);
            }
        }
    }

    public void setAvailableForPhase2() {
        Log.e("Phase2", "begin phase 2");
        PHASE2 = true;
        word = "";
        for(Tile t : list) {
            t.setmAvailable(true);
            t.setmCompleted(false);
            View outer = view.findViewById(mLargeIds[t.large]);
            mLargeTiles[t.large].setView(outer);
            mLargeTiles[t.large].setmCompleted(false);
            final Button inner = outer.findViewById
                    (mSmallIds[t.small]);
            addAvailable(t);
            inner.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            inner.setEnabled(true);
            t.setView(inner);
            addAvailable(t);
        }
    }

    private void makeTilesUnavailable(View v) {
        for(int i = 0; i < 9; i++) {
            View outer = v.findViewById(mLargeIds[i]);
            for(int j = 0; j < 9; j++) {
                if(i != mLastLarge) {
                    final Button inner = outer.findViewById
                            (mSmallIds[j]);
                    final Tile smallTile = mSmallTiles[i][j];
                    smallTile.setView(inner);
                    inner.setEnabled(false);
                }
            }
        }
    }

    private void resetButtonColor() {
        for(Tile t : list) {
            View outer = view.findViewById(mLargeIds[t.large]);
            //mLargeTiles[t.large].setView(outer);
            final Button inner = outer.findViewById
                    (mSmallIds[t.small]);
            inner.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void makeTileUnavailable(View v) {
        for(int i = 0; i < 9; i++) {
            View outer = v.findViewById(mLargeIds[i]);
            for(int j = 0; j < 9; j++) {
                final Button inner = outer.findViewById
                        (mSmallIds[j]);
                final Tile smallTile = mSmallTiles[i][j];
                smallTile.setView(inner);
                if(i == mLastLarge)
                    inner.setEnabled(false);
                else
                    inner.setEnabled(true);
            }
        }

       /* View outer = v.findViewById(mLargeIds[mLastLarge]);
        for(int j = 0; j < 9; j++) {
            final Button inner = outer.findViewById
                    (mSmallIds[j]);
            final Tile smallTile = mSmallTiles[mLastLarge][j];
            smallTile.setView(inner);
            inner.setEnabled(false);
        }*/
    }

    private void makeTilesAvailable(View v) {
        for(int i = 0; i < 9; i++) {
            View outer = v.findViewById(mLargeIds[i]);
            for(int j = 0; j < 9; j++) {
                final Button inner = outer.findViewById
                        (mSmallIds[j]);
                final Tile smallTile = mSmallTiles[i][j];
                smallTile.setView(inner);
                if(i != mLastLarge) {
                    inner.setEnabled(true);
                }
                else if(PHASE2) {
                    inner.setEnabled(true);
                }
            }
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    /** Create a string containing the state of the game. */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(true);
        builder.append(',');
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].ismAvailable());
                Log.e("AVAIL", Boolean.toString(mSmallTiles[large][small].ismAvailable()));
                builder.append(',');
            }
        }
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                /*Log.e("GET", Character.toString(letters[large][small]));*/
                builder.append(letters[large][small]);  //mSmallTiles[large][small].letter
                builder.append(',');
            }
        }
        builder.append(word);
        builder.append(',');
        builder.append(((GameActivity) getActivity()).timer);
        builder.append(',');
        Log.e("GET", Integer.toString(score));
        builder.append(score);
        builder.append(',');
        return builder.toString();
    }

    /** Restore the state of the game from the given string. */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        RESUME = Boolean.getBoolean(fields[index++]);
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                boolean val = Boolean.getBoolean(fields[index++]);
                mSmallTiles[large][small].setmAvailable(val);
            }
        }
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                letters[large][small] = fields[index++].charAt(0);
                mSmallTiles[large][small].letter = letters[large][small];
            }
        }
        word = fields[index++];
        ((GameActivity) getActivity()).timer = Integer.parseInt(fields[index++]);
        score = Integer.parseInt(fields[index++]);
        ((GameActivity) getActivity()).setScore(score);
        initViews(view);
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
    }

    public int getPoints(char mLetter) {    // per standard scrabble letter points distribution
        switch (mLetter) {
            case 'E':
            case 'A':
            case 'I':
            case 'O':
            case 'N':
            case 'R':
            case 'T':
            case 'L':
            case 'S':
            case 'U':
                return 1;

            case 'D':
            case 'G':
                return 2;

            case 'B':
            case 'C':
            case 'M':
            case 'P':
                return 3;

            case 'F':
            case 'H':
            case 'V':
            case 'W':
            case 'Y':
                return 4;

            case 'K':
                return 5;

            case 'J':
            case 'X':
                return 8;

            case 'Q':
            case 'Z':
                return 10;

            default:
                return 0;
        }
    }
}

