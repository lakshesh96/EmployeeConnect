package bank.axis.nearbyme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Locale;

import bank.axis.nearbyme.Database.Cluster;
import bank.axis.nearbyme.Database.UserInfo;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoogleMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoogleMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleMapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnInfoWindowClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
    private String userId = "";
    String name,email;
    private String uid ="123";
    Geocoder geocoder;
    List<Address> addresses;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private int PLACE_PICKER_REQUEST = 1;

    public GoogleMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoogleMapFragment.
     */
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
        if (getArguments() != null) {
            userID = getArguments().getString("UID");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        userinfo = new UserInfo();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                try {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent i2 = builder.build(getActivity());
                    startActivityForResult(i2, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_google_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        //updateLocationUI();
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

        //Code to set marker of Selected Location
        if(mReceivedLocation != null)
        {
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.title_activity_maps))
                    .position(mReceivedLocation)
                    .snippet("Selected Location"));
            //refreshMap(mMap);
            mMap.clear();
            markStartingLocationOnMap(mMap, mReceivedLocation);
        }
        mMap.setOnInfoWindowClickListener(this);
        getDeviceLocation();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        super.onPause();
        //mGoogleApiClient.disconnect();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Info window clicked", Toast.LENGTH_SHORT).show();
        String temp_address;
        LatLng temp_coordinates;
        temp_address = userinfo.getAddress();
        temp_coordinates = userinfo.getCoordinates();

        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(temp_coordinates.latitude,temp_coordinates.longitude,1);
            userinfo.setAddress(addresses.get(0).getAddressLine(0));
            //geo_state = addresses.get(0).getAdminArea();
            userinfo.setPincode(addresses.get(0).getPostalCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(mAuth.getInstance().getCurrentUser().getUid() == null){
            /*Intent i = new Intent(EmployeeDetails.this,SignInActivity.class);
            Toast.makeText(this, "Please Login again!", Toast.LENGTH_SHORT).show();
            startActivity(i);*/
        }else
            uid = mAuth.getInstance().getCurrentUser().getUid();

        //user = new UserInfo(name,"563225635",email,temp_address,temp_coordinates);
        mDatabase.child("Users").child(userID).setValue(userinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "User Added Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        Cluster cluster = new Cluster(uid,temp_coordinates);
        mDatabase.child("Cluster").child(userinfo.getPincode()).child(uid).setValue(cluster).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Cluster added Successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location){

        mapObject.addMarker(new MarkerOptions().position(location).title("Selected location"));
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
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
        LatLng cord = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        userinfo.setCoordinates(cord);
        mMap.addMarker(new MarkerOptions()
                .title(getString(R.string.title_activity_maps))
                .position(cord)
                .snippet("Selected Location"));
        mMap.clear();
        markStartingLocationOnMap(mMap, cord);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                final CharSequence name = place.getName();
                //address = place.getAddress();
                userinfo.setAddress(place.getAddress().toString());
                //coordinates = place.getLatLng();
                userinfo.setCoordinates(place.getLatLng());
                final Locale pin = place.getLocale();
                String attributions = (String) place.getAttributions();
                if(attributions == null){
                    attributions = "";
                }

                //args.putParcelable("coordinates",userinfo.getCoordinates());
                mReceivedLocation = userinfo.getCoordinates();
                mMap.addMarker(new MarkerOptions()
                        .title(getString(R.string.title_activity_maps))
                        .position(mReceivedLocation)
                        .snippet("Selected Location"));
                mMap.clear();
                markStartingLocationOnMap(mMap, mReceivedLocation);
                /*Intent i = new Intent(EmployeeDetails.this,MapsActivity.class);
                i.putExtra("bundle",args);
                startActivity(i);*/
                /*String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();*/
            }
        }else {
            Bundle bundle = getActivity().getIntent().getParcelableExtra("bundle");
            mReceivedLocation = bundle.getParcelable("coordinates");
            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
        /*mMap.addMarker(new MarkerOptions()
                .title(getString(R.string.title_activity_maps))
                    .position(mReceivedLocation)
                    .snippet("Selected Location"));*/

        }
    }
}
