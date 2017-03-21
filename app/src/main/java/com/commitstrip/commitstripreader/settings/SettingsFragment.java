package com.commitstrip.commitstripreader.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.local.StripSharedPreferencesDataSource;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String keyReceiveNotification = StripSharedPreferencesDataSource.KEY_RECEIVE_NOTIFICATION;

        if (key.compareTo(keyReceiveNotification) == 0) {

            boolean receiveNotification = sharedPreferences.getBoolean(keyReceiveNotification, true);

            FirebaseApp.initializeApp(getActivity().getApplicationContext());

            if (receiveNotification) {
                FirebaseMessaging.getInstance().subscribeToTopic(Configuration.TOPIC_NAME);
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Configuration.TOPIC_NAME);
            }
        }
    }
}
