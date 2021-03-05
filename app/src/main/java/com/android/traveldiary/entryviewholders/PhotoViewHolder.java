package com.android.traveldiary.entryviewholders;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.traveldiary.activites.TravelActivity;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.R;
import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.serverrequests.DeleteNoteRequest;
import com.android.traveldiary.serverrequests.DeletePhotoRequest;
import com.android.traveldiary.traveladds.AddNoteActivity;
import com.android.traveldiary.traveladds.EditPhotoActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    private LinearLayout title_layout;
    public ImageView photo;
    public TextView title;
    private boolean isSet = false;
    public TextView buttonViewOption;
    private Context context;
    private Photo photoObj;
    private String token;
    TravelActivity activity;

    public PhotoViewHolder(View itemView, Context context, TravelActivity activity) {
        super(itemView);
        itemView.setTag("photo");
        Log.e("PhotoViewHolder", "constructor");
        title = (TextView) itemView.findViewById(R.id.title);
        photo = (ImageView) itemView.findViewById(R.id.photo);
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            photo.getLayoutParams().height = 300;
            photo.requestLayout();
        }
        title_layout = (LinearLayout) itemView.findViewById(R.id.title_layout);
        this.context = context;
        this.activity = activity;
    }

    public void setDetails(final Photo obj, final EntriesListAdapter.OnItemClickListener listener, String token, boolean isMyTravel) {
        if (!isSet) {
            this.token = token;
            photoObj = obj;
            if (obj.getTitle().matches("") || obj.getTitle().matches("null"))
                title_layout.setVisibility(View.GONE);
            else title.setText(obj.getTitle());
            //todo set photo from file
            String photoURL = obj.getPhotoPath();

            new PhotoViewHolder.GetImageFromUrl(photo).execute(photoURL);
//            Bitmap photoBitmap = getBitmap(obj.getPhotoPath());
//            photo.setImageBitmap(photoBitmap);

            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);

            if (isMyTravel)
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                    listener.onItemClick(obj);
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.setOnMenuItemClickListener(PhotoViewHolder.this);
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
                editPhoto();
                return true;
            case R.id.delete:
//                showDeleteMessage();
                return true;
            default:
                return false;
        }
    }

    private void editPhoto() {
        Intent intent = new Intent(context, EditPhotoActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("travelID", photoObj.getTravelID());
        intent.putExtra("photoID", photoObj.getID());
        intent.putExtra("photoPath", photoObj.getPhotoPath());
        intent.putExtra("title", photoObj.getTitle());
        intent.putExtra("date", photoObj.getDate());

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void showDeleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity, R.style.AlertDialogUnfollowTheme);
        alert.setTitle("Delete photo");
        alert.setMessage("Are you sure you want to delete photo ?");
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
                    activity.remove(photoObj);
                    Log.i("PhotoViewHolder.DELETE", "resp: " + jsonResponse.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DeletePhotoRequest request = new DeletePhotoRequest(token, photoObj.getID(), responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap bitmap;

        public GetImageFromUrl(ImageView img) {
            this.imageView = img;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String stringUrl = url[0];
            bitmap = null;
            InputStream inputStream;
            try {

                inputStream = new java.net.URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
