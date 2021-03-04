package com.android.traveldiary.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.EditActivity;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.classes.User;
import com.android.traveldiary.fragments.FragmentUser;
import com.android.traveldiary.serverrequests.DeleteLikePostRequest;
import com.android.traveldiary.serverrequests.DeleteTravelRequest;
import com.android.traveldiary.serverrequests.GetTravelRequest;
import com.android.traveldiary.serverrequests.GetUserRequest;
import com.android.traveldiary.serverrequests.PostLikePostRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class TravelListAdapter extends BaseAdapter implements PopupMenu.OnMenuItemClickListener {

    boolean showComments = true;
    boolean showLogs = true;
    private Travel travelObj;
    private Context context;
    private LayoutInflater inflater;
    private List<Travel> travelList;
    private String token;
    private String TAG = "TravelListAdapter";
    private boolean[] imageSet;
    private boolean[] isLiked;


    public TravelListAdapter(Context context, List<Travel> travelList) {
        if (showLogs) Log.e("TravelAdapter", "set Adapter of  travelList");
        this.context = context;
        this.travelList = travelList;
        imageSet = new boolean[travelList.size()];
        isLiked = new boolean[travelList.size()];
        if (showLogs) Log.v("ListAdapter", "travelList.size(): " + travelList.size());

        SharedPreferences preferences = context.getSharedPreferences("appPref", MODE_PRIVATE);
        token = preferences.getString("token", "");
        if (token.equals(""))
            Log.e(TAG, "---> TOKEN NOT SET");

    }

    public void updateTravelList(List<Travel> travelList) {
        this.travelList.clear();
        this.travelList.addAll(travelList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return travelList.size();
    }

    @Override
    public Object getItem(int position) {
        return travelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_item_trip, null);
//            Log.i("TravelListAdapter", "" + position);
        }

        final Travel travel = travelList.get(position);
        imageSet[position] = false;
        isLiked[position] = false;
//        if (!travel.isMyTravel()) Log.v("ListAdapter", "travelList.size(): " + travelList.size()
//                +" \nposition: "+position
//                +"\n "+travel.toString());

        TextView username = (TextView) row.findViewById(R.id.trip_item_username);
        username.setText(travel.getUsername());

        TextView travelName = (TextView) row.findViewById(R.id.trip_item_title);
        String name = travel.getTitle();
        travelName.setText(name);
//        travelName.setSelected(true);

        final TextView likeNumber = (TextView) row.findViewById(R.id.trip_item_likes);
        final int likes = travel.getLikesNumber();
        likeNumber.setText("" + travel.getLikesNumber());

        boolean likedPhoto = travel.isLiked();
//        liked[position] = travel.isLiked();

        final CheckBox likeButton = (CheckBox) row.findViewById(R.id.trip_item_like_btn);
        likeButton.setChecked(travel.isLiked());

        if (travel.isMyTravel()) {
            final ImageView more = (ImageView) row.findViewById(R.id.trip_item_settings);
            more.setVisibility(View.VISIBLE);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    travelObj = travel;
                    Log.e(TAG, "t1: " + travel);
                    Log.e(TAG, "t2: " + travelObj);
                    PopupMenu popup = new PopupMenu(context, view);
                    popup.setOnMenuItemClickListener(TravelListAdapter.this);
                    popup.inflate(R.menu.travel_item_edit_delete_menu);
                    popup.show();
                }
            });
        }

        if (imageSet[position] == false) {
            if (!travel.getImagePath().matches("") && !travel.getImagePath().matches("null")) {
//            Log.e(TAG, "photoUrl "+photoURL);
                String photoURL = travelList.get(position).getImagePath();
                ImageView albumImg = (ImageView) row.findViewById(R.id.trip_item_image);
                new TravelListAdapter.GetImageFromUrl(albumImg).execute(travel.getImagePath());
                imageSet[position] = true;
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.city);
                ImageView albumImg = (ImageView) row.findViewById(R.id.trip_item_image);
                albumImg.setImageBitmap(bitmap);
                imageSet[position] = true;
            }
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likeButton.isChecked()) {
                    likePost(travel);
                    travel.setLiked(true);
                    travel.setLikesNumber(travel.getLikesNumber() + 1);

                    likeNumber.setText("" + travel.getLikesNumber());
                    likeButton.setChecked(travel.isLiked());
                    likeButton.setEnabled(true);

                } else {
                    dislikePost(travel);

                    travel.setLiked(false);
                    travel.setLikesNumber(travel.getLikesNumber() - 1);
                    likeNumber.setText("" + travel.getLikesNumber());
                }
            }
        });

        //todo if clicked on the username -> go to profile

