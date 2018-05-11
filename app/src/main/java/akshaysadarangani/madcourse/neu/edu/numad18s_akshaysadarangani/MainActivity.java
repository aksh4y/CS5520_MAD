package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button about, error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        about = findViewById(R.id.about_btn);
        error = findViewById(R.id.error_btn);

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
    }
}