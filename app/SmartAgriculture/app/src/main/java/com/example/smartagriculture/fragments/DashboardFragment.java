package com.example.smartagriculture.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

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

import com.example.smartagriculture.R;
import com.example.smartagriculture.models.DeviceDataModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DashboardFragment extends Fragment {

    LineChart lineChart;
    ProgressBar progressBar;
    ArrayList<DeviceDataModel> deviceDataModels;
    CheckBox temp, humAir, humSoil;
    TextView startDate, endDate;
    DatePickerDialog dpd1,dpd2;
    long startTime,endTime;
    Spinner spinnerChart;
    String typeChart;

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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        // lay thanh phan giao dien trong Fragment
        lineChart = view.findViewById(R.id.lineChart);
        spinnerChart = view.findViewById(R.id.spinner_chart);
        progressBar = view.findViewById(R.id.progressBar);
        temp = view.findViewById(R.id.checkbox_temp);
        humAir = view.findViewById(R.id.checkbox_air);
        humSoil = view.findViewById(R.id.checkbox_soil);
        startDate = view.findViewById(R.id.tv_date_start);
        endDate = view.findViewById(R.id.tv_date_end);

        // khoi tao cho spinner
        String[] charts = {"Line Chart", "Base Chart","Column Chart"};
        typeChart = charts[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,charts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChart.setAdapter(adapter);

        spinnerChart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeChart = (String) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(),typeChart,Toast.LENGTH_SHORT).show();
                drawChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // khoi tao cac gia tri ban dau
        Date today = new Date();
        endTime = today.getTime() / 1000; //timestamp tính theo second
        startTime = endTime - 60 * 60 * 24;
        Date before = new Date(startTime * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        startDate.setText(formatter.format(before));
        endDate.setText(formatter.format(today));

        temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                drawChart();
            }
        });

        humAir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                drawChart();
            }
        });

        humSoil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                drawChart();
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
                        c.set(nYear,nMonth,nDay);
                        Date date = c.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        startDate.setText(formatter.format(date));
                        startTime = date.getTime();
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
                        c.set(nYear,nMonth,nDay);
                        Date date = c.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        endDate.setText(formatter.format(date));
                        endTime = date.getTime();
                        getDeviceData();
                    }
                }, year, month, day);
                dpd2.show();
            }
        });

        // ve bieu do lan dau
        getDeviceData();

        return view;
    }

    public void getDeviceData() {
        // lay time tu DB theo startTime và endTime
        Date date = new Date();
        long timestamp = date.getTime();
        // select time_send < endtime and >startTime
        deviceDataModels = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            DeviceDataModel data = new DeviceDataModel(Math.random() * 50 + 50, Math.random() * 20 + 20,Math.random() * 30 + 30, String.valueOf(timestamp + i * 60 * 60 * 1000));
            deviceDataModels.add(data);
        }
        drawChart();
    }

    public void drawChart() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String java_date2 = sdf.format(date);

        //chart
        int numDataPoints = deviceDataModels.size();
        ArrayList<Entry> yTemperatureAir = new ArrayList<>();
        ArrayList<Entry> yHumidityAir = new ArrayList<>();
        ArrayList<Entry> yHumiditySoid = new ArrayList<>();
        for (int i = 0; i < numDataPoints; i++) {
            DeviceDataModel data = deviceDataModels.get(i);
            yTemperatureAir.add(new Entry(i, data.getTemperatureAir().intValue()));
            yHumidityAir.add(new Entry(i, data.getHumidityAir().intValue()));
            yHumiditySoid.add(new Entry(i, data.getHumiditySoil().intValue()));
        }
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        if (temp.isChecked()) {
            LineDataSet lineDataSet1 = new LineDataSet(yTemperatureAir, "Nhiệt độ không khí");
            lineDataSet1.setDrawCircles(true);
            lineDataSet1.setColor(Color.RED);
            lineDataSets.add(lineDataSet1);
        }
        if (humAir.isChecked()) {
            LineDataSet lineDataSet2 = new LineDataSet(yHumidityAir, "Độ ẩm không khí");
            lineDataSet2.setDrawCircles(true);
            lineDataSet2.setColor(Color.BLUE);
            lineDataSets.add(lineDataSet2);
        }
        if (humSoil.isChecked()) {
            LineDataSet lineDataSet3 = new LineDataSet(yHumiditySoid, "Độ ẩm đất");
            lineDataSet3.setDrawCircles(true);
            lineDataSet3.setColor(Color.BLACK);
            lineDataSets.add(lineDataSet3);
        }

        // bat dau ve
        if(typeChart.equals("Line Chart")) {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new MyAxisValueFormatter());
            LineData data = new LineData(lineDataSets);
            lineChart.setData(data);
            lineChart.animateXY(3000, 2000);
            Description description = new Description();
            description.setText(java_date2);
            lineChart.setDescription(description);
            lineChart.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private class MyAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return value + "hour(s) before";
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            axis.setLabelCount(6, true);
            int index = Math.round(value);
            long timestamp = Long.parseLong(deviceDataModels.get(index).getTimeSend());
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            return sdf.format(date);
        }
    }

}