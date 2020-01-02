package com.android.traveldiary.diaryentries;

public interface DiaryEntry extends Comparable<DiaryEntry> {

    String getEntryType();
    String getDate();
    int getPosition();


}
