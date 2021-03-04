package com.android.traveldiary.entryviewholders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.EditActivity;
import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.adapters.TravelListAdapter;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.serverrequests.DeleteTravelRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TravelViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener{

    public TextView username, travelName, likeNumber;
    final CheckBox likeButton;
    final ImageView more;
    ImageView albumImg;

    private boolean isSet=false;
    private Context context;
    Travel travel;

    public TravelViewHolder(View itemView, Context context) {
        super(itemView);
        username = (TextView) itemView.findViewById(R.id.trip_item_username);
        travelName = (TextView) itemView.findViewById(R.id.trip_item_title);
        likeNumber = (TextView) itemView.findViewById(R.id.trip_item_likes);
        more = (ImageView) itemView.findViewById(R.id.trip_item_settings);
        likeButton = (CheckBox) itemView.findViewById(R.id.trip_item_like_btn);
        albumImg = (ImageView) itemView.findViewById(R.id.trip_item_image);
        this.context = context;
    }

    public void setDetails(final Travel travel, final EntriesListAdapter.OnItemClickListener listener){
        if (!isSet) {
            this.travel = travel;
            username.setText(travel.getUsername());
            travelName.setText(travel.getTitle());
            final int likes = travel.getLikesNumber();
            likeNumber.setText("" + travel.getLikesNumber());
            likeButton.setChecked(travel.isLiked());

            if(travel.isMyTravel()) {
                more.setVisibility(View.VISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.setOnMenuItemClickListener(TravelViewHolder.this);
                        popup.inflate(R.menu.travel_item_edit_delete_menu);
                        popup.show();
                    }
                });
            }


//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        listener.onItemClick(travel);
//                    }
//                });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (likeButton.isChecked()) {
                        //todo server request
//                    likePost(travel, position);
                        //todo update the number of likes
                        travel.setLiked(true);
                        travel.setLikesNumber(travel.getLikesNumber() + 1);
                        likeNumber.setText("" + travel.getLikesNumber());
                        likeButton.setChecked(travel.isLiked());
//                    likeButton.setEnabled(true);
                    } else {
//                    dislikePost(travel, position);
                        //todo update the number of likes
                        travel.setLiked(false);
                        travel.setLikesNumber(travel.getLikesNumber() - 1);
                        likeNumber.setText("" + travel.getLikesNumber());
                    }
                }
            });
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.edit:
                // do your code
                editTravel();
                return true;
            case R.id.delete:
                showDeleteMessage();
                return true;
            default:
                return false;
        }
    }


    public void editTravel(){
        Intent intent = new Intent(context, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("travel_id",travel.getTravelID());

        context.startActivity(intent,bundle);
    }


    private void showDeleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialogUnfollowTheme);
        alert.setTitle("Delete travel");
        alert.setMessage("Are you sure you want to delete " + travel.getTitle()+ "?");
        alert.setPositiveButton("Unfollow", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                //todo unfollow
                deleteTravel();
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

    private void deleteTravel(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        //todo refresh list
//                        travelList.remove(travel);
//                        notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
//        DeleteTravelRequest request = new DeleteTravelRequest(token,travel.getTravelID(),responseListener,null);
//        RequestQueue queue = Volley.newRequestQueue(context);
//        queue.add(request);
    }
}