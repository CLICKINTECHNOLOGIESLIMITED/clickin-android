package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.GPSTracker;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, GoogleMap.OnCameraChangeListener, TextWatcher

{


    EditText locationText;
    ListView location_list;
    ArrayAdapter<String> adapter;
    LatLng mlatLng;
    Marker markerOptions;
    List<String> searchLocations;
    List<LatLng> searchcor;
    //Boolean check = false;
    /* to check gps*/
    AlertDialog alertDialog = null;
    private GoogleMap mMap;
    private LocationClient locationClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mapview);


        locationText = (EditText) findViewById(R.id.locationText);
        locationText.addTextChangedListener(this);
        // markerImage.set
        location_list = (ListView) findViewById(R.id.location_list);

        if (checkGPSEnabled()) {
            buildAlertMessageNoGps();
        }

        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }


        findViewById(R.id.markerImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ChatRecordView.class);
                intent.putExtra("lat", mlatLng.latitude);
                intent.putExtra("lng", mlatLng.longitude);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edt_location_search = (EditText) findViewById(R.id.locationText);
                String searchtext = edt_location_search.getText().toString();
                if (!Utils.isEmptyString(searchtext)) {
                    findViewById(R.id.location_list).setVisibility(View.VISIBLE);
                    new GeocoderTask().execute(searchtext);
                }
            }
        });

    }

    public void showMarker(Double lat, Double lng) {
        if (markerOptions != null)
            markerOptions.remove();

        markerOptions = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).draggable(true).visible(true));
    }

    private void initilizeMap() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap == null) {
                finish();
            }
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                final LatLng curLoc = mMap.getCameraPosition().target;


                showMarker(curLoc.latitude, curLoc.longitude);
                String latlng1 = curLoc.latitude + ";" + curLoc.longitude;
                new Geo().execute(latlng1);


                mlatLng = curLoc;
                findViewById(R.id.location_list).setVisibility(View.GONE);
                try {
                    EditText myEditText = (EditText) findViewById(R.id.locationText);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        /*resetMyLocationPosition();*/
        locationClient = new LocationClient(this, this, this);
        locationClient.connect();
        locationRequest = LocationRequest.create()
                .setInterval(500)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnCameraChangeListener(this);


        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();

        /* code to add marker on defaut location */

        String latlng = authManager.getLatLan();
        String arg[] = latlng.split(";", 2);
        final Double lat = Double.parseDouble(arg[0]);
        final Double lng = Double.parseDouble(arg[1]);


        if (!Utils.isEmptyString(authManager.getLatLan()) && lat != 0.0) {
            new Geo().execute(latlng);
            showMarker(lat, lng);
            defaultposition(lat, lng);
        } else {
            try {
                Location mLocation = locationClient.getLastLocation();
                Double mLat = mLocation.getLatitude();
                Double mLng = mLocation.getLongitude();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                LatLng tempa = searchcor.get(position);
                mlatLng = tempa;
                showMarker(mlatLng.latitude, mlatLng.longitude);
                locationText.setText("" + searchLocations.get(position));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(tempa));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                searchLocations.clear();
                searchcor.clear();
                adapter.notifyDataSetChanged();
                try {
                    EditText myEditText = (EditText) findViewById(R.id.locationText);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();

        if (locationClient.isConnected())
            locationClient.removeLocationUpdates(this);

        locationClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        LatLng point1 = new LatLng(lat, lng);
        locationClient.removeLocationUpdates(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onCameraChange(CameraPosition position) {


    }

    public void defaultposition(Double lat, Double lng) {
        LatLng latLng = new LatLng(lat, lng);
        mlatLng = latLng;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));

    }

    /* code for text watcher */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private boolean checkGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            return true;
        } else {
            return false;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGPSEnabled()) {
            if (alertDialog != null && !alertDialog.isShowing())
                buildAlertMessageNoGps();
        } else {
            Utils.launchBarDialog(MapActivity.this);
            GPSTracker gpsTracker = new GPSTracker(MapActivity.this);
            Double mLat = gpsTracker.getLatitude();
            Double mLng = gpsTracker.getLongitude();
            LatLng latLng = new LatLng(mLat, mLng);
            mlatLng = latLng;


            showMarker(mLat, mLng);
            Utils.dismissBarDialog();
        }

    }

    private void buildAlertMessageNoGps() {

        // check = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Your GPS seems to be disabled, do you want to enable it?");
        builder.setItems(new CharSequence[]{"Yes", "No"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //check = false;
                                dialog.cancel();
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                break;

                            case 1:
                                //check = false;
                                dialog.cancel();
                                finish();
                                overridePendingTransition(0, R.anim.top_out);

                                break;

                            default:
                                break;
                        }
                    }
                }
        );

        alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.top_out);

    }

    private class Geo extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {

            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                String arg[] = locationName[0].split(";", 2);
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

                EditText editText = (EditText) findViewById(R.id.locationText);
                editText.setText("" + location);
            }


        }

    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(final List<Address> addresses) {


            searchLocations = new ArrayList<String>();
            searchcor = new ArrayList<LatLng>();

            for (Address address : addresses) {

                LatLng temp = new LatLng(address.getLatitude(), address.getLongitude());

                String location = null;


                if (!Utils.isEmptyString(address.getAddressLine(0)))
                    location = address.getAddressLine(0);
                if (!Utils.isEmptyString(address.getAddressLine(1)))
                    location = location + " " + address.getAddressLine(1);

                if (location.contains("null"))
                    location = location.replaceAll("null", "");
                if (!Utils.isEmptyString(location))
                    searchLocations.add(location);
                searchcor.add(temp);

            }
            adapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_list_item_1, searchLocations);
            location_list.setAdapter(adapter);


        }

    }

}