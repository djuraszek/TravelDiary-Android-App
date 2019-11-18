package com.android.traveldiary.diaryentries;

import com.android.traveldiary.database.Consts;

public class Note implements DiaryEntry , Comparable<DiaryEntry>{

    String ENTRY_TYPE = Consts.ENTRY_TYPE_NOTE;
    String date;
    String note, title;
    int position = -1;
    long travelID, noteID;

    public Note(long id, String title, String note, String date, int position, long travelID) {
        noteID = id;
        this.date = date;
        this.note = note;
        this.title = title;
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

    public String getNote() {
        return note;
    }

    public String getTitle() {
        return title;
    }

    public long getTravelID() {
        return travelID;
    }

    public long getID() {
        return noteID;
    }


    @Override
    public int compareTo(DiaryEntry diaryEntry) {
        int comparePosition = diaryEntry.getPosition();
        //ascending order
        return this.position - comparePosition;
    }
}

