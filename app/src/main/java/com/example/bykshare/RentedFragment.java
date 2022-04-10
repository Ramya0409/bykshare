package com.example.bykshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RentedFragment extends Fragment {
    RecyclerView rentedbikes;

    private ArrayList<BikeRentEventsClass> rental_list;
    String bikename, pickup, dropoff, price, location;

    private String uid;
    private DatabaseReference ref;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rented_fragment, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference("users");

        rentedbikes = (RecyclerView) view.findViewById(R.id.rentedbikes);

        Query getrentbikes = ref.child(uid).child("rentedbikes");
        rental_list = new ArrayList<>();

        getrentbikes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(getTag(), String.valueOf(snapshot));
                if (snapshot.exists()) {
                    for(DataSnapshot bikelist : snapshot.getChildren()){
                        bikename = bikelist.child("bikename").getValue(String.class);
                        pickup = bikelist.child("pickup").getValue(String.class);
                        dropoff = bikelist.child("dropoff").getValue(String.class);
                        price = bikelist.child("price").getValue(String.class);
                        location = bikelist.child("location").getValue(String.class);

                        BikeRentEventsClass bikedetails = new BikeRentEventsClass(bikename, pickup, dropoff, price, location);

                        rental_list.add(bikedetails);
                        setAdapter();
                    }

                } else {
                    Log.d(getTag(), "Snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setAdapter() {
        RentalBikesAdapter adapter = new RentalBikesAdapter(getContext(),rental_list);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
        rentedbikes.setLayoutManager(layout);
        rentedbikes.setItemAnimator(new DefaultItemAnimator());
        rentedbikes.setAdapter(adapter);
    }
}
