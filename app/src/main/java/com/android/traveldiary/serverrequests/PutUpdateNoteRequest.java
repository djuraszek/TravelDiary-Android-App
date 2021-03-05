package com.android.traveldiary.serverrequests;

import android.util.Log;

import com.android.traveldiary.database.Consts;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PutUpdateNoteRequest extends StringRequest {

    private static final String URL = Consts.getServerUrl();
    private static final String REQUEST_URL = URL+"notes/";
    private Map<String, String> params;
    String token = "";


    public PutUpdateNoteRequest(String token, int noteID, String title, String note, String date,
                                Response.Listener<String> listener, Response.ErrorListener error) {
        super(Method.PUT, REQUEST_URL+noteID, listener, null);
        params = new HashMap<>();
        params.put("title",title);
        params.put("note",note);
        Log.e("PutUpdateNoteRequest","t: "+title+" d: "+date+" id: "+noteID+ " note "+note);
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
