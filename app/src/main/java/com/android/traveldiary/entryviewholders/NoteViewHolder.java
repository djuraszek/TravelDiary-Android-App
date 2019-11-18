package com.android.traveldiary.entryviewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.traveldiary.diaryentries.Note;
import com.android.traveldiary.R;
import com.android.traveldiary.adapters.EntriesListAdapter;

public class NoteViewHolder extends RecyclerView.ViewHolder{

    public TextView title, note;
    private boolean isSet=false;

    public NoteViewHolder(View itemView) {
        super(itemView);
        Log.e("NoteViewHolder","constructor");
        title = (TextView) itemView.findViewById(R.id.note_title);
        note = (TextView) itemView.findViewById(R.id.note_text);
    }

    public void setDetails(final Note obj, final EntriesListAdapter.OnItemClickListener listener){
        if (!isSet) {
            title.setText(obj.getTitle());
            if (obj.getTitle().matches("")) title.setVisibility(View.GONE);
            note.setText(obj.getNote());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(obj);
                }
            });
        }
    }


}