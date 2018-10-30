package com.app.tetris.hidrecorder;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.InetAddress;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ServiceCast")
public class MainActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {

    int leftLimit, rightLimit;
    public Dialog dialog;
    public AudioManager mAudioManager;
    public TextToSpeech tts;
    public String myString;
    public int STREAM_VOLUME;
  //  public Intent bgServiceIntent, shakeDetectIntent;
    //  public Intent callDetectService;
    //public ToggleButton commandActivate, cameraActivate, notificationActive;
    public ToggleButton videoRecordButton;
    public TextView batteryPercent;
    public TinyDB tinydb;
  //  public Intent callDetect;
    public int level, stateOfService = 0, stateOfNotification = 0, stateOfEyeDetection = 0, videoRecordState = 0;
    private Intent videoRecordService;
    private String currentLanguage;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        STREAM_VOLUME = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    //    bgServiceIntent = new Intent(this, NewService.class);
       // shakeDetectIntent = new Intent(this, ShakeDetectAndMessageSend.class);
        videoRecordService = new Intent(this, VideoRecordService.class);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,STREAM_VOLUME,0);
        // callDetectService =   new Intent(this, CallDetectService.class);


//        final NestedScrollView llBottomSheet = (NestedScrollView) findViewById(R.id.bottom_sheet);
//        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        // bottomSheetBehavior.setHideable(true);
//        bottomSheetBehavior.setPeekHeight(80);
//
//        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                Toast.makeText(MainActivity.this, newState+"", Toast.LENGTH_SHORT).show();
//                bottomSheetBehavior.setPeekHeight(80);
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                bottomSheetBehavior.setPeekHeight(80);
//                // if(bottomSheet.getHeight()>80)
//            }
//        });




//        registerReceiver(mySmsReceiver,new IntentFilter("MyBroadcats"));


        setContentView(R.layout.activity_main);
        ActionBar toolbar = getActionBar();

        //callDetect = new Intent(this, CallDetectService.class);

        /***************************************************/
        SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "capture.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);



        batteryPercent = (TextView) this.findViewById(R.id.textView2);
        getBatteryPercentage();
        //tts = new TextToSpeech(this, this);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    //  tts.setLanguage(Locale.US);
                    tts.setLanguage(Locale.getDefault());
                }
            }
        });
        currentLanguage = Locale.getDefault().getDisplayLanguage();

//        commandActivate = (ToggleButton) findViewById(R.id.toggleButton);
//        cameraActivate = (ToggleButton) findViewById(R.id.toggleButton2);
//        notificationActive = (ToggleButton) findViewById(R.id.notificationButton);
        videoRecordButton = (ToggleButton) findViewById(R.id.videoRecordButton);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");
//        commandActivate.setTypeface(font);
//        cameraActivate.setTypeface(font);
//        notificationActive.setTypeface(font);
        videoRecordButton.setTypeface(font);
        // txtSpeechInput      = (TextView) findViewById(R.id.textView);
        // seekBar             = (SeekBar) findViewById(R.id.seekBar);

        tinydb = new TinyDB(MainActivity.this);
        stateOfService = tinydb.getInt("STATEOFSERVICE");
        stateOfNotification = tinydb.getInt("STATEOFNOTIFICATION");
        stateOfEyeDetection = tinydb.getInt("STATEOFEYEDETECT");
        videoRecordState = tinydb.getInt("VIDEO_RECORDING");

        leftLimit = tinydb.getInt("FORCELOWLIMIT");
        rightLimit = tinydb.getInt("FORCEHIGHLIMIT");

      //  final Intent locationService = new Intent(this, LocationService.class);

      //  Toast.makeText(MainActivity.this, String.valueOf(isConnectingToInternet()), Toast.LENGTH_LONG).show();


        System.out.println(String.valueOf(stateOfService));
//Setting Command Button
//        if (stateOfService == 1) {
//            if(currentLanguage.equals("Türkçe"))
//                commandActivate.setTextOn("Komut modu aktif");
//            else
//                commandActivate.setTextOn("Command mode activated");
//            commandActivate.setBackgroundColor(Color.RED);
//            commandActivate.setChecked(true);
//        } else {
//            if(currentLanguage.equals("Türkçe"))
//                commandActivate.setTextOff("Komut modu kapalı");
//            else
//                commandActivate.setTextOff("Command mode deactivated");
//            commandActivate.setBackgroundColor(Color.parseColor("#1a237e"));
//            commandActivate.setChecked(false);
//        }
//Setting Notification Button
//        if (stateOfNotification == 1) {
//            if(currentLanguage.equals("Türkçe"))
//                notificationActive.setTextOff("Bildirimler aktif");
//            else
//                notificationActive.setTextOff("Notifications on");
//            notificationActive.setBackgroundColor(Color.RED);
//            notificationActive.setChecked(true);
//        } else {
//            if(currentLanguage.equals("Türkçe"))
//                notificationActive.setTextOn("Bildirimler kapalı");
//            else
//                notificationActive.setTextOn("Notifications on");
//            notificationActive.setBackgroundColor(Color.parseColor("#2196f3"));
//            notificationActive.setChecked(false);
//        }

