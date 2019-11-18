package com.android.traveldiary.classes;

import android.graphics.Bitmap;

public class Image {

    private int imageId;
    private Bitmap bitmap;
    private int travelID;

    public Image(int imageId, Bitmap bitmap, int travelID) {
        this.imageId = imageId;
        this.bitmap = bitmap;
        this.travelID = travelID;
    }

    public int getImageId() {
        return imageId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getTravelID() {
        return travelID;
    }
}
