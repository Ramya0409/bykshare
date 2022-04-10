package com.example.bykshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignUpPage extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "SignUpPage";
    EditText fullname, emailid, mobileno, pwd, confirmpwd, otp;
    TextView verifyphoneno;
    CheckBox terms_conds;
    Button submitotp, signupbtn;
    RadioButton male,female,other;
    String gen;
    private FirebaseAuth phoneAuth;
    private String verificationId;

    private FirebaseAuth emailAuth;

    FirebaseDatabase rootnode;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        Log.i(ACTIVITY_NAME, "in onCreate");

        phoneAuth = FirebaseAuth.getInstance();

        emailAuth = FirebaseAuth.getInstance();

        fullname = (EditText) findViewById(R.id.fullName);
        male = (RadioButton) findViewById(R.id.dmale);
        female = (RadioButton) findViewById(R.id.dfemale);
        other = (RadioButton) findViewById(R.id.dother);



        emailid = (EditText) findViewById(R.id.userEmailId);
        mobileno = (EditText) findViewById(R.id.mobileNumber);
        verifyphoneno = (TextView) findViewById(R.id.getotp);
        verifyphoneno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is for checking whether the user
                // has entered his mobile number or not.
                if (TextUtils.isEmpty(mobileno.getText().toString())) {
                    // when mobile number text field is empty
                    // displaying a toast message.
                    Toast.makeText(SignUpPage.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    String phone = "+1" + mobileno.getText().toString();
                    sendVerificationCode(phone);
                    Toast.makeText(SignUpPage.this, "OTP sent!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        otp = (EditText) findViewById(R.id.otp);
        pwd = (EditText) findViewById(R.id.password);
        confirmpwd = (EditText) findViewById(R.id.confirmPassword);
        terms_conds = (CheckBox) findViewById(R.id.terms_conditions);
        submitotp = (Button) findViewById(R.id.submitotp);
        submitotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validating if the OTP text field is empty or not.
                if (TextUtils.isEmpty(otp.getText().toString())) {
                    // if the OTP text field is empty display
                    // a message to user to enter OTP
                    Toast.makeText(SignUpPage.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    // if OTP field is not empty calling
                    // method to verify the OTP.
                    verifyCode(otp.getText().toString());
                }
            }
        });

        signupbtn = (Button) findViewById(R.id.signUpBtn);
        //Save data in firebase on clicking the button
        signupbtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (!ValidateFullname() | !ValidateGender() | !ValidateEmail() | !ValidateMobile() | !ValidatePassword()) {
                    return;
                }
                //Get all the values
                String uname = fullname.getText().toString();
                String email = emailid.getText().toString();
                String password = pwd.getText().toString();
                String cfmpwd = confirmpwd.getText().toString();
                String mobile = mobileno.getText().toString();
                String phoneotp = otp.getText().toString();

                if ((terms_conds.isChecked()) && (password.equals(cfmpwd)) && (!phoneotp.isEmpty()) ) {
                    emailAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(ACTIVITY_NAME, "createUserWithEmail:success");
                                        FirebaseUser user = emailAuth.getCurrentUser();

                                        rootnode = FirebaseDatabase.getInstance();
                                        ref = rootnode.getReference("users");

                                        if (user != null) {

                                            UserHelperClass helperClass = new UserHelperClass(uname, email, password, mobile, gen);
                                            ref.child(user.getUid()).setValue(helperClass);

                                            Toast.makeText(getApplicationContext(), "You have successfully signed up!", Toast.LENGTH_LONG).show();
                                            Intent login = new Intent(getApplicationContext(), LoginPage.class);
                                            startActivity(login);
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(ACTIVITY_NAME, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpPage.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    if (!(terms_conds.isChecked())) {
                        Toast.makeText(getApplicationContext(), "Please accept the terms and conditions to proceed!", Toast.LENGTH_LONG).show();
                    } else if (!(password.equals(cfmpwd))) {
                        Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please verify mobile number by entering OTP!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        phoneAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Toast.makeText(SignUpPage.this, "Mobile Number Verified!", Toast.LENGTH_SHORT).show();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(SignUpPage.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(phoneAuth)
                        .setPhoneNumber(number)		 // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)				 // Activity (for callback binding)
                        .setCallbacks(mCallBack)		 // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                otp.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(SignUpPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private boolean ValidateFullname(){
        String val = fullname.getText().toString().trim();

        if(val.isEmpty()){
            fullname.setError("Field cannot be empty!");
            return false;
        }
        else{
            fullname.setError(null);
            return true;
        }
    }

    private boolean ValidateGender(){

        if (male.isChecked()) {
            gen = "male";
        } else if (female.isChecked()) {
            gen = "female";
        } else if (other.isChecked()){
            gen = "others";
        }

        if(gen == null){
            other.setError("Field cannot be empty!");
            return false;
        }
        else{
            other.setError(null);
            return true;
        }
    }

    private boolean ValidateEmail(){
        String val = emailid.getText().toString().trim();
        String checkemail = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

        if(val.isEmpty()){
            emailid.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkemail)){
            emailid.setError("Invalid Email ID!");
            return false;
        }
        else{
            emailid.setError(null);
            return true;
        }
    }

    private boolean ValidateMobile(){
        String val = mobileno.getText().toString().trim();
        String checkmobile = "^[2-9][0-9]{9}";

        if(val.isEmpty()){
            mobileno.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkmobile)){
            mobileno.setError("Enter valid mobile number!");
            return false;
        }
        else{
            mobileno.setError(null);
            return true;
        }
    }

    private boolean ValidatePassword(){
        String val = pwd.getText().toString().trim();
        String checkpwd = "(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{8,10})$";

        if(val.isEmpty()){
            pwd.setError("Field cannot be empty!");
            return false;
        }
        else if(!val.matches(checkpwd)){
            pwd.setError("Password must be between 8 and 10 characters, contain at least one digit and one alphabetic character, and must not contain special characters!");
            return false;
        }
        else{
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

        // Check if user is signed in (non-null) and update UI accordingly.
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

    public void loginpage(View view) {
        Intent loginpg = new Intent(this, LoginPage.class);
        startActivity(loginpg);
    }
}