//Setting Eye Detection Button
//        if (stateOfEyeDetection == 1) {
//            if(currentLanguage.equals("Türkçe"))
//                cameraActivate.setText("Uyku tespit etme");
//            else
//                cameraActivate.setText("Sleep detection");
//            cameraActivate.setBackgroundColor(Color.parseColor("#f50057"));
//            cameraActivate.setChecked(true);
//        } else {
//            if(currentLanguage.equals("Türkçe"))
//                 cameraActivate.setText("Uyku tespit etme");
//            else
//                cameraActivate.setText("Sleep detection");
//            cameraActivate.setBackgroundColor(Color.parseColor("#f50057"));
//            cameraActivate.setChecked(false);
//        }

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



        if(isTablet(getApplicationContext())){
//            commandActivate.setTextSize(22);
//            notificationActive.setTextSize(22);
//            cameraActivate.setTextSize(22);
            videoRecordButton.setTextSize(22);
        }

        getWindow();
//        commandActivate.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                if (commandActivate.isChecked()) {
//
//                    if(isConnectingToInternet()) {
//                        commandActivate.setBackgroundColor(Color.RED);
//                        myString = "command mode activated";
//
//                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, STREAM_VOLUME, 0);
//                        //    startService(shakeDetectIntent);
//                        if (currentLanguage.equals("Türkçe")) {
//                            myString = "komut modu aktif edildi";
//                        }
//                        speakOut();
//                        startService(bgServiceIntent);
//                        stateOfService = 1;
//                        tinydb.putInt("STATEOFSERVICE", stateOfService);
//                        if (leftLimit == 0 && rightLimit == 0) {
//                            leftLimit = 9;
//                            rightLimit = 10;
//                        }
//
//                        tinydb.putInt("FORCELOWLIMIT", leftLimit);
//                        tinydb.putInt("FORCEHIGHLIMIT", rightLimit);
//
//                    }
//                    else{
//                        commandActivate.setChecked(false);
//                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
//                                .setTitle("No Internet")
//                                .setIcon(R.drawable.error)
//                                .setMessage(Html.fromHtml(String.format("<font size = '24' color='#ff9800'>Check your internet connection.\n   ", "#000000")))
//                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                })
//                                .show();
//                    }
//
//                } else {
//                    commandActivate.setBackgroundColor(Color.parseColor("#1a237e"));
//                    stopService(bgServiceIntent);
//                    //  stopService(shakeDetectIntent);
//
//
//                    myString = "command mode deactivated";
//                    if (currentLanguage.equals("Türkçe")) {
//                        myString = "Komut modu kapatıldı";
//                    }
//                    speakOut();
//                    stateOfService = 0;
//                    tinydb.putInt("STATEOFSERVICE", stateOfService);
//                }
//
//            }
//        });
//
//        cameraActivate.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                stateOfEyeDetection = 1;
//                tinydb.putInt("STATEOFEYEDETECT", stateOfEyeDetection);
//                myString = "Camera eye detection mode activated";
//                if(currentLanguage.equals("Türkçe"))
//                {
//                    myString = "Uyku tespit modu aktif edildi";
//                }
//                speakOut();
//                Intent intent = new Intent(MainActivity.this, FdActivity.class);
//                intent.putExtra("methodType", "googleFaceTracker");
//                startActivity(intent);
//            }
//        });
//
/////Ayniki Zaten
//        //TODO use shared preferences
//        notificationActive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (notificationActive.isChecked()) {
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        notificationActive.setBackgroundColor(Color.RED);
//                        stateOfNotification = 1;
//                        myString = "Activate Smart Driver Assistant on List";
//                        if(currentLanguage.equals("Türkçe"))
//                        {
//                            myString = "Smart Driver Assistant'ı listeden aktif hale getirin";
//                        }
//                        speakOut();
//                        tinydb.putInt("STATEOFNOTIFICATION", stateOfNotification);
//                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "This device does not support reading notifications", Toast.LENGTH_SHORT).show();
//
//                    }
//                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        notificationActive.setBackgroundColor(Color.parseColor("#2196f3"));;
//                        stateOfNotification = 0;
//                        myString = "Deactivate Smart Driver Assistant on List";
//                        if(currentLanguage.equals("Türkçe"))
//                        {
//                            myString = "Smart Driver Assistant'ı listeden kaldırın";
//                        }
//                        speakOut();
//                        tinydb.putInt("STATEOFNOTIFICATION", stateOfNotification);
//                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "This device does not support reading notifications", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                }
//            }
//        });

        videoRecordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (videoRecordButton.isChecked()) {
                    startService(videoRecordService);
                    videoRecordState = 1;
                    tinydb.putInt("VIDEO_RECORDING", videoRecordState);
                //    videoRecordButton.setBackgroundColor(Color.RED);
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
                  //  videoRecordButton.setBackgroundColor(Color.parseColor("#26a69a"));
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
//            case R.id.menu_bookmark: {
//                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//
//                View layout = inflater.inflate(R.layout.info_dialog, null);
//                final AlertDialog alertContact = new AlertDialog.Builder(MainActivity.this)
//                        .setView(layout)
//                        .setIcon(R.drawable.tet)
//                        .show();
//                alertContact.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#001b5e20")));
//                TextView infoText = (TextView)layout.findViewById(R.id.textViewInfo);
//                Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");
//                infoText.setTypeface(font);
//                return true;
//            }


            case R.id.howto:
                Intent intent = new Intent(this, VideosActivity.class);
               // finish();
                startActivity(intent);
                return true;
            case R.id.menu_save:
                Intent i = new Intent(this, Settings.class);
                finish();
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,STREAM_VOLUME,0);
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        String text = myString;
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        while (tts.isSpeaking());
    }

    /**
     * Showing google speech input dialog
     */


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


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("https://www.google.com.tr/?gws_rd=ssl"); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }


    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
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
                Uri.parse("android-app://com.app.tetris.hidrecorder/http/host/path")
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
                Uri.parse("android-app://com.app.tetris.hidrecorder/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}