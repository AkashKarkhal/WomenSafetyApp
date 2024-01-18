package com.example.s.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.example.s.Service.HelpService;
import com.example.s.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        updateUi();


        binding.StartButton.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.SEND_SMS, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else{
                Intent serviceIntent = new Intent(MainActivity.this, HelpService.class);
                if (isServiceRunning()) {
                    stopService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
                updateUi();
            }
        });

    }


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (HelpService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void updateUi(){
        if (isServiceRunning()) {
            binding.StartButton.setText("Stop\nHelping Procedure");
        } else {
            binding.StartButton.setText("Start\nHelping Procedure");
        }
    }
}