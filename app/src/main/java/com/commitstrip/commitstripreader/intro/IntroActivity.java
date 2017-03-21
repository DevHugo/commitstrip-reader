package com.commitstrip.commitstripreader.intro;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.JobListener;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.liststrip.ListStripActivity;
import com.commitstrip.commitstripreader.service.DownloadImageService;
import com.commitstrip.commitstripreader.util.CheckInternetConnection;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class IntroActivity extends AppIntro2 implements JobListener {

    private Integer totalSlides = 3;
    private Integer currentSlide = 0;

    private static final String TAG_RETAINED_FRAGMENT = "tag_retained_fragment";
    private RetainedSyncLocalDatabaseFragment mTaskFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setWizardMode(true);

        if (!CheckInternetConnection.isOnline()) {

            addSlide(AppIntro2Fragment.newInstance(
                    getString(com.commitstrip.commitstripreader.R.string.no_internet_connexion),
                    getString(R.string.description_no_internet_connexion),
                    R.drawable.adminsys_doubt,
                    Color.parseColor("#F1F6F9"),
                    Color.parseColor("#000000"), Color.parseColor("#000000"))
            );

            setSwipeLock(true);
            setProgressButtonEnabled(false);
        }
        else {

            FragmentManager fm = getFragmentManager();
            mTaskFragment = (RetainedSyncLocalDatabaseFragment) fm.findFragmentByTag(
                    TAG_RETAINED_FRAGMENT);

            // If the Fragment is non-null, then it is currently being
            // retained across a configuration change.
            if (mTaskFragment == null) {
                mTaskFragment = new RetainedSyncLocalDatabaseFragment();
                fm.beginTransaction().add(mTaskFragment, TAG_RETAINED_FRAGMENT).commit();
            }

            addSlide(AppIntro2Fragment.newInstance(
                    getString(com.commitstrip.commitstripreader.R.string.title_tutorial),
                    getString(R.string.description_tutorial),
                    R.drawable.welcome_strip,
                    Color.parseColor("#F1F6F9"),
                    Color.parseColor("#000000"), Color.parseColor("#000000"))
            );

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getBaseContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                    addSlide(AppIntro2Fragment.newInstance(
                            getString(com.commitstrip.commitstripreader.R.string.title_permission),
                            getString(R.string.description_permission),
                            R.drawable.adminsys_doubt,
                            Color.parseColor("#FFFFFF"),
                            Color.parseColor("#000000"), Color.parseColor("#000000"))
                    );

                    askForPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    totalSlides--;
                }
            } else {
                totalSlides--;
            }

            addSlide(AppIntro2Fragment.newInstance(
                    getString(R.string.title_synchronize),
                    getString(R.string.description_synchronize),
                    R.drawable.logo_commitstrip,
                    Color.parseColor("#FFFFFF"),
                    Color.parseColor("#000000"), Color.parseColor("#000000"))
            );
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(getApplicationContext(), ListStripActivity.class);
        startActivity(intent);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("firstrun", false).apply();

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        currentSlide++;

        if (currentSlide.equals(totalSlides)) {

            if (!getTaskFragment().isJobFinished()) {
                setSwipeLock(true);
                setNextPageSwipeLock(false);
            }

            // Ask Firebase to register the device in the topic. Used for receiving notification.
            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseMessaging.getInstance().subscribeToTopic(Configuration.TOPIC_NAME);
        }
    }

    @Override
    public void onPreExecute() {}

    @Override
    public void onProgressUpdate(int percent) {}

    @Override
    public void onCancelled() {
        getTaskFragment().setJobFinished(true);

        new Handler(Looper.getMainLooper()).post(() -> setSwipeLock(false));
    }

    @Override
    public void onPostExecute() {
        getTaskFragment().setJobFinished(true);

        new Handler(Looper.getMainLooper()).post(() -> {
            setSwipeLock(false);
        });
    }

    public RetainedSyncLocalDatabaseFragment getTaskFragment() {
        FragmentManager fm = getFragmentManager();
        return (RetainedSyncLocalDatabaseFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);
    }
}
