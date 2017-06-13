package bank.axis.nearbyme.Database;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by LAKSHESH on 13-Jun-17.
 */

public class Cluster {

    private String uid;
    private LatLng coordinates;

    public Cluster(){

    }
    public Cluster(String uid, LatLng coordinates){
        this.uid = uid;
        this.coordinates = coordinates;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

}
