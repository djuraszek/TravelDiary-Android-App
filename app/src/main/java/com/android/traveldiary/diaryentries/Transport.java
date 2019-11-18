package com.android.traveldiary.diaryentries;

import com.android.traveldiary.database.Consts;

public class Transport implements DiaryEntry, Comparable<DiaryEntry> {
    String ENTRY_TYPE = Consts.ENTRY_TYPE_TRANSPORT;
    long transportID;
    String departureDate, arrivalDate;
    String transportType, departurePlace, arrivalPlace, departureTime, arrivalTime;
    int position, travelID;

    public Transport(long transportID, String transportType,
                     String departurePlace, String departureDate, String departureTime,
                     String arrivalPlace, String arrivalDate, String arrivalTime,
                     int position, int travelID) {
        this.transportID = transportID;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.transportType = transportType;
        this.departurePlace = departurePlace;
        this.arrivalPlace = arrivalPlace;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.position = position;
        this.travelID = travelID;
    }

    @Override
    public String getEntryType() {
        return ENTRY_TYPE;
    }

    @Override
    public String getDate() {
        return departureDate;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public String toString(){
        return "Travel "+travelID+" : "+transportType+" ("+departurePlace+":"+departureDate+" -> "+
                arrivalPlace+ ":"+arrivalDate;
    }

    public String getENTRY_TYPE() {
        return ENTRY_TYPE;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getTransportType() {
        return transportType;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getTravelID() {
        return travelID;
    }

    public long getID() {
        return transportID;
    }



    @Override
    public int compareTo(DiaryEntry diaryEntry) {
        int comparePosition = diaryEntry.getPosition();
        //ascending order
        return this.position - comparePosition;
    }
}
