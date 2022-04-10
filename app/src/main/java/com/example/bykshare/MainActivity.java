package com.example.bykshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static final String ACTIVITY_NAME = "MainActivity";
    private DrawerLayout drawer;
    Toolbar toolbar;

    String uid;

    FirebaseUser user;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(ACTIVITY_NAME, "in onCreate");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().add(R.id.empty_fragment,new MapsFragment()).commit();

        drawer = findViewById(R.id.navigationpage);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        ref = FirebaseDatabase.getInstance().getReference("users");

        NavigationView navview = (NavigationView) findViewById(R.id.nav_view);
        navview.setNavigationItemSelectedListener(this);

        View header = navview.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.username);

        if (uid != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(ACTIVITY_NAME, String.valueOf(snapshot));
                    if (snapshot.exists()) {
                        String uname = snapshot.child(uid).child("fullname").getValue(String.class);

                        username.setText(uname);

                    } else {
                        Log.d(ACTIVITY_NAME, "Snapshot does not exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.map_view:
                toolbar.setTitle(R.string.app_name);
                getSupportFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new MapsFragment()).commit();
                break;
            case R.id.acc_settings:
                toolbar.setTitle(R.string.nav2);
                getSupportFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new AccountFragment()).commit();
                break;
           /* case R.id.chats:
                toolbar.setTitle(R.string.nav3);
                getSupportFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new ChatFragment()).commit();
                break;*/
            case R.id.lister:
                toolbar.setTitle(R.string.nav4);
                getSupportFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new ListedFragment()).commit();
                break;
            case R.id.renter:
                toolbar.setTitle(R.string.nav5);
                getSupportFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new RentedFragment()).commit();
                break;
            case R.id.notification:
                toolbar.setTitle(R.string.nav6);
                getSupportFragmentManager().beginTransaction().replace(R.id.empty_fragment,
                        new NotificationFragment()).commit();
                break;
            case R.id.termsofservice:
                Uri uri = Uri.parse("https://www.termsandconditionsgenerator.com/live.php?token=UpuI8YMcEMd9JY39kOLmjWUaSWSLPXNG");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.logout:
                Intent logout = new Intent(this,LoginPage.class);
                startActivity(logout);
                Toast.makeText(this,"You are logged out!",Toast.LENGTH_LONG).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}