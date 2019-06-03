package com.example.proyecto_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mindorks.placeholderview.PlaceHolderView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class gallery extends AppCompatActivity {

    private PlaceHolderView mGalleryView;
    private final int CODIGO_FOTO = 1;
    private List<String> urls;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        urls = new ArrayList<>();
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> set = prefs.getStringSet("urls",new HashSet<String>());
        for (String s : set) {
            urls.add(s);
            Log.v("nueva url encontrada",s);
        }
        Boolean style = prefs.getBoolean("estilo", false);
        // check style settings
        if (style) {
            // if personalized
            setTheme(R.style.AppTheme1);
        } else {
            // if default
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        findViewById(R.id.addPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (elIntentFoto.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(elIntentFoto, CODIGO_FOTO);
                }
            }});
        mGalleryView = (PlaceHolderView) findViewById(R.id.galleryView);

       for (String url:urls) {
            ImageDownloadTask descarga = new ImageDownloadTask(getApplicationContext(),url);
            descarga.execute((Void) null);
            while (descarga.getmImage() == null) {
                //Log.v("esperando","esperando");
            }
            mGalleryView
                    .addView(
                            new GalleryItem(getApplicationContext(),descarga.getmImage())
                    );
        }


    }

    /**
     * This method manages the back button action
     */
    @Override
    public void onBackPressed() {
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // stablish a writer
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(urls);
        // redefine style flag
        editor.putStringSet("urls",set);
        editor.commit();
        // return to main activity
        launchMain();
    }

    /**
     * Thi method launches the main activity
     */
    public void launchMain(){
        Intent i = new Intent(this , MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CODIGO_FOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap laminiatura = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            laminiatura.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            String fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String url = "IMG_" + timeStamp + "_";
            urls.add(url);
            ImageUploadTask mImageTask = new ImageUploadTask(fotoen64, getApplicationContext(), url);
            mImageTask.execute((Void) null);
            int mCod = mImageTask.getmCod();
            // check result
            if(mCod == -1){
                showBasicMsg(getString(R.string.successUpload));
            }else if(mCod == 0){
                showBasicMsg(getString(R.string.error_incorrect_password));
            }else if(mCod == 1){
                showBasicMsg(getString(R.string.error_inexistent_user));
            }else if(mCod == 2){
                showBasicMsg(getString(R.string.error_bad_login_register));
            }else if(mCod == 3){
                showBasicMsg(getString(R.string.error_incorrect_call));
            }else if(mCod == 4) {
                showBasicMsg(getString(R.string.error_server_error));
            }

        }
    }

    /**
     * This method displays a short message in top of the App
     * @param mensaje
     */
    private void showBasicMsg(String mensaje){
        // generate toast
        int tiempo= Toast.LENGTH_SHORT;
        Toast aviso = Toast.makeText(this, mensaje, tiempo);
        // show in upper bound centered
        aviso.setGravity(Gravity.TOP| Gravity.CENTER, 0, 0);
        aviso.show();
    }
}
