package com.example.abdullahal_munzir.shareyouwant;

import android.*;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.SCALE_Y;
public class EditUserprofile extends AppCompatActivity  implements  View.OnClickListener {

    private static final String TAG = "ViewDatabase";

    private EditText editname,editemail,editcontact,dateOfBirth,Location;
    private Button updateinfo,choosePic,uploadPic;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private ImageView profilePic;
    private Uri filepath,resultpath;
    private static final int PICK_IMAGE_REQUEST=1;
    private StorageReference storageReference;
    private DatePickerDialog.OnDateSetListener mDatesetListener;
    private int age;
    private double latitude,longitude;
    private View chose_upload_layout;
    private boolean isEdit=false;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userprofile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();


        updateinfo = (Button) findViewById(R.id.updateInfo);
        editname = (EditText) findViewById(R.id.editUsername);
        editemail = (EditText) findViewById(R.id.editEmail);
        editcontact = (EditText) findViewById(R.id.editContactnum);
        choosePic=(Button) findViewById(R.id.choose_pic);
        uploadPic=(Button) findViewById(R.id.upload_pic);
        profilePic=(ImageView)findViewById(R.id.profile_pic);
        dateOfBirth=(EditText) findViewById(R.id.dateBirth);
        Location=(EditText) findViewById(R.id.userMap);
        chose_upload_layout = findViewById(R.id.chose_upload_layout);

