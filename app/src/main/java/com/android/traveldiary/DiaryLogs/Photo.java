package com.android.traveldiary.DiaryLogs;

import com.android.traveldiary.database.Consts;

import java.time.LocalDate;

public class Photo implements DiaryEntry, Comparable<DiaryEntry> {

    private String ENTRY_TYPE = Consts.ENTRY_TYPE_PHOTO;
    private String title, photoPath;
    private String date;
    private int position = -1;
    private long travelID, photoID;

    public Photo( long photoID, String title, String photoPath, String date, int position, long travelID) {
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

    public long getTravelID() {
        return travelID;
    }

    public long getID() {
        return photoID;
    }


    @Override
    public int compareTo(DiaryEntry diaryEntry) {
        int comparePosition = diaryEntry.getPosition();
        //ascending order
        return this.position - comparePosition;
    }
}
