package com.android.traveldiary.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.traveldiary.R;
import com.android.traveldiary.classes.User;

import java.util.ArrayList;

public class SearchResultsAdapter extends BaseAdapter
{
    private LayoutInflater layoutInflater;

    private ArrayList<User> userDetails =new ArrayList<>();
    int count;
    Typeface type;
    Context context;

    //constructor method
    public SearchResultsAdapter(Context context, ArrayList<User> user_details) {
        Log.i("SearchResultsAdapter","users "+user_details.size());
        layoutInflater = LayoutInflater.from(context);

        this.userDetails=user_details;
        this.count= user_details.size();
        this.context = context;

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.e("SearchResultsAdapter","NotifyDataSetCHanged()");
    }

    public void addAll(ArrayList<User> newList) {
        Log.v("addAll",""+newList.toString());
        userDetails.clear();
        userDetails.addAll(newList);
//        userDetails = newList;
//        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userDetails.size();
    }

    @Override
    public Object getItem(int arg0) {
        return userDetails.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(userDetails.size()>0) {
            SearchResultsAdapter.ViewHolder holder;
            User tempUser = userDetails.get(position);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.listview_item_search_profile, null);
                holder = new SearchResultsAdapter.ViewHolder();
                holder.user_name = (TextView) convertView.findViewById(R.id.search_item_name);
                holder.user_username = (TextView) convertView.findViewById(R.id.search_item_username);
                holder.user_photo = (TextView) convertView.findViewById(R.id.search_item_photo);

                convertView.setTag(holder);
            } else {
                holder = (SearchResultsAdapter.ViewHolder) convertView.getTag();
            }

            holder.user_name.setText(tempUser.getName());
            holder.user_name.setTypeface(type);

            holder.user_username.setText(tempUser.getUsername());
            holder.user_username.setTypeface(type);

            holder.user_photo.setText(tempUser.getNameInitials());
            holder.user_photo.setTypeface(type);

            return convertView;
        }
        return null;
    }

    static class ViewHolder
    {
        TextView user_name;
        TextView user_username;
        TextView user_photo;
    }

}
