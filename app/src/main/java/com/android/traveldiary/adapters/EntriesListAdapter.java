package com.android.traveldiary.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import com.android.traveldiary.activites.EditActivity;
import com.android.traveldiary.diaryentries.DiaryEntry;
import com.android.traveldiary.diaryentries.MapMarker;
import com.android.traveldiary.diaryentries.Note;
import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.diaryentries.Transport;
import com.android.traveldiary.diaryentries.VoiceNote;
import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.entryviewholders.MapViewHolder;
import com.android.traveldiary.entryviewholders.NoteViewHolder;
import com.android.traveldiary.entryviewholders.PhotoViewHolder;
import com.android.traveldiary.entryviewholders.TransportViewHolder;
import com.android.traveldiary.entryviewholders.VoiceNoteViewHolder;
//import com.android.traveldiary.entryViewHolders.MapViewHolder;
//import com.android.traveldiary.entryViewHolders.NoteViewHolder;
//import com.android.traveldiary.entryViewHolders.PhotoViewHolder;
//import com.android.traveldiary.entryViewHolders.TransportViewHolder;
//import com.android.traveldiary.entryViewHolders.VoiceNoteViewHolder;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class EntriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(DiaryEntry item);
    }

    Activity travelActivity;
    List<DiaryEntry> entries;
    Context context;
    private LayoutInflater inflater;

    private final OnItemClickListener itemClickListener;

    public EntriesListAdapter(Context context, List<DiaryEntry> entriesList, Activity travelActivity, OnItemClickListener listener){
        Log.v("EntriesListAdapter","Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.entries = entriesList;
        this.itemClickListener = listener;
        this.travelActivity = travelActivity;
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
            setOnLongPressMenu(view);
            return new NoteViewHolder(view,context);

        } else if (viewType == Consts.ENTRY_TYPE_VOICE_NOTE_INT){ // for voice note layout
            view = inflater.inflate(R.layout.travel_voice_note, viewGroup, false);
            setOnLongPressMenu(view);
            return new VoiceNoteViewHolder(view, travelActivity);
        }
        else if (viewType == Consts.ENTRY_TYPE_TRANSPORT_INT){ // for transport layout
            view = inflater.inflate(R.layout.travel_transport, viewGroup, false);
            setOnLongPressMenu(view);
            return new TransportViewHolder(view,context);
        }
        else if (viewType == Consts.ENTRY_TYPE_MAP_COORDS_INT){ // for map layout
            view = inflater.inflate(R.layout.travel_map_coords, viewGroup, false);
            setOnLongPressMenu(view);
            return new MapViewHolder(view,context);
        }
        else { // for photo layout
            view = inflater.inflate(R.layout.travel_photo, viewGroup, false);
            setOnLongPressMenu(view);
            return new PhotoViewHolder(view,context);
        }

    }

    private void setOnLongPressMenu(View view){

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
//        Log.v("EntriesListAdapter","onBindViewHolder");
        if (getItemViewType(position) == Consts.ENTRY_TYPE_TRANSPORT_INT) {
            ((TransportViewHolder)viewHolder).setDetails((Transport)entries.get(position),itemClickListener);
        }
        else if (getItemViewType(position) == Consts.ENTRY_TYPE_VOICE_NOTE_INT) {
            ((VoiceNoteViewHolder)viewHolder).setDetails((VoiceNote) entries.get(position),itemClickListener);
        }
        else if (getItemViewType(position) == Consts.ENTRY_TYPE_PHOTO_INT) {
            ((PhotoViewHolder)viewHolder).setDetails((Photo) entries.get(position),itemClickListener);
        }
        else if (getItemViewType(position) == Consts.ENTRY_TYPE_NOTE_INT) {
            ((NoteViewHolder)viewHolder).setDetails((Note) entries.get(position),itemClickListener);
        }
        else if (getItemViewType(position) == Consts.ENTRY_TYPE_MAP_COORDS_INT) {
            ((MapViewHolder)viewHolder).setDetails((MapMarker)entries.get(position),itemClickListener);
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

        } else if (entries.get(position).getEntryType().matches(Consts.ENTRY_TYPE_TRANSPORT)) {
            return Consts.ENTRY_TYPE_TRANSPORT_INT;

        }else if (entries.get(position).getEntryType().matches(Consts.ENTRY_TYPE_VOICE_NOTE)) {
            return Consts.ENTRY_TYPE_VOICE_NOTE_INT;

        }else if (entries.get(position).getEntryType().matches(Consts.ENTRY_TYPE_MAP_MARKER)) {
            return Consts.ENTRY_TYPE_MAP_COORDS_INT;
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
