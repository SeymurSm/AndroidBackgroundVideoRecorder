package com.app.tetris.hidrecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tetris.hidrecorder.services.ShakeDetectAndMessageSend;

/**
 * Created by Seymur on 15.11.2015.
 */

public class Settings extends PreferenceActivity  {

    Button share,moreapps,about,Adduser;

    String currText="";





    int Type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preference);

        Preference About = findPreference("About");
        About.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                final AlertDialog alertDialog = new AlertDialog.Builder(Settings.this)
                        .setTitle("About")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(Html.fromHtml(String.format("<font color='#bbdefb'>Thanks to use our product. We would like to get your feedback and suggestions.\n   e-mail:<font color='#088A68'>  info@tetris.com </font>", "#000000")))
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .show();
                return true;
            }
        });



        Preference More_apps = (Preference) findPreference("more_apps");
        More_apps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
                return true;
            }
        });


        Preference Share = (Preference) findPreference("Share");
        Share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String shareBody = "http://www.google.com";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "extra");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "SENDING"));
                return true;
            }
        });

        Preference videoDuration = (Preference) findPreference("videoDuration");
        videoDuration.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater inflater = Settings.this.getLayoutInflater();
                View layout = inflater.inflate(R.layout.videe_length_dialog, null);

                final NumberPicker minutePicker = (NumberPicker) layout.findViewById(R.id.numberPicker);
                final NumberPicker minutePickerSecond = (NumberPicker) layout.findViewById(R.id.numberPickerSecond);

                minutePickerSecond.setMaxValue(60);
                minutePickerSecond.setMinValue(10);
                minutePicker.setMaxValue(60);
                minutePicker.setMinValue(0);
                minutePicker.setWrapSelectorWheel(false);
                minutePickerSecond.setWrapSelectorWheel(false);

                final AlertDialog alertContact = new AlertDialog.Builder(Settings.this)
                        .setView(layout)
                        .setTitle("                  Set Video Length ")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                    TinyDB tinydb = new TinyDB(getApplicationContext());
                                    tinydb.putInt("VIDEO_DURATION", minutePicker.getValue()*60+ minutePickerSecond.getValue());

                            }
                        })
                        .show();


                return true;
            }
        });

        final TinyDB tiny = new TinyDB(getApplicationContext());





        TinyDB tinydb = new TinyDB(getApplicationContext());



        ListPreference listPreference = (ListPreference) findPreference("videoRecord");
        int type = tinydb.getInt("VIDEO_QUALITY");

        if(type==0) {
            listPreference.setSummary("High");
            currText="High";
        }
        else if(type==1) {
            listPreference.setSummary("Medium");
            currText="Medium";
        }
        else if(type==2) {
            listPreference.setSummary("Low");
            currText="Low";
        }


        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                currText = newValue.toString();
                return true;
            }
        });




    }














    @Override
    public void onBackPressed() {

        done();
    }



    public void done(){

        TinyDB tinydb = new TinyDB(getApplicationContext());
        if( currText.equals("High")){

            Type = 0;
        }
        else if (currText.equals("Medium")){

            Type = 1;
        }
        else if( currText.equals("Low")){

            Type = 2;
        }



        tinydb.putInt("VIDEO_QUALITY", Type);

        finish();
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

    };




}
