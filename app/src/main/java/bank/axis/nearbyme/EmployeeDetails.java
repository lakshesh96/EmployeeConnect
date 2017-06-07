package bank.axis.nearbyme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.UserDetails.UsersModel;
//import com.androidquery.AQuery;


public class EmployeeDetails extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView et_name,et_email,et_number;
    ImageView profile_image;
    Button bt_upload,bt_map;

    //Database
    private final String TAG = "EmployeeDetails";
    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private static UsersModel addUserData;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        bt_map = (Button) findViewById(R.id.bt_map);
        //Database
        addUserData = new UsersModel();
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference();
        mUserReference = DatabaseInstance.getFirebaseInstance().getReference("NearByMe");

        bt_upload = (Button) findViewById(R.id.bt_upload_details);
        et_number = (EditText) findViewById(R.id.et_number);

        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String contact_number = et_number.getText().toString();
                //if(TextUtils.isEmpty(et_email.getText())){
                    //createUser(et_name.getText().toString(),et_email.getText().toString(),et_number.getText().toString());
                //}
            }
        });
        bt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EmployeeDetails.this,MapsActivity.class);
                startActivity(i);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header_layout = navigationView.getHeaderView(0);

        et_name = (TextView) header_layout.findViewById(R.id.name_et);
        //et_name.setText("Test");
        et_email = (TextView) header_layout.findViewById(R.id.email_et);
        //et_email.setText("Test");
        profile_image = (ImageView) header_layout.findViewById(R.id.imageView);

        Bundle extras = getIntent().getExtras();
        String name = (String) extras.get("key1");
        String email = (String) extras.get("key2");
        String photourl = (String) extras.get("key3");
        //URI photourl2 =  dt.getStringExtra("key3");
        et_name.setText(name);
        et_email.setText(email);
        //profile_image.setImageBitmap(getBitmapFromURL(photourl));
        new ImageLoadTask(photourl,profile_image).execute();

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
        getMenuInflater().inflate(R.menu.employee_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Code for getting Logged in Credentials from SignInActivity
    /*protected void onActivityResult(int reqCode,int resCode, Intent dt)
    {
        super.onActivityResult(reqCode,resCode,dt);
        String name = dt.getStringExtra("key1");
        String email = dt.getStringExtra("key2");
        String photourl = dt.getStringExtra("key3");
        //URI photourl2 =  dt.getStringExtra("key3");
        et_name.setText(name);
        et_email.setText(email);
        //profile_image.setImageBitmap(getBitmapFromURL(photourl));
        new ImageLoadTask(photourl,profile_image);

    }*/
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    //Databse

    /*FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference("message");*/
    //myRef.setValue("Hello, World!");
    private void createUser(String name,String email, String number)
    {
        if(TextUtils.isEmpty(userId))
        {
            userId = mDatabase.push().getKey();
        }
        UsersModel user = new UsersModel();
        user.setName(name);
        user.setEmail(email);
        user.setNumber(number);
        mDatabase.child(userId).setValue("name");
    }



}
/*public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;

    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

}*/
