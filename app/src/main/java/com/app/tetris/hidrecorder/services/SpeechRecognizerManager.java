package com.app.tetris.hidrecorder.services;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ASUS on 2.4.2016.
 */
public final class SpeechRecognizerManager {

    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    protected boolean flag = true;

    protected boolean mIsListening;
    private boolean mIsStreamSolo;


    private boolean mMute=false;



    private final static String TAG="SpeechRecognizerManager";

    private onResultsReady mListener;
    public static int counter = 0;




    public SpeechRecognizerManager(Context context,onResultsReady listener)
    {
        try{
            mListener=listener;
        }
        catch(ClassCastException e)
        {
            Log.e(TAG, e.toString());
        }
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());
        startListening();

    }

    private synchronized void listenAgain()
    {
        if(mIsListening) {
            Log.d(TAG,"startListening");
            mIsListening = false;
            mSpeechRecognizer.cancel();
            startListening();

        }
    }


    private synchronized void startListening()
    {
        if(!mIsListening)
        {
            mIsListening = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // turn off beep sound
                if (!mIsStreamSolo && mMute) {
                    /*
                    mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    mIsStreamSolo = true;  */
                }
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            flag = true;
        }
    }

    public void destroy()
    {
        mIsListening=false;
        if (!mIsStreamSolo) {
         /*   mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            mIsStreamSolo = true;   */
        }
        Log.d(TAG, "onDestroy");
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer=null;
        }

    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {}

        @Override
        public  void onError(int error)
        {
            if(flag) {
                flag = false;
                if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    Log.d(TAG, "busy");
                    if (mListener != null) {
                        ArrayList<String> errorList = new ArrayList<String>(1);
                        errorList.add("ERROR RECOGNIZER BUSY");

                        if (mListener != null)
                            mListener.onResults(errorList);
                    }
                    return;
                }

                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    if (mListener != null)
                        mListener.onResults(null);
                }

                if (error == SpeechRecognizer.ERROR_NETWORK) {
                    ArrayList<String> errorList = new ArrayList<String>(1);
                    errorList.add("STOPPED LISTENING");
                    if (mListener != null)
                        mListener.onResults(errorList);
                }
                synchronized (this.getClass()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "running");
                            listenAgain();

                        }
                    }, 100);
                }
            }

        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params) {}

        @Override
        public synchronized void onResults(Bundle results)
        {
            if(results!=null && mListener!=null)
                mListener.onResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
            listenAgain();

        }

        @Override
        public void onRmsChanged(float rmsdB) {}

    }

    public boolean ismIsListening() {
        return mIsListening;
    }


    public interface onResultsReady
    {
        public void onResults(ArrayList<String> results);
    }

    public void mute(boolean mute)
    {
        mMute=mute;
    }

    public boolean isInMuteMode()
    {
        return mMute;
    }
}
