package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import java.io.InputStream;
import java.util.ArrayList;

public class DictionaryActivity extends AppCompatActivity {

    Search search;
    InputStream inputStream;
    Button ack;
    EditText txt;
    ArrayList<String> data;
    private RecyclerView.Adapter mAdapter;
    ToneGenerator toneGen1;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        this.setTitle(getResources().getString(R.string.title_activity_dictionary));   // Change title

        RecyclerView mRecyclerView = findViewById(R.id.list);
        fab = findViewById(R.id.floatingActionButton);
        ack = findViewById(R.id.acknowledgement);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        data = new ArrayList<>();
        mAdapter = new MyAdapter(data);
        mRecyclerView.setAdapter(mAdapter);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        inputStream = getResources().openRawResource(R.raw.wordlist);
        search = new Search(inputStream);
        txt = findViewById(R.id.searchTerm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus();
                data.clear();
                mAdapter.notifyDataSetChanged();

            }
        });

        ack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DictionaryActivity.this, AcknowledgementsActivity.class);
                startActivity(intent);
            }
        });

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s != "" && s.length() > 2 && search.isWord(s.toString()) && !data.contains(s.toString())) {
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150); // beep
                    data.add(s.toString());
                    mAdapter.notifyDataSetChanged();
                    //notifyChanges();

                    //clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fab.setImageBitmap(textAsBitmap("Clear", 40, Color.WHITE));
    }

    //method to convert your text to image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public void clearFocus() {
        txt.setText("");
        txt.clearFocus();
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
