package bank.axis.nearbyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.Database.UserDetails;
import bank.axis.nearbyme.Database.UserInfo;

public class InfoWindowPersonDetails extends AppCompatActivity {

    private UserInfo userInfo;
    private ArrayList<UserDetails> userList;
    private UserDetails userDetails;
    private String locality;

    private TextView tv_name, tv_email, tv_address, tv_phone, tv_dept, tv_post, tv_jobrole;
    private TextView bt_call, bt_text;
    private ImageView iv_profile_icon;
    private String phoneNumber;
    private DatabaseReference mDatabase;

    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window_person_details);

        mDatabase = DatabaseInstance.getFirebaseInstance().getReference("UserDetails");

        userList = new ArrayList<UserDetails>();
        userDetails = new UserDetails();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        //locality = sharedPref.getString("locality", "not found");

        tv_name = (TextView) findViewById(R.id.tv_Name);
        tv_email = (TextView) findViewById(R.id.tv_Email);
        tv_address = (TextView) findViewById(R.id.tv_Address);
        tv_phone = (TextView) findViewById(R.id.tv_Phone);
        tv_dept = (TextView) findViewById(R.id.tv_Dept);
        tv_post = (TextView) findViewById(R.id.tv_Post);
        tv_jobrole = (TextView) findViewById(R.id.tv_JobRole);
        iv_profile_icon = (ImageView) findViewById(R.id.iv_profile_icon);
        bt_call = (Button) findViewById(R.id.bt_call);
        bt_text = (Button) findViewById(R.id.bt_text);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            userInfo = (UserInfo) b.getSerializable("userInfo");
            locality = this.getIntent().getStringExtra("locality");
        }
        if (userInfo.getPhotoURL() != null) {
            new ImageLoadTask(userInfo.getPhotoURL(), iv_profile_icon).execute();
        } else
            iv_profile_icon.setImageResource(R.drawable.question_mark);

        tv_name.setText(userInfo.getName());
        tv_address.setText(userInfo.getAddress());
        tv_email.setText(userInfo.getEmail());

        fetchDataFromFirebase();

        if (tv_phone.getText().equals(null))
            tv_phone.setText("Not Known");

        if (tv_dept.getText() == null)
            tv_dept.setText("Not Known");

        if (tv_post.getText() == null)
            tv_post.setText("Not Known");

        if (tv_jobrole.getText() == null)
            tv_jobrole.setText("Not Known");

        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(InfoWindowPersonDetails.this,
                        android.Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                   /* // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(InfoWindowPersonDetails.this,
                            android.Manifest.permission.CALL_PHONE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {*/

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(InfoWindowPersonDetails.this,
                                new String[]{android.Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.

                }
                else {
                    if (phoneNumber != null) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));

                        if (ActivityCompat.checkSelfPermission(InfoWindowPersonDetails.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(intent);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(InfoWindowPersonDetails.this);
                        builder.setTitle("Sorry!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("No Phone Number found.");
                        builder.show();
                    }
                }
            }
        });

        bt_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(phoneNumber != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoWindowPersonDetails.this);
                    builder.setTitle("Sorry!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("No Phone Number found.");
                    builder.show();
                }
            }
        });

        /*bt_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(InfoWindowPersonDetails.this,
                        android.Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(InfoWindowPersonDetails.this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+phoneNumber));

                    if (ActivityCompat.checkSelfPermission(InfoWindowPersonDetails.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                }

            }
        });*/

    }

    private void fetchDataFromFirebase() {
        if(locality != "not found") {
            mDatabase.child(locality).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        UserDetails details = dataSnapshot1.getValue(UserDetails.class);
                        Log.d("Received values", String.valueOf(details));
                        userList.add(details);
                    }
                    if(userList != null){
                        addLocationDataAfterFetching();
                    }

                }
                @Override
                public void onCancelled(DatabaseError e) {
                    System.out.println("The read Failed with exception: " + e.toException());
                }
            });

        }
    }

    private void addLocationDataAfterFetching() {

        for(int i=0 ; i<userList.size() ; i++){
            if(userInfo.getEmail().equals(userList.get(i).getEmail())){
                phoneNumber = userList.get(i).getPhone();
                tv_phone.setText(userList.get(i).getPhone());
                tv_dept.setText(userList.get(i).getDept());
                tv_post.setText(userList.get(i).getPost());
                tv_jobrole.setText(userList.get(i).getJobrole());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+phoneNumber));

                    if (ActivityCompat.checkSelfPermission(InfoWindowPersonDetails.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoWindowPersonDetails.this);
                    builder.setTitle("Oops!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Please grant permission inorder to contact this person.");
                    builder.show();
                    //bt_call.setVisibility(View.INVISIBLE);

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            /*case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoWindowPersonDetails.this);
                    builder.setTitle("This is Alert Box").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Please grant permission inorder to contact this person.");
                    builder.show();
                }
                return;
            }*/
        }
    }
}
