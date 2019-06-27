package com.example.abdullahal_munzir.shareyouwant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.Manifest;

public class SingleContentView extends AppCompatActivity {

    private DatabaseReference mDatabase,mUserDatabase;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;
    private TextView my_content_date_time, my_content_category, my_content_author_platform,txtAuthor_Platform, my_content_details,my_content_title , my_content_demand, call_text, chat_text, txt_owner, txt_location;
    private ImageView my_content_image;
    private View call_button,button_chat;
    private FloatingActionButton edit_my_single_content, delet_my_single_content, update_my_single_content;
    private String mPost_key, image_uri;
    private String userEmail, userPhone, userName, userId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_content_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final LinearLayout layout = findViewById(R.id.image_layout);

        final Context context = getApplicationContext();
        DateTime dateTime  = new DateTime();


        call_button =  findViewById(R.id.call_btn);
        button_chat =  findViewById(R.id.chat_btn);

        call_text = findViewById(R.id.call_text);
        chat_text = findViewById(R.id.chat_text);


        call_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_button.performClick();
            }
        });

        chat_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_chat.performClick();
            }
        });


        //deleteCache(this);
        mPost_key = getIntent().getExtras().getString("post_id");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        my_content_title = findViewById(R.id.my_content_title);
        my_content_date_time = findViewById(R.id.my_content_date);
        my_content_demand = findViewById(R.id.my_content_demand);
        my_content_category = findViewById(R.id.my_content_category);
        my_content_image = findViewById(R.id.my_content_image);
        my_content_author_platform = findViewById(R.id.my_content_author_platform);
        my_content_details = findViewById(R.id.my_content_details);
        txtAuthor_Platform = findViewById(R.id.txtAuthor_Platform);
        txt_owner = findViewById(R.id.txt_owner);
        txt_location = findViewById(R.id.txt_location);

       /* edit_my_single_content = findViewById(R.id.edit_my_single_content);
        delet_my_single_content = findViewById(R.id.delet_my_single_content);
        update_my_single_content = findViewById(R.id.update_my_single_content);
        */



        Toast.makeText(getApplicationContext(), mPost_key, Toast.LENGTH_SHORT).show();

        my_content_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        });

        txt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userId != null){
                    Intent intent = new Intent(SingleContentView.this, LenderMapsActivity.class);
                    intent.putExtra("user_id",userId);
                    startActivity(intent);
                }
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

                } else {
                   my_content_category.setBackgroundResource(R.drawable.book_tag_background);
                    String author = (String) dataSnapshot.child("author").getValue();
                    my_content_author_platform.setText(author);
                    txtAuthor_Platform.setText("Author: ");

                }
                String post_details = (String) dataSnapshot.child("details").getValue();
                image_uri = (String) dataSnapshot.child("image_uri").getValue();
                long demand = (long) dataSnapshot.child("demand").getValue();
                long date_time = (long) dataSnapshot.child("date_time").getValue();
                date_time = date_time*(-1);
                Boolean isNegotiable = (Boolean) dataSnapshot.child("negotiable").getValue();
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


                try {
                    userId = (String) dataSnapshot.child("uid").getValue();
                    if(userId!=null) {

                        mUserDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userEmail = (String) dataSnapshot.child("email").getValue();
                                userPhone = (String) dataSnapshot.child("phone_num").getValue();
                                userName = (String) dataSnapshot.child("name").getValue();
                                txt_owner.setText("Owner: "+userName);

                                Log.d("user", "onDataChange: "+userEmail+userName+userPhone);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {


                            }
                        });
                    }

                        } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("123456", "onCancelled: "+databaseError);

            }
        });


      /*  edit_my_single_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        update_my_single_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        delet_my_single_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }); */


        call_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                callPhoneNumber();
            }
        });
        button_chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SingleContentView.this, Chat.class);
                intent.putExtra("user_name",userName);
                startActivity(intent);

            }
        });





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

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(SingleContentView.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + userPhone));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + userPhone));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
            else
            {

            }
        }
    }

}