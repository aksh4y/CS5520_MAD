package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button about, error, dictionary, scroggle;
    TextView versionCode, versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(getResources().getString(R.string.about_name));   // Change title

        about = findViewById(R.id.about_btn);
        error = findViewById(R.id.error_btn);
        dictionary = findViewById(R.id.dictionary_btn);
        scroggle = findViewById(R.id.scroggle_btn);
        versionCode = findViewById(R.id.version_code);
        versionName = findViewById(R.id.version_name);

        String vName = "Version Name: " + BuildConfig.VERSION_NAME;
        String vCode = "Version Code: " + BuildConfig.VERSION_CODE;
        versionName.setText(vName);
        versionCode.setText(vCode);

        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });

        error.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                throw new RuntimeException("This is an intentional crash.");
            }
        });

        dictionary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dictionaryIntent = new Intent(MainActivity.this, DictionaryActivity.class);
                startActivity(dictionaryIntent);
            }
        });

        scroggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dictionaryIntent = new Intent(MainActivity.this, ScroggleActivity.class);
                startActivity(dictionaryIntent);
            }
        });
    }
}
