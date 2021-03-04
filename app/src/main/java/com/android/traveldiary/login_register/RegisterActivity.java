package com.android.traveldiary.login_register;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.traveldiary.R;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText et_username, et_password, et_email, et_name, et_c_password;
    Button registerBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
//                .getColor(R.color.white)));
        this.setTitle("");
        values();

//        for(int i=0; i<names.length;i++) {
//            register(i,RegisterActivity.this);
//            i++;
//        }
    }

    public void values(){
        et_username = (EditText)findViewById(R.id.username_reg);
        et_password = (EditText)findViewById(R.id.password_reg);
        et_c_password = (EditText)findViewById(R.id.c_password_reg);
        et_email = (EditText)findViewById(R.id.email_reg);
        et_name = (EditText)findViewById(R.id.name_reg);
        registerBtn = (Button)findViewById(R.id.register_btn);
        loginBtn =  (Button)findViewById(R.id.login_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = et_email.getText().toString();
                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();
                final String cpassword = et_c_password.getText().toString();
                final String name = et_name.getText().toString();

                if(!password.matches(cpassword)){
                    Toast.makeText(getApplicationContext(),"Passwords must be the same",Toast.LENGTH_SHORT).show();
                }
                else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                    RegisterRequest registerRequest = new RegisterRequest(email, username, password, name, responseListener, null);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }







//
//    public static void register(int j, RegisterActivity activity) {
//        final String name = names[j]+" "+names[j+1];
//        final String username = names[j].toLowerCase()+"_"+names[j+1].toLowerCase();
//        final String email = username+"@test.com";
//        final String password = "123456789";
//        final String cpassword = "123456789";
//        System.out.println(name + " "+username+ " "+email);
//
//
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonResponse = new JSONObject(response);
//                    boolean success = jsonResponse.getBoolean("success");
//                    if (success) {
//                        System.out.println(jsonResponse);
//                    } else {
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        RegisterRequest registerRequest = new RegisterRequest(email, username, password, name, responseListener, null);
//        RequestQueue queue = Volley.newRequestQueue(activity);
//        queue.add(registerRequest);
//    }
//
//
//    static String[] names = new String[]{
//            "Fenton", "Riley",
//            "Dale", "Evans",
//            "Mike", "Johnston",
//            "Thomas", "Evans",
//            "Martin", "Bennett",
//            "Fiona", "Moore",
//            "Daniel", "Johnson",
//            "Vanessa", "West",
//            "Alisa", "Brooks",
//            "George", "Jones",
//            "Aiden", "Bennett",
//            "Honey", "Armstrong",
//            "Kate", "Dixon",
//            "Vanessa", "Harrison",
//            "Dominik", "Murray",
//            "Henry", "Gibson",
//            "Oliver", "Adams",
//            "Owen", "Douglas",
//            "Eddy", "Ferguson",
//            "Sophia", "Brooks",
//            "Brad", "Hunt",
//            "Dale", "Murray",
//            "Darcy", "Lloyd",
//            "Miley", "Barnes",
//            "Adison", "Farrell",
//            "Charlotte", "Hawkins",
//            "Aldus", "Parker",
//            "Julia", "Payne",
//            "Adison", "Murray",
//            "Tiana", "Robinson",
//            "Lilianna", "Gibson",
//            "Kirsten", "Warren",
//            "Mike", "Richards",
//            "Gianna", "Thompson",
//            "Kelsey", "Foster",
//            "Oscar", "Andrews",
//            "Brooke", "Cameron",
//            "Rebecca", "Crawford",
//            "Michelle", "Edwards",
//            "Amanda", "Douglas",
//            "Reid", "Richardson",
//            "Chloe", "Carter",
//            "Frederick", "Craig",
//            "Jenna", "Baker",
//    };



}


//    Response.ErrorListener errorListener = new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            Log.e("Volly Error", error.toString());
////                        Log.e("Volly Error", error.toString());
//            error.printStackTrace();
//
//            NetworkResponse networkResponse = error.networkResponse;
//            if (networkResponse != null) {
//                Log.e("Status code", String.valueOf(networkResponse.statusCode));
//            }
//
//
//            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                Toast.makeText(getApplicationContext(), "Network timeout",
//                        Toast.LENGTH_LONG).show();
//            } else if (error instanceof AuthFailureError) {
//                Log.e("RegisterActivity", "AuthFailureError");
//                //TODO
//            } else if (error instanceof ServerError) {
//                //TODO
//                Log.e("RegisterActivity", "ServerError");
//            } else if (error instanceof NetworkError) {
//                //TODO
//                Log.e("RegisterActivity", "NetworkError");
//            } else if (error instanceof ParseError) {
//                //TODO
//                Log.e("RegisterActivity", "ParseError");
//            }
//        }
//    };

