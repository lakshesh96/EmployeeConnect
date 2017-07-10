package bank.axis.nearbyme.UserDetails;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LAKSHESH on 29-Jun-17.
 */

public class MarkerLoadTask extends AsyncTask<Void, Void, Bitmap> {
    private String photoURL;

    public MarkerLoadTask(){}

    public MarkerLoadTask(String photoURL){
        this.photoURL = photoURL;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        try {
            URL urlConnection = new URL(photoURL);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        Bitmap b = result;
    }
}
