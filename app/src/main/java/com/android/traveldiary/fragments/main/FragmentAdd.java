package com.android.traveldiary.fragments.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.traveldiary.classes.Travel;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.android.traveldiary.dummy.ImageFilePath;
import com.android.traveldiary.serverrequests.PostCreateTravelRequest;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class FragmentAdd extends Fragment {

    androidx.appcompat.widget.AppCompatEditText countriesET;
    ChipGroup countriesChipGroup;
    MaterialButton addPhotoBtn, deletePhotoBtn;
    TextInputEditText dateFromET, dateToET, titleET;
    TextInputLayout countiresInputLayout;
    TextInputEditText countiresET;
    ImageView image;
    ImageView toolbarSaveBtn;
    DatabaseHelper helper;

    final Calendar myCalendar = Calendar.getInstance();
    private String photoUri = "";
    int newTravelId;
    List<String> selectedCountries = new ArrayList<>();
    MainActivity mainActivity;

    Bitmap bitmap;
    Travel travel;

    public FragmentAdd() {
        // Required empty public constructor
    }

    public static FragmentAdd newInstance(String param1, String param2) {
        FragmentAdd fragment = new FragmentAdd();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setTitle("Add new");
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        mainActivity = (MainActivity) getActivity();
        System.out.println("TOKEN: " + mainActivity.getToken());

//        countriesChipGroup = (ChipGroup) view.findViewById(R.id.countries_chip_group);
        titleET = (TextInputEditText) view.findViewById(R.id.input_title);
        dateFromET = (TextInputEditText) view.findViewById(R.id.input_date_from);
        dateToET = (TextInputEditText) view.findViewById(R.id.input_date_to);
        image = (ImageView) view.findViewById(R.id.travel_image);
        deletePhotoBtn = (MaterialButton) view.findViewById(R.id.delete_photo_btn);
        addPhotoBtn = (MaterialButton) view.findViewById(R.id.new_photo_btn);
        toolbarSaveBtn = (ImageView) view.findViewById(R.id.toolbar_save);


        toolbarSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo change to SAVE TRAVEL
                saveTravel();
//                travel = new Travel(14, "lol", "2020-06-01", "2020-06-02");
//                addPhoto();
//                updateTravelPhoto(3);
            }
        });

        ButtonHandler();
        dateHandler();
        return view;
    }


    public void ButtonHandler() {
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
            }
        });
        deletePhotoBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().setTitle("Add new");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void dateHandler() {
        dateToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (!dateFromET.getText().toString().matches("")) {
                    DatePickerDialog picker = new DatePickerDialog(getActivity(), dateTo, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    String date = dateFromET.getText().toString();
                    System.out.println("Data " + date);

                    picker.getDatePicker().setMinDate(getDateInMillis(date));
                    picker.show();
                } else
                    Toast.makeText(getActivity(), "Choose start date first", Toast.LENGTH_SHORT).show();
            }
        });

        dateFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog picker = new DatePickerDialog(getActivity(), dateFrom, myCalendar
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

    private long getDateInMillis(String date) {
        Calendar c = Calendar.getInstance();
        int year = Integer.parseInt(date.substring(8));
        int month = Integer.parseInt(date.substring(5, 7));
        int day = Integer.parseInt(date.substring(0, 4));
        c.set(year, (month - 1), day); //january is 0; february is 1 .... (month - 1)

        System.out.println("d: " + day + " m: " + month + " y: " + year);
        System.out.println("data: " + c.getTime().toString());


        return c.getTimeInMillis();

    }


    /**
     * @param datePick - 0 - dateFrom; 1 - datTo
     */
    private void updateLabel(int datePick) {
//        SimpleDateFormat sdf = new SimpleDateFormat(Consts.STRING_DATE_PATTERN, Locale.US);
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

            final Chip chip = new Chip(getActivity());
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
            Toast.makeText(getActivity(), "You've already added this country", Toast.LENGTH_SHORT).show();
    }

    private final static int MENU_ITEM_SAVE = 1;

    /**
     * ________________________________________________________________________________________
     * ADD TO SERVER
     * ________________________________________________________________________________________
     */

    private void saveTravel() {
        // check if title is not null
        if (titleET.getText().toString().matches("")) {
            titleET.setError("Field obligatory");
        } else if (dateFromET.getText().toString().matches("")) {
            dateFromET.setError("Field obligatory");
        } else if (dateToET.getText().toString().matches("")) {
            dateToET.setError("Field obligatory");
        } else {
            travel = new Travel(newTravelId, titleET.getText().toString(), dateFromET.getText().toString(), dateToET.getText().toString());
//            travel.setPhotoPath(photoUri);
            bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

            Log.e("AddTravel", "" + travel.toString());

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            Log.v("ADD TRAVEL RESPONSE", "resp: " + jsonResponse.toString());
                            JSONObject data = jsonResponse.getJSONObject("data");
                            int travelID = data.getInt("id");
                            System.out.println("new travel id: "+travelID);
                            travel.setTravelID(travelID);

                            if (bitmap != null) addPhoto();
//                            showCompleteMessage();
                        } else {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
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
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String body;
                    //get status code here
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    Log.e("ServerError", "status code: " + statusCode);
                    //get response body and parse with appropriate encoding
                    if (error.networkResponse.data != null) {
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    //do stuff with the body...
                }
            };

            PostCreateTravelRequest registerRequest = new PostCreateTravelRequest(mainActivity.getToken(), travel, responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(registerRequest);
        }
    }

    private String ROOT_URL = "https://travellist.mitimise.tk/api/photos/";

    private void addPhoto() {
//        String photo = getStringImage(bitmap);
        final String title = "albumPhoto";
        final String date = travel.getStartDate();
        ROOT_URL += "" + travel.getTravelID();
//        final String tags = editTextTags.getText().toString().trim();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL, mainActivity.getToken(),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(mainActivity, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            boolean success = obj.getBoolean("success");
                            if(success) {
                                Log.e("FragmentAdd.addPhoto", "resp: " + obj.toString());
                                JSONObject data = obj.getJSONObject("data");
                                int id = data.getInt("id");
                                updateTravelPhoto(id);
                            }
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
                })  {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("date", date);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(mainActivity).add(volleyMultipartRequest);

    }

