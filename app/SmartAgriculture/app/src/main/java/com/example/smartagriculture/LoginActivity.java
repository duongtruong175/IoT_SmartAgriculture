package com.example.smartagriculture;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartagriculture.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    TextView tvRegister;
    Button btnLogin;
    boolean doubleBackToExitPressedOnce = false;
    JSONObject db2Credential;
    String host, service;
    String accessToken, id; //id la ma de lay ket qua viec thuc hien cau lenh sql
    int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // lay cac bien duoc truyen tu activity qua lop Intent
        Bundle bundle = getIntent().getExtras();
        boolean isGetAccessToken = false;
        if (bundle != null) { // neu da lay access token tu truoc
            accessToken = bundle.getString("accessToken");
            host = bundle.getString("host");
            if (accessToken != null && host != null) { // fix bug tren may that
                isGetAccessToken = true;
            }
        }
        // neu activity chay lan dau -> lay access token 1 lan duy nhat
        if (isGetAccessToken == false) {
            // thong so ket noi Db2
            try {
                String db2id = "{\n" +
                        "  \"db\": \"BLUDB\",\n" +
                        "  \"dsn\": \"DATABASE=BLUDB;HOSTNAME=dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net;PORT=50000;PROTOCOL=TCPIP;UID=jcf39976;PWD=xxjcth98lb@q6rtd;\",\n" +
                        "  \"host\": \"dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net\",\n" +
                        "  \"hostname\": \"dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net\",\n" +
                        "  \"https_url\": \"https://dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net\",\n" +
                        "  \"jdbcurl\": \"jdbc:db2://dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net:50000/BLUDB\",\n" +
                        "  \"parameters\": {},\n" +
                        "  \"password\": \"xxjcth98lb@q6rtd\",\n" +
                        "  \"port\": 50000,\n" +
                        "  \"ssldsn\": \"DATABASE=BLUDB;HOSTNAME=dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net;PORT=50001;PROTOCOL=TCPIP;UID=jcf39976;PWD=xxjcth98lb@q6rtd;Security=SSL;\",\n" +
                        "  \"ssljdbcurl\": \"jdbc:db2://dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net:50001/BLUDB:sslConnection=true;\",\n" +
                        "  \"uri\": \"db2://jcf39976:xxjcth98lb%40q6rtd@dashdb-txn-sbox-yp-dal09-04.services.dal.bluemix.net:50000/BLUDB\",\n" +
                        "  \"username\": \"jcf39976\"\n" +
                        "}";
                db2Credential = new JSONObject(db2id);

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
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                //tao json object request voi method GET
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, host + service, userInfo,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (statusCode == 200) {
                                    try {
                                        accessToken = response.getString("token");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // response loi 400 401 403 404
                        Toast.makeText(getApplicationContext(), "Request API Db2 thất bại, hãy khởi động lại ứng dụng", Toast.LENGTH_SHORT).show();
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
        }

        // an thanh ActionBar
        ActionBar ab = getSupportActionBar();
        ab.hide();

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accessToken != null && host != null) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("accessToken", accessToken);
                    bundle.putString("host", host);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, hãy khởi động lại ứng dụng", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (username.length() == 0) {
                    etUsername.requestFocus();
                    etUsername.setError("Dữ liệu không được để trống");
                } else if (password.length() == 0) {
                    etPassword.requestFocus();
                    etPassword.setError("Dữ liệu không được để trống");
                } else { // lay duoc du lieu

                    // view ProgressBar when loading API
                    findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);

                    // tao cau lenh sql va gui den API SQL Statements
                    String sql = "SELECT * FROM \"User\" WHERE \"username\" = '" + username + "' AND \"password\" = '" + password + "';";
                    try {
                        // tao object gui di
                        JSONObject sqlInfo = new JSONObject();
                        sqlInfo.put("commands", sql);
                        sqlInfo.put("limit", 1000);
                        sqlInfo.put("separator", ";");
                        sqlInfo.put("stop_on_error", "yes");

                        // header chua access token
                        String auth_header = "Bearer " + accessToken;

                        // service thuc hien sql
                        service = "/sql_jobs";

                        // call API thuc hien sql
                        //khoi tao RequestQueue
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        //tao json object request voi method POST
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, host + service, sqlInfo,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (statusCode == 201) {
                                            try {
                                                id = response.getString("id");
                                                // tu id lay duoc o tren tiep tuc goi API de lay ket qua truy van
                                                //khoi tao RequestQueue
                                                RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
                                                //tao json object request voi method GET
                                                JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, host + service + "/" + id, null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                if (statusCode == 200) {
                                                                    try {
                                                                        JSONArray results = response.getJSONArray("results");
                                                                        JSONObject dataObject = results.getJSONObject(0);

                                                                        if (!dataObject.has("error") && dataObject.has("rows_count")) { // neu thuc hien cau lenh sql khong co error
                                                                            // lay danh sach du lieu
                                                                            int rowsCount = dataObject.getInt("rows_count"); // so luong ban ghi tra ve boi select
                                                                            if (rowsCount == 1) {
                                                                                JSONArray jsonArrayRows = dataObject.getJSONArray("rows"); // result dang [["1","a",...],["2","b",...]]
                                                                                JSONArray row = jsonArrayRows.getJSONArray(0); // lay ra 1 dong ["1","a",...]
                                                                                // id | username | password | name | email | gender | date
                                                                                int userID = row.getInt(0);
                                                                                String user = row.getString(1);
                                                                                String pass = row.getString(2);
                                                                                String name = row.getString(3);
                                                                                String email = row.getString(4);
                                                                                String gender = row.getString(5);
                                                                                String date = row.getString(6);
                                                                                UserModel acc = new UserModel(userID, user, pass, name, email, gender, date);

                                                                                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                                Bundle bundle = new Bundle();
                                                                                bundle.putString("accessToken", accessToken);
                                                                                bundle.putString("host", host);
                                                                                bundle.putInt("userId", acc.getId());
                                                                                intent.putExtras(bundle);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            } else { // van co result nhung result trong
                                                                                Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        } else { // cau lenh sql bi loi
                                                                            Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, xin thử lại", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // response loi 400 401 403 404
                                                        Toast.makeText(getApplicationContext(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
                                                        headers.put("Authorization", auth_header);
                                                        return headers;
                                                    }
                                                };
                                                //them request vao RequestQueue , no se tu dong duoc chay
                                                requestQueue1.add(request1);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                        }

                                        // hide ProgressBar when loading API complete
                                        findViewById(R.id.pb_loading).setVisibility(View.GONE);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // hide ProgressBar when loading API complete
                                findViewById(R.id.pb_loading).setVisibility(View.GONE);
                                // response loi 400 401 403 404
                                Toast.makeText(getApplicationContext(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
                                headers.put("Authorization", auth_header);
                                return headers;
                            }
                        };
                        //them request vao RequestQueue , no se tu dong duoc chay
                        requestQueue.add(request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

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
}