package com.example.proyecto_1;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.HttpsURLConnection;

/**
 * Represents an asynchronous upload task used to authenticate
 * the user.
 */
public class ImageUploadTask extends AsyncTask<Void, Void, Boolean> {

    // values to intern control upload attempt
    private final String mImagen;
    private Context mContext;
    private String mUrl;
    private String mToken;
    private int mCod;

    /**
     * Constructor for asynchronous upload task
     *
     * @param imagen
     * @param app
     * @param url
     */
    public ImageUploadTask(String imagen, Context app, String url) {
        // set up control values
        mImagen = imagen;
        mContext = app;
        mUrl = url;
        mCod = -1;
    }

    /**
     * get status for displayng errors
     * @return
     */
    public int getmCod(){
        return mCod;
    }

    /**
     * This method launches the upload task in background
     *
     * @param params
     * @return
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        // attempt upload
        HttpsURLConnection urlConnection = com.example.proyecto_1.GeneradorConexionesSeguras.getInstance().crearConexionSegura(mContext, "https://134.209.235.115/eandres011/WEB/gestorImagenes.php");
        try {
            // request options setting
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("addImage", true);
            parametrosJSON.put("imagen", mImagen);
            parametrosJSON.put("url", mUrl);
            parametrosJSON.put("app", mToken);
            // adding JSON content
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                // constructing response JSON
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                // getting response values from server
                JSONObject json = new JSONObject(result);
                // status handles server responses
                String status = (String) json.get("estado");
                // code handles server issues
                int cod = (int) json.get("code");
                if (status == "ok") {
                    return true;
                } else {
                    // set status code
                    mCod = cod;
                    return false;
                }
            } else {
                // handle wrong calls
                mCod = 3;
                return false;
            }
            // catch any exception
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method displays an error if messaging attemp failed
     *
     * @param success
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        // nothing to do
    }

    /**
     * This method manages the situation that the user stops the messaging attempt
     */
    @Override
    protected void onCancelled() {
        // cant stop task
    }
}