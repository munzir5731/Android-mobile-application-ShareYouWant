package com.example.abdullahal_munzir.shareyouwant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.abdullahal_munzir.shareyouwant.MyDirectory.CreateContent;
import com.example.abdullahal_munzir.shareyouwant.MyDirectory.MyContent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mContentList;
    private DatabaseReference mDatabase, mCurrentUserRefference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private Query myTopPostsQuery;
    private ProgressBar mProgress;
    AlertDialog.Builder builder;
    int c=0;
    static Context context;
    LinearLayoutManager mLayoutManager;
    StaggeredGridLayoutManager mStaggerLayoutManager;
    TextView itemCount;
    private View create_button;
    private boolean complete;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressDialog = new ProgressDialog(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mProgress = findViewById(R.id.m_progress_bar);
        View no_internet_layout = findViewById(R.id.no_internet_layout);
        EditText retry = findViewById(R.id.retry_btn);
        no_internet_layout.setVisibility(View.GONE);
        itemCount = findViewById(R.id.item_count);
        itemCount.setHint("Result: 0 Content");

        View home_view = findViewById(R.id.my_content_recyclerview);

        home_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                itemCount.setVisibility(View.GONE);
                //slideDown(itemCount);
                return false;
            }
        });

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
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        myTopPostsQuery = mDatabase.orderByChild("date_time");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }


        mContentList = findViewById(R.id.my_content_recyclerview);
        //mContentList.setHasFixedSize(true);
        //mContentList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

       /* mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true); */

        mStaggerLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //mContentList.setLayoutManager(mLayoutManager);
        mContentList.setLayoutManager(mStaggerLayoutManager);


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("test", "onCreate: insideside of authstate");

            }
        };


        create_button = findViewById(R.id.create_button_layout);
        create_button.setOnClickListener(view -> {

           // mAuth.addAuthStateListener(mAuthListener);

            if (mAuth.getCurrentUser() == null) {

                // Sign in logic here.
                alertDialouge("Sign in", "Please sign in to continue", 1);

            }

            else if (mAuth.getCurrentUser() != null){

                progressDialog.setTitle("Preparing.....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                String current_uid = mAuth.getCurrentUser().getUid();
                mCurrentUserRefference.child(current_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("phone_num") && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("latitude")) {
                            Intent intent = new Intent(MainActivity.this, CreateContent.class);
                            startActivity(intent);
                        }
                        else{
                            alertDialouge("Incomplete Profile", "Please complete your profile to continue", 2);
                        }
                        progressDialog.dismiss();
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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




        if(isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "online", Toast.LENGTH_SHORT).show();
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

            FirebaseRecyclerAdapter<Content, MainActivity.ContentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Content, MainActivity.ContentViewHolder>(
                    Content.class,
                    R.layout.all_content_row,
                    MainActivity.ContentViewHolder.class,
                    myTopPostsQuery

            ) {
                @Override
                protected void populateViewHolder(MainActivity.ContentViewHolder viewHolder, Content model, int position) {

                    itemCount.setText("Result : "+getItemCount()+" content");


                    if(c==15) {

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

                    mProgress.setVisibility(View.GONE);
                    c++;
                    Log.d(" online count", "populateViewHolder: " + c);

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Toast.makeText(getApplicationContext(), post_key, Toast.LENGTH_SHORT).show();
                            Intent SingleMyContentIntent = new Intent(MainActivity.this, SingleContentView.class);
                            SingleMyContentIntent.putExtra("post_id", post_key);
                            startActivity(SingleMyContentIntent);

                        }
                    });

                }
            };

            mContentList.setAdapter(firebaseRecyclerAdapter);
        }
        else {
            Toast.makeText(getApplicationContext(), "ofline", Toast.LENGTH_SHORT).show();
            mProgress.setVisibility(View.GONE);
            no_internet_layout.setVisibility(View.VISIBLE);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chat) {

            /*rifat your code for chat will bw placed here */

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_archive) {

            if (mAuth.getCurrentUser() == null) {
                // Sign in logic here.
                alertDialouge("Sign in", "Please sign in to continue", 1);
            }

            else{
                startActivity(new Intent(MainActivity.this, MyContent.class));

            }

        }
        //else if (id == R.id.profile) {

//            if (mAuth.getCurrentUser() == null) {
//                // Sign in logic here.
//                alertDialouge("Sign in", "Please sign in to manage profile", 1);
//            }
//            else {
//                startActivity(new Intent(MainActivity.this, EditUserprofile.class));
//            }

     //  }
        else if (id == R.id.settings) {

        } else if (id == R.id.log_out) {

            if (mAuth.getCurrentUser() == null) {
                // Sign in logic here.
                alertDialouge("", "You are not signed in yet!!", 3);
            }
            else {
                builder.setTitle("Log Out?")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  static class ContentViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public ContentViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }

        public void setTitle(String title){
            TextView content_title = mView.findViewById(R.id.all_content_title);
            content_title.setText(title);
        }

        public void setDemand(int demand){
            TextView content_demand = mView.findViewById(R.id.all_content_demand);
            content_demand.setText(demand+" Tk.");
        }

        public void setCategory(String category){
            TextView content_category = mView.findViewById(R.id.all_content_category);
            if(category.equals("Game")){
                content_category.setBackgroundResource(R.drawable.game_tag_background);
            }
            else {
                content_category.setBackgroundResource(R.drawable.book_tag_background);
            }
            content_category.setText(category);
        }
        public  void  setDate_time(long create_time_millis){
            TextView date_time_field = mView.findViewById(R.id.all_content_date);

            create_time_millis= create_time_millis*(-1);

            String time= timeDifference(create_time_millis);

            date_time_field.setText(time);

        }

        public  void setImage_uri( String Image_uri){
            ImageView content_image = mView.findViewById(R.id.all_content_image);
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
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

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

    private void alertDialouge(String title, String message, int pid){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(pid==1) {
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
        }
        if(pid==2){
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("Complete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(MainActivity.this, EditUserprofile.class));
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);

        }

        if(pid==3) {
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("OK",null);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isCompleteProfile() {
        if (mAuth.getCurrentUser() != null){
            String current_uid = mAuth.getCurrentUser().getUid();
        mCurrentUserRefference.child(current_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("phone_num") && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("latitude")) {
                    complete= true;
                }
                else complete = false;
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

        return complete;

    }

    @Override
    protected  void onStart() {
        super.onStart();

    }

    @Override
    protected  void onResume() {
        super.onResume();

    }

    @Override
    protected  void onRestart() {
        super.onRestart();

    }
}
