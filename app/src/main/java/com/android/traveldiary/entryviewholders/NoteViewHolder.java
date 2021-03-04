package com.android.traveldiary.entryviewholders;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.adapters.TravelListAdapter;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.diaryentries.Note;
import com.android.traveldiary.R;
import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.serverrequests.DeleteNoteRequest;
import com.android.traveldiary.traveladds.AddNoteActivity;
import com.android.traveldiary.traveladds.EditNoteActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NoteViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    public TextView title, note;
    private boolean isSet = false;
    public TextView buttonViewOption;
    private Context context;
    private Note noteObj;
    private String token;
    private RelativeLayout titleLayout;

    private TravelActivity activity;

    public NoteViewHolder(View itemView, Context context, TravelActivity activity) {
        super(itemView);
        itemView.setTag("note");
        Log.e("NoteViewHolder", "constructor");
        title = (TextView) itemView.findViewById(R.id.note_title);
        note = (TextView) itemView.findViewById(R.id.note_text);
        buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        titleLayout = (RelativeLayout)itemView.findViewById(R.id.note_title_layout);
        this.context = context;
        this.activity = activity;
    }

    public void setDetails(final Note obj, final EntriesListAdapter.OnItemClickListener listener, String token, boolean isMyTravel) {
        if (!isSet) {
            this.token = token;
            noteObj = obj;
            title.setText(obj.getTitle());
            if (obj.getTitle().matches("") || obj.getTitle().matches("null"))
                titleLayout.setVisibility(View.GONE);

            note.setText(obj.getNote());

            if (isMyTravel)
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
//                    listener.onItemClick(obj);
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.setOnMenuItemClickListener(NoteViewHolder.this);
                        popup.inflate(R.menu.travel_item_edit_delete_menu);
                        popup.show();
                        return false;
                    }
                });
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(context, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.edit:
                // do your code
                editNote();
                return true;
            case R.id.delete:
                showDeleteMessage();
                return true;
            default:
                return false;
        }
    }

    private void editNote() {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("travelID", noteObj.getTravelID());
        intent.putExtra("noteID", noteObj.getID());
        intent.putExtra("title", noteObj.getTitle());
        intent.putExtra("note", noteObj.getNote());
        intent.putExtra("date", noteObj.getDate());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void showDeleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity, R.style.AlertDialogUnfollowTheme);
        alert.setTitle("Delete Note");
        alert.setMessage("Are you sure you want to delete note?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteNote();
            }
        });
        alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        alert.show();
    }

    private void deleteNote() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.i("NoteViewHolder.DELETE", "resp: " + jsonResponse.toString());
                    activity.remove(noteObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DeleteNoteRequest request = new DeleteNoteRequest(token, noteObj.getID(), responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

}