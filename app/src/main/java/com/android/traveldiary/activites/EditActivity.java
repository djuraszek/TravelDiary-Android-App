package com.android.traveldiary.activites;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.android.traveldiary.serverrequests.DeletePhotoRequest;
import com.android.traveldiary.serverrequests.GetTravelRequest;
import com.android.traveldiary.serverrequests.PutUpdatePhotoRequest;
import com.android.traveldiary.serverrequests.PutUpdateTravelRequest;
import com.android.traveldiary.serverrequests.VolleyMultipartRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
//import com.mukesh.countrypicker.CountryPicker;
//import com.mukesh.countrypicker.CountryPickerListener;
//import com.scrounger.countrycurrencypicker.library.Country;
//import com.scrounger.countrycurrencypicker.library.CountryCurrencyPicker;
//import com.scrounger.countrycurrencypicker.library.CountryPickerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class EditActivity extends AppCompatActivity {
    String TAG = "EditActivity";

    TextInputEditText dateFromET, dateToET, titleET;
    TextInputLayout countiresInputLayout;
    TextInputEditText countiresET;
    TextView screenTitle;
    ChipGroup countriesChipGroup;
    androidx.appcompat.widget.AppCompatEditText countriesET;
    final Calendar myCalendarFrom = Calendar.getInstance();
    final Calendar myCalendarTo = Calendar.getInstance();
    DatabaseHelper helper;
    ImageView image;
    private String photoUri;
    MaterialButton addPhotoBtn, deletePhotoBtn;
    ImageView saveBtn;
    Travel travel;
    List<String> newCountries, savedCountries, removedCountries;

    boolean deletePhoto = false;
    int photoID = -1;

    public Bitmap originalPhotoBitmap, newBitmap;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add);

        SharedPreferences preferences = getSharedPreferences("appPref", MODE_PRIVATE);
        token = preferences.getString("token", "");

        int travelID = this.getIntent().getExtras().getInt("travel_id");

//        helper = new DatabaseHelper(this);

//        countriesChipGroup = (ChipGroup) findViewById(R.id.countries_chip_group);
        titleET = (TextInputEditText) findViewById(R.id.input_title);
        dateFromET = (TextInputEditText) findViewById(R.id.input_date_from);
        dateToET = (TextInputEditText) findViewById(R.id.input_date_to);
        image = (ImageView) findViewById(R.id.travel_image);
        screenTitle = (TextView) findViewById(R.id.toolbar_title);
        screenTitle.setText("Edit travel");

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deletePhotoBtn = (MaterialButton) findViewById(R.id.delete_photo_btn);
        addPhotoBtn = (MaterialButton) findViewById(R.id.new_photo_btn);
        saveBtn = (ImageView) findViewById(R.id.toolbar_save);

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
//        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTravel();
            }
        });
        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditActivity.this, R.style.AlertDialogUnfollowTheme);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
//                        removeTravel(travelID,listPosition);
                        setPhotoVisible(false);
                        deletePhoto = true;
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        getTravelFromServer(travelID);
    }


    public void getTravelFromServer(final int travelID) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
