package com.app.tetris.hidrecorder;

/**
 * Created by seymur on 10.05.2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Locale;

public class SMSApp extends BroadcastReceiver {
    private static final String TAG = SMSApp.class.getSimpleName();
    public static final String SMS_CONTENT = "sms_content";
    private TextToSpeech tts;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        TinyDB tinydb = new TinyDB(context);
        if(tinydb.getInt("MESSAGENOTIFY")==1)
        {
            tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                    }
                }
            });
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String messageReceived = "";
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    messageReceived += msgs[i].getMessageBody().toString();
                    messageReceived += "\n";
                }
                Toast.makeText(context, messageReceived, Toast.LENGTH_LONG).show();
                String senderPhoneNumber = msgs[0].getOriginatingAddress();
                speak(messageReceived);

            }

//        Log.i(TAG, "Intent recieved: " + intent.getAction());
//        Cursor c = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
//        c.moveToFirst();
//        String smsBody = c.getString(12);
//        Toast.makeText(context, "SMS RECEIVED:", Toast.LENGTH_LONG).show();
//        Toast.makeText(context, smsBody, Toast.LENGTH_LONG).show();
//            Intent fireActivityIntent = new Intent(context, SMSActivity.class);
//            fireActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            fireActivityIntent.putExtra(SMS_CONTENT, messageReceived);
//            context.startActivity(fireActivityIntent);
        }
        else{
            Toast.makeText(context, "Message Received", Toast.LENGTH_LONG).show();
        }
    }

        public void speak(String text)
        {
               tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }


}
