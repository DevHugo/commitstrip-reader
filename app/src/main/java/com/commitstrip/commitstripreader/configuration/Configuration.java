package com.commitstrip.commitstripreader.configuration;

public class Configuration {

    public static final int SWIPE_DISTANCE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;

    // Download image service job's id, the id have no exact meaning just don't put two service with the same id.
    public static final String JOB_ID_DOWNLOAD_IMAGE_SERVICE = "DOWNLOAD_IMAGE_SERVICE";

    // Backend's url
    public static String URL_BACKEND = "http://ns531153.ip-198-100-145.net:8080/";

    public static boolean OFFLINE_MODE = false;

    // Database version, increment this number, if you change the structure of the database. Don't forget to provide sql script migration.
    public static int DATABASE_VERSION = 1;

    // Notification topic name. If you want to change this, you have to change it, in the backend too.
    public static String TOPIC_NAME = "commitstrip";

    // Folder name in the storage area.
    public static String FOLDER_NAME_IMAGE = "commitstrip";

    // Is id is correct
    public static boolean isIdCorrect (Long id) {
        return id != null && id != -1 && id != 0;
    }

}
