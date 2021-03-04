package com.android.traveldiary.serverrequests;

import android.app.Activity;
import android.util.Log;

import com.android.traveldiary.classes.User;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ServerRequest {

    Activity parentActivity;

    public ServerRequest(Activity parentActivity){
        this.parentActivity = parentActivity;
    }

    JSONArray responseArray = null;
    JSONObject responseObj = null;

    public JSONArray searchUsersRequest(String token, String newText){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        String resp = jsonResponse.toString();
                        Log.i("Resp",""+resp);
                        JSONArray dataObject = jsonResponse.getJSONArray("data");
                        saveServerResponse(dataObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch(IllegalArgumentException e){
                    Log.e("FragmentSearch","Illegal argument eception");
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e( "ResponseError",""+error.toString());
                System.out.println("Failure (" + error.networkResponse.statusCode + ")");
                parseVolleyError(error);

            }
        };
        SearchRequest searchRequest = new SearchRequest(token, newText, responseListener,errorListener);
        RequestQueue queue = Volley.newRequestQueue(parentActivity);
        queue.add(searchRequest);

        return responseArray;
    }

    public void saveServerResponse(JSONArray resp){
        responseArray = resp;
    }

    public void saveServerResponse(JSONObject resp){
        responseObj = resp;
    }

    /**
     *   ---------------------------------------------------------------------------------
     *                              GET FOLLOWERS AND FOLLOWING
     *   ---------------------------------------------------------------------------------
     */
    JSONArray followers, following;

    public void getFollowsRequest(String token){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
//                                String resp = jsonResponse.toString();
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        Log.i("ServerResp",""+dataObject.toString());
                        setFollowers(dataObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetFollowersRequest request = new GetFollowersRequest(token,0, responseListener,null);
        RequestQueue queue = Volley.newRequestQueue(parentActivity);
        queue.add(request);
    }

    public ArrayList<ArrayList<User>> getFollowers(){
        ArrayList<ArrayList<User>> follows = new ArrayList<>();
        try {
            follows.add(convertFollowersFromJSON(followers));
            follows.add(convertFollowersFromJSON(following));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return follows;
    }

    public void setFollowers(JSONObject dataObject){
        try {
            followers = dataObject.getJSONArray("followers");
            following = dataObject.getJSONArray("following");
        } catch (JSONException e) {
            Log.e("ServerRequest","Error while gettinf folowers");
            e.printStackTrace();
        }
    }

    public ArrayList<User> convertFollowersFromJSON(JSONArray arr) throws JSONException {
        ArrayList<User> users = new ArrayList<>();
        Log.i("JSONArray",""+arr);
        for (int i = 0; i < arr.length(); i++) { // Walk through the Array.
            JSONObject obj = arr.getJSONObject(i);
            System.out.println("-->user: "+obj);

            int id = obj.getInt("id");
            String name = obj.getString("name");
            String username = obj.getString("username");
            User user = new User(id,username,name);
            users.add(user);
        }
        return users;
    }

    /**
     *   ---------------------------------------------------------------------------------
     *                              GET
     *   ---------------------------------------------------------------------------------
     */















    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            Log.e("VolleyError",""+ message);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
