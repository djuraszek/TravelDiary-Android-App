package com.android.traveldiary.entryviewholders;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.traveldiary.diaryentries.VoiceNote;
import com.android.traveldiary.R;
import com.android.traveldiary.adapters.EntriesListAdapter;
import com.android.traveldiary.database.Consts;

import java.io.IOException;

public class VoiceNoteViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    public TextView buttonViewOption;

    private TextView title, seekbarTimeTV, seekbarMaxTimeTV;
    ;
    private ImageView play_button, stop_button;
    private SeekBar mSeekBar;
    private boolean isSet = false, isPlaying = false;

    private String filePath;
    private MediaPlayer player = null;
    private Handler mHandler = new Handler();

    public VoiceNoteViewHolder(View itemView, Activity activity) {
        super(itemView);
        Log.e("VoiceNoteViewHolder", "constructor");
        title = (TextView) itemView.findViewById(R.id.record_title);
        mSeekBar = (SeekBar) itemView.findViewById(R.id.seek_bar);
        seekbarTimeTV = (TextView) itemView.findViewById(R.id.seek_bar_time);
        seekbarMaxTimeTV = (TextView) itemView.findViewById(R.id.seek_bar_max_time);
        play_button = (ImageView) itemView.findViewById(R.id.play_button);
        stop_button = (ImageView) itemView.findViewById(R.id.stop_button);
        buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        this.activity = activity;
    }

    public void setDetails(final VoiceNote obj, final EntriesListAdapter.OnItemClickListener listener) {
        if (!isSet) {
            filePath = obj.getRecordURI();

            title.setText(obj.getTitle());
            if (obj.getTitle().matches(""))
                title.setVisibility(View.GONE);


            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPlaying = true;
                    startPlaying();
                    play_button.setVisibility(View.GONE);
                    stop_button.setVisibility(View.VISIBLE);
                }
            });
            stop_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopPlaying();
                    isPlaying = false;
                    play_button.setVisibility(View.VISIBLE);
                    stop_button.setVisibility(View.GONE);
                    seekbarTimeTV.setText("0:00");
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(obj);
                }
            });

            seekbarTimeTV.setText("0:00");
            preparePlaying();
        }
    }

    private void preparePlaying() {
        isPlaying = true;
        player = new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.prepare();
            setmSeekBar(player.getDuration());

        } catch (IOException e) {
            Log.e(Consts.LOG_TAG, "prepare() failed");
        }
    }

    private void startPlaying() {
        if(player==null)
            preparePlaying();
        player.start();
    }

    private void stopPlaying() {
        if (isPlaying)
            if (player != null) {
                player.release();
                player = null;
            }
        mSeekBar.setProgress(0);
    }

    public void setmSeekBar(final int mFileDuration) {
        seekbarMaxTimeTV.setText(millisToTime(mFileDuration));

        mSeekBar.setMax(mFileDuration / 1000);
        //Make sure you update Seekbar on UI thread
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    int mCurrentPosition = player.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);

                    if (isPlaying && (player.getCurrentPosition() != mFileDuration)) {
                        //update the time on screen
                        String time = millisToTime(player.getCurrentPosition());
                        seekbarTimeTV.setText(time);
//                        Log.e("SeekbarTime", "" + seekbarTimeTV.getText());
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        //when draging seekbar with thumb

    }

    public String millisToTime(long millis) {
        String time = "";
        long seconds = millis / 1000;
        int mins = (int) seconds / 60;
        int sec = (int) seconds % 60;
        if (mins < 10) time += "0";
        time += mins + ":";
        if (sec < 10) time += "0";
        time += sec;
        return time;
    }
}
