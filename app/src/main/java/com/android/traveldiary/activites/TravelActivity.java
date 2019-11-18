package com.android.traveldiary.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.Nullable;

import com.android.traveldiary.adapters.EntriesListAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.traveldiary.diaryentries.DiaryEntry;
import com.android.traveldiary.fragments.EntryFragment;
import com.android.traveldiary.R;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.android.traveldiary.dummy.DummyContent;
import com.android.traveldiary.traveladds.AddMapActivity;
import com.android.traveldiary.traveladds.AddNoteActivity;
import com.android.traveldiary.traveladds.AddPhotoActivity;
import com.android.traveldiary.traveladds.AddTransportActivity;
import com.android.traveldiary.traveladds.AddVoiceNoteActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

//import java.text.DateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TravelActivity extends AppCompatActivity implements EntryFragment.OnListFragmentInteractionListener {

    CollapsingToolbarLayout collapsingToolbar;
    ImageView toolbarImage;
    Toolbar toolbar;
    NestedScrollView nestedScrollView;

    DatabaseHelper helper;
    Travel travel;
    String travelTitle = "";

//    List<String> dates;
//    ViewPager entriesViewPager;
//    TextView previousDayTV, nextDayTV, currentDayTV, currentDateTV;


    //leftover EntryFragment
    private EntryFragment.OnListFragmentInteractionListener mListener;
    private String date;
    private int day;
    private EntriesListAdapter listAdapter;
    private Context context;
    RecyclerView entriesRecyclerView;
    List<DiaryEntry> entriesList;


//    LocalDate currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        helper = new DatabaseHelper(this);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFillViewport (true);

        context = getApplicationContext();
        getTravelInfo();
        setupToolbar();
        setupFABmenu();
        setupEntriesRecyclerView();
//        getDates();
//        setupViewPager();
//        try {
//            setupNextPreviousButton();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    private void setupEntriesRecyclerView(){
        Log.e("EntryFragment.onCreateView()","entriesList.size()>0");
        //todo change sorting?
        Collections.sort(entriesList);

        listAdapter = new EntriesListAdapter(
                getApplicationContext(),
                entriesList,
                TravelActivity.this,
                new EntriesListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(DiaryEntry item) {
                        Toast.makeText(context, "Item Clicked", Toast.LENGTH_LONG).show();
                    }
                }
        );

        entriesRecyclerView = (RecyclerView)findViewById(R.id.entriesListRV);
        entriesRecyclerView.setAdapter(listAdapter);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        entriesRecyclerView.setHasFixedSize(true);


        Log.e("EntryFragment.onCreateView()","listAdapter.getItemCount(): "+listAdapter.getItemCount());
    }

    public void getTravelInfo() {
        Intent intent = getIntent();
        int travelID = intent.getIntExtra("travelID", -1);
        travel = helper.getTravel(travelID);

        String noDateNeeded = "";
        entriesList = helper.getEntries(travel.getTravelID(),noDateNeeded);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                //todo refresh adapter with content
                entriesRecyclerView.getAdapter().notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //do nothing
            }
        }

    }

    /**
     * --------------------------------------
     * SETUP TOOLBAR
     */

    public void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(travelTitle);
        toolbarImage = (ImageView) findViewById(R.id.toolbar_image_view);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        if (travel != null) {
            collapsingToolbar.setTitle(travel.getTitle());
            if (!travel.getImagePath().matches("")) {
                toolbarImage.setImageBitmap(getBitmap(travel.getImagePath()));
//                setupToolbarColorPalete();
            }
        }
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap = null;
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    int mutedColor;
    public void setupToolbarColorPalete() {
        Bitmap bitmap = imageView2Bitmap(toolbarImage);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                int lightMutedColor = palette.getLightMutedColor(R.attr.colorPrimary);
                int vibrant = palette.getVibrantColor(R.attr.colorPrimary);
                int lightVibrant = palette.getVibrantColor(R.attr.colorPrimary);

                collapsingToolbar.setContentScrimColor(mutedColor);
                collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
                collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
                Typeface tf = Typeface.createFromAsset(getAssets(),"strawberry_blossom.ttf");
                System.out.println(tf.getStyle());
                collapsingToolbar.setExpandedTitleTypeface(tf);
            }
        });
    }

    private Bitmap imageView2Bitmap(ImageView view) {
        Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
        return bitmap;
    }



//    private void nextDate() {
////        if (isFABOpen) closeFABMenu();
////        else {
//            currentDate = currentDate.plusDays(1);
//            currentDay += 1;
//            currentDateTV.setText(dateToString(currentDate));
//            currentDayTV.setText("" + currentDay);
////        }
//    }
//
//    private void previousDate() {
//
//            currentDate = currentDate.minusDays(1);
//            currentDay -= 1;
//
//            currentDateTV.setText(dateToString(currentDate));
//            currentDayTV.setText("" + currentDay);
//    }


    /**
     * request permission to use camera and reas from file
     */
    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
