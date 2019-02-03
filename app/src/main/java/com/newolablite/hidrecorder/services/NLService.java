package com.newolablite.hidrecorder.services;

import android.content.Context;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.newolablite.hidrecorder.TinyDB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;

/**
 * Created by bledi on 12/13/15.
 */
public class NLService extends NotificationListenerService { //ignore the warning

    Context context;
    public Thread mythread;
    public int numberOfNotifications = 0;
    private TextToSpeech tts;
    public boolean started = false;
    public String FROM_FACEBOOK_CONSTANT = "From facebook, ";
    public String SAYS_FACEBOOK_CONSTANT = " says: ";
    Queue<String> allNotifications = new LinkedList<String>();
    HashMap<String, CharSequence[]> facebookMessages = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });
        context = getApplicationContext();

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //getAllNotifications();
        if (sbn.getPackageName().toLowerCase().contains("facebook"))
            readFacebookMessages();
        else if (sbn.getPackageName().toLowerCase().contains("whatsapp"))
            tts.speak("You have new notification from whatsapp", TextToSpeech.QUEUE_FLUSH, null);
        else if (sbn.getPackageName().toLowerCase().contains("gm"))
            tts.speak("You have new notification from gmail", TextToSpeech.QUEUE_FLUSH, null);
//        else
//            tts.speak("You have notification from unknown source", TextToSpeech.QUEUE_FLUSH, null);

        Log.d("SOURCE", sbn.getPackageName().toLowerCase());
    }

    public void readFacebookMessages()
    {
        TinyDB tinydb = new TinyDB(getApplicationContext());
        int ifActivatedFacebookFeed = tinydb.getInt("FACEBOOKNOTIFY");

        Log.d("SOURCE_FB", ifActivatedFacebookFeed+"");

        //todo ifActivatedFacebookFeed value incorrectly use !!!
//        if(ifActivatedFacebookFeed == 1)
//        {
            StatusBarNotification[] activeNos = getActiveNotifications();

            for (StatusBarNotification sb : activeNos) {
                if (!sb.getPackageName().toLowerCase().contains("facebook"))
                    continue;

                Bundle extras = sb.getNotification().extras;
                String key = extras.getString("android.title");

                Log.d("LOOOP", "000000");


                if (!facebookMessages.containsKey(key)) //If it is a new message
                {
                    Log.d("HERE1", "111111111");
                    if (extras.getCharSequence("android.text") != null) {
                        Log.d("HERE2", "222222222");

                        CharSequence[] charSequenceArray = new CharSequence[1];
                        charSequenceArray[0] = extras.getCharSequence("android.text").toString();
                        facebookMessages.put(key, charSequenceArray);
                        allNotifications.add(FROM_FACEBOOK_CONSTANT + key + SAYS_FACEBOOK_CONSTANT + charSequenceArray[0].toString());
                    } else if ((CharSequence[]) extras.get("android.textLines") != null) {
                        Log.d("HERE3", "333333333");

                        CharSequence[] tt = (CharSequence[]) extras.get("android.textLines");
                        CharSequence[] charSequences = new CharSequence[tt.length];


                        for (int i = 0; i < tt.length; i++) {
                            charSequences[i] = tt[i].toString();
                            allNotifications.add(FROM_FACEBOOK_CONSTANT + key + SAYS_FACEBOOK_CONSTANT + tt[i].toString());
                            Log.d("Printing", tt[i].toString());
                        }

                        Log.d("PRINTING...", "************");

                        facebookMessages.put(key, charSequences);

                    }
                } else {
                    Log.d("HERE4", "44444444");

                    if (extras.getCharSequence("android.text") != null) {
                        Log.d("HERE5", "55555555");

                        String newMessage = extras.getCharSequence("android.text").toString();

                        CharSequence[] charSequenceArray = facebookMessages.get(key);

                        boolean sameMessage = false;

                        for (int i = 0; i < charSequenceArray.length; i++) {
                            String existingMessage = charSequenceArray[i].toString();
                            if (newMessage.equals(existingMessage)) {
                                sameMessage = true;
                                break;
                            }
                        }

                        if (!sameMessage) {
                            CharSequence[] newCharSequenceArray = new CharSequence[charSequenceArray.length + 1]; //new values
                            for (int i = 0; i < charSequenceArray.length; i++)
                                newCharSequenceArray[i] = charSequenceArray[i];
                            newCharSequenceArray[charSequenceArray.length] = newMessage;
                            facebookMessages.put(key, newCharSequenceArray);
                            allNotifications.add(FROM_FACEBOOK_CONSTANT + key + SAYS_FACEBOOK_CONSTANT + newMessage);
                        }
                    } else if ((CharSequence[]) extras.get("android.textLines") != null) {
                        Log.d("HERE6", "666666");

                        CharSequence[] oldCharSequenceArray = facebookMessages.get(key);
                        CharSequence[] newCharSequenceArray = (CharSequence[]) extras.get("android.textLines");

                        int totalNewAdditions = 0;

                        Queue<Integer> positions = new LinkedList<>();

                        for (int i = 0; i < newCharSequenceArray.length; i++) {
                            boolean found = false;
                            Log.d("CANDIDAATAE", newCharSequenceArray[i].toString());
                            for (int j = 0; j < oldCharSequenceArray.length; j++) {
                                Log.d("LOOKING", oldCharSequenceArray[j].toString());
                                if (newCharSequenceArray[i].toString().equals(oldCharSequenceArray[j].toString())) //if it is the same message
                                {
                                    Log.d("HERE7", "7777777");
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                Log.d("HERE8", "88888");
                                Log.d("Misterious", newCharSequenceArray[i].toString());
                                totalNewAdditions++;
                                positions.add(i);
                            }
                        }

                        if (totalNewAdditions == 0)
                            continue;

                        CharSequence[] resultCharSequenceArray = new CharSequence[oldCharSequenceArray.length + totalNewAdditions]; //new values

                        int index = 0;
                        for (index = 0; index < oldCharSequenceArray.length; index++)
                            resultCharSequenceArray[index] = oldCharSequenceArray[index];

                        while (!positions.isEmpty()) {
                            int pos = positions.poll();
                            resultCharSequenceArray[index] = newCharSequenceArray[pos];
                            allNotifications.add(FROM_FACEBOOK_CONSTANT + key + SAYS_FACEBOOK_CONSTANT + newCharSequenceArray[pos].toString());
                            index++;
                        }

                        facebookMessages.put(key, resultCharSequenceArray);
                    }
                }
            }

            if (!allNotifications.isEmpty()) //speak out the message
                speak();

//        }

    }

    public void getAllNotifications()
    {
        StatusBarNotification[] activeNos = getActiveNotifications();


        Log.d("SIZE_activeNos", activeNos.length + "");

        CharSequence[] ch;

        /*for (StatusBarNotification sb: activeNos)
        {
            Log.d("PACKAGE NAME",  sb.getPackageName());
        }*/

        if (activeNos.length > 1)
        {
            Bundle extras = activeNos[2].getNotification().extras;
            //String text = extras.getCharSequence("android.text").toString();
            Log.d("PACKAGE NAME", activeNos[2].getPackageName());
            //Log.d("TEXT",  extras.toString());
            Set<String> keys = extras.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                String key = it.next();
                Log.d("Printing...","[" + key + "=" + extras.get(key)+"]");
            }

            String title = extras.getString("android.title");
            Log.d("TITLE", title);

            //Log.d("textLines...",extras.getCharSequence("android.textLines").toString());
            //2130840239

            CharSequence[] tt = (CharSequence[]) extras.get("android.textLines");
            Log.d("textLines...", tt[0].toString());

            //Log.d("Title", extras.getString(Notification.EXTRA_TITLE).toString());

            //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void speak()
    {
        if(!started) {
            started = true;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while (!allNotifications.isEmpty()) {
                        String text = allNotifications.poll();
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        while (tts.isSpeaking());
                        Log.d("SPEAKING", text);
                    }
                    Log.d("EXITTING", "FROM HERE");
                    started = false;
                }
            };
            mythread = new Thread(runnable);
            mythread.start();
            Log.d("EXITTING", "Also FROM HERE");
        }

    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d("Msg", "Notification Removed");

    }

}