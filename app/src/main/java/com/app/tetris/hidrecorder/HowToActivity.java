package com.app.tetris.hidrecorder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bledi on 12/19/15.
 */

@SuppressLint("NewApi") public final  class HowToActivity extends ActionBarActivity {

    ListView listView;


    @SuppressLint({"NewApi", "InvalidWakeLockTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);



        final ArrayList<String> filePaths = new ArrayList<String>();
        final ArrayList<String> filePathsImages = new ArrayList<String>();


        File folder = new File(Environment.getExternalStorageDirectory() + "/hidrecorder");
        File folderImages = new File(Environment.getExternalStorageDirectory() + "/hidrecorder/images");
        if(folder.exists()) {

            if (folder.listFiles().length > 0) {
                for (File f : folder.listFiles()) {
                    if (f.isFile()) {
                        String name = f.getName().toString();
                        filePaths.add(Environment.getExternalStorageDirectory() + "/hidrecorder/" + name);
                    }
                    // Do your stuff
                }
                if (folderImages.listFiles().length > 0) {
                    for (File f : folderImages.listFiles()) {
                        if (f.isFile()) {
                            String nameImage = f.getName().toString();
                            filePathsImages.add(Environment.getExternalStorageDirectory() + "/hidrecorder/images/" + nameImage);
                        }
                        // Do your stuff
                    }
                }

                TextView ikinci = (TextView) findViewById(R.id.textViewLay1);
                Typeface fontiki = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");
                ikinci.setTypeface(fontiki);
                ikinci.setText("Records \n\n");


                ArrayList<VideoFile> videoFiles = new ArrayList<VideoFile>();

                for (int i = 0;i<filePaths.size(); i++) {
//                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(e, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
//                    Matrix matrix = new Matrix();
//                    Bitmap bitmap = Bitmap.createBitmap(thumb, 0, 0,
//                            thumb.getWidth(), thumb.getHeight(), matrix, true);
                           // thumb.getWidth(), thumb.getHeight(), matrix, true);

                    VideoFile vf = new VideoFile();
                    vf.setFileDate(filePaths.get(i).substring(filePaths.get(i).lastIndexOf("/")+1, filePaths.get(i).indexOf(".")));
                    vf.setFileImage(loadImageFromStorage(filePathsImages.get(i)));
                    videoFiles.add(vf);
                }

                listView = (ListView) findViewById(R.id.commandsList);

                ListAdapterHomeMain adapter = new ListAdapterHomeMain(getApplicationContext(), videoFiles, new ListAdapterHomeMain.ICallback() {
                    @Override
                    public void onPlayEvent(int position) {
                        Toast.makeText(HowToActivity.this, position+"", Toast.LENGTH_SHORT).show();
                        File file = new File(filePaths.get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "video/*");
                        startActivity(intent);
                    }

                });
                listView.setAdapter(adapter);
            }
        }

    }




    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(getApplicationContext(),MainActivity.class);
        finish();
        startActivity(intent);
    }





    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();

        Log.v("MyApp", "onDestroy");
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private Bitmap loadImageFromStorage(String path)
    {
        Bitmap b = null;
        try {
            File f=new File(path);
             b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return b;

    }

}
