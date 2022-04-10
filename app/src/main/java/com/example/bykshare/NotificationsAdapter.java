package com.example.bykshare;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>{
    private ArrayList<BikeRentEventsClass> notify_list;
    protected static final String TAG = "NotificationsAdapter";

    public NotificationsAdapter(ArrayList<BikeRentEventsClass> notifylist) {
        this.notify_list = notifylist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView bikename, renteremail, price, pickup, dropoff;
        private RadioGroup consent;
        private RadioButton yes, no;

        private DatabaseReference ref;
        private FirebaseUser user;
        private String uid;

        public MyViewHolder(View view){
            super(view);

            ref = FirebaseDatabase.getInstance().getReference("users");
            user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user.getUid();

            bikename = (TextView) view.findViewById(R.id.rent_bikename);
            renteremail = (TextView) view.findViewById(R.id.rentereid);
            price = (TextView) view.findViewById(R.id.price);
            pickup = (TextView) view.findViewById(R.id.pickup);
            dropoff = (TextView) view.findViewById(R.id.dropoff);
            consent = (RadioGroup) view.findViewById(R.id.approval);
            yes = (RadioButton) view.findViewById(R.id.yes);
            no = (RadioButton) view.findViewById(R.id.no);

            consent.clearCheck();
            consent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedid) {
                    RadioButton btn = (RadioButton) radioGroup.findViewById(checkedid);
                    String loc = getbikeloc(getAdapterPosition());

                    if( btn == yes){
                        Log.i(TAG, "The rental has been approved!");
                        String approval = "yes";
                        addNotification(approval, loc);
                        onApproval();
                    }
                    else if( btn == no){
                        Log.i(TAG, "The rental has not been approved!");
                        String approval = "no";
                        addNotification(approval, loc);
                        onApproval();
                    }
                }
            });
        }

        private void onApproval(){

            String bike_name = bikename.getText().toString();

            ref.child(uid).child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot notifylist : snapshot.getChildren()){
                        if(bike_name.equals(notifylist.child("bikename").getValue(String.class))){
                            notifylist.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage(), error.toException());
                }
            });
            removeAt(getAdapterPosition());
        }

        private void addNotification(String approval, String loc) {
            String _renteremail = renteremail.getText().toString();

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot list : snapshot.getChildren()){
                        if(_renteremail.equals(list.child("emailid").getValue(String.class))){
                            String uid = list.getKey();
                            findApproval(approval, uid, loc);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage(), error.toException());
                }
            });
        }

        private void findApproval(String approval, String uid, String loc) {
            String bike_name = bikename.getText().toString();
            String _price = price.getText().toString();
            String pick_up = pickup.getText().toString();
            String drop_off = dropoff.getText().toString();

            ref.child(uid).child("approvals").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot list : snapshot.getChildren()) {
                        if (bike_name.equals(list.child("bikename").getValue(String.class))) {
                            list.child("approved").getRef().setValue(approval);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage(), error.toException());
                }
            });

            ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BikeRentEventsClass rentbikeslist = new BikeRentEventsClass(bike_name, pick_up, drop_off, _price, loc);
                    if(snapshot.child("rentedbikes") == null){
                        snapshot.getRef().setValue("rentedbikes");
                        snapshot.getRef().child("rentedbikes").push().setValue(rentbikeslist);
                    }else{
                        snapshot.getRef().child("rentedbikes").push().setValue(rentbikeslist);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage(), error.toException());
                }
            });
        }

        private String getbikeloc(int pos) {
            return notify_list.get(pos).getLocation();
        }

        private void removeAt(int position) {
            notify_list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notify_list.size());
        }

    }

    @NonNull
    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View notifyview = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_notification,parent,false);
        return new MyViewHolder(notifyview);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.MyViewHolder holder, int position) {
        String _bikename = notify_list.get(position).getBikename();
        String _renteremail = notify_list.get(position).getRentereid();
        String _price = notify_list.get(position).getPrice();
        String _pickup = notify_list.get(position).getPickup();
        String _dropoff = notify_list.get(position).getDropoff();

        holder.bikename.setText(_bikename);
        holder.renteremail.setText(_renteremail);
        holder.price.setText(_price);
        holder.pickup.setText(_pickup);
        holder.dropoff.setText(_dropoff);
    }

    @Override
    public int getItemCount() {
        return notify_list.size();
    }
}
