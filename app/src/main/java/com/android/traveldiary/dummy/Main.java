package com.android.traveldiary.dummy;

import java.util.Calendar;

public class Main {

    public static void main(String[]args){
        String startDate = "11.02.2019";
        Calendar c = Calendar.getInstance();
        int year = Integer.parseInt(startDate.substring(6));
        int month = Integer.parseInt(startDate.substring(3,5));
        int day = Integer.parseInt(startDate.substring(0,2));
        c.set(year, month, day);//Year,Month -1,Day
        System.out.println(day+"."+month+"."+year);

    }
}
