package com.example.bykshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BikeRentalForm extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "BikeRentalForm";

    ImageButton backarrow;
    TextView biketitle, totalprice, pickup, dropoff;
    Button request_bike;

    private DatabaseReference ref;
    private FirebaseUser user;
    String uid, useremail, price, bikename;

    private int totalfee, hour_rate, day_rate, week_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_rental_form);
        Log.i(ACTIVITY_NAME, "in onCreate");

        backarrow = (ImageButton) findViewById(R.id.back_arrow);
        biketitle = (TextView) findViewById(R.id.bike_title);
        pickup = (TextView) findViewById(R.id.pickupdate);
        dropoff = (TextView) findViewById(R.id.dropoffdate);
        totalprice = (TextView) findViewById(R.id.totalfee);
        request_bike = (Button) findViewById(R.id.request_bike);

        Intent receivedetails = getIntent();

        bikename = receivedetails.getStringExtra("BikeTitle");
        String owneremail = receivedetails.getStringExtra("OwnerEmail");
        String bike_loc = receivedetails.getStringExtra("location");

        biketitle.setText(bikename);

        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(pickup);
            }
        });

        dropoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(dropoff);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CalculatePrice(bikename);
            }
        };

        pickup.addTextChangedListener(textWatcher);
        dropoff.addTextChangedListener(textWatcher);

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bikerentpage = new Intent(getApplicationContext(), BikeRenting.class);
                bikerentpage.putExtra("BikeTitle", bikename);
                startActivity(bikerentpage);
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    useremail = snapshot.child(uid).child("emailid").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        request_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pick_up = pickup.getText().toString();
                String drop_off = dropoff.getText().toString();
                String price = totalprice.getText().toString();

                if(!ValidatePickUp() || !ValidateDropOff()){
                    return;
                }

                BikeRentEventsClass rentalinfo = new BikeRentEventsClass(useremail, bikename, pick_up, drop_off, price, "None", bike_loc);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.i(ACTIVITY_NAME, String.valueOf(snapshot));
                        for(DataSnapshot addnotify : snapshot.getChildren()){
                            if(owneremail.equals(addnotify.child("emailid").getValue(String.class))){
                                if((addnotify.child("notifications") == null) &&
                                        (ref.child(uid).child("approvals") == null)){
                                    addnotify.getRef().setValue("notifications");
                                    addnotify.getRef().child("notifications").push().setValue(rentalinfo);

                                    ref.child(uid).setValue("approvals");
                                    ref.child(uid).child("approvals").push().setValue(rentalinfo);
                                    addNotification();
                                }else{
                                    addnotify.getRef().child("notifications").push().setValue(rentalinfo);

                                    ref.child(uid).child("approvals").push().setValue(rentalinfo);
                                    addNotification();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addNotification(){
        Toast.makeText(getApplicationContext(), "Your rental request has been sent!", Toast.LENGTH_SHORT).show();

        Intent bikerent = new Intent(getApplicationContext(), BikeRenting.class);
        bikerent.putExtra("BikeTitle", bikename);
        startActivity(bikerent);
    }

    private void showDateTimeDialog(TextView datetime) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int mins) {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, mins);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");

                        datetime.setText(dateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(BikeRentalForm.this, timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                        false).show();
            }
        };
        new DatePickerDialog(BikeRentalForm.this, dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void CalculatePrice(String bikename) {
        DatabaseReference bikeref = FirebaseDatabase.getInstance().getReference("listedbikes");

        bikeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot bikeinfo : snapshot.getChildren()){
                    if(bikename.equals(bikeinfo.child("nameofbike").getValue(String.class))){
                        String hourrate = bikeinfo.child("hourlyrate").getValue(String.class);
                        String dayrate = bikeinfo.child("dailyrate").getValue(String.class);
                        String weekrate = bikeinfo.child("weeklyrate").getValue(String.class);

                        hour_rate = Integer.parseInt(hourrate);
                        day_rate = Integer.parseInt(dayrate);
                        week_rate = Integer.parseInt(weekrate);
                        Log.i(ACTIVITY_NAME, "hourrate: " + hour_rate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.i(ACTIVITY_NAME, "dayrate: " + day_rate);

        if (!(pickup.getText().toString()).isEmpty() || !(dropoff.getText().toString()).isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");
            try {
                Date d1 = dateFormat.parse(pickup.getText().toString());
                Date d2 = dateFormat.parse(dropoff.getText().toString());

                long diff = Math.abs(d1.getTime() - d2.getTime());
                long hours = ((diff / 1000) / 60) / 60;

                Log.i(ACTIVITY_NAME, "hours: " + hours);
                if (hours < 24) {
                    totalfee = (int) (hours * hour_rate);
                    price = "$ "+ Integer.toString(totalfee);
                    totalprice.setText(price);

                    Log.i(ACTIVITY_NAME, "hours" + hours);
                } else if (hours >= 24 && hours < 168) {
                    long days = hours / 24;
                    hours = hours % 24;

                    int dayfee = (int) (days * day_rate);
                    int hourfee = (int) (hours * hour_rate);
                    totalfee = dayfee + hourfee;
                    price = "$ "+ Integer.toString(totalfee);
                    totalprice.setText(price);

                    Log.i(ACTIVITY_NAME, days + "days " + hours + "hours");
                } else {
                    long weeks = hours / 168;
                    hours = hours % 168;
                    if (hours > 24 && hours < 168) {
                        long days = hours / 24;
                        hours = hours % 24;

                        int weekfee = (int) (weeks * week_rate);
                        int dayfee = (int) (days * day_rate);
                        int hourfee = (int) (hours * hour_rate);
                        totalfee = weekfee + dayfee + hourfee;
                        price = "$ "+ Integer.toString(totalfee);
                        totalprice.setText(price);

                        Log.i(ACTIVITY_NAME, weeks + "weeks " + days + "days " + hours + "hours");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "The field(s) is/are empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean ValidatePickUp() {
        String val = pickup.getText().toString().trim();

        if (val.isEmpty()) {
            pickup.setError("Field cannot be empty!");
            return false;
        } else {
            pickup.setError(null);
            return true;
        }
    }

    private boolean ValidateDropOff() {
        String val = dropoff.getText().toString().trim();

        if (val.isEmpty()) {
            dropoff.setError("Field cannot be empty!");
            return false;
        } else {
            dropoff.setError(null);
            return true;
        }
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