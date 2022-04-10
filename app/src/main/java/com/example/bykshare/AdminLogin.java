package com.example.bykshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLogin extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "AdminLogin";
    private EditText emailid, pwd;
    private CheckBox show_pwd;

    String email, pswd;

    private FirebaseAuth emailAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        Log.i(ACTIVITY_NAME, "in onCreate");

        emailid = (EditText)findViewById(R.id.adm_emailid);
        pwd = (EditText)findViewById(R.id.adm_password);
        show_pwd = (CheckBox)findViewById(R.id.shpwd_adm);
        show_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked)
                {
                    show_pwd.setText(R.string.hide_pwd);
                    pwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    show_pwd.setText(R.string.show_pwd);
                    pwd.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        emailAuth = FirebaseAuth.getInstance();
    }

    public void loginAdmin(View view){
        if(!ValidateFields()){
            return;
        }

        //get data
        email = emailid.getText().toString().trim();
        pswd = pwd.getText().toString().trim();

        emailAuth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ACTIVITY_NAME, "signInWithEmail:success");

                            Intent admpage = new Intent(AdminLogin.this, AdminPage.class);
                            startActivity(admpage);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ACTIVITY_NAME, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AdminLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean ValidateFields(){
        String email = emailid.getText().toString().trim();
        String pswd = pwd.getText().toString().trim();

        if(email.isEmpty()){
            emailid.setError("Field cannot be empty!");
            emailid.requestFocus();
            return false;
        }
        else if(pswd.isEmpty()){
            pwd.setError("Field cannot be empty!");
            pwd.requestFocus();
            return false;
        }
        else{
            emailid.setError(null);
            pwd.setError(null);
            return true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");

        FirebaseUser currentUser = emailAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() { }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}