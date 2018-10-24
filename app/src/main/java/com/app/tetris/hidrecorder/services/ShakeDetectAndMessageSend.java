package com.app.tetris.hidrecorder.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;

import com.app.tetris.hidrecorder.ShakeEventListener;
import com.app.tetris.hidrecorder.TinyDB;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Seymur on 27.2.2016.
 */

public class ShakeDetectAndMessageSend extends Service  {
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    private boolean shaked = false;
    private static Timer timer = new Timer();
    TextToSpeech tts;
    boolean flag = true;
    String locAddress = "";
    int counter = 0;
    public static Timer timerFeedback = new Timer();
    public static boolean driverNoFeedback = false;
    public void onCreate()
    {
        super.onCreate();


        Context context = getApplicationContext();
        final TinyDB tinydb = new TinyDB(getApplicationContext());
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
        

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        mSensorListener.MIN_FORCE  = tinydb.getInt("SHAKEVALUE")*3;
        mSensorListener.MIN_DIRECTION_CHANGE  = tinydb.getInt("DIRECTIONVALUE")/2 + 2;
        mSensorListener.MAX_TOTAL_DURATION_OF_SHAKE  = tinydb.getInt("DURATIONVALUE")*100;
        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                if(!shaked) {
                    int locationactive = tinydb.getInt("LOCATIONACTIVE");
                    tinydb.getDouble("LATITUDE", 0.0);
                    tinydb.getDouble("LONGITUDE", 0.0);
                    String number = tinydb.getString("EMERGENCYNUMBER");
                    if (locationactive == 0) {
                        String say = "Is everything okay ? Please say yes or no ";
                       // NewService.driverState = true;
                       // NewService.listeningFlag = true;
                        speak(say);
                    } else {
                        String location = String.valueOf(tinydb.getDouble("LATITUDE", 0.0)) + " , " + String.valueOf(tinydb.getDouble("LONGITUDE", 0.0));

                        Address returnedAddress = null;

                        if( isNetworkConnected()) {
                           Geocoder geocoder = new Geocoder(ShakeDetectAndMessageSend.this, Locale.getDefault());

                           try {
                               List<Address> addresses = geocoder.getFromLocation(tinydb.getDouble("LATITUDE", 0.0), tinydb.getDouble("LONGITUDE", 0.0), 1);

                               if (addresses != null) {
                                   returnedAddress = addresses.get(0);
                                   StringBuilder strReturnedAddress = new StringBuilder();
                                   for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                                       strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                                   }

                               } else {
                                   //"No Address returned!"
                               }
                           } catch (IOException e) {
                               // TODO Auto-generated catch block
                               e.printStackTrace();
                               //"Canont get Address!"
                           }
                            Toast.makeText(getApplicationContext(),location + "\n"+ returnedAddress.getAddressLine(1) + "  " + returnedAddress.getAddressLine(0)+ " "+returnedAddress.getAddressLine(2),Toast.LENGTH_LONG).show();
                       }

                        if (!number.equals(null) || !number.equals("")) {

                            timerFeedback.schedule(new SayHello(), 0, 1000);
                         //   NewService.driverState = true;
                           // NewService.listeningFlag = true;

                            speak("Is everything okay ? Please say Yes or No ");
                            if(isNetworkConnected() && isConnectingToInternet()) {
                               // Toast.makeText(getApplicationContext(),String.valueOf(isInternetAvailable()),Toast.LENGTH_LONG).show();

                                locAddress = location + "\n" + returnedAddress.getAddressLine(1) + "\n" + returnedAddress.getAddressLine(0) + "\n" + returnedAddress.getAddressLine(2);
                                Toast.makeText(getApplicationContext(),location + "\n"+ returnedAddress.getAddressLine(1) + "  " + returnedAddress.getAddressLine(0)+ " "+returnedAddress.getAddressLine(2),Toast.LENGTH_LONG).show();
                            }
                            else{
                                locAddress = location;

                            }

                        } else {
                          //  NewService.driverState = true;
                          //  NewService.listeningFlag = true;
                            speak("Is everything okay? Predefined number is not setted");
                        }
                    }

                    shaked = true;
                }
            }
        });
    }

    class SayHello extends TimerTask {
        public void run() {

            if(counter<14)
                counter = counter + 1;
            else {
                counter = 0;
                TinyDB tinydb = new TinyDB(getApplicationContext());
                String number = tinydb.getString("EMERGENCYNUMBER");
                System.out.println(number);
                if(!number.equals(null)) {
                    speak("No answered so I am sending the location info to predefined contact");
                    sendSMS(number, locAddress);
                }
                else{
                    sendSMS("+905077352608", locAddress);
                }
                timerFeedback.cancel();
               // NewService.driverState = false;
             //   NewService.listeningFlag = false;

            }
            if(driverNoFeedback ){
                TinyDB tinydb = new TinyDB(getApplicationContext());
                String number = tinydb.getString("EMERGENCYNUMBER");
                System.out.println(number);
                if(!number.equals(null))
                    sendSMS(number,locAddress);
                else{
                    sendSMS("+905077352608", locAddress);
                }
              //  NewService.driverState = false;
              //  NewService.listeningFlag = false;
                driverNoFeedback = false;
                timerFeedback.cancel();
            }
        }
    }


    public void sendSMS(String phoneNo, String msg){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
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

    private class mainTask extends TimerTask
    {
        public void run()
        {
            shaked = false;
        }
    }









    private void speak(String str)
    {

        tts.speak(str, TextToSpeech.QUEUE_ADD, null);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }
    public boolean isTablet()
    {
        TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
        {
            return true;
        }
        return false;
    }








    @Override
    public void onDestroy() {
        Toast.makeText(this, "Crash Service Destroyed", Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(mSensorListener);
        super.onDestroy();
    }
}