package bank.axis.nearbyme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInEmailActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    //private TextView mStatusTextView;
    //private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignIn;
    //private Button mSignOut;
    private TextView createAccount;

    private EditText et_signup_email,et_signup_pass;
    private Button bt_signup;
    private TextView tv_signup_signin;
    private View layout_signin,layout_signup;

    ProgressDialog pd;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_imported);

        pd = new ProgressDialog(SignInEmailActivity.this);
        pd.setMessage("Loading");
        // Views
        //mStatusTextView = (TextView) findViewById(R.id.status);
        //mDetailTextView = (TextView) findViewById(R.id.detail);
        mEmailField = (EditText) findViewById(R.id.et_imported_email);
        mPasswordField = (EditText) findViewById(R.id.et_imported_pass);
        mSignIn = (Button) findViewById(R.id.bt_imported_signin);
        //mSignOut = (Button) findViewById(R.id.bt_imported_signout);
        createAccount = (TextView) findViewById(R.id.tv_imported_signup);

        tv_signup_signin = (TextView) findViewById(R.id.tv_signup_signin);
        bt_signup = (Button) findViewById(R.id.bt_signup);
        et_signup_email = (EditText) findViewById(R.id.et_signup_email);
        et_signup_pass = (EditText) findViewById(R.id.et_signup_pass);

        layout_signin = findViewById(R.id.layout_signin);
        layout_signup = findViewById(R.id.layout_signup);

        mEmailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    if(TextUtils.isEmpty(mEmailField.getText()))
                        mEmailField.setError("This field cannot be left blank");
                }
            }
        });
        mPasswordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    if(TextUtils.isEmpty(mPasswordField.getText()))
                        mPasswordField.setError("This field cannot be left blank");
                }
            }
        });


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(mEmailField.getText()))
                    mEmailField.setError("This field cannot be left blank");
                else if(TextUtils.isEmpty(mPasswordField.getText()))
                    mPasswordField.setError("This field cannot be left blank");
                else {
                    signIn("thought@axisbank.com", "qwerty");
                }
            }
        });

        et_signup_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    if(TextUtils.isEmpty(et_signup_email.getText()))
                        et_signup_email.setError("This field cannot be left blank");
                }
            }
        });
        et_signup_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    if(TextUtils.isEmpty(et_signup_pass.getText()))
                        et_signup_pass.setError("This field cannot be left blank");
                }
            }
        });

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_signup_email.getText()))
                    et_signup_email.setError("This field cannot be left blank");
                else if(TextUtils.isEmpty(et_signup_pass.getText()))
                    et_signup_pass.setError("This field cannot be left blank");
                else {
                    createAccount(et_signup_email.getText().toString(), et_signup_pass.getText().toString());
                }
            }
        });

        tv_signup_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_signup.setVisibility(View.GONE);
                layout_signin.setVisibility(View.VISIBLE);
            }
        });

        /*mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });*/

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_signin.setVisibility(View.GONE);
                layout_signup.setVisibility(View.VISIBLE);
            }
        });
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInEmailActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void hideProgressDialog() {
        pd.hide();
    }

    private void showProgressDialog() {
        pd.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pd.dismiss();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInEmailActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInEmailActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SignInEmailActivity.this,EmployeeDetails.class);
            i.putExtra("key1","Lakshesh Girdhar");
            i.putExtra("key2",user.getEmail());
            i.putExtra("key3","https://lh4.googleusercontent.com/-y815ElnucOY/AAAAAAAAAAI/AAAAAAAANDI/VuX6xP4gmO8/s96-c/photo.jpg");
            //i.putExtra("key3","https://doc-00-6c-docs.googleusercontent.com/docs/securesc/ha0ro937gcuc7l7deffksulhg5h7mbp1/3fi15rna3e04b9aef3so8evg5e6c5utq/1499925600000/10307004476748173491/*/0BwhT1OH_ubtmMTRXOEJfSU5INzQ");
            startActivity(i);
            finish();

        } else {
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
    }*/
}
