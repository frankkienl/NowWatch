package nl.frankkie.nowwatch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ImageButton earth = (ImageButton) findViewById(R.id.main_btn_earth);
        ImageButton noContinent = (ImageButton) findViewById(R.id.main_btn_no_continent);
        ImageButton all = (ImageButton) findViewById(R.id.main_btn_all);

        earth.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                setImage(R.drawable.nowclock_earth);
                startClock();
            }
        });
        noContinent.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                setImage(R.drawable.nowclock_no_continent);
                startClock();
            }
        });
        all.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                setImage(R.drawable.nowclock_all);
                startClock();
            }
        });
    }

    public void setImage(int id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("sEarthImageId", id).commit();
    }

    public void startClock() {
        Intent i = new Intent();
        i.setClass(this, NowClockActivity.class);
        startActivity(i);
    }
}
