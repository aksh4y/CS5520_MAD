package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private Tile mEntireBoard = new Tile(this);
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private Tile.Owner mPlayer = Tile.Owner.X;
    private Set<Tile> mAvailable = new HashSet<Tile>();
    private int mLastLarge;
    private int mLastSmall;
    private int score = 0;
    private int timer = 90;
    private ImageButton submit;
    private TextView words;
    private String word = "";
    private Search search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        search = new Search(inputStream);
        initGame();
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
        initViews(rootView);
        updateAllTiles();
        /*submit.performClick();*/
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews(final View rootView) {      // @TODO: Set up letters here
        mEntireBoard.setView(rootView);
        words = rootView.findViewById(R.id.word);
        words.setText("");
        submit = rootView.findViewById(R.id.button_submit);
        ImageButton clear = rootView.findViewById(R.id.button_clear);
        final View view = rootView;
        WordsGenerator w = new WordsGenerator(this.getActivity().getApplicationContext());
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            char[] letters = w.shuffle(w.getRandomWord()).toCharArray();
            for (int small = 0; small < 9; small++) {
                final Button inner = outer.findViewById
                        (mSmallIds[small]);
                inner.setText(Character.toString(letters[small]));
                final int fLarge = large;
                final int fSmall = small;
                final Tile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                inner.setEnabled(true);
                inner.setVisibility(View.VISIBLE);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAvailable(smallTile)) {
                            inner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            word += inner.getText().toString();
                            smallTile.setmAvailable(false);
                            makeMove(fLarge, fSmall);
                            makeTilesUnavailable(view);
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
                if(search.isWord(word)) {
                    String str = words.getText().toString() + " " + word.toUpperCase();
                    words.setText(str);
                    lockTile(view);
                    setAllAvailable();
                    makeTilesAvailable(view);
                    mLargeTiles[mLastLarge].setmCompleted(true);
                    FrameLayout fl = getActivity().findViewById(R.id.frameLayout1);
                    LinearLayout item = fl.findViewById(R.id.linearLayout1);
                    final TextView scoreView = item.findViewById(R.id.score);
                    if(scoreView.getText().toString().substring(6).trim().equals(""))
                        score = 0;
                    else
                        score = Integer.parseInt(scoreView.getText().toString().substring(6).trim());
                    for(char ch : word.toCharArray()) {
                        score += getPoints(Character.toUpperCase(ch));
                    }
                    if(word.length() == 9) {  // 5 bonus points for finding longest word
                        score += 5;
                        Toast.makeText(getActivity().getApplicationContext(), "Good Job! Bonus Points Earned.", Toast.LENGTH_SHORT).show();
                    }
                    String scoreText = "SCORE: " + Integer.toString(score);
                    scoreView.setText(scoreText);
                    final TextView timerView = item.findViewById(R.id.timer);

                    new CountDownTimer(90000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            timerView.setText("TIME LEFT 0:"+checkDigit(timer));
                            timer--;
                        }
                        public void onFinish() {
                            timerView.setText("TIME LEFT 0:0");
                            makeTilesUnavailable(rootView);
                            cancel();
                        }

                    }.start();

                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid word!", Toast.LENGTH_SHORT).show();
                    //clearTile();
                    //setAllAvailable();
                }
                word = "";
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mLargeTiles[mLastLarge].getmCompleted()) {
                    resetTile(view);
                    makeTilesAvailable(view);
                }
            }
        });
    }

    private void lockTile(View v) {
        View outer = v.findViewById(mLargeIds[mLastLarge]);
        for(int j = 0; j < 9; j++) {
            final Button inner = outer.findViewById(mSmallIds[j]);
            final Tile smallTile = mSmallTiles[mLastLarge][j];
            smallTile.setView(inner);
            if(smallTile.ismAvailable())
                inner.setVisibility(View.GONE);
        }
    }

    private void resetTile(View v) {
        View outer = v.findViewById(mLargeIds[mLastLarge]);
        for(int j = 0; j < 9; j++) {
            final Button inner = outer.findViewById(mSmallIds[j]);
            final Tile smallTile = mSmallTiles[mLastLarge][j];
            clearAvailable();
            setAllAvailable();
            smallTile.setmAvailable(true);
            smallTile.setView(inner);
            inner.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            word = "";
        }
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void switchTurns() {
        mPlayer = mPlayer == Tile.Owner.X ? Tile.Owner.O : Tile
                .Owner.X;
    }

    private void makeMove(int large, int small) {
        mLastLarge = large;
        mLastSmall = small;
        Tile smallTile = mSmallTiles[large][small];
        Tile largeTile = mLargeTiles[large];
        smallTile.setmAvailable(false);
        //smallTile.setOwner(mPlayer);
        setAvailableFromLastMove(large);
        /*Tile.Owner oldWinner = largeTile.getOwner();
        Tile.Owner winner = largeTile.findWinner();
        if (winner != oldWinner) {
            largeTile.setOwner(winner);
        }
        winner = mEntireBoard.findWinner();
        mEntireBoard.setOwner(winner);*/
        // updateAllTiles();
        /*if (winner != Tile.Owner.NEITHER) {
            ((GameActivity)getActivity()).reportWinner(winner);
        }*/
    }

    public void restartGame() {
        initGame();
        initViews(getView());

        updateAllTiles();
    }

    public void initGame() {
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
            Log.e("Game Frag", "mAvailable is empty");
            setAllAvailable();
        }
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

    private void makeTilesAvailable(View v) {
        for(int i = 0; i < 9; i++) {
            View outer = v.findViewById(mLargeIds[i]);
            for(int j = 0; j < 9; j++) {
                if(i != mLastLarge) {
                    final Button inner = outer.findViewById
                            (mSmallIds[j]);
                    final Tile smallTile = mSmallTiles[i][j];
                    smallTile.setView(inner);
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
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getOwner().name());
                builder.append(',');
            }
        }
        return builder.toString();
    }

    /** Restore the state of the game from the given string. */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
            }
        }
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

