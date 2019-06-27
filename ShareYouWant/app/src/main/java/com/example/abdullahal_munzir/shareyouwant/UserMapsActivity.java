package com.example.abdullahal_munzir.shareyouwant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.abdullahal_munzir.shareyouwant.MyDirectory.CreateContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    LocationManager locationManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    String userID;
    private Button saveLocation;
    private  Double latitude=0.0,longitude=0.0;
    private DialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       dialog = new DialogFragment();


    mAuth=FirebaseAuth.getInstance();
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    myRef = mFirebaseDatabase.getReference();
    FirebaseUser users = mAuth.getCurrentUser();
    saveLocation=(Button)findViewById(R.id.saveLocation);

    userID = users.getUid();
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.
            permission.ACCESS_FINE_LOCATION) != PackageManager.
            PERMISSION_GRANTED) && (ActivityCompat.
            checkSelfPermission(this, android.Manifest.
            permission.ACCESS_COARSE_LOCATION) != PackageManager.
            PERMISSION_GRANTED)) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.

        return;
    }

    locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();

                        LatLng latLngObj=new LatLng(latitude,longitude);
                        Geocoder geocoder=new Geocoder(getApplicationContext());
                        try{
                            List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
                            String sublocal=addressList.get(0).getSubLocality();
                            String local=addressList.get(0).getLocality();
                            String Address=sublocal+","+local;
                            Toast.makeText(getApplicationContext(),""+longitude,Toast.LENGTH_SHORT).show();

                            mMap.addMarker(new MarkerOptions().position(latLngObj).title(Address));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngObj,14f));
                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }

                });
    }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude=location.getLatitude();
                longitude=location.getLongitude();

                LatLng latLngObj=new LatLng(latitude,longitude);
                Geocoder geocoder=new Geocoder(getApplicationContext());
                try{
                    List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);;
                    String Address=addressList.get(0).getAddressLine(0);
                    Toast.makeText(getApplicationContext(),""+longitude,Toast.LENGTH_SHORT).show();


                    mMap.addMarker(new MarkerOptions().position(latLngObj).title(Address));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngObj,14f));
                }catch (Exception e){

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


    }

    else {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserMapsActivity.this);
            builder.setMessage("Please enable location mode from settings and retry.")
                    .setTitle("Location Error!")
                    .setPositiveButton(android.R.string.ok,  new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();


        }

        saveLocation.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (latitude != 0.0 && longitude != 0.0){
                myRef.child("users").child(userID).child("latitude").setValue(latitude);
            myRef.child("users").child(userID).child("longitude").setValue(longitude);
            toastMessage("Your Location is saved");
            finish();
            }
        }
    });



}
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

   /* @Override
    public void onDestroy(){
        super.onDestroy();
        locationManager.removeUpdates((LocationListener) locationManager);

    }*/

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


    }


}
