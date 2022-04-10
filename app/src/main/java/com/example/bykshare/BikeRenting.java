package com.example.bykshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BikeRenting extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "BikeRenting";

    ImageButton backarrow;
    TextView biketitle, hr_rate, day_rate, week_rate, bike_type, rider_ht, location;
    Button requestbike;
    String owneremail;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_renting);
        Log.i(ACTIVITY_NAME, "in onCreate");

        backarrow = (ImageButton) findViewById(R.id.backarrow);
        biketitle = (TextView) findViewById(R.id.biketitle);
        hr_rate = (TextView) findViewById(R.id.hr_rate);
        day_rate = (TextView) findViewById(R.id.day_rate);
        week_rate = (TextView) findViewById(R.id.week_rate);
        bike_type = (TextView) findViewById(R.id.bike_type);
        rider_ht = (TextView) findViewById(R.id.rider_ht);
        location = (TextView)  findViewById(R.id.location);
        requestbike = (Button) findViewById(R.id.requestbike);

        Intent receiveinfo = getIntent();

        String bike_title = receiveinfo.getStringExtra("BikeTitle");

        ref = FirebaseDatabase.getInstance().getReference("listedbikes");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(ACTIVITY_NAME, String.valueOf(snapshot));
                for(DataSnapshot bikeinfo : snapshot.getChildren()){
                    if(bike_title.equals(bikeinfo.child("nameofbike").getValue(String.class))){
                        String _hrrate = bikeinfo.child("hourlyrate").getValue(String.class);
                        String _dayrate = bikeinfo.child("dailyrate").getValue(String.class);
                        String _weekrate = bikeinfo.child("weeklyrate").getValue(String.class);
                        String _biketype = bikeinfo.child("biketype").getValue(String.class);
                        String _riderht = bikeinfo.child("riderheight").getValue(String.class);
                        String loc = bikeinfo.child("location").getValue(String.class);
                        owneremail = bikeinfo.child("useremailid").getValue(String.class);

                        String hour_rate = "$" + " " + _hrrate + " /hour";
                        String dayrate = "$" + " " + _dayrate + " /day";
                        String weekrate = "$" + " " + _weekrate + " /week";
                        String riderht = _riderht + " " + "cms";

                        biketitle.setText(bike_title);
                        hr_rate.setText(hour_rate);
                        day_rate.setText(dayrate);
                        week_rate.setText(weekrate);
                        bike_type.setText(_biketype);
                        rider_ht.setText(riderht);
                        location.setText(loc);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mappage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mappage);
            }
        });

        requestbike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rentformpage = new Intent(getApplicationContext(), BikeRentalForm.class);
                rentformpage.putExtra("BikeTitle",bike_title);
                rentformpage.putExtra("OwnerEmail",owneremail);
                rentformpage.putExtra("location", location.getText().toString());
                startActivity(rentformpage);
            }
        });
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
