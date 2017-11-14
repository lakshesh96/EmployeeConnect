package bank.axis.nearbyme;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bt_proceed;
    private static int SPLASH_TIME_OUT = 3000;
    LocationManager locationManager;
    LocationManager locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this,UserEducation3.class);
                startActivity(i);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
