package com.dylanredfield.simonslides;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Button mEasyButton;
    private Button mMediumButton;
    private Button mHardButton;
    private TextView mStartText;
    private Typeface mFont;
    private int mHighScore;
    private TextView mHighScoreCounter;
    private TextView mHighScoreText;
    private SharedPreferences mPref;
    private boolean mFirstTime;

    public static String SPEED_CONSTANT = "SPEED_CONSTANT";
    public static String PREF_STRING = "SHARED_PREFERENCES";
    public static String EASY_STRING = "EASY_STRING";
    public static String MEDIUM_STRING = "MEDIUM_STRING";
    public static String HARD_STRING = "HARD_STRING";
    public static String FIRST_TIME_STRING = "FIRST_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHighScore = 0;

        mPref = getSharedPreferences(PREF_STRING, Activity.MODE_PRIVATE);
        mHighScore = mPref.getInt(MEDIUM_STRING, 0);
        mFirstTime = mPref.getBoolean(FIRST_TIME_STRING, true);

        mFont = Typeface.createFromAsset(getAssets(), "Bellota-Regular.otf");
        mEasyButton = (Button) findViewById(R.id.button_easy);
        mEasyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), GameActivity.class);
                startIntent.putExtra(SPEED_CONSTANT, 1250);
                startActivity(startIntent);
            }
        });
        mMediumButton = (Button) findViewById(R.id.button_medium);
        mMediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), GameActivity.class);
                startIntent.putExtra(SPEED_CONSTANT, 1000);
                startActivity(startIntent);
            }
        });
        mHardButton = (Button) findViewById(R.id.button_hard);
        mHardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), GameActivity.class);
                startIntent.putExtra(SPEED_CONSTANT, 750);
                startActivity(startIntent);
            }
        });
        mStartText = (TextView) findViewById(R.id.start_game_text);
        mStartText.setTypeface(mFont);
        mEasyButton.setTypeface(mFont);
        mMediumButton.setTypeface(mFont);
        mHardButton.setTypeface(mFont);
        mHighScoreCounter = (TextView) findViewById(R.id.high_score);
        mHighScoreCounter.setTypeface(mFont);
        mHighScoreCounter.setText("" + mHighScore);

        mHighScoreText = (TextView) findViewById(R.id.high_score_text);
        mHighScoreText.setTypeface(mFont);
        //mEasyButton.setBackgroundColor(mLayout.getSolidColor());
        //mMediumButton.setBackgroundColor(mLayout.getSolidColor());
        //mHardButton.setBackgroundColor(mLayout.getSolidColor());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPref = getSharedPreferences(PREF_STRING, Activity.MODE_PRIVATE);
        mHighScore = mPref.getInt(MEDIUM_STRING, 0);
        mFirstTime = mPref.getBoolean(FIRST_TIME_STRING, true);

        mHighScoreCounter.setText("" + mHighScore);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
