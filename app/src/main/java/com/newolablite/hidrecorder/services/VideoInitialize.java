package com.newolablite.hidrecorder.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VideoInitialize extends Service {
    public VideoInitialize() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onCreate() {

        startService(new Intent(VideoInitialize.this, VideoRecordService.class));

    }
}
