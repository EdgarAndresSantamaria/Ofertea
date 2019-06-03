package com.example.proyecto_1;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    /**
     * Keep track of the messaging task to ensure we can cancel it if requested.
     */
    private UserMessageTask mMessageTask = null;
    private ImageUploadTask mImageTask = null;
    private String elToken;


    /**
     * This method creates the GUI for item details and ensemble the action logic
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //display GUI
        setContentView(R.layout.activity_item_detail);
        // retrieve user preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // retrieve style status
        Boolean style =  prefs.getBoolean("estilo",false);

        // recoger elementos remotos
        //ImageDownloadTask mImageDlTask = new ImageDownloadTask(getApplicationContext());
        //mImageDlTask.execute((Void) null);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        elToken = task.getResult().getToken();
                    }
                });

        if(style){
            AppBarLayout bar = (AppBarLayout) findViewById(R.id.app_bar);
            bar.setBackgroundColor(Color.parseColor("#FF33b5e5"));
        }

        // set the action to fab5 icon (favourite)
        FloatingActionButton fab5 = (FloatingActionButton) findViewById(R.id.fab5);
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // realizar peticion a servicio FCM
                // pedir token
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("token", "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                // Show a progress spinner, and kick off a background task to
                                // perform the user login attempt.
                                mMessageTask = new UserMessageTask(token,getApplicationContext());
                                mMessageTask.execute((Void) null);
                                int mCod = mMessageTask.getmCod();
                                // check result
                                if(mCod == -1){
                                    showBasicMsg(getString(R.string.success));
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

                            }});
            }});


        // set the action to fab6 icon (backward)
        FloatingActionButton fab6 = (FloatingActionButton) findViewById(R.id.fab6);
        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayList();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
/**

        // esperar a la carga asincrona
        while(mImageDlTask.getmImage() == null){

        }
        Bitmap elBitmap = mImageDlTask.getmImage();
        CoordinatorLayout layout = findViewById(R.id.layout_coord);
        //ImageView Setup
        ImageView imageView = new ImageView(this);
        //setting image resource
        imageView.setImageBitmap(elBitmap);
        //setting image position
        imageView.setLayoutParams(new AppBarLayout.LayoutParams(100,
                100));

        layout.addView(imageView);
**/
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }


    /**
     * This method manages the function of back button
     */
    @Override
    public void onBackPressed() {
        // return to list view
        displayList();
    }

    /**
     * This method starts ItemListActivity
     */
    public void displayList(){
        // start Activity 'itemListActivity'
        Intent i = new Intent(this , ItemListActivity.class);
        startActivity(i);
        finish();
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
