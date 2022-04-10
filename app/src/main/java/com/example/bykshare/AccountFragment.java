package com.example.bykshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    EditText name, email, mobile, pswd;
    Button updatebtn;

    String uid;
    DatabaseReference ref;
    FirebaseUser user;

    String fullname, eid, mob, pwd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);

        name = (EditText) view.findViewById(R.id.namefield);
        email = (EditText) view.findViewById(R.id.emailfield);
        mobile = (EditText) view.findViewById(R.id.mobilefield);
        pswd = (EditText) view.findViewById(R.id.pswdfield);
        updatebtn = (Button) view.findViewById(R.id.updatebtn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference("users");

        showUserData(uid);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        return view;
    }

    private void showUserData(String uId) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(getTag(), String.valueOf(snapshot));
                if (snapshot.exists()) {
                    fullname = snapshot.child(uId).child("fullname").getValue(String.class);
                    eid = snapshot.child(uId).child("emailid").getValue(String.class);
                    mob = snapshot.child(uId).child("mobileno").getValue(String.class);
                    pwd = snapshot.child(uId).child("pwd").getValue(String.class);

                    name.setText(fullname);
                    email.setText(eid);
                    mobile.setText(mob);
                    pswd.setText(pwd);

                } else {
                    Log.d(getTag(), "Snapshot does not exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void update(){
        if(isNameChanged() || isEmailChanged() || isMobChanged() || isPwdChanged()){
            Toast.makeText(getContext(),"Changes have been saved!",Toast.LENGTH_LONG).show();
            NavigationView navview = (NavigationView) getActivity().findViewById(R.id.nav_view);
            View header = navview.getHeaderView(0);
            TextView username = (TextView) header.findViewById(R.id.username);
            username.setText(fullname);
        }
        else{
            Toast.makeText(getContext(),"No changes found!",Toast.LENGTH_LONG).show();
        }

    }

    private boolean isPwdChanged() {
        if((ValidatePassword()) && (! pwd.equals(pswd.getText().toString()))){
            ref.child(uid).child("pwd").setValue(pswd.getText().toString());

            user.updatePassword(pswd.getText().toString());
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isMobChanged() {
        if((ValidateMobile()) && (! mob.equals(mobile.getText().toString()))){
            ref.child(uid).child("mobileno").setValue(mobile.getText().toString());

            return true;
        }
        else{
            return false;
        }
    }

    private boolean isEmailChanged() {
        if((ValidateEmail()) && (! eid.equals(email.getText().toString()))){
            ref.child(uid).child("emailid").setValue(email.getText().toString());

            user.updateEmail(email.getText().toString());
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNameChanged() {
        if(! fullname.equals(name.getText().toString())){
            ref.child(uid).child("fullname").setValue(name.getText().toString());
            return true;
        }
        else{
            return false;
        }
    }

    private boolean ValidateEmail(){
        String val = email.getText().toString().trim();
        String checkemail = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

        if(val.isEmpty()){
            email.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkemail)){
            email.setError("Invalid Email ID!");
            return false;
        }
        else{
            email.setError(null);
            return true;
        }
    }

    private boolean ValidateMobile(){
        String val = mobile.getText().toString().trim();
        String checkmobile = "^[2-9][0-9]{9}";

        if(val.isEmpty()){
            mobile.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkmobile)){
            mobile.setError("Enter valid mobile number!");
            return false;
        }
        else{
            mobile.setError(null);
            return true;
        }
    }

    private boolean ValidatePassword(){
        String val = pswd.getText().toString().trim();
        String checkpwd = "(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{8,10})$";

        if(val.isEmpty()){
            pswd.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkpwd)){
            pswd.setError("Password must be between 8 and 10 characters, contain at least one digit and one alphabetic character, and must not contain special characters!");
            return false;
        }
        else{
            pswd.setError(null);
            return true;
        }
    }
}
