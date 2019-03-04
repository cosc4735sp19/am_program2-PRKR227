package com.example.photomaps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    static final LatLng CHEYENNE = new LatLng(41.1400, -104.8197);
    static final LatLng LARAMIE = new LatLng(41.312928, -105.587253);
    private FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    public static final int REQUEST_ACCESS_onConnected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.cameraButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        getLastLocation();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MapsActivity.LARAMIE, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Sets the map type to be "hybrid"
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL); //normal map
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //allow user to use zoom controls (ie the + - buttons on the map.
        map.getUiSettings().setZoomControlsEnabled(true);

        //add a marker click event.
        map.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker myMarker) {
                Toast.makeText(getApplicationContext(), "Clicked the " + myMarker.getTitle() + " Marker", Toast.LENGTH_SHORT).show();

                //return true;  //yes we consumed the event.
                return false; //so the default action is shown as well.
            }

        });


        //add map click listener.
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                Toast.makeText(getApplicationContext(), "Lat: " + point.latitude+ " Long:" +point.longitude,  Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //the picture is stored in the intent in the data key.
        //get the picture and show it in an the imagview.
        //Note the picture is not stored on the filesystem, so this is the only "copy" of the picture.
        Bundle extras = data.getExtras();
        if (extras != null) {
            //if you know for a fact there will be a bundle, you can use  data.getExtras().get("Data");  but we don't know.
            Bitmap bp = (Bitmap) extras.get("data");
            //mf.setPic(bp);
            getLastLocation();
            LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromBitmap(bp)));

        } else {
            Toast.makeText(this, "No picture was returned", Toast.LENGTH_SHORT).show();
        }
    }

    public void getLastLocation() {
        //first check to see if I have permissions (marshmallow) if I don't then ask, otherwise start up the demo.
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            //I'm on not explaining why, just asking for permission.
           // Log.v(TAG, "asking for permissions");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MapsActivity.REQUEST_ACCESS_onConnected);

            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            return;
                        }
                        mLastLocation = location;

                        //Log.v(TAG, "getLastLocation");
                        if (mLastLocation != null) {
                            //startIntentService();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }
}
