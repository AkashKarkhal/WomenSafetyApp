package com.example.s.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Date;

public class HelpService extends Service {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;
    MediaRecorder mediaRecorder;
    String outputFile;
    Handler handler;
   int count =0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle the updated location here
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(getApplicationContext(),"Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                sendSms();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle location provider status changes
                Toast.makeText(getApplicationContext(),"Status changed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderEnabled(String provider) {
                // Handle when the location provider is enabled
                Toast.makeText(getApplicationContext(),"Service Enabled by service provider", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onProviderDisabled(String provider) {
                // Handle when the location provider is disabled
                Toast.makeText(getApplicationContext(),"Service Desabled by service provider", Toast.LENGTH_SHORT).show();

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           if(loc!=null){
               latitude = loc.getLatitude();
               longitude = loc.getLongitude();
           }
            sendSms();
        }
        //startRecording();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopRecording();
        Toast.makeText(this, "Service distroyed", Toast.LENGTH_SHORT).show();
    }
    void getLocation() {
    }
    void sendSms(){
        SmsManager smsManager = SmsManager.getDefault();
        String phoneNumber = "8558975929";  // Replace with the recipient's phone number
        String mapUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;
        String message = "I'm in Danger. Please Help Me. My location is : \nLatitude: " + latitude + "\nLongitude: " + longitude+"/nOpen link to know my location :\n"+mapUrl;
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void startRecording() {
        outputFile = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/help"+(++count)+"("+new Date().getTime()+").mp4";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        // mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setOutputFile(outputFile);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show();
            stopSelf(); // Stop the service if recording fails
        }
    }
    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }
}
