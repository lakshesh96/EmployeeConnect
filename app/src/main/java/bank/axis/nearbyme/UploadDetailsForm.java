package bank.axis.nearbyme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import bank.axis.nearbyme.Database.DatabaseInstance;
import bank.axis.nearbyme.Database.UserDetails;
import bank.axis.nearbyme.Database.UserInfo;

public class UploadDetailsForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView tv_name,tv_email,tv_address;
    private EditText et_phone,et_post,et_jobrole;
    private Spinner spinner_department;
    private String dept;
    private Button bt_publish;
    UserInfo userinfo;
    UserDetails userdetails;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details_form);

        userdetails = new UserDetails();
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference();

        tv_name = (TextView) findViewById(R.id.tv_Name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_address = (TextView) findViewById(R.id.tv_address);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_post = (EditText) findViewById(R.id.et_post);
        et_jobrole = (EditText) findViewById(R.id.et_jobrole);
        spinner_department = (Spinner) findViewById(R.id.spinner_department);
        bt_publish = (Button) findViewById(R.id.bt_publish);

        loadSpinnerData();
        spinner_department.setOnItemSelectedListener(this);

        Bundle b = this.getIntent().getExtras();
        if(b!=null){
            userinfo = (UserInfo) b.getSerializable("userinfo");
        }
        tv_name.setText(userinfo.getName());
        tv_email.setText(userinfo.getEmail());
        tv_address.setText(userinfo.getAddress());


        et_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    if (!isValidMobile(et_phone.getText().toString())) {
                        et_phone.setError("Enter Valid Mobile Number");
                    }
                }
            }
        });



        bt_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidMobile(et_phone.getText().toString())) {
                    et_phone.setError("Enter Valid Mobile Number");
                } else {
                      mDatabase.child("Cluster").child(userinfo.getLocality()).child(userinfo.getId()).setValue(userinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });

                    userdetails.setName(userinfo.getName());
                    userdetails.setEmail(userinfo.getEmail());
                    userdetails.setAddress(userinfo.getAddress());
                    if (et_phone.getText() != null)
                        userdetails.setPhone(et_phone.getText().toString());
                    else
                        userdetails.setPhone("Unknown");

                    if (et_post.getText() != null)
                        userdetails.setPost(et_post.getText().toString());
                    else
                        userdetails.setPost("Unknown");

                    if (et_jobrole.getText() != null)
                        userdetails.setJobrole(et_jobrole.getText().toString());
                    else
                        userdetails.setJobrole("Unknown");

                    if (spinner_department.getSelectedItem() != null)
                        userdetails.setDept(spinner_department.getSelectedItem().toString());
                    else
                        userdetails.setDept("Unknown");

                    mDatabase.child("UserDetails").child(userinfo.getLocality()).child(userinfo.getId()).setValue(userdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UploadDetailsForm.this, "Details added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }
            }
        });

    }

    private void loadSpinnerData() {
        List<String> labels = new ArrayList<>();
        labels.add("Select Department...");
        labels.add("Innovation Lab");
        labels.add("BIU");
        labels.add("RL&P");
        labels.add("BIU Risk");
        labels.add("BIU Collection");
        labels.add("Model Monitoring");
        labels.add("Digital Banking");
        labels.add("ABR");
        labels.add("DSI");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,labels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_department.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        dept = spinner_department.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean isValidMobile(String phone) {
        //return android.util.Patterns.PHONE.matcher(phone).matches();
        boolean check=false;
            if(phone.length() != 10) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        return check;
    }
}
