package com.android.traveldiary.diaryentries;

import android.util.Log;

import com.android.traveldiary.database.Consts;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Photo implements DiaryEntry, Comparable<DiaryEntry> {

    private String ENTRY_TYPE = Consts.ENTRY_TYPE_PHOTO;
    private String title, photoPath;
    private String date;
    private int position = -1;
    private int travelID, photoID;

    public Photo(int photoID, String title, String photoPath, String date, int position, int travelID) {
        this.title = title;
        this.photoPath = photoPath;
        this.date = date;
        this.position = position;
        this.travelID = travelID;
        this.photoID = photoID;
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

    public String getTitle() {
        return title;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public int getTravelID() {
        return travelID;
    }

    public int getID() {
        return photoID;
    }




    @Override
    public int compareTo(DiaryEntry diaryEntry) {
//        "created_at":"2020-05-28T11:30:20.000000Z"
        String DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
        String dateS1=(this.date.replaceFirst("T"," ")).substring(0,19);
        String dateS2=diaryEntry.getDate().replaceFirst("T"," ").substring(0,19);

        Log.e("Photo.compareTo","d1: "+dateS1+ "d2: "+dateS2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        LocalDate date1 = LocalDate.parse(dateS1, formatter);
        LocalDate date2 = LocalDate.parse(dateS2, formatter);

        if (date1.isAfter(date2)) return 1;
        else return -1;
    }
}
