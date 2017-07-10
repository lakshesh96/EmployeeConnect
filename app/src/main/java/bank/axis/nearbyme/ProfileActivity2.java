package bank.axis.nearbyme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import bank.axis.nearbyme.Database.UserDetails;
import bank.axis.nearbyme.Database.UserInfo;
import bank.axis.nearbyme.Database.onDataReceivedInterface;

/**
 * Created by LAKSHESH on 10-Jul-17.
 */

public class ProfileActivity2 extends AppCompatActivity implements onDataReceivedInterface{

    private ArrayList<UserDetails> userList;
    private String name,photoURL,locality;

    private TextView tv_name_head;
    private ImageView iv_profile_icon;
    private DatabaseReference mDatabase;
    private static UserDetails details;
    private String MY_PREFS_NAME = "MyLocationData";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private onDataReceivedInterface onDataReceivedInterfaceListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        onDataReceivedInterfaceListener = this;
    }

    @Override
    public void onDataReceived(ArrayList<UserInfo> userInfoArrayList) {

    }

    @Override
    public void getUserDetails(UserDetails userInfoArrayList) {

    }
}
