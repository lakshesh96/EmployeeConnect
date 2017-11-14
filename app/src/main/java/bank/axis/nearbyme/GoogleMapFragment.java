package bank.axis.nearbyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Locale;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.Database.UserInfo;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback,/*GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,*/GoogleMap.OnInfoWindowClickListener,GoogleClientCallBack, GoogleMap.OnMarkerDragListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userID;
    private String mParam2;
    private String TAG = GoogleMapFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private CameraPosition mCameraPosition;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private LatLng mReceivedLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    UserInfo userinfo;
    String name,email,photourl;
    Geocoder geocoder;
    List<Address> addresses;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private int PLACE_PICKER_REQUEST = 1;
    private GoogleClientCallBack googleClientCallBack;
    private SharedPreferences sharedPref;
    private String MY_PREFS_NAME = "MyLocationData";
    private FragmentManager fragmentManager;
    private Bundle googleMapGeneralQueryFragmentData;


    public GoogleMapFragment() {
    }
    // TODO: Rename and change types and number of parameters
    public static GoogleMapFragment newInstance(String param1, String param2) {
        GoogleMapFragment fragment = new GoogleMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userinfo = new UserInfo();
        googleClientCallBack = this;
        sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);

        if(GoogleClient.getGoogleApiClient() == null){
            new GoogleClient(getActivity(),googleClientCallBack);
        }else{
            mGoogleApiClient = GoogleClient.getGoogleApiClient();
        }
        if (getArguments() != null) {
            userID = getArguments().getString("UID");
            name = getArguments().getString("NAME");
            email = getArguments().getString("EMAIL");
            photourl = getArguments().getString("PHOTOURL");
            userinfo.setName(name);
            userinfo.setEmail(email);
            userinfo.setPhotoURL(photourl);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference();

        if(GoogleClient.getGoogleApiClient() == null){
            new GoogleClient(getActivity(),googleClientCallBack);
        }

        //getParentFragment().getActivity().findViewById(R.id.nav_camera).performClick();
        //getActivity().findViewById(R.id.nav_camera).performClick();


        /*googleMapGeneralQueryFragmentData = new Bundle();
        googleMapGeneralQueryFragmentData.putString("UID",userID);
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GeneralQueryFragment generalQueryFragment = new GeneralQueryFragment();
        generalQueryFragment.setArguments(googleMapGeneralQueryFragmentData);
        fragmentTransaction.replace(R.id.fragmentHolder,generalQueryFragment);
        fragmentTransaction.commit();*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mGoogleApiClient != null ) syncMap();
        return rootView;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSearchFragment();
    }

    private void initializeSearchFragment() {
        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                userinfo.setAddress(place.getAddress().toString());
                userinfo.setLatitude(place.getLatLng().latitude);
                userinfo.setLongitude(place.getLatLng().longitude);
                mReceivedLocation = new LatLng(userinfo.getLatitude(),userinfo.getLongitude());
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mReceivedLocation));
                mMap.addMarker(new MarkerOptions()
                        .title(place.getName().toString())
                        .position(mReceivedLocation)
                        .snippet("Selected Location")
                        .draggable(true));

                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // TODO Auto-generated method stub
        LatLng dragPosition = marker.getPosition();
        userinfo.setLatitude(dragPosition.latitude);
        userinfo.setLongitude(dragPosition.longitude);
        marker.setTitle("New Selection");
        marker.setSnippet("");
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), "Place Marker on your Desired Location.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker arg0){
                return null;
            }
            @Override
            public View getInfoContents(Marker marker)
            {
                View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents,null);
                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());
                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });

        if(mReceivedLocation != null)
        {
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.title_activity_maps))
                    .position(mReceivedLocation)
                    .snippet("Selected Location")
                    .draggable(true));
            mMap.clear();
            markStartingLocationOnMap(mMap, mReceivedLocation);
        }
        mMap.setOnInfoWindowClickListener(this);
        getDeviceLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        //mGoogleApiClient.stopAutoManage(getActivity());
        //mGoogleApiClient.disconnect();

    }

/*    @Override
    public void onResume() {
        super.onResume();
        //syncMap();
    }*/

    private void syncMap() {
        mapFragment.getMapAsync(this);;
    }

    @Override
    public void onConnected(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
        //mapFragment.getMapAsync(this);
        syncMap();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng temp_coordinates;
        temp_coordinates = new LatLng(userinfo.getLatitude(),userinfo.getLongitude());
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(temp_coordinates.latitude,temp_coordinates.longitude,1);
            userinfo.setAddress(addresses.get(0).getAddressLine(0));
            userinfo.setPincode(addresses.get(0).getPostalCode());
            //userinfo.setLocality(addresses.get(0).getLocality());
            userinfo.setLocality(addresses.get(0).getSubAdminArea());

            String locality = null;
            String state = addresses.get(0).getAdminArea();
            String city = addresses.get(0).getSubAdminArea();
            if(addresses.get(0).getLocality().equals("Dahmi Kalan") || addresses.get(0).getLocality().equals("Dehmi Kalan")){
                locality = "Jaipur";
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            //editor.putString("locality",addresses.get(0).getLocality());
            editor.putString("locality",city);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        userinfo.setId(userID);


        Intent i = new Intent(getActivity(),UploadDetailsForm.class);
        //i.putExtra("name",userinfo.getName());
        Bundle b = new Bundle();
        b.putSerializable("userinfo", userinfo);
        i.putExtras(b);
        startActivity(i);


        /*mDatabase.child("Cluster").child(userinfo.getLocality()).child(userinfo.getId()).setValue(userinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Cluster added Successfully", Toast.LENGTH_SHORT).show();
            }
        });*/


    }




    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location){

        Marker marker = mapObject.addMarker(new MarkerOptions().position(location).title("Current location"));
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
        marker.showInfoWindow();
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if(mLocationPermissionGranted){
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
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
        LatLng cord = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        userinfo.setLatitude(cord.latitude);
        userinfo.setLongitude(cord.longitude);
        geocoder = new Geocoder(getActivity(),Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(cord.latitude,cord.longitude,1);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("locality",addresses.get(0).getSubAdminArea());
            editor.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions()
                .title(getString(R.string.title_activity_maps))
                .position(cord)
                .snippet("Selected Location")
                .draggable(true));
        mMap.clear();
        mMap.setPadding(0,200,0,0);
        mMap.setMyLocationEnabled(true);
        markStartingLocationOnMap(mMap, cord);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Bundle bundle = getActivity().getIntent().getParcelableExtra("bundle");
            mReceivedLocation = bundle.getParcelable("coordinates");
            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
    }
}
