package com.android.traveldiary.entryviewholders;

import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.traveldiary.diaryentries.Transport;
import com.android.traveldiary.R;
import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.database.Consts;

public class TransportViewHolder extends RecyclerView.ViewHolder{
    String TAG = "TransportViewHolder";
    public int[] transportIcons = {R.drawable.ic_transport_bike,R.drawable.ic_transport_boat,
            R.drawable.ic_transport_car, R.drawable.ic_transport_motorcycle, R.drawable.ic_transport_plane,
            R.drawable.ic_transport_train};


    public ImageView icon;
    public TextView departure_place,departure_time,arrival_place,arrival_time;
    private boolean isSet=false;
    public TextView buttonViewOption;
    Context context;

    public TransportViewHolder(View itemView, Context context) {
        super(itemView);
        Log.e("TransportViewHolder","constructor");
        icon = (ImageView) itemView.findViewById(R.id.transport_icon);
        departure_place = (TextView) itemView.findViewById(R.id.departure_place);
        departure_time = (TextView) itemView.findViewById(R.id.departure_time);
        arrival_place = (TextView) itemView.findViewById(R.id.arrival_place);
        arrival_time = (TextView) itemView.findViewById(R.id.arrival_time);
        buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        this.context = context;
    }

    public void setDetails(final Transport transport, final EntriesListAdapter.OnItemClickListener listener){
        if(!isSet) {
            departure_place.setText(transport.getDeparturePlace());
            departure_time.setText(transport.getDepartureTime());
            arrival_place.setText(transport.getArrivalPlace());
            arrival_time.setText(transport.getArrivalTime());
            int iconInt = getIcon(transport.getTransportType());
            if (iconInt != 0) icon.setImageResource(transportIcons[iconInt]);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(transport);
                }
            });
        }
    }

    public int getIcon(String transportType){
        Log.v(TAG, "transportType "+transportType);
        switch (transportType) {
            case Consts.TRANSPORT_TYPE_BIKE:
                return 0;
            case Consts.TRANSPORT_TYPE_BOAT:
                return 1;
            case Consts.TRANSPORT_TYPE_CAR:
                return 2;
            case Consts.TRANSPORT_TYPE_MOTORCYCLE:
                return 3;
            case Consts.TRANSPORT_TYPE_PLANE:
                return 4;
            case Consts.TRANSPORT_TYPE_TRAIN:
                return 5;
            default:
                return -1;
        }
    }
}
