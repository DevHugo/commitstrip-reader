package com.commitstrip.commitstripreader.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.StripRepositorySingleton;
import com.commitstrip.commitstripreader.dto.NotificationDataPayload;
import com.commitstrip.commitstripreader.dto.SimpleStripDto;
import com.commitstrip.commitstripreader.strip.StripActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Inject
    StripRepository mStripRepository;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Gson gson = new Gson();
            try {
                // Parse the notification and send it to the user.
                NotificationDataPayload notificationData = gson.fromJson(remoteMessage.getData().toString(), NotificationDataPayload.class);

                // Use dagger to inject field, we need to save the strip content
                DaggerMyFirebaseMessagingServiceComponent.builder()
                        .stripRepositoryComponent(StripRepositorySingleton.getInstance(getApplicationContext()).getStripRepositoryComponent())
                        .build().inject(this);

                List<SimpleStripDto> list = notificationData.getStrips();

                if (list.size() == 1)
                    sendNotification(list.get(0), getString(R.string.new_strip_published), list.get(0).getTitle());
                else
                    sendNotification(list.get(0), getString(R.string.new_strips_published), "");

                // Save new strips
                //refreshLocalDatabase();

                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

                for (SimpleStripDto strip : notificationData.getStrips()) {
                    Runnable myRunnable = () -> {
                        mStripRepository.saveImageStripInCache(strip.getId(), strip.getContent());
                    };

                    mainHandler.post(myRunnable);
                }

            }
            catch (Exception e) {
                Log.e(TAG, "Error during launch of the exception: ", e);
            }
        }
    }


    /**
     * Refresh the local database by sending a new synchronization with the backend.
     */
    /*
    private void refreshLocalDatabase() {
        StripRepositorySingleton stripRepository = StripRepositorySingleton.getInstance(getApplicationContext());

        SyncLocalDatabaseWithRemoteTaskComponent task = DaggerSyncLocalDatabaseWithRemoteTaskComponent.builder()
                .localStorageComponent(stripRepository.getLocalStorageComponent())
                .sharedPreferencesComponent(stripRepository.getSharedPreferencesComponent())
                .stripRepositoryComponent(stripRepository.getStripRepositoryComponent())
                .build();

        SyncLocalDatabaseTask updateTask = task.getDownloadDatabaseTask();

        updateTask.execute();
    }*/

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param simpleStripDto
     * @param title
     * @param description
     */
    private void sendNotification(SimpleStripDto simpleStripDto, String title, String description) {
        Intent intent = new Intent(this, StripActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(StripActivity.ARGUMENT_STRIP_ID, simpleStripDto.getId());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_commitstrip)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
