package com.android.traveldiary.serverrequests;

import android.graphics.Bitmap;

import com.android.traveldiary.classes.Travel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class PostCreatePhotoRequest extends StringRequest {

    private static final String REQUEST_URL = "https://travellist.mitimise.tk/api/photos/";
    private Map<String, String> params;
    private String token = "";

    public PostCreatePhotoRequest(String token, int travelID, String title, String date, String photo,
                                  Response.Listener<String> listener, Response.ErrorListener error) {
        super(Method.POST, REQUEST_URL+travelID, listener, error);
        params = new HashMap<>();
        this.token = token;
        params.put("title",title);
        params.put("date",date);
        params.put("photo",photo);
        //todo dodac zdjecie?????
//        params.put("photo",photo);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");

        String auth = "Bearer "+token;
        headers.put("Authorization", auth);

        return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }



//    @Override
//    protected Map<String, DataPart> getByteData() {
//        Map<String, DataPart> params = new HashMap<>();
//        long imagename = System.currentTimeMillis();
//        params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
//        return params;
//    }
}
