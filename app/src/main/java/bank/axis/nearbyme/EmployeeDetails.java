package bank.axis.nearbyme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.UserDetails.UsersModel;
//import com.androidquery.AQuery;


public class EmployeeDetails extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MapFragment.OnFragmentInteractionListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnInfoWindowClickListener  {

    TextView et_name,et_email,et_number,tv_location_name,tv_location_address,tv_location_attributes;
    ImageView profile_image;
    Button bt_upload,bt_map;

    //Database
    private final String TAG = EmployeeDetails.class.getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private static UsersModel addUserData;
    private String userId;
    Bundle args = new Bundle();

    //Map
    int PLACE_PICKER_REQUEST = 1;

    //Code for google map

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private LatLng mReceivedLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String default_info_snippet = "waiting";
    private static final String default_info_title = new String("still waiting");
    private static final String[] test = {"One","Two"};

    // Used for selecting the current place.
    /*private final int mMaxEntries = 5;
    private String[] mLikelyPlaceNames = new String[mMaxEntries];
    private String[] mLikelyPlaceAddresses = new String[mMaxEntries];
    private String[] mLikelyPlaceAttributions = new String[mMaxEntries];
    private LatLng[] mLikelyPlaceLatLngs = new LatLng[mMaxEntries];*/

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

        /*bt_map = (Button) findViewById(R.id.bt_map);
        tv_location_name = (TextView) findViewById(R.id.tv_location_name);
        tv_location_address = (TextView) findViewById(R.id.tv_location_address);
        tv_location_attributes = (TextView) findViewById(R.id.tv_location_attribution);*/
        //Database
        addUserData = new UsersModel();
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference();
        mUserReference = DatabaseInstance.getFirebaseInstance().getReference("NearByMe");

        //bt_upload = (Button) findViewById(R.id.bt_upload_details);
        //et_number = (EditText) findViewById(R.id.et_number);

        /*bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String contact_number = et_number.getText().toString();
                //if(TextUtils.isEmpty(et_email.getText())){
                    //createUser(et_name.getText().toString(),et_email.getText().toString(),et_number.getText().toString());
                //}
            }
        });*/
        /*bt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EmployeeDetails.this,MapsActivity.class);
                startActivity(i);
                try {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent i = builder.build(EmployeeDetails.this);
                    startActivityForResult(i, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*        Fragment fragment = null;
        Class fragmentClass = MapFragment.class;
        try{
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }*/
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.frame_nav,fragment).commit();
        //DrawerLayout drawer2 = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer2.closeDrawer(GravityCompat.START);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
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

        //Google maps <code></code>
        //Bundle bundle = getIntent().getParcelableExtra("bundle");
        //mReceivedLocation = bundle.getParcelable("coordinates");
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        SignInActivity off = new SignInActivity();
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            off.signOut();
            Intent i = new Intent(EmployeeDetails.this,SignInActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_send) {
            off.revokeAccess();
            System.exit(1);
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

    //Code for getting Selected Pointer from Place Picker
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final LatLng coordinates = place.getLatLng();
                final Locale pin = place.getLocale();
                String attributions = (String) place.getAttributions();
                if(attributions == null){
                    attributions = "";
                }
                /*tv_location_name.setText(name);
                tv_location_address.setText(address);
                tv_location_attributes.setText(attributions);*/

                args.putParcelable("coordinates",coordinates);
                mReceivedLocation = coordinates;
                mMap.addMarker(new MarkerOptions()
                        .title(getString(R.string.title_activity_maps))
                        .position(mReceivedLocation)
                        .snippet("Selected Location"));
                refreshMap(mMap);
                markStartingLocationOnMap(mMap, mReceivedLocation);
                /*Intent i = new Intent(EmployeeDetails.this,MapsActivity.class);
                i.putExtra("bundle",args);
                startActivity(i);*/
                /*String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();*/
            }
        }else {
            Bundle bundle = getIntent().getParcelableExtra("bundle");
            mReceivedLocation = bundle.getParcelable("coordinates");
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        /*mMap.addMarker(new MarkerOptions()
                .title(getString(R.string.title_activity_maps))
                .position(mReceivedLocation)
                .snippet("Selected Location"));*/

        }
    }
    public Bundle sendData(){
        return args;
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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getDeviceLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*if(mReceivedLocation != null)
        {
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.title_activity_maps))
                    .position(mReceivedLocation)
                    .snippet("Selected Location"));
        }*/
        updateLocationUI();
        //getDeviceLocation();
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker arg0){
                return null;
            }
            @Override
            public View getInfoContents(Marker marker)
            {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,null);
                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());
                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });

        //Code to set marker of Selected Location
        if(mReceivedLocation != null)
        {
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.title_activity_maps))
                    .position(mReceivedLocation)
                    .snippet("Selected Location"));
            refreshMap(mMap);
            markStartingLocationOnMap(mMap, mReceivedLocation);
        }
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        //Code for InfoWindow press action
        mMap.setOnInfoWindowClickListener(this);
    }

    private void refreshMap(GoogleMap mapInstance){
        mapInstance.clear();
    }
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // A step later in the tutorial adds the code to get the device location.
        if(mLocationPermissionGranted){
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        LatLng temp = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .title(getString(R.string.title_activity_maps))
                .position(temp)
                .snippet("Selected Location"));
        refreshMap(mMap);
        markStartingLocationOnMap(mMap, temp);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }

        //getDeviceLocation();
    }

    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location){
        mapObject.addMarker(new MarkerOptions().position(location).title("Selected location"));
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}

