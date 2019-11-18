package com.android.traveldiary.classes;

import android.graphics.Bitmap;

public class Travel {

    private int travelID;
    private String title, photoURI="";
    private String startDate, endDate;
//    private Bitmap photoBitmap;

    public Travel(int travelID, String title, String startDate, String endDate) {
        this.travelID = travelID;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getTravelID() {
        return travelID;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getImagePath() {
        return photoURI;
    }

//    public Bitmap getImageBitmap() {
//        return photoBitmap;
//    }

    public void setImageURI(String image) {
        this.photoURI = image;
    }
//    public void setImageBitmap(Bitmap image) {
//        this.photoBitmap = image;
//    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhotoPath(String photoURI) {
        this.photoURI = photoURI;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String toString(){
        return travelID + " " +title+ " "+startDate + " - " + endDate;
    }
}
