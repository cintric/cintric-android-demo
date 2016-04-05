package com.cintric.android.cintricdemo.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cintric.android.cintricdemo.R;

import java.util.List;


public class LocationVisitAdapter extends ArrayAdapter<AbstractMapListItem> {

    public LocationVisitAdapter(Context context, List<AbstractMapListItem> visits) {
        super(context, 0, visits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Visit visit = (Visit) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_visit, parent, false);
        }
        // Lookup view for data population
        TextView visitTitle = (TextView) convertView.findViewById(R.id.visitTitle);


        ImageView activityIcon = (ImageView) convertView.findViewById(R.id.placeIcon);


        TextView visitDetail = (TextView) convertView.findViewById(R.id.visitDetail);
        String timespentString;
        try {
            timespentString = CintricUtils.getTimespentString(visit.departureDateTimestamp - visit.arrivalDateTimestamp);
        } catch (IllegalArgumentException e) {
            timespentString = "current";
        }
        String arrivalDateString = CintricUtils.getReadableDate(visit.arrivalDate);
        visitDetail.setText(String.format("%s ago for %s at %s", CintricUtils.getTimeAgo(visit.arrivalDateTimestamp), timespentString, arrivalDateString));

        // Populate the data into the template view using the data object
//        visitTitle.setText(String.format("%s: %f, %f", visit.placeName, visit.lat, visit.lon));
        visitTitle.setText(visit.placeName);

        // Return the completed view to render on screen
        return convertView;
    }
}

