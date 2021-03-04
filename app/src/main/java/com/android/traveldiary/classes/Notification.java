package com.android.traveldiary.classes;

import androidx.annotation.NonNull;

public class Notification {
    int id, userID, travelID;
    String body;

    public Notification(int id, int userID, int travelID, String body) {
        this.id = id;
        this.userID = userID;
        this.travelID = travelID;
        this.body = body;
    }

    public int getUserID() {
        return userID;
    }

    public int getTravelID() {
        return travelID;
    }

    public String getBody() {
        return body;
    }

    @NonNull
    @Override
    public String toString() {
        return "NOTIFICATION "+id+" user "+userID+" (t: "+travelID+") body= "+body;
    }
}
