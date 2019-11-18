package com.android.traveldiary.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.EditActivity;
import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.adapters.TravelListAdapter;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;

import java.util.List;


public class FragmentTravels extends Fragment {


    ListView profileListView;
    DatabaseHelper helper;
    //    Toolbar toolbar;
    TravelListAdapter adapter;
    private List<Travel> travelList;
    boolean isScrolling = false;

    public static FragmentTravels newInstance(String param1, String param2) {
        FragmentTravels fragment = new FragmentTravels();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_fragment_travels, container, false);

        helper = new DatabaseHelper(getContext());
        setupProfileListView(view);

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void setupProfileListView(View view) {
        travelList = helper.getTravelsList();
        profileListView = (ListView) view.findViewById(R.id.travel_listView);
        adapter = new TravelListAdapter(getContext(), travelList);
        profileListView.setAdapter(adapter);

        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View mainView,
                                    int position, long arg3) {
                TravelListAdapter adapter = (TravelListAdapter) adapterView.getAdapter();

                Travel travel = adapter.getTravelList().get(position);
                System.out.println(travel.toString());

                int travelID = travel.getTravelID();
                String title = travel.getTitle();
                String dateFrom = travel.getStartDate().toString();
                String dateTo = travel.getEndDate().toString();

//                TravelActivity tripFragment = new TravelActivity();
                Intent travelIntent = new Intent(getContext(), TravelActivity.class);
                travelIntent.putExtra("travelID", travelID);

                startActivity(travelIntent);
            }
        });
        registerForContextMenu(profileListView);

        profileListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final ListView lw = getListView();

                System.out.println("scroll state: "+scrollState);
                if (scrollState == 0) {
                    isScrolling = false;
                } else {
                    isScrolling = true;
                }
            }
        });
    }

    public ListView getListView() {
        return profileListView;
    }


    /**
     * MENU
     */



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo); todo
        if (!isScrolling && v.getId() == R.id.travel_listView) {
            MenuInflater inflater = getActivity().getMenuInflater();
//            ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
            menu.setHeaderTitle("Select the action");
            inflater.inflate(R.menu.travel_item_edit_delete_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int listPosition = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        final int travelID = travelList.get(listPosition).getTravelID();
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(getContext(), EditActivity.class);
                intent.putExtra(Consts.STRING_TRAVEL_ID, travelID);
                startActivity(intent);
                return true;

            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        removeTravel(travelID, listPosition);

                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    private void removeTravel(int travelID, int listPosition) {
        DatabaseHelper helper = new DatabaseHelper(getContext());
        int numberBeforeDelete = helper.getTravelsList().size();
        helper.delete(travelID);
        int numberAfterDelete = helper.getTravelsList().size();
        Log.e("Removed travel", "before: " + numberBeforeDelete + " -> after: " + numberAfterDelete);

        travelList.remove(listPosition);
        adapter.notifyDataSetChanged();
    }
}
