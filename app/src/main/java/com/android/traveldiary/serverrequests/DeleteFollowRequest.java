package com.android.traveldiary.serverrequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteFollowRequest extends StringRequest {

    private static final String REQUEST_URL = "https://travellist.mitimise.tk/api/follows";
    private Map<String, String> params;
    private String token = "";

    //unfollow person by given id
    public DeleteFollowRequest(String token, int id, Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(Method.DELETE, REQUEST_URL+"/"+id, listener, errorListener);
        this.token = token;
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



