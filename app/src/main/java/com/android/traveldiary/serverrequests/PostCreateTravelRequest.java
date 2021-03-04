package com.android.traveldiary.serverrequests;

import com.android.traveldiary.classes.Travel;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class PostCreateTravelRequest extends StringRequest {

    private static final String REQUEST_URL = "https://travellist.mitimise.tk/api/travels";
    private Map<String, String> params;
    private String token = "";

    public PostCreateTravelRequest(String token, Travel travel,
                                   Response.Listener<String> listener, Response.ErrorListener error) {
        super(Method.POST, REQUEST_URL, listener, error);
        this.token = token;

        params = new HashMap<>();
        params.put("title",travel.getTitle());
        params.put("start_date",travel.getStartDate());
        params.put("end_date",travel.getEndDate());
        //todo dodac zdjecie?????
//        params.put("photo",photo);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Accept","application/json");
//        headers.put("Content-Type","application/json");


        String auth = "Bearer "+token;
        headers.put("Authorization", auth);

        return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }


}
