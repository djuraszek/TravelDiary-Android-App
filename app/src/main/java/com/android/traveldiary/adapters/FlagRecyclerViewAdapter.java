package com.android.traveldiary.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.util.List;

public class FlagRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Integer> flagList;
    private List<String> countriesList;
    private LayoutInflater inflater;
    private Context context;
    private ItemClickListener mClickListener;

    public static class FlagViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView flagIV;
        String country;
        Context context;

        public FlagViewHolder(View v) {
            super(v);

            flagIV = (ImageView)v.findViewById(R.id.flag_IV);
        }
        public void setDetails(int flagRes, String c,final Context baseContext){
            this.country = c;
            this.context = baseContext;
            flagIV.setImageResource(flagRes);
            flagIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SpannableString text = new SpannableString(country);
                    Balloon balloon = new Balloon.Builder(context)
                            .setArrowSize(10)
                            .setArrowOrientation(ArrowOrientation.TOP)
                            .setArrowVisible(true)
                            .setWidthRatio(1.0f)
                            .setHeight(65)
                            .setTextSize(15f)
                            .setArrowPosition(0.62f)
                            .setCornerRadius(4f)
                            .setText(country)
                            .setTextColor(ContextCompat.getColor(context, R.color.black))
                            .setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            .setBalloonAnimation(BalloonAnimation.FADE)
                            .build();
                }
            });
        }
    }

    public FlagRecyclerViewAdapter(Context context, List<Integer> flagList, List<String>countriesList) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.flagList = flagList;
        this.countriesList = countriesList;

    }

    @Override
    public FlagViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // create a new view
        View view = inflater.inflate(R.layout.flag_item_settings, viewGroup, false);
        return new FlagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FlagViewHolder)holder).setDetails(flagList.get(position), countriesList.get(position),context);
    }

    @Override
    public int getItemCount() {
        return flagList.size();
    }

    // convenience method for getting data at click position
    private int getItem(int id) {
        return flagList.get(id);
    }

    public String getCountry(int id) {
        return countriesList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
