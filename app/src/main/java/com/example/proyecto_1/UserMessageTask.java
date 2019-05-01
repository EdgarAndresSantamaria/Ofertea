package com.example.proyecto_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.HttpsURLConnection;

/**
 * Represents an asynchronous messaging task
 * the user.
 */
public class UserMessageTask extends AsyncTask<Void, Void, Boolean> {

    // values to intern control messaging attempt
    private final String mToken;
    private Context mContext;
    private int mCod;

    /**
     * Constructor for asynchronous messaging task
     *
     * @param app
     * @param token
     */
    public UserMessageTask(String token, Context app) {
        // set up control values
        mToken = token;
        mContext = app;
        mCod = -1;
    }

    /**
     * get status for displayng errors
     *
     * @return
     */
    public int getmCod(){
        return mCod;
    }

    /**
     * This method launches the messaging task in background
     *
     * @param params
     * @return
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        // attempt messaging
        HttpsURLConnection urlConnection = com.example.proyecto_1.GeneradorConexionesSeguras.getInstance().crearConexionSegura(mContext, "https://134.209.235.115/eandres011/WEB/gestorMensajeria.php");
        try {
            // request options setting
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("mensaje", true);
            parametrosJSON.put("token", mToken);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            parametrosJSON.put("user", prefs.getString("user","invitado"));
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
