package com.android.traveldiary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.adapters.TravelListAdapter;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.serverrequests.DeleteFollowRequest;
import com.android.traveldiary.serverrequests.GetFollowersRequest;
import com.android.traveldiary.serverrequests.GetUsersTravelsRequest;
import com.android.traveldiary.serverrequests.PostFollowRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentUser extends Fragment {

    private String token;
    private int myID;

    private String username = "username";
    private String name = "";
    private int currentUserID;
    private String initials = "";
    private TextView profilePictureTV, profileNameTV;
    private TextView travelNoTV, followersNoTV, followingNoTV;
    private LinearLayout noContentLayout, followersLayout, followingLayout;
    private ListView travelListView;
    private String TAG = "FragmentUser";
    CheckBox follow;

    private RelativeLayout splashScreen;

    private boolean isCurrentUserFollowing = false;


    public FragmentUser() {
        // Required empty public constructor
    }

    public static FragmentUser newInstance(String param1, String param2) {
        FragmentUser fragment = new FragmentUser();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            username = this.getArguments().getString("username");
            name = this.getArguments().getString("name");
            currentUserID = this.getArguments().getInt("id", -1);
            Log.i("FragmentUser.onCreate", username + " " + name);

            SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
            token = preferences.getString("token", "");
            myID = preferences.getInt("id", -1);

            if (currentUserID < 0) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        setToolbar(view);
        Log.i("FragmentUser.onCreateView", "trololo");

        splashScreen = view.findViewById(R.id.splash_screen);
        splashScreen.setVisibility(View.VISIBLE);

        profileNameTV = (TextView) view.findViewById(R.id.profile_name);
        profileNameTV.setText(name);

        profilePictureTV = (TextView) view.findViewById(R.id.profile_picture);
        profilePictureTV.setText(getNameInitials(profileNameTV.getText().toString()));

        //todo if list is not empty then hide this layout
        noContentLayout = (LinearLayout) view.findViewById(R.id.profile_no_content);


        //get followers and following
        followersNoTV = (TextView) view.findViewById(R.id.profile_followers_number);
        followingNoTV = (TextView) view.findViewById(R.id.profile_following_number);

        // check if follow
        getFollows();


        travelListView = (ListView) view.findViewById(R.id.travel_listView);
        //todo get travels + set adapter for listview
//        travelNoTV = (TextView) view.findViewById(R.currentUserID.profile_travel_number);
        getTravels();


        //must be after getting followers
        follow = (CheckBox) view.findViewById(R.id.follow_button);
        //first we need to get info from server to now if we follow this person
        //setFollowButton();


        //todo go to followers and following
//        followersLayout = view.findViewById(R.id.followers_layout);
//        followingLayout = view.findViewById(R.id.following_layout);


        return view;
    }


    private void setToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(username);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

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
            }
        }

        return initials.toUpperCase();
    }
//
//    private void goToFollowsFragment(String TAG){
//        Bundle bundle = new Bundle();
//        bundle.putString("follow", TAG);
//
//        SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
//        String token = preferences.getString("token", "");
//
//        FragmentFollows fragment = new FragmentFollows(token);
//        fragment.setArguments(bundle);
//
//        ((MainActivity)getActivity()).replaceFragment(fragment);
//    }


    /**
     * -------------------------------------------------------------------------------------
     * get travels
     * -------------------------------------------------------------------------------------
     */

    private void getTravels() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
