package com.kkontagion.flipmenu.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.kkontagion.flipmenu.R;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Kon on 11/3/2018.
 */

public class HistoryItem implements Parcelable {
    JSONObject jsonData;
    String name, location, fullText;
    Calendar cal;
    int id; // for adapter

    public String getSPName() {
        return Uri.parse(name).getLastPathSegment().split("\\.")[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeLong(cal.getTimeInMillis());
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeString(fullText);
        parcel.writeString(jsonData.toString());
    }

    public static final Parcelable.Creator<HistoryItem> CREATOR
            = new Parcelable.Creator<HistoryItem>() {
        public HistoryItem createFromParcel(Parcel in) {
            return new HistoryItem(in);
        }

        public HistoryItem[] newArray(int size) {
            return new HistoryItem[size];
        }
    };

    private HistoryItem(Parcel in) {
        this.id = in.readInt();
        this.cal = Calendar.getInstance();
        cal.setTimeInMillis(in.readLong());
        this.name = in.readString();
        this.location = in.readString();
        this.fullText = in.readString();
        try {
            this.jsonData = new JSONObject(in.readString());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error getting JSON data.");
            this.jsonData = new JSONObject();
        }
    }

    public HistoryItem(JSONObject jsonData, String name, String location, Calendar cal) {
        this.jsonData = jsonData;
        this.name = name;
        this.location = location;
        this.cal = cal;
    }

    public HistoryItem(String name, SharedPreferences sp, Context ctx) {
        try {
            this.jsonData = new JSONObject(sp.getString("jsondata", null));
            this.fullText = ((JSONObject) ((JSONObject) jsonData.getJSONArray("responses").get(0))
                    .getJSONArray("textAnnotations").get(0)
            ).getString("description");
        } catch (Exception e) {
            this.jsonData = new JSONObject();
            this.fullText = "Nothing found.";
            Log.e(getClass().getSimpleName(), "Error in jsondata.");
        }

        this.name = name;
        this.location = sp.getString("location", ctx.getString(R.string.history_location));
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

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
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
