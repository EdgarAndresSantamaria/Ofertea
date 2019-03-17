package com.example.proyecto_1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    // intern lenguage preferences flag
    private Boolean language;

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
        language =  prefs.getBoolean("ingles",false);

        if(style){
            AppBarLayout bar = (AppBarLayout) findViewById(R.id.app_bar);
            bar.setBackgroundColor(Color.parseColor("#FF33b5e5"));
        }

        // retrieve user login status
        String user =  prefs.getString("user","invitado");
        if(!user.equals("invitado")){
            //user loged
            // set the action to fab5 icon (favourite)
            FloatingActionButton fab5 = (FloatingActionButton) findViewById(R.id.fab5);
            fab5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(language){
                        // english version
                        showNotification("You added an article to favourites.", "we ll take care about it for future search");
                    }else {
                        // spanish version
                        showNotification("Has añadido un articulo a favoritos.", "tendremos en cuenta esta información para posteriores búsquedas");
                    }
                }
            });
        }else{
            // user not loged
            // set the action to fab5 icon (favourite)
            FloatingActionButton fab5 = (FloatingActionButton) findViewById(R.id.fab5);
            fab5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(language){
                        // english version
                        showNotification("only avaliable for registered users", "go to login / register section on menu");
                    }else {
                        // spanish version
                        showNotification("solo disponible para usuarios registrados", "ve al menú y registrate");
                    }
               }
            });
        }

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
     * This method shows a notification made with a title 'text' and a description as 'subText'
     * @param text
     * @param subText
     */
    public void showNotification(String text, String subText){
        // retrieve Notification Manager
        NotificationManager  manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Start a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "IdCanal");
        // for API > Oreo
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            // build notification
            NotificationChannel channel = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
            // personalize notification
            builder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle("info")
                    .setContentText(text)
                    .setSubText(subText)
                    .setVibrate(new long[]{0, 1000, 500, 1000}).setAutoCancel(true);
            // create channel
            manager.createNotificationChannel(channel);
            // show notification
            manager.notify(1, builder.build());
        }
    }

}
