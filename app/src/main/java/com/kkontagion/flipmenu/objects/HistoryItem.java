package com.kkontagion.flipmenu.objects;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Kon on 11/3/2018.
 */

public class HistoryItem {
    JSONObject jsonData;
    String name, location;
    Calendar cal;
    int id; // for adapter

    public HistoryItem(JSONObject jsonData, String name, String location, Calendar cal) {
        this.jsonData = jsonData;
        this.name = name;
        this.location = location;
        this.cal = cal;
    }

    public HistoryItem(String name, SharedPreferences sp) {
        try {
            this.jsonData = new JSONObject(sp.getString("jsondata", null));
        } catch (Exception e) {
            this.jsonData = new JSONObject();
            Log.e(getClass().getSimpleName(), "Error in jsondata.");
        }

        this.name = name;
        this.location = sp.getString("location", null);
        this.cal = Calendar.getInstance();
        long t = sp.getLong("datetime", 0);
        if (t > 0)
            cal.setTimeInMillis(t);
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return cal.getTime().toLocaleString();
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.id == ((Item) obj).getId()) || super.equals(obj);
    }
}
