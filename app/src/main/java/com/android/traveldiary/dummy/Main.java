package com.android.traveldiary.dummy;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Toast;

import com.android.traveldiary.diaryentries.Note;
import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.login_register.LoginActivity;
import com.android.traveldiary.login_register.RegisterActivity;
import com.android.traveldiary.login_register.RegisterRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Main {


    static String[] names = new String[]{"Kevin", "Casey",
            "Fenton", "Miller",
            "Emma", "Foster",
            "Natalie", "Nelson",
            "Jordan", "Armstrong",
            "Sam", "Baker",
            "Fenton", "Riley",
            "Dale", "Evans",
            "Mike", "Johnston",
            "Thomas", "Evans",
            "Martin", "Bennett",
            "Fiona", "Moore",
            "Daniel", "Johnson",
            "Vanessa", "West",
            "Alisa", "Brooks",
            "George", "Jones",
            "Aiden", "Bennett",
            "Honey", "Armstrong",
            "Kate", "Dixon",
            "Vanessa", "Harrison",
            "Dominik", "Murray",
            "Henry", "Gibson",
            "Oliver", "Adams",
            "Owen", "Douglas",
            "Eddy", "Ferguson",
            "Sophia", "Brooks",
            "Brad", "Hunt",
            "Dale", "Murray",
            "Darcy", "Lloyd",
            "Miley", "Barnes",
            "Adison", "Farrell",
            "Charlotte", "Hawkins",
            "Aldus", "Parker",
            "Julia", "Payne",
            "Adison", "Murray",
            "Tiana", "Robinson",
            "Lilianna", "Gibson",
            "Kirsten", "Warren",
            "Mike", "Richards",
            "Gianna", "Thompson",
            "Kelsey", "Foster",
            "Oscar", "Andrews",
            "Brooke", "Cameron",
            "Rebecca", "Crawford",
            "Michelle", "Edwards",
            "Amanda", "Douglas",
            "Reid", "Richardson",
            "Chloe", "Carter",
            "Frederick", "Craig",
            "Jenna", "Baker",
    };

    public static void main(String[] args) {
//        Photo photo = new Photo(1, "", "", "2020-05-10T00:00:00.000000Z", 0, 1);
//        Note note = new Note(1, "", "", "2020-05-11T00:00:00.000000Z", 0, 1);
//        System.out.println(photo.compareTo(note));

//        RegisterActivity activity = new RegisterActivity();
        for(int i=0; i<names.length;i++) {
            register(i,null);
            i++;
        }
    }

    public static void register(int j, RegisterActivity activity) {
        final String name = names[j]+" "+names[j+1];
        final String username = names[j].toLowerCase()+"_"+names[j+1].toLowerCase();
        final String email = username+"@test.com";
        final String password = "123456789";
        final String cpassword = "123456789";
        System.out.println(name + " "+username+ " "+email);


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
    }
}
