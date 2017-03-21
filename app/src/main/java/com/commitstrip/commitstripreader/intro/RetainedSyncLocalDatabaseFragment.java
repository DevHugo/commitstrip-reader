package com.commitstrip.commitstripreader.intro;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import com.commitstrip.commitstripreader.common.JobListener;
import com.commitstrip.commitstripreader.service.SyncLocalDatabaseWithRemote;

/**
 * This Fragment manages a single background task and retains
 * itself across configuration changes.
 */
public class RetainedSyncLocalDatabaseFragment extends Fragment {

  private JobListener mCallbacks;
  private SyncLocalDatabaseWithRemote mTask;
  private boolean isJobFinished;

  /**
   * Hold a reference to the parent Activity so we can report the
   * task's current progress and results. The Android framework
   * will pass us a reference to the newly created Activity after
   * each configuration change.
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mCallbacks = (JobListener) activity;
  }

  /**
   * This method will only be called once when the retained
   * Fragment is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Retain this fragment across configuration changes.
    setRetainInstance(true);

    Thread backgroundThread = new Thread(() -> {
      // Start to sync local database
      mTask = new SyncLocalDatabaseWithRemote();
      mTask.startSynchronize(getActivity().getApplicationContext(), mCallbacks);
    });

    backgroundThread.start();

    isJobFinished = false;
  }

  public boolean isJobFinished() {
    return isJobFinished;
  }

  public void setJobFinished(boolean jobFinished) {
    isJobFinished = jobFinished;
  }

  @Override public void onDestroy() {
    super.onDestroy();

    mTask.stop();
  }

  /**
   * Set the callback to null so we don't accidentally leak the
   * Activity instance.
   */
  @Override
  public void onDetach() {
    super.onDetach();
    mCallbacks = null;
  }


}
