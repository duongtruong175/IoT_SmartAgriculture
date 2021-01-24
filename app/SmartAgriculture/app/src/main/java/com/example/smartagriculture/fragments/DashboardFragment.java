package com.example.smartagriculture.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.example.smartagriculture.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.example.smartagriculture.AAChartCoreLib.AAChartCreator.AAChartView;
import com.example.smartagriculture.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.example.smartagriculture.AAChartCoreLib.AAChartEnum.AAChartZoomType;
import com.example.smartagriculture.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    long startTime, endTime;

    ProgressBar progressBar;
    CheckBox tempAir, humAir, humSoil;
    TextView startDate, endDate, tvNoData;
    DatePickerDialog dpd1, dpd2;
    Spinner spinnerChartType, spinnerSymbolType;
    SwitchCompat switchParam, switchSymbol;

    AAChartModel aaChartModel;
    AAChartView aaChartView;
    AASeriesElement[] aaSeriesElements;
    ArrayList<Double> temperatureAirData;
    ArrayList<Double> humidityAirData;
    ArrayList<Double> humiditySoilData;
    String[] chartTypes;
    String[] symbolTypes;

    String deviceId;
    String host, service;
    String accessToken, id; //id la ma de lay ket qua viec thuc hien cau lenh sql
    int statusCode;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // lay du lieu tu HomeFragment truyen qua
        deviceId = getArguments().getString("deviceId");
        accessToken = getArguments().getString("accessToken");
        host = getArguments().getString("host");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // lay thanh phan giao dien trong Fragment
        spinnerChartType = view.findViewById(R.id.spinner_chartType);
        spinnerSymbolType = view.findViewById(R.id.spinner_symbolType);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoData = view.findViewById(R.id.tv_no_data);
        switchParam = view.findViewById(R.id.switch_param);
        switchSymbol = view.findViewById(R.id.switch_symbol);
        tempAir = view.findViewById(R.id.checkbox_tempAir);
        humAir = view.findViewById(R.id.checkbox_humAir);
        humSoil = view.findViewById(R.id.checkbox_humSoil);
        startDate = view.findViewById(R.id.tv_date_start);
        endDate = view.findViewById(R.id.tv_date_end);
        // lay view chart
        aaChartView = view.findViewById(R.id.AAChartView);

        // khoi tao cac gia tri ban dau
        Date today = new Date();
        endTime = today.getTime() / 1000; //timestamp tính theo second
        startTime = endTime - 60 * 60 * 24;
        Date before = new Date(startTime * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        startDate.setText(formatter.format(before));
        endDate.setText(formatter.format(today));
        // du lieu cho spinner
        chartTypes = new String[]{"Line", "Spline", "Area", "Areaspline", "Bar", "Column", "Scatter", "Pie",
                "Bubble", "Pyramid", "Funnel", "Columnrange", "Arearange", "Areasplinerange", "Boxplot", "Waterfall"};
        symbolTypes = new String[]{"Circle", "Square", "Diamond", "Triangle", "Triangle-down"};
        // du lieu ban dau cho chart
        temperatureAirData = new ArrayList<>();
        humidityAirData = new ArrayList<>();
        humiditySoilData = new ArrayList<>();
        temperatureAirData.add(0.0);
        humidityAirData.add(0.0);
        humiditySoilData.add(0.0);

        // init chart truoc khi tao cac su kien listener cho cac view
        initChart();

        // khoi tao cac kieu bieu do (chart) cho spinner
        ArrayAdapter<String> adapterChart = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, chartTypes);
        adapterChart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChartType.setAdapter(adapterChart);

        // khoi tao cac kieu bieu tuong (symbol) cho spinner
        ArrayAdapter<String> adapterSymbol = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, symbolTypes);
        adapterSymbol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSymbolType.setAdapter(adapterSymbol);

        // listener cua spinner chart type
        spinnerChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = chartTypes[position].toLowerCase();
                // thay vi phai case (xem cac kieu chart trong AAChartType.java)
                aaChartModel.chartType(type);
                aaChartView.aa_refreshChartWithChartModel(aaChartModel);
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(aaSeriesElements, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // listener cua spinner symbol type
        spinnerSymbolType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = symbolTypes[position].toLowerCase();
                // thay vi phai case (xem cac kieu symbol trong AAChartSymbolType.java)
                aaChartModel.markerSymbol(type);
                aaChartView.aa_refreshChartWithChartModel(aaChartModel);
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(aaSeriesElements, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // listener cua switch Param show?
        switchParam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aaChartModel.dataLabelsEnabled(isChecked);
                aaChartView.aa_refreshChartWithChartModel(aaChartModel);
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(aaSeriesElements, false);
            }
        });

        // listener cua switch Symbol show?
        switchSymbol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    aaChartModel.markerRadius(6f);
                } else {
                    aaChartModel.markerRadius(0f);
                }
                aaChartView.aa_refreshChartWithChartModel(aaChartModel);
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(aaSeriesElements, false);
            }
        });

        tempAir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateChart();
            }
        });

        humAir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateChart();
            }
        });

        humSoil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateChart();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = startDate.getText().toString().trim();
                String[] parts = date.split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year = Integer.parseInt(parts[2]);

                dpd1 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nYear, int nMonth, int nDay) {
                        Calendar c = Calendar.getInstance();
                        c.set(nYear, nMonth, nDay);
                        Date date = c.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        startDate.setText(formatter.format(date));
                        startTime = date.getTime() / 1000;
                        getDeviceData();
                    }
                }, year, month, day);
                dpd1.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = endDate.getText().toString().trim();
                String[] parts = date.split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year = Integer.parseInt(parts[2]);

                dpd2 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nYear, int nMonth, int nDay) {
                        Calendar c = Calendar.getInstance();
                        c.set(nYear, nMonth, nDay);
                        Date date = c.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        endDate.setText(formatter.format(date));
                        endTime = date.getTime() / 1000;
                        getDeviceData();
                    }
                }, year, month, day);
                dpd2.show();
            }
        });

        // lay du lieu lan dau
        getDeviceData();

        return view;
    }

    // ham lay du lieu thiet bi tu DB theo startTime và endTime
    public void getDeviceData() {
        progressBar.setVisibility(View.VISIBLE);

        // tao cau lenh sql va gui den API SQL Statements
        String sql = "SELECT * FROM \"DeviceData\" WHERE \"device_id\" = '" + deviceId + "' AND \"time_send\" > " + startTime + " AND \"time_send\" < " + endTime + " FETCH FIRST 20 ROWS ONLY;";
        try {
            // tao object gui di
            JSONObject sqlInfo = new JSONObject();
            sqlInfo.put("commands", sql);
            sqlInfo.put("limit", 10000);
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
                                                                if (jsonArrayRows.length() > 0) { // neu du lieu tra ve khong rong
                                                                    temperatureAirData = new ArrayList<>();
                                                                    humidityAirData = new ArrayList<>();
                                                                    humiditySoilData = new ArrayList<>();
                                                                    for (int i = 0; i < jsonArrayRows.length(); i++) {
                                                                        JSONArray row = jsonArrayRows.getJSONArray(i); // lay ra 1 dong ["1","a",...]
                                                                        // id | device_id | temperature_air | humidity_air | humidity_soil | time_send
                                                                        Double temperatureAir = row.getDouble(2);
                                                                        Double humidityAir = row.getDouble(3);
                                                                        Double humiditySoil = row.getDouble(4);
                                                                        temperatureAirData.add(temperatureAir);
                                                                        humidityAirData.add(humidityAir);
                                                                        humiditySoilData.add(humiditySoil);
                                                                    }
                                                                }
                                                                else {
                                                                    // khong co du lieu trong khoang thoi gian do
                                                                    temperatureAirData = new ArrayList<>();
                                                                    humidityAirData = new ArrayList<>();
                                                                    humiditySoilData = new ArrayList<>();
                                                                    temperatureAirData.add(0.0);
                                                                    humidityAirData.add(0.0);
                                                                    humiditySoilData.add(0.0);
                                                                }

                                                                // cap nhap lai chart
                                                                updateChart();
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

                            progressBar.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
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

    public void initChart() {
        // init AASeriesElement
        aaSeriesElements = new AASeriesElement[]{
                new AASeriesElement().name("Temperature Air").data(temperatureAirData.toArray()),
                new AASeriesElement().name("Humidity Air").data(humidityAirData.toArray()),
                new AASeriesElement().name("Humidity Soil").data(humiditySoilData.toArray())
        };
        // init AAChartModel
        aaChartModel = new AAChartModel()
                .chartType(chartTypes[0].toLowerCase())
                .markerSymbol(symbolTypes[0].toLowerCase())
                .title("Biểu đồ thông số môi trường")
                .subtitle("Thiết bị có ID: " + deviceId)
                .dataLabelsEnabled(true)
                .markerRadius(6f)
                .series(aaSeriesElements);
        // ve bieu do 1 lan duy nhat
        aaChartView.aa_drawChartWithChartModel(aaChartModel);
    }

    // ham cap nhap trang thai cho bieu do
    public void updateChart() {
        aaSeriesElements = new AASeriesElement[]{};
        List<AASeriesElement> a = new ArrayList<>();
        if (tempAir.isChecked()) {
            a.add(new AASeriesElement()
                    .name("Temperature Air")
                    .data(temperatureAirData.toArray()));
        }
        if (humAir.isChecked()) {
            a.add(new AASeriesElement()
                    .name("Humidity Air")
                    .data(humidityAirData.toArray()));
        }
        if (humSoil.isChecked()) {
            a.add(new AASeriesElement()
                    .name("Humidity Soil")
                    .data(humiditySoilData.toArray()));
        }
        Object[] a1 = a.toArray();
        aaSeriesElements = Arrays.copyOf(a1, a1.length, AASeriesElement[].class);
        if (aaSeriesElements.length == 0) {
            aaChartView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
            aaChartView.setVisibility(View.VISIBLE);
            // cap nhap lai trang thai bieu do
            aaChartModel.series(aaSeriesElements);
            aaChartView.aa_refreshChartWithChartModel(aaChartModel);
            aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(aaSeriesElements, false);
        }
    }

}