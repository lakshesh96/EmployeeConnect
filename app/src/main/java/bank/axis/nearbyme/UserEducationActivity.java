package bank.axis.nearbyme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class UserEducationActivity extends AppCompatActivity {

    ImageButton bt_next;
    Button bt_allow_permissions;
    private int flag = 0;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 2;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_education);

        bt_next = (ImageButton) findViewById(R.id.bt_user_education_next);
        bt_allow_permissions = (Button) findViewById(R.id.bt_permissions);

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==1){
                    Intent i = new Intent(UserEducationActivity.this,SignInEmailActivity.class);
                    startActivity(i);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserEducationActivity.this);
                    builder.setTitle("Sorry!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Please provide necessary permissions to Continue!");
                    builder.show();
                }
            }
        });

        bt_allow_permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(UserEducationActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                ActivityCompat.requestPermissions(UserEducationActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);

                Boolean check = isNetworkAvailable();
                if(check == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserEducationActivity.this);
                    builder.setTitle("Error!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Please provide necessary permissions to proceed.");
                    builder.show();
                }

                if (ContextCompat.checkSelfPermission(UserEducationActivity.this,
                        android.Manifest.permission.INTERNET)/* +
                ContextCompat.checkSelfPermission(SignInActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)*/
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(UserEducationActivity.this,
                            new String[]{android.Manifest.permission.INTERNET},
                            MY_PERMISSIONS_REQUEST_INTERNET);
                }
                else {
                    Toast.makeText(UserEducationActivity.this, "Thank You!", Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(UserEducationActivity.this, "Thank You!", Toast.LENGTH_SHORT).show();
                    flag = 1;

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(UserEducationActivity.this);
                    builder.setTitle("Error!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Please connect to Internet before proceeding.");
                    builder.show();
                }
                return;
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
