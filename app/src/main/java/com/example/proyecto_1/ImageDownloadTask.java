package com.example.proyecto_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import javax.net.ssl.HttpsURLConnection;

/**
 * Represents an asynchronous download task
 * the user.
 */
public class ImageDownloadTask extends AsyncTask<Void, Void, Boolean> {

    // values to intern control download attempt
    private Bitmap mImage;
    private String imageUrl;
    private Context mContext;
    public Boolean status;

    /**
     * Constructor for asynchronous download task
     *
     * @param app
     */
    public ImageDownloadTask(Context app, String url) {
        // set up control values
        mImage = null;
        mContext = app;
        imageUrl =url;
    }

    /**
     * This method allows to get downloaded image
     * @return
     */
    public Bitmap getmImage(){
        return mImage;
    }

    /**
     * This method launches the messaging task in background
     *
     * @param params
     * @return
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        Log.v("llamando a imagen","https://134.209.235.115/eandres011/WEB/" + imageUrl);
            try {
                // attempt download ( default image )
                HttpsURLConnection urlConnection = com.example.proyecto_1.GeneradorConexionesSeguras.getInstance().crearConexionSegura(mContext, "https://134.209.235.115/eandres011/WEB/'"+imageUrl+"'.jpg");
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.v("success","imagen descargada");
                     // generate bitmap from image
                     mImage = BitmapFactory.decodeStream(urlConnection.getInputStream());
                    return true;
                }else{
                    return false;
                }
            // catch any exception
            } catch (Exception e) {
                return false;
            }
    }

    /**
     * This method does nothing
     *
     * @param success
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        // nothing to do
    }

    /**
     * This method does nothing
     */
    @Override
    protected void onCancelled() {
        // cant stop task
    }
}
