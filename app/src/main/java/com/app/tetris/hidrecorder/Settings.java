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
    static final int PICK_CONTACT=1;
    private Spinner spinner1, spinner2;
    private static TextToSpeech myTts;
    private TextView texttim;
    private TextToSpeech tts;
    Button share,moreapps,about,Adduser;
    EditTextPreference editTextPreference;
    String currText="";
  //  String currTextDur="";
    String currValue ;
    String cNumber;
    String nameOfEmergencyPerson;
 //   private Intent callDetect;
    private Intent  shakeDetectService;
    private static final int REQUEST_CODE = 1;
    Preference emergencyPerson;
    Preference shakeIntensity;
    int shakeValue  = 15, durationValue = 300 , directionValue = 4;


    int Type = 0;
  //  int TypeDur = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // callDetect = new Intent(this, CallDetectService.class);
      //  locationService = new Intent(this, LocationService.class);
        shakeDetectService = new Intent(this, ShakeDetectAndMessageSend.class);




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




        System.out.println("BURDAAAAAA111");
        Preference More_apps = (Preference) findPreference("more_apps");
        More_apps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
                return true;
            }
        });
        System.out.println("BURDAAAAA2222");

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


        emergencyPerson = (Preference) findPreference("EmergencyPerson");
        final TinyDB tiny = new TinyDB(getApplicationContext());
        String nam = tiny.getString("EMERGENCYNAME");
        if(nam.equals("") || nam.equals(null))
            emergencyPerson.setSummary("Not set");
        else
            emergencyPerson.setSummary(nam);

        emergencyPerson.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater inflater = Settings.this.getLayoutInflater();

                View layout = inflater.inflate(R.layout.contact, null);
                //	final DatePicker reminderPick=(DatePicker)layout.findViewById(R.id.datePicker);
                //   final EditText numberEdit = (EditText) layout.findViewById(R.id.addNumberEditer);

                final AlertDialog alertContact = new AlertDialog.Builder(Settings.this)
                        .setView(layout)
                        .setTitle("                  Pick a Contact ")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TinyDB tinydb;
                                tinydb = new TinyDB(getApplicationContext());
                                tinydb.putString("EMERGENCYNUMBER", cNumber);
                            }
                        })

					/*.setIcon(R.drawable
							.pin)*/
                        .show();
                final ImageButton contactPick = (ImageButton) layout.findViewById(R.id.pickContactDialogButton);
                contactPick.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        Uri uri = Uri.parse("content://contacts");
                        Intent intent = new Intent(Intent.ACTION_PICK, uri);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(intent, REQUEST_CODE);
                        alertContact.hide();

                    }
                });

                return true;
            }
        });


        shakeIntensity = (Preference) findPreference("ShakeIntensity");
        shakeIntensity.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater inflater = Settings.this.getLayoutInflater();

                View layout = inflater.inflate(R.layout.shake_seekbar, null);
                //	final DatePicker reminderPick=(DatePicker)layout.findViewById(R.id.datePicker);
                //   final EditText numberEdit = (EditText) layout.findViewById(R.id.addNumberEditer);
                final TinyDB tinyDB = new TinyDB(getApplicationContext());
                final SeekBar intensitySeekbar = (SeekBar) layout.findViewById(R.id.seekBar);
                final SeekBar durationSeekbar = (SeekBar) layout.findViewById(R.id.seekBarDuration);
                final SeekBar directionSeekbar = (SeekBar) layout.findViewById(R.id.seekBarDirectChange);

                intensitySeekbar.setMax(18);
                durationSeekbar.setMax(350);
                directionSeekbar.setMax(6);

                intensitySeekbar.setProgress( tinyDB.getInt("SHAKEVALUE"));
                directionSeekbar.setProgress( tinyDB.getInt("DIRECTIONVALUE"));
                durationSeekbar.setProgress( tinyDB.getInt("DURATIONVALUE"));

                final AlertDialog alertContact = new AlertDialog.Builder(Settings.this)
                        .setView(layout)
                        .setTitle("Change Crash Parameters")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                System.out.println(shakeValue);
                                tinyDB.putInt("SHAKEVALUE", intensitySeekbar.getProgress());
                                tinyDB.putInt("DURATIONVALUE",durationSeekbar.getProgress());
                                tinyDB.putInt("DIRECTIONVALUE",directionSeekbar.getProgress());
                            }
                        })
                        .show();

                intensitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        progress = progresValue;
                       // Toast.makeText(getApplicationContext(), "New Shake Intensity is "+progress, Toast.LENGTH_SHORT).show();
                        shakeValue = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                      //  Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        Toast.makeText(getApplicationContext(), "New Min Force is " + progress, Toast.LENGTH_SHORT).show();
                    }
                });



                durationSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        progress = progresValue;
                        durationValue = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //  Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        Toast.makeText(getApplicationContext(), "New Min Duration time is " + progress, Toast.LENGTH_SHORT).show();
                    }
                });



                directionSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        progress = progresValue;
                        directionValue = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //  Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        Toast.makeText(getApplicationContext(), "New Min Direction Change is " + progress, Toast.LENGTH_SHORT).show();
                    }
                });


                return true;
            }
        });








        final SwitchPreference locationEnable = (SwitchPreference) findPreference("location");

        if(tiny.getInt("LOCATIONACTIVE")==1){
            locationEnable.setSummary("Activated");
            locationEnable.setChecked(true);
        }
        else{
            locationEnable.setSummary("Deactivated");
            locationEnable.setChecked(false);
        }

