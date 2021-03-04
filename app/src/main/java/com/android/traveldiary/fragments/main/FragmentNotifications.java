package com.android.traveldiary.fragments.main;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.adapters.NotificationsAdapter;
import com.android.traveldiary.classes.Notification;
import com.android.traveldiary.classes.User;
import com.android.traveldiary.fragments.FragmentUser;
import com.android.traveldiary.serverrequests.GetNotificationsRequest;
import com.android.traveldiary.serverrequests.GetUserRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentNotifications extends Fragment {
    private String TAG = "FragmentNotifications";
    private String token;
    private MainActivity mainActivity;
    private ArrayList<Notification> notifications;
    private ListView listView;


    public FragmentNotifications() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setTitle("Notifications");
        SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
        token = preferences.getString("token", "");
        mainActivity = (MainActivity) getActivity();


        getNotifications();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        setToolbar(view);
        listView = view.findViewById(R.id.notifications_listView);

        return view;
    }


    private void getNotifications() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray dataObject = jsonResponse.getJSONArray("data");
                        Log.i(TAG + ": ServerResp", "" + jsonResponse.toString());
                        Log.i(TAG + ": ServerResp", "" + dataObject);
                        if (dataObject.length() > 0)
                            getNotif(dataObject);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error while loading notifications");
                    e.printStackTrace();
                }
            }
        };
        GetNotificationsRequest request = new GetNotificationsRequest(token, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Handle Error
                if (error != null) {
                    Log.d("EditActivity", "Error: " + error + "\nResponse Data "
//                            + error.networkResponse.data.toString()
                            + "\nCause " + error.getCause() + "\nmessage" + error.getMessage());
                    Log.d(TAG, "Failed with error msg:\t" + error.getMessage());
                    Log.d(TAG, "Error StackTrace: \t" + error.getStackTrace().toString());
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        queue.add(request);
    }

    private void getNotif(JSONArray array) throws JSONException {
        notifications = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            int notifID = obj.getInt("id");
            int user_id = obj.getInt("user_id");
            int travel_id = -1;

            if (!obj.get("travel_id").toString().equals("null"))
                travel_id = obj.getInt("travel_id");

            String body = obj.getString("body");

            Log.i(TAG, "notif " + obj.toString());
            Notification notif = new Notification(notifID, user_id, travel_id, body);
            Log.i(TAG, "notif " + notif.toString());
            notifications.add(notif);
        }
        setUpListView();
    }

    private void setUpListView() {

        NotificationsAdapter adapter = new NotificationsAdapter(getContext(), notifications);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Notification n = (Notification) listView.getAdapter().getItem(position);
                getUser(n.getUserID());
            }
        });
    }

    private void getUser(int id) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Log.i(TAG, "" + jsonResponse.toString());
                        //todo go to user profile
                        JSONObject obj = jsonResponse.getJSONObject("data");
//                        "id": 11,
//                        "name": "Larry Stylinson",
//                        "username": "larry_is_real",
                        int id = obj.getInt("id");
                        String name = obj.getString("name");
                        String username = obj.getString("username");

                        User user = new User(id, username, name);
                        goToUserProfle(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetUserRequest request = new GetUserRequest(token, id, listener, null);
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        queue.add(request);
    }

    private void goToUserProfle(User user) {

        Bundle bundle = new Bundle();
        bundle.putString("username", user.getUsername());
        bundle.putString("name", user.getName());
        bundle.putInt("id", user.getPersonID());
        // set Fragmentclass Arguments

        Fragment userFragment = new FragmentUser();
        userFragment.setArguments(bundle);

        mainActivity.replaceFragment(userFragment);
    }


    private void setToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Notifications");
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        getActivity().setTitle("Notifications");
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().setTitle("Notifications");
    }


}
