package com.android.traveldiary.traveladds;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.database.DatabaseHelper;
import com.android.traveldiary.dummy.ImageFilePath;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class AddPhotoActivity extends AppCompatActivity {
    ImageView image;
    EditText titleET, dateET;
    LinearLayout photoActionLayout;

    int REQUEST_PICTURE_CAPTURE = -1;
    String photoFilePath, photoUri;
    MaterialButton addPhotoBtn, deletePhotoBtn;
    int travelID, position;
    String date;
    long startDate, endDate;
    int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        setViews();
        showPictureDialog();
        dateTimeHandler();
        setPhotoVisible(false);
    }

    private void setViews() {
        image = (ImageView) findViewById(R.id.travel_image);
        photoActionLayout = (LinearLayout)findViewById(R.id.layout_photo_action);
        addPhotoBtn = (MaterialButton) findViewById(R.id.new_photo_btn);
        deletePhotoBtn = (MaterialButton) findViewById(R.id.delete_photo_btn);

        dateET = (EditText) findViewById(R.id.input_date);
        titleET = (EditText) findViewById(R.id.input_title);

        Intent intent = this.getIntent();
        travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);
        date = intent.getStringExtra(Consts.STRING_CURRENT_DATE);
        dateET.setText(date);
        position = intent.getIntExtra(Consts.STRING_ENTRY_POSITION, -1);
        startDate = intent.getLongExtra(Consts.LONG_START_DATE,-1);
        endDate = intent.getLongExtra(Consts.LONG_END_DATE,-1);

        MaterialButton saveBtn = (MaterialButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

//      todo  position = intent.getIntExtra(Consts.STRING_ENTRY_POSITION,-1);

        if (travelID == -1 || position == -1) {
            Toast.makeText(getApplicationContext(), "Error occured: AddTransport", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        if (!date.matches("")) {
            day = Integer.parseInt(date.substring(0, 2)); // dd-MM-yyyy
            month = Integer.parseInt(date.substring(3, 5));
            year = Integer.parseInt(date.substring(6));
        } else{
            Toast.makeText(AddPhotoActivity.this,"Date error",Toast.LENGTH_SHORT).show();
        }


        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
    }

    public boolean onButtonClick(View item) {
        switch(item.getId()) {
            case R.id.new_photo_btn:
                showPictureDialog();
                return true;

            case R.id.delete_photo_btn:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete photo");
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


    File getPhotoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "ZOFTINO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        photoFilePath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPhotoFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.zoftino.android.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
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
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    photoUri = ImageFilePath.getPath(AddPhotoActivity.this, contentURI);
                    Log.e("photoURI",""+photoUri);
                    Toast.makeText(AddPhotoActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image.setImageBitmap(bitmap);
                    setPhotoVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPhotoActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Consts.REQUEST_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(thumbnail);
            image.setVisibility(View.VISIBLE);
            photoUri = saveImage(thumbnail);
            setPhotoVisible(true);
            Toast.makeText(AddPhotoActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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

    private void showSavedMessage(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Message");
        alert.setMessage("Your photo has been saved!");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        alert.show();
    }

    public void save(){
        if(photoUri.matches("")){
            Toast.makeText(AddPhotoActivity.this, "You haven't chosen any photo to add", Toast.LENGTH_SHORT).show();
        }
        else{
            Photo p = new Photo(getUniqueID(),titleET.getText().toString(), photoUri,date,position,travelID);

            DatabaseHelper helper = new DatabaseHelper(AddPhotoActivity.this);
            helper.addEntry(p);
            showSavedMessage();
        }
    }


    public long getUniqueID(){
        return System.currentTimeMillis();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
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
                DatePickerDialog picker = new DatePickerDialog(AddPhotoActivity.this, dateListener, year, month, day);
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
