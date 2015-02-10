package com.sourcefuse.clickinandroid.view;

import android.app.Dialog;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by mukesh on 17/7/14.
 */

public class MapView extends FragmentActivity implements LocationListener {
    private static final String TAG = ChatRecordView.class.getSimpleName();
    LatLng latLng;
    String coordinates = null;
    private GoogleMap googleMap;
    private EditText searchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.view_mapview);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            coordinates = bundle.getString("coordinates");
            String args[] = coordinates.split(",", 2);
            Double lat = Double.parseDouble(args[0]);
            if (lat != 0.0)
                new Geo().execute(coordinates);


        }

        searchLocation = (EditText) findViewById(R.id.edt_location_search);
        searchLocation.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            googleMap = fm.getMap();

            if (coordinates == null) {
                // Enabling MyLocation Layer of Google Map
                googleMap.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(provider, 20000, 0, this);
            } else {

                double latitude = Double.parseDouble(coordinates.substring(0, coordinates.indexOf(",")));
                double longitude = Double.parseDouble(coordinates.substring(coordinates.indexOf(",") + 1));
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                Marker markerOptions = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(true).visible(true));

            }
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    private class Geo extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {

            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = null;


            try {
                String arg[] = locationName[0].split(",", 2);
                final Double lat = Double.parseDouble(arg[0]);
                final Double lng = Double.parseDouble(arg[1]);

                addresses = geocoder.getFromLocation(lat, lng, 1);
            } catch (IOException e) {
                e.printStackTrace();

            }
            return addresses;
        }

        @Override
        protected void onPostExecute(final List<Address> addresses) {


            for (Address address : addresses) {

                LatLng temp = new LatLng(address.getLatitude(), address.getLongitude());

                String location = null;


                if (!Utils.isEmptyString(address.getAddressLine(0)))
                    location = address.getAddressLine(0);
                if (!Utils.isEmptyString(address.getAddressLine(1)))
                    location = location + " " + address.getAddressLine(1);


                if (location.contains("null"))
                    location = location.replaceAll("null", "");

                EditText editText = (EditText) findViewById(R.id.edt_location_search);
                editText.setText("" + location);
            }


        }

    }


}

