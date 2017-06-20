package com.letmeeat.letmeeat.helpers;

/**
 * Created by santhosh on 19/06/2017.
 * Activities should create an instance of this class and call requestLocationUpdate when they have to fetch the new location.
 * Every single time requestLocationUpdate is called, it will trigger the GPS update and wait for its first locationChanged and once it recieves it, it will stop recieving any more updates, unless triggered again.
 */

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;


@SuppressWarnings("MissingPermission")
public class LocationHelper implements LocationListener {

    public interface LocationHelperListener {
        void onLocationIdentified(Location location);
    }

    private LocationManager locationManager;
    private LocationHelperListener listener;
    private final Activity activity;

    private final Handler handler;
    private final Runnable sendResultRunnable;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    public LocationHelper(Activity activity) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.activity = activity;
        this.sendResultRunnable = new Runnable() {
            @Override
            public void run() {
                sendResult();
            }
        };
        this.handler = new Handler();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (listener != null) {
            listener.onLocationIdentified(location);
            locationManager.removeUpdates(this);
            handler.removeCallbacks(sendResultRunnable);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param firstLocation  The new Location that you want to evaluate
     * @param secondLocation The current Location fix, to which you want to compare the new one
     */
    private boolean isFirstLocationisBetter(Location firstLocation, Location secondLocation) {
        if (secondLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = firstLocation.getTime() - secondLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (firstLocation.getAccuracy() - secondLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(firstLocation.getProvider(),
                secondLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    public boolean getLocation(LocationHelperListener listener) {
        this.listener = listener;
        if (locationManager == null)
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        //don't start listeners if no provider is enabled
        if (!isGPSEnabled && !isNetworkEnabled)
            return false;

        try {
            if (isGPSEnabled)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            if (isNetworkEnabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            handler.postDelayed(sendResultRunnable, 1000);
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

    private void sendResult() {
        Location net_loc = null, gps_loc = null;
        locationManager.removeUpdates(this);
        if (isGPSEnabled)
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkEnabled)
            net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //if there are both values use the latest one
        if (gps_loc != null && net_loc != null) {
            if (isFirstLocationisBetter(gps_loc, net_loc))
                listener.onLocationIdentified(gps_loc);
            else
                listener.onLocationIdentified(net_loc);
            return;
        }

        if (gps_loc != null) {
            listener.onLocationIdentified(gps_loc);
            return;
        }
        if (net_loc != null) {
            listener.onLocationIdentified(net_loc);
            return;
        }
        listener.onLocationIdentified(null);
    }
}