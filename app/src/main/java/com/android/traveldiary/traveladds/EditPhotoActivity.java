package com.android.traveldiary.traveladds;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.EditActivity;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.dummy.ImageFilePath;
import com.android.traveldiary.serverrequests.PutUpdatePhotoRequest;
import com.android.traveldiary.serverrequests.VolleyMultipartRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class EditPhotoActivity extends AppCompatActivity {
    private String TAG = "AddPhotoActivity";

    ImageView image;
    EditText titleET;
    //    EditText dateET;
    LinearLayout photoActionLayout;

    int REQUEST_PICTURE_CAPTURE = -1;
    String photoFilePath, photoUri;
    MaterialButton addPhotoBtn, deletePhotoBtn;
    int travelID, photoID;
    String date, title;

    Bitmap originalPhotoBitmap, newBitmap;

    String token;
    Bitmap bitmap;
    String photoURL;

    boolean photoChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        setViews();

        setPhotoVisible(true);
        setToolbar();
    }

    private void setViews() {
        image = (ImageView) findViewById(R.id.travel_image);
        photoActionLayout = (LinearLayout) findViewById(R.id.layout_photo_action);
        addPhotoBtn = (MaterialButton) findViewById(R.id.new_photo_btn);
        deletePhotoBtn = (MaterialButton) findViewById(R.id.delete_photo_btn);

        titleET = (EditText) findViewById(R.id.input_title);

        Intent intent = this.getIntent();
        travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);

        token = intent.getStringExtra("token");
        photoID = intent.getIntExtra("photoID", -1);
        photoURL = intent.getStringExtra("photoPath");
        title = intent.getStringExtra("title");
        date = intent.getStringExtra("date");

        new EditPhotoActivity.GetImageFromUrl(image, this).execute(photoURL);
        setPhotoVisible(true);

//      todo  position = intent.getIntExtra(Consts.STRING_ENTRY_POSITION,-1);

        if (travelID == -1 || photoID == -1) {
            Toast.makeText(getApplicationContext(), "Error occured: AddTransport", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
    }

    public boolean onButtonClick(View item) {
        switch (item.getId()) {
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
                        photoChanged = false;
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, Consts.REQUEST_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    photoUri = ImageFilePath.getPath(EditPhotoActivity.this, contentURI);
                    Log.e("photoURI", "" + photoUri);
                    Toast.makeText(EditPhotoActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image.setImageBitmap(bitmap);
                    this.newBitmap = bitmap;
                    photoChanged = true;
                    setPhotoVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditPhotoActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Consts.REQUEST_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(thumbnail);
            image.setVisibility(View.VISIBLE);
            photoUri = saveImage(thumbnail);
            this.newBitmap = thumbnail;
            photoChanged = true;
            setPhotoVisible(true);
            Toast.makeText(EditPhotoActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
        alert.setMessage("Your photo has been updated!");
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
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogUnfollowTheme);
        alert.setTitle("Message");
        alert.setMessage("Your photo has been saved!");
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
        //todo
//        newBitmap = ((BitmapDrawable) image.getDrawable().getCurrent()).getBitmap();
//        title = titleET.getText().toString();
        String UPDATE_PHOTO_URL = "https://travellist.mitimise.tk/api/photos/";
        String METHOD_PUT = "?_method=put";

        String URL = UPDATE_PHOTO_URL + photoID + METHOD_PUT;

        if (newBitmap == null) {
            Toast.makeText(EditPhotoActivity.this, "You haven't chosen any photo to add", Toast.LENGTH_SHORT).show();
        } else {
            final String title = titleET.getText().toString();
            final String date = this.date;

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
//                                    updateTravel();
                                }
                                Toast.makeText(EditPhotoActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
            Volley.newRequestQueue(EditPhotoActivity.this).add(volleyMultipartRequest);
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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


    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Edit Photo");
        ImageView toolbarMenuIcon = (ImageView) toolbar.findViewById(R.id.toolbar_save);

        toolbarMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditPhotoActivity.this.onBackPressed();
            }
        });
    }


    /** ------------------------------------------------------------------------------------
     *                      GET IMAGE FROM URL CLASS
     *  ------------------------------------------------------------------------------------
     */
    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap bitmap;
        EditPhotoActivity activity;

        public GetImageFromUrl(ImageView img, EditPhotoActivity activity) {
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
            activity.newBitmap = bitmap;
        }
    }
}