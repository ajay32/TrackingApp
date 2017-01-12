package com.hackingbuzz.hikinggpslocator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    Geocoder geocoder;
    List<Address> listAddress;

    TextView latitude, longititude, altitiude , accuracy , addressText;
    Location location;  // object for storing last known location..



    public void updateLocationInfo(Location location) {                                              //we shoud always take a seperate method for processing..n we taking it coz..hum more than one jagah se location update kar rahe hai..listener se and lastKnownLocation se..so ek method main sara kaam two time nai likna padega
     //   Log.i("placeinfo",location.toString());
        latitude.setText("Latitude: "+ location.getLatitude());
        longititude.setText("Longitude: "+ location.getLongitude());
        accuracy.setText("Accuracy: "+ location.getAccuracy());
        altitiude.setText("Altitude: "+ location.getAltitude() );

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());    // we creating it here coz we need address and we can only get by knowing lattitle and longitiud of a place .that is just known to location object..so we gotta create here geocoder
        try {
             listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);    // we initilzing it ,,my be we dont get lattitiude or longitieu ..so it shoudnt through null pointer exception
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address="Couldn't find the Address";

        if(listAddress != null && listAddress.size() >0) { // means it has atleast one element  // nested if..if this condition not fullfil..interal if statement will not work

            address = " ";
            if (listAddress.get(0).getSubThoroughfare() != null) {
                address += listAddress.get(0).getSubThoroughfare() + "\n";   //subThorughfare means subStreet
            }
            if (listAddress.get(0).getThoroughfare() != null) {
                address += listAddress.get(0).getThoroughfare() + "\n";
            }
            if (listAddress.get(0).getLocality() != null) {     // locality means city name
                address += listAddress.get(0).getLocality() + "\n";
            }
            if (listAddress.get(0).getPostalCode() != null) {     // locality means city name
                address += listAddress.get(0).getPostalCode() + "\n";
            }
            if (listAddress.get(0).getCountryName() != null) {     // locality means city name
                address += listAddress.get(0).getCountryName() + "\n";
            }
        }


        addressText.setText("Address: " + address);



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = (TextView) findViewById(R.id.latitude);
        longititude = (TextView) findViewById(R.id.longitude);
        accuracy = (TextView) findViewById(R.id.accuracy);
        altitiude = (TextView) findViewById(R.id.altitude);
        addressText = (TextView) findViewById(R.id.address);






        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {  // main thing here is when uesr locatio changes it will come in location object u can see it as parameter..n we need to use this
                updateLocationInfo(location);    // using location object but sending it in other method..
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT <23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0 ,locationListener);
        } else {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION} , 1);

            }  else {
                // otherewise permission granted...if again opening the app after taking permission first time opened the app..
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0 ,locationListener);

                // here is the deal.. when a person open an app..it shoud show some location..but our location lister will not show any loation unitl it gets a change..so have to show some
                //location n we can do using lastKnownLocation...like u open google maps..it doesnt show ur current location until u click current location button
                try {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    if(location != null) {
                        updateLocationInfo(location);
                    } else {
                        Toast.makeText(this,"GPS not getting your last Location", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }
}
