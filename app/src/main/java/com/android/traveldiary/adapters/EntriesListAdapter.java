package com.android.traveldiary.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.diaryentries.DiaryEntry;
import com.android.traveldiary.diaryentries.Note;
import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.entryviewholders.NoteViewHolder;
import com.android.traveldiary.entryviewholders.PhotoViewHolder;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class EntriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(DiaryEntry item);
    }

    TravelActivity travelActivity;
    List<DiaryEntry> entries;
    Context context;
    private LayoutInflater inflater;
    String token;
    boolean isMyTravel = false;

    private final OnItemClickListener itemClickListener;

    public EntriesListAdapter(Context context, List<DiaryEntry> entriesList, TravelActivity travelActivity, OnItemClickListener listener, String token, boolean isMyTravel){
        Log.v("EntriesListAdapter","Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.entries = entriesList;
        this.itemClickListener = listener;
        this.travelActivity = travelActivity;
        this.token = token;
        this.isMyTravel = isMyTravel;
    }

    public void updateEntries(List<DiaryEntry> newList){
        entries.clear();
        entries.addAll(newList);
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        if (viewType == Consts.ENTRY_TYPE_NOTE_INT) { // for note layout
            view = inflater.inflate(R.layout.travel_note, viewGroup, false);
            return new NoteViewHolder(view, context,travelActivity);
        }
        else { // for photo layout
            view = inflater.inflate(R.layout.travel_photo, viewGroup, false);
            return new PhotoViewHolder(view,context, travelActivity);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
//        Log.v("EntriesListAdapter","onBindViewHolder");
        if (getItemViewType(position) == Consts.ENTRY_TYPE_PHOTO_INT) {
            ((PhotoViewHolder)viewHolder).setDetails((Photo) entries.get(position),itemClickListener,token,isMyTravel);
        }
        else if (getItemViewType(position) == Consts.ENTRY_TYPE_NOTE_INT) {
            ((NoteViewHolder)viewHolder).setDetails((Note) entries.get(position),itemClickListener,token,isMyTravel);
        }
    }

    @Override
    public int getItemCount() {
//        Log.v("EntriesListAdapter","getItemCount "+entries.size());
        return entries.size();
    }

    @Override
    public int getItemViewType(int position) {
//        Log.v("EntriesListAdapter","getItemViewType");
        if (entries.get(position).getEntryType().matches(Consts.ENTRY_TYPE_NOTE)) {
            return Consts.ENTRY_TYPE_NOTE_INT;

        }else if (entries.get(position).getEntryType().matches(Consts.ENTRY_TYPE_PHOTO)) {
            return Consts.ENTRY_TYPE_PHOTO_INT;
        }
        else {
            return -1;
        }
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


}
