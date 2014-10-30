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
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by mukesh on 17/7/14.
 */

    public class MapView extends FragmentActivity implements LocationListener {
    private static final String TAG = ChatRecordView.class.getSimpleName();
    private GoogleMap googleMap;
    private EditText searchLocation;
    MarkerOptions markerOptions;
    LatLng latLng;
    String coordinates = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_mapview);
Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            coordinates = bundle.getString("coordinates");
        }
      //  http://maps.google.com/maps/api/staticmap?center=28.6189112,77.3786174&markers=color%3ared|color%3ared|label%3aA|28.6189112,77.3786174&zoom=15&size=500x180&sensor=true
        //http://maps.google.com/maps/api/staticmap?center=28.6189112,77.3786174&zoom=15&size=500x180&sensor=true
        searchLocation = (EditText) findViewById(R.id.edt_location_search);
        searchLocation.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//            map =  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            // Getting Google Play availability status
            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

            // Showing status
            if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

                int requestCode = 10;
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
                dialog.show();

            } else { // Google Play Services are available

                // Getting reference to the SupportMapFragment of activity_main.xml
                SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                // Getting GoogleMap object from the fragment
                googleMap = fm.getMap();

                if(coordinates==null) {
                    // Enabling MyLocation Layer of Google Map
                    googleMap.setMyLocationEnabled(true);

                    // Getting LocationManager object from System Service LOCATION_SERVICE
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                    // Creating a criteria object to retrieve provider
                    Criteria criteria = new Criteria();

                    // Getting the name of the best provider
                    String provider = locationManager.getBestProvider(criteria, true);

                    // Getting Current Location
                    Location location = locationManager.getLastKnownLocation(provider);

                    if (location != null) {
                        onLocationChanged(location);
                    }
                    locationManager.requestLocationUpdates(provider, 20000, 0, this);
                    }
                else
                {
                    searchLocation.setText(coordinates);
                    double latitude = Double.parseDouble(coordinates.substring(0,coordinates.indexOf(",")));

                    double longitude = Double.parseDouble(coordinates.substring(coordinates.indexOf(",")+1));
                    Log.e("lat/long",""+latitude+"/"+longitude);
                    // Creating a LatLng object for the current location
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Showing the current location in Google Map
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Zoom in the Google Map
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


                }
            }

        searchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                   if(!Utils.isEmptyString(searchLocation.getText().toString())){
                       new GeocoderTask().execute(searchLocation.getText().toString());
                   }
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {


        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Setting latitude and longitude in the TextView tv_location
        Log.e(TAG, "Latitude:" + latitude + ", Longitude:" + longitude);


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


    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            googleMap.clear();

            // Adding Markers on Google Map for each matching address
            for (int i = 0; i < addresses.size(); i++) {

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                googleMap.addMarker(markerOptions);

                // Locate the first location
                if (i == 0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }

    }
}

/*



    Intent intent=new Intent(MainActivity.this,SecondActivity.class);
    startActivityForResult(intent, 2);// Activity is started with requestCode 2


 protected void onActivityResult(int requestCode, int resultCode, Intent data)
       {
                 super.onActivityResult(requestCode, resultCode, data);

                  // check if the request code is same as what is passed  here it is 2
                   if(requestCode==2)
                         {
                            String message=data.getStringExtra("MESSAGE");
                            textView1.setText(message);

                         }

     }

    Intent intent=new Intent();
intent.putExtra("MESSAGE",message);

        setResult(2,intent);

        finish();*/