package com.android.traveldiary.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.Nullable;

import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.diaryentries.Note;
import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.entryviewholders.PhotoViewHolder;
import com.android.traveldiary.serverrequests.GetTravelRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.traveldiary.diaryentries.DiaryEntry;
import com.android.traveldiary.fragments.EntryFragment;
import com.android.traveldiary.R;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.android.traveldiary.dummy.DummyContent;
import com.android.traveldiary.traveladds.AddNoteActivity;
import com.android.traveldiary.traveladds.AddPhotoActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

//import java.text.DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TravelActivity extends AppCompatActivity implements EntryFragment.OnListFragmentInteractionListener {
    public String TAG = "TravelActivity";

    CollapsingToolbarLayout collapsingToolbar;
    ImageView toolbarImage;
    Toolbar toolbar;
    NestedScrollView nestedScrollView;

    //    DatabaseHelper helper;
    Travel travel;
    String travelTitle = "";

    //leftover EntryFragment
    private EntryFragment.OnListFragmentInteractionListener mListener;
    private String date;
    private int day;
    private EntriesListAdapter listAdapter;
    private static Context context;
    RecyclerView entriesRecyclerView;
    List<DiaryEntry> entriesList;
    private String token;
    private boolean isMyTravel=false;
//    LocalDate currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
//        helper = new DatabaseHelper(this);
        context = getApplicationContext();

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFillViewport(true);

        final SwipeRefreshLayout pullToRefersh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTravelInfo();
                pullToRefersh.setRefreshing(false);
            }
        });

        getTravelInfo();