//        locationEnable.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                if (locationEnable.isChecked()) {
//                    startService(locationService);
//                    locationEnable.setSummary("Activated");
//                    TinyDB tinydb = new TinyDB(getApplicationContext());
//                    tinydb.putInt("LOCATIONACTIVE", 1);
//                } else {
//                    stopService(locationService);
//                    locationEnable.setSummary("Deactivated");
//                    TinyDB tinydb = new TinyDB(getApplicationContext());
//                    tinydb.putInt("LOCATIONACTIVE", 0);
//                }
//                //code for what you want it to do
//                return true;
//            }
//        });
        final SwitchPreference shakeService = (SwitchPreference) findPreference("shakeService");
        if(tiny.getInt("CRASHDETECT")==1)
        {
            shakeService.setSummary("Activated");
            shakeService.setChecked(true);
        }
        else
        {
            shakeService.setSummary("Deactivated");
            shakeService.setChecked(false);
        }


        shakeService.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(shakeService.isChecked())
                {
                    locationEnable.setEnabled(true);
                    startService(shakeDetectService);
                    shakeService.setSummary("Activated");
                    tiny.putInt("CRASHDETECT", 1);
                }
                else{

                    stopService(shakeDetectService);
                    //stopService(locationService);
                    locationEnable.setEnabled(false);
                    locationEnable.setSummary("Deactivated");
                    shakeService.setSummary("Deactivated");
                    tiny.putInt("CRASHDETECT",0);

                }
                return true;
            }
        });

        if(shakeService.isChecked()){
            locationEnable.setEnabled(true);
        }
        else{
            locationEnable.setEnabled(false);
            locationEnable.setSummary("Deactivated");
        }







        final SwitchPreference incommingCall = (SwitchPreference) findPreference("incommingcall");
        if(incommingCall.isChecked())
            incommingCall.setSummary("Activated");
        else
            incommingCall.setSummary("Deactivated");
        incommingCall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(incommingCall.isChecked())
                {
                    incommingCall.setSummary("Activated");
                   // startService(callDetect);
                }
                else{
                    incommingCall.setSummary("Deactivated");
                   // stopService(callDetect);
                }
                //code for what you want it to do
                return true;
            }
        });


        System.out.println("BURDAAAAAA333");
        String  username ="Enter Your Name";


        TinyDB tinydb = new TinyDB(getApplicationContext());
        username = tinydb.getString("USERNAME");


        System.out.println("BURDAAAAAA44");

        editTextPreference =  (EditTextPreference)findPreference("Usarname");
        if(username.equals(null)||username.equals("")){
            username="";
            editTextPreference.setSummary("Add User");
        }
        else
            editTextPreference.setSummary("Current user: "+username);


        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(newValue.toString().equals("")||newValue.toString().equals(null))
                    editTextPreference.setSummary("Add User");
                else
                    editTextPreference.setSummary("Current User: " + newValue.toString());

                TinyDB tinydb = new TinyDB(getApplicationContext());
                tinydb.putString("USERNAME", newValue.toString());


                return false;
            }
        });


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


