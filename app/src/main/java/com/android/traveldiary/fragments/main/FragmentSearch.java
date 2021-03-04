package com.android.traveldiary.fragments.main;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.adapters.SearchResultsAdapter;
import com.android.traveldiary.classes.User;
import com.android.traveldiary.fragments.FragmentUser;
import com.android.traveldiary.serverrequests.SearchRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class FragmentSearch extends Fragment {


    private ArrayList<User> userSearchResult = new ArrayList<>();
    private ListView searchResultList;
//    private SearchResultsAdapter listAdapter;

    public FragmentSearch() {
        // Required empty public constructor
    }

    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setTitle("Search");

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.my_toolbar);

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) toolbar.findViewById(R.id.search_view);
        searchResultList = (ListView)view.findViewById(R.id.search_list_view);


        //handle the searching situation
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
//                mAdapter.updateUIWithFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
//                Log.d("FragmentSearch","onQueryTextChange");
                if (newText.length() >= 1) {
//                    Log.d("FragmentSearch","execute");

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    String resp = jsonResponse.toString();
//                                    Log.i("Resp",""+resp);
                                    JSONArray dataObject = jsonResponse.getJSONArray("data");
                                    getDataFromJSON(dataObject);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch(IllegalArgumentException e){
//                                Log.e("FragmentSearch","Illegal argument eception");
                                e.printStackTrace();
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Log.e( "ResponseError",""+error.toString());
                            System.out.println("Failure (" + error.networkResponse.statusCode + ")");
                            parseVolleyError(error);

                        }
                    };
                    SearchRequest searchRequest = new SearchRequest(getToken(), newText, responseListener,errorListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    queue.add(searchRequest);
                }
                return false;
            }
        });

        searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Log.e("FragmentSearch","OnListItmeClick");

                hideKeyboard(getActivity());

                User user = (User)searchResultList.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("username", user.getUsername());
                bundle.putString("name", user.getName());
                bundle.putInt("id", user.getPersonID());
                // set Fragmentclass Arguments

                Fragment userFragment = new FragmentUser();
                userFragment.setArguments(bundle);

                ((MainActivity)getActivity()).replaceFragment(userFragment);

            }
        });

        return view;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void getDataFromJSON(JSONArray arr) throws JSONException {
        userSearchResult.clear();
//        Log.i("JSONArray",""+arr);
        for (int i = 0; i < arr.length(); i++) { // Walk through the Array.
            JSONObject obj = arr.getJSONObject(i);
            System.out.println("-->user: "+obj);

            int id = obj.getInt("id");
            String name = obj.getString("name");
            String username = obj.getString("username");
            User user = new User(id,username,name);
            userSearchResult.add(user);
        }
        System.out.println("List of searched users:\n"+userSearchResult.toString());

//        listAdapter = new SearchResultsAdapter(getActivity(),userSearchResult);
        searchResultList.setAdapter(new SearchResultsAdapter(getActivity(),userSearchResult));

        System.out.println("List of searched users:\n"+userSearchResult.toString());
    }


    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        getActivity().setTitle("Search");

    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().setTitle("Search");
    }


    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private String getToken(){
        SharedPreferences preferences = getActivity().getSharedPreferences("appPref", MODE_PRIVATE);
        String token = preferences.getString("token", "");
        return token;
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
//            Log.e("VolleyError",""+ message);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
