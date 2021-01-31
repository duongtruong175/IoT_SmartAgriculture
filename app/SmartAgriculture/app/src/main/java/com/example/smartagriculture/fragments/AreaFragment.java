package com.example.smartagriculture.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.smartagriculture.ControlActivity;
import com.example.smartagriculture.DashboardActivity;
import com.example.smartagriculture.R;
import com.example.smartagriculture.adapters.AreaAdapter;
import com.example.smartagriculture.models.AreaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaFragment extends Fragment {

    String host, service;
    String accessToken, id; //id la ma de lay ket qua viec thuc hien cau lenh sql
    int userId;
    int statusCode;

    List<AreaModel> areas;

    ImageView add_area;
    ListView listArea;
    View pbLoading;

    public AreaFragment() {
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
        View view = inflater.inflate(R.layout.fragment_area, container, false);
        add_area = view.findViewById(R.id.add_area);
        // lay ra list view
        listArea = view.findViewById(R.id.list_area);
        pbLoading = view.findViewById(R.id.pb_loading);

        // lay du lieu cho list view
        getListArea();

        // chon tung cai trong khu vuc
        listArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                AreaModel area = areas.get(i);
                // hien thi dialog
                actionArea(area);
            }
        });

        // an vao them khu vuc
        add_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog customDialog = new Dialog(getActivity());
                customDialog.setContentView(R.layout.dialog_add_area);
                EditText etName = customDialog.findViewById(R.id.et_name);
                EditText etDeviceType = customDialog.findViewById(R.id.et_devicetype);
                EditText etDeviceId = customDialog.findViewById(R.id.et_deviceid);
                customDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
                customDialog.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(customDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            // try catch de tranh luc ban phim khong bat
                        }
                        String name = etName.getText().toString().trim();
                        String deviceType = etDeviceType.getText().toString().trim();
                        String deviceId = etDeviceId.getText().toString().trim();
                        if (name.length() == 0) {
                            etName.requestFocus();
                            etName.setError("Dữ liệu không được để trống");
                        } else if (deviceType.length() == 0) {
                            etDeviceType.requestFocus();
                            etDeviceType.setError("Dữ liệu không được để trống");
                        } else if (deviceId.length() == 0) {
                            etDeviceId.requestFocus();
                            etDeviceId.setError("Dữ liệu không được để trống");
                        } else {
                            customDialog.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
                            // tao cau lenh sql va gui den API SQL Statements
                            String sql = "INSERT INTO \"Area\"  (\"name\",\"device_type\",\"device_id\",\"user_id\") VALUES ('" + name + "','" + deviceType + "','" + deviceId + "'," + userId + ");";
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
                                                                                    int rowsAffected = dataObject.getInt("rows_affected"); // so hang duoc them bang cau lenh insert
                                                                                    if (rowsAffected == 1) {
                                                                                        etName.setText("");
                                                                                        etDeviceType.setText("");
                                                                                        etDeviceId.setText("");
                                                                                        Toast.makeText(getActivity(), "Thêm khu vực thành công", Toast.LENGTH_SHORT).show();
                                                                                        customDialog.findViewById(R.id.pb_loading).setVisibility(View.GONE);
                                                                                        customDialog.dismiss();
                                                                                        getListArea();
                                                                                    }
                                                                                } else { // cau lenh sql bi loi
                                                                                    Toast.makeText(getActivity(), "ID khu vực đã tồn tại hoặc xảy ra lỗi, xin thử lại", Toast.LENGTH_SHORT).show();
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

                                                customDialog.findViewById(R.id.pb_loading).setVisibility(View.GONE);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        customDialog.findViewById(R.id.pb_loading).setVisibility(View.GONE);
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
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.show();
            }
        });

        return view;
    }

    public void actionArea(AreaModel area) {
        final Dialog customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.dialog_action_area);
        TextView tvAreName = customDialog.findViewById(R.id.txt_area);
        tvAreName.setText("Bạn muốn làm gì với: " + area.getName() + " ?");
        customDialog.findViewById(R.id.btn_control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                Intent intent = new Intent(getActivity(), ControlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("deviceType", area.getDeviceType());
                bundle.putString("deviceId", area.getDeviceId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        customDialog.findViewById(R.id.btn_dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("accessToken", accessToken);
                bundle.putString("host", host);
                bundle.putString("deviceId", area.getDeviceId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }

    public void getListArea() {
        pbLoading.setVisibility(View.VISIBLE);
        // tao cau lenh sql va gui den API SQL Statements
        String sql = "SELECT * FROM \"Area\" WHERE \"user_id\" = " + userId + ";";
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
                                                                // result dang [["1","a",...],["2","b",...]]
                                                                JSONArray jsonArrayRows = dataObject.getJSONArray("rows");
                                                                areas = new ArrayList<>();
                                                                for (int i = 0; i < jsonArrayRows.length(); i++) {
                                                                    JSONArray row = jsonArrayRows.getJSONArray(i); // lay ra 1 dong ["1","a",...]
                                                                    // id | name | device_type | device_id | user_id
                                                                    int id = row.getInt(0);
                                                                    String name = row.getString(1);
                                                                    String deviceType = row.getString(2);
                                                                    String deviceId = row.getString(3);
                                                                    areas.add(new AreaModel(id, name, deviceType, deviceId, userId));
                                                                }

                                                                // set adapter
                                                                AreaAdapter adapter = new AreaAdapter(areas);
                                                                listArea.setAdapter(adapter);

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
                            pbLoading.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // hide ProgressBar when loading API complete
                    pbLoading.setVisibility(View.GONE);
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