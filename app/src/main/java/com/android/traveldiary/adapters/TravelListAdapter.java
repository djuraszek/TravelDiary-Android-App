package com.android.traveldiary.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.traveldiary.R;
import com.android.traveldiary.classes.Travel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class TravelListAdapter extends BaseAdapter {

    boolean showComments = false;
    boolean showLogs = false;
    private Travel travel;
    private Context context;
    private LayoutInflater inflater;
    private List<Travel> travelList;
//    private List<Bitmap> imageList;
    private List<Boolean> editModeList;

    public TravelListAdapter(Context context, List<Travel> travelList) {
        if(showLogs)Log.e("SeansListAdapter", "set Adapter of  travelList");
        this.context = context;
        this.travelList = travelList;
//        this.imageList = new ArrayList<>();
        this.editModeList = new ArrayList<>();

        for (int i = 0; i < travelList.size(); i++) {
//            imageList.add(travelList.get(i).getImageBitmap());
            editModeList.add(false);
        }

        if (showLogs) Log.v("ListAdapter", "travelList.size(): " + travelList.size());
    }

    public void updateTravelList(List<Travel> travelList){
        this.travelList.clear();
        this.travelList.addAll(travelList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return travelList.size();
    }

    @Override
    public Object getItem(int position) {
        return travelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        final ViewGroup parentGroup = parent;
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            listView = inflater.inflate(R.layout.listview_item_trip, null);
        }
        travel = travelList.get(position);

        TextView travelName = (TextView) listView.findViewById(R.id.trip_item_title);
        String name = travel.getTitle();
        travelName.setText(name);
        travelName.setSelected(true);

        TextView dateFrom = (TextView) listView.findViewById(R.id.trip_item_date_from);
        String startDate = "" + travel.getStartDate();
        dateFrom.setText(startDate);

        TextView dateTo = (TextView) listView.findViewById(R.id.trip_item_date_to);
        String endDate = "" + travel.getEndDate();
        dateTo.setText(endDate);


        if (startDate.matches(endDate)) {
            dateTo.setVisibility(View.GONE);
            TextView dash = (TextView) listView.findViewById(R.id.item_date_dash);
            dash.setVisibility(View.GONE);
        }

        final LinearLayout buttonsLayout = (LinearLayout) listView.findViewById(R.id.option_buttons_layout);
        ImageButton editButton = (ImageButton) listView.findViewById(R.id.edit_button);
        ImageButton deleteButton = (ImageButton) listView.findViewById(R.id.delete_button);

        if (!travel.getImagePath().matches("")
                && !travel.getImagePath().matches("null")) {
            Bitmap photoBitmap = getBitmap(travel.getImagePath());

            ImageView albumImg = (ImageView) listView.findViewById(R.id.trip_item_image);
            albumImg.setImageBitmap(photoBitmap);
            albumImg.setMaxHeight(150);
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.city);
            ImageView albumImg = (ImageView) listView.findViewById(R.id.trip_item_image);
            albumImg.setImageBitmap(bitmap);
            albumImg.setMaxHeight(150);
        }

        return listView;
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

    public List<Travel> getTravelList() {
        return travelList;
    }

    public List<Boolean> getEditModeList() {
        return editModeList;
    }
}
