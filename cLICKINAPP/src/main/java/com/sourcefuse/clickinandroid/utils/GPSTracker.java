package com.sourcefuse.clickinandroid.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;


/**
 * Created by prafull on 9/9/14.
 */

public class GPSTracker extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private final IBinder mBinder = new LocalBinder();
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    AuthManager authManager;
    private Context mContext = null;

    public GPSTracker() {

    }

    public GPSTracker(Context context) {
        this.mContext = context;

        authManager = ModelManager.getInstance().getAuthorizationManager();
        getLocation();
    }

    public Location getLocation() {
        android.util.Log.e("in get location ", "in get location ");
        try {
            android.util.Log.e("in location try", "in location try");
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            this.canGetLocation = true;
            if (isNetworkEnabled) {
                android.util.Log.e("in one--->", "in one--->");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                android.util.Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        String lat = latitude + ";" + longitude;
                        android.util.Log.e("lat---> and lang----> on network enable", "" + lat);

                        authManager.setLatLan(lat);
                    }
                }
            } else if (isGPSEnabled) {
                android.util.Log.e("in two--->", "in two--->");
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    android.util.Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            String lat = latitude + ";" + longitude;
                            authManager.setLatLan(lat);
                            android.util.Log.e("lat---> and lang----> on isGPSEnabled", "" + lat);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(" location exception--->", " location exception--->");
        }

        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public GPSTracker getService() {
            return GPSTracker.this;
        }
    }

}
