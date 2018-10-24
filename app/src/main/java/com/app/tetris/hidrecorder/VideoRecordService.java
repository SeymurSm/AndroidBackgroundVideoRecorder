package com.app.tetris.hidrecorder;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.util.Date;

/**
 * Created by Seymur 3.4.2016
 */

public class VideoRecordService extends Service implements SurfaceHolder.Callback {

    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;
    int videoQuality = 0;
    int videoDuration = 10;
    TinyDB tinyDB;
    private TextToSpeech tts;
    @Override
    public void onCreate() {
//        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    //  tts.setLanguage(Locale.US);
//                    tts.setLanguage(Locale.getDefault());
//                }
//            }
//        });
//        tts.speak("Recording started wait 20 seconds", TextToSpeech.QUEUE_FLUSH, null);
        tinyDB = new TinyDB(getApplicationContext());
        videoQuality = tinyDB.getInt("VIDEO_QUALITY");
        videoDuration = tinyDB.getInt("VIDEO_DURATION");

        int ifWithButton = tinyDB.getInt("VIDEO_RECORDING");
        // Start foreground service to avoid unexpected kill
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setContentTitle("Background Video Recorder")
                    .setContentText("")
                    .setSmallIcon(R.drawable.webcam)
                    .build();
        }
        startForeground(1234, notification);





        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceView.getHolder().addCallback(this);


        final Handler handler = new Handler();
        if(ifWithButton == 0) {
            if(videoDuration>10) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                      //  startService(new Intent(VideoRecordService.this, NewService.class));
                    }
                }, videoDuration * 1000);
            }
            else{
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                      //  startService(new Intent(VideoRecordService.this, NewService.class));
                    }
                }, 10 * 1000);
            }
        }    

    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        camera = Camera.open();
        mediaRecorder = new MediaRecorder();
        camera.unlock();

        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if(videoQuality == 0)
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        else if(videoQuality == 1)
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        else
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

        File folder = new File(Environment.getExternalStorageDirectory() + "/hidrecorder");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        mediaRecorder.setOutputFile(
                Environment.getExternalStorageDirectory() + "/hidrecorder/" +
                        DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime()) +
                        ".mp4"
        );

        try { mediaRecorder.prepare(); } catch (Exception e) {}
        mediaRecorder.start();

    }

    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {

        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        camera.lock();
        camera.release();
        windowManager.removeView(surfaceView);

    }


    public void speak(String s)
    {
        tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);

        while (tts.isSpeaking());
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}

    @Override
    public IBinder onBind(Intent intent) { return null; }

}
