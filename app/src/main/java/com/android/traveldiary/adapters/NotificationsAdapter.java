package com.android.traveldiary.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.traveldiary.R;
import com.android.traveldiary.classes.Image;
import com.android.traveldiary.classes.Notification;
import com.android.traveldiary.classes.User;

import java.util.ArrayList;

public class NotificationsAdapter extends BaseAdapter
{
    private LayoutInflater layoutInflater;
    private String TAG = "NotificationsAdapter";

    private ArrayList<Notification> notifications =new ArrayList<>();
    int count;
    Typeface type;
    Context context;

    //constructor method
    public NotificationsAdapter(Context context, ArrayList<Notification> notifications) {
        Log.i("SearchResultsAdapter","users "+notifications.size());
        layoutInflater = LayoutInflater.from(context);

        this.notifications=notifications;
        this.count= notifications.size();
        this.context = context;

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.e("SearchResultsAdapter","NotifyDataSetCHanged()");
    }

    public void addAll(ArrayList<Notification> newList) {
        Log.v("addAll",""+newList.toString());
        notifications.clear();
        notifications.addAll(newList);
//        userDetails = newList;
//        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int arg0) {
        return notifications.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(notifications.size()>0) {
            NotificationsAdapter.ViewHolder holder;
            Notification temp = notifications.get(position);

//            Log.i(TAG, "converVierw == null -> "+(convertView == null));
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.listview_item_notifications, null);
                holder = new NotificationsAdapter.ViewHolder();
                holder.body = (TextView) convertView.findViewById(R.id.notifications_body);
                holder.user_photo = (ImageView) convertView.findViewById(R.id.notifications_photo);

                convertView.setTag(holder);
            } else {
                holder = (NotificationsAdapter.ViewHolder) convertView.getTag();
            }

            holder.body.setText(temp.getBody());
            holder.body.setTypeface(type);

            if(temp.getTravelID()>0){
                holder.user_photo.setImageResource(R.drawable.ic_no_photo);
            }else{
                holder.user_photo.setImageResource(R.drawable.ic_people);
            }

            return convertView;
        }
        return null;
    }

    static class ViewHolder
    {
        TextView body;
        ImageView user_photo;
    }

}
