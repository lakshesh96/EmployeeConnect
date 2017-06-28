package bank.axis.nearbyme;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

/**
 * Created by LAKSHESH on 23-Jun-17.
 */

public class GoogleClient implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static GoogleApiClient mGoogleApiClient = null;
    private GoogleClientCallBack googleClientCallBack;
    public GoogleClient(Context context, GoogleClientCallBack googleClientCallBack){
        this.googleClientCallBack = googleClientCallBack;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleClientCallBack.onConnected(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    public static boolean isGoogleClientConnected(){
        if(mGoogleApiClient != null) return true;
        return false;
    }
}
