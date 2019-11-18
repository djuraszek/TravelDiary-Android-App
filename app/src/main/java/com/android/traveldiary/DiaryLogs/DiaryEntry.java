package com.android.traveldiary.DiaryLogs;

import java.time.LocalDate;

public interface DiaryEntry extends Comparable<DiaryEntry> {

    String getEntryType();
    String getDate();
    int getPosition();
}
