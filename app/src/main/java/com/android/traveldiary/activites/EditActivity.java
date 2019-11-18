package com.android.traveldiary.activites;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.traveldiary.R;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.android.traveldiary.dummy.ImageFilePath;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
//import com.scrounger.countrycurrencypicker.library.Country;
//import com.scrounger.countrycurrencypicker.library.CountryCurrencyPicker;
//import com.scrounger.countrycurrencypicker.library.CountryPickerListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class EditActivity extends AppCompatActivity {


    TextInputEditText dateFromET, dateToET, titleET;
    TextInputLayout countiresInputLayout;
    TextInputEditText countiresET;
    TextView screenTitle;
    ChipGroup countriesChipGroup;
    androidx.appcompat.widget.AppCompatEditText countriesET;
    //    private CountryCurrencyPicker pickerDialog;
    final Calendar myCalendar = Calendar.getInstance();
    DatabaseHelper helper;
    ImageView image;
    private String photoUri;
    //    LinearLayout photoActionLayout;
    MaterialButton addPhotoBtn, saveBtn, deletePhotoBtn;
    Travel travel;
    List<String> newCountries, savedCountries, removedCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        helper = new DatabaseHelper(this);

        countriesChipGroup = (ChipGroup) findViewById(R.id.countries_chip_group);
        titleET = (TextInputEditText) findViewById(R.id.input_title);
        dateFromET = (TextInputEditText) findViewById(R.id.input_date_from);
        dateToET = (TextInputEditText) findViewById(R.id.input_date_to);
        image = (ImageView) findViewById(R.id.travel_image);
        screenTitle = (TextView)findViewById(R.id.title) ;
        screenTitle.setText("Edit travel");
//        image.setVisibility(View.INVISIBLE);
        saveBtn = (MaterialButton) findViewById(R.id.save_btn);
        deletePhotoBtn = (MaterialButton)findViewById(R.id.delete_photo_btn);

        countriesET = (TextInputEditText) findViewById(R.id.input_countries);
        countiresInputLayout = (TextInputLayout) findViewById(R.id.input_countries_layout);

        addPhotoBtn = (MaterialButton) findViewById(R.id.new_photo_btn);

        countriesET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCountryPickerDialog();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTravel();
            }
        });


        dateHandler();
        getIntentTravelInfo();
    }

    private void dateHandler() {
        dateToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog picker = new DatePickerDialog(EditActivity.this, dateTo, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }
        });

        dateFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateToET.getText() != null) {
                    DatePickerDialog picker = new DatePickerDialog(EditActivity.this, dateFrom, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    picker.show();
                } else
                    Toast.makeText(getApplicationContext(), "Choose start date first", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private void getIntentTravelInfo() {
        Intent intent = this.getIntent();
        int travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);
        if (travelID == -1) onBackPressed();
        else {
            travel = helper.getTravel(travelID);
            if (travel != null) {
                titleET.setText(travel.getTitle());
                dateFromET.setText(travel.getStartDate());
                dateToET.setText(travel.getEndDate());
                newCountries = new ArrayList<>();
                removedCountries = new ArrayList<>();
                savedCountries = helper.getTravelCountryVisits(travel.getTravelID());
                if(!travel.getImagePath().matches("")) {
                    //there is photo attached
                    image.setImageBitmap(getBitmap(travel.getImagePath()));
                    setPhotoVisible(true);
                }else{
                    setPhotoVisible(false);
                }
                Log.e("EditActivity.getIntentTravelInfo()", "id: " + travelID + " ->newCountries.size() = " + newCountries.size());
                for (String country : savedCountries) {
                    System.out.println("country: " + country);
                    addCountry(country);
                }
            } else {
                Toast.makeText(this, "Could not find Travel in database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @param datePick - 0 - dateFrom; 1 - dateTo
     */
    private void updateLabel(int datePick) {
        SimpleDateFormat sdf = new SimpleDateFormat(Consts.STRING_DATE_PATTERN, Locale.US);

        if (datePick == 0)
            dateFromET.setText(sdf.format(myCalendar.getTime()));
        else
            dateToET.setText(sdf.format(myCalendar.getTime()));
    }


    public void showCountryPickerDialog() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String country, String code, String dialCode, int flagDrawableResID) {
                Toast.makeText(getApplicationContext(), "" + country, Toast.LENGTH_SHORT).show();
                System.out.println("You've chosen " + country);
                addCountry(country);
                picker.dismiss();
            }
        });
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    public void addCountry(String country) {
        if(!savedCountries.contains(country))
            newCountries.add(country);
        if(removedCountries.contains(country))
            removedCountries.remove(country);

        final Chip chip = new Chip(this);
        chip.setChipText(country);
        chip.setCloseIconEnabled(true);
        chip.setChipBackgroundColorResource(R.color.colorPrimary);
        chip.setTextAppearanceResource(R.style.ChipTextStyle);

        countriesChipGroup.addView(chip);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country = chip.getText().toString();
                if(savedCountries.contains(country))
                    removedCountries.add(country);
                newCountries.remove(country);
                countriesChipGroup.removeView(chip);
            }
        });
    }

    private final static int MENU_ITEM_SAVE = 1;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem item = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, "Save");
