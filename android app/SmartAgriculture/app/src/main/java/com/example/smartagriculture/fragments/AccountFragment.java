package com.example.smartagriculture.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
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
import com.example.smartagriculture.LoginActivity;
import com.example.smartagriculture.R;
import com.example.smartagriculture.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {

    String host, service;
    String accessToken, id; //id la ma de lay ket qua viec thuc hien cau lenh sql
    int userId;
    int statusCode;

    TextView tvUsername, tvName, tvEmail, tvGender, tvDate, tvUpdate, tvChangePassword, tvLogout;
    View loading;

    UserModel acc;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // lay du lieu tu MainActivity truyen qua
        accessToken = getArguments().getString("accessToken");
        host = getArguments().getString("host");
        userId = getArguments().getInt("userId");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tvUsername = view.findViewById(R.id.tv_value_username);
        tvName = view.findViewById(R.id.tv_value_name);
        tvEmail = view.findViewById(R.id.tv_value_email);
        tvGender = view.findViewById(R.id.tv_value_gender);
        tvDate = view.findViewById(R.id.tv_value_date);
        tvUpdate = view.findViewById(R.id.tv_update);
        tvChangePassword = view.findViewById(R.id.tv_value_password);
        tvLogout = view.findViewById(R.id.tv_value_logout);
        loading = view.findViewById(R.id.pb_loading);

        // lay du lieu nguoi dung
        loading.setVisibility(View.VISIBLE);
        // tao cau lenh sql va gui den API SQL Statements
        String sql = "SELECT * FROM \"User\" WHERE \"id\" = " + userId + ";";
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
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                                    RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
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
                                                                    String username = row.getString(1);
                                                                    String password = row.getString(2);
                                                                    String name = row.getString(3);
                                                                    String email = row.getString(4);
                                                                    String gender = row.getString(5);
                                                                    String date = row.getString(6);
                                                                    acc = new UserModel(userId, username, password, name, email, gender, date);

                                                                    // load du lieu cho view
                                                                    tvUsername.setText(username);
                                                                    tvName.setText(name);
                                                                    tvEmail.setText(email);
                                                                    tvGender.setText(gender);
                                                                    tvDate.setText(date);

                                                                } else { // van co result nhung result trong
                                                                    Toast.makeText(getActivity(), "Có lỗi xảy ra, xin thử lại", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else { // cau lenh sql bi loi
                                                                Toast.makeText(getActivity(), "Có lỗi xảy ra, xin thử lại", Toast.LENGTH_SHORT).show();
                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Toast.makeText(getActivity(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // response loi 400 401 403 404
                                            Toast.makeText(getActivity(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                            }

                            // hide ProgressBar when loading API complete
                            loading.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // hide ProgressBar when loading API complete
                    loading.setVisibility(View.GONE);
                    // response loi 400 401 403 404
                    Toast.makeText(getActivity(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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

        // thay doi ten
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInforDialog(tvName);
            }
        });

        // thay doi email
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInforDialog(tvEmail);
            }
        });

        // thay doi gioi tinh
        tvGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderDialog(tvGender);
            }
        });

        // thay doi date
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tvDate);
            }
        });

        // thay doi mat khau
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("accessToken", accessToken);
                bundle.putString("host", host);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().finish();
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update lại du lieu user
                updateInformation();
            }
        });

        return view;
    }

    // ham show dialog thay doi thong tin
    public void showInforDialog(TextView tv) {
        final Dialog customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.dialog_change_information);
        EditText etNewValue = customDialog.findViewById(R.id.et_new_value);
        customDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(customDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                String value = etNewValue.getText().toString().trim();
                if (value.length() == 0) {
                    etNewValue.requestFocus();
                    etNewValue.setError("Dữ liệu không được để trống");
                } else {
                    tv.setText(value);
                    customDialog.dismiss();
                }
            }
        });
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }

    // ham show dialog thay doi gioi tinh
    public void showGenderDialog(TextView tv) {
        final Dialog customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.dialog_change_gender);
        customDialog.findViewById(R.id.txt_male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Male");
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.txt_female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Female");
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.txt_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Other");
                customDialog.dismiss();
            }
        });
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }

    // ham show dialog thay doi date
    public void showDateDialog(TextView tv) {
        String date = tv.getText().toString().trim();
        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int year = Integer.parseInt(parts[2]);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int nYear, int nMonth, int nDay) {
                Calendar c = Calendar.getInstance();
                c.set(nYear, nMonth, nDay);
                Date date = c.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                tv.setText(formatter.format(date));
            }
        }, year, month, day);
        dpd.show();
    }

    // ham show dialog thay doi mat khau
    public void showPasswordDialog() {
        final Dialog customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.dialog_change_password);
        EditText etCurrentPassword = customDialog.findViewById(R.id.et_current_password);
        EditText etNewPassword = customDialog.findViewById(R.id.et_new_password);
        EditText etConformPassword = customDialog.findViewById(R.id.et_conform_password);
        View load = customDialog.findViewById(R.id.pb_loading);
        customDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(customDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                String currentPassword = etCurrentPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String conformPassword = etConformPassword.getText().toString().trim();
                if (currentPassword.length() == 0) {
                    etCurrentPassword.requestFocus();
                    etCurrentPassword.setError("Dữ liệu không được để trống");
                } else if (newPassword.length() == 0) {
                    etNewPassword.requestFocus();
                    etNewPassword.setError("Dữ liệu không được để trống");
                } else if (conformPassword.length() == 0) {
                    etConformPassword.requestFocus();
                    etConformPassword.setError("Dữ liệu không được để trống");
                } else if (!currentPassword.equals(acc.getPassword())) {
                    etCurrentPassword.requestFocus();
                    etCurrentPassword.setError("Mật khẩu không chính xác");
                } else if (!newPassword.equals(conformPassword)) {
                    etConformPassword.requestFocus();
                    etConformPassword.setError("Mật khẩu không khớp");
                } else if (newPassword.equals(currentPassword)) {
                    etNewPassword.requestFocus();
                    etNewPassword.setError("Mật khẩu chưa đổi");
                } else { // thay doi mat khau
                    // view ProgressBar when loading API
                    load.setVisibility(View.VISIBLE);

                    // tao cau lenh sql va gui den API SQL Statements
                    String sql = "UPDATE \"User\" SET \"password\" = '" + newPassword + "' WHERE \"id\" = " + userId + ";";
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
                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                                                RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
                                                //tao json object request voi method GET
                                                JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, host + service + "/" + id, null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                if (statusCode == 200) {
                                                                    try {
                                                                        JSONArray results = response.getJSONArray("results");
                                                                        JSONObject dataObject = results.getJSONObject(0);

                                                                        // du lieu tra ve la ket qua thuc hien cau lenh insert
                                                                        if (!dataObject.has("error") && dataObject.has("rows_affected")) {
                                                                            int rowsAffected = dataObject.getInt("rows_affected"); // so ban ghi thay doi bang cau lenh update
                                                                            if (rowsAffected == 1) {
                                                                                // cap nhap lai gia tri, in thong bao va tat dialog
                                                                                acc.setPassword(newPassword);
                                                                                Toast.makeText(getActivity(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                                                customDialog.dismiss();
                                                                            }
                                                                        } else { // cau lenh sql bi loi
                                                                            Toast.makeText(getActivity(), "Thay đổi mặt khẩu thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getActivity(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // response loi 400 401 403 404
                                                        Toast.makeText(getActivity(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getActivity(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                        }

                                        // hide ProgressBar when loading API complete
                                        load.setVisibility(View.GONE);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // hide ProgressBar when loading API complete
                                load.setVisibility(View.GONE);
                                // response loi 400 401 403 404
                                Toast.makeText(getActivity(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }

    // ham thay doi thong tin nguoi dung
    public void updateInformation() {
        String name = tvName.getText().toString().trim();
        String email = tvEmail.getText().toString().trim();
        String gender = tvGender.getText().toString().trim();
        String date = tvDate.getText().toString().trim();

        if (acc != null && name != null && email != null && gender != null && date != null) {
            // view ProgressBar when loading API
            loading.setVisibility(View.VISIBLE);

            // tao cau lenh sql va gui den API SQL Statements
            String sql = "UPDATE \"User\" SET \"name\" = '" + name + "', \"email\" = '" + email + "', \"gender\" = '" + gender + "', \"date\" = '" + date + "' WHERE \"id\" = " + userId + ";";
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
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                                        RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
                                        //tao json object request voi method GET
                                        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, host + service + "/" + id, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        if (statusCode == 200) {
                                                            try {
                                                                JSONArray results = response.getJSONArray("results");
                                                                JSONObject dataObject = results.getJSONObject(0);

                                                                // du lieu tra ve la ket qua thuc hien cau lenh insert
                                                                if (!dataObject.has("error") && dataObject.has("rows_affected")) {
                                                                    int rowsAffected = dataObject.getInt("rows_affected"); // so hang thay doi bang cau lenh update
                                                                    if (rowsAffected == 1) {
                                                                        // cap nhap lai thong tin cho view
                                                                        acc.setName(name);
                                                                        acc.setEmail(email);
                                                                        acc.setGender(gender);
                                                                        acc.setDate(date);
                                                                        tvName.setText(name);
                                                                        tvEmail.setText(email);
                                                                        tvGender.setText(gender);
                                                                        tvDate.setText(date);
                                                                        Toast.makeText(getActivity(), "Thay đổi thông tin tài khoản thành công", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else { // cau lenh sql bi loi
                                                                    Toast.makeText(getActivity(), "Thay đổi thông tin tài khoản thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
                                                                }

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            Toast.makeText(getActivity(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // response loi 400 401 403 404
                                                Toast.makeText(getActivity(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "Phản hồi API không chính xác, xin thử lại", Toast.LENGTH_SHORT).show();
                                }

                                // hide ProgressBar when loading API complete
                                loading.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // hide ProgressBar when loading API complete
                        loading.setVisibility(View.GONE);
                        // response loi 400 401 403 404
                        Toast.makeText(getActivity(), "Request API Db2 thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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

}