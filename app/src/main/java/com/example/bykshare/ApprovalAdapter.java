package com.example.bykshare;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.MyViewHolder>{
    private ArrayList<BikeRentEventsClass> approve_list;
    protected static final String TAG = "ApprovalAdapter";

    public ApprovalAdapter(ArrayList<BikeRentEventsClass> approvelist) {
        this.approve_list = approvelist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView bikename, renteremail, price, pickup, dropoff, setapproval;
        private ImageButton delbtn;

        private DatabaseReference ref;
        private FirebaseUser user;
        private String uid;

        public MyViewHolder(@NonNull View view) {
            super(view);

            user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference("users");

            bikename = (TextView) view.findViewById(R.id.rent_bn);
            renteremail = (TextView) view.findViewById(R.id.rent_eid);
            price = (TextView) view.findViewById(R.id.r_price);
            pickup = (TextView) view.findViewById(R.id.r_pickup);
            dropoff = (TextView) view.findViewById(R.id.r_dropoff);
            setapproval = (TextView) view.findViewById(R.id.consent);
            delbtn = (ImageButton) view.findViewById(R.id.notifydel);

            delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String nameofbike = bikename.getText().toString();
                    Query delapproval = ref.child(uid).child("approvals").orderByChild("bikename").equalTo(nameofbike);

                    delapproval.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot bikeinfo: snapshot.getChildren()) {
                                bikeinfo.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, error.getMessage(), error.toException());
                        }
                    });
                    removeAt(getAdapterPosition());
                }
            });
        }

        private void removeAt(int position) {
            approve_list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, approve_list.size());
        }
    }

    @NonNull
    @Override
    public ApprovalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View approveview = LayoutInflater.from(parent.getContext()).inflate(R.layout.approval_notification,parent,false);
        return new MyViewHolder(approveview);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalAdapter.MyViewHolder holder, int position) {
        String _bikename = approve_list.get(position).getBikename();
        String _renteremail = approve_list.get(position).getRentereid();
        String _price = approve_list.get(position).getPrice();
        String _pickup = approve_list.get(position).getPickup();
        String _dropoff = approve_list.get(position).getDropoff();
        String _approval = approve_list.get(position).getApproved();

        holder.bikename.setText(_bikename);
        holder.renteremail.setText(_renteremail);
        holder.price.setText(_price);
        holder.pickup.setText(_pickup);
        holder.dropoff.setText(_dropoff);

        if(_approval.equals("yes")){
            holder.setapproval.setText("Rental has been approved by owner");
        }else if(_approval.equals("no")){
            holder.setapproval.setText("Rental has been rejected by owner");
        }else{
            holder.setapproval.setText("Approval still pending on owner");
        }
    }

    @Override
    public int getItemCount() {
        return approve_list.size();
    }
}
