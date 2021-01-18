package com.example.smartagriculture;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etUsername, etPassword, etEmail;
    TextView tvDate;
    Button btnRegister;
    RadioGroup rgGender;
    RadioButton rbSelected ;
    TextView tvLogin;

    Calendar c;
    DatePickerDialog dpd;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // an thanh ActionBar
        ActionBar ab = getSupportActionBar();
        ab.hide();

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        tvDate = findViewById(R.id.tv_date);
        rgGender = findViewById(R.id.rg_gender);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login) ;

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nYear, int nMonth, int nDay) {
                        c.set(nYear,nMonth,nDay);
                        Date date = c.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        tvDate.setText(formatter.format(date));
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();
                String date = tvDate.getText().toString();
                // lay radio button duoc chon
                int selectedId = rgGender.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                rbSelected = findViewById(selectedId);
                String gender = rbSelected.getText().toString();
                if(name.length()==0)
                {
                    etName.requestFocus();
                    etName.setError("Field can not be empty");
                }
                else if(username.length()==0)
                {
                    etUsername.requestFocus();
                    etUsername.setError("Field can not be empty");
                }
                else if(password.length()==0)
                {
                    etPassword.requestFocus();
                    etPassword.setError("Field can not be empty");
                }
                else if(email.length()==0)
                {
                    etEmail.requestFocus();
                    etEmail.setError("Field can not be empty");
                }
                else if(date.length()==0)
                {
                    tvDate.setError("Field can not be empty");
                }
                else {
                    Toast.makeText(getApplicationContext(), "You have just registered successfully" + name + ", " + username + ", "+ password + ", "+ email + ", "+ gender + ", " + date, Toast.LENGTH_SHORT).show();
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}