//    resp: {
//        "success":true,
//                "data":{"id":3,
//                "path":"https:\/\/travellist.mitimise.tk\/storage\/photos\/zEBKLUHv7JwQaDEMlme7a5cuZijjKn6ySejwpKkT.png",
//                "title":null,
//                "date":"2020-05-11T00:00:00.000000Z",
//                "created_at":"2020-05-28T11:30:20.000000Z",
//                "updated_at":"2020-05-28T11:30:20.000000Z",
//                "travel_id":"2"},
//        "message":"Photo saved successfully"}


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void updateTravelPhoto(int photoID) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Log.v("ADD TRAVEL RESPONSE", "resp: " + jsonResponse.toString());

                        showCompleteMessage();
                    } else {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
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
        PutUpdateTravelRequest registerRequest = new PutUpdateTravelRequest(mainActivity.getToken(), photoID, travel, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(registerRequest);
    }

//    resp: {
//        "success":true,
//                "data":{"id":2,
//                "title":"Malabami",
//                "start_date":"2020-05-11T00:00:00.000000Z",
//                "end_date":"2020-05-13T00:00:00.000000Z",
//                "created_at":"2020-05-27T19:07:34.000000Z",
//                "updated_at":"2020-05-29T09:59:00.000000Z",
//                "user_id":3,
//                "main_photo":"https:\/\/travellist.mitimise.tk\/storage\/photos\/zEBKLUHv7JwQaDEMlme7a5cuZijjKn6ySejwpKkT.png",
//                "likes_count":0,
//                "is_liked":false,
//                "photos&notes":[{"type":"photo",
//                "data":{"id":3,
//                "path":"https:\/\/travellist.mitimise.tk\/storage\/photos\/zEBKLUHv7JwQaDEMlme7a5cuZijjKn6ySejwpKkT.png",
//                "title":null,"date":"2020-05-11T00:00:00.000000Z",
//                "created_at":"2020-05-28T11:30:20.000000Z",
//                "updated_at":"2020-05-28T11:30:20.000000Z",
//                "travel_id":2}}]},
//        "message":"Travel updated successfully"}


    /**
     * ________________________________________________________________________________________
     * ADD TO SERVER
     * ________________________________________________________________________________________
     */


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


    private void showSavedMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Message");
        alert.setMessage("Your travel has been saved!");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
//                Intent intent = new Intent();
//                intent.putExtra("addedTravel", true);
//                setResult(RESULT_OK, intent);
//                finish();
                mainActivity.goToMyProfile();
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
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
        Log.e("AddActivity", "setPhotoVisible-" + visiblePhoto);
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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
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
        if (resultCode == getActivity().RESULT_CANCELED) {
            setPhotoVisible(false);
            return;
        }
        if (requestCode == Consts.REQUEST_GALLERY) {
            if (data != null) {
//                photoActionLayout.setVisibility(View.VISIBLE);
//                image.setVisibility(View.VISIBLE);
                deletePhotoBtn.setVisibility(View.VISIBLE);

                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);

                    photoUri = ImageFilePath.getPath(getActivity(), contentURI);
                    Log.e("photoURI", "" + photoUri);
                    Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    image.setImageBitmap(bitmap);
                    setPhotoVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == Consts.REQUEST_CAMERA) {
//            photoActionLayout.setVisibility(View.VISIBLE);
//            image.setVisibility(View.VISIBLE);
//            addPhotoBtn.setVisibility(View.INVISIBLE);
            deletePhotoBtn.setVisibility(View.VISIBLE);
            bitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitmap);
            photoUri = saveImage(bitmap);
            setPhotoVisible(true);
            Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
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
            MediaScannerConnection.scanFile(getActivity(),
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Error occured! Go back to previous screen")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        onBackPressed();
                    }
                });
    }

    public void showCompleteMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.AlertDialogUnfollowTheme);
        alert.setTitle("Message");
        alert.setMessage("Your travel has been added!");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                mainActivity.goToMyProfile();
            }
        });
        alert.show();
    }


    private void setToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ImageView toolbarMenuIcon = (ImageView) toolbar.findViewById(R.id.toolbar_menu);

        toolbarMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                saveTravel();
            }
        });

//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//

}
