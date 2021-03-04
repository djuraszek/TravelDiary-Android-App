package com.android.traveldiary.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.adapters.TravelListAdapter;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.classes.User;
import com.android.traveldiary.fragments.FragmentFollows;
import com.android.traveldiary.serverrequests.GetFollowersRequest;
import com.android.traveldiary.serverrequests.GetMyTravelsRequest;
import com.android.traveldiary.serverrequests.ServerRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class FragmentProfile extends Fragment {
    private String TAG = "FragmentProfile";

    private String username = "username";
    private String name = "name";
    private String token = "";
    private int id;
    private String initials = "";
    private TextView profilePictureTV, profileNameTV;

    private TextView travelNoTV, followersNoTV, followingNoTV;
    private LinearLayout noContentLayout, followersLayout, followingLayout;
    private ListView travelListView;

    private MainActivity mainActivity;
    ServerRequest serverRequest;

    public boolean isVisibleToUser = false;
    ArrayList<Travel> travelList;


    public FragmentProfile() {
        // Required empty public constructor
    }

    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setTitle(username);

        mainActivity = (MainActivity) getActivity();
        serverRequest = new ServerRequest(mainActivity);

        SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
        token = preferences.getString("token", "");
        username = preferences.getString("username", "");
        name = preferences.getString("name", "");
        id = preferences.getInt("id", -1);

        if (id == -1 || token.equals("")) {
            Log.e("FragmentProfile", "id: " + id + "\ntoken:(" + token + ")");
        }
        Log.e("FragmentProfile", "id: " + id + " " + username + " (" + name + ")");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setToolbar(view);


//        profilePictureTV = (TextView) view.findViewById(R.id.profile_picture);
        profileNameTV = (TextView) view.findViewById(R.id.profile_name);
        profileNameTV.setText(name);

        profilePictureTV = (TextView) view.findViewById(R.id.profile_picture);
        profilePictureTV.setText(getNameInitials(profileNameTV.getText().toString()));

        // done
        followersNoTV = (TextView) view.findViewById(R.id.profile_followers_number);
        followingNoTV = (TextView) view.findViewById(R.id.profile_following_number);
//        getFollows();

        //
        travelListView = (ListView) view.findViewById(R.id.travel_listView);

        //if list is not empty then hide this layout
        noContentLayout = (LinearLayout) view.findViewById(R.id.profile_no_content);


        followersLayout = view.findViewById(R.id.followers_layout);
        followingLayout = view.findViewById(R.id.following_layout);

        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = "FOLLOWERS";
                goToFollowsFragment(TAG);
            }
        });
        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = "FOLLOWING";
                goToFollowsFragment(TAG);
            }
        });

        if (travelListView.getAdapter() == null)
            getMyTravels();

        if(followers==-1 || following==-1)
            getFollows();
        setFollowers();

        final SwipeRefreshLayout pullToRefersh = (SwipeRefreshLayout)view.findViewById(R.id.pullToRefresh);
        pullToRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyTravels();
                pullToRefersh.setRefreshing(false);
            }
        });

        return view;
    }



    private void setToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(username);
        ImageView toolbarMenuIcon = (ImageView) toolbar.findViewById(R.id.toolbar_menu);
//        mainActivity.setupDrawerLayout();

        toolbarMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.logOut();
            }
        });

    }

    /**
     * ---------------------------------------------------------------------------------
     * GET MY TRAVELS
     * ---------------------------------------------------------------------------------
     */

    private void getMyTravels() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
//                                String resp = jsonResponse.toString();
                        JSONArray dataObject = jsonResponse.getJSONArray("data");
                        Log.e("ServerResp.getMyTravels()", "" + dataObject.toString());
                        if (dataObject.length() > 0) getTravels(dataObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetMyTravelsRequest request = new GetMyTravelsRequest(token, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        queue.add(request);
    }

    private void getTravels(JSONArray arr) throws JSONException {
        travelList = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) { // Walk through the Array.
            JSONObject obj = arr.getJSONObject(i);
            System.out.println("-->user: " + obj);

            String username = obj.getString("username");
            int userID = obj.getInt("user_id");

            String title = obj.getString("title");
            String dateFrom = obj.getString("start_date");
            String dateTo = obj.getString("end_date");
            int travelID = obj.getInt("id");
            String mainPhotoURL = obj.getString("main_photo");
            int likesCount = obj.getInt("likes_count");
            boolean isLiked = obj.getBoolean("is_liked");

            Travel t = new Travel(travelID, title, username, userID, isLiked, likesCount);
            t.setStartDate(dateFrom);
            t.setPhotoPath(mainPhotoURL);
            t.setEndDate(dateTo);
            t.setMyTravel(true);
            travelList.add(t);
        }
        System.out.println("List of searched users:\n" + travelList.toString());

        setTravelListView(travelList);
    }

    private void setTravelListView(ArrayList<Travel> travelList) {
        noContentLayout.setVisibility(GONE);

        TravelListAdapter adapter = new TravelListAdapter(getContext(), travelList);
        travelListView.setAdapter(adapter);

        travelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View mainView, int position, long arg3) {
                Log.i("FragmentTravels", "item click");
                TravelListAdapter adapter = (TravelListAdapter) adapterView.getAdapter();
                Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
                Travel travel = adapter.getTravelList().get(position);
                System.out.println(travel.toString());

                int travelID = travel.getTravelID();
                String title = travel.getTitle();
                String dateFrom = travel.getStartDate().toString();
                String dateTo = travel.getEndDate().toString();

//                TravelActivity tripFragment = new TravelActivity();
                Intent travelIntent = new Intent(getContext(), TravelActivity.class);
                travelIntent.putExtra("travelID", travelID);
                travelIntent.putExtra("token", token);
                travelIntent.putExtra("isMyTravel", true);

                startActivity(travelIntent);
            }
        });
    }

    /**
     * ---------------------------------------------------------------------------------
     * GET FOLLOWERS AND FOLLOWING
     * ---------------------------------------------------------------------------------
     */
    int followers = -1;
    int following = -1;

    private void getFollows() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
//                                String resp = jsonResponse.toString();
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        Log.i("ServerResp", "" + dataObject.toString());
                        setFollowers(dataObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetFollowersRequest request = new GetFollowersRequest(token, id, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        queue.add(request);
    }

    public void setFollowers(JSONObject dataObject) throws JSONException {
        followers = dataObject.getJSONArray("followers").length();
        following = dataObject.getJSONArray("following").length();
        setFollowers();
    }


    private void setFollowers(){
        followersNoTV.setText("" + followers);
        followingNoTV.setText("" + following);
    }

    private void goToFollowsFragment(String TAG) {
        Bundle bundle = new Bundle();
        bundle.putString("follow", TAG);
        bundle.putString("username", username);

        SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        FragmentFollows fragment = new FragmentFollows(token);
        fragment.setArguments(bundle);

        ((MainActivity) getActivity()).replaceFragment(fragment);
    }


    private String getNameInitials(String name) {
        initials = "";
        name = " " + name;

        for (int i = 0; i < name.length(); i++) {
            // sorry about the 3x&&, dont remember the use of trim, but you
            // can check " your name complete" if " y"==true y is what you want
            if (("" + name.charAt(i)).equals(" ") && i + 1 < name.length() && !("" + name.charAt(i + 1)).equals(" ")) {
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                initials += name.charAt(i + 1);
                profileNameTV.setText(name.substring(i+1));
            }
        }
        return initials.toUpperCase();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
