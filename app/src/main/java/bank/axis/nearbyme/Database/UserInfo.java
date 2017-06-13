package bank.axis.nearbyme.Database;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by LAKSHESH on 13-Jun-17.
 */

public class UserInfo {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private LatLng coordinates;
    private String pincode;

    public UserInfo(){}

    public UserInfo(String name,String phoneNumber,String email,String address,LatLng coordinates){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.coordinates = coordinates;
    }

    /*public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
