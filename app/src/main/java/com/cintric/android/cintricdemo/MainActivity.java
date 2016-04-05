package com.cintric.android.cintricdemo;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.cintric.android.cintric.Cintric;
import com.cintric.android.cintric.CintricActivity;
import com.cintric.android.cintric.CintricEventHandler;
import com.cintric.android.cintricdemo.demo.AbstractMapListActivity;
import com.cintric.android.cintricdemo.demo.AbstractMapListItem;
import com.cintric.android.cintricdemo.demo.LocationFileManager;
import com.cintric.android.cintricdemo.demo.LocationVisitAdapter;
import com.cintric.android.cintricdemo.demo.Visit;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AbstractMapListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadViews();
        setActivityTitle("My Locations");
        loadBasicLocalVisits(0);


        // This verifies your SDK key and starts the Cintric SDK service
        // You must enter your own SDK Key and Secret. Get one at: https://cintric.com/signup
        // If you're targeting Android 6.0 (API level 23) or higher, call this method when you want to request location permission.
        Cintric.startCintricService(this, "YOUR-SDK-KEY", "YOUR-SECRET");

        // If you want to be notified of location updates implement the CintricEventHandler class
        new CintricEventHandler() {

            // This method is called every time the location is updated by Cintric
            @Override
            public void onLocationChange(Location location) {
                Log.d("TAG", String.format("Cintric location changed to: %f, %f", location.getLatitude(), location.getLongitude()));
                LocationFileManager.writeLocation(getApplicationContext(), location.getLatitude(), location.getLongitude(), location.getTime(), location.getAccuracy());
                loadBasicLocalVisits(0);
            }

            // This method is called each time Cintric detects an activity.
            @Override
            public void onActivityDetection(CintricActivity cintricActivity) {
                Log.d("TAG", "Cintric activity detected: " + cintricActivity.toJsonString());
            }
        };
    }

    // If your app targetSdkVersion is 23 or higher, you must implement this method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Let Cintric know the user has responded to the permission dialog
        Cintric.onRequestPermissionsResult(getApplicationContext(), requestCode, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected ArrayAdapter<AbstractMapListItem> getInitialItemsAdapter() {
        return new LocationVisitAdapter(this, getItems());
    }

    @Override
    public void onItemInfoWindowClick(AbstractMapListItem item) {

    }

    private void loadBasicLocalVisits(float minVisitTime) {

        showProgressBar();

        ArrayList<Visit> visits = LocationFileManager.getVisits(getApplicationContext());

        ArrayList<AbstractMapListItem> filteredVisits = new ArrayList<>();

        for (Visit visit : visits) {
            if (visit.departureDateTimestamp - visit.arrivalDateTimestamp >= minVisitTime) {
                filteredVisits.add(visit);
            }
        }
        Collections.sort(filteredVisits, Collections.reverseOrder());

        itemsDidLoad(filteredVisits);
    }
}