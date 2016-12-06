package com.hitch.nomad.hitchbeacon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitch.nomad.hitchbeacon.Hitchbeacon.context;

public class OtpAuth extends AppCompatActivity {

    User user;

    private static final String REGISTER_URL = "http://138.68.81.101/exc/sendOTP";
    public EditText editTextMobile,editTextOtp, editTextmf,editTextage;
    public Button verifyButton, proceedButton;
    private String urlVerifyOtp = "http://138.68.81.101/exc/verifyOTP";
    private DatabaseReference mDatabase;
    private RadioButton female;
    public EditText nameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);
        editTextMobile = (EditText) findViewById(R.id.editTextMobil);
        nameET = (EditText)findViewById(R.id.editTextName);
        editTextage = (EditText) findViewById(R.id.editTextAge);
        verifyButton = (Button) findViewById(R.id.buttonSubmitMobil);
        proceedButton = (Button) findViewById(R.id.buttonVerify);
        editTextOtp = (EditText)findViewById(R.id.editTextVerify);
        female = (RadioButton)findViewById(R.id.femaleRB);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


    }

    private void registerUser() {

        final String mobile = editTextMobile.getText().toString().trim();
        final String mf;// = editTextmf.getText().toString().trim();

        if(female.isChecked()){
            mf = "f";
        }else {
            mf = "m";
        }
        final String age = editTextage.getText().toString().trim();
        final String name = nameET.getText().toString();
        user = new User(mobile,age,mf,name);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(OtpAuth.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OtpAuth.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mf", mf);
                params.put("age", age);
                params.put("mobile", mobile);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void verifyOtp() {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                 urlVerifyOtp,new Response.Listener<String>() {

            @Override
            public void onResponse(String responseString) {
                Log.d("verifyOtp", responseString.toString());
                JSONObject response = null;
                Boolean success;
                try {
                    response = new JSONObject(responseString);
                    success = response.getBoolean("success");
                    Log.d("json",response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    try {
                            Boolean successB = response.getBoolean("success");
                        if (successB==true) {
                            Hitchbeacon.user = user;
                            Hitchbeacon.setListners();
                            mDatabase.child("users").child(user.email).setValue(user);
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            sharedPreferences.edit().putString("email",user.email).apply();
                            Hitchbeacon.loggedin=true;
                            Hitchbeacon.setLoggedin();
                            startActivity(new Intent(OtpAuth.this, IconTabsActivity.class));
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("otp", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp",editTextOtp.getText().toString());
                return params;
            }
        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }
}
