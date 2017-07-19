package bank.axis.nearbyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Random;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.Database.UserDetails;
import bank.axis.nearbyme.Database.UserInfo;
import bank.axis.nearbyme.Database.onDataReceivedInterface;

public class GeneralQueryFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleClientCallBack,onDataReceivedInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    //private View rootView2;
    private SupportMapFragment mapFragment2;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private CameraPosition mCameraPosition;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private LatLng mReceivedLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private String TAG = GoogleMapFragment.class.getSimpleName();
    private String pincode;
    private String locality;
    private ArrayList<UserInfo> userList;
    ArrayList<Marker> markerList;
    private GoogleClientCallBack googleClientCallback;
    private View rootView;
    private int PLACE_PICKER_REQUEST = 1;
    UserInfo userinfo;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String userID;
    LatLng cord;
    Marker myLocationMarker;
    //For Custom InfoWindow
    final int RQS_GooglePlayServices = 1;
    TextView tv_LocInfo;
    Bitmap bmp;
    private onDataReceivedInterface onDataReceivedInterfaceListener;
    private OnFragmentInteractionListener mListener;
    private String MY_PREFS_NAME = "MyLocationData";

    public GeneralQueryFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static GeneralQueryFragment newInstance(String param1, String param2) {
        GeneralQueryFragment fragment = new GeneralQueryFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            userID = getArguments().getString("UID");
        }
        userinfo = new UserInfo();
        googleClientCallback = this;
        onDataReceivedInterfaceListener = this;

        //tv_LocInfo = (TextView) getView().findViewById(R.id.locinfo);


        if (GoogleClient.getGoogleApiClient() == null) {
            new GoogleClient(getActivity(), googleClientCallback);
        } else {
            mGoogleApiClient = GoogleClient.getGoogleApiClient();
        }
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference("Cluster");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
        locality = sharedPref.getString("locality", "not found");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_general_query, container, false);
        mapFragment2 = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_receive);
        mapFragment2.getMapAsync(this);

        return rootView;
    }

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
        //mReceivedLocation = new LatLng(userList.get(0).getLatitude(),userList.get(0).getLongitude());
        /*ArrayList<Marker> */
        markerList = new ArrayList<>();
        //markStartingLocationOnMap(mMap,locality);

        fetchDataFromFirebase();
        mMap.setOnInfoWindowClickListener(this);
        //mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
    }

    private void fetchDataFromFirebase() {
        userList = new ArrayList<UserInfo>();

        if (locality != "not found") {

            /*mDatabase.child(locality).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        UserInfo details = dataSnapshot1.getValue(UserInfo.class);
                        Log.d("Received values", String.valueOf(details));
                        userList.add(details);
                    }
                    if (mMap != null && mGoogleApiClient != null) {
                        //syncMap();
                        addLocationDataToMapAfterFetchingFrom(mMap);
                    }

                }

                @Override
                public void onCancelled(DatabaseError e) {
                    System.out.println("The read Failed with exception: " + e.toException());
                }
            });*/
            DatabaseInstance databaseInstance = new DatabaseInstance();
            if(onDataReceivedInterfaceListener != null && locality != null)
                DatabaseInstance.setListener(this);
                databaseInstance.getAllUsers(locality);
        }
    }

    public void addLocationDataToMapAfterFetchingFrom(final GoogleMap googleMap) {
        mMap = googleMap;
        Random r = new Random();
        int ri = r.nextInt(9+1);
        for (int i = 0; i < userList.size(); i++) {
            String uid = userList.get(i).getId();
            if (uid.equals(userID)) {
                LatLng location = new LatLng(userList.get(i).getLatitude(), userList.get(i).getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(userList.get(i).getName()).snippet(userList.get(i).getAddress()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                markerList.add(marker);
            } else {
                //bmp = new MarkerLoadTask(userList.get(i).getPhotoURL());
                //Bitmap bmp = markerLoadTask.loadMarker();
                //new MarkerLoad(userList.get(i).getLatitude(),userList.get(i).getLongitude(),userList.get(i).getName(),userList.get(i).getAddress(),userList.get(i).getPhotoURL());
                Marker marker = createMarker(userList.get(i).getLatitude(), userList.get(i).getLongitude(), userList.get(i).getName(), userList.get(i).getAddress());
                markerList.add(marker);
                if(i == ri){
                    marker.showInfoWindow();
                }
                /*final int index = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final URL imageUrl = new URL(userList.get(index).getPhotoURL());
                            final Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openStream());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Marker marker = createMarker(userList.get(index).getLatitude(),userList.get(index).getLongitude(),userList.get(index).getName(),userList.get(index).getAddress(),bitmap,userList.get(index).getPhotoURL());
                                    markerList.add(marker);
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/

            }
        }
        getDeviceLocation();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 200; // offset from edges of the map in pixels
        if (markerList.size() > 1) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);
        } else {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(markerList.get(0).getPosition(), 10);
            googleMap.animateCamera(cu);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DatabaseInstance.setListener(null);

    }

    @Override
    public void onStop() {
        super.onStop();
        /*mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        onDataReceivedInterfaceListener = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {


        Intent i = new Intent(getActivity(), InfoWindowPersonDetails.class);
        Bundle b = new Bundle();

        for (int j = 0; j < userList.size(); j++) {
            if (marker.getTitle().equals(userList.get(j).getName())) {
                b.putSerializable("userInfo", userList.get(j));
                i.putExtra("locality", locality);
                i.putExtras(b);
                startActivity(i);
            }
        }
        /*Intent i = new Intent(getActivity(),ProfileActivity.class);
        Bundle b = new Bundle();
        for(int j=0 ; j<userList.size() ; j++){
            if(marker.getSnippet().equals(userList.get(j).getAddress())){
                b.putSerializable("userInfo", userList.get(j));
                i.putExtra("locality",locality);
                i.putExtras(b);
                startActivity(i);
            }
        }*/


    }

    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location) {
        myLocationMarker = mapObject.addMarker(new MarkerOptions().position(location).title("Your Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        markerList.add(myLocationMarker);
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
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
        cord = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        mMap.setMyLocationEnabled(true);
        //userinfo.setCoordinates(cord);
        /*mMap.addMarker(new MarkerOptions()
                .title(getString(R.string.title_activity_maps))
                .position(cord)
                .snippet("Current Location"))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));*/
        //mMap.clear();
        if(myLocationMarker == null){
            markStartingLocationOnMap(mMap, cord);
        }


    }

    @Override
    public void onConnected(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
        syncMap();
    }

    private void syncMap() {
        mapFragment2.getMapAsync(this);
    }

    @Override
    public void onDataReceived(ArrayList<UserInfo> userInfoArrayList) {
        userList = userInfoArrayList;
        if (mMap != null && mGoogleApiClient != null && userList != null) {
            //syncMap();
            addLocationDataToMapAfterFetchingFrom(mMap);
        }
    }

    @Override
    public void getUserDetails(UserDetails userInfoArrayList) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        Bundle bundle = getActivity().getIntent().getParcelableExtra("bundle");
        mReceivedLocation = bundle.getParcelable("coordinates");
        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
    }
    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        //title = title + "_" + bmp.toString();
        //title = title + "_" + photoURL;
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                /*.icon(BitmapDescriptorFactory.fromBitmap(bmp))*/);
    }

    //Custom Info Window
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
        private View myContentsView;
        MyInfoWindowAdapter(){
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.custom_info_content, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            //String str = marker.getTitle();
            //final String[] str2 = str.split("_");
            //Bitmap bmp = StringToBitMap(str2[1]);
            TextView tv_Title = ((TextView) myContentsView.findViewById(R.id.titleView));
            tv_Title.setText(marker.getTitle());
            TextView tv_Snippet = ((TextView) myContentsView.findViewById(R.id.snippetView));
            tv_Snippet.setText(marker.getSnippet());
            //ImageView iv_Icon = ((ImageView)myContentsView.findViewById(R.id.icon));
            //iv_Icon.setImageBitmap(bmp);
            //new ImageLoadTask(str2[1],iv_Icon);


            return myContentsView;
        }
        public Bitmap StringToBitMap(String encodedString){
            try {
                byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch(Exception e) {
                e.getMessage();
                return null;
            }
        }
    }
}
