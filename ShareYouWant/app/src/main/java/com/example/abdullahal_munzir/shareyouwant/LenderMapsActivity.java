package com.example.abdullahal_munzir.shareyouwant;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class LenderMapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private GoogleMap mMap;
    private Double Latitude,Longitude;
    private String user,name;
    private ImageView LenderImage;
    private TextView LenderLocation;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lender_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LenderImage=(ImageView) findViewById(R.id.lenderProfilePic);
        LenderLocation=(TextView) findViewById(R.id.lenderLocation);
  /*      Bundle bundle=getIntent().getExtras();
        Latitude=bundle.getDouble("lat");
        Longitude=bundle.getDouble("long");*/
        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user_id");
        context = getApplicationContext();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("users").child(user);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    name = dataSnapshot.child("name").getValue().toString();

                }
                if (dataSnapshot.hasChild("latitude")) {
                    Latitude = (double) dataSnapshot.child("latitude").getValue();

                }
                if (dataSnapshot.hasChild("longitude")) {
                    Longitude = (double) dataSnapshot.child("longitude").getValue();
                }
                if (dataSnapshot.hasChild("profile_image")) {
                    String image = dataSnapshot.child("profile_image").getValue().toString();
                   // Picasso.with(LenderPositionActivity.this).load(image).into(LenderImage);
                    RequestOptions options = new RequestOptions()
                            .dontAnimate()
                            .error(R.mipmap.ic_launcher_round);
                    Glide.with(context)
                            .load(image)
                            .apply(options)
                            .into(LenderImage);

                }
                LatLng latLngObj = new LatLng(Latitude,Longitude);
                Geocoder geocoder=new Geocoder(getApplicationContext());
                try{
                    List<Address> addressList=geocoder.getFromLocation(Latitude,Longitude,1);;
                    String Address=addressList.get(0).getAddressLine(0);
                    String sublocal=addressList.get(0).getSubLocality();
                    Toast.makeText(getApplicationContext(),""+Longitude,Toast.LENGTH_SHORT).show();
                    String position=sublocal+","+Address;
                    LenderLocation.setText(position);
                    mMap.addMarker(new MarkerOptions().position(latLngObj).title(name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngObj,14f));
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
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


    }
}