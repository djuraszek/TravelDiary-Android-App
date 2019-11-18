package com.android.traveldiary.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.android.traveldiary.fragments.FragmentStatistics;
import com.android.traveldiary.fragments.FragmentTravels;
import com.android.traveldiary.adapters.TabAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.traveldiary.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Locale;

import android.content.res.Resources;
import android.util.DisplayMetrics;


import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private final static String LANGUAGE_EN_LOCALE = "en-US";
    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    //Permissions that need to be explicitly requested from end user.
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    CollapsingToolbarLayout collapsingToolbar;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExtendedFloatingActionButton fabButton;
    private List<Travel> travelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLocale(Locale.ENGLISH.getLanguage());
        super.onCreate(savedInstanceState);
        System.out.println("OnCreate");
        checkPermissions();
        Locale.ENGLISH.getLanguage();

        setContentView(R.layout.activity_main);
        setupCollapsingToolbar();

//        MainActivity.this.deleteDatabase("traveldiary.db");
//        helper.getReadableDatabase().execSQL("Delete from Transport");
//        helper.getReadableDatabase().close();
//        System.out.println("db path: " + path);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        setTranslucentStatusBar();
//        getWindow().getDecorView().

        setupTabLayout();
    }

    private void setupTabLayout() {
        DatabaseHelper helper = new DatabaseHelper(this);
        travelList = helper.getTravelsList();

        viewPager = (ViewPager) findViewById(R.id.tabs_view_pager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentTravels(), "Travels");
        adapter.addFragment(new FragmentStatistics(this), "Statistics");
        viewPager.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Travels"));
        tabLayout.addTab(tabLayout.newTab().setText("Statistics"));

//        final TabLayout.Tab statisticsTab = tabLayout.newTab();
//        statisticsTab.setText("Statistics");
//        System.out.println(""+statisticsTab.getText().toString());
//        tabLayout.addTab(statisticsTab);

//        tabLayout.selectTab();
//        tabLayout.setupWithViewPager(viewPager);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().matches("Travels")) {
                    fabButton.show();
                } else {
                    fabButton.hide();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //setup fab button
        fabButton = (ExtendedFloatingActionButton) findViewById(R.id.add_new_fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdd = new Intent(getApplicationContext(), AddActivity.class);
                intentAdd.putExtra(Consts.STRING_TRAVEL_ID, travelList.size());
                startActivityForResult(intentAdd, DATA_SET_CHANGED_REQUEST_CODE);
            }
        });
    }

    private static int DATA_SET_CHANGED_REQUEST_CODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == DATA_SET_CHANGED_REQUEST_CODE && resultCode == RESULT_OK) {

            ((FragmentTravels) adapter.getItem(0)).notifyDataSetChanged();
            ((FragmentStatistics) adapter.getItem(1)).notifyDataSetChanged();

        }
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

    @Override
    protected void onStart() {
        super.onStart();
//        System.out.println("OnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        System.out.println("OnResume");
    }


    private void setupCollapsingToolbar() {
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Travel diary");
        collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK);
        collapsingToolbar.setExpandedTitleTextColor(ColorStateList.valueOf(Color.WHITE));
//        collapsingToolbar.setExpandedTitleTextColor(Color.WHITE);
    }


    /**
     * HANDLE RIGHT SIDE NAVIGATION DRAWER
     */

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private void setNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, null, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        navigationView = (NavigationView) findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setCheckedItem(R.id.home);
    }


    @Override
    public void onBackPressed() {
    }


    //translucent bar
    public void setTranslucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
