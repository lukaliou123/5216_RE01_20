package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.location.FusedLocationProviderClient;

import comp5216.sydney.edu.au.afinal.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MapsActivity.class.getSimpleName();

    // below are the latitude and longitude
    // of 4 different locations.
    LatLng sydney = new LatLng(-34, 151);
    LatLng TamWorth = new LatLng(-31.083332, 150.916672);
    LatLng NewCastle = new LatLng(-32.916668, 151.750000);
    LatLng Brisbane = new LatLng(-27.470125, 153.021072);

    // two array list for our lat long and location Name;
    private ArrayList<LatLng> latLngArrayList;
    private ArrayList<String> locationNameArraylist;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    int LIMIT = 1;

    private static ArrayList<Type> mArrayList = new ArrayList<>();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;
    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation = new Location("");

    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8688197, 151.2092955);
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private TextView locationTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        latLngArrayList = new ArrayList<>();
        locationNameArraylist = new ArrayList<>();
        initFirestore();
        getListItems();
        super.onCreate(savedInstanceState);



        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //System.out.println("arr size is:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: "+ mArrayList.size());

        // initializing our array lists.


        // on below line we are adding
        // data to our array list.
//        latLngArrayList.add(sydney);
//        locationNameArraylist.add("Sydney");
//        latLngArrayList.add(TamWorth);
//        locationNameArraylist.add("TamWorth");
//        latLngArrayList.add(NewCastle);
//        locationNameArraylist.add("New Castle");
//        latLngArrayList.add(Brisbane);
//        locationNameArraylist.add("Brisbase");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // below line is to add marker to google maps
        for (int i = 0; i < latLngArrayList.size(); i++) {

            // adding marker to each location on google maps
            mMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title("Marker in " + locationNameArraylist.get(i)));

            // below line is use to move camera.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

        // adding on click listener to marker of google maps.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // on marker click we are getting the title of our marker
                // which is clicked and displaying it in a toast message.
                String markerName = marker.getTitle();
                Toast.makeText(MapsActivity.this, "Clicked location is " + markerName, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            // Check if location permission is granted
            if (mLocationPermissionGranted) {
                mMap.getUiSettings().setMyLocationButtonEnabled(true); // false to disable my location button
                mMap.getUiSettings().setZoomControlsEnabled(true); // false to disable zoom controls
                mMap.getUiSettings().setCompassEnabled(true); // false to disable compass
                mMap.getUiSettings().setRotateGesturesEnabled(true); // false to disable rotate gesture
            } else {
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){
        try {
            if (mLocationPermissionGranted) {
                //mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                //new OnCompleteListener<Location>
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Obtain the current location of the device

                            mLastKnownLocation = task.getResult();
                            String currentOrDefault = "Current";

                            if (mLastKnownLocation != null) {
                                Log.i(task.getResult()+"","1111111111111111111111");
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                currentOrDefault = "Default";
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                                // Set current location to the default location
                                mLastKnownLocation = new Location("");
                                mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
                                mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
                            }
                            Log.i(mLastKnownLocation.getLatitude()+"","!!!!!!!!!!!!!!");
                            // Show location details on the location TextView
                            String msg = currentOrDefault + " Location: " +
                                    Double.toString(mLastKnownLocation.getLatitude()) + ", " +
                                    Double.toString(mLastKnownLocation.getLongitude());
                            locationTextView.setText(msg);

                            // Add a marker for my current location on the map
                            MarkerOptions marker = new MarkerOptions().position(
                                            new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                    .title("I am here");
                            mMap.addMarker(marker);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
// Get the 50 highest rated restaurants
//        mQuery = mFirestore.collection("events")
//                .limit(LIMIT);
    }

    private void getListItems() {
        String TAG = "sb!!!!!!!!!!!!!!!!!!!!!!!!";
//        mFirestore.collection("Events").document("z3dGI0sXu4uX8fe4QGGD").get()
//                .addOnCompleteListener(new OnCompleteListener()  {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
//                        String group_string= document.getData().toString();
//                        System.out.println("what is this!!!!!!!!!!!!!!!!!!!: "+group_string);
//                    }
//                });

            mFirestore.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint geo = (GeoPoint) document.getData().get("geo");
                                LatLng newLatng = new LatLng(geo.getLatitude(),geo.getLongitude());
                                latLngArrayList.add(newLatng);
                                String locName = (String)document.getData().get("location");
                                //System.out.println("lat is !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: "+geo);
                                locationNameArraylist.add(locName);
                                System.out.println("geo is !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: "+document.getData().get("location"));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            if(mMap != null){ //prevent crashing if the map doesn't exist yet (eg. on starting activity)
                                mMap.clear();


                            }
                            // add markers from database to the map
                            onMapReady(mMap);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }



}