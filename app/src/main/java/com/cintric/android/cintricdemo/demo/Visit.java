package com.cintric.android.cintricdemo.demo;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Visit extends AbstractMapListItem {

    public String visitId;
    public String placeName;


    public long arrivalDateTimestamp;
    public long departureDateTimestamp;

    public Date arrivalDate;
    public Date departureDate;

    public Visit() {

    }

    public static Visit fromBasicJsonString(String basicJsonStr) {

        Visit visit = new Visit();
        try {
            JSONObject locJson = new JSONObject(basicJsonStr);

            visit.lat = locJson.getDouble("lat");
            visit.lon = locJson.getDouble("lon");
            visit.accuracy = locJson.getInt("accuracy");

            visit.arrivalDateTimestamp = locJson.getLong("timestamp");
            visit.arrivalDate = new Date(visit.arrivalDateTimestamp);

            visit.visitId = String.format("%f,%f,%d", visit.lat, visit.lon, visit.arrivalDateTimestamp);
            visit.placeName = String.format("%f, %f", visit.lat, visit.lon);

            visit.title = visit.placeName;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return visit;
    }

    @Override
    public int compareTo(@NonNull Object object) {
        Visit otherVisit = (Visit) object;
        return this.arrivalDate.compareTo(otherVisit.arrivalDate);
    }
}
