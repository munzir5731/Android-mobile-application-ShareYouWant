package com.example.abdullahal_munzir.shareyouwant.MyDirectory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.abdullahal_munzir.shareyouwant.R;
import com.example.abdullahal_munzir.shareyouwant.UserMapsActivity;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class CreateContent extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1000;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabasePost;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mUser;
    private ProgressDialog mProgress;

    ArrayAdapter<String> categoryAdapter;
    Boolean selectanother_item=false;
    Spinner spinner_cat;
    String [] catList= {"Select Category","Book", "Game CD", "Out Fits"};

    Button btn_choose_photo, btn_share;
    EditText txt_name, txt_author_platform, txt_details, txt_demand;
    ImageView brows_image;
    Uri filePath, resultUri;
    CheckBox negotiable_chk;
    boolean isBook=false, isGame=false;

    View imageLayout, demandLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

              /*  Snackbar snackbar = Snackbar
                        .make(view, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                recreate();

                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.WHITE);
                // Changing action button text color
                Context context= getApplicationContext();
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.white));
                sbView.setBackgroundColor(getResources().getColor(R.color.snakbar_red));
                snackbar.show(); */

        btn_choose_photo = findViewById(R.id.btn_choose_image);
        btn_share = findViewById(R.id.btn_share);

        brows_image = findViewById(R.id.image_view);
        negotiable_chk = findViewById(R.id.check_negotiable);

        txt_name = findViewById(R.id.txt_name);
        txt_author_platform= findViewById(R.id.txt_author_platform);
        txt_details = findViewById(R.id.txt_details);
        txt_demand = findViewById(R.id.txt_demand);
        imageLayout = findViewById(R.id.image_layout);
        demandLayout = findViewById(R.id.demand_layout);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabasePost = mDatabase.child("posts");


        Log.d("Uid", "onCreate: " + mCurrentUser.getUid());
        mUser = mDatabase.child("users").child(mCurrentUser.getUid());


        mProgress = new ProgressDialog(this);

        spinner_cat = findViewById(R.id.spn_cat_activity);


        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, catList);

        spinner_cat.setAdapter(categoryAdapter);

        spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                switch (i) {
                    case 0:
                        txt_name.setVisibility(View.GONE);
                        txt_author_platform.setVisibility(View.GONE);
                        txt_details.setVisibility(View.GONE);
                        imageLayout.setVisibility(View.GONE);
                        demandLayout.setVisibility(View.GONE);
                        btn_share.setVisibility(View.GONE);

                        break;

                    case 1:

                        isBook=true;

                        txt_name.setVisibility(View.VISIBLE);
                        slideUp(txt_name);
                        txt_author_platform.setVisibility(View.VISIBLE);
                        slideUp(txt_author_platform);
                        txt_details.setVisibility(View.VISIBLE);
                        slideUp(txt_details);
                        demandLayout.setVisibility(View.VISIBLE);
                        slideUp(demandLayout);
                        imageLayout.setVisibility(View.VISIBLE);
                        slideUp(imageLayout);

                        txt_name.setHint("Book Name");
                        txt_author_platform.setHint("Book Author");
                        txt_details.setHint("Book Details");

                        isGame=false;


                        break;

                    case 2:

                        isGame=true;

                        txt_name.setVisibility(View.VISIBLE);
                        txt_author_platform.setVisibility(View.VISIBLE);
                        txt_details.setVisibility(View.VISIBLE);
                        demandLayout.setVisibility(View.VISIBLE);
                        imageLayout.setVisibility(View.VISIBLE);

                        txt_name.setHint("Game Name");
                        txt_author_platform.setHint("Game Platform");
                        txt_details.setHint("Game Details");

                        isBook=false;


                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        txt_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txt_name.getText().toString().trim().length()<1){
                    txt_name.setError("Required");
                }
            }
        });

        txt_author_platform.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txt_author_platform.getText().toString().trim().length()<1){
                    txt_author_platform.setError("Required");
                }
            }
        });

        txt_demand.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txt_demand.getText().toString().trim().length()<1){
                    txt_demand.setError("Required");
                }
            }
        });

        txt_demand.addTextChangedListener(new TextWatcher(){

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (txt_demand.getText().toString().matches("^0") )
                {
                    // Not allowed
                    txt_demand.setError("Minimum value 1");
                    txt_demand.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable arg0) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });








        btn_choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoseImage();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemName =  txt_name.getText().toString().trim();
                String itemAuthorOrPlatform =  txt_author_platform.getText().toString().trim();
                String itemDetails =  txt_details.getText().toString().trim();
                String demand = txt_demand.getText().toString().trim();
                int itemDemand =0;
                if(!demand.isEmpty()) {
                    itemDemand = Integer.parseInt(demand);
                }
                String Category;
                if(isBook) {
                    Category = "Book";
                }
                else if(isGame){
                    Category = "Game";
                }
                else Category = "Out Fit";

                Boolean checkState=false;
                if(negotiable_chk.isChecked()) {
                    checkState=true;
                }

                if(itemName.length()>=1 && itemAuthorOrPlatform.length()>=1 && itemDemand >=1 && resultUri != null) {
                   // bookDataPasser.onBookDataPass(Category, bookName, bookAuthor, bookDetails, resultUri, bookDemand, checkState);
                    startPosting(Category, itemName, itemAuthorOrPlatform, itemDetails, resultUri, itemDemand,checkState);
                }
                else {
                    if(itemName.length() == 0){txt_name.setError("Required");}
                    if(itemAuthorOrPlatform.length() == 0){txt_author_platform.setError("Required");}
                    if(itemDemand == 0){txt_demand.setError("Minimum value is 1");}
                    if(resultUri == null){
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Please Select an Image",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });






    }

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(1000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                //view.getHeight()
                1000); // toYDelta
        animate.setDuration(1000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private void ChoseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int ResultCode, Intent data){
        super.onActivityResult(requestCode, ResultCode, data);
        if(ResultCode== Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST ){

            filePath = data.getData();

            CropImage.activity(filePath)
                    .setAutoZoomEnabled(true)
                    .setActivityTitle("Adjustment")
                    .start(this);


           /* Bitmap bitmapImage;

            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                bookImage.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
            }*/


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (ResultCode == RESULT_OK) {
                resultUri = result.getUri();

                brows_image.setImageURI(resultUri);
                btn_share.setVisibility(View.VISIBLE);
                slideUp(btn_share);

            } else if (ResultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Context context = getApplicationContext();
                Toast.makeText(context, ""+error ,Toast.LENGTH_LONG).show();
            }
        }

        else {
            Context context = getApplicationContext();
            ///Toast.makeText(context, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public void startPosting(final String Category, final String ItemName, final String ItemAuthorOrPlatform, final String ItemDetails, Uri ImageURI, final double ItemDemand, final Boolean checkState){
        mProgress.setTitle("Your post is creating..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        Bitmap bitmapImage;
        byte[] data;

        try {
            bitmapImage = MediaStore.Images.Media.getBitmap( getContentResolver(), ImageURI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            data = baos.toByteArray();




            StorageReference filePath = mStorage.child("book_Image").child("image"+System.currentTimeMillis());

            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final long time_millis = (System.currentTimeMillis())*(-1);

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost = mDatabasePost.push();
                    if(isBook){
                        newPost.child("author").setValue(ItemAuthorOrPlatform);
                    }
                    else {
                        newPost.child("platform").setValue(ItemAuthorOrPlatform);
                    }

                    newPost.child("title").setValue(ItemName);
                    newPost.child("details").setValue(ItemDetails);
                    newPost.child("demand").setValue(ItemDemand);
                    newPost.child("category").setValue(Category);
                    newPost.child("date_time").setValue(time_millis);
                    newPost.child("negotiable").setValue(checkState);
                    newPost.child("image_uri").setValue(downloadUrl.toString());
                    newPost.child("uid").setValue(mCurrentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgress.dismiss();
                                spinner_cat.setSelection(categoryAdapter.getPosition("Select Category"));

                                AlertDialog.Builder builder = new AlertDialog.Builder(CreateContent.this);
                                builder.setMessage("Post created successfully")
                                        .setTitle("Success")
                                        .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();


                            }
                            else{
                                mProgress.dismiss();
                            }
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mProgress.dismiss();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double totalProgress = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            mProgress.setMessage("Progressing"+"  "+(int)totalProgress+"%");
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
                return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateContent.this);
        builder.setMessage("Are you sure yoy want to discard?")
                .setTitle("Discard?")
                .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
