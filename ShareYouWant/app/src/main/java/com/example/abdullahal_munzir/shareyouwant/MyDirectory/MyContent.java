package com.example.abdullahal_munzir.shareyouwant.MyDirectory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.abdullahal_munzir.shareyouwant.Content;
import com.example.abdullahal_munzir.shareyouwant.EditUserprofile;
import com.example.abdullahal_munzir.shareyouwant.LoginActivity;
import com.example.abdullahal_munzir.shareyouwant.MainActivity;
import com.example.abdullahal_munzir.shareyouwant.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MyContent extends AppCompatActivity {

    private RecyclerView mContentList;
    private DatabaseReference mDatabase, mCurrentUserRefference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private Query myPostsQuery;
    private ProgressBar mProgress;
    static Context context;
    LinearLayoutManager mLayoutManager;
    private TextView itemCount;
    private String userId;
    int c=0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);

        Log.d("test", "onCreate: outside of start");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemCount = findViewById(R.id.item_count);
        itemCount.setHint("Your total content : 0");
        progressDialog = new ProgressDialog(this);

        mProgress = findViewById(R.id.m_progress_bar);
        View no_internet_layout = findViewById(R.id.no_internet_layout);
        EditText retry = findViewById(R.id.retry_btn);
        no_internet_layout.setVisibility(View.GONE);

        //mProgress.show();

        //context = getApplicationContext();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });



        mCurrentUserRefference = FirebaseDatabase.getInstance().getReference().child("users");

        mContentList = findViewById(R.id.my_content_recyclerview);
        //mContentList.setHasFixedSize(true);
        //mContentList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mContentList.setLayoutManager(mLayoutManager);
        //mContentList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        View create_button = findViewById(R.id.create_button_layout);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null){

                    progressDialog.setTitle("Preparing.....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    String current_uid = mAuth.getCurrentUser().getUid();
                    mCurrentUserRefference.child(current_uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("phone_num") && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("latitude")) {
                                Intent intent = new Intent(MyContent.this, CreateContent.class);
                                startActivity(intent);
                            }
                            else{
                                alertDialouge("Incomplete Profile", "Please complete your profile to continue");
                            }
                            progressDialog.dismiss();
                        }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        View view = findViewById(R.id.my_content_layout);
        Snackbar snackbar = Snackbar
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


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("test", "onCreate: insideside of authstate");
                if (firebaseAuth.getCurrentUser() == null) {
                    // Sign in logic here.
                    startActivity(new Intent(MyContent.this, LoginActivity.class));
                }

                else{
                      Runnable runnable = new Runnable() {
                          @Override
                          public void run() {
                              if(!isOnline()) {
                                  Log.d("online", "onAuthStateChanged: offline");
                                  snackbar.show();

                              }
                              else{
                                  Log.d("online", "onAuthStateChanged: online");
                              }
                          }
                      };
                      Thread thread = new Thread(runnable);
                      thread.start();


                    mAuth = FirebaseAuth.getInstance();
                    userId=mAuth.getCurrentUser().getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
                    myPostsQuery = mDatabase.orderByChild("uid").equalTo(userId);

                    myPostsQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                            }
                            else {
                                mProgress.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    FirebaseRecyclerAdapter<Content, ContentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Content, ContentViewHolder>(
                                Content.class,
                                R.layout.my_content_row,
                                ContentViewHolder.class,
                                myPostsQuery

                        ) {
                            @Override
                            protected void populateViewHolder(ContentViewHolder viewHolder, Content model, int position) {

                                itemCount.setText("Your total content : "+getItemCount());
                                mProgress.setVisibility(View.GONE);
                                c++;
                                if(c==10) {

                                    Thread thread = new Thread(runnable);
                                    thread.start();
                                    c=0;
                                }



                                final String post_key = getRef(position).getKey();
                                viewHolder.setTitle(model.getTitle());
                                viewHolder.setCategory(model.getCategory());
                                viewHolder.setDemand(model.getDemand());
                                viewHolder.setImage_uri(model.getImage_uri());
                                viewHolder.setDate_time(model.getDate_time());

                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        //Toast.makeText(getApplicationContext(), post_key, Toast.LENGTH_SHORT).show();
                                        Intent SingleMyContentIntent = new Intent(MyContent.this, SingleMyContentView.class);
                                        SingleMyContentIntent.putExtra("post_id", post_key);
                                        startActivity(SingleMyContentIntent);

                                    }
                                });

                            }
                        };

                        mContentList.setAdapter(firebaseRecyclerAdapter);


                }
            }
        };

        if(isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "online", Toast.LENGTH_SHORT).show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(mAuthListener);
        }
        else {
            Toast.makeText(getApplicationContext(), "ofline", Toast.LENGTH_SHORT).show();
            mProgress.setVisibility(View.GONE);
            no_internet_layout.setVisibility(View.VISIBLE);
            snackbar.show();
        }

    }

    @Override
    protected  void onStart() {
        super.onStart();

           /* if(isNetworkAvailable()){
                Toast.makeText(getApplicationContext(), "online", Toast.LENGTH_SHORT).show();
                mAuth = FirebaseAuth.getInstance();
                mAuth.addAuthStateListener(mAuthListener);
            }
            else {
                Toast.makeText(getApplicationContext(), "ofline", Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.GONE);
                noInternetText.setVisibility(View.VISIBLE);
            }*/
       //mAuth = FirebaseAuth.getInstance();
      // mAuth.addAuthStateListener(mAuthListener);

        Log.d("monitor", "onStart: out of condition");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_my_content, menu);
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
                case R.id.action_settings:
                    Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
                    return true;
            }



        return super.onOptionsItemSelected(item);
    }

    public  static class ContentViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public ContentViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }

        public void setTitle(String title){
            TextView content_title = mView.findViewById(R.id.content_title);
            content_title.setText(title);
        }

        public void setDemand(int demand){
            TextView content_demand = mView.findViewById(R.id.content_demand);
            content_demand.setText(demand+" Tk.");
        }

        public void setCategory(String category){
            TextView content_category = mView.findViewById(R.id.content_category);
            if(category.equals("Game")){
                content_category.setBackgroundResource(R.drawable.game_tag_background);
            }
            else {
                content_category.setBackgroundResource(R.drawable.book_tag_background);
            }
            content_category.setText(category);
        }
        public  void  setDate_time(long create_time_millis){
            TextView date_time_field = mView.findViewById(R.id.content_date);
            create_time_millis= create_time_millis*(-1);

            String time= timeDifference(create_time_millis);

            date_time_field.setText(time);

        }

        public  void setImage_uri( String Image_uri){
            ImageView content_image = mView.findViewById(R.id.content_image);
            /*Picasso.get()
                   .load(Image_uri)
                    //.resize(150, 150)
                   // .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                   .into(content_image); */
            RequestOptions options = new RequestOptions()
                    .dontAnimate()
                    .error(R.mipmap.ic_launcher_round);
            Glide.with(mView)
                    .load(Image_uri)
                    .apply(options)
                    .into(content_image);


        }

    }

    public Boolean isOnline() {
        Log.d("called", "isOnline: called");
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block

            Log.d("error", "isOnline: "+e);
        }
        return false;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();}

    public static  String timeDifference(long create_time_millis){
            long current_time_millis = System.currentTimeMillis();
            long difference = (current_time_millis-create_time_millis)/1000;

        Log.d("timemillis", "timeDifference: called"+"current: "+current_time_millis+"  diff:    "+difference);


            if(difference <= 60){
                Log.d("timemillis", "timeDifference: <60");
                return "Just now";
            }
            else if(difference<3600){
                difference = difference/60;
                if(difference > 1){
                    Log.d("timemillis", "timeDifference: >1");
                    return ""+difference+" minutes ago";
                }
                Log.d("timemillis", "timeDifference: ==1");
                return ""+difference+" minute ago";
            }
            else if(difference<86400){
                difference = difference/3600;

                if(difference > 1) {
                    return "" + difference + " hours ago";
                }
                return "" + difference + " hour ago";
            }
            else if(difference<2592000){
                difference = difference/84600;
                if(difference > 1) {
                    return "" + difference + " days ago";
                }
                return "" + difference + " day ago";
            }

            else if(difference<31104000){
                difference = difference/2592000;
                if(difference > 1) {
                    return "" + difference + " months ago";
                }
                return "" + difference + " month ago";
            }
            else
                return "a year ago";

    }


    private void alertDialouge(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("Complete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(MyContent.this, EditUserprofile.class));
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
