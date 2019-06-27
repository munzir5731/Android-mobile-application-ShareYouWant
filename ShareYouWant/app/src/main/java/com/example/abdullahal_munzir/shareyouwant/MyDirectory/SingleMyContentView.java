package com.example.abdullahal_munzir.shareyouwant.MyDirectory;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.abdullahal_munzir.shareyouwant.DateTime;
import com.example.abdullahal_munzir.shareyouwant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class SingleMyContentView extends AppCompatActivity {

    private DatabaseReference mDatabase,mUpdateDatabase, updateTitle, updateImageUri, updateAuthor, updateDemand, updateDetails;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    TextView my_content_date_time, my_content_category,txtAuthor_Platform;
    EditText my_content_author_platform, my_content_details,my_content_title , my_content_demand;
    ImageView my_content_image;
    Button change_image;
    FloatingActionButton edit_my_single_content, delet_my_single_content, update_my_single_content;
    private Uri filePath, resultUri=null;
    private ProgressDialog mProgress;
    String mPost_key, image_uri;

    private final int PICK_IMAGE_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_my_content_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final LinearLayout layout = findViewById(R.id.image_layout);

        final Context context = getApplicationContext();
        DateTime dateTime  = new DateTime();




        //deleteCache(this);
        mPost_key = getIntent().getExtras().getString("post_id");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUpdateDatabase = mDatabase.child(mPost_key);

        updateTitle = mUpdateDatabase.child("title");
        updateAuthor = mUpdateDatabase.child("author");
        updateDetails = mUpdateDatabase.child("details");
        updateDemand = mUpdateDatabase.child("demand");
        updateImageUri = mUpdateDatabase.child("image_uri");

        my_content_title = findViewById(R.id.my_content_title);
        my_content_date_time = findViewById(R.id.my_content_date);
        my_content_demand = findViewById(R.id.my_content_demand);
        my_content_category = findViewById(R.id.my_content_category);
        my_content_image = findViewById(R.id.my_content_image);
        my_content_author_platform = findViewById(R.id.my_content_author_platform);
        txtAuthor_Platform= findViewById(R.id.txtAuthor_Platform);
        my_content_details = findViewById(R.id.my_content_details);

        edit_my_single_content = findViewById(R.id.edit_my_single_content);
        delet_my_single_content = findViewById(R.id.delet_my_single_content);
        update_my_single_content = findViewById(R.id.update_my_single_content);

        change_image = findViewById(R.id.change_image);

        //mProgressBar = findViewById(R.id.mProgressBar);
        mProgress = new ProgressDialog(this);

        change_image.setVisibility(View.GONE); //hiding change image Button
        update_my_single_content.setVisibility(View.GONE); // hiding upadte button

        nonEditable(my_content_details);
        nonEditable(my_content_title);
        nonEditable(my_content_demand);
        nonEditable(my_content_author_platform);
        Toast.makeText(getApplicationContext(), mPost_key, Toast.LENGTH_SHORT).show();

        my_content_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        });



            mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        String post_title = (String) dataSnapshot.child("title").getValue();
                    String post_category = (String) dataSnapshot.child("category").getValue();
                        if ((post_category).equals("Game")) {
                            String platform = (String) dataSnapshot.child("platform").getValue();
                            my_content_author_platform.setText(platform);
                            txtAuthor_Platform.setText("Platform: ");
                            my_content_category.setBackgroundResource(R.drawable.game_tag_background);
                            my_content_category.setText(platform);
                        } else {
                            String author = (String) dataSnapshot.child("author").getValue();
                            my_content_author_platform.setText(author);
                            my_content_author_platform.setText(author);
                            txtAuthor_Platform.setText("Author: ");
                            my_content_category.setBackgroundResource(R.drawable.book_tag_background);
                        }
                    String post_details = (String) dataSnapshot.child("details").getValue();
                    image_uri = (String) dataSnapshot.child("image_uri").getValue();
                    long demand = (long) dataSnapshot.child("demand").getValue();
                    long date_time = (long) dataSnapshot.child("date_time").getValue();
                    Boolean isNegotiable = (Boolean) dataSnapshot.child("negotiable").getValue();
                    date_time = date_time*(-1);
                    String timeMillis = Long.toString(date_time);
                    String date = dateTime.convertDate(timeMillis, "d MMM yyyy 'at' hh:mm aaa");


                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.spin)
                            .error(R.mipmap.ic_launcher_round);
                    Glide.with(context)
                            .load(image_uri)
                            .apply(options)
                            .into(my_content_image);

                    my_content_category.setText(post_category);
                    my_content_title.setText(post_title);
                    my_content_demand.setText("" + demand);
                    my_content_details.setText(post_details);
                    my_content_date_time.setText(date);

                }
            }

                @Override
        public void onCancelled(DatabaseError databaseError) {


        }
    });




        edit_my_single_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                change_image.setVisibility(View.VISIBLE); // showing change image button
                update_my_single_content.setVisibility(View.VISIBLE); // showing update button
                delet_my_single_content.setVisibility(View.GONE); // hiding delet button
                edit_my_single_content.setVisibility(View.GONE); // hiding edit button

                my_content_details.setBackgroundResource(R.drawable.edit_background);
                my_content_author_platform.setBackgroundResource(R.drawable.edit_background);
                my_content_title.setBackgroundResource(R.drawable.edit_background);
                my_content_demand.setBackgroundResource(R.drawable.edit_background);
                my_content_demand.setBackgroundResource(R.drawable.edit_background);
                my_content_title.setBackgroundResource(R.drawable.edit_background);

                editable(my_content_details);
                editable(my_content_title);
                editable(my_content_demand);
                editable(my_content_author_platform);


            }
        });


        update_my_single_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title =  my_content_title.getText().toString().trim();
                String author =  my_content_author_platform.getText().toString().trim();
                String details =  my_content_details.getText().toString().trim();
                int  demand = Integer.parseInt( my_content_demand.getText().toString().trim());

                Boolean checkState = true;

                startPosting(title, author, details, demand, resultUri, checkState);

            }
        });

        delet_my_single_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialouge("Delete?","Are you sure you want to delete this item?", 4);
            }
        });

        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int ResultCode, Intent data){
        super.onActivityResult(requestCode, ResultCode, data);
        if(ResultCode== RESULT_OK && requestCode == PICK_IMAGE_REQUEST ){

            filePath = data.getData();

            CropImage.activity(filePath)
                    //.setAutoZoomEnabled(true)
                    .setActivityTitle("Adjustment")
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (ResultCode == RESULT_OK) {
                resultUri = result.getUri();

                my_content_image.setImageURI(resultUri);

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

    public void startPosting(final String title, final String author, final String details, final int demand, final Uri ImageURI, final Boolean checkState) {
        mProgress.setTitle("Updating...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        if (resultUri != null){

            Bitmap bitmapImage;
            byte[] data;

            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageURI);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                data = baos.toByteArray();


                StorageReference filePath = mStorageRef.child("book_Image").child("image" + System.currentTimeMillis());

                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        mStorage = FirebaseStorage.getInstance();
                        StorageReference deletImageRef = mStorage.getReferenceFromUrl(image_uri);
                        deletImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //if the existing image delete successfully

                                updateAuthor.setValue(author);
                                updateTitle.setValue(title);
                                updateDetails.setValue(details);
                                updateDemand.setValue(demand);
                                updateImageUri.setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //if update everything successfully
                                            mProgress.dismiss();
                                            alertDialouge("Success!", "Updated successfully", 1);

                                        } else {
                                            mProgress.dismiss();
                                            alertDialouge("Failed!", "Updating failed, Please try again", 2);
                                        }
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mProgress.dismiss();
                                alertDialouge("Failed!", "Updating failed for: "+exception+"/nPlease try again", 2);
                            }
                        });

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                mProgress.dismiss();
                                alertDialouge("Failed!", "Updating failed for: "+e+"/nPlease try again", 2);
                            }
                        })

                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double totalProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                mProgress.setMessage("Progressing" + "  " + (int) totalProgress + "%");
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try{
                updateAuthor.setValue(author);
                updateTitle.setValue(title);
                updateDetails.setValue(details);
                updateDemand.setValue(demand);

                mProgress.dismiss();

                alertDialouge("Success!", "Updated successfully", 1);

            } catch (Exception e) {
                e.printStackTrace();
                alertDialouge("Failed!", "Updating failed for: "+e+"/nPlease try again", 2);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        //deleteCache(this);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_view_menu, menu);
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
            case R.id.action_info:
                Toast.makeText(getApplicationContext(), "info", Toast.LENGTH_SHORT).show();
                return true;
        }



        return super.onOptionsItemSelected(item);
    }
    public void nonEditable(View v){
       v.setFocusable(false);
       v.setFocusableInTouchMode(false);
       v.setClickable(false);
    }

    public void editable(View v){
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.setClickable(true);

    }

    private void deleteItem(){

        mProgress.setTitle("Deleting...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        mStorage = FirebaseStorage.getInstance();
        StorageReference deletImageRef = mStorage.getReferenceFromUrl(image_uri);
        deletImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child(mPost_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgress.dismiss();
                        if (task.isSuccessful()) {
                            alertDialouge("Success!", "Your item deleted successfully",3 );
                            finish();

                        } else {
                            alertDialouge("Failed!", "Error occurred in deletion ", 2);
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mProgress.dismiss();
                alertDialouge("Failed!", "Error occurred in deletion. Probable cause: "+exception+"Please try again", 2);

            }
        });
    }


    private void alertDialouge(String title, String message, int pid){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(pid==1) {
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            recreate();
                        }
                    })
                    .setCancelable(false);
        }

        else if(pid==2) {
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
        }

        else if(pid==3) {
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton(android.R.string.ok, null);
        }

        else if(pid==4) {
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteItem();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
