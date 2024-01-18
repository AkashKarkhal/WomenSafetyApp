package com.example.s.Service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.s.Activities.MainActivity;
import com.example.s.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class HelpService extends Service {
    private double latitude;
    private double longitude;
    MediaRecorder mediaRecorder;
    String outputFile;
   int count =0;
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "HelpServiceChannel";

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;



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

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Handle the updated location here
        // Handle location provider status changes
        // Handle when the location provider is enabled
        // Handle when the location provider is disabled
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Handle the updated location here
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(getApplicationContext(), "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle location provider status changes
                Toast.makeText(getApplicationContext(), "Status changed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // Handle when the location provider is enabled
                Toast.makeText(getApplicationContext(), "Service Enabled by service provider", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                // Handle when the location provider is disabled
                Toast.makeText(getApplicationContext(), "Service Desabled by service provider", Toast.LENGTH_SHORT).show();

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           if(loc!=null){
               latitude = loc.getLatitude();
               longitude = loc.getLongitude();
           }

            Toast.makeText(getApplicationContext(),"Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
        }



        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Help Service")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);




        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        // Set up a RecognitionListener to receive the speech recognition results
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    // Check if the recognized text contains your specific voice keyword
                    if (recognizedText.toLowerCase().contains("help")) {
                        // Call a function to perform a specific task
                        performTaskInBackground();
                    }
                    else{
                        startListening();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }

            // Implement other methods of RecognitionListener as neede
        });
        startListening();

    }

    private void performTaskInBackground() {
        Toast.makeText(this, "Sound Matched", Toast.LENGTH_SHORT).show();
        playSound();
        stopListening();
        sendSms();
        startRecording();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
        stopListening();
        Toast.makeText(this, "Service distroyed", Toast.LENGTH_SHORT).show();
    }

    void sendSms(){
        SmsManager smsManager = SmsManager.getDefault();
        String phoneNumber = "8558975929";  // Replace with the recipient's phone number
        String mapUrl = "https://www.google.com/maps?q=" + latitude + "%2C" + longitude;
        String message = "I'm in Danger. Please Help Me. My location is : \nLatitude: " + latitude + "\nLongitude: " + longitude+"\nOpen link to know my location:\n"+mapUrl;
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HelpServiceChannel";
            String description = "Channel for HelpService notifications";
            int importance = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Started listning", Toast.LENGTH_SHORT).show();
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    // Add this method to stop listening when needed
    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    void playSound() {
        MediaPlayer mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.sec);
        try {
            mediaPlayer.prepare();
        }
        catch (Exception e){
            Toast.makeText(this, "error in media preparing", Toast.LENGTH_SHORT).show();
        }
        mediaPlayer.start();
       
    }
}
