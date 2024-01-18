package com.example.s.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;

import com.example.s.R;
import com.example.s.Service.HelpService;
import com.example.s.SharedPrafferences.MySharedPrefference;
import com.example.s.databinding.ActivityMainBinding;
import com.example.s.databinding.ActivitySettingsBinding;

import java.util.ArrayList;

public class settings extends AppCompatActivity {

    ActivitySettingsBinding binding ;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.codeword.setText("Help");

        binding.savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phno = binding.phnno.getText().toString();
                String codeword = binding.codeword.getText().toString();

                if (phno.isEmpty()){
                    Toast.makeText(settings.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (phno.length()!=10) {
                    Toast.makeText(settings.this, "Enter Valid Number", Toast.LENGTH_SHORT).show();

                }
                else if (codeword.isEmpty()){
                    Toast.makeText(settings.this, "Please Set Code Word", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(settings.this, "Data is Saved", Toast.LENGTH_SHORT).show();
                    MySharedPrefference.writeString(getApplicationContext(),"phone",phno);
                    MySharedPrefference.writeString(getApplicationContext(),"code",codeword);
                    finish();
                }
            }
        });


        binding.back.setOnClickListener(view -> {finish();});

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
                String errorMessage;
                switch (i) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage = "Client-side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        errorMessage = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage = "No match found";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        errorMessage = "Recognition service busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        errorMessage = "Server-side error";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage = "Speech timeout";
                        break;
                    default:
                        errorMessage = "Unknown error";
                        break;
                }

                Toast.makeText(getApplicationContext(),errorMessage, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    binding.codeword.setText(recognizedText);
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


        binding.codeword.setOnClickListener(view -> {startListening();});

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!MySharedPrefference.isSharedPreferencesEmpty(getApplicationContext())) {
            String phone= MySharedPrefference.readString(getApplicationContext(),"phone","8558975929");
            String code=MySharedPrefference.readString(getApplicationContext(),"code","help");
            binding.phnno.setText(phone);
            binding.codeword.setText(code);
        }
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

}