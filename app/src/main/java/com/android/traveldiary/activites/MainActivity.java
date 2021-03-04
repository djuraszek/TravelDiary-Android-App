package com.android.traveldiary.activites;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.classes.User;
import com.android.traveldiary.fragments.main.FragmentAdd;
import com.android.traveldiary.fragments.main.FragmentNotifications;
import com.android.traveldiary.fragments.main.FragmentProfile;
import com.android.traveldiary.fragments.main.FragmentSearch;
import com.android.traveldiary.fragments.main.FragmentTravels;
import com.android.traveldiary.adapters.TabAdapter;
import com.android.traveldiary.login_register.LoginActivity;
import com.android.traveldiary.serverrequests.GetFollowersRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.traveldiary.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final static String LANGUAGE_EN_LOCALE = "en-US";
    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    //Permissions that need to be explicitly requested from end user.
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    final Fragment fragment1 = new FragmentTravels();
    final Fragment fragment2 = new FragmentSearch();
    final Fragment fragment3 = new FragmentAdd();
    final Fragment fragment4 = new FragmentNotifications();
    final Fragment fragment5 = new FragmentProfile();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private BottomNavigationView bottomNavigationView;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExtendedFloatingActionButton fabButton;
    private List<Travel> travelList;
    private NestedScrollView nestedScrollView;

    private String token = "";
    private int myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLocale(Locale.ENGLISH.getLanguage());
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
//                .getColor(R.color.white)));

        System.out.println("Main.OnCreate()");
        checkPermissions();
        Locale.ENGLISH.getLanguage();

        SharedPreferences preferences = getSharedPreferences("appPref", MODE_PRIVATE);
        token = preferences.getString("token", "");
        myID = preferences.getInt("id", -1);
        isTokenSaved();

        setContentView(R.layout.activity_main);
        setupBottomNavView();
        getFollows();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    private void setupBottomNavView() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.fragment_container, fragment5, "MyProfile").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment4, "Notifications").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment3, "Add new").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment2, "Search").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment1, "Travellist").commit();

    }

    private void setToolbarTitle(String title) {
        getActionBar().setTitle(title);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_home:
                    popBackStack();
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.bottom_navigation_search:
                    popBackStack();
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.bottom_navigation_add:
                    popBackStack();
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;

                case R.id.bottom_navigation_notification:
                    popBackStack();
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    return true;

                case R.id.bottom_navigation_profile:
                    popBackStack();
                    fm.beginTransaction().hide(active).show(fragment5).commit();
                    active = fragment5;
                    return true;
            }
            return false;
        }

    };

    public void goToMyProfile(){
        popBackStack();
        fm.beginTransaction().hide(active).show(fragment5).commit();
        active = fragment5;
    }


    public String getToken() {
        return token;
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            Log.i("MainActivity", "replaceFragment");
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, "UserProfile");
            ft.addToBackStack(null); // name can be null
            ft.commit();
            active = fragment;
//
            Log.i("MainActivity", "fm.getBackStackEntryCount(): " + fm.getBackStackEntryCount());

//        fm.beginTransaction().show(fragment).commit();
        }
    }

    private static int DATA_SET_CHANGED_REQUEST_CODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
//        if (requestCode == DATA_SET_CHANGED_REQUEST_CODE && resultCode == RESULT_OK) {
//
//            ((FragmentTravels) adapter.getItem(0)).notifyDataSetChanged();
//            ((FragmentStatistics) adapter.getItem(1)).notifyDataSetChanged();
//        }
    }

    public void setLocale(String lang) {
        Resources res = this.getResources();
// Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        System.out.println("Locales: " + conf.getLocales().toLanguageTags().toString());
        conf.setLocale(new Locale(lang)); // API 17+ only.

// Use
        conf.locale = new Locale(lang); //if targeting lower versions
        res.updateConfiguration(conf, dm);
        System.out.println("Locales: " + conf.getLocales().toLanguageTags().toString());
    }


    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
//                todo initialize();
                break;
        }
    }


    private void popBackStack() {
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i("MainActivity.onBackPressed()", "fm.getBackStackEntryCount() " + fm.getBackStackEntryCount());
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            active = fm.findFragmentById(R.id.fragment_container);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isTokenSaved() {
        SharedPreferences preferences = getSharedPreferences("appPref", MODE_PRIVATE);
        String token = preferences.getString("token", "");
        Log.i("MainActivity", "token: " + token);
        return !token.equalsIgnoreCase("");
    }


    boolean mSlideState = false;
    //    private DrawerLayout drawerLayout;
    private String TAG = "MainActivity";

//    public void setupDrawerLayout() {
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerLayout.setDrawerListener(new ActionBarDrawerToggle(this,
//                drawerLayout,
//                null,
//                0,
//                0) {
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//                mSlideState = false;//is Closed
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                mSlideState = true;//is Opened
//            }
//        });
//
//        NavigationView navView = (NavigationView) findViewById(R.id.navView);
//        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_view_followers:
//                        Log.d(TAG, "nav_view_followers");
//                        break;
//                    case R.id.nav_view_following:
//                        Log.d(TAG, "nav_view_following");
//                        break;
//                    case R.id.nav_view_profile:
//                        Log.d(TAG, "nav_view_profile");
//                        break;
//                    case R.id.nav_view_statistics:
//                        Log.d(TAG, "nav_view_statistics");
//                        break;
//                    case R.id.nav_view_log_out:
//                        Log.d(TAG, "nav_view_log_out");
//
//                        break;
//                }
//                return false;
//            }
//        });
//
//        navView.inflateHeaderView(R.layout.nav_view_header);
//    }

//    public void openDrawer() {
//        if (mSlideState == false) {
//            drawerLayout.openDrawer(Gravity.END);
//        } else {
//            drawerLayout.closeDrawer(Gravity.END);
//        }
//    }


    /**
     * ------------------------------------------------------------------------
     * server requests
     * ------------------------------------------------------------------------
     */

    private ArrayList<User> followers;
    private ArrayList<User> following;

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
        GetFollowersRequest request = new GetFollowersRequest(token, myID, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void setFollowers(JSONObject dataObject) throws JSONException {
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        JSONArray followers = dataObject.getJSONArray("followers");
        for (int i = 0; i < followers.length(); i++) {
            JSONObject obj = followers.getJSONObject(i);

            int userID = obj.getInt("id");
            String name = obj.getString("name");
            String username = obj.getString("username");
            User u = new User(userID, username, name);
            this.followers.add(u);
        }

        JSONArray following = dataObject.getJSONArray("following");
        for (int i = 0; i < following.length(); i++) {
            JSONObject obj = following.getJSONObject(i);

            int userID = obj.getInt("id");
            String name = obj.getString("name");
            String username = obj.getString("username");
            User u = new User(userID, username, name);
            this.following.add(u);
        }
    }
//    "id":2,
//    "name":"Marcin",
//    "username":"marcinkozakk"


    public ArrayList<User> getFollowers() throws InterruptedException {
        if (followers == null) {
            getFollows();
//            TimeUnit.SECONDS.sleep(30);
            Log.e("MainActivity.getFollowers", "followers==null : true");
        }
        return followers;
    }

    public ArrayList<User> getFollowing() throws InterruptedException {
        if (followers == null) {
            getFollows();
//            TimeUnit.SECONDS.sleep(30);
            Log.e("MainActivity.getFollowers", "followers==null : false");
        }
        return following;
    }

    public void logOut() {
        deleteToken();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteToken(){
        getSharedPreferences("appPref", MODE_PRIVATE).edit().clear().commit();
    }

}
