package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessaging;
import akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani.app.Config;
import akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani.utils.NotificationUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

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


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
