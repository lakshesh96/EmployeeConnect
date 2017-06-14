package bank.axis.nearbyme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
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

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    public GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Button bt_logout,bt_disconnect;
    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        bt_logout = (Button) findViewById(R.id.bt_logout);
        bt_disconnect = (Button) findViewById(R.id.bt_disconnect);
        bt_logout.setVisibility(View.INVISIBLE);
        bt_disconnect.setVisibility(View.INVISIBLE);

        try {
            int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if(off==0){
                Intent gpsOptionsIntent = new Intent (android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Toast.makeText(SignInActivity.this, "Please switch on GPS and then press Back", Toast.LENGTH_LONG).show();
                startActivity(gpsOptionsIntent);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        //GPS Permission
        /*
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


        } else {

        }*/
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                if (opr.isDone()) {
                    // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                    // and the GoogleSignInResult will be available instantly.
                    Log.d(TAG, "Got cached sign-in");
                    GoogleSignInResult result = opr.get();
                    handleSignInResult(result);
                    //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent();
//                    GoogleSignInAccount acct = result.getSignInAccount();
//                    textView.setText(acct.getDisplayName());
//                    Intent i = new Intent(SignInActivity.this,EmployeeDetails.class);
//                    i.putExtra("key1",acct.getDisplayName());
//                    //Log.d(TAG, acct.getDisplayName());
//                    i.putExtra("key2",acct.getEmail());
//                    //Log.d(TAG, acct.getEmail());
//                    i.putExtra("key3",acct.getPhotoUrl());
//                    startActivity(i);

                } else {
                    // If the user has not previously signed in on this device or the sign-in has expired,
                    // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                    // single sign-on will occur in this branch.
                    showProgressDialog();
                    opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(GoogleSignInResult googleSignInResult) {
                            hideProgressDialog();
                            //handleSignInResult(googleSignInResult);
                        }
                    });
                }
                signIn();
            }
        });

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        bt_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
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
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
            handleSignInResult(result);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
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
                            user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
            firebaseAuthWithGoogle(acct);
            Toast.makeText(this, "Welcome "+acct.getDisplayName(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(SignInActivity.this,EmployeeDetails.class);
            i.putExtra("key1",acct.getDisplayName());
            i.putExtra("key2",acct.getEmail());
            i.putExtra("key3",acct.getPhotoUrl().toString());
            mAuth = FirebaseAuth.getInstance();
            //uid = mAuth.getCurrentUser().getUid();
            //i.putExtra("uid",uid);
            //i.putExtra("key4",mGoogleApiClient.toString());
            startActivity(i);
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
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
            case R.id.bt_logout:
                signOut();
                break;
            case R.id.bt_disconnect:
                revokeAccess();
                break;
        }
    }
}
