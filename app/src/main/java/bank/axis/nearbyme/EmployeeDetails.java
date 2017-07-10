package bank.axis.nearbyme;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.Database.UserInfo;
import bank.axis.nearbyme.UserDetails.UsersModel;


public class EmployeeDetails extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GeneralQueryFragment.OnFragmentInteractionListener,GoogleMapFragment.OnFragmentInteractionListener,FirebaseAuth.AuthStateListener  {

    TextView et_name,et_email,et_number,tv_location_name,tv_location_address,tv_location_attributes;
    ImageView profile_image;
    FirebaseAuth mAuth;
    String name,email,photourl;
    private String uid;
    UserInfo userinfo;
    //Database
    private final String TAG = EmployeeDetails.class.getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private static UsersModel addUserData;
    int PLACE_PICKER_REQUEST = 1;
    private Location mLastKnownLocation;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private Bundle googleMapData;
    private Bundle googleMapGeneralQueryFragmentData;


    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private FragmentManager fragmentManager;
    private SupportMapFragment mapFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);
        userinfo = new UserInfo();
        //Database
        addUserData = new UsersModel();
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference();
        mUserReference = DatabaseInstance.getFirebaseInstance().getReference("NearByMe");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent i2 = builder.build(EmployeeDetails.this);
                    startActivityForResult(i2, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header_layout = navigationView.getHeaderView(0);

        et_name = (TextView) header_layout.findViewById(R.id.name_et);
        et_email = (TextView) header_layout.findViewById(R.id.email_et);
        profile_image = (ImageView) header_layout.findViewById(R.id.imageView);

        Bundle extras = getIntent().getExtras();
        name = (String) extras.get("key1");
        email = (String) extras.get("key2");
        photourl = (String) extras.get("key3");
        et_name.setText(name);
        et_email.setText(email);
        new ImageLoadTask(photourl,profile_image).execute();
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GoogleMapFragment googleMapFragment = new GoogleMapFragment();
        googleMapData = new Bundle();
        googleMapGeneralQueryFragmentData = new Bundle();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        googleMapData.putString("UID",uid);
        googleMapData.putString("NAME",name);
        googleMapData.putString("EMAIL",email);
        googleMapData.putString("PHOTOURL",photourl);
        googleMapGeneralQueryFragmentData.putString("UID",uid);

        googleMapFragment.setArguments(googleMapData);
        fragmentTransaction.add(R.id.fragmentHolder,googleMapFragment);
        fragmentTransaction.commit();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("photoURL",photourl);
                b.putString("name",name);
                b.putString("email",email);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ProfileActivity profileActivity = new ProfileActivity();
                profileActivity.setArguments(b);
                fragmentTransaction.replace(R.id.fragmentHolder,profileActivity);
                fragmentTransaction.commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

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
        getMenuInflater().inflate(R.menu.employee_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        SignInActivity off = new SignInActivity();
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            GeneralQueryFragment generalQueryFragment = new GeneralQueryFragment();
            generalQueryFragment.setArguments(googleMapGeneralQueryFragmentData);
            fragmentTransaction.replace(R.id.fragmentHolder,generalQueryFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_gallery) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            GoogleMapFragment googleMapFragment = new GoogleMapFragment();
            googleMapFragment.setArguments(googleMapData);
            fragmentTransaction.replace(R.id.fragmentHolder,googleMapFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_slideshow) {

            /*Intent i = new Intent(EmployeeDetails.this,ProfileActivity.class);
            i.putExtra("photoURL",photourl);
            i.putExtra("name",name);
            i.putExtra("email",email);
            startActivity(i);*/
            Bundle b = new Bundle();
            b.putString("photoURL",photourl);
            b.putString("name",name);
            b.putString("email",email);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ProfileActivity profileActivity = new ProfileActivity();
            profileActivity.setArguments(b);
            fragmentTransaction.replace(R.id.fragmentHolder,profileActivity);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_share) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this,SignInActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GoogleMapFragment googleMapFragment = new GoogleMapFragment();
        googleMapData = new Bundle();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        googleMapData.putString("UID",uid);
        googleMapData.putString("NAME",name);
        googleMapData.putString("EMAIL",email);
        googleMapFragment.setArguments(googleMapData);
        fragmentTransaction.add(R.id.fragmentHolder,googleMapFragment);
        fragmentTransaction.commit();
    }
}

