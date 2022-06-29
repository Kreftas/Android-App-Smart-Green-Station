package com.example.sgs_test_2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.sgs_test_2.database.Database;
import com.example.sgs_test_2.station.StationsFragment;
import com.example.sgs_test_2.station.TextViewer;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import Utility.MockupFactory;

public class RealMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    private Database databas;
    private MapFragment fragment = new MapFragment();
    private FragmentTransaction transaction;
    public NavigationView navigationView;
    private SharedPreferences sp;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private ActivityResultLauncher<Intent> someActivityResultLauncher2;
    private ActivityResultLauncher<Intent> someActivityResultLauncher3;
    private final MockupFactory mockupFactory = new MockupFactory();
    private boolean stationsInitialized = false;
    private ProgressDialog progress;
    private boolean QR_code = true;
    private boolean success;
    public ArrayList<Integer> k = new ArrayList<>();
    public TextView name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);
        //receiver = new ConnectionChangeReceiver();
        //receiver.onReceive(getBaseContext(), new Intent(this, ConnectionChangeReceiver.class).putExtra("from" , "previousActivity"));

        transaction = getSupportFragmentManager().beginTransaction();
        databas = new Database(getBaseContext(), this);
        stationsInitialized = databas.initializeStations();

        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        boolean themeMode = sp.getBoolean("nightDayMode", false);


        if (themeMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            getWindow().setNavigationBarColor(Color.parseColor("#00000001"));
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Add listener to nav bar
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Add button to open nav bar
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_draw_open, R.string.naw_draw_close);
        drawerLayout.addDrawerListener(toggle);

        /*
        View header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.signature);
        String newName = sp.getString("newName", null);
        name.setText(newName);

        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals("newName")){
                    View header = navigationView.getHeaderView(0);
                    name = header.findViewById(R.id.signature);
                    String newName = sp.getString("newName", null);
                    name.setText(newName);
                }
            }
        });

         */

        toggle.syncState();

        // When QR-scanner is finished and closed the data from the QR-code will be returned and handled here.
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        // fetches ID from QR-scanner
                        String id = result.getData().getStringExtra("id");

                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new TextViewer(mockupFactory.getName(Integer.parseInt(id)), Integer.parseInt(id), QR_code), "TextViewer");
                        transaction.addToBackStack("TextViewer");
                        transaction.commit();
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {

                        getSupportFragmentManager().popBackStack("MapFragment", 0);
                    }
                });

        someActivityResultLauncher2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        transaction.replace(R.id.fragment_container, new MapFragment(), "MapFragment").addToBackStack("MapFragment").commit();
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        transaction.replace(R.id.fragment_container, new MapFragment(), "MapFragment").addToBackStack("MapFragment").commit();
                    }
                });

        someActivityResultLauncher3 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    finish();
                    recreate();
                });

        //Set first window to MapFragment
        if (savedInstanceState == null) {
            //Clear the stack (just in case)
            //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            navigationView.setCheckedItem(R.id.nav_map);
            //transaction = getSupportFragmentManager().beginTransaction();

            boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("isFirstRun", true);
            if (isFirstRun) {
                /* transaction.replace(R.id.fragment_container, new VideoFragment(), "VideoFragment");*/
                //drawerLayout.setDrawerLockMode(DrawerLayout. LOCK_MODE_LOCKED_CLOSED);
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).apply();

                Intent myIntent = new Intent(RealMainActivity.this, FullscreenActivity.class);
                someActivityResultLauncher2.launch(myIntent);
                // Never seems to reach this condition since when changing into NIGHT/DAY mode seems to pops all Fragments from stack. (Activity recreation might be the cause)
            } else {
                transaction.replace(R.id.fragment_container, new MapFragment(), "MapFragment").addToBackStack("MapFragment").commit();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


            case R.id.nav_map:
                getSupportFragmentManager().popBackStack("MapFragment", 0);
                break;
            case R.id.nav_stations:
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new StationsFragment(), "StationsFragment");
                transaction.addToBackStack("StationsFragment");
                transaction.commit();
                break;

            case R.id.nav_about:
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new AboutSGSFragment(), "AboutSGSFragment");
                transaction.addToBackStack("AboutSGSFragment");
                transaction.commit();
                break;

            case R.id.nav_settings:
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SettingsFragment(), "SettingsFragment");
                transaction.addToBackStack("SettingsFragment");
                transaction.commit();
                break;

            case R.id.qr_code:
                startQR();
                break;

            case R.id.nav_instagram:
                Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/smartgreenstaton"));
                startActivity(i1);
                break;

            case R.id.nav_webpage:
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://smartgreenstation.se/"));
                startActivity(i2);
                break;

            case R.id.nav_linkedin:
                Intent i3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/rodrigo-godoy-zamora-62b475160/"));
                startActivity(i3);
                break;

            case R.id.video:
                Intent i4 = new Intent(RealMainActivity.this, FullscreenActivity.class);
                startActivity(i4);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // When accessing a Fragment class by choosing an NavigationItem, this will return the user back to the MainActivity, hence going back to the first instance of MapFragment
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public Database getDatabaseObject() {
        return databas;
    }

    public boolean checkPermission() {
        // here we are checking two permission that is vibrate
        // and camera which is granted by user and not.
        // if permission is granted then we are returning
        // true otherwise false.
        int position_permission = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return position_permission == PackageManager.PERMISSION_GRANTED;
    }

    private void startQR() {
        if (getDatabaseObject().databaseInitialized()) {
            Intent i = new Intent(getApplicationContext(), Camera.class);
            if (databas.getStationIds() != null) {
                i.putExtra("stations", databas.getStationIds());
            }
            someActivityResultLauncher.launch(i);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void showInternetError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.No_Internet1)
                .setMessage(R.string.No_Internet2)
                .setCancelable(true)
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    someActivityResultLauncher3.launch(i);
                });
    }

    public ActivityResultLauncher<Intent> getActivityLauncher() {
        return someActivityResultLauncher3;
    }

}