//                            openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Permission Request Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     *
     * @param startDate - true => return startDay in long
     * @return
     */
    private long getDateInMillis(boolean isStartDate){
        Calendar c = Calendar.getInstance();
        if(isStartDate) {
            String startDate = travel.getStartDate();
            int year = Integer.parseInt(startDate.substring(6));
            int month = Integer.parseInt(startDate.substring(3,5));
            int day = Integer.parseInt(startDate.substring(0,2));
            c.set(year, month, day);
            return c.getTimeInMillis();
        }
        else{
            String endDate = travel.getEndDate();
            int year = Integer.parseInt(endDate.substring(6));
            int month = Integer.parseInt(endDate.substring(3,5));
            int day = Integer.parseInt(endDate.substring(0,2));
            c.set(year, month, day);
            return c.getTimeInMillis();
        }
    }


    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }






    /**
     * ---------------------------------------
     * SETUP FAB Button MENU
     */

    FloatingActionButton fab_map, fab_photo, fab_audio, fab_note, fab_transport;
    FloatingActionMenu floatingActionMenu;
    boolean isFABOpen;
    CoordinatorLayout FAB_menuLayout;
    RelativeLayout blurBackgroundLayout;

    private void setupFABmenu() {
//        blurBackgroundLayout = (RelativeLayout) findViewById(R.id.blur_background_layout);
//        blurBackgroundLayout.setVisibility(View.GONE);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        floatingActionMenu.setClosedOnTouchOutside(true);
        floatingActionMenu.setMenuButtonColorNormal(getResources().getColor(R.color.colorPrimary));
        floatingActionMenu.setMenuButtonColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        floatingActionMenu.setMenuButtonColorRipple(getResources().getColor(R.color.colorAccent));



        fab_map = (FloatingActionButton) findViewById(R.id.fab_map);
        fab_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        fab_audio = (FloatingActionButton) findViewById(R.id.fab_audio);
        fab_note = (FloatingActionButton) findViewById(R.id.fab_note);
        fab_transport = (FloatingActionButton) findViewById(R.id.fab_transport);

        //maps
        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = entriesList.get(entriesList.size()-1).getPosition()+1;
                Intent intent = new Intent(getApplicationContext(), AddMapActivity.class);
                intent.putExtra("travelID", travel.getTravelID());
                intent.putExtra(Consts.STRING_CURRENT_DATE, travel.getStartDate());
                intent.putExtra(Consts.LONG_START_DATE, getDateInMillis(true));
                intent.putExtra(Consts.LONG_END_DATE, getDateInMillis(false));
                intent.putExtra(Consts.STRING_ENTRY_POSITION,newPosition);
                startActivityForResult(intent,1);

            }
        });
        //photo
        fab_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photoDir = "";
                requestMultiplePermissions();
                int newPosition = entriesList.get(entriesList.size()-1).getPosition()+1;

                Intent intent = new Intent(getApplicationContext(), AddPhotoActivity.class);
                intent.putExtra("travelID", travel.getTravelID());
                intent.putExtra(Consts.STRING_CURRENT_DATE, travel.getStartDate());
                intent.putExtra(Consts.LONG_START_DATE, getDateInMillis(true));
                intent.putExtra(Consts.LONG_END_DATE, getDateInMillis(false));
                intent.putExtra(Consts.STRING_ENTRY_POSITION,newPosition);
                intent.putExtra("photoDir", photoDir);
                startActivityForResult(intent,1);
            }
        });
        //voice record
        fab_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = entriesList.get(entriesList.size()-1).getPosition()+1;
                Intent intent = new Intent(getApplicationContext(), AddVoiceNoteActivity.class);
                intent.putExtra("travelID", travel.getTravelID());
                intent.putExtra(Consts.STRING_CURRENT_DATE, travel.getStartDate());
                intent.putExtra(Consts.LONG_START_DATE, getDateInMillis(true));
                intent.putExtra(Consts.LONG_END_DATE, getDateInMillis(false));
                intent.putExtra(Consts.STRING_ENTRY_POSITION,newPosition);
                startActivityForResult(intent,1);
            }
        });
        //note
        fab_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = entriesList.get(entriesList.size()-1).getPosition()+1;
                Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
                intent.putExtra("travelID", travel.getTravelID());
                intent.putExtra(Consts.STRING_CURRENT_DATE, travel.getStartDate());
                intent.putExtra(Consts.LONG_START_DATE, getDateInMillis(true));
                intent.putExtra(Consts.LONG_END_DATE, getDateInMillis(false));
                intent.putExtra(Consts.STRING_ENTRY_POSITION,newPosition);
                startActivityForResult(intent,1);
            }
        });
        // transport
        fab_transport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = entriesList.get(entriesList.size()-1).getPosition()+1;
                Intent intent = new Intent(getApplicationContext(), AddTransportActivity.class);
                intent.putExtra(Consts.STRING_TRAVEL_ID, travel.getTravelID());
                intent.putExtra(Consts.STRING_CURRENT_DATE, travel.getStartDate());
                intent.putExtra(Consts.LONG_START_DATE, getDateInMillis(true));
                intent.putExtra(Consts.LONG_END_DATE, getDateInMillis(false));
                intent.putExtra(Consts.STRING_ENTRY_POSITION,newPosition);


                startActivityForResult(intent,1);
            }
        });
    }

    private LocalDate stringToDate(String date) {
        Log.d("Date to parse", date);
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(Consts.STRING_DATE_PATTERN));
        return localDate;
    }
    private String dateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(Consts.STRING_DATE_PATTERN));
    }



