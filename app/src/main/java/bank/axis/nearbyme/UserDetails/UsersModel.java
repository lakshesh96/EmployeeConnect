package bank.axis.nearbyme.UserDetails;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LAKSHESH on 02-Jun-17.
 */

public class UsersModel {

    private String email,name;
    private String number;

    /*public UsersModel(String name, String email, String number)
    {

    }*/

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber(){
        return number;
    }
    public void setNumber(String number){
        this.number=number;
    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name",name);
        result.put("email",email);
        result.put("number",number);
        return result;
    }





}
