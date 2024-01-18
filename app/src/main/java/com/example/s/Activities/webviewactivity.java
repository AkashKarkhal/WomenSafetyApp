package com.example.s.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.window.OnBackInvokedDispatcher;

import com.example.s.R;
import com.example.s.databinding.ActivityMainBinding;
import com.example.s.databinding.ActivityWebviewactivityBinding;

public class webviewactivity extends AppCompatActivity {

    ActivityWebviewactivityBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(view -> {
            finish();
        });

        Intent i =  getIntent();
        String mode = i.getStringExtra("mode");
        WebSettings webSettings = binding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webview.setWebViewClient(new WebViewClient());

        assert mode != null;
        if (mode.equals("rights")){

            binding.webview.loadUrl("https://en.wikipedia.org/wiki/Violence_against_women");

        }else{
            binding.heading.setText("Helpline Numbers");
            binding.webview.loadUrl("https://indianhelpline.com/women-helpline");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if (binding.webview.canGoBack()) {
                        // If there's history, go back
                        binding.webview.goBack();
                    } else {
                        // If no history, perform default back button behavior (e.g., exit the app)
                        finish();

                    }
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

}