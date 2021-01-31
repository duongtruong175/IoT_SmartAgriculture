package com.example.smartagriculture;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    JSONObject db2Credential;
    String host, service;
    String accessToken;
    int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // an thanh ActionBar
        ActionBar ab = getSupportActionBar();
        ab.hide();

        if (isNetworkConnected()) {
            String url = "https://smartagriculture-nhom16.mybluemix.net/api/Db2";
            try {
                // object gui kem de lay chuoi ket noi db2
                JSONObject key = new JSONObject();
                key.put("userkey", "nhom16");
                key.put("passwordkey", "nhom16");

                // lay chuoi ket noi db2
                //khoi tao RequestQueue
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                //tao json object request voi method POST
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, key,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (statusCode == 200) {
                                    try {
                                        db2Credential = response.getJSONObject("db2Credential");
                                        // ghep thanh chuoi url cua api
                                        String api = "/dbapi/v4";
                                        host = db2Credential.getString("https_url") + api;

                                        // object gui kem de lay Access Token
                                        JSONObject userInfo = new JSONObject();
                                        userInfo.put("userid", db2Credential.getString("username"));
                                        userInfo.put("password", db2Credential.getString("password"));

                                        // service lay access token
                                        service = "/auth/tokens";

                                        // call API Get access token
                                        //khoi tao RequestQueue
                                        RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
                                        //tao json object request voi method POST
                                        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.POST, host + service, userInfo,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        if (statusCode == 200) {
                                                            try {
                                                                accessToken = response.getString("token");
                                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("accessToken", accessToken);
                                                                bundle.putString("host", host);
                                                                intent.putExtras(bundle);
                                                                startActivity(intent);
                                                                finish();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            showErrorDialog();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // response loi 400 401 403 404
                                                showErrorDialog();
                                            }
                                        }
                                        ) {
                                            @Override
                                            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                                statusCode = response.statusCode;
                                                return super.parseNetworkResponse(response);
                                            }

                                            @Override
                                            public Map getHeaders() throws AuthFailureError {
                                                HashMap headers = new HashMap();
                                                headers.put("Content-Type", "application/json");
                                                return headers;
                                            }
                                        };
                                        //them request vao RequestQueue , no se tu dong duoc chay
                                        requestQueue2.add(request2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // response loi 401
                        showErrorDialog();
                    }
                }
                ) {
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        statusCode = response.statusCode;
                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public Map getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                //them request vao RequestQueue , no se tu dong duoc chay
                requestQueue.add(request);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else { // khong bat ket noi internet
            showErrorDialog();
        }

    }

    // kiem tra may co ket noi internet hay khong
    private boolean isNetworkConnected() {
        Context context = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // an 2 lan back lien tiep de thoat
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn BACK một lần nữa để thoát", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void showErrorDialog() {
        final Dialog customDialog = new Dialog(MainActivity.this);
        customDialog.setContentView(R.layout.dialog_error);
        customDialog.findViewById(R.id.txt_reconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
    }

}