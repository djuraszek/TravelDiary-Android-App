package com.android.traveldiary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.traveldiary.R;
import com.android.traveldiary.adapters.FlagRecyclerViewAdapter;
import com.android.traveldiary.classes.MapWebView;
import com.android.traveldiary.database.DatabaseHelper;
import com.mukesh.countrypicker.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FragmentStatistics extends Fragment {
    private static int COUNTRIES_IN_THE_WORLD = 250;
    private TextView countriesCountTV, worldPercentTV;
    private DatabaseHelper helper;
    private RecyclerView flagsRecyclerView;
    private FlagRecyclerViewAdapter flagListAdapter;

    private List<String> countries;
    private List<Integer> flags;
    Activity activity;

    public FragmentStatistics() {
        // Required empty public constructor
    }

    public FragmentStatistics(Activity activity){
        this.activity = activity;
    }

    public static FragmentStatistics newInstance() {
        FragmentStatistics fragment = new FragmentStatistics();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_statistics, container, false);
        helper = new DatabaseHelper(getContext());
        countries = helper.getDistinctCountryVisits();
        int visitedCountries = countries.size();
        countriesCountTV = (TextView) view.findViewById(R.id.countries_count_TV);
        countriesCountTV.setText("" + visitedCountries);
        worldPercentTV = (TextView) view.findViewById(R.id.world_percent_TV);
        int percent = 100 * visitedCountries / COUNTRIES_IN_THE_WORLD;
        worldPercentTV.setText(percent + "%");
        flagsRecyclerView = (RecyclerView) view.findViewById(R.id.flags_recyclerview);
        setFlags();
        mapView = (MapWebView) view.findViewById(R.id.webview);
        setMapChart();
        return view;
    }

    public void notifyDataSetChanged() {
        countries.clear();
        countries = helper.getDistinctCountryVisits();
        int visitedCountries = countries.size();
        countriesCountTV.setText("" + visitedCountries);
        int percent = 100 * visitedCountries / COUNTRIES_IN_THE_WORLD;
        worldPercentTV.setText(percent + "%");

        setFlags();
        setMapChart();
    }

    private static String LOG = "Fragment Statistics";

    private void setFlags() {
//        Log.e(LOG, "countries.size() " + countries.size());
        flags = new ArrayList<>();
        for (String country : countries) {
            System.out.println(country);
            flags.add(Country.getCountryByName(country).getFlag());
        }

//        int numberOfColumns = calculateNoOfColumns(getContext(),50);
        int numberOfColumns = 5;
        flagListAdapter = new FlagRecyclerViewAdapter(this.getContext(), flags, countries);
        flagListAdapter.setClickListener(new FlagRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("TAG", "You clicked number " + flagListAdapter.getCountry(position) + ", which is at cell position " + position);
            }
        });
        flagsRecyclerView.setAdapter(flagListAdapter);
        flagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        flagsRecyclerView.setHasFixedSize(true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }



    //google statistics map chart

    MapWebView mapView;

    private void setMapChart() {
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setSupportZoom(true);
        mapView.getSettings().setBuiltInZoomControls(false);
        mapView.getSettings().setDisplayZoomControls(false);
        // this function is used to access javascript from html page
        mapView.addJavascriptInterface(new JavaScriptInterface(getContext()), "AndroidNativeCode");
        // load file from assets folder
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if(tabletSize)
            mapView.loadUrl("file:///android_asset/map_tablet.html");
        else
            mapView.loadUrl("file:///android_asset/map.html");
    }



    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        // method to send JsonArray to HTML
        @JavascriptInterface
        public void getValueJson() throws JSONException {
            final JSONArray jArray = new JSONArray();
            for(String c: countries){
                JSONObject jObject = new JSONObject();
                int rand = (int)(Math.random()*20)+1;
                jObject.put("Country", c);
                jObject.put("Popularity", rand);
                jArray.put(jObject);
            }

            System.out.println(jArray.toString());

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mapView.loadUrl("javascript:setJson(" + jArray + ")");
                }
            });

        }

    }
}
