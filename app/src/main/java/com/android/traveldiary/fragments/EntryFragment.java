package com.android.traveldiary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.traveldiary.DiaryLogs.DiaryEntry;
import com.android.traveldiary.R;
import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.adapters.TravelListAdapter;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.dummy.DummyContent.DummyItem;

import java.util.Collections;
import java.util.List;


public class EntryFragment extends Fragment {

//    private static final String ARG_COLUMN_COUNT = "column-count";
//    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<DiaryEntry> entriesList;
    private String date;
    private int day;
    private EntriesListAdapter listAdapter;
    private Context context;
    RecyclerView entriesRV;

    public EntryFragment() {
    }


    public EntryFragment(Context context, List<DiaryEntry> entries, int day, String date){
        this.entriesList = entries;
        this.date = date;
        this.day = day;
        this.context = context;
    }

    public void notifyAdapterDataSetChanged(){
        if(listAdapter!= null) listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        entriesRV = (RecyclerView) view.findViewById(R.id.fragmentListRV);
        ViewCompat.setNestedScrollingEnabled(entriesRV,true);
        Log.e("EntryFragment.onCreateView()","entriesList.size(): "+entriesList.size());
        if(entriesList.size()==0){
            // if there're no entries then show the TextView
            Log.e("EntryFragment.onCreateView()","entriesList.size()==0");
        }
        else{
            Log.e("EntryFragment.onCreateView()","entriesList.size()>0");
//            Collections.sort(entriesList);

//            listAdapter = new EntriesListAdapter(this.getContext(), entriesList);

//            entriesRV.setAdapter(listAdapter);
//            entriesRV.setLayoutManager(new LinearLayoutManager(context));
//            entriesRV.setHasFixedSize(true);


            Log.e("EntryFragment.onCreateView()","listAdapter.getItemCount(): "+listAdapter.getItemCount());
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
