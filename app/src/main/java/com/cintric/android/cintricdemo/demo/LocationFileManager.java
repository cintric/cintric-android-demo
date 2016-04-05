package com.cintric.android.cintricdemo.demo;

import android.content.Context;

import com.cintric.android.cintric.CintricActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;


public class LocationFileManager {

    private static final String LOCATION_FILENAME = "locations.txt";
    private static final String ACTIVITY_FILENAME = "activities.txt";

    public static void writeActivity(Context context, CintricActivity activity) {

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(ACTIVITY_FILENAME, Context.MODE_APPEND);
            String lineToWrite = activity.toJsonString() + "\n";
            outputStream.write(lineToWrite.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<CintricActivity> getActivities(Context context) {

        ArrayList<CintricActivity> activities = new ArrayList<>();
        try {
            FileInputStream fileIn = context.openFileInput(ACTIVITY_FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileIn);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                activities.add(new CintricActivity(line));
            }
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activities;
    }


    public static void writeLocation(Context context, double lat, double lon, long timestamp, float accuracy) {
        FileOutputStream outputStream;

        JSONObject locJson = new JSONObject();

        try {
            locJson.put("lat", lat);
            locJson.put("lon", lon);
            locJson.put("timestamp", timestamp);
            locJson.put("accuracy", accuracy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String locString = locJson.toString() + "\n";

        try {
            outputStream = context.openFileOutput(LOCATION_FILENAME, Context.MODE_APPEND);
            outputStream.write(locString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Visit> getVisits(Context context) {
        ArrayList<Visit> visits = new ArrayList<>();
        try {
            FileInputStream fileIn = context.openFileInput(LOCATION_FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileIn);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            Visit prevVisit = null;
            Visit curVisit = null;
            while ((line = bufferedReader.readLine()) != null) {
                curVisit = Visit.fromBasicJsonString(line);
                visits.add(curVisit);
                if (prevVisit != null) {
                    prevVisit.departureDateTimestamp = curVisit.arrivalDateTimestamp;
                    prevVisit.departureDate = curVisit.arrivalDate;
                }
                prevVisit = curVisit;
            }
            curVisit.departureDateTimestamp = curVisit.arrivalDateTimestamp;
            curVisit.departureDate = new Date(curVisit.departureDateTimestamp);
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return visits;
    }
}
