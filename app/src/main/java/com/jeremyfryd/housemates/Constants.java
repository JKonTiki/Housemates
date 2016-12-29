package com.jeremyfryd.housemates;

/**
 * Created by jeremy on 12/19/16.
 */

public class Constants {
    public static final String FIREBASE_CHILD_HOUSES = "houses";
    public static final String FIREBASE_CHILD_CODES = "codes";
    public static final String FIREBASE_CHILD_ROOMMATES = "roommates";
    public static final String PREFERENCES_LATITUDE_KEY = "latitude";
    public static final String PREFERENCES_LONGITUDE_KEY = "longitude";
    public static final String PREFERENCES_USERNAME_KEY = "username";
    public static final float GEOFENCE_RADIUS_IN_METERS = 50;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final int LOCATION_UPDATE_INTERVAL = 3 * 60 * 1000;
    public static final int LOCATION_FASTEST_INTERVAL = 30 * 1000;
    public static final long GEO_DURATION = 12 * 60 * 60 * 1000;
    public static final String GEOFENCE_REQ_ID = "My Geofence";
    public static final float GEOFENCE_RADIUS = 50.0f;
}
