package com.example.bykshare;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private GoogleMap gmap;
    /*private CameraPosition camposition;

    // The entry point to the Places API.
    private PlacesClient placesClient;*/
    Context mapfragment;

    SearchView search;

    private DatabaseReference ref1;
    private ArrayList<ListingBikeClass> mbikeaddress;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {

            gmap = googleMap;
            gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Prompt the user for permission.
            getLocationPermission();

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View mapview = inflater.inflate(R.layout.fragment_maps, container, false);

        mapfragment = getContext();

        ref1 = FirebaseDatabase.getInstance().getReference("listedbikes");

        mbikeaddress = new ArrayList<>();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
//            camposition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a PlacesClient
//        Places.initialize(mapfragment, BuildConfig.MAPS_API_KEY);
//        placesClient = Places.createClient(mapfragment);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mapfragment);

        search = (SearchView) mapview.findViewById(R.id.search_bar);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d(getTag(), String.valueOf(snapshot));

                for (DataSnapshot bikeinfo : snapshot.getChildren()) {

                    String loc = bikeinfo.child("location").getValue(String.class);
                    String _bikename = bikeinfo.child("nameofbike").getValue(String.class);
                    String _biketype = bikeinfo.child("biketype").getValue(String.class);
                    String _hourrate = bikeinfo.child("hourlyrate").getValue(String.class);
                    String _riderht = bikeinfo.child("riderheight").getValue(String.class);

                    String hour_rate = "$" + " " + _hourrate;
                    String rider_ht = _riderht + " " + "cms";

                    ListingBikeClass bike_info = new ListingBikeClass(_bikename, _biketype, rider_ht, hour_rate, loc);
                    mbikeaddress.add(bike_info);
                }
//                Log.i(getTag(), String.valueOf(mbikeaddress.size()));
                setMarkerForBikes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mapfragment, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String loc = search.getQuery().toString();
                List<Address> addressList = null;

                if (loc != null || !loc.equals("")) {
                    Geocoder geocoder = new Geocoder(mapfragment);
                    try {
                        addressList = geocoder.getFromLocationName(loc, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList.size() != 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    gmap.addMarker(new MarkerOptions().position(latLng).title(loc));
                        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    } else {
                        Toast.makeText(mapfragment, "No location matches the search input!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mapfragment, "Please provide an input to search!", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mapFragment.getMapAsync(callback);

        return mapview;
    }

    private void setMarkerForBikes() {

        Geocoder code = new Geocoder(mapfragment);
        List<Address> address;
        LatLng resLatLng = null;

        try {
            for (int i = 0; i < (mbikeaddress.size()); i++) {
                String pos = mbikeaddress.get(i).getLocation();
                String title = mbikeaddress.get(i).getNameofbike();
                String snippet = mbikeaddress.get(i).getHourlyrate() + "    " + mbikeaddress.get(i).getBiketype()
                        + "    " + mbikeaddress.get(i).getRiderheight();

                address = code.getFromLocationName(pos, 1);
                if (address == null) {
                    Toast.makeText(mapfragment, "No address found", Toast.LENGTH_LONG).show();
                }

                Address location = address.get(0);

                resLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                gmap.addMarker(new MarkerOptions()
                        .position(resLatLng)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                gmap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(mapfragment));

                gmap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        String _title = marker.getTitle();

                        Intent bikerent = new Intent(getActivity().getBaseContext(), BikeRenting.class);
                        bikerent.putExtra("BikeTitle", _title);
                        getActivity().startActivity(bikerent);
                    }
                });
            }
        } catch (IOException ex) {

            ex.printStackTrace();
            Toast.makeText(mapfragment, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Gets the current location of the device, and positions the map's camera.
    private void getDeviceLocation() {
        //Get the best and most recent location of the device, which may be null in rare
        //cases when a location is not available.
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(getTag(), "Current location is null. Using defaults.");
                            Log.e(getTag(), "Exception: %s", task.getException());
                            gmap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            gmap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    //Updates the map's UI settings based on whether the user has granted location permission.
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (gmap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                gmap.setMyLocationEnabled(true);
                gmap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                gmap.setMyLocationEnabled(false);
                gmap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Prompts the user for permission to use the device location.
    private void getLocationPermission() {
        // Request location permission, so that we can get the location of the
        // device. The result of the permission request is handled by a callback,
        // onRequestPermissionsResult.
        if (ContextCompat.checkSelfPermission(mapfragment,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Handles the result of the request for location permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    // Saves the state of the map when the activity is paused.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (gmap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, gmap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}