//                                String resp = jsonResponse.toString();
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        Log.v(TAG + ".ServerResp", "" + dataObject.toString());
                        String title = dataObject.getString("title");
                        String dateFrom = dataObject.getString("start_date").substring(0, 10);
                        String dateTo = dataObject.getString("end_date").substring(0, 10);
                        int userID = dataObject.getInt("user_id");

                        travel = new Travel(travelID, title, dateFrom, dateTo);
                        travel.setUserID(userID);

                        boolean isPhotoNull = dataObject.get("main_photo").toString().equals("null");
                        if (!isPhotoNull) {
                            JSONObject photoObj = dataObject.getJSONObject("main_photo");
                            String photoURL = photoObj.getString("path");
                            photoID = (dataObject.getJSONObject("main_photo")).getInt("id");
                            travel.setPhotoPath(photoURL);
                        }
                        getIntentTravelInfo(travelID);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetTravelRequest request = new GetTravelRequest(token, travelID, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void getIntentTravelInfo(int travelID) {
//        Intent intent = this.getIntent();
//        int travelID = intent.getIntExtra(, -1);
        if (travelID == -1) {
            Toast.makeText(this, "Wrong travelID", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {

            if (travel != null) {
                titleET.setText(travel.getTitle());
                dateFromET.setText(travel.getStartDate());
                dateToET.setText(travel.getEndDate());
                dateHandler();

                int date[] = getDate(travel.getStartDate());
                int dateTo[] = getDate(travel.getEndDate());
                setDates(date, dateTo);

                if (!travel.getImagePath().matches("")) {
                    new EditActivity.GetImageFromUrl(image, this).execute(travel.getImagePath());
                    setPhotoVisible(true);
                } else {
                    setPhotoVisible(false);
                }
            } else {
                Toast.makeText(this, "Could not find Travel in database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setDates(int[] dateFrom, int[] dateTo) {
        myCalendarTo.set(Calendar.YEAR, dateTo[0]);
        myCalendarTo.set(Calendar.MONTH, dateTo[1]);
        myCalendarTo.set(Calendar.DAY_OF_MONTH, dateTo[2]);

        myCalendarFrom.set(Calendar.YEAR, dateFrom[0]);
        myCalendarFrom.set(Calendar.MONTH, dateFrom[1]);
        myCalendarFrom.set(Calendar.DAY_OF_MONTH, dateFrom[2]);
    }

    /**
     * @param datePick - 0 - dateFrom; 1 - dateTo
     */
    private void updateLabel(int datePick) {
        SimpleDateFormat sdf = new SimpleDateFormat(Consts.STRING_DATE_PATTERN, Locale.US);
        if (datePick == 0)
            dateFromET.setText(sdf.format(myCalendarFrom.getTime()));
        else
            dateToET.setText(sdf.format(myCalendarTo.getTime()));
    }


//    public void showCountryPickerDialog() {
//        final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
//        picker.setListener(new CountryPickerListener() {
//            @Override
//            public void onSelectCountry(String country, String code, String dialCode, int flagDrawableResID) {
//                Toast.makeText(getApplicationContext(), "" + country, Toast.LENGTH_SHORT).show();
//                System.out.println("You've chosen " + country);
//                addCountry(country);
//                picker.dismiss();
//            }
//        });
//        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
//    }

    public void addCountry(String country) {
        if (!savedCountries.contains(country))
            newCountries.add(country);
        if (removedCountries.contains(country))
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
                if (savedCountries.contains(country))
                    removedCountries.add(country);
                newCountries.remove(country);
                countriesChipGroup.removeView(chip);
            }
        });
    }

    private final static int MENU_ITEM_SAVE = 1;


    private void saveTravel() {
        // check if title is not null
        if (titleET.getText().toString().matches("")) {
            titleET.setError("Field obligatory");
            Toast.makeText(this, "Title field obligatory", Toast.LENGTH_SHORT).show();
        } else if (dateFromET.getText().toString().matches("")) {
            dateFromET.setError("Field obligatory");
            Toast.makeText(this, "Date field obligatory", Toast.LENGTH_SHORT).show();
        } else if (dateToET.getText().toString().matches("")) {
            dateToET.setError("Field obligatory");
            Toast.makeText(this, "Date field obligatory", Toast.LENGTH_SHORT).show();
        } else {
            //sprawdz czy zmieniono zdjecie lub usunieto zdjecie
            if (newBitmap != null) {
                //jesli nie bylo zdjecia w systemie to dodaj nowe
                if (photoID == -1)
                    addPhoto();
                    //aktualizuj zdjecie ktore bylo w systemie
                else
                    updatePhoto();
                //usun zdjecie calkowicie
            } else if (deletePhoto) {
                deletePhoto();
            } else {
                updateTravel();
            }
//            showCompleteMessage();
        }
    }

    /**
     * -------------------------------------------------------------------------------
     * SERVER REQUESTS
     * ------------------------------------------------------------------------------
     */
    private String ROOT_URL = "https://travellist.mitimise.tk/api/photos/";
//    private static String URL = "https://webhook.site/ddbc7035-14c7-4926-ba39-4e160773e523";

    private void addPhoto() {
        final String title = "albumPhoto";
        final String date = dateFromET.getText().toString();

        ROOT_URL += travel.getTravelID();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL, token,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            boolean success = obj.getBoolean("success");
                            if (success) {
                                Log.e("POST PHOTO UPLOAD", "" + obj.toString());
                                //todo go to updateTravel
                                photoID = (obj.getJSONObject("data")).getInt("id");
                                updateTravel();
                            }
                            Toast.makeText(EditActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d("EditActivity.addPhoto", "Error: " + error
                                + "\nStatus Code " + error.networkResponse.statusCode
                                + "\nResponse Data " + error.networkResponse.data
                                + "\nCause " + error.getCause()
                                + "\nmessage" + error.getMessage());

                        Log.d(TAG, "Failed with error msg:\t" + error.getMessage());
                        Log.d(TAG, "Error StackTrace: \t" + error.getStackTrace());
                        // edited here
                        try {
                            byte[] htmlBodyBytes = error.networkResponse.data;
                            Log.e(TAG, new String(htmlBodyBytes), error);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        if (error.getMessage() == null) {
                            Log.d(TAG, "null");
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", "albumPhoto");
                params.put("date", date);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new VolleyMultipartRequest.DataPart(imagename + ".png", getFileDataFromDrawable(newBitmap)));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(EditActivity.this).add(volleyMultipartRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    private void updatePhoto() {
        final String title = "albumPhoto";
        final String date = travel.getStartDate();

        PutUpdatePhotoRequest volleyMultipartRequest = new PutUpdatePhotoRequest(token, photoID,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            boolean success = obj.getBoolean("success");
                            if (success) {
                                Log.e(TAG + ".POST PHOTO UPDATE", "" + obj.toString());
                                //todo go to updateTravel
                                photoID = (obj.getJSONObject("data")).getInt("id");
                                updateTravel();
                            }
                            Toast.makeText(EditActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.d("EditActivity.addPhoto", "Error: " + error
                                + "\nStatus Code " + error.networkResponse.statusCode
                                + "\nResponse Data " + error.networkResponse.data
                                + "\nCause " + error.getCause()
                                + "\nmessage" + error.getMessage());

                        Log.d(TAG, "Failed with error msg:\t" + error.getMessage());
                        Log.d(TAG, "Error StackTrace: \t" + error.getStackTrace());
                        // edited here
                        try {
                            byte[] htmlBodyBytes = error.networkResponse.data;
                            Log.e(TAG, new String(htmlBodyBytes), error);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        if (error.getMessage() == null) {
                            Log.d(TAG, "null");
                        }
                    }
                })  {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", "albumPhoto");
                params.put("date", date);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new PutUpdatePhotoRequest.DataPart(imagename + ".png", getFileDataFromDrawable(newBitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(EditActivity.this).add(volleyMultipartRequest);
    }

    private void deletePhoto() {
        //todo set photoID to null
//        photoID = ;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Log.v(TAG + ".DELETE PHOTO RESPONSE", "resp: " + jsonResponse.toString());
                        photoID = -1;
                        updateTravel();
                    } else {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditActivity.this, R.style.AlertDialogUnfollowTheme);
                        builder.setMessage("Register Faild")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DeletePhotoRequest registerRequest = new DeletePhotoRequest(token, photoID, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(EditActivity.this);
        queue.add(registerRequest);

    }

    private void updateTravel() {
        travel.setStartDate(dateFromET.getText().toString());
        travel.setEndDate(dateToET.getText().toString());
        travel.setTitle(titleET.getText().toString());

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Log.v(TAG + ".UPDATE TRAVEL RESPONSE", "resp: " + jsonResponse.toString());
                        showCompleteMessage();
                    } else {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditActivity.this, R.style.AlertDialogUnfollowTheme);
                        builder.setMessage("Uploading travel Faild").setNegativeButton("Retry", null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Handle Error
                Log.d("AddNoteActivity", "Error: " + error
                        + "\nStatus Code " + error.networkResponse.statusCode
                        + "\nResponse Data " + error.networkResponse.data
                        + "\nCause " + error.getCause()
                        + "\nmessage" + error.getMessage());

                Log.d(TAG, "Failed with error msg:\t" + error.getMessage());
                Log.d(TAG, "Error StackTrace: \t" + error.getStackTrace());
                // edited here
                try {
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    Log.e(TAG, new String(htmlBodyBytes), error);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (error.getMessage() == null) {
                    Log.d(TAG, "null");
                }
            }
        };

        PutUpdateTravelRequest registerRequest = new PutUpdateTravelRequest(token, photoID, travel, responseListener, error);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(registerRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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
                AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogUnfollowTheme);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
//                        removeTravel(travelID,listPosition);
                        setPhotoVisible(false);
                        deletePhoto = true;
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
                    newBitmap = bitmap;
                    deletePhoto = false;
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
            newBitmap = thumbnail;
            newBitmap = thumbnail;
            deletePhoto = false;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogUnfollowTheme);
        builder.setMessage("Error occured! Go back to previous screen")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                });
    }

    public void showCompleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogUnfollowTheme);
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


    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap bitmap;
        EditActivity activity;

        public GetImageFromUrl(ImageView img, EditActivity activity) {
            this.imageView = img;
            this.activity = activity;
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
            activity.originalPhotoBitmap = bitmap;
        }
    }

    public int[] getDate(String date) {
        int[] d = new int[3];
        d[0] = Integer.parseInt(date.substring(0, 4));
        d[1] = Integer.parseInt(date.substring(5, 7));
        d[2] = Integer.parseInt(date.substring(8, 10));
        Log.i(TAG, "from " + date + " to " + d[0] + "-" + d[1] + "-" + d[2]);
        return d;
    }

    private void dateHandler() {
        dateToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog picker = new DatePickerDialog(EditActivity.this, dateTo, myCalendarTo
                        .get(Calendar.YEAR), myCalendarTo.get(Calendar.MONTH),
                        myCalendarTo.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }
        });

        dateFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateToET.getText() != null) {
                    int date[] = getDate(travel.getStartDate());
                    myCalendarFrom.set(date[0], date[1], date[2]);
                    DatePickerDialog picker = new DatePickerDialog(EditActivity.this, dateFrom, myCalendarFrom
                            .get(Calendar.YEAR), myCalendarFrom.get(Calendar.MONTH),
                            myCalendarFrom.get(Calendar.DAY_OF_MONTH));
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
            myCalendarFrom.set(Calendar.YEAR, year);
            myCalendarFrom.set(Calendar.MONTH, monthOfYear);
            myCalendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(0);
        }

    };
    DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendarTo.set(Calendar.YEAR, year);
            myCalendarTo.set(Calendar.MONTH, monthOfYear);
            myCalendarTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(1);
        }

    };




}
