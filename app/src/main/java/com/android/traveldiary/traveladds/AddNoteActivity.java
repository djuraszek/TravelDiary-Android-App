package com.android.traveldiary.traveladds;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.traveldiary.DiaryLogs.Note;
import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {
    EditText titleET, noteET, dateET;
    int travelID, position;
    String date;

    int day, month, year;
    long startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setViews();
        getIntentExtras();
        dateTimeHandler();
    }

    public void setViews() {
        titleET = findViewById(R.id.input_title);
        noteET = findViewById(R.id.input_note);
        dateET = findViewById(R.id.input_date);

        MaterialButton saveBtn = (MaterialButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

    }

    public void getIntentExtras() {
        Intent intent = this.getIntent();
        travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);
        date = intent.getStringExtra(Consts.STRING_CURRENT_DATE);
        dateET.setText(date);
        startDate = intent.getLongExtra(Consts.LONG_START_DATE,-1);
        endDate = intent.getLongExtra(Consts.LONG_END_DATE,-1);

//      todo  position = intent.getIntExtra(Consts.STRING_ENTRY_POSITION,-1);

        if (travelID == -1) {
            Toast.makeText(getApplicationContext(), "Error occured: AddTransport", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        if (!date.matches("")) {
            day = Integer.parseInt(date.substring(0, 2)); // dd-MM-yyyy
            month = Integer.parseInt(date.substring(3, 5));
            year = Integer.parseInt(date.substring(6));
        } else{
            Toast.makeText(AddNoteActivity.this,"Date error",Toast.LENGTH_SHORT).show();
        }
    }


    // --------------------------------------
    //    TOOLBAR MENU (SAVE)
    // --------------------------------------

    private final static int MENU_ITEM_SAVE = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, "Save");
        item.setIcon(R.drawable.ic_done);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                save();
                showSavedMessage();
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void showSavedMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Message");
        alert.setMessage("Your note has been saved!");
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
        if (noteET.getText().toString().matches("")) {
            noteET.setError("Obligatory field");
        } else {
            Note n = new Note(getUniqueID(), titleET.getText().toString(), noteET.getText().toString(), date, position, travelID);

            DatabaseHelper helper = new DatabaseHelper(AddNoteActivity.this);
            helper.addEntry(n);
            showSavedMessage();
        }
    }

    public long getUniqueID() {
        return System.currentTimeMillis();
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

    private void dateTimeHandler() {
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("AddTransportActivity.dateTimeHandler()", "arrivalDate.onClick");
                DatePickerDialog picker = new DatePickerDialog(AddNoteActivity.this, dateListener, year, month, day);
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
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat(Consts.STRING_DATE_PATTERN, Locale.US);
            dateET.setText(sdf.format(myCalendar.getTime()));
        }

    };
}
