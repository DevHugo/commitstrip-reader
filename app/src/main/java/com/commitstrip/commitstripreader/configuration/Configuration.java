package com.commitstrip.commitstripreader.configuration;

public class Configuration {

    // Backend's url
    public static final String URL_BACKEND = "http://188.166.34.36/";

    // Download service job's id, the id have no exact meaning just don't put two service with the same id.
    public static final String JOB_ID_SYNC_LOCAL_DATABASE = "SYNC_LOCAL_DATABASE";

    // Database version, increment this number, if you change the structure of the database. Don't forget to provide sql script migration.
    public static int DATABASE_VERSION = 1;

    // Keys for the shared preference.
    public static String SHAREDPREFERENCES_KEY_DATABASE_SYNC_OK = "DATABASE_SYNC_OK";
    public static String SHAREDPREFERENCES_KEY_LAST_STRIP_READ = "LAST_STRIP_READ";

    // Notification topic name. If you want to change this, you have to change it, in the backend too.
    public static String TOPIC_NAME = "commitstrip";

    // Folder name in the storage area.
    public static String FOLDER_NAME_IMAGE = "commistrip";

    public static String FILENAME_CACHE_SYNC_LOCAL_DATABASE = "lastSync.txt";
    public static String SHAREDPREFERENCES_KEY_SHOULD_USE_VOLUME_KEY = "use_volume_key";
}
