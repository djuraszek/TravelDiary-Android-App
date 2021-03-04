package com.android.traveldiary.traveladds;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.traveldiary.R;
import com.android.traveldiary.database.Consts;
import com.android.traveldiary.serverrequests.PostCreateNoteRequest;
import com.android.traveldiary.serverrequests.PutUpdateNoteRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EditNoteActivity extends AppCompatActivity {
    private static String TAG = "AddNoteActivity";

    EditText titleET, noteET;
    private ImageView saveButton;


    int travelID,noteID;
    String date;
    String token;
    int userID;
//

//    int day, month, year;
//    long startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);


        setViews();
        getIntentExtras();
//        dateTimeHandler();
        setToolbar();
    }

    public void setViews() {
        titleET = findViewById(R.id.input_title);
        noteET = findViewById(R.id.input_note);
        saveButton = findViewById(R.id.toolbar_menu);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    public void getIntentExtras() {
        Intent intent = this.getIntent();
        travelID = intent.getIntExtra(Consts.STRING_TRAVEL_ID, -1);
        date = intent.getStringExtra("date").substring(0,10);
        token = intent.getStringExtra("token");

        noteID = intent.getIntExtra("noteID",-1);
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");

        if(!title.equals("null")) titleET.setText(title);
        noteET.setText(note);

        Log.i("EditNoteActivity.getIntentExtras()","travelID "+travelID+" date "+date+"token:"+token);

        if (travelID == -1 || noteID == -1) {
            Toast.makeText(getApplicationContext(), "Error occured: AddTransport", Toast.LENGTH_SHORT).show();
            onBackPressed();
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
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void showSavedMessage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialogUnfollowTheme);
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
            String title = titleET.getText().toString();
            String note = noteET.getText().toString();

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            showSavedMessage(); }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            PutUpdateNoteRequest request = new PutUpdateNoteRequest(token, noteID, title, note, date, responseListener, null);
            Log.i("AddNoteActivity", "request body"+request.getParams().toString());

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);
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


    private void addNoteToServer() {

    }

//    new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            //   Handle Error
//            Log.d("AddNoteActivity", "Error: " + error
//                    + "\nStatus Code " + error.networkResponse.statusCode
//                    + "\nResponse Data " + error.networkResponse.data
//                    + "\nCause " + error.getCause()
//                    + "\nmessage" + error.getMessage());
//
//            Log.d(TAG, "Failed with error msg:\t" + error.getMessage());
//            Log.d(TAG, "Error StackTrace: \t" + error.getStackTrace());
//            // edited here
//            try {
//                byte[] htmlBodyBytes = error.networkResponse.data;
//                Log.e(TAG, new String(htmlBodyBytes), error);
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//            if (error.getMessage() == null){
//                Log.d(TAG, "null" );
//            }
//        }
//    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ImageView toolbarMenuIcon = (ImageView) toolbar.findViewById(R.id.toolbar_menu);

        toolbarMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
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
}
