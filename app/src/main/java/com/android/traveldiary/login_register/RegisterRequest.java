package com.android.traveldiary.login_register;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://api.travellist.tk/api/register";
    private Map<String, String> params;

    public RegisterRequest(String email, String username, String password, String name,
                           Response.Listener<String> listener, Response.ErrorListener error) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, error);
        params = new HashMap<>();
        params.put("email",email);
        params.put("username",username);
        params.put("password",password);
        params.put("c_password",password);
        params.put("name",name);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("Content-Type","application/x-www-form-urlencoded");
        return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
    }
    @Override
    public String getBodyContentType() {
        return "application/json";
    }
}
