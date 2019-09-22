package com.seymur.hidrecorder.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.seymur.hidrecorder.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.seymur.hidrecorder.utils.TinyDB;
import com.seymur.hidrecorder.utils.TypefaceSpan;
import com.seymur.hidrecorder.services.VideoRecordService;

import java.net.InetAddress;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ServiceCast")
public class MainActivity extends ActionBarActivity {

    int leftLimit, rightLimit;


    public ToggleButton videoRecordButton;
    public TextView batteryPercent;
    public TinyDB tinydb;
    public int level, videoRecordState = 0;
    private Intent videoRecordService;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoRecordService = new Intent(this, VideoRecordService.class);


        setContentView(R.layout.activity_main);

        /***************************************************/
        SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "capture.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);


        batteryPercent = (TextView) this.findViewById(R.id.textView2);
        getBatteryPercentage();

        videoRecordButton = (ToggleButton) findViewById(R.id.videoRecordButton);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");

        videoRecordButton.setTypeface(font);


        tinydb = new TinyDB(MainActivity.this);
        videoRecordState = tinydb.getInt("VIDEO_RECORDING");

        leftLimit = tinydb.getInt("FORCELOWLIMIT");
        rightLimit = tinydb.getInt("FORCEHIGHLIMIT");

        if (videoRecordState == 1) {

            videoRecordButton.setChecked(true);
            videoRecordButton.setShadowLayer(
                    1.5f, // radius
                    5.0f, // dx
                    5.0f, // dy
                    Color.parseColor("#FF3D803D")); // shadow color
        } else {

            videoRecordButton.setChecked(false);
        }


        getWindow();


        videoRecordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (videoRecordButton.isChecked()) {
                    startService(videoRecordService);
                    videoRecordState = 1;
                    tinydb.putInt("VIDEO_RECORDING", videoRecordState);
                    videoRecordButton.setEnabled(false);
                    videoRecordButton.getBackground().setAlpha(160);
                    Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    videoRecordButton.setEnabled(true);
                                    videoRecordButton.getBackground().setAlpha(255);
                                }
                            });
                        }
                    }, 3000);
                } else {
                    stopService(videoRecordService);
                    videoRecordState = 0;
                    tinydb.putInt("VIDEO_RECORDING", videoRecordState);
                }

            }
        });


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.howto:
                Intent intent = new Intent(this, VideosActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_save:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

  
    @SuppressLint("NewApi")
    private void getBatteryPercentage() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            @SuppressWarnings("deprecation")
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                level = -1;

                IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = registerReceiver(null, filter);
                int chargeState = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                switch (chargeState) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        batteryPercent.setText("chr");
                        batteryPercent.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.bat_chrgng));
                        break;
                    default: {
                        if (currentLevel >= 0 && scale > 0) {
                            level = (currentLevel * 100) / scale;
                        }

                        if (level >= 0 && level < 10) {
                            batteryPercent.setText(level + "%");
                            batteryPercent.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bat_empty));
                        } else if (level >= 10 && level < 30) {

                            batteryPercent.setText(level + "%");
                            batteryPercent.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bat_bir));
                        } else if (level >= 30 && level < 50) {

                            batteryPercent.setText(level + "%");
                            batteryPercent.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bat_iki));
                        } else if (level >= 50 && level < 70) {

                            batteryPercent.setText(level + "%");
                            batteryPercent.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bat_uc));
                        } else if (level >= 70 && level <= 90) {

                            batteryPercent.setText(level + "%");
                            batteryPercent.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bat_dord));
                        } else if (level >= 90 && level < 100) {

                            batteryPercent.setText(level + "%");
                            batteryPercent.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bat_bes));
                        } else {


                        }
                    }
                }

            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.app.seymur.hidrecorder/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.app.seymur.hidrecorder/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    
}