//        item.setIcon(R.drawable.ic_done);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                saveTravel();
//                return false;
//            }
//        });
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    private void saveTravel() {
        // check if title is not null
        if (titleET.getText().toString().matches("")) {
            titleET.setError("Field obligatory");
        } else if (dateFromET.getText().toString().matches("")) {
            dateFromET.setError("Field obligatory");
        } else if (dateToET.getText().toString().matches("")) {
            dateToET.setError("Field obligatory");
        } else {
            travel.setTitle(titleET.getText().toString());
            travel.setStartDate(dateFromET.getText().toString());
            travel.setEndDate(dateToET.getText().toString());
            travel.setPhotoPath(photoUri);

            //save changes in travel
            DatabaseHelper helper = new DatabaseHelper(this);
            helper.update(travel);

            int travelID = travel.getTravelID();
            //deal with the flags
            for(String country: newCountries){
                if(!savedCountries.contains(country)){
                    //add country to database
                    helper.addCountryVisit(country,travelID);
                }
            }
            for(String country: removedCountries){
                if(savedCountries.contains(country)){
                    //remove country from database
                    helper.removeCountryVisit(country,travelID);
                }
            }
            showCompleteMessage();
        }
    }


    /**
     * MENU
     */


    public boolean onButtonClick(View item) {
        switch (item.getId()) {
            case R.id.new_photo_btn:
                showPictureDialog();
                return true;

//            case R.id.edit_photo_btn:
//                showPictureDialog();
//                return true;

            case R.id.delete_photo_btn:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
//                        removeTravel(travelID,listPosition);
                        setPhotoVisible(false);
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
                return true;
            default:
                return false;
        }
    }

    public void setPhotoVisible(boolean visiblePhoto) {
        if (visiblePhoto) {
            // photo - visible, delete_photo_button - visible
            image.setVisibility(View.VISIBLE);
            deletePhotoBtn.setVisibility(View.VISIBLE);
        } else {
            // photo - gone, delete_photo_button - gone
            image.setVisibility(View.GONE);
            deletePhotoBtn.setVisibility(View.GONE);
        }
    }

    /**
     * ______________________________________________________________________________
     * HANDLE ADD PHOTO
     */

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, Consts.REQUEST_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    //wywoływane po powrocie do aplikacji
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            setPhotoVisible(false);
            return;
        }
        if (requestCode == Consts.REQUEST_GALLERY) {
            if (data != null) {
//                image.setVisibility(View.VISIBLE);
//                addPhotoBtn.setVisibility(View.INVISIBLE);

                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    photoUri = ImageFilePath.getPath(EditActivity.this, contentURI);
                    Log.e("photoURI", "" + photoUri);
                    Toast.makeText(EditActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image.setImageBitmap(bitmap);
                    setPhotoVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == Consts.REQUEST_CAMERA) {
//            image.setVisibility(View.VISIBLE);
//            addPhotoBtn.setVisibility(View.INVISIBLE);
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(thumbnail);
            photoUri = saveImage(thumbnail);
            setPhotoVisible(true);
            Toast.makeText(EditActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + Consts.IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        return "";
    }

    public void showErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Error occured! Go back to previous screen")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                });
    }

    public void showCompleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Message");
        alert.setMessage("Your travel has been updated!");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
