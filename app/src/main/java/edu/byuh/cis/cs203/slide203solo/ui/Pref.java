package edu.byuh.cis.cs203.slide203solo.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import edu.byuh.cis.cs203.slide203solo.R;

public class Pref extends PreferenceActivity {
    /**
     * This method creates the preferences and adds them to a preference screen.
     * It uses all externalized strings, this is good fro translation.
     * @param b
     */
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(this);
        SwitchPreference music = new SwitchPreference(this);
        music.setTitle(getResources().getString(R.string.music_title));
        music.setSummaryOn(getResources().getString(R.string.music_on));
        music.setSummaryOff(getResources().getString(R.string.music_off));
        music.setDefaultValue(true);
        music.setKey("MUSIC_PREF");


        ListPreference speed = new ListPreference(this);
        speed.setTitle(getResources().getString(R.string.speed_title));
        speed.setSummary(getResources().getString(R.string.speed_summary));
        speed.setKey("SPEED_PREF");
        String[] entries = {getResources().getString(R.string.speed_fast),getResources().getString(R.string.speed_medium),getResources().getString(R.string.speed_slow)};
        speed.setEntries(entries);
        String[] values = {"20","50","100"};
        speed.setEntryValues(values);
        speed.setDefaultValue("50");

        ps.addPreference(music);
        ps.addPreference(speed);
        setPreferenceScreen(ps);
    }

    /**
     * this is a façade method used to simplify the method call in another class
     * @param c
     * @return
     */
    public static boolean soundOn(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("MUSIC_PREF",true);
    }
    public static int getSpeed(Context c){
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(c).getString("SPEED_PREF","50"));
    }
}
