package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;


import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
    Button clear;
    EditText txt;
    ArrayList<String> data;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ToneGenerator toneGen1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        this.setTitle(getResources().getString(R.string.title_activity_dictionary));   // Change title

        mRecyclerView = findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        data = new ArrayList<>();
        mAdapter = new MyAdapter(data);
        mRecyclerView.setAdapter(mAdapter);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        inputStream = getResources().openRawResource(R.raw.wordlist);
        search = new Search(inputStream);
        clear = findViewById(R.id.clear);
        txt = findViewById(R.id.searchTerm);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus();
                data.clear();
                mAdapter.notifyDataSetChanged();

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
                    mAdapter.notifyItemInserted(data.size()-1);
                    clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
