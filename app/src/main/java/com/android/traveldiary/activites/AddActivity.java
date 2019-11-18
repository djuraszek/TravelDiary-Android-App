package com.android.traveldiary.activites;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
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

//import com.pchmn.materialchips.views.ChipsInputEditText;

//import com.scrounger.countrycurrencypicker.library.Country;
//import com.scrounger.countrycurrencypicker.library.CountryCurrencyPicker;
//import com.scrounger.countrycurrencypicker.library.CountryPickerListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class AddActivity extends AppCompatActivity {

    TextInputEditText dateFromET, dateToET, titleET;
    TextInputLayout countiresInputLayout;
    TextInputEditText countiresET;
    ChipGroup countriesChipGroup;
    androidx.appcompat.widget.AppCompatEditText countriesET;
    //    private CountryPicker pickerDialog;
//    private CountryPickerListener pickerListener;
    final Calendar myCalendar = Calendar.getInstance();
    DatabaseHelper helper;
    ImageView image;
    private String photoUri = "";
    //    LinearLayout photoActionLayout;
    MaterialButton addPhotoBtn, saveBtn, deletePhotoBtn;
    int newTravelId;
    List<String> selectedCountries = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.add_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        newTravelId = this.getIntent().getIntExtra(Consts.STRING_TRAVEL_ID, -1);

//        titleET = (TextInputEditText)findViewById(R.id.input_title);
        countriesChipGroup = (ChipGroup) findViewById(R.id.countries_chip_group);
        titleET = (TextInputEditText) findViewById(R.id.input_title);
        dateFromET = (TextInputEditText) findViewById(R.id.input_date_from);
        dateToET = (TextInputEditText) findViewById(R.id.input_date_to);
        image = (ImageView) findViewById(R.id.travel_image);
//        image.setVisibility(View.INVISIBLE);
        saveBtn = (MaterialButton) findViewById(R.id.save_btn);
        deletePhotoBtn = (MaterialButton) findViewById(R.id.delete_photo_btn);

//        countriesET = (androidx.appcompat.widget.AppCompatEditText)findViewById(R.id.input_countries);
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

        setPhotoVisible(false);
    }


    public void setLocale(String lang) {
        Resources res = this.getResources();
// Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        System.out.println("Locales: " + conf.getLocales().toLanguageTags().toString());
        conf.setLocale(new Locale(lang)); // API 17+ only.

// Use
        conf.locale = new Locale(lang); //if targeting lower versions
        res.updateConfiguration(conf, dm);
        System.out.println("Locales: " + conf.getLocales().toLanguageTags().toString());
    }

    private void dateHandler() {
        dateToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (!dateFromET.getText().toString().matches("")) {
                    DatePickerDialog picker = new DatePickerDialog(AddActivity.this, dateTo, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    picker.show();
                } else
                    Toast.makeText(getApplicationContext(), "Choose start date first", Toast.LENGTH_SHORT).show();
            }
        });

        dateFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog picker = new DatePickerDialog(AddActivity.this, dateFrom, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                picker.show();
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

    /**
     * @param datePick - 0 - dateFrom; 1 - datTo
     */
    private void updateLabel(int datePick) {
        SimpleDateFormat sdf = new SimpleDateFormat(Consts.STRING_DATE_PATTERN, Locale.US);

        if (datePick == 0)
            dateFromET.setText(sdf.format(myCalendar.getTime()));
        else
            dateToET.setText(sdf.format(myCalendar.getTime()));
    }

    public String getClassName() {
        return "FragmentAdd";
    }

    public void addCountry(String country) {
        if (!selectedCountries.contains(country)) {
            selectedCountries.add(country);

            final Chip chip = new Chip(this);
            chip.setChipText(country);
            chip.setCloseIconEnabled(true);
            chip.setChipBackgroundColorResource(R.color.colorPrimary);
            chip.setTextAppearanceResource(R.style.ChipTextStyle);

            countriesChipGroup.addView(chip);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCountries.remove(chip.getText().toString());
                    countriesChipGroup.removeView(chip);
                }
            });
        } else
            Toast.makeText(this, "You've already added this country", Toast.LENGTH_SHORT).show();
    }

    public void showCountryPickerDialog() {
//        for(Country c: Country.COUNTRIES)
//            System.out.println(c.getName().toString());

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

    private final static int MENU_ITEM_SAVE = 1;

    private void saveTravel() {
        // check if title is not null
        if (titleET.getText().toString().matches("")) {
            titleET.setError("Field obligatory");
        } else if (dateFromET.getText().toString().matches("")) {
            dateFromET.setError("Field obligatory");
        } else if (dateToET.getText().toString().matches("")) {
            dateToET.setError("Field obligatory");
        } else {
            Travel travel = new Travel(newTravelId,
                    titleET.getText().toString(),
                    dateFromET.getText().toString(),
                    dateToET.getText().toString());
            travel.setPhotoPath(photoUri);

            //save changes in travel
            DatabaseHelper helper = new DatabaseHelper(this);
            helper.addTravel(travel);
            if (selectedCountries.size() > 0) {
                //add countries
                int travelID = helper.getTravelId(travel);
                if (travelID != -1) {
                    for (String country : selectedCountries) {
                        helper.addCountryVisit(country, travelID);
                    }
                } else {
                    Log.e("AddActivity.saveTravel()", "TravelId == -1 ");
                }
            }
            showSavedMessage();
        }
    }

    private void showSavedMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Message");
        alert.setMessage("Your travel has been saved!");

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                Intent intent = new Intent();
                intent.putExtra("addedTravel", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        alert.show();
    }

    /**
     * MENU
     */


    public boolean onButtonClick(View item) {
        switch (item.getId()) {
            case R.id.new_photo_btn:
                showPictureDialog();

                return true;

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
        Log.e("AddActivity","setPhotoVisible-"+visiblePhoto);
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
//                photoActionLayout.setVisibility(View.VISIBLE);
//                image.setVisibility(View.VISIBLE);
//                addPhotoBtn.setVisibility(View.INVISIBLE);

                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    photoUri = ImageFilePath.getPath(AddActivity.this, contentURI);
                    Log.e("photoURI", "" + photoUri);
                    Toast.makeText(AddActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image.setImageBitmap(bitmap);
                    setPhotoVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Consts.REQUEST_CAMERA) {
//            photoActionLayout.setVisibility(View.VISIBLE);
//            image.setVisibility(View.VISIBLE);
//            addPhotoBtn.setVisibility(View.INVISIBLE);
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(thumbnail);
            photoUri = saveImage(thumbnail);
            setPhotoVisible(true);
            Toast.makeText(AddActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Added new photo!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