//        ListPreference listPreferenceDurationVid = (ListPreference) findPreference("videoLength");
//        int typeDuration = tinydb.getInt("VIDEO_DURATION");
//
//        if(typeDuration==0) {
//            listPreferenceDurationVid.setSummary("10 seconds");
//            currTextDur="10";
//        }
//        else if(typeDuration==1) {
//            listPreferenceDurationVid.setSummary("20 seconds");
//            currTextDur="20";
//        }
//        else if(typeDuration==2) {
//            listPreferenceDurationVid.setSummary("30 seconds");
//            currTextDur="30";
//        }
//
//
//        listPreferenceDurationVid.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                preference.setSummary(newValue.toString());
//                currTextDur = newValue.toString();
//                return true;
//            }
//        });



        final SwitchPreference fromFacebook = (SwitchPreference) findPreference("fromfacebook");
        if(fromFacebook.isChecked())
            fromFacebook.setSummary("Activated");
        else
            fromFacebook.setSummary("Deactivated");
        fromFacebook.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (fromFacebook.isChecked()) {

                    fromFacebook.setSummary("Activated");
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    tinydb.putInt("FACEBOOKNOTIFY", 1);
                } else {

                    fromFacebook.setSummary("Deactivated");
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    tinydb.putInt("FACEBOOKNOTIFY", 0);
                }
                //code for what you want it to do
                return true;
            }
        });



        final SwitchPreference fromMessage = (SwitchPreference) findPreference("fromgsmessage");
        if(fromMessage.isChecked())
            fromMessage.setSummary("Activated");
        else
            fromMessage.setSummary("Deactivated");
        fromMessage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (fromMessage.isChecked()) {

                    fromMessage.setSummary("Activated");
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    tinydb.putInt("MESSAGENOTIFY", 1);
                } else {

                    fromMessage.setSummary("Deactivated");
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    tinydb.putInt("MESSAGENOTIFY", 0);
                }
                //code for what you want it to do
                return true;
            }
        });



        final ListPreference listPreferenceLanguage = (ListPreference) findPreference("language");

        int typeLang = tinydb.getInt("LANGUAGE");

        if(typeLang==0) {
            listPreferenceLanguage.setSummary("Türkçe");
            listPreferenceLanguage.setIcon(R.drawable.turkey);
            listPreferenceLanguage.setTitle("Dil seçimi");
        }
        else if(typeLang==1) {
            listPreferenceLanguage.setSummary("English");
            listPreferenceLanguage.setIcon(R.drawable.england);
            listPreferenceLanguage.setTitle("Select Language");
        }


        listPreferenceLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                if(newValue.toString().equals("0")){
                    listPreferenceLanguage.setSummary("Türkçe");
                    listPreferenceLanguage.setIcon(R.drawable.turkey);
                    listPreferenceLanguage.setTitle("Dil seçimi");
                }
                else if(newValue.toString().equals("1")){
                    listPreferenceLanguage.setSummary("English");
                    listPreferenceLanguage.setIcon(R.drawable.england);
                    listPreferenceLanguage.setTitle("Select Language");
                }

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

        if(currText.equals("0"))
            tinydb.putInt("LANGUAGE",0);
        else if(currText.equals("1"))
            tinydb.putInt("LANGUAGE",1);


//        if( currTextDur.equals("10")){
//
//            TypeDur = 0;
//        }
//        else if (currTextDur.equals("20")){
//
//            TypeDur = 1;
//        }
//        else if( currTextDur.equals("30")){
//
//            TypeDur = 2;
//        }


        tinydb.putInt("VIDEO_QUALITY", Type);
       // tinydb.putInt("VIDEO_DURATION", TypeDur);


        finish();
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
                System.out.println(number);
                Toast.makeText(getApplicationContext(),number,Toast.LENGTH_LONG).show();
                TinyDB tinydb = new TinyDB(getApplicationContext());
                tinydb.putString("EMERGENCYNUMBER", number);
                tinydb.putString("EMERGENCYNAME", name);
                // Log.d(TAG, "ZZZ number : " + number + " , name : " + name);
                cNumber = number;
                nameOfEmergencyPerson = name;
                emergencyPerson.setSummary(name);

            }
        }
    };




}
