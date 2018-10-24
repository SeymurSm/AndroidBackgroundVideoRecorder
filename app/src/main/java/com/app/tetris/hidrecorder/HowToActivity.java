package com.app.tetris.hidrecorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.tetris.hidrecorder.indicator.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bledi on 12/19/15.
 */

@SuppressLint("NewApi") public final  class HowToActivity extends ActionBarActivity {

    public static ViewPager _mViewPager;
    public static ViewPagerAdapter _adapter;

    PowerManager pm;
    PowerManager.WakeLock wl;
    KeyguardManager km;
    static  KeyguardManager.KeyguardLock kl,lock;
    int[] back = new int[]{
            R.color.transparent,
    };


    int[] smsIcon = new int[]{
            R.drawable.com_one,
            R.drawable.com_two,
            R.drawable.com_three,
            R.drawable.com_four,
            R.drawable.com_five,
            R.drawable.com_six,
            R.drawable.com_seven,
            R.drawable.com_eight,
            R.drawable.com_nine,
    };

    List<HashMap<String,String>> aList;
    HashMap<String, String> hm;
    ListView listView;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);
        setUpView();
        setTab();


//        TextView textView = (TextView)findViewById(R.id.textViewLay2);
//        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");
//        textView.setTypeface(font);


        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        km=(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        kl= km.newKeyguardLock("INFO");
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "INFO");
        wl.acquire(); //wake up the screen
        kl.disableKeyguard();// dismiss the keyguard

        registerReceiver(endAct, new IntentFilter("command"));
    }

    private void setUpView(){
        _mViewPager = (ViewPager) findViewById(R.id.viewPager);
        _adapter = new ViewPagerAdapter(getApplicationContext(),getSupportFragmentManager());
        _mViewPager.setAdapter(_adapter);
        _mViewPager.setCurrentItem(0);
    }

    private void setTab(){
        _mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                        TextView ikinci = (TextView)findViewById(R.id.textViewLay1);
                        Typeface fontiki = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");
                        ikinci.setTypeface(fontiki);
                        ikinci.setText("Commands \n\n");

                        List<HashMap<String,String>> list;
                        list = new ArrayList<HashMap<String, String>>();
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[0]));
                        hm.put("catiki", "OK");
                        hm.put("catikibir", "Start command");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[1]));
                        hm.put("catiki", "Close");
                        hm.put("catikibir", "Sleep commanding");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[2]));
                        hm.put("catiki", "Battery");
                        hm.put("catikibir", "Battery Level");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[3]));
                        hm.put("catiki", "Message");
                        hm.put("catikibir", "Outgoing Message");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[4]));
                        hm.put("catiki", "Call");
                        hm.put("catikibir", "Outgoing Call");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[5]));
                        hm.put("catiki", "Date");
                        hm.put("catikibir", "Date of Month");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[6]));
                        hm.put("catiki", "Time");
                        hm.put("catikibir", "Hour of Day");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[7]));
                        hm.put("catiki", "Record");
                        hm.put("catikibir", "Starting Video Capture");
                        list.add(hm);
                        hm = new HashMap<String, String>();
                        hm.put("back", Integer.toString(back[0]));
                        hm.put("catbir", Integer.toString(smsIcon[8]));
                        hm.put("catiki", "Detection");
                        hm.put("catikibir", "Activationg Crash Detection");
                        list.add(hm);

                        String[] from = {"back","catbir", "catiki","catikibir"};
                        int[] to = {R.id.backlayer,R.id.catbir, R.id.catiki,R.id.catikibir};
                        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), list, R.layout.list_back, from, to);

                        listView = (ListView) findViewById(R.id.commandsList);

                        listView.setAdapter(adapter);
                        listView.setTextFilterEnabled(true);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                LayoutInflater inflater = HowToActivity.this.getLayoutInflater();
                                Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");
                                View layout = inflater.inflate(R.layout.command_info, null);
                                final AlertDialog alertContact = new AlertDialog.Builder(HowToActivity.this)
                                        .setView(layout)
                                        .show();
                                alertContact.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));
                                TextView infoText = (TextView)layout.findViewById(R.id.commandDescription);
                                infoText.setTypeface(font);
                                if (position == 0) {
                                    infoText.setText("STARTING COMMAND");
                                }else if (position == 1) {
                                    infoText.setText("CLOSE COMMANDING");
                                }else if (position == 2) {
                                    infoText.setText("" +
                                            "GETTING BATTERY INFO OF DEVICE");
                                }else if (position == 3) {
                                    infoText.setText("SENDING MESSAGE");
                                }else if (position == 4) {
                                    infoText.setText("CALLING TO SOMEONE FROM CONTACT LIST");
                                }else if (position == 5) {
                                    infoText.setText("GETTING DATE OF MONTH");
                                }else if (position == 6) {
                                    infoText.setText("GETTING TIME OF DATE");
                                }else if (position == 7) {
                                    infoText.setText("STARTING VIDEO RECORDING OF THE ROAD");
                                }else if (position == 8) {
                                    infoText.setText("STARTING EYE DETECTION ACTIVITY");
                                }
                            }
                        });
                        break;

                    case 1:
                        findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
                        findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                        TextView ucuncu = (TextView)findViewById(R.id.textViewLay2);
                        Typeface fontuc = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/uicksandregular.otf");
                        ucuncu.setTypeface(fontuc);
                        ucuncu.setText("USER GUIDANCE \n\n" +

                                        "in case of shake event answer 'yes' to Assistant if everything is okay\n\n" +
                                        "In case of incoming call say 'answer' or 'reject' to Assistant to handle incoming Call\n\n" +
                                        "In order to send a message first speech out 'message' command then speech message body. After all say contact name and confirm  message sending by saying 'yes', in order to cancel answer with 'no' command" + "\n\n" +
                                        "Video Resolution and Duration are optimizable under Options Activity" + "\n\n" +
                                        ""
                        );
                        ucuncu.setMovementMethod(new ScrollingMovementMethod());

                        break;
                    case 2:
                        findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.third_tab).setVisibility(View.VISIBLE);

                        break;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(getApplicationContext(),MainActivity.class);
        finish();
        startActivity(intent);
    }


    private final BroadcastReceiver endAct = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    public static void changePage(int position){
        _mViewPager.setCurrentItem(position);
    }


//    protected void onStop() {
//        super.onStop();
//      //  reEnable();
//        //  wl.release();
//        finish();
//    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        reEnable();
        super.onDestroy();

        Log.v("MyApp", "onDestroy");
    }



    public void reEnable(){

        kl.reenableKeyguard();
        finish();
       // wl = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "INFO");
       // wl.acquire();
    }
}
