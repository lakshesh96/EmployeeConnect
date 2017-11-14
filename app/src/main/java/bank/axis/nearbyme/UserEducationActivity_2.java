package bank.axis.nearbyme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class UserEducationActivity_2 extends AppCompatActivity {

    Button bt_begin;
    ImageView iv_edu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_education_2);

        iv_edu = (ImageView) findViewById(R.id.imageView3);
        bt_begin = (Button) findViewById(R.id.bt_user_education_begin);

        bt_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_edu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
