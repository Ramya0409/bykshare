package com.example.bykshare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    RecyclerView notifications, approvals;

    private ArrayList<BikeRentEventsClass> notify_list;
    private ArrayList<BikeRentEventsClass> approval_list;
    String bikename, rentereid, pickup, dropoff, price, _approval, loc;

    private String uid;
    private DatabaseReference ref;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference("users");

        notifications = (RecyclerView) view.findViewById(R.id.notifications);
        approvals = (RecyclerView) view.findViewById(R.id.approvals);

        getNotifications();

        getApprovals();

        return view;
    }

    private void getNotifications(){
        Query getnotifications = ref.child(uid).child("notifications");
        notify_list = new ArrayList<>();

        getnotifications.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot listofnot : snapshot.getChildren()) {
                        rentereid = listofnot.child("rentereid").getValue(String.class);
                        bikename = listofnot.child("bikename").getValue(String.class);
                        pickup = listofnot.child("pickup").getValue(String.class);
                        dropoff = listofnot.child("dropoff").getValue(String.class);
                        price = listofnot.child("price").getValue(String.class);
                        _approval = listofnot.child("approved").getValue(String.class);
                        loc = listofnot.child("location").getValue(String.class);

                        BikeRentEventsClass list = new BikeRentEventsClass(rentereid,bikename,pickup,dropoff,price,_approval,loc);
                        notify_list.add(list);
                        if(!(ref.child(uid).child("notifications") == null)) {
                            setBikeRequestAdapter();
                        }
                    }
                }else {
                    Log.d(getTag(), "Snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getApprovals(){
        Query getapprovals = ref.child(uid).child("approvals");
        approval_list = new ArrayList<>();

        getapprovals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot applist : snapshot.getChildren()) {
                        rentereid = applist.child("rentereid").getValue(String.class);
                        bikename = applist.child("bikename").getValue(String.class);
                        pickup = applist.child("pickup").getValue(String.class);
                        dropoff = applist.child("dropoff").getValue(String.class);
                        price = applist.child("price").getValue(String.class);
                        _approval = applist.child("approved").getValue(String.class);

                        BikeRentEventsClass a_list = new BikeRentEventsClass(rentereid,bikename,pickup,dropoff,price,_approval);
                        approval_list.add(a_list);

                        if(!(ref.child(uid).child("approvals") == null)) {
                            setApprovalsAdapter();
                        }
                    }
                }else {
                    Log.d(getTag(), "Snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setApprovalsAdapter() {
        ApprovalAdapter adapter = new ApprovalAdapter(approval_list);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
        approvals.setLayoutManager(layout);
        approvals.setItemAnimator(new DefaultItemAnimator());
        approvals.setAdapter(adapter);
    }

    private void setBikeRequestAdapter() {
        NotificationsAdapter adapter = new NotificationsAdapter(notify_list);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
        notifications.setLayoutManager(layout);
        notifications.setItemAnimator(new DefaultItemAnimator());
        notifications.setAdapter(adapter);
    }
}