        disableEdit(editname);
        disableEdit(editemail);
        disableEdit(editcontact);
        disableEdit(dateOfBirth);
        disableEdit(Location);
        chose_upload_layout.setVisibility(View.GONE);
        updateinfo.setVisibility(View.GONE);



/*        Location.setPaintFlags(Location.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        dateOfBirth.setPaintFlags(dateOfBirth.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);*/
/*        Bitmap bitmapn= BitmapFactory.decodeResource(getResources(),R.drawable.camera);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmapn);
        roundedBitmapDrawable.setCircular(true);
        profilePic.setImageDrawable(roundedBitmapDrawable);*/



        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser users = mAuth.getCurrentUser();
        userID = users.getUid();
        myRef = mFirebaseDatabase.getReference().child("users").child(userID);
        storageReference= FirebaseStorage.getInstance().getReference();
        choosePic.setOnClickListener(this);
        uploadPic.setOnClickListener(this);


        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit){
                    if (ActivityCompat.checkSelfPermission(EditUserprofile.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        // TODO: Consider calling

                        ActivityCompat.requestPermissions(EditUserprofile.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);

                        return;
                    }


                Log.d("internet", "onClick: ");
                startActivity(new Intent(EditUserprofile.this, UserMapsActivity.class));
                }
            }
        });


        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEdit){
                    Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(EditUserprofile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , mDatesetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                }

            }
        });
        mDatesetListener=new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                getAge(year,month,dayOfMonth);

                month=month+1;
                String date=dayOfMonth+"/"+month+"/"+year;
                if(age>=12) {
                    dateOfBirth.setText(date);
                }
                else{
                    toastMessage("You should be 12 years or more to use this app");
                }

            }
        };

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
                // ...
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("email")) {
                    String email = dataSnapshot.child("email").getValue().toString();
                    editemail.setText(email);
//                    mUserInfo.setText("\n" + email);
                    Log.d(TAG, "onDataChange: " + email);
                }

                if (dataSnapshot.hasChild("name")) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    editname.setText(name);
                    Log.d(TAG, "onDataChange: " + name);
                }

                if (dataSnapshot.hasChild("phone_num")) {
                    String phone_num = dataSnapshot.child("phone_num").getValue().toString();
                    editcontact.setText(phone_num);

                    Log.d(TAG, "onDataChange: " + phone_num);
                }

                if (dataSnapshot.hasChild("profile_image")) {
                    String image = dataSnapshot.child("profile_image").getValue().toString();
                    //Picasso.with(EditUserprofile.this).load(image).into(profilePic);
                    RequestOptions options = new RequestOptions()
                            .dontAnimate()
                            .error(R.mipmap.ic_launcher_round);
                    Glide.with(context)
                            .load(image)
                            .apply(options)
                            .into(profilePic);
                }
                if (dataSnapshot.hasChild("date_of_birth")) {
                    String birthday = dataSnapshot.child("date_of_birth").getValue().toString();
                    dateOfBirth.setText(birthday);
                    Log.d(TAG, "onDataChange: " + birthday);
                }
                if (dataSnapshot.hasChild("longitude")) {
                    longitude = (Double)dataSnapshot.child("longitude").getValue();

                }
                if (dataSnapshot.hasChild("latitude")) {
                    latitude = (Double)dataSnapshot.child("latitude").getValue();

                }
                Geocoder geocoder=new Geocoder(getApplicationContext());
                try{
                    List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
                    String sublocal=addressList.get(0).getSubLocality();
                    String Address=addressList.get(0).getAddressLine(0);
                    String userAddress=sublocal+","+ Address;
                    Location.setText(userAddress);

                }catch (Exception e){

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        //update user informaion
        updateinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit pressed.");
                String name = editname.getText().toString();
                String email = editemail.getText().toString();
                String phoneNum = editcontact.getText().toString();
                String userDateofBirth=dateOfBirth.getText().toString();


                //handle the exception if the EditText fields are null
                if(!name.equals("") && !email.equals("") && !phoneNum.equals("")&&!userDateofBirth.equals("")){
                    //UserInformation userInformation = new UserInformation(email,name,phoneNum,userDateofBirth);
                    //myRef.setValue(userInformation);
                    myRef.child("email").setValue(email);
                    myRef.child("name").setValue(name);
                    myRef.child("phone_num").setValue(phoneNum);
                    myRef.child("date_of_birth").setValue(userDateofBirth);
                    toastMessage("New Information has been saved.");


                }else{
                    toastMessage("Fill out all the fields");
                }

                recreate();
            }
        });
    }
    private int getAge(int year,int month,int day){
        //Date of birth
        Calendar dob=Calendar.getInstance();
        dob.set(year,month,day);
        //Current date
        Calendar today=Calendar.getInstance();
        age=today.get(Calendar.YEAR)-dob.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_YEAR)<dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;


    }
    //image choosing
    private void showFilechooser(){
        Intent intent=new Intent();
        intent.setAction(intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) {
            filepath=data.getData();
            CropImage.activity(filepath)
                   // .setFixAspectRatio(true)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                   // .setMaxCropResultSize(700,700)
                      .setMinCropResultSize(1500,1500)
                    .setAspectRatio(1,1)
                    .start(this);


           /*try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);

                profilePic.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
*/


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultpath= result.getUri();
                profilePic.setImageURI(resultpath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }





    //uploading image
    private void uploadFile(){
        if(resultpath!=null){
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("uploading......");
            progressDialog.show();
            StorageReference riversRef = storageReference.child("profile Images").child(userID+".jpg");

            riversRef.putFile(resultpath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            final String downloadUrl= taskSnapshot.getDownloadUrl().toString();
                            myRef.child("profile_image").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(EditUserprofile.this,"profile image stored to database succesfully....",Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                String message=task.getException().getMessage();
                                                Toast.makeText(EditUserprofile.this,"Error occured"+message,Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int)progress)+ "% Uploaded...");
                }
            })

            ;


        }
        else{

        }

    }





    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v==choosePic){
            showFilechooser();
        }

        else if(v==uploadPic){
            uploadFile();
        }
    }

    public void disableEdit(EditText v){
        v.setFocusable(false); //disabling edit text field
        v.setFocusableInTouchMode(false);
        v.setClickable(false);
        v.setCursorVisible(false);
    }
    public void enableEdit(EditText v){
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.setClickable(true);
        v.setCursorVisible(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d("click", "onOptionsItemSelected: "+"clicked");
                finish();
                return true;
            case R.id.action_edit:
                enableEdit(editname);
                enableEdit(editemail);
                enableEdit(editcontact);

                editname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_black_24dp, 0);
                editcontact.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_black_24dp, 0);
                Location.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_location_black_24dp, 0);
                dateOfBirth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_date_range_black_24dp, 0);
                chose_upload_layout.setVisibility(View.VISIBLE);
                updateinfo.setVisibility(View.VISIBLE);
                isEdit= true;


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == 99 )
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Location.performClick();
            }
            else
            {

            }
        }

        if(requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "coarse location permission granted");
            } else {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

}
