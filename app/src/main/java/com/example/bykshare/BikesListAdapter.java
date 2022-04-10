package com.example.bykshare;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class BikesListAdapter extends RecyclerView.Adapter<BikesListAdapter.MyViewHolder>{

    private ArrayList<ListingBikeClass> bikeslist;
    protected static final String TAG = "BikesListAdapter";

    public BikesListAdapter(ArrayList<ListingBikeClass> bikeslist){
        this.bikeslist = bikeslist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView bikename, hourrate, biketype, riderht;
        private ImageButton delbtn;

        private DatabaseReference ref, bikelistref;
        private FirebaseUser user;
        private String uid;

        public MyViewHolder(View view){
            super(view);

            user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference("users");
            bikelistref = FirebaseDatabase.getInstance().getReference();

            bikename = (TextView) view.findViewById(R.id.nameofbike);
            hourrate = (TextView) view.findViewById(R.id.hourlyrate);
            biketype = (TextView) view.findViewById(R.id.biketype);
            riderht = (TextView) view.findViewById(R.id.riderheight);
            delbtn = (ImageButton) view.findViewById(R.id.deletebike);

            delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String nameofbike = getbikename(getAdapterPosition());
                    Query delbikeusr = ref.child(uid).child("userlistedbikes").orderByChild("nameofbike").equalTo(nameofbike);

                    delbikeusr.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    Query delbikegrp = bikelistref.child("listedbikes").orderByChild("nameofbike").equalTo(nameofbike);

                    delbikegrp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot bikegrp: snapshot.getChildren()) {
                                bikegrp.getRef().removeValue();
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

        private String getbikename(int pos) {
            return bikeslist.get(pos).getNameofbike();
        }

        private void removeAt(int position) {
            bikeslist.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bikeslist.size());
        }
    }

    @NonNull
    @Override
    public BikesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View bikeview = LayoutInflater.from(parent.getContext()).inflate(R.layout.listed_bikes,parent,false);
        return new MyViewHolder(bikeview);
    }

    @Override
    public void onBindViewHolder(@NonNull BikesListAdapter.MyViewHolder holder, int position) {
        String _bikename = bikeslist.get(position).getNameofbike();
        String _biketype = bikeslist.get(position).getBiketype();
        String _hourrate = bikeslist.get(position).getHourlyrate();
        String _riderht = bikeslist.get(position).getRiderheight();

        holder.bikename.setText(_bikename);
        holder.hourrate.setText(_hourrate);
        holder.biketype.setText(_biketype);
        holder.riderht.setText(_riderht);
    }

    @Override
    public int getItemCount() {
        return bikeslist.size();
    }
}
