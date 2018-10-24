package com.app.tetris.hidrecorder.indicator;

/**
 * Created by bledi on 11/18/15.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.tetris.hidrecorder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LayoutOne extends Fragment {
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

    public static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_one, null);
        TextView ikinci = (TextView)root.findViewById(R.id.textViewLay1);
        final Context ctx = getContext();
        Typeface fontiki = Typeface.createFromAsset(ctx.getAssets(), "fonts/capture.ttf");
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
        SimpleAdapter adapter = new SimpleAdapter(ctx, list, R.layout.list_back, from, to);

        listView = (ListView) root.findViewById(R.id.commandsList);

        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
               // LayoutInflater inflater = LayoutOne.this.getLayoutInflater();
                Typeface font = Typeface.createFromAsset(ctx.getAssets(), "fonts/capture.ttf");
                View layout = inflater.inflate(R.layout.command_info, null);
                final AlertDialog alertContact = new AlertDialog.Builder(getContext())
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
        return root;
    }



}