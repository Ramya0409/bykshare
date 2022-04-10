package com.example.bykshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RentalBikesAdapter extends RecyclerView.Adapter<RentalBikesAdapter.MyViewHolder> {
    private ArrayList<BikeRentEventsClass> rentallist;
    protected static final String TAG = "RentalBikesAdapter";
    Context cxt;
    MyViewHolder v;

    public RentalBikesAdapter(Context cxt, ArrayList<BikeRentEventsClass> rentallist) {
        this.cxt = cxt;
        this.rentallist = rentallist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView bikename, price, pickup, dropoff;
        private ImageButton locbtn;
        private Button paynow;

        private DatabaseReference ref;
        private FirebaseUser user;
        private String uid;
        private Address location;

        public static final String clientKey = "AYUvU9C75osFnk99cawHHEl4YjGZwsoe6QfPEEamkkdmrDrZg6ak6LZ-YZSq3TQS0-vOIwT2WJ3Mb6ul";
        public static final int PAYPAL_REQUEST_CODE = 123;

        // Paypal Configuration Object
        private PayPalConfiguration config = new PayPalConfiguration()
                // Start with mock environment.  When ready,
                // switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                // on below line we are passing a client id.
                .clientId(clientKey);

        public MyViewHolder(@NonNull View view) {
            super(view);

            user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference("users");

            bikename = (TextView) view.findViewById(R.id.rentalbike);
            price = (TextView) view.findViewById(R.id.rentprice);
            pickup = (TextView) view.findViewById(R.id.pick_up);
            dropoff = (TextView) view.findViewById(R.id.drop_off);
            locbtn = (ImageButton) view.findViewById(R.id.location_btn);
            paynow = (Button) view.findViewById(R.id.payrent);

            locbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pick_loc = getlocation(getAdapterPosition());
                    getLatLong(pick_loc);
                    double sourceLat = 43.47939024364975;
                    double sourceLong = -80.52529807312796;
                    String uri = "http://maps.google.com/maps?saddr=" + sourceLat + "," + sourceLong
                            + "&daddr=" + location.getLatitude() + "," + location.getLongitude();;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    cxt.startActivity(intent);
                }
            });

            paynow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPayment();
                }
            });

        }

        private void getLatLong(String pick_loc) {
            Geocoder code = new Geocoder(cxt);
            List<Address> address;

            try {
                address = code.getFromLocationName(pick_loc, 1);
                if (address == null) {
                    Toast.makeText(cxt, "No address found", Toast.LENGTH_LONG).show();
                }

                location = address.get(0);
            } catch (IOException ex) {

                ex.printStackTrace();
                Toast.makeText(cxt, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        private void getPayment() {
            String amount = (price.getText().toString()).substring(2);
            Log.i(TAG, amount);
            PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "CAD", "Total Fees",
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(cxt, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

            ((Activity) cxt).startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }

       public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
           if (requestCode == PAYPAL_REQUEST_CODE) {

               if (resultCode == Activity.RESULT_OK) {
                   PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                   if (confirm != null) {
                       Log.i(TAG, "The payment was successfull");
                   } else if (resultCode == Activity.RESULT_CANCELED) {
                       Log.i(TAG, "The user canceled.");
                   } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                       Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                   }
               }
           }
       }

        private String getlocation(int pos) {
            return rentallist.get(pos).getLocation();
        }
    }


    @NonNull
    @Override
    public RentalBikesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rentbike = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_bikes,parent,false);

        return new MyViewHolder(rentbike);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalBikesAdapter.MyViewHolder holder, int position) {
        String _bikename = rentallist.get(position).getBikename();
        String _price = rentallist.get(position).getPrice();
        String _pickup = rentallist.get(position).getPickup();
        String _dropoff = rentallist.get(position).getDropoff();

        holder.bikename.setText(_bikename);
        holder.price.setText(_price);
        holder.pickup.setText(_pickup);
        holder.dropoff.setText(_dropoff);
    }

    @Override
    public int getItemCount() {
        return rentallist.size();
    }
}
