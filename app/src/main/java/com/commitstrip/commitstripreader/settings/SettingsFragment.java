package com.commitstrip.commitstripreader.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.configuration.Configuration;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

}
