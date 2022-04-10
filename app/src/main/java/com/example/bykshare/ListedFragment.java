package com.example.bykshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ListedFragment extends Fragment {

    Button bikelistingbtn;
    RecyclerView listofbikes;

    private ArrayList<ListingBikeClass> bikeslist;
    String bikename, hourrate, biketype, riderht;

    private String uid;
    private DatabaseReference ref;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listed_fragment, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference("users");

        listofbikes = (RecyclerView) view.findViewById(R.id.listedbikes);
        bikelistingbtn = (Button) view.findViewById(R.id.listabikebtn);

        bikelistingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTitle("List A Bicycle");
                getFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new BikeListingFragment()).commit();
            }
        });

        Query getuserbikes = ref.child(uid).child("userlistedbikes");
        bikeslist = new ArrayList<>();

        getuserbikes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(getTag(), String.valueOf(snapshot));
                if (snapshot.exists()) {
                    for(DataSnapshot bikelist : snapshot.getChildren()){
                        bikename = bikelist.child("nameofbike").getValue(String.class);
                        hourrate = bikelist.child("hourlyrate").getValue(String.class);
                        biketype = bikelist.child("biketype").getValue(String.class);
                        riderht = bikelist.child("riderheight").getValue(String.class);

                        String _hourrate = "$"+" "+hourrate;
                        String _riderht = riderht+" "+"cms";

                        ListingBikeClass bikedetails = new ListingBikeClass(bikename, _hourrate, biketype, _riderht);

                        bikeslist.add(bikedetails);
                        setAdapter();
                    }
                    Log.i(getTag(), String.valueOf(bikeslist.size()));

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

    public void setAdapter() {
        BikesListAdapter adapter = new BikesListAdapter(bikeslist);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
        listofbikes.setLayoutManager(layout);
        listofbikes.setItemAnimator(new DefaultItemAnimator());
        listofbikes.setAdapter(adapter);
    }
}
