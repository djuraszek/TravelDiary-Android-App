package com.android.traveldiary.diaryentries;

import com.android.traveldiary.database.Consts;

public class VoiceNote implements DiaryEntry, Comparable<DiaryEntry>{
    private String ENTRY_TYPE = Consts.ENTRY_TYPE_VOICE_NOTE, title ="", recordURI="";
    private String date;
    private int position;
    private long travelID, noteID;

    public VoiceNote(long noteID, String title, String recordURI, String date, int position, long travelID) {
        this.title = title;
        this.recordURI = recordURI;
        this.date = date;
        this.position = position;
        this.travelID = travelID;
        this.noteID = noteID;
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

    public String getRecordURI() {
        return recordURI;
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
