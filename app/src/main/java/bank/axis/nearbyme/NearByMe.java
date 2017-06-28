package bank.axis.nearbyme;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by LAKSHESH on 23-Jun-17.
 */

public class NearByMe extends Application{
    private static GoogleApiClient mGoogleApiClient;
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