//        username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                findUser(travel.getUserID());
//            }
//        });
        return row;
    }

    public List<Travel> getTravelList(){
        return travelList;
    }

    private void likePost(final Travel travel) {

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        //todo refresh list
                        Log.i(TAG, "" + jsonResponse.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error: Could not like travel", Toast.LENGTH_SHORT).show();
                    travel.removeLike();
                    travel.setLiked(false);
                    notifyDataSetChanged();
                }
            }
        };

        PostLikePostRequest request = new PostLikePostRequest(token, travel.getTravelID(), listener, null);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void dislikePost(final Travel travel) {

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Log.i(TAG, "" + jsonResponse.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error: Could not unlike travel", Toast.LENGTH_SHORT).show();
                    travel.addLike();
                    travel.setLiked(true);
                    notifyDataSetChanged();
                }
            }
        };

        DeleteLikePostRequest request = new DeleteLikePostRequest(token, travel.getTravelID(), listener, null);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(context, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
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

    public void editTravel() {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("travel_id", travelObj.getTravelID());

        context.startActivity(intent);
    }


    private void showDeleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialogUnfollowTheme);
        alert.setTitle("Delete travel");
        alert.setMessage("Are you sure you want to delete " + travelObj.getTitle() + "?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
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

    private void deleteTravel() {
        Log.e(TAG, "delete travel: "+travelObj.toString());
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Log.i("TravelListAdapter.deleteTravel()","travel removed");
                        //todo refresh list
                        travelList.remove(travelObj);
                        notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Handle Error
                Log.d("EditActivity.addPhoto", "Error: " + error
                        + "\nStatus Code " + error.networkResponse.statusCode
                        + "\nResponse Data " + error.networkResponse.data
                        + "\nCause " + error.getCause()
                        + "\nmessage" + error.getMessage());

                Log.d("FragmentAdd", "Failed with error msg:\t" + error.getMessage());
                Log.d("FragmentAdd", "Error StackTrace: \t" + error.getStackTrace());
                // edited here
                try {
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    Log.e("FragmentAdd", new String(htmlBodyBytes), error);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (error.getMessage() == null) {
                    Log.d("FragmentAdd", "null");
                }
            }
        };
        DeleteTravelRequest request = new DeleteTravelRequest(token, travelObj.getTravelID(), responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    /**
     * ----------------------------------------------------------------------------------------
     * GET IMAGE CLASS
     * ----------------------------------------------------------------------------------------
     */

    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap bitmap;

        public GetImageFromUrl(ImageView img) {
            this.imageView = img;
            this.bitmap = bitmap;
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

//
//    @SuppressLint("ResourceAsColor")
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        ViewHolder holder = new ViewHolder();
//
//        if (convertView == null) {
//            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.listview_item_trip, null);
//            holder.itemImg = (ImageView) convertView.findViewById(R.id.trip_item_image);;
//            holder.username = (TextView) convertView.findViewById(R.id.trip_item_username);
//            holder.travelName = (TextView) convertView.findViewById(R.id.trip_item_title);;
//            holder.likeNumber = (TextView) convertView.findViewById(R.id.trip_item_likes);
//            holder.likeButton = (CheckBox) convertView.findViewById(R.id.trip_item_like_btn);
//
//            convertView.setTag(holder);
////            Log.i("TravelListAdapter", "" + position);
//        }else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        final Travel travel = travelList.get(position);
//        holder.travel = travelList.get(position);
//
//        imageSet[position] = false;
//        isLiked[position] = false;
////        if (!travel.isMyTravel()) Log.v("ListAdapter", "travelList.size(): " + travelList.size()
////                +" \nposition: "+position
////                +"\n "+travel.toString());
//
//        holder.username.setText(travel.getUsername());
//
//        String name = travel.getTitle();
//        holder.travelName.setText(name);
//
//        final int likes = travel.getLikesNumber();
//        holder.likeNumber.setText("" + travel.getLikesNumber());
//
//        boolean likedPhoto = travel.isLiked();
////        liked[position] = travel.isLiked();
//
//        holder.likeButton.setChecked(travel.isLiked());
//
//        if (travel.isMyTravel()) {
//            final ImageView more = (ImageView) convertView.findViewById(R.id.trip_item_settings);
//            more.setVisibility(View.VISIBLE);
//            more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    travelObj = travel;
//                    Log.e(TAG, "t1: " + travel);
//                    Log.e(TAG, "t2: " + travelObj);
//                    PopupMenu popup = new PopupMenu(context, view);
//                    popup.setOnMenuItemClickListener(TravelListAdapter.this);
//                    popup.inflate(R.menu.travel_item_edit_delete_menu);
//                    popup.show();
//                }
//            });
//        }
//
//        if (holder.bitmap == null) {
//            String url = travel.getImagePath();
//            holder.setImage(url);
//        }
//
//        holder.setLikeButton();
//
////        holder.likeButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                if (holder.likeButton.isChecked()) {
////                    likePost(travel, position);
////
////                    travel.setLiked(true);
////                    travel.setLikesNumber(travel.getLikesNumber() + 1);
////
////                    holder.likeNumber.setText("" + travel.getLikesNumber());
////                    holder.likeButton.setChecked(travel.isLiked());
////                    holder.likeButton.setEnabled(true);
////
////                } else {
////                    dislikePost(travel, position);
////
////                    travel.setLiked(false);
////                    travel.setLikesNumber(travel.getLikesNumber() - 1);
////                    holder.likeNumber.setText("" + travel.getLikesNumber());
////                }
////            }
////        });
//
//        //todo if clicked on the username -> go to profile
//
////        username.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                findUser(travel.getUserID());
////            }
////        });
//        return convertView;
//    }
//
//public class ViewHolder{
//    Travel travel;
//    TextView itemTitle;
//    TextView itemPubDate;
//    ImageView itemImg;
//    String imageUrl;
//    TextView likeNumber;
//    CheckBox likeButton;
//    TextView username;
//    TextView travelName;
//    Bitmap bitmap;
//
//    public void setLikeButton(){
//        likeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (likeButton.isChecked()) {
//                    likePost(travel);
//
//                    travel.setLiked(true);
//                    travel.setLikesNumber(travel.getLikesNumber() + 1);
//
//                    likeNumber.setText("" + travel.getLikesNumber());
//                    likeButton.setChecked(travel.isLiked());
//                    likeButton.setEnabled(true);
//
//                } else {
//                    dislikePost(travel);
//
//                    travel.setLiked(false);
//                    travel.setLikesNumber(travel.getLikesNumber() - 1);
//                    likeNumber.setText("" + travel.getLikesNumber());
//                }
//            }
//        });
//    }
//
//    public void setImage(String url) {
//
//        if (!travel.getImagePath().matches("") && !travel.getImagePath().matches("null")) {
////            Log.e(TAG, "photoUrl "+photoURL);
////                String photoURL = travel.getImagePath();
//            String photoURL = url;
////                ImageView albumImg = (ImageView) convertView.findViewById(R.id.trip_item_image);
//            TravelListAdapter.GetImageFromUrl im = new TravelListAdapter.GetImageFromUrl(itemImg);
//            im.execute(travel.getImagePath());
//            bitmap = im.bitmap;
////                imageSet[position] = true;
//        } else {
//            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.city);
////                ImageView albumImg = (ImageView) convertView.findViewById(R.id.trip_item_image);
//            itemImg.setImageBitmap(bitmap);
////                imageSet[position] = true;
//        }
//    }
//
//
//}
