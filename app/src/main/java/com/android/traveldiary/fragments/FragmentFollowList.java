package com.android.traveldiary.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.adapters.SearchResultsAdapter;
import com.android.traveldiary.classes.User;
import com.android.traveldiary.fragments.FragmentUser;

import java.util.ArrayList;

public class FragmentFollowList extends Fragment {

    private ArrayList<User> userList;
    private ListView followLV;
    private TextView noFollowers, noFollowing;
    private String TAG="";

    public FragmentFollowList() {
        // Required empty public constructor
    }

    public FragmentFollowList(ArrayList<User> userList,String TAG) {
        this.userList = userList;
        this.TAG = TAG;
        Log.i("FragmentFollowList",TAG+" "+userList.size());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            this.TAG = getArguments().getString("follow");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow_list, container, false);
        noFollowers = (TextView) view.findViewById(R.id.no_followers);
        noFollowing = (TextView) view.findViewById(R.id.no_following);
        noFollowers.setVisibility(View.GONE);
        noFollowing.setVisibility(View.GONE);

        followLV = view.findViewById(R.id.follow_list_view);

        Log.e("FragmentFollowList", "onCreateView: list.size()"+userList.size());
        if (userList.size() > 0){
            followLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.e("FragmentFollowList", "OnListItmeClick");

                    User user = (User) followLV.getAdapter().getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", user.getUsername());
                    bundle.putString("name", user.getName());
                    bundle.putInt("id",user.getPersonID());
                    // set Fragmentclass Arguments

                    Fragment userFragment = new FragmentUser();
                    userFragment.setArguments(bundle);

                    ((MainActivity) getActivity()).replaceFragment(userFragment);

                }
            });

            followLV.setAdapter(new SearchResultsAdapter(getActivity(), userList));

        }
        else
            showNoFollows();

        return view;
    }

    private void showNoFollows() {
        if(TAG.equals("FOLLOWERS"))
            noFollowers.setVisibility(View.VISIBLE);
        else
            noFollowing.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
