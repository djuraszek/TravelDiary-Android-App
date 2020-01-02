package com.android.traveldiary.entryviewholders;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.R;
import com.android.traveldiary.adapters.EntriesListAdapter;

import java.io.File;
import java.io.FileInputStream;

public class PhotoViewHolder extends RecyclerView.ViewHolder{

    private LinearLayout title_layout;
    public ImageView photo;
    public TextView title;
    private boolean isSet=false;
    public TextView buttonViewOption;

    public PhotoViewHolder(View itemView, Context context) {
        super(itemView);
        Log.e("PhotoViewHolder","constructor");
        title = (TextView) itemView.findViewById(R.id.title);
        photo = (ImageView) itemView.findViewById(R.id.photo);
        if(context.getResources().getBoolean(R.bool.isTablet)) {
            photo.getLayoutParams().height = 300;
            photo.requestLayout();
        }
        title_layout = (LinearLayout) itemView.findViewById(R.id.title_layout);
    }

    public void setDetails(final Photo obj,final EntriesListAdapter.OnItemClickListener listener){
        if(!isSet) {
            if (obj.getTitle().matches("")) title_layout.setVisibility(View.GONE);
            else title.setText(obj.getTitle());
            //todo set photo from file
            Bitmap photoBitmap = getBitmap(obj.getPhotoPath());
            photo.setImageBitmap(photoBitmap);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(obj);
                }
            });
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