package com.android.traveldiary.traveladds;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.traveldiary.DiaryLogs.VoiceNote;
import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class AddVoiceNoteActivity extends AppCompatActivity {
    private static String TAG = "AddVoiceNoteActivity";

    private static String fileName = null, filePath, audioFormat;
    private LinearLayout recordingLayout;
    private RelativeLayout playerLayout;

    private DatabaseHelper helper;

    //    private RecordButton recordButton = null;
    private MediaRecorder recorder = null;

    //    private PlayButton playButton = null;
    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    long startDate, endDate;


    int day, month, year;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Consts.REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_add_vioce_note);

        helper = new DatabaseHelper(getApplicationContext());

        // Record to the external cache directory for visibility
        filePath = getExternalCacheDir().getAbsolutePath();
        filePath += "/";
        Log.d("file path ", "" + fileName);
        audioFormat = ".3gp";
        fileName = filePath + getFileName() + audioFormat;

        ActivityCompat.requestPermissions(this, permissions, Consts.REQUEST_RECORD_AUDIO_PERMISSION);

        setViews();
        getIntentExtras();
        dateTimeHandler();
    }

    int travelID;
    String date;
    int position;
    EditText recordNameET, dateET;
    ImageView recordBtn, stopBtn, pauseBtn, playBtn, deleteBtn;
    TextView timer, seekbarTimeTV, seekbarMaxTimeTV;
    Button saveBtn;
    Chronometer chronometer;
    SeekBar mSeekBar;

    public void setViews() {
        saveBtn = (Button) findViewById(R.id.save_btn);
        dateET = (EditText) findViewById(R.id.input_date);
        recordNameET = (EditText) findViewById(R.id.input_title);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        recordBtn = (ImageView) findViewById(R.id.record_button);
        stopBtn = (ImageView) findViewById(R.id.stop_button);
        pauseBtn = (ImageView) findViewById(R.id.pause_button);
        playBtn = (ImageView) findViewById(R.id.play_button);
        deleteBtn = (ImageView) findViewById(R.id.delete_button);
        recordingLayout = (LinearLayout) findViewById(R.id.record_layout);
        playerLayout = (RelativeLayout) findViewById(R.id.player_layout);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekbarTimeTV = (TextView) findViewById(R.id.seek_bar_time);
        seekbarMaxTimeTV = (TextView) findViewById(R.id.seek_bar_max_time);

        MaterialButton saveBtn = (MaterialButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

//        Toolbar toolbar = (Toolbar) findViewById(R.id.add_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void getIntentExtras() {
        Intent intent = this.getIntent();
        travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);
        date = intent.getStringExtra(Consts.STRING_CURRENT_DATE);
        dateET.setText(date);
        position = intent.getIntExtra(Consts.STRING_ENTRY_POSITION, -1);
        startDate = intent.getLongExtra(Consts.LONG_START_DATE, -1);
        endDate = intent.getLongExtra(Consts.LONG_END_DATE, -1);

        if (travelID == -1 || position == -1) {
            Toast.makeText(getApplicationContext(), "Error occured: AddVoiceNote", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "travelID = " + travelID + "\nposition = " + position);
            onBackPressed();
        }
        if (!date.matches("")) {
            day = Integer.parseInt(date.substring(0, 2)); // dd-MM-yyyy
            month = Integer.parseInt(date.substring(3, 5));
            year = Integer.parseInt(date.substring(6));
        } else {
            Toast.makeText(AddVoiceNoteActivity.this, "Date error", Toast.LENGTH_SHORT).show();
        }
    }


    private void dateTimeHandler() {
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("AddVoiceNoteActivity.dateTimeHandler()", "dateET.onClick");
                DatePickerDialog picker = new DatePickerDialog(AddVoiceNoteActivity.this, dateListener, year, month, day);
                picker.getDatePicker().setMinDate(startDate);
                picker.getDatePicker().setMaxDate(endDate);
                picker.show();
            }
        });
    }

    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat(Consts.STRING_DATE_PATTERN, Locale.US);
            dateET.setText(sdf.format(myCalendar.getTime()));
        }

    };

    long timeWhenStopped = 0;
    boolean isRecording = false, pausedRecording = false, isPlaying = false;

    public void onClick(View view) {
        Log.e("clicked view", "" + view.getId());
        switch (view.getId()) {
            case R.id.record_button:
                if (!isRecording) {
                    isRecording = true;
                    startChronometer();
                    startAnimation();
                    if (!pausedRecording) startRecording();
                    else resumeRecording();
                }
                break;
            case R.id.stop_button:
                if (isRecording) {
                    isRecording = false;
                    stopAnimation();
                    stopChronometer();
                    stopRecording();
                    showPlayer();
                }
                break;
            case R.id.pause_button:
                if (isRecording) {
                    isRecording = false;
                    pausedRecording = true;
                    stopAnimation();
                    pauseChronometer();
                    pauseRecording();
                }
                break;
            case R.id.play_button:
                if (!isPlaying) {
                    preparePlaying();
                    isPlaying = true;
                } else {
                    stopPlaying();
                    isPlaying = false;
                }
                break;
            case R.id.delete_button:
                if (isPlaying)
                    stopPlaying();
                hidePlayer();
                break;
        }
    }

    public void startChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start();
    }

    public void pauseChronometer() {
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();

    }

    public void stopChronometer() {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timeWhenStopped = 0;
    }

    public void showPlayer() {
        recordingLayout.setVisibility(View.GONE);
        playerLayout.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
    }

    public void hidePlayer() {
        recordingLayout.setVisibility(View.VISIBLE);
        playerLayout.setVisibility(View.INVISIBLE);
        File record = new File(fileName);
        saveBtn.setVisibility(View.GONE);
        record.delete();
    }


    private Handler mHandler = new Handler();

    public void setmSeekBar(final int mFileDuration) {
        seekbarMaxTimeTV.setText(millisToTime(mFileDuration));

        mSeekBar.setMax(mFileDuration / 1000);
        //Make sure you update Seekbar on UI thread
        AddVoiceNoteActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    int mCurrentPosition = player.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);

                    if (isPlaying && (player.getCurrentPosition() != mFileDuration)) {
                        //update the time on screen
                        String time = millisToTime(player.getCurrentPosition());
                        seekbarTimeTV.setText(time);
                        Log.e("SeekbarTime", "" + seekbarTimeTV.getText());
                    }
                }

                mHandler.postDelayed(this, 1000);
            }
        });
        //when draging seekbar with thumb
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null && fromUser) {
                    player.seekTo(progress * 1000);
                    seekbarTimeTV.setText("" + progress);
                    player.start();
                }
            }
        });
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

    /**
     * Handle Recording here
     */


    private void preparePlaying() {
        isPlaying = true;
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            setmSeekBar(player.getDuration());
            startPlaying();
        } catch (IOException e) {
            Log.e(Consts.LOG_TAG, "prepare() failed");
        }
    }

    private void startPlaying() {
        player.start();
    }

    private void stopPlaying() {
        if (isPlaying)
            if (player != null) {
                player.release();
                player = null;
            }
    }

    private void startRecording() {
        fileName = filePath + getFileName() + audioFormat;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        stopBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(Consts.LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void pauseRecording() {
        recorder.pause();
    }

    private void resumeRecording() {
        recorder.resume();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


    /**
     * ----------------------------------------------------------
     * PULSE ANIMATION
     * -----------------------------------------------------------
     */
    ObjectAnimator scaleDown;

    private void pulseAnimation() {
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                recordBtn,
                PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                PropertyValuesHolder.ofFloat("scaleY", 0.8f));
        scaleDown.setDuration(810);
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
    }

    private void startAnimation() {
        if (scaleDown == null) pulseAnimation();
        scaleDown.start();
    }

    private void stopAnimation() {
        scaleDown.setRepeatCount(1);
        scaleDown.cancel();
    }


    private String getFileName() {
        LocalDateTime date = LocalDateTime.now();
        String pattern = "yyyyMMddhhmmss";
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }


    // --------------------------------------
    //    TOOLBAR MENU (SAVE)
    // --------------------------------------


    private void showSavedMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Message");
        alert.setMessage("Your voice note has been saved!");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        alert.show();
    }

    public void save() {
        if (recordNameET.getText().toString().matches("")) {
            recordNameET.setError("Obligatory field");
        } else if (dateET.getText().toString().matches("")) {
            dateET.setError("Obligatory field");
        } else {
            //change file name to set by user
            File file = new File(fileName);
            String newFileName = fileName = filePath + recordNameET.getText().toString() + audioFormat;
            File newFile = new File(newFileName);
            file.renameTo(newFile);
            //add to database

            DatabaseHelper helper = new DatabaseHelper(AddVoiceNoteActivity.this);
            VoiceNote vNote = new VoiceNote(getUniqueID(), recordNameET.getText().toString(), newFileName, dateET.getText().toString(), position, travelID);
            helper.addEntry(vNote);

            showSavedMessage();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public long getUniqueID() {
        return System.currentTimeMillis();
    }

}
