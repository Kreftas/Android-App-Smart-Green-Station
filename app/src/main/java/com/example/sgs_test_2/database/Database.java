package com.example.sgs_test_2.database;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import com.example.sgs_test_2.R;
import com.example.sgs_test_2.RealMainActivity;
import com.example.sgs_test_2.station.TextViewer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utility.DatabaseVolley;
import Utility.MockupFactory;

public class Database {
    private final HashMap<Integer, String> stationDescription;
    private final DatabaseVolley volley;
    public HashMap<Integer, Station> stationList;
    private ArrayList<String> urlList;
    private final HashMap<Integer, Marker> markerCollection = new HashMap<>();
    private final MockupFactory mockupFactory = new MockupFactory();
    private int count = 0;
    private final RealMainActivity activity;
    private boolean success = false;

    //Constructor
    public Database(Context context, RealMainActivity activity) {
        this.activity = (RealMainActivity) activity;
        volley = new DatabaseVolley(context);
        stationDescription = new HashMap<>();
        stationList = new HashMap<>();
        urlList = new ArrayList<>();
    }

    // Initializing all stations. All available stations are loaded from database and put as Station objects inside a HashMap.
    // Each Station object contains data about the stations whereabouts, the name, id, and datafields of which data each Station collects respectively.
    public boolean initializeStations() {
        String url = "https://api.thingspeak.com/channels.json?api_key=Z5OLO6JM80OTXCY5";
        volley.fetchJsonArray(url, response -> {
            try {
                JSONArray array = (JSONArray) response;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    int id = object.getInt("id");
                    String name = mockupFactory.getName(id);
                    stationDescription.put(id, name);
                    stationList.put(id, (new Station(id, name, Double.parseDouble(object.getString("longitude")), Double.parseDouble(object.getString("latitude")))));
                    urlList.add("https://api.thingspeak.com/channels/" + id + "/feeds.json?results=10");
                }
                success = true;
                // arrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
        return success;
    }

    // Returns a HashMap with ID and name of station
    public HashMap<Integer, String> getStationDescription() {
        return stationDescription;
    }

    // Returns an array with station ID's only
    public Integer[] getStationIds() {
        if (!stationDescription.isEmpty()) {
            return stationDescription.keySet().toArray(new Integer[0]);
        }
        return null;
    }

    // Adds each station available to Google Maps as markers. Stations location is determined by its coordinates available inside Station class.
    public HashMap<Integer, Marker> addMarkersToMap(GoogleMap mMap, Fragment f) {                           // Added synchronized to prevent asynchronous execution while loading the markers on Map
        if (activity.isNetworkConnected()) {
            if (!success && count < 6) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    count++;
                    addMarkersToMap(mMap, f);
                }, 1000);
                this.initializeStations();
            } else if (success && activity.isNetworkConnected()) {
                count = 0;
                if (f.getView() != null) {
                    f.getView().findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                }
                for (HashMap.Entry<Integer, Station> entry : stationList.entrySet()) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(entry.getValue().getlatitude(), entry.getValue().getlongitude()))
                            .title(mockupFactory.getName(entry.getKey())));
                    marker.setTag((entry.getKey()));
                    markerCollection.put(entry.getKey(), marker);

                }
            }
        }

        return markerCollection;
        // Adds markers to the GUI Map.
        // Also returns a marker collection, to keep track of the markers inserted into Maps, and fetch their respective ID, if deemed necessary by the situation.
    }

    public void populateDataList(int id, ArrayList<String> dataList, ArrayList<String> valueList, TextViewer.DataListAdapter arrayAdapter, ArrayList<String> latlng) {
        String url = "https://api.thingspeak.com/channels/" + id + "/feeds.json?results=1";

        volley.fetchJsonObject(url, response -> {
            try {
                JSONObject responseObject = (JSONObject) response;
                JSONObject channelObject = responseObject.getJSONObject("channel");
                latlng.add(channelObject.getString("latitude"));
                latlng.add(channelObject.getString("longitude"));
                JSONArray feedArray = responseObject.getJSONArray("feeds");
                JSONObject feedObject = feedArray.getJSONObject(0);
                int j = 1;
                while (!feedObject.isNull("field" + j)) {
                    dataList.add(channelObject.getString("field" + j));
                    valueList.add(feedObject.getString("field" + j));
                    j++;
                }
                arrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void populateChart(int id, String dataField, Cartesian cartesian) {
        List<DataEntry> dataEntries = new ArrayList<>();
        String url = "https://api.thingspeak.com/channels/" + id + "/feeds.json?results=40";
        volley.fetchJsonObject(url, response -> {
            try {

                JSONObject responseObject = (JSONObject) response;
                JSONArray feedArray = responseObject.getJSONArray("feeds");
                for (int i = 1; i < feedArray.length(); i = i + 4) {
                    JSONObject feedObject = feedArray.getJSONObject(i);
                    String formatedTime = feedObject.getString("created_at")
                            .substring(11, 16);
                    String date = feedObject.getString("created_at")
                            .substring(0, 10);
                    dataEntries.add(new ValueDataEntry(
                            formatedTime,
                            Double.parseDouble(feedObject.getString("field" + (Integer.parseInt(dataField) + 1)))
                    ));
                    cartesian.xAxis(0).title(date);
                }

                Column column = cartesian.column(dataEntries);
                column.tooltip()
                        .titleFormat("{%X}")
                        .position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0d)
                        .offsetY(5d)
                        .format("{%Value}{groupsSeparator: }");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }


    public boolean databaseInitialized() {
        return success;
    }

    public int getFetchCount() {
        return count;
    }

}