//                                String resp = jsonResponse.toString();
                        JSONArray dataObject = jsonResponse.getJSONArray("data");
                        Log.v("ServerResp", "" + dataObject.toString());
                        if (dataObject.length() > 0)
                            convertTravels(dataObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetUsersTravelsRequest request = new GetUsersTravelsRequest(token, currentUserID, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private List<Travel> travelList = new ArrayList<>();

    private void convertTravels(JSONArray arr) throws JSONException {
        for (int i = 0; i < arr.length(); i++) { // Walk through the Array.
            JSONObject obj = arr.getJSONObject(i);
            System.out.println("-->travel: " + obj);

            int id = obj.getInt("id");
            String title = obj.getString("title");
            int user_id = obj.getInt("user_id");
            String username = obj.getString("username");
            String photoURL = obj.getString("main_photo");
            int likes_count = obj.getInt("likes_count");
            boolean liked = obj.getBoolean("is_liked");

            Travel t = new Travel(id, title, username, user_id, liked, likes_count);
            t.setPhotoPath(photoURL);
            travelList.add(t);
        }
        System.out.println("List of searched users:\n" + travelList.toString());

        TravelListAdapter listAdapter = new TravelListAdapter(getActivity(), travelList);
        travelListView.setAdapter(listAdapter);

        // todo set likes for travel

        noContentLayout.setVisibility(View.GONE);
    }


    /**
     * -------------------------------------------------------------------------------------
     * followers and follows
     * -------------------------------------------------------------------------------------
     */
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
                        Log.v("ServerResp", "" + dataObject.toString());
                        setFollowers(dataObject);
                        hideSplash();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetFollowersRequest request = new GetFollowersRequest(token, currentUserID, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private int followersNumber = 0;

    public void setFollowers(JSONObject dataObject) {
        try {
            followersNumber = dataObject.getJSONArray("followers").length();
            int following = dataObject.getJSONArray("following").length();
            Log.i(TAG, "followers " + followersNumber + " following " + following);
            followersNoTV.setText("" + followersNumber);
            followingNoTV.setText("" + following);

            if (followersNumber > 0) {
                isCurrentUserFollowing = searchIfFollows(dataObject);
            }
            setFollowButton();
            Log.i(TAG, "isFollowing " + isCurrentUserFollowing);
        } catch (JSONException e) {
            Log.e("ServerRequest", "Error while getting folowers");
            e.printStackTrace();
        }
    }

    public boolean searchIfFollows(JSONObject dataObject) throws JSONException {
        JSONArray followers = dataObject.getJSONArray("followers");
        for (int i = 0; i < followers.length(); i++) {
            if (followers.getJSONObject(i).getInt("id") == myID) {
                return true;
            }
        }
        return false;
    }

    /**
     * -------------------------------------------------------------------------------------
     * follow BUTTON
     * -------------------------------------------------------------------------------------
     */

    private void setFollowButton() {
        Log.e("setFollowButton()", "am i following: " + isCurrentUserFollowing);
        if (isCurrentUserFollowing) setCheckedFollowButton();
        else setUncheckedFollowButton();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if follows then you can unfollow
                if (isCurrentUserFollowing) {
                    //todo show popup window
                    showDeleteMessage();
                } else {
                    //todo onClick follow
                    follow();
                }
            }
        });
    }

    private void showDeleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AlertDialogUnfollowTheme);
        alert.setTitle("Unfollow");
        alert.setMessage("Are you sure you want to unfollow " + username + "?");
        alert.setPositiveButton("Unfollow", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                //todo unfollow
                unfollow();
            }
        });
        alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                follow.setChecked(true);
                return;
            }
        });
        alert.show();
    }


    private void setCheckedFollowButton() {
        followersNoTV.setText(followersNumber + "");
        //change to following
        isCurrentUserFollowing = true;

        follow.setChecked(true);
        follow.setText("FOLLOWING");
        follow.setTextColor(getActivity().getColor(R.color.black));
        follow.setBackgroundResource(R.drawable.button_follow_false);
    }

    private void setUncheckedFollowButton() {
        //todo change number of followers
        followersNoTV.setText(followersNumber + "");

        Log.i("FollowUser", "is not checked");
        follow.setChecked(false);
        follow.setText("FOLLOW");
        follow.setTextColor(getActivity().getColor(R.color.white));
        follow.setBackgroundResource(R.drawable.button_follow_true);
    }


    private void follow() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
//                                String resp = jsonResponse.toString();
//                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        Log.v("ServerRespFollow: Follow user", jsonResponse.toString());

                        //todo change number of followers
                        followersNumber++;
                        isCurrentUserFollowing = true;
                        setCheckedFollowButton();
                    } else {
                        Toast.makeText(getActivity(), "Could not follow, try in a bit", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PostFollowRequest request = new PostFollowRequest(token, currentUserID, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private void unfollow() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Log.v("ServerRespFollow", "" + jsonResponse.toString());

                        followersNumber--;
                        isCurrentUserFollowing = false;
                        setUncheckedFollowButton();
                    } else {
                        Toast.makeText(getActivity(), "Could not follow, try in a bit", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DeleteFollowRequest request = new DeleteFollowRequest(token, currentUserID, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    /**
     * -------------------------------------------------------------------------------------
     * <p>
     * -------------------------------------------------------------------------------------
     */

    private void hideSplash() {
        splashScreen.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().setTitle(initials);
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
