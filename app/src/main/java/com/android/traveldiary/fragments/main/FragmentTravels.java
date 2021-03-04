package com.android.traveldiary.fragments.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.EditActivity;
import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.adapters.TravelListAdapter;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.android.traveldiary.serverrequests.GetHomeScreenTravelsRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class FragmentTravels extends Fragment {

    private Toolbar toolbar;

    ListView travelListView;
    DatabaseHelper helper;
    TravelListAdapter adapter;
    private List<Travel> travelList;
    boolean isScrolling = false;
    private String token="";

    private View view;

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

    public void notifyDataSetChanged(){
        travelList = helper.getTravelsList();
        adapter.updateTravelList(travelList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_travels, container, false);

        toolbar = (Toolbar)view.findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

        SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
        token = preferences.getString("token", "");

        //todo get travelList from server
        getTravelList();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(adapter!=null)adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupTravelListView() {
        travelListView = (ListView) view.findViewById(R.id.travel_listView);
        adapter = new TravelListAdapter(getContext(), travelList);
        travelListView.setAdapter(adapter);

        travelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View mainView, int position, long arg3) {
                Log.i("FragmentTravels","item click");
                TravelListAdapter adapter = (TravelListAdapter) adapterView.getAdapter();
//                Toast.makeText(getActivity(),"Click",Toast.LENGTH_SHORT).show();
                Travel travel = adapter.getTravelList().get(position);
                System.out.println("You Clicked "+travel.toString());

                int travelID = travel.getTravelID();
                String title = travel.getTitle();
                String dateFrom = travel.getStartDate().toString();
                String dateTo = travel.getEndDate().toString();

//                TravelActivity tripFragment = new TravelActivity();
                Intent travelIntent = new Intent(getContext(), TravelActivity.class);
                travelIntent.putExtra("travelID", travelID);
                travelIntent.putExtra("token", token);

                startActivity(travelIntent);
            }
        });

    }

    public ListView getListView() {
        return travelListView;
    }


    public boolean listIsAtTop()   {
        if(travelListView.getChildCount() == 0) return true;
        return travelListView.getChildAt(0).getTop() == 0;
    }


    /** ------------------------------------------------------------------------------------------
     *                      GET LIST OF TRAVELS FOR MAIN SCREEN
     *  ------------------------------------------------------------------------------------------
     */

    private void getTravelList(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        String resp = jsonResponse.toString();
                        Log.e("ServerResp.getHomeTravels()", "" + resp.toString());
                        JSONArray dataObject = jsonResponse.getJSONArray("data");
                        getDataFromJSON(dataObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    Log.e("FragmentSearch", "Illegal argument eception");
                    e.printStackTrace();
                }
            }
        };
        GetHomeScreenTravelsRequest searchRequest = new GetHomeScreenTravelsRequest(token, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(searchRequest);
    }

    public void getDataFromJSON(JSONArray arr) throws JSONException {

        travelList = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) { // Walk through the Array.
            JSONObject obj = arr.getJSONObject(i);
            System.out.println("-->travel: " + obj);

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
            t.setEndDate(dateTo);
            t.setPhotoPath(mainPhotoURL);

            travelList.add(t);
        }
        System.out.println("List of searched users:\n" + travelList.toString());

        setupTravelListView();
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
                alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        removeTravel(travelID, listPosition);

                    }
                });
                alert.setNegativeButton("BACk", new DialogInterface.OnClickListener() {
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
