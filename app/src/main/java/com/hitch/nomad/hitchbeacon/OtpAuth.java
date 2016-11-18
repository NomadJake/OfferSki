package com.hitch.nomad.hitchbeacon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtpAuth extends AppCompatActivity {

    private static final String REGISTER_URL = "http://138.197.198.152/android_sms/request_sms.php";
    public EditText editTextMobile,editTextOtp;
    public Button verifyButton, proceedButton;
    private String urlVerifyOtp = "http://138.197.198.152/android_sms/verify_otp.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);
        editTextMobile = (EditText) findViewById(R.id.editTextMobil);
        verifyButton = (Button) findViewById(R.id.buttonSubmitMobil);
        proceedButton = (Button) findViewById(R.id.buttonVerify);
        editTextOtp = (EditText)findViewById(R.id.editTextVerify);
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
    }

    private void registerUser() {

        final String mobile = editTextMobile.getText().toString().trim();

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
                params.put("name", "ofrskyuser");
                params.put("email", "ofrskyuser@hitch.com");
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
                try {
                    response = new JSONObject(responseString);
                    Log.d("json",response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String error = response.getString("error");
                    if (error.equals("true")) {
                        Toast.makeText(OtpAuth.this, "Wrong OTP", Toast.LENGTH_LONG).show();
                    } else {

                        JSONObject userJsonobject = response.getJSONObject("profile");
                        String name = userJsonobject.getString("name");
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
