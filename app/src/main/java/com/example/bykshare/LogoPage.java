package com.example.bykshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LogoPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_page);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginPage.class));
            finish();
        }, 2000);
    }
}