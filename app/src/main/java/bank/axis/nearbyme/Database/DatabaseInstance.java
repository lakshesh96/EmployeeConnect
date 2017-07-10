package bank.axis.nearbyme.Database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by LAKSHESH on 02-Jun-17.
 */

public class DatabaseInstance {
    private static FirebaseDatabase firebaseInstance;
    private DatabaseReference mDatabase;
    private static onDataReceivedInterface onDataReceivedInterfaceListener;
    public DatabaseInstance(){}

    private static FirebaseDatabase createInstance(){
        if(firebaseInstance == null){
            firebaseInstance = FirebaseDatabase.getInstance();
            firebaseInstance.setPersistenceEnabled(true);
        }
        return firebaseInstance;
    }
    public static FirebaseDatabase getFirebaseInstance(){
        return createInstance();
    }



    public void getAllUsers(String locality){
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference("Cluster");
        final ArrayList<UserInfo> userList = new ArrayList<>();
        mDatabase.child(locality).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserInfo details = dataSnapshot1.getValue(UserInfo.class);
                    Log.d("Received values", String.valueOf(details));
                    userList.add(details);
                }
                if(getListener() != null)
                    getListener().onDataReceived(userList);
            }

            @Override
            public void onCancelled(DatabaseError e) {
                System.out.println("The read Failed with exception: " + e.toException());
            }
        });

    }

    public static void setListener(onDataReceivedInterface context){
        onDataReceivedInterfaceListener = context;
    }

    public static onDataReceivedInterface getListener(){
        return onDataReceivedInterfaceListener;
    }

    public void getUserDetails(String locality) {
        mDatabase = DatabaseInstance.getFirebaseInstance().getReference("UserDetails");
        final UserDetails[] userDetail = {new UserDetails()};
        mDatabase.child(locality).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDetails details = dataSnapshot.getValue(UserDetails.class);
                Log.d("Received values", String.valueOf(details));
                userDetail[0] = details;
                if(getListener() != null)
                    getListener().getUserDetails(userDetail[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
