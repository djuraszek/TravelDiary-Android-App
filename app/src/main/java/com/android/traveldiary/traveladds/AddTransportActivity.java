package com.android.traveldiary.traveladds;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.traveldiary.diaryentries.Transport;
import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;


public class AddTransportActivity extends AppCompatActivity {
    private static String TAG = "AddTransportActivity";
    int travelID, position;
    String currentDate;
    ChipGroup chipGroup;
    String transportType = "";
    TextInputEditText departurePlaceTV, arrivalPlaceTV, departureTimeTV, arrivalTimeTV;
    TextInputEditText departureDateTV, arrivalDateTV;
    String dPlace, dDate, dTime;
    String aPlace, aDate, aTime;
    long startDate, endDate;
    int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transport);
        setViews();
        getIntentExtras();

    }

    public void getIntentExtras() {
        Intent intent = this.getIntent();
        travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);
        currentDate = intent.getStringExtra(Consts.STRING_CURRENT_DATE);
        position = intent.getIntExtra(Consts.STRING_ENTRY_POSITION, -1);

        startDate = intent.getLongExtra(Consts.LONG_START_DATE,-1);
        endDate = intent.getLongExtra(Consts.LONG_END_DATE,-1);

        if(startDate==-1 || endDate==-1){
            Toast.makeText(getApplicationContext(), "Error occured: start or end date equals -1", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "DATE ERROR: intent start/end date == -1");
            onBackPressed();
        }

        if (travelID == -1) {
            Toast.makeText(getApplicationContext(), "Error occured: AddTransport", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        if (!currentDate.matches("")) {
            departureDateTV.setText(currentDate);
            arrivalDateTV.setText(currentDate);

            day = Integer.parseInt(currentDate.substring(0, 2)); // dd-MM-yyyy
            month = Integer.parseInt(currentDate.substring(3, 5));
            year = Integer.parseInt(currentDate.substring(6));
            Log.e("AddTransportActivity.getIntExtras()", day + " " + month + " " + year);
        }

    }

    public boolean checkInputNotNull() {
        if (transportType.matches("")) {
            Toast.makeText(getApplicationContext(), "Choose means of transportType.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(dPlace)) {
            departurePlaceTV.setError("Obligatory field");
            return false;
        } else if (dDate.matches("")) {
            departureDateTV.setError("Obligatory field");
            return false;
        } else if (aPlace.matches("")) {
            arrivalPlaceTV.setError("Obligatory field");
            return false;
        } else if (aDate.matches("")) {
            arrivalDateTV.setError("Obligatory field");
            return false;
        } else
            return true;
    }

    public void setViews() {
        chipGroup = (ChipGroup) findViewById(R.id.chip_group);
        departurePlaceTV = (TextInputEditText) findViewById(R.id.departure_place);
        departureDateTV = (TextInputEditText) findViewById(R.id.departure_date);
        departureTimeTV = (TextInputEditText) findViewById(R.id.departure_time);
        arrivalPlaceTV = (TextInputEditText) findViewById(R.id.arrival_place);
        arrivalDateTV = (TextInputEditText) findViewById(R.id.arrival_date);
        arrivalTimeTV = (TextInputEditText) findViewById(R.id.arrival_time);
        MaterialButton saveBtn = (MaterialButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        dateTimeHandler();
    }

//    private void createToolbar() {
//        Toolbar toolbar = findViewById(R.id.add_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//    }


    public void onChipChecked(View view) {
        // Is the button now checked?
        boolean checked = ((Chip) view).isChecked();

        switch (view.getId()) {
            case R.id.chip_car:
                chipGroup.clearCheck();
                chipGroup.check(view.getId());
                if (checked)
                    transportType = "car";
                break;
            case R.id.chip_bike:
                chipGroup.clearCheck();
                chipGroup.check(view.getId());
                if (checked)
                    transportType = "bike";
                break;
            case R.id.chip_boat:
                chipGroup.clearCheck();
                chipGroup.check(view.getId());
                if (checked)
                    transportType = "boat";
                break;
            case R.id.chip_plane:
                chipGroup.clearCheck();
                chipGroup.check(view.getId());
                if (checked)
                    transportType = "plane";
                break;
            case R.id.chip_train:
                chipGroup.clearCheck();
                chipGroup.check(view.getId());
                if (checked)
                    transportType = "train";
                break;
            default:
                break;
        }
    }

    private LocalDate stringToDate(String date) throws ParseException {
        Log.d("Date to parse", date);
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(Consts.STRING_DATE_PATTERN));
    }

    private String dateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(Consts.STRING_DATE_PATTERN));
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
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void showSavedMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Message");
        alert.setMessage("Your transport has been saved!");
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
        dPlace = departurePlaceTV.getText().toString();
        dDate = departureDateTV.getText().toString();
        dTime = departureTimeTV.getText().toString();
        aPlace = arrivalPlaceTV.getText().toString();
        aDate = arrivalDateTV.getText().toString();
        aTime = arrivalTimeTV.getText().toString();

        if (checkInputNotNull()) {
            //todo add to databse
            DatabaseHelper helper = new DatabaseHelper(AddTransportActivity.this);
            Transport t = new Transport(getUniqueID(), transportType,
                    departurePlaceTV.getText().toString(),
                    departureDateTV.getText().toString(),
                    departureTimeTV.getText().toString(),
                    arrivalPlaceTV.getText().toString(),
                    arrivalDateTV.getText().toString(),
                    arrivalTimeTV.getText().toString(),
                    position, travelID);
            Log.e(TAG, t.toString());
            helper.addEntry(t);

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


    /**
     * ------------------------------------------------------------------------------------------
     * DATE AND TIME PICKER
     * ------------------------------------------------------------------------------------------
     */

    private void dateTimeHandler() {
        departureDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TOD Auto-generated method stub
                Log.e("AddTransportActivity.dateTimeHandler()", "arrivalDate.onClick");
                DatePickerDialog picker = new DatePickerDialog(AddTransportActivity.this, dateTo, year, month, day);
                picker.getDatePicker().setMinDate(startDate);
                picker.getDatePicker().setMaxDate(endDate);
                picker.show();
                picker.show();
            }
        });

        arrivalDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("AddTransportActivity.dateTimeHandler()", "arrivalDate.onClick");
                DatePickerDialog picker = new DatePickerDialog(AddTransportActivity.this,
                        dateFrom, year, month, day);
                picker.getDatePicker().setMinDate(startDate);
                picker.getDatePicker().setMaxDate(endDate);
                picker.show();
                picker.show();
            }
        });

        departureTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TOD Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddTransportActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = selectedHour + ":";
                        if (selectedMinute < 10) time += "0";
                        time += selectedMinute;
                        departureTimeTV.setText(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        arrivalTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TOD Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddTransportActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = selectedHour + ":";
                        if (selectedMinute < 10) time += "0";
                        time += selectedMinute;
                        arrivalTimeTV.setText(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

    }

    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(0);
        }

    };
    DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(1);
        }

    };

    /**
     * @param datePick - 0 - dateFrom; 1 - datTo
     */
    private void updateLabel(int datePick) {
        SimpleDateFormat sdf = new SimpleDateFormat(Consts.STRING_DATE_PATTERN, Locale.US);

        if (datePick == 0)
            departureDateTV.setText(sdf.format(myCalendar.getTime()));
        else
            arrivalDateTV.setText(sdf.format(myCalendar.getTime()));
    }



}
