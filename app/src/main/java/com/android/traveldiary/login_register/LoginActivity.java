package com.android.traveldiary.login_register;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.android.traveldiary.R;
import com.android.traveldiary.activites.MainActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    TextView linkForgotPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
//                .getColor(R.color.white)));
        this.setTitle("");
        if (isTokenSaved()) {
            Log.i("LoginActivity", "token already saved - user logged in");
            goToMainView();
        }
        values();
    }

    public void values() {
        etEmail = (EditText) findViewById(R.id.email_log);
        etPassword = (EditText) findViewById(R.id.password_log);
        btnLogin = (Button) findViewById(R.id.login_btn);
        btnRegister = (Button) findViewById(R.id.register_btn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
//                                String resp = jsonResponse.toString();
                                JSONObject dataObject = jsonResponse.getJSONObject("data");
                                Log.e("LoginActivity", "" + dataObject.toString());
                                String name = dataObject.getString("name");
                                String username = dataObject.getString("username");
                                String token = dataObject.getString("token");
                                int id = dataObject.getInt("id");
                                saveLoginToken(token, username, id, name);
                                goToMainView();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                int messageID = R.string.error_login;
                                int negativeButtonLoginID = R.string.negative_login;
                                String message = getResources().getString(messageID);
                                String negativeButtonMessage = getResources().getString(negativeButtonLoginID);
                                builder.setMessage(message)
                                        .setNegativeButton(negativeButtonMessage, null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    private void goToMainView() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void saveLoginToken(String token, String login, int id,String name) {
        SharedPreferences preferences = getSharedPreferences("appPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.putString("login", login);
        editor.putString("username", login);
        editor.putString("name", name);
        editor.putInt("id",id);
        editor.commit();
        Log.i("LoginActivity.saveToken()", "token saved");
    }

    private boolean isTokenSaved() {
        SharedPreferences preferences = this.getSharedPreferences("appPref", MODE_PRIVATE);
        String token = preferences.getString("token", "");
        Log.e("LoginActivity", "token: " + token);
        return !token.equalsIgnoreCase("");
    }
}
