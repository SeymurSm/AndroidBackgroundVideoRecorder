package com.app.tetris.hidrecorder;

import android.graphics.Bitmap;

public class VideoFile {

    private String fileDate;
    private Bitmap fileImage;

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public Bitmap getFileImage() {
        return fileImage;
    }

    public void setFileImage(Bitmap fileImage) {
        this.fileImage = fileImage;
    }
}
