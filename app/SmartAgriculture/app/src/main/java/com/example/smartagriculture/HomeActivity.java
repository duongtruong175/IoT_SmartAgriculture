package com.example.smartagriculture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.smartagriculture.fragments.AccountFragment;
import com.example.smartagriculture.fragments.AreaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    String host;
    String accessToken;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // lay cac bien duoc truyen tu activity qua lop Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            accessToken = bundle.getString("accessToken");
            host = bundle.getString("host");
            userId = bundle.getInt("userId");
        }

        // an thanh ActionBar
        ActionBar ab = getSupportActionBar();
        ab.hide();

        // lay ra nav
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navSelected);

        // init cac layout ban dau
        navView.setSelectedItemId(R.id.navigation_area);
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

    // khi nav thay doi
    private BottomNavigationView.OnNavigationItemSelectedListener navSelected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.navigation_area) {
                Fragment navFragment = new AreaFragment();
                // gan va khoi chay fragment trong FragmentLayout
                initFragment(navFragment);
                return true;
            } else if (item.getItemId() == R.id.navigation_account) {
                Fragment navFragment = new AccountFragment();
                // gan va khoi chay fragment trong FragmentLayout
                initFragment(navFragment);
                return true;
            }
            return false;
        }
    };

    // ham gan vo khoi chay fragment trong FragmentLayout
    private void initFragment(Fragment navFragment) {
        Bundle bundle = new Bundle();
        bundle.putString("accessToken", accessToken);
        bundle.putString("host", host);
        bundle.putInt("userId", userId);
        navFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_fragment, navFragment); //gan fragment cho FragmentLayout
        ft.commit(); // khoi chay fragment
    }
}