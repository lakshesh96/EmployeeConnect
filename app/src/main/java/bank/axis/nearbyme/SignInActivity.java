package bank.axis.nearbyme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import bank.axis.nearbyme.Database.UserInfo;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    public GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    EditText et_userid,et_pass;
    Button signInButton,emailSignIn;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 2;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 3;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4;
    String uid;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_imported);

        userInfo = new UserInfo();

        et_userid = (EditText) findViewById(R.id.et_imported_email);
        et_pass = (EditText) findViewById(R.id.et_imported_pass);
        signInButton = (Button) findViewById(R.id.bt_imported_signin);
        emailSignIn = (Button) findViewById(R.id.bt_imported_signin_dummy);

        et_userid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    if(TextUtils.isEmpty(et_userid.getText()))
                        et_userid.setError("This field cannot be left blank");
                }
            }
        });
        et_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    if(TextUtils.isEmpty(et_pass.getText()))
                        et_pass.setError("This field cannot be left blank");
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("355485271926-7urd9ctu0h1r6k6tqbtt3han3s6ig0fg.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();


        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        Boolean check = isNetworkAvailable();
        if(check == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
            builder.setTitle("Error!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Please provide necessary permissions to proceed.");
            builder.show();
        }

        if (ContextCompat.checkSelfPermission(SignInActivity.this,
                android.Manifest.permission.INTERNET)/* +
                ContextCompat.checkSelfPermission(SignInActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)*/
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(SignInActivity.this,
                    new String[]{android.Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
            /*

            */

            /*ActivityCompat.requestPermissions(SignInActivity.this,
                    new String[]{android.Manifest.permission.INTERNET,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_INTERNET_AND_LOCATION);*/
        }
        else{

            /*OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {

                Log.d(TAG, "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                firebaseAuthWithGoogle(result);
                signIn();
            }*/
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*if(TextUtils.isEmpty(et_userid.getText()))
                        et_userid.setError("This field cannot be left blank");
                    else if(TextUtils.isEmpty(et_pass.getText()))
                        et_pass.setError("This field cannot be left blank");
                    else{*/
                        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                        if (opr.isDone()) {

                            Log.d(TAG, "Got cached sign-in");
                            GoogleSignInResult result = opr.get();
                            firebaseAuthWithGoogle(result);

                        } else {
                            showProgressDialog();
                            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                                @Override
                                public void onResult(GoogleSignInResult googleSignInResult) {
                                    hideProgressDialog();
                                }
                            });
                        }
                        signIn();
                    //}
                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            firebaseAuthWithGoogle(result);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInResult result) {
        GoogleSignInAccount acct = result.getSignInAccount();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            handleSignInResult(result);
                            user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                            builder.setTitle("Oops!").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Authentication failed, Please Check your Network Connections");
                            builder.show();
                            //updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //firebaseAuthWithGoogle(acct);
            Toast.makeText(this, "Welcome "+acct.getDisplayName(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SignInActivity.this,EmployeeDetails.class);
            i.putExtra("key1",acct.getDisplayName());
            i.putExtra("key2",acct.getEmail());
            i.putExtra("key3",acct.getPhotoUrl().toString());
            mAuth = FirebaseAuth.getInstance();
            startActivity(i);
            finish();
        } else {
            Log.d(TAG,"SignIn Failed");
        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut(/*GoogleApiClient temp*/) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient/*temp*/).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(SignInActivity.this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                        //updateUI(false);
                    }
                });
    }
    public void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(SignInActivity.this, "Successfully Disconnected", Toast.LENGTH_SHORT).show();
                        //updateUI(false);
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    signInButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                            if (opr.isDone()) {

                                Log.d(TAG, "Got cached sign-in");
                                GoogleSignInResult result = opr.get();
                                firebaseAuthWithGoogle(result);

                            } else {
                                showProgressDialog();
                                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                                    @Override
                                    public void onResult(GoogleSignInResult googleSignInResult) {
                                        hideProgressDialog();
                                    }
                                });
                            }
                            signIn();
                        }
                    });


                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
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
