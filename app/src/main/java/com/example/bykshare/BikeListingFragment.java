package com.example.bykshare;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BikeListingFragment extends Fragment {
    private EditText bikename, biketype, riderht, hourrate, dayrate, weekrate, street, cityprovince, zipcode;
    private Button bikelistbtn, gobackbtn;

    String uid, useremail;
    DatabaseReference userref, bikelistref;
    FirebaseUser user;
    Context bikelistingfrag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bike_listing_fragment, container, false);

        bikename = (EditText) view.findViewById(R.id.bikename);
        biketype = (EditText) view.findViewById(R.id.typeofbike);
        riderht = (EditText) view.findViewById(R.id.riderht);
        hourrate = (EditText) view.findViewById(R.id.hourrate);
        dayrate = (EditText) view.findViewById(R.id.dayrate);
        weekrate = (EditText) view.findViewById(R.id.weekrate);
        street = (EditText) view.findViewById(R.id.street);
        cityprovince = (EditText) view.findViewById(R.id.cityprovince);
        zipcode = (EditText) view.findViewById(R.id.zipcode);
        bikelistbtn = (Button) view.findViewById(R.id.submitbike);
        gobackbtn = (Button) view.findViewById(R.id.goback);

        bikelistingfrag = getContext();

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        userref = FirebaseDatabase.getInstance().getReference("users");
        bikelistref = FirebaseDatabase.getInstance().getReference();

        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    useremail = snapshot.child(uid).child("emailid").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        bikelistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidateBikeName() || !ValidateBikeType() || !ValidateRiderHeight() || !ValidateRates() || !ValidateStreet() || !ValidateCityProvince() || !ValidateZipCode()) {
                    return;
                }

                //Get all values
                String nameofbike = bikename.getText().toString();
                String typeofbike = biketype.getText().toString();
                String riderheight = riderht.getText().toString();
                String hour = hourrate.getText().toString();
                String day = dayrate.getText().toString();
                String week = weekrate.getText().toString();
                String str = street.getText().toString();
                String cipro = cityprovince.getText().toString();
                String zip = zipcode.getText().toString();

                String location = str + ", " + cipro + ", " + zip;

                if (user != null) {
                    ListingBikeClass listabike = new ListingBikeClass(useremail, nameofbike.toUpperCase(), typeofbike.toUpperCase(), riderheight, hour, day, week, location.toUpperCase());
                    ListingBikeClass listuserbike = new ListingBikeClass(nameofbike.toUpperCase(), typeofbike.toUpperCase(), riderheight, hour, day, week, location.toUpperCase());

                    bikelistref.child("listedbikes").push().setValue(listabike);
                    if (userref.child(uid).child("userlistedbikes") == null) {
                        userref.child(uid).setValue("userlistedbikes");
                        saveBikeInfo(listuserbike);
                    } else {
                        saveBikeInfo(listuserbike);
                    }
                    }
                }
        });

        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTitle(R.string.nav4);
                getFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new ListedFragment()).commit();
            }
        });

        return view;
    }

    private void saveBikeInfo(ListingBikeClass listuserbike) {
        userref.child(uid).child("userlistedbikes").push().setValue(listuserbike);
        new Handler().postDelayed(() -> {
            Toast.makeText(bikelistingfrag, "Your bike has been listed successfully!", Toast.LENGTH_LONG).show();
        }, 2000);

        getFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                new ListedFragment()).commit();
    }

    private boolean ValidateBikeName() {
        String val = bikename.getText().toString();

        if (val.isEmpty()) {
            bikename.setError("Field cannot be empty!");
            return false;
        } else {
            bikename.setError(null);
            return true;
        }
    }

    private boolean ValidateBikeType() {
        String val = biketype.getText().toString();

        if (val.isEmpty()) {
            biketype.setError("Field cannot be empty!");
            return false;
        } else {
            biketype.setError(null);
            return true;
        }
    }

    private boolean ValidateRiderHeight() {
        String val = riderht.getText().toString();
        String checkht = "^[12][0-9][0-9]";

        if (val.isEmpty()) {
            riderht.setError("Field cannot be empty!");
            return false;
        } else if (!val.matches(checkht)) {
            riderht.setError("Enter valid height!");
            return false;
        } else {
            biketype.setError(null);
            return true;
        }
    }

    private boolean ValidateRates() {
        String hour = hourrate.getText().toString();
        String day = dayrate.getText().toString();
        String week = weekrate.getText().toString();

        if (hour.isEmpty()) {
            hourrate.setError("Field cannot be empty!");
            return false;
        } else if (day.isEmpty()) {
            dayrate.setError("Field cannot be empty!");
            return false;
        } else if (week.isEmpty()) {
            weekrate.setError("Field cannot be empty!");
            return false;
        } else {
            hourrate.setError(null);
            dayrate.setError(null);
            weekrate.setError(null);
            return true;
        }
    }

    private boolean ValidateStreet() {
        String val = street.getText().toString();

        if (val.isEmpty()) {
            street.setError("Field cannot be empty!");
            return false;
        } else {
            street.setError(null);
            return true;
        }
    }

    private boolean ValidateCityProvince() {
        String val = cityprovince.getText().toString();
        String checkcityprovince = "^[a-zA-Z]+,[a-zA-Z]+$";

        if (val.isEmpty()) {
            cityprovince.setError("Field cannot be empty!");
            return false;
        } else if (!val.matches(checkcityprovince)) {
            cityprovince.setError("Enter details in City,State/Province format!");
            return false;
        } else {
            cityprovince.setError(null);
            return true;
        }
    }

    private boolean ValidateZipCode() {
        String val = zipcode.getText().toString();
        String checkzipcode = "^[a-zA-Z0-9]{6}";

        if (val.isEmpty()) {
            zipcode.setError("Field cannot be empty!");
            return false;
        } else if (!val.matches(checkzipcode)) {
            zipcode.setError("Enter valid Zip Code!");
            return false;
        } else {
            zipcode.setError(null);
            return true;
        }
    }
}