//        setupToolbar();

    }


    public void remove(DiaryEntry entry) {
        entriesList.remove(entry);
        listAdapter.notifyDataSetChanged();
    }


    public void getTravelInfo() {
        Intent intent = getIntent();
        final int travelID = intent.getIntExtra("travelID", -1);
        token = intent.getStringExtra("token");
        isMyTravel = intent.getBooleanExtra("isMyTravel",false);

        String noDateNeeded = "";
//        travel = helper.getTravel(travelID);
//        entriesList = helper.getEntries(travel.getTravelID(),noDateNeeded);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        Log.i("GetTravel: ServerResp", "" + dataObject.toString());

                        String title = dataObject.getString("title");
                        String startDate = dataObject.getString("start_date").substring(0, 10);
                        String endDate = dataObject.getString("end_date").substring(0, 10);

                        String photoURL = "";
                        if (!dataObject.get("main_photo").toString().equals("null"))
                            photoURL = (dataObject.getJSONObject("main_photo")).getString("path");
                        int userID = dataObject.getInt("user_id");

                        travel = new Travel(travelID, title, startDate, endDate);
                        travel.setMyTravel(isMyTravel);

                        if (!photoURL.equals("null") || !photoURL.equals("")){
                            travel.setPhotoPath(photoURL);
                        }

                        if (travel.isMyTravel())
                            setupFABmenu();

                        JSONArray travelEntries = dataObject.getJSONArray("photos&notes");
                        setUpEntiresList(travelEntries);
                        setupToolbar();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Error while loading travel");

                    e.printStackTrace();
                }
            }
        };

        GetTravelRequest request = new GetTravelRequest(token, travelID, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Handle Error
                if (error != null) {
                    Log.d("EditActivity", "Error: " + error + "\nStatus Code " + error.networkResponse.statusCode + "\nResponse Data " + error.networkResponse.data.toString() + "\nCause " + error.getCause() + "\nmessage" + error.getMessage());
                    Log.d(TAG, "Failed with error msg:\t" + error.getMessage());
                    Log.d(TAG, "Error StackTrace: \t" + error.getStackTrace().toString());
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void setUpEntiresList(JSONArray travelEntries) throws JSONException {
        entriesList = new ArrayList<>();

        for (int i = 0; i < travelEntries.length(); i++) {
            JSONObject dataObj = travelEntries.getJSONObject(i);
            String type = dataObj.getString("type");

            JSONObject dataObject = dataObj.getJSONObject("data");
            if (type.equals("photo")) {

                int id = dataObject.getInt("id");
                String title = dataObject.getString("title");
                String photoURL = dataObject.getString("path");
                String date = dataObject.getString("created_at");
                int travelID = dataObject.getInt("travel_id");

                if (!title.equals("albumPhoto")) {
                    Photo p = new Photo(id, title, photoURL, date, -1, travelID);
                    entriesList.add(p);
                }
            } else {
                int id = dataObject.getInt("id");
                String title = dataObject.getString("title");
                String note = dataObject.getString("note");
                String date = dataObject.getString("created_at");
                int travelID = dataObject.getInt("travel_id");

                Note n = new Note(id, title, note, date, -1, travelID);
                entriesList.add(n);
            }
        }
        setupEntriesRecyclerView();
    }


    private void setupEntriesRecyclerView() {
//        Log.e("EntryFragment.onCreateView()", "entriesList.size()>0");
        Collections.sort(entriesList);

        listAdapter = new EntriesListAdapter(
                getApplicationContext(),
                entriesList,
                TravelActivity.this,
                new EntriesListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(final DiaryEntry item) {
                        //todo
                        Toast.makeText(context, "Item Clicked", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "" + item.getEntryType());
                    }
                }, token, isMyTravel
        );

        entriesRecyclerView = (RecyclerView) findViewById(R.id.entriesListRV);
        entriesRecyclerView.setAdapter(listAdapter);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        entriesRecyclerView.setHasFixedSize(true);
        entriesRecyclerView.getAdapter().notifyDataSetChanged();

        Log.e("EntryFragment.onCreateView()", "listAdapter.getItemCount(): " + listAdapter.getItemCount());
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
            if (!travel.getImagePath().equals("")) {
                Log.i(TAG,"photoURL ("+travel.getImagePath()+")");
                new GetImageFromUrl(toolbarImage, this).execute(travel.getImagePath());
            }else {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.city);
                toolbarImage.setImageBitmap(bitmap);
            }
        }
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    int mutedColor;

    public void setupToolbarColorPalete(Bitmap bitmap1) {
        Bitmap bitmap = imageView2Bitmap(toolbarImage);
        if (bitmap != null)
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
                }
            });
    }

    private Bitmap imageView2Bitmap(ImageView view) {
        Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
        return bitmap;
    }


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
     * @param startDate - true => return startDay in long
     * @return
     */
    private long getDateInMillis(boolean isStartDate) {
        Calendar c = Calendar.getInstance();
        if (isStartDate) {
            String startDate = travel.getStartDate();
            int year = Integer.parseInt(startDate.substring(0, 4));
            int month = Integer.parseInt(startDate.substring(5, 7));
            int day = Integer.parseInt(startDate.substring(8, 10));
            c.set(year, month, day);
            return c.getTimeInMillis();
        } else {
            String endDate = travel.getEndDate();
            int year = Integer.parseInt(endDate.substring(0, 4));
            int month = Integer.parseInt(endDate.substring(5, 7));
            int day = Integer.parseInt(endDate.substring(8, 10));
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

    FloatingActionButton fab_photo, fab_note;
    FloatingActionMenu floatingActionMenu;
    boolean isFABOpen;
    CoordinatorLayout FAB_menuLayout;
    RelativeLayout blurBackgroundLayout;

    private void setupFABmenu() {
//        blurBackgroundLayout = (RelativeLayout) findViewById(R.id.blur_background_layout);
//        blurBackgroundLayout.setVisibility(View.GONE);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        floatingActionMenu.setClosedOnTouchOutside(true);
        floatingActionMenu.setMenuButtonColorNormal(getResources().getColor(R.color.colorAccent));
        floatingActionMenu.setMenuButtonColorPressed(getResources().getColor(R.color.colorAccentDarker));
        floatingActionMenu.setMenuButtonColorRipple(getResources().getColor(R.color.colorPrimaryDark));

        floatingActionMenu.setVisibility(View.VISIBLE);

        fab_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        fab_note = (FloatingActionButton) findViewById(R.id.fab_note);

        fab_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photoDir = "";
                requestMultiplePermissions();
                int newPosition = 0;
                Intent intent = new Intent(getApplicationContext(), AddPhotoActivity.class);
                intent.putExtra(Consts.STRING_TRAVEL_ID, travel.getTravelID());
                intent.putExtra(Consts.STRING_CURRENT_DATE, travel.getStartDate());
                intent.putExtra(Consts.LONG_START_DATE, getDateInMillis(true));
                intent.putExtra("photoDir", photoDir);
                intent.putExtra("token", token);
                startActivityForResult(intent, 1);
            }
        });

        fab_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = 0;
                Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
                intent.putExtra("travelID", travel.getTravelID());
                intent.putExtra(Consts.STRING_CURRENT_DATE, travel.getStartDate());
                intent.putExtra("token", token);
                startActivityForResult(intent, 1);
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


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TravelActivity", "onResume");
//        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo); todo
        if (v.getId() == R.id.entriesListRV) {
            MenuInflater inflater = this.getMenuInflater();
//            ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
            menu.setHeaderTitle("Select the action");
            Log.i(TAG, "clicked view " + v.toString());
            Log.i(TAG, "clicked view " + v.getTag());
            inflater.inflate(R.menu.entry_list_item_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int listPosition = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        final DiaryEntry entry = entriesList.get(listPosition);

        switch (item.getItemId()) {
//            case R.id.edit:
//                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
//                intent.putExtra(Consts.STRING_TRAVEL_ID, travelID);
//                startActivity(intent);
//                return true;

            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                alert.setTitle("Delete entry");

                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
//                        removeEntry(entry);

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

    private long getEntryID(DiaryEntry entry) {
        String entryType = entry.getEntryType();
        if (entryType == Consts.ENTRY_TYPE_NOTE)
            return ((Note) entry).getID();
        else if (entryType == Consts.ENTRY_TYPE_PHOTO)
            return ((Photo) entry).getID();
        else return -1;
    }

//    private void removeEntry(DiaryEntry entry) {
//        //todo remove
//        long id = getEntryID(entry);
//        if (id != -1) {
//            helper.removeEntry(id, entry.getEntryType());
//            entriesList = helper.getEntries(travel.getTravelID(), "");
//            ((EntriesListAdapter) entriesRecyclerView.getAdapter()).updateEntries(entriesList);
//        }
//    }

//
//    private void showAlert(final DiaryEntry item) {
//        AlertDialog.Builder alert = //new AlertDialog.Builder(getApplicationContext());
//                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
//        alert.setTitle("Delete entry");
//
//        alert.setMessage("Are you sure you want to delete?");
//        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // continue with delete
//                removeEntry(item);
//
//            }
//        });
//        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // close dialog
//                dialog.cancel();
//            }
//        });
//        alert.show();
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //todo refresh adapter with content
                getTravelInfo();
//                ((EntriesListAdapter) entriesRecyclerView.getAdapter()).updateEntries(entriesList);
//                entriesRecyclerView.getAdapter().notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //do nothing
            }
        }

    }


    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap bitmap;
        TravelActivity travelActivity;

        public GetImageFromUrl(ImageView img, TravelActivity travelActivity) {
            this.imageView = img;
            this.travelActivity = travelActivity;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String stringUrl = url[0];
            bitmap = null;
            InputStream inputStream;
            try {
                inputStream = new java.net.URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
            travelActivity.setupToolbarColorPalete(bitmap);
        }
    }
}
