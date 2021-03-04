package com.android.traveldiary.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.traveldiary.R;
import com.android.traveldiary.classes.Image;
import com.android.traveldiary.classes.Travel;

import java.util.ArrayList;
import java.util.List;

public class TravelHelper {

    private List<Travel> travelList;
    int[] photoList = {};
    private Context context;

    public TravelHelper(Context context) {
        this.context = context;
        travelList = getCustomTravels();
    }

    public List<Travel> getTravelList() {
        return travelList;
    }

    public Bitmap getCoverPhoto(int travelID){
        return BitmapFactory.decodeResource(context.getResources(), photoList[travelID]);
    }

    public Travel getTravel(int travelID){
        return travelList.get(travelID);
    }

    public List<Travel> getCustomTravels(){
        travelList = new ArrayList<>();

        Travel t0 = new Travel(0,"London Calling","02.06.2016","10.06.2016");
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), photoList[0]);
//        t0.setImageBitmap(bitmap);

        travelList.add(t0);

        Travel t1 = new Travel(1,"Visiting Scotland","11.02.2017","14.02.2017");
        bitmap = BitmapFactory.decodeResource(context.getResources(), photoList[1]);
//        t1.setImageBitmap(bitmap);
        travelList.add(t1);

        Travel t3 = new Travel(2,"Midnight in Paris","08.08.2017","15.08.2017");
        bitmap = BitmapFactory.decodeResource(context.getResources(), photoList[2]);
//        t3.setImageBitmap(bitmap);
        travelList.add(t3);

        Travel t2 = new Travel(3,"Malta For a Week","03.09.2017","10.09.2017");
        bitmap = BitmapFactory.decodeResource(context.getResources(), photoList[3]);
//        t2.setImageBitmap(bitmap);
        travelList.add(t2);

        Travel t4 = new Travel(4,"Oslo","12.12.2017","15.12.2017");
        bitmap = BitmapFactory.decodeResource(context.getResources(), photoList[4]);
//        t4.setImageBitmap(bitmap);
        travelList.add(t4);

        return travelList;
    }


}
