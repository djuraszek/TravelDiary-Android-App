package com.android.traveldiary.serverrequests;

import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class PostCreateNoteRequest extends StringRequest {

    private static final String REQUEST_URL = "https://travellist.mitimise.tk/api/notes/";
    private Map<String, String> params;
    String token = "";


    public PostCreateNoteRequest(String token, int travelID, String title, String note, String date,
                                 Response.Listener<String> listener, Response.ErrorListener error) {
        super(Method.POST, REQUEST_URL+travelID, listener, null);
        params = new HashMap<>();
        params.put("title",title);
        params.put("note",note);
        params.put("date",date);
        Log.e("PostCreateNoteRequest","t: "+title+" d: "+date+" id: "+travelID+ " note "+note);
        this.token = token;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("Content-Type","application/x-www-form-urlencoded");


        String auth = "Bearer "+token;
        headers.put("Authorization", auth);

        return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
    }

//    @Override
//    public String getBodyContentType() {
//        return "application/json";
//    }


}
