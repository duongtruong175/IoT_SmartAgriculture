package com.example.smartagriculture.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartagriculture.R;
import com.example.smartagriculture.adapters.AreaAdapter;
import com.example.smartagriculture.models.AreaModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    ImageView add_area;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        add_area = view.findViewById(R.id.add_area);

        // lay ra list view
        ListView listArea = view.findViewById(R.id.list_area);

        // lay du lieu cho list view
        List<AreaModel> areas = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            areas.add(new AreaModel("Khu vuc " + i, "ESP32", "Thiet bi thu " + i));
        }

        // set adapter
        AreaAdapter adapter = new AreaAdapter(areas);
        listArea.setAdapter(adapter);

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
                        String name = etName.getText().toString().trim();
                        String deviceType = etDeviceType.getText().toString().trim();
                        String deviceId = etDeviceId.getText().toString().trim();
                        Toast.makeText(getActivity(),"Add area successful" + name + ", " + deviceType + ", " + deviceId, Toast.LENGTH_SHORT).show();
                    }
                });
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.show();
            }
        });

        return view;
    }

    public void actionArea(AreaModel area){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Action");
        alertDialog.setMessage("Khu vực: " + area.getName());
        alertDialog.setPositiveButton("Real data and Control", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment f = new ControlFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.nav_fragment, f); //gan fragment cho FragmentLayout
                ft.commit(); // khoi chay fragment
            }
        });
        alertDialog.setNegativeButton("History", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment f = new DashboardFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.nav_fragment, f); //gan fragment cho FragmentLayout
                ft.commit(); // khoi chay fragment
            }
        });
        alertDialog.show();
    }

}