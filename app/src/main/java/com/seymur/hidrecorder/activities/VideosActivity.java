package com.seymur.hidrecorder.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.seymur.hidrecorder.R;
import com.seymur.hidrecorder.adapters.ListAdapterHomeMain;
import com.seymur.hidrecorder.utils.TinyDB;
import com.seymur.hidrecorder.models.VideoFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by bledi on 12/19/15.
 */

@SuppressLint("NewApi")
public final class VideosActivity extends ActionBarActivity {

    ListView listView;
    ListAdapterHomeMain adapter;
    TinyDB tinyDB;
    boolean sortOrderAscending = true;

    @SuppressLint({"NewApi", "InvalidWakeLockTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ArrayList<String> filePaths = new ArrayList<String>();
        final ArrayList<String> filePathsImages = new ArrayList<String>();
        tinyDB = new TinyDB(getApplicationContext());

        File folder = new File(Environment.getExternalStorageDirectory() + "/hidrecorder");
        File folderImages = new File(Environment.getExternalStorageDirectory() + "/hidrecorder/images");
        if (folder.exists()) {

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


                final ArrayList<VideoFile> videoFiles = new ArrayList<VideoFile>();

                for (int i = 0; i < filePaths.size(); i++) {
                    VideoFile vf = new VideoFile();
                    vf.setFileDate(filePaths.get(i).substring(filePaths.get(i).lastIndexOf("/") + 1, filePaths.get(i).indexOf(".")));

                    String imageP = tinyDB.getString(filePaths.get(i));
                    if (!imageP.isEmpty() && imageP.length() > 0) {
                        File f = new File(imageP);
                        if (f.exists())
                            vf.setFileImage(loadImageFromStorage(imageP));
                        else {
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePaths.get(i), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                            Matrix matrix = new Matrix();
                            Bitmap bitmap = Bitmap.createBitmap(thumb, 0, 0,
                                    thumb.getWidth(), thumb.getHeight(), matrix, true);
                            //tinyDB.putImage(outputFileFolder+"/images",outputFileName+".png", bitmap );

                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(imageP);
                                // Use the compress method on the BitMap object to write image to the OutputStream
                                Bitmap.createScaledBitmap(bitmap, 128, 72, false).compress(Bitmap.CompressFormat.PNG, 10, fos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            vf.setFileImage(loadImageFromStorage(imageP));
                        }
                    } else
                        vf.setFileImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_movie));
                    videoFiles.add(vf);
                }


                ImageButton filterButton = (ImageButton) findViewById(R.id.ibFilter);
                listView = (ListView) findViewById(R.id.commandsList);

                adapter = new ListAdapterHomeMain(getApplicationContext(), videoFiles, new ListAdapterHomeMain.ICallback() {
                    @Override
                    public void onPlayEvent(int position) {
                        File file = new File(filePaths.get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "video/*");
                        startActivity(intent);
                    }

                    @Override
                    public void onShareEvent(int position) {
                        shareVideo("Recorded with HiddenRecorder", filePaths.get(position));
                    }

                    @Override
                    public void onDeleteEvent(final int position) {
                        LayoutInflater inflater = VideosActivity.this.getLayoutInflater();

                        View layout = inflater.inflate(R.layout.delete_dialog, null);
                        final AlertDialog alertContact = new AlertDialog.Builder(VideosActivity.this)
                                .setView(layout)
                                .setIcon(R.drawable.tet)
                                .show();
                        AppCompatButton buttonYes = (AppCompatButton) layout.findViewById(R.id.btnYes);
                        AppCompatButton buttonNo = (AppCompatButton) layout.findViewById(R.id.btnNo);
                        alertContact.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));
                        TextView infoText = (TextView) layout.findViewById(R.id.textViewInfo);
                        //Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/capture.ttf");
                        //infoText.setTypeface(font);
                        Typeface fontuc = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/uicksandregular.otf");
                        infoText.setTypeface(fontuc);
                        buttonYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                File file = new File(filePaths.get(position));
                                boolean result = file.delete();
                                if (result) {
                                    videoFiles.remove(position);
                                    adapter.refreshEvents(videoFiles);
                                } else {
                                    Toast.makeText(VideosActivity.this, "Error occurred on delete!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        buttonNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                    }

                });
                listView.setAdapter(adapter);

                filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sortOrderAscending = !sortOrderAscending;
                        Collections.sort(videoFiles, new CustomComparator(sortOrderAscending));
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onDestroy() {
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

    private Bitmap loadImageFromStorage(String path) {
        Bitmap b = null;
        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return b;

    }


    public void shareVideo(final String title, String path) {

        MediaScannerConnection.scanFile(VideosActivity.this, new String[]{path},

                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        getApplicationContext().startActivity(Intent.createChooser(shareIntent,
                                title));

                    }
                });
    }


    public void deleteVideo(String path) {
        File dir = getFilesDir();
        File file = new File(dir, "my_filename");
        boolean deleted = file.delete();
    }

    //                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(e, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
//                    Matrix matrix = new Matrix();
//                    Bitmap bitmap = Bitmap.createBitmap(thumb, 0, 0,
//                            thumb.getWidth(), thumb.getHeight(), matrix, true);
    // thumb.getWidth(), thumb.getHeight(), matrix, true);

    public class CustomComparator implements Comparator<VideoFile> {
        boolean sortOrder;

        public CustomComparator(boolean sortOrderAscending) {
            this.sortOrder = sortOrderAscending;
        }

        @Override
        public int compare(VideoFile o1, VideoFile o2) {
            if (sortOrder)
                return o1.getFileDate().compareTo(o2.getFileDate());
            else
                return o2.getFileDate().compareTo(o1.getFileDate());
        }
    }

}
