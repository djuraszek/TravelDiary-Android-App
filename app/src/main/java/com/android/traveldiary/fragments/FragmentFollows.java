package com.android.traveldiary.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.adapters.TabAdapter;
import com.android.traveldiary.classes.User;
import com.android.traveldiary.serverrequests.GetFollowersRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLOutput;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class FragmentFollows extends Fragment {

    private ViewPager viewPager;
    private String TAG = "";
    private String username = "";
    private ArrayList<User> followersList = new ArrayList<>();
    private ArrayList<User> followingList = new ArrayList<>();
    private TabLayout tabLayout;

    private String token;
    private int id;
    private MainActivity mainActivity;

    public FragmentFollows() {
        // Required empty public constructor
    }

    public FragmentFollows(String token) {
        this.token = token;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
            token = preferences.getString("token", "");
            id = preferences.getInt("id", -1);
            TAG = this.getArguments().getString("follow");
            username = this.getArguments().getString("username");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following_followers, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.follow_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        TabAdapter adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        Log.e("fragmentFollows","onCreateView()");
//todo get followers and following from server

        try {
            followersList = ((MainActivity)getActivity()).getFollowers();
            followingList = ((MainActivity)getActivity()).getFollowing();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //todo add 2 fragments for follows and followers
//        FragmentFollowList following = new FragmentFollowList(followingList,"FOLLOWING");
//        FragmentFollowList followers = new FragmentFollowList(followersList,"FOLLOWERS");

        adapter.addFragment(new FragmentFollowList(followersList,"FOLLOWERS"), "Followers");
        adapter.addFragment(new FragmentFollowList(followingList,"FOLLOWING"), "Following");

        viewPager.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("Following"));
        tabLayout.addTab(tabLayout.newTab().setText("Followers"));
        Log.e("FragmentFollows","viewPager"+tabLayout.getTabCount());

        int selectedTab=getSelectedTabNo();
        viewPager.setCurrentItem(selectedTab);

        setToolbar(view);
        return view;
    }

    private void setupLists(){

    }



    private int getSelectedTabNo(){
        Log.e("FragmentFollows","selected tab: "+TAG);
        if(TAG.equals("FOLLOWERS")) return 0;
        else return 1;
    }

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

        drawerLayout = view.findViewById(R.id.drawer_layout);


    }

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

}
