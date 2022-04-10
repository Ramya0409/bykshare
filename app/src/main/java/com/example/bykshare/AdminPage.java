package com.example.bykshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;

public class AdminPage extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "AdminPage";
    private Button ar_users, ar_admins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        ar_admins = (Button) findViewById(R.id.ar_admin);
        ar_users = (Button) findViewById(R.id.ar_user);

        getSupportActionBar().setTitle("Admin Page");

        ar_admins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRemovePage("Admins");
            }
        });

        ar_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRemovePage("Users");
            }
        });
    }

    public void AddRemovePage(String val){
        Intent page = new Intent(this, AddRemoveUA.class);
        page.putExtra("UorA",val);
        startActivity(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}