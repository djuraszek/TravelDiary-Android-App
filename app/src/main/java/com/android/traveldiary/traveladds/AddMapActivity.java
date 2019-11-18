package com.android.traveldiary.traveladds;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.traveldiary.diaryentries.MapMarker;
import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class AddMapActivity extends AppCompatActivity {
    private static String APP_TAG = "AddMapActvity";
    private static String OSM_PREFS = "PreferencesOSM";
    //    MapView map, mMapView;
//    MyLocationNewOverlay mLocationOverlay;
//    CompassOverlay mCompassOverlay;
//    ScaleBarOverlay mScaleBarOverlay;
    Context context;
    //    RotationGestureOverlay mRotationGestureOverlay;
    ItemizedIconOverlay mMyLocationOverlay;

    int travelID, position;
    String date;

    double EuropeCenterLongitude = 48.815062;
    double longeuropeCenterLatitude = 11.571591;


    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    //    private ResourceProxy mResourceProxy;

    protected ImageButton btCenterMap;
    private TextInputEditText titleET, moreInfoET;

    private Marker marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handle permissions here

        //chck internet on and localization on
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //load/initialize the osmdroid configuration, this can be done
        Context context = AddMapActivity.this;
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        //inflate and create the map
        setContentView(R.layout.activity_add_map);
        checkNetworkServiceEnabled();
        context = getBaseContext();
        getIntentExtras();


        titleET = (TextInputEditText) findViewById(R.id.input_title);
        moreInfoET = (TextInputEditText) findViewById(R.id.input_more_info);

        mMapView = (MapView) findViewById(R.id.map_view);
        Log.e("AddMapActivity", "null map " + (mMapView == null));
        setup();

    }


    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (mMapView != null)
            mMapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        if (mMapView != null)
            mMapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void getIntentExtras() {
        Intent intent = this.getIntent();
        travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);
        date = intent.getStringExtra(Consts.STRING_CURRENT_DATE);

        position = intent.getIntExtra(Consts.STRING_ENTRY_POSITION, -1);

        if (travelID == -1) {
            Toast.makeText(getApplicationContext(), "Error occured: AddTransport", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void checkNetworkServiceEnabled() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled ) {
            // notify user
            //todo wywala blad przy show
            new AlertDialog.Builder(this)
                    .setMessage("GPS network not enabled")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            onBackPressed();
                        }
                    })
                    .setPositiveButton("Enable in settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            AddMapActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .show();
        }
    }

    public void setup() {
        final DisplayMetrics dm = getResources().getDisplayMetrics();

        this.mCompassOverlay = new CompassOverlay(AddMapActivity.this, new InternalCompassOrientationProvider(this), mMapView);
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(AddMapActivity.this), mMapView);

        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mRotationGestureOverlay = new RotationGestureOverlay(this, mMapView);
        mRotationGestureOverlay.setEnabled(true);


        mMapView.setTilesScaledToDpi(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setFlingEnabled(true);

//        mMapView.getOverlays().add(this.mRotationGestureOverlay);
        mMapView.getOverlays().add(this.mLocationOverlay);
        mMapView.getOverlays().add(this.mCompassOverlay);
        mMapView.getOverlays().add(this.mScaleBarOverlay);

        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);
        mCompassOverlay.enableCompass();


        mMapView.setMinZoomLevel(3.0);
        mMapView.getController().zoomTo(12.0);

        System.out.println("zoom: " + mMapView.getMinZoomLevel());
        System.out.println("zoom: " + mMapView.getMaxZoomLevel());
        System.out.println("zoom: " + mMapView.getZoomLevelDouble());

        btCenterMap = (ImageButton) findViewById(R.id.ic_center_map);

        btCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AddMapActvity", "centerMap clicked ");
                if(mLocationOverlay.getMyLocation() != null) {
                    GeoPoint myPosition = new GeoPoint(mLocationOverlay.getMyLocation().getLatitude(), mLocationOverlay.getMyLocation().getLongitude());
                    mMapView.getController().animateTo(myPosition);
                    System.out.println("zoom: " + mMapView.getZoomLevelDouble());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enable GPS",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (markerSet) {
                    Marker marker = (Marker) mMapView.getOverlays().get(mMapView.getOverlays().size() - 1);
                    GeoPoint position = marker.getPosition();

                    String title = titleET.getText().toString();
                    if (!title.matches(""))
                        marker.setTitle(title);
                    String moreInfo = moreInfoET.getText().toString();
                    if (!moreInfo.matches("")) marker.setSnippet(moreInfo);
                    Toast.makeText(getBaseContext(), title, Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint point) {
                Toast.makeText(getBaseContext(), point.getLatitude() + " - " + point.getLongitude(), Toast.LENGTH_LONG).show();
                addMarker(point);
                return false;
            }
        };
        mMapView.getOverlays().add(new MapEventsOverlay(mReceive));

        MaterialButton saveBtn = (MaterialButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

//    public void roadManagement(){
//        RoadManager roadManager = new OSRMRoadManager(this);
//        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
//        waypoints.add(startPoint);
//        GeoPoint endPoint = new GeoPoint(48.4, -1.9);
//        waypoints.add(endPoint);
//        map.getOverlays().add(roadOverlay);
//    }

//    /**
//     * @param searchFacility e.g.'cinema' 'airport' ...
//     */
//    public void getPointsOfInterest(String searchFacility) {
//        GeoPoint startPoint = (GeoPoint) mMapView.getMapCenter();
//        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
//        ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, searchFacility, 50, 0.1);
//
//        //group all those POI markers in a FolderOverlay.
//        FolderOverlay poiMarkers = new FolderOverlay(this);
//        mMapView.getOverlays().add(poiMarkers);
//
//        //create the markers, and put them in the FolderOverlay
//        Drawable poiIcon = getResources().getDrawable(R.drawable.marker_poi_default);
//        for (POI poi : pois) {
//            Marker poiMarker = new Marker(mMapView);
//            poiMarker.setTitle(poi.mType);
//            poiMarker.setSnippet(poi.mDescription);
//            poiMarker.setPosition(poi.mLocation);
//            poiMarker.setIcon(poiIcon);
//            if (poi.mThumbnail != null) {
//                poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
//            }
//            poiMarkers.add(poiMarker);
//        }
//    }


    boolean markerSet = false;

    public Marker addMarker(GeoPoint position) {
        if (markerSet) {
            mMapView.getOverlays().remove(mMapView.getOverlays().size() - 1);
            markerSet = false;
        }
        marker = new Marker(mMapView);
        marker.setPosition(position);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable icon = getResources().getDrawable(R.drawable.marker_poi_default);
//        Drawable icon = getCustomMarkerIcon();
        icon.setTint(getColor(R.color.red));
        marker.setIcon(icon);

//        marker.set
        marker.setPanToView(true);
        marker.setDraggable(true);

//        GeoPoint position = marker.getPosition();
        String title = titleET.getText().toString();
        if (title.matches(""))
            marker.setTitle("Latitude: " + position.getLatitude() + "\n" + "Longitude: " + position.getLongitude());
        else
            marker.setTitle(title);
        String moreInfo = moreInfoET.getText().toString();
        if (!moreInfo.matches("")) marker.setSnippet(moreInfo);

        MarkerInfoWindow newInfoWindow = new MarkerInfoWindow(R.layout.map_marker_layout, mMapView);
        marker.setInfoWindow(newInfoWindow);
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();



        markerSet = true;
        return marker;
    }

    public Drawable getCustomMarkerIcon() {
        View viewMarker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker_layout, null);
        ImageView myImage = (ImageView) viewMarker.findViewById(R.id.img_id);
        return myImage.getDrawable();
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void save() {
        if (titleET.getText().toString().matches("")) {
            titleET.setError("Obligatory field");
        } else if(marker==null){
            Toast.makeText(this, "Longclick on map to put new marker",Toast.LENGTH_SHORT).show();
        }
        else {
            MapMarker n = new MapMarker(getUniqueID(), titleET.getText().toString(),moreInfoET.getText().toString(),
                    marker.getPosition().getLongitude(),marker.getPosition().getLatitude(),
                    date, position, travelID);

            DatabaseHelper helper = new DatabaseHelper(AddMapActivity.this);
            helper.addEntry(n);
            showSavedMessage();
        }
    }



    private void showSavedMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Message");
//        alert.setMessage("Your map coordinates has been saved!");
//        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // continue with delete
//                Intent returnIntent = new Intent();
//                setResult(Activity.RESULT_OK, returnIntent);
//                finish();
//            }
//        });

        View view = LayoutInflater.from(AddMapActivity.this).inflate(R.layout.dialog_positive_layout,null);
        Button positiveButton = view.findViewById(R.id.btnDialog);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        alert.setView(view);
        alert.show();
    }


    public long getUniqueID() {
        return System.currentTimeMillis();
    }


}
