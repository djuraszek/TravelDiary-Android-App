package com.android.traveldiary.serverrequests;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchRequest extends StringRequest {

    private static final String SEARCH_REQUEST_URL = "https://travellist.mitimise.tk/api/users/search?username=";
    private Map<String, String> params;
    private String token = "";

    public SearchRequest(String token, String query, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, SEARCH_REQUEST_URL +query, listener, errorListener);
        this.token = token;
        Log.e("SearchRequest. URL",""+(SEARCH_REQUEST_URL +query));
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");

//        String creds = String.format("%s",token);
//        String auth = "Bearer " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        String auth = "Bearer "+token;
        headers.put("Authorization", auth);

        return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
