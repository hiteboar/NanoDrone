package com.example.nanodroneapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanodroneapp.Bluetooth.BluetoothController;
import com.example.nanodroneapp.ui.DebugFragment;
import com.example.nanodroneapp.ui.HomeFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private String[] mNeededPermissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,

    };
    public static final int MULTIPLE_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        CheckPermissions();
    }

    private void CheckPermissions(){

        ArrayList<String> lToRequestPermissions = new ArrayList<String>();
        for(int i = 0; i < mNeededPermissions.length; ++i){
            if (ContextCompat.checkSelfPermission(this, mNeededPermissions[i]) != PackageManager.PERMISSION_GRANTED){
                lToRequestPermissions.add(mNeededPermissions[i]);
            }
        }

        if (lToRequestPermissions.size() > 0){
            String[] lPermissionsList = new String[lToRequestPermissions.size()];
            lToRequestPermissions.toArray(lPermissionsList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(lPermissionsList, MULTIPLE_PERMISSIONS);
            }
        }
    }

    //CREATE MENU BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //MENU ITEM SELECTION
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_debug){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new DebugFragment()).commit();
        }
        return true;
    }

    //SIDE MENU SELECTION
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_settings){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new BluetoothConnectionFragment()).commit();
        }
        else if (id == R.id.nav_home){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new HomeFragment()).commit();
        }

        String lDeviceName = BluetoothController.Instance().GetDeviceName();
        if (lDeviceName == null || lDeviceName == ""){
            lDeviceName = getString(R.string.Default_device_name);
        }
        ((TextView)findViewById(R.id.Header_ConnectedDeviceName)).setText(lDeviceName);

        drawerLayout.closeDrawer(GravityCompat.START);
     return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}