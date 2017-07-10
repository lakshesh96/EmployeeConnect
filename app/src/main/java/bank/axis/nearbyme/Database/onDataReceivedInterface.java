package bank.axis.nearbyme.Database;

import java.util.ArrayList;

/**
 * Created by LAKSHESH on 04-Jul-17.
 */

public interface onDataReceivedInterface {

    public void onDataReceived(ArrayList<UserInfo> userInfoArrayList);

    public void getUserDetails(UserDetails userInfoArrayList);
}
