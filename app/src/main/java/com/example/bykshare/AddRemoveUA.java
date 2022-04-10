package com.example.bykshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddRemoveUA extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "AddRemoveUA";
    RecyclerView list;
    Button add_admin;
    EditText username, email_id, password;

    private ArrayList<AddRemClass> ua_list;
    String uname, emailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_ua);

        list = (RecyclerView) findViewById(R.id.addremovelist);
        add_admin = (Button) findViewById(R.id.add_ua);

        Intent getval = getIntent();
        String val = getval.getStringExtra("UorA");

        if(val.equals("Admins")){
            add_admin.setText("Add New Admin");
            getSupportActionBar().setTitle("Add/Remove Admins");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admins");
            adminsList(ref, "admin");
        }else {
            add_admin.setText("Add New User");
            getSupportActionBar().setTitle("Add/Remove Users");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            usersList(ref, "user");
        }

        ua_list = new ArrayList<>();

        add_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(val.equals("Admins")){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admins");
                    showDialogBox(ref);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Users cannot be added!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialogBox(DatabaseReference ref) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_admin_dialog);

        username = (EditText) dialog.findViewById(R.id.uName);
        email_id = (EditText) dialog.findViewById(R.id.adm_eid);
        password = (EditText) dialog.findViewById(R.id.adm_pwd);
        Button addbtn = (Button) dialog.findViewById(R.id.addbtn);
        ImageButton closedialog = (ImageButton) dialog.findViewById(R.id.closedialog);

        closedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usr_name = username.getText().toString();
                String adm_eid = email_id.getText().toString();
                String adm_pwd = password.getText().toString();

                if(!ValidateUname() || !ValidateEmail() || !ValidatePassword()){
                    return;
                }

                AddRemClass admininfo = new AddRemClass(usr_name, adm_eid, adm_pwd);
                ref.push().setValue(admininfo);

                Toast.makeText(getApplicationContext(),"New admin has been added!", Toast.LENGTH_SHORT).show();
                Intent goback = new Intent(dialog.getContext(), AddRemoveUA.class);
                goback.putExtra("UorA", "Admins");
                startActivity(goback);
            }
        });

        dialog.show();
    }

    private void usersList(DatabaseReference ref, String val){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(ACTIVITY_NAME, String.valueOf(snapshot));
                if (snapshot.exists()) {
                    for(DataSnapshot userslist : snapshot.getChildren()){
                        uname = userslist.child("fullname").getValue(String.class);
                        emailid = userslist.child("emailid").getValue(String.class);

                        AddRemClass userinfo = new AddRemClass(uname, emailid);

                        ua_list.add(userinfo);
                        setAdapter(val);
                    }

                } else {
                    Log.d(ACTIVITY_NAME, "Snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void adminsList(DatabaseReference ref, String val){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(ACTIVITY_NAME, String.valueOf(snapshot));
                if (snapshot.exists()) {
                    for(DataSnapshot adminslist : snapshot.getChildren()){
                        uname = adminslist.child("uname").getValue(String.class);
                        emailid = adminslist.child("emailid").getValue(String.class);

                        AddRemClass admininfo = new AddRemClass(uname, emailid);

                        ua_list.add(admininfo);
                        setAdapter(val);
                    }

                } else {
                    Log.d(ACTIVITY_NAME, "Snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean ValidateUname(){
        String val = username.getText().toString().trim();

        if(val.isEmpty()){
            username.setError("Field cannot be empty!");
            return false;
        }
        else{
            username.setError(null);
            return true;
        }
    }

    private boolean ValidateEmail(){
        String val = email_id.getText().toString().trim();
        String checkemail = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

        if(val.isEmpty()){
            email_id.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkemail)){
            email_id.setError("Invalid Email ID!");
            return false;
        }
        else{
            email_id.setError(null);
            return true;
        }
    }

    private boolean ValidatePassword(){
        String val = password.getText().toString().trim();
        String checkpwd = "(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{8,10})$";

        if(val.isEmpty()){
            password.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkpwd)){
            password.setError("Password must be between 8 and 10 characters, contain at least one digit and one alphabetic character, and must not contain special characters!");
            return false;
        }
        else{
            password.setError(null);
            return true;
        }
    }

    private void setAdapter(String val) {
        UAListAdapter adapter = new UAListAdapter(ua_list, val);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getApplicationContext());
        list.setLayoutManager(layout);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(adapter);
    }
}