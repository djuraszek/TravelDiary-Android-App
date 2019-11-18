package com.android.traveldiary.DiaryLogs;

import com.android.traveldiary.database.Consts;

public class MapMarker implements DiaryEntry , Comparable<DiaryEntry>{

    private long mapCoordID;
    private String ENTRY_TYPE = Consts.ENTRY_TYPE_MAP_MARKER;
    private String title, description;
    private double longitude, latitude;

    private String date;
    private int position;
    private long travelID;

    public MapMarker(long mapCoordID, String title, String description,double longitude, double latitude, String date, int position, long travelID) {
        this.mapCoordID = mapCoordID;
        this.title = title;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
        this.position = position;
        this.travelID = travelID;
    }

    @Override
    public String getEntryType() {
        return ENTRY_TYPE;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public String getENTRY_TYPE() {
        return ENTRY_TYPE;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    public long getID() {
        return mapCoordID;
    }

    public long getTravelID() {
        return travelID;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public int compareTo(DiaryEntry diaryEntry) {
        int comparePosition = diaryEntry.getPosition();
        //ascending order
        return this.position - comparePosition;
    }
}
