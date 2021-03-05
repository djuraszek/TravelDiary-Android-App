package com.android.traveldiary.serverrequests;

import android.util.Log;

import com.android.traveldiary.database.Consts;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostLikePostRequest extends StringRequest {

    private static final String URL = Consts.getServerUrl();
    private static final String REQUEST_URL = URL+"likes";
    private Map<String, String> params;
    private String token = "";

    public PostLikePostRequest(String token, int id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REQUEST_URL, listener, errorListener);
        this.token = token;
        params = new HashMap<>();
        params.put("travel_id",""+id);
        Log.i("PostLikePostRequest"," id "+id+" token: "+token);
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
}
