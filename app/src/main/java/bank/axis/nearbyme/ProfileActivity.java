package bank.axis.nearbyme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.Database.UserDetails;
import bank.axis.nearbyme.Database.UserInfo;
import bank.axis.nearbyme.Database.onDataReceivedInterface;

public class ProfileActivity extends Fragment implements onDataReceivedInterface{

    private ArrayList<UserDetails> userList;
    private String name,photoURL,locality;

    private TextView tv_name_head;
    private ImageView iv_profile_icon;
    private DatabaseReference mDatabase;
    private static UserDetails details;
    private String MY_PREFS_NAME = "MyLocationData";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    private SupportMapFragment mapFragment;

    private onDataReceivedInterface onDataReceivedInterfaceListener;

    public ProfileActivity(){}

    public static ProfileActivity newInstance(String param1, String param2){
        ProfileActivity profileActivity = new ProfileActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2,param2);
        profileActivity.setArguments(args);
        return profileActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onDataReceivedInterfaceListener = this;

        setHasOptionsMenu(true);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            photoURL = getArguments().getString("photoURL");
        }

        mDatabase = DatabaseInstance.getFirebaseInstance().getReference("UserDetails");
        userList = new ArrayList<UserDetails>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
        locality = sharedPreferences.getString("locality", "not found");
        if(locality.equals("not found")){
            locality = "Bengaluru";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_profile2, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        iv_profile_icon = (ImageView) rootView.findViewById(R.id.iv_profile_img);
        tv_name_head = (TextView) rootView.findViewById(R.id.tv_name);
        if(photoURL != null) {
            //new ImageLoadTask(photoURL, iv_profile_icon).execute();
            Picasso.with(getActivity()).load(photoURL).into(iv_profile_icon);
        }
        else
            iv_profile_icon.setImageResource(R.drawable.question_mark);

        tv_name_head.setText(name);

        fetchDataFromFirebase();

        return rootView;
    }

    private void fetchDataFromFirebase() {

        DatabaseInstance databaseInstance = new DatabaseInstance();
        if(onDataReceivedInterfaceListener != null && locality != null)
            DatabaseInstance.setListener(this);
        databaseInstance.getUserDetails(locality);
    }

    @Override
    public void onDataReceived(ArrayList<UserInfo> userInfoArrayList) {

    }

    @Override
    public void getUserDetails(UserDetails userInfoArrayList) {

        details = userInfoArrayList;
        if(details != null) {
            setDetailsinLayout(details);
        }
    }

    private void setDetailsinLayout(UserDetails userDetails) {

        TextView name = (TextView) rootView.findViewById(R.id.profile_name);
        name.setText(userDetails.getName());
        TextView email = (TextView) rootView.findViewById(R.id.profile_email);
        email.setText(userDetails.getEmail());
        TextView phone = (TextView) rootView.findViewById(R.id.profile_phone);
        phone.setText(userDetails.getPhone());
        TextView address = (TextView) rootView.findViewById(R.id.profile_address);
        address.setText(userDetails.getAddress());

        TextView dept = (TextView) rootView.findViewById(R.id.profile_dept);
        dept.setText(userDetails.getDept());
        TextView post = (TextView) rootView.findViewById(R.id.profile_post);
        post.setText(userDetails.getPost());
        TextView jobrole = (TextView) rootView.findViewById(R.id.profile_jobrole);
        jobrole.setText(userDetails.getJobrole());
    }

    @Override
    public void onPause() {
        super.onPause();
        DatabaseInstance.setListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        onDataReceivedInterfaceListener = this;
    }
}
