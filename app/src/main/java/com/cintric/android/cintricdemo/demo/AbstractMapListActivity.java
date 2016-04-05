package com.cintric.android.cintricdemo.demo;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cintric.android.cintricdemo.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class AbstractMapListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ProgressBar progressBar;
    private ListView listView;

    private List<AbstractMapListItem> items = new ArrayList<>();
    private ArrayAdapter itemsAdapter;

    private HashMap<Marker, Integer> mHashMap = new HashMap<>();
    private List<Marker> markerArrayList = new ArrayList<>();

    private boolean mapHasLoaded = false;

    private Circle currentMapCircle = null;

    protected void loadViews() {
        setContentView(R.layout.activity_list_map);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        listView = (ListView) findViewById(R.id.listView);

        itemsAdapter = getInitialItemsAdapter();
        listView.setAdapter(itemsAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (markerArrayList.size() < position + 1) {
                    return;
                }
                Marker marker = markerArrayList.get(position);
                marker.showInfoWindow();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15);
                map.animateCamera(cameraUpdate);
                addMapCircleForItem(items.get(mHashMap.get(marker)));
            }
        });

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }


    @Override
    public void onMapReady(GoogleMap map) {
        loadMap(map);
    }

    protected void setActivityTitle(String title) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(title);
        }
    }

    protected abstract ArrayAdapter<AbstractMapListItem> getInitialItemsAdapter();

    public List<AbstractMapListItem> getItems() {
        return items;
    }


    protected void showProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    protected void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.GONE);
    }


    protected void itemsDidLoad(ArrayList<AbstractMapListItem> loadedItems) {

        items.clear();
        items.addAll(loadedItems);

        itemsAdapter.notifyDataSetChanged();
        if (mapHasLoaded) {
            addMapMarkers();
        }

        hideProgressBar();
    }

    protected void loadMap(GoogleMap googleMap) {

        map = googleMap;
        if (map != null) {

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mapHasLoaded = true;
                    addMapMarkers();
                }
            });

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int index = mHashMap.get(marker);
                    AbstractMapListItem item = items.get(index);
                    onItemInfoWindowClick(item);
                }
            });

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {
                    addMapCircleForItem(items.get(mHashMap.get(marker)));
                    listView.smoothScrollToPosition(mHashMap.get(marker));
                    listView.setItemChecked(mHashMap.get(marker), true);
                    listView.getAdapter().getView(mHashMap.get(marker), null, listView).setSelected(true);
                    return false;
                }
            });

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    removeCircleOverlay();
                    listView.clearChoices();
                    listView.requestLayout();
                }
            });

        } else {
            Log.d("MAP", "Map was null");
        }
    }

    private void removeCircleOverlay() {
        if (currentMapCircle != null) {
            currentMapCircle.remove();
            currentMapCircle = null;
        }
    }

    private void addMapCircleForItem(AbstractMapListItem item) {
        if (currentMapCircle != null) {
            removeCircleOverlay();
        }
        if (item.accuracy > 0) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(item.lat, item.lon))
                    .radius(item.accuracy)
                    .fillColor(0x770000ff)
                    .strokeWidth(0);
            currentMapCircle = map.addCircle(circleOptions);
        }
    }

    abstract public void onItemInfoWindowClick(AbstractMapListItem item);

    private void addMapMarkers() {

        map.clear();
        markerArrayList.clear();
        mHashMap.clear();

        for (int i = 0; i < items.size(); i++) {

            AbstractMapListItem item = items.get(i);
            LatLng latLng = new LatLng(item.lat, item.lon);

            Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(item.title));

            mHashMap.put(marker, i);
            markerArrayList.add(i, marker);
        }

        if (markerArrayList.size() == 0) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerArrayList) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        int padding = 10;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cameraUpdate);
    }
}
