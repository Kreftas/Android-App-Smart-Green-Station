package com.example.sgs_test_2.database;

import java.util.ArrayList;
import java.util.HashMap;

public class Station {
    private String locationAddress;
    private String locationCity;
    private final double longitude;
    private final double latitude;
    private ArrayList<String> fieldName;                                    // The names of the datafields which store the measurment values provided by this Station.
    //  Includes "Temperature" and "Air_Quality", but could include more depending of the number of sensors the Station has.
    private HashMap<String, ArrayList<Object>> fieldNameValuePair;          // A HashMap containing a specific Station's datafields of the measurments taken at this specific Station.
    // This includes "Temperature" and Air_quality, although there could be more depending on the amount of sensors the station has.


    public Station(int id, String name, double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        fieldNameValuePair = new HashMap<>();
    }

    public double getlongitude() {
        return longitude;
    }


    public double getlatitude() {
        return latitude;
    }

    public void addFieldName(String fieldName) {
        this.fieldName.add(fieldName);
    }

    // Adds a datafield name and value.
    public void addFieldNameValuePair(String fieldName, ArrayList<Object> object) {
        fieldNameValuePair.put(fieldName, object);
    }

    // Returns the names of the datafields only.
    public ArrayList<String> getFieldNames() {
        return fieldName;
    }

    // Returns the names and values of the datafields.
    public HashMap<String, ArrayList<Object>> getFieldNameValuePair() {
        return fieldNameValuePair;
    }

}




