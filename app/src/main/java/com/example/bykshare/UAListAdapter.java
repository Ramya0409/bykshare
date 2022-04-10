package com.example.bykshare;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UAListAdapter extends RecyclerView.Adapter<UAListAdapter.MyViewHolder> {
    private ArrayList<AddRemClass> ua_list;
    protected static final String TAG = "UAListAdapter";
    private String u_or_a;

    public UAListAdapter(ArrayList<AddRemClass> ua_list, String u_or_a) {
        this.ua_list = ua_list;
        this.u_or_a = u_or_a;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView uname, emailid;
        private ImageButton removebtn;

        public MyViewHolder(@NonNull View view) {
            super(view);

            uname = (TextView) view.findViewById(R.id.uname);
            emailid = (TextView) view.findViewById(R.id.eid);
            removebtn = (ImageButton) view.findViewById(R.id.remove_ua);

            removebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eid = getemail(getAdapterPosition());
                    if (u_or_a.equals("admin")) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admins");
                        remList(ref, eid);
                    } else {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                        remList(ref, eid);
                    }
                    removeAt(getAdapterPosition());
                }
            });
        }

        private void remList(DatabaseReference ref, String eid) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot info : snapshot.getChildren()) {
                        if (eid.equals(info.child("emailid").getValue(String.class))) {
                            info.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage(), error.toException());
                }
            });
        }

        private String getemail(int pos) {
            return ua_list.get(pos).getEmailid();
        }

        private void removeAt(int position) {
            ua_list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ua_list.size());
        }
    }

    @NonNull
    @Override
    public UAListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listview = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_delete,parent,false);
        return new MyViewHolder(listview);
    }

    @Override
    public void onBindViewHolder(@NonNull UAListAdapter.MyViewHolder holder, int position) {
        String user_name = ua_list.get(position).getUname();
        String email_id = ua_list.get(position).getEmailid();

        holder.uname.setText(user_name);
        holder.emailid.setText(email_id);
    }

    @Override
    public int getItemCount() {
        return ua_list.size();
    }
}
