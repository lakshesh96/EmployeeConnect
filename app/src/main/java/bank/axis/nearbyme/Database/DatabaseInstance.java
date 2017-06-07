package bank.axis.nearbyme.Database;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by LAKSHESH on 02-Jun-17.
 */

public class DatabaseInstance {
    private static FirebaseDatabase firebaseInstance;

    private DatabaseInstance(){}

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
}
