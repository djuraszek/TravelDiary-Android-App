package com.android.traveldiary.serverrequests;

import com.android.traveldiary.classes.Travel;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PutUpdateTravelRequest extends StringRequest {

    private static final String REQUEST_URL = "https://travellist.mitimise.tk/api/travels/";
    private Map<String, String> params;
    String token;

    public PutUpdateTravelRequest(String token, int photoID, Travel travel, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, REQUEST_URL + travel.getTravelID(), listener, errorListener);
        this.token = token;
        params = new HashMap<>();
        params.put("title", travel.getTitle());
        params.put("start_date", travel.getStartDate());
        params.put("end_date", travel.getEndDate());
        if (photoID != -1)
            params.put("photo_id", "" + photoID);

    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        String auth = "Bearer " + token;
        headers.put("Authorization", auth);

        return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