//    private void getDates() {
//        dates = new ArrayList<>();
//
//        LocalDate startDate = stringToDate(travel.getStartDate());
//        LocalDate endDate = stringToDate(travel.getEndDate());
//
//        while (!startDate.isAfter(endDate)) {
//            dates.add(dateToString(startDate));
//            startDate = startDate.plusDays(1);
//            Log.e("TravelActivity.getDates()","dates: "+dates.size());
//        }
//        if(dates.size()==1) {
//            isOneDayTravel = true;
//            previousClickable = false;
//            nextClickable = false;
//        }
//    }
//    private void setupViewPager() {
//        //here we store fragmenets that will have entriesList from each day of the travel
//        entriesViewPager = (ViewPager) findViewById(R.id.entriesViewPager);
//
//
////        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//
//        Log.e("TravelActivity.setupViewPager()","dates.size(): "+dates.size());
//        //todo
//        for (int i = 0; i < dates.size(); i++) {
//            List<DiaryEntry>entriesList = helper.getEntries(travel.getTravelID(), dates.get(i));
//            EntryFragment fragment = new EntryFragment(
//                    TravelActivity.this,
//                    entriesList,
//                    i + 1,
//                    dates.get(i));
//            adapter.addFragment(fragment);
//            Log.e("TravelActivity.setupViewPager()","entriesList.size(): "+entriesList.size());
//        }
//        entriesViewPager.setAdapter(adapter);
//        entriesViewPager.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                return true;
//            }
//        });
//    }

    /**
     *  LEFTOVERS OF DATES
     */

//    int currentDay = 1;
//    boolean previousClickable = false;
//    boolean nextClickable = true;

//    private void setupNextPreviousButton() throws ParseException {
//        previousDayTV = (TextView) findViewById(R.id.previous_day);
//        nextDayTV = (TextView) findViewById(R.id.next_day);
//        currentDateTV = (TextView) findViewById(R.id.travel_date);
//        currentDayTV = (TextView) findViewById(R.id.travel_day);
//
//        //setup first day and first date of the travel
//        final LocalDate firstDate = stringToDate(travel.getStartDate());
//        final LocalDate lastDate = stringToDate(travel.getEndDate());
//        currentDate = stringToDate(travel.getStartDate());
//
//        currentDateTV.setText(travel.getStartDate());
//        currentDayTV.setText("" + currentDay);
//
//        if(isOneDayTravel){
//            nextDayTV.setVisibility(View.INVISIBLE);
//            previousDayTV.setVisibility(View.INVISIBLE);
//        }
//
//        previousDayTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (previousClickable) {
//                    previousDate();
//
//                    if (currentDate.compareTo(firstDate) == 0) {
//                        previousDayTV.setVisibility(View.INVISIBLE);
//                        changeToClickable(false, nextClickable);
//                    }
//                    if (!nextClickable) {
//                        nextDayTV.setVisibility(View.VISIBLE);
//                        changeToClickable(previousClickable, true);
//                    }
//                }
//            }
//        });
//        nextDayTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (nextClickable) {
//                    nextDate();
//
//                    if (currentDate.compareTo(lastDate) == 0) {
//                        nextDayTV.setVisibility(View.INVISIBLE);
//                        changeToClickable(previousClickable, false);
//                    }
//                    if (!previousClickable) {
//                        Log.d("previous", "setToClickable()");
//                        previousDayTV.setVisibility(View.VISIBLE);
//                        changeToClickable(true, nextClickable);
//                    }
//                }
//            }
//        });
//    }
//

//    private void changeToClickable(boolean previous, boolean next) {
//        previousClickable = previous;
//        nextClickable = next;
//    }


    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }
}
