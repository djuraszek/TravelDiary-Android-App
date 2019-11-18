package com.android.traveldiary.entryviewholders;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.android.traveldiary.diaryentries.MapMarker;
import com.android.traveldiary.R;
import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.classes.CustomMapView;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapViewHolder extends RecyclerView.ViewHolder {

    MapMarker obj;
    private CustomMapView mMapView;
    private Marker mapMarker;
    private Context context;
    protected ImageButton btCenterMap;
    MyLocationNewOverlay mLocationOverlay;

    boolean isSet = false;

    public MapViewHolder(View itemView, Context context) {
        super(itemView);
        Log.e("MapViewHolder", "constructor");
        //chck internet on and localization on
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //load/initialize the osmdroid configuration, this can be done
        this.context = context;
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        mMapView = itemView.findViewById(R.id.map_view);
        btCenterMap = (ImageButton) itemView.findViewById(R.id.ic_center_map);
        setup(itemView);
    }

    public void setDetails(final MapMarker obj, final EntriesListAdapter.OnItemClickListener listener) {
        if (!isSet) {
            this.obj = obj;
            setMarker();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(obj);
                }
            });
        }
        else{
        }
    }

    public void setup(View view) {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(context, mMapView);
        mRotationGestureOverlay.setEnabled(true);

        mMapView.setTilesScaledToDpi(true);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMultiTouchControls(true);
        mMapView.setFlingEnabled(true);

        mMapView.setMinZoomLevel(3.0);
        mMapView.getController().zoomTo(14.0);

//        System.out.println("zoom: " + mMapView.getMinZoomLevel());
//        System.out.println("zoom: " + mMapView.getMaxZoomLevel());
//        System.out.println("zoom: " + mMapView.getZoomLevelDouble());
    }

    boolean markerSet = false;

    public Marker setMarker() {
        Log.e("MapViewHolder", "setMarker");
        mapMarker = new Marker(mMapView);
        GeoPoint geoPoint = new GeoPoint(obj.getLatitude(), obj.getLongitude());
        mapMarker.setPosition(geoPoint);
        Log.e("MapViewHolder", "latitude: " + geoPoint.getLatitude() + " longitude: " + geoPoint.getLongitude());


        mapMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
        Drawable icon = context.getResources().getDrawable(R.drawable.marker_poi_default);
        icon.setTint(context.getColor(R.color.red));
        mapMarker.setIcon(icon);

        mapMarker.setPanToView(true);
        mapMarker.setDraggable(true);

        String title = obj.getTitle();
        mapMarker.setTitle(title);
        String moreInfo = obj.getDescription();
        if (!moreInfo.matches("")) mapMarker.setSnippet(moreInfo);

        MarkerInfoWindow newInfoWindow = new MarkerInfoWindow(R.layout.map_marker_layout, mMapView);
        mapMarker.setInfoWindow(newInfoWindow);
        mMapView.getOverlays().add(mapMarker);
        mMapView.invalidate();

        mMapView.getController().animateTo(mapMarker.getPosition());

        btCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.getController().animateTo(mapMarker.getPosition());
            }
        });

        markerSet = true;
        return mapMarker;
    }
}