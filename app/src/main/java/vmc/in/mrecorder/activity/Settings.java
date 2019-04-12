package vmc.in.mrecorder.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ls.directoryselector.DirectoryDialog;
import com.ls.directoryselector.DirectoryPreference;

import java.io.File;
import java.util.prefs.Preferences;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.syncadapter.SyncUtils;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.Utils;

public class Settings extends AppCompatActivity implements TAG {

    private Toolbar toolbar;


    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(Settings.this) < 6.0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MyPreferenceFragment())
                .commit();
        PreferenceManager.setDefaultValues(Settings.this, R.xml.settings, false);

    }


    public static class MyPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        private Preference storePathPrefs;
        private String smsText;
        private Context context;


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            final SwitchPreference recordingPreference = (SwitchPreference) findPreference("prefRecording");
            final SwitchPreference callPreference = (SwitchPreference) findPreference("prefOfficeTimeRecording");
            final SwitchPreference mcubecallPreference = (SwitchPreference) findPreference("prefMcubeRecording");
            final SwitchPreference debug = (SwitchPreference) findPreference("prefDebug");

            PreferenceCategory mCategory = (PreferenceCategory) findPreference("SMS_Category");
            //final SwitchPreference sms = (SwitchPreference) findPreference("prefSMS");
            final SwitchPreference askBeforeSend = (SwitchPreference) findPreference("prefAskSMSBefore");
            final SwitchPreference incomingSms = (SwitchPreference) findPreference("prefIncomingSMS");
            final SwitchPreference outgoingSms = (SwitchPreference) findPreference("prefOutgoingSMS");
            final SwitchPreference missedSms = (SwitchPreference) findPreference("prefMissedSMS");
            final EditTextPreference incomingsmsContent = (EditTextPreference) findPreference("prefIncomingSMSContent");
            final EditTextPreference outgoingsmsContent = (EditTextPreference) findPreference("prefOutgoingSMSContent");
            final EditTextPreference missedsmsContent = (EditTextPreference) findPreference("prefMissedSMSContent");
            //  mCategory.removePreference(missedsmsContent);
            incomingsmsContent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    smsText = newValue.toString();
                    incomingsmsContent.setSummary(smsText);
                    return true;
                }
            });
            outgoingsmsContent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    smsText = newValue.toString();
                    outgoingsmsContent.setSummary(smsText);
                    return true;
                }
            });

            missedsmsContent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    smsText = newValue.toString();
                    missedsmsContent.setSummary(smsText);
                    return true;
                }
            });


            if (incomingSms.isChecked())
                incomingsmsContent.setEnabled(true);
            else incomingsmsContent.setEnabled(false);
            if (outgoingSms.isChecked())
                outgoingsmsContent.setEnabled(true);
            else outgoingsmsContent.setEnabled(false);
            if (missedSms.isChecked())
                missedsmsContent.setEnabled(true);
            else missedsmsContent.setEnabled(false);

            //Place cursor to end of text
            PlaceCursor pcl = new PlaceCursor();
            incomingsmsContent.setOnPreferenceClickListener(pcl);
            outgoingsmsContent.setOnPreferenceClickListener(pcl);
            missedsmsContent.setOnPreferenceClickListener(pcl);


            incomingSms.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        if (selected) {
                            incomingsmsContent.setEnabled(true);
                        } else {
                            incomingsmsContent.setEnabled(false);
                        }
                    }
                    return true;
                }
            });
            outgoingSms.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        if (selected) {
                            outgoingsmsContent.setEnabled(true);
                        } else {
                            outgoingsmsContent.setEnabled(false);
                        }
                    }
                    return true;
                }
            });
            missedSms.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        if (selected) {
                            missedsmsContent.setEnabled(true);
                        } else {
                            missedsmsContent.setEnabled(false);
                        }
                    }
                    return true;
                }
            });

            storePathPrefs = findPreference("store_path");
            storePathPrefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary((String) newValue);
                    return true;
                }
            });


            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());

            boolean notifyMode = sharedPrefs.getBoolean("prefOfficeTimeRecording", false);
            boolean recording = sharedPrefs.getBoolean("prefRecording", true);
            boolean mcuberecording = sharedPrefs.getBoolean("prefMcubeRecording", false);
            boolean debugEnable = sharedPrefs.getBoolean("prefDebug", false);


            if (recording) {
                recordingPreference.setChecked(true);

            } else {
                recordingPreference.setChecked(false);
            }
            if (notifyMode) {

                callPreference.setChecked(true);
            } else {
                callPreference.setChecked(false);
            }
            if (mcuberecording) {
                mcubecallPreference.setChecked(true);

            } else {

                mcubecallPreference.setChecked(false);
            }
            if (debugEnable) {
                debug.setChecked(true);

            } else {
                debug.setChecked(false);
            }


            callPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (newValue instanceof Boolean) {
                        return false;
                    }
                    return false;
                }
            });

            recordingPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        return false;
                    }
                    return false;
                }
            });
            mcubecallPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        return false;
                    }
                    return false;
                }
            });
            debug.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        return false;
                    }
                    return false;
                }
            });


        }

        // To place cursor at end of the text on Edittext preference
        class PlaceCursor implements Preference.OnPreferenceClickListener {
            @Override
            public boolean onPreferenceClick(Preference pref) {
                EditTextPreference et = (EditTextPreference) pref;
                //String text= et.getEditText().toString().length()+" ";
                et.getEditText().setSelection(et.getText().length());
                return true;
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Preference storePathPrefs = findPreference("store_path");
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String storepath = sharedPrefs.getString("store_path", "null");
            if (storepath.equals("null")) {
                File sampleDir = Environment.getExternalStorageDirectory();
                File sample = new File(sampleDir.getAbsolutePath() + "/data");
                if (!sample.exists()) {
                    sample.mkdirs();
                }
                SharedPreferences.Editor ed = sharedPrefs.edit();
                ed.putString("store_path", sample.getAbsolutePath());
                ed.commit();
                storePathPrefs.setSummary(sample.getAbsolutePath());
            } else {
                storePathPrefs.setSummary(storepath);

            }


        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            EditTextPreference incomingsmsContent = (EditTextPreference) findPreference("prefIncomingSMSContent");
            incomingsmsContent.setSummary(incomingsmsContent.getText().toString().trim());
            EditTextPreference outgoingsmsContent = (EditTextPreference) findPreference("prefOutgoingSMSContent");
            outgoingsmsContent.setSummary(outgoingsmsContent.getText().toString().trim());
            EditTextPreference missedsmsContent = (EditTextPreference) findPreference("prefMissedSMSContent");
            // missedsmsContent.setSummary(missedsmsContent.getText().toString().trim());

            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        Preference singlePref = preferenceGroup.getPreference(j);
                        updatePreference(singlePref, singlePref.getKey());
                    }
                } else {
                    updatePreference(preference, preference.getKey());
                }
            }


        }

        private void updatePreference(Preference preference, String key) {
            if (preference == null) return;
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntry());
                return;
            }
            if (preference instanceof SwitchPreference) {
                SwitchPreference checkPreference = (SwitchPreference) preference;
                checkPreference.setSummary(checkPreference.getSummary());
                return;
            }
            if (preference instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) preference;
                // editTextPref.setSummary(smsText.equals("")?"Enter Text Here":smsText);

                return;
            }


        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            return v;
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            Preference pref = findPreference(key);
            boolean recording = sharedPreferences.getBoolean("prefRecording", true);
            CallApplication.getInstance().startRecording();
            Log.d("Alarm", "Settings Triggered stop Recording");
            updatePreference(pref, key);
            SyncUtils.CreateSyncAccount(getActivity());
            SyncUtils.updateSync();


        }

    }


}


