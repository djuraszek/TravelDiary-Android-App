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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.traveldiary.DiaryLogs.DiaryEntry;
import com.android.traveldiary.DiaryLogs.MapMarker;
import com.android.traveldiary.DiaryLogs.Note;
import com.android.traveldiary.DiaryLogs.Photo;
import com.android.traveldiary.DiaryLogs.Transport;
import com.android.traveldiary.DiaryLogs.VoiceNote;
import com.android.traveldiary.R;
import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.entryViewHolders.MapViewHolder;
import com.android.traveldiary.entryViewHolders.NoteViewHolder;
import com.android.traveldiary.entryViewHolders.PhotoViewHolder;
import com.android.traveldiary.entryViewHolders.TransportViewHolder;
import com.android.traveldiary.entryViewHolders.VoiceNoteViewHolder;
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

    public EntriesListAdapter(Context context, List<DiaryEntry> entriesList, Activity travelActivity,OnItemClickListener listener){
        Log.v("EntriesListAdapter","Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.entries = entriesList;
        this.itemClickListener = listener;
        this.travelActivity = travelActivity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        if (viewType == Consts.ENTRY_TYPE_NOTE_INT) { // for note layout
            view = inflater.inflate(R.layout.travel_note, viewGroup, false);
            return new NoteViewHolder(view);

        } else if (viewType == Consts.ENTRY_TYPE_VOICE_NOTE_INT){ // for voice note layout
            view = inflater.inflate(R.layout.travel_voice_note, viewGroup, false);
            return new VoiceNoteViewHolder(view, travelActivity);
        }
        else if (viewType == Consts.ENTRY_TYPE_TRANSPORT_INT){ // for transport layout
            view = inflater.inflate(R.layout.travel_transport, viewGroup, false);
            return new TransportViewHolder(view);
        }
        else if (viewType == Consts.ENTRY_TYPE_MAP_COORDS_INT){ // for map layout
            view = inflater.inflate(R.layout.travel_map_coords, viewGroup, false);
            return new MapViewHolder(view,context);
        }
        else { // for photo layout
            view = inflater.inflate(R.layout.travel_photo, viewGroup, false);
            return new PhotoViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
//        Log.v("EntriesListAdapter","onBindViewHolder");
        if (getItemViewType(position) == Consts.ENTRY_TYPE_TRANSPORT_INT) {
            ((TransportViewHolder)viewHolder).setDetails((Transport)entries.get(position),itemClickListener);
        }
        else if (getItemViewType(position) == Consts.ENTRY_TYPE_NOTE_INT) {
            ((NoteViewHolder)viewHolder).setDetails((Note) entries.get(position),itemClickListener);
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
            ((MapViewHolder)viewHolder).setDetails((MapMarker) entries.get(position),itemClickListener);
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
