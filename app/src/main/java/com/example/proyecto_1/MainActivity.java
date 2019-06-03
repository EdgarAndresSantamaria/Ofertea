package com.example.proyecto_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_1.dummy.DummyContent;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    // intern management use variables
    private String user;
    private Boolean language;
    private Boolean style;
    private final int LOCATION = 1;

    /**
     * This method displays the GUI associated to main Activity and ensembles its logic
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retrieve user preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        style = prefs.getBoolean("estilo", false);
        // check style settings
        if (style) {
            // if personalized
            setTheme(R.style.AppTheme1);
        } else {
            // if default
            setTheme(R.style.AppTheme);
        }
        // display GUI
        setContentView(R.layout.activity_main);
        // load item list content
        new DummyContent(this);
        // retrieve terns of use staus
        Boolean terminos = prefs.getBoolean("terns", false);
        //retrieve language preference
        language = prefs.getBoolean("ingles", false);
        if (!terminos) {
            // if terns not yet agreed
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // generate an alert
            // configure terns alert
            builder.setTitle(getString(R.string.ternsTit));
            builder.setMessage(getString(R.string.ternsDesc));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    agreeTerns();
                }
            });
            // if negative to terns
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // shutdown
                    finish();
                }
            });
            // if want to know about me
            builder.setNeutralButton(getString(R.string.neutral), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // show my CV
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EdgarAndresSantamaria/CV"));
                    startActivity(i);
                }
            });

            AlertDialog eldialogo = builder.create();
            eldialogo.show();
        }
        // ensemble the app personalized toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // configure the explanation for login / register button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.lTit), Snackbar.LENGTH_LONG);
                showBasicMsg(getString(R.string.lMess));
            }
        });
        // configure the explanation for Selected products button
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.pTit), Snackbar.LENGTH_LONG);
                showBasicMsg(getString(R.string.pMess));
            }
        });
        // configure the explanation for Contact information button
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.cTit), Snackbar.LENGTH_LONG);
                showBasicMsg(getString(R.string.cMess));
            }
        });
        // configure the explanation for Products button
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.prTit), Snackbar.LENGTH_LONG);
                showBasicMsg(getString(R.string.prMess));
            }
        });
        // configure the explanation for Preferences button
        FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.prefTit), Snackbar.LENGTH_LONG);
                showBasicMsg(getString(R.string.prefMess));
            }
        });
        // set up the personalized bar actions into layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // retrieve the navigation bar
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // get menu item object
        Menu nav_Menu = navigationView.getMenu();
        // hide unused items
        nav_Menu.findItem(R.id.fotos).setVisible(false);
        nav_Menu.findItem(R.id.carrito).setVisible(false);
        nav_Menu.findItem(R.id.salir).setVisible(false);
        // retrieve log information
        user = prefs.getString("user", "invitado");
        if (!user.equals("invitado")) {
            // display and hide dynamically
            nav_Menu.findItem(R.id.perfil).setVisible(false);
            nav_Menu.findItem(R.id.salir).setVisible(true);
            // initialize welcome greetings
            TextView welcome = (TextView) findViewById(R.id.textView3);
            welcome.setText(getString(R.string.wellcome) + user + " !");
        }

        if (style) {
            // manually change color to dynamic views
            toolbar.setBackgroundColor(Color.parseColor("#FF33b5e5"));
            fab4.setBackgroundTintList(ColorStateList.valueOf(0xFF33b5e5));
            fab3.setBackgroundTintList(ColorStateList.valueOf(0xFF33b5e5));
            fab2.setBackgroundTintList(ColorStateList.valueOf(0xFF33b5e5));
            fab1.setBackgroundTintList(ColorStateList.valueOf(0xFF33b5e5));
            fab.setBackgroundTintList(ColorStateList.valueOf(0xFF33b5e5));
        }
    }

    /**
     * This method launches Logi activity
     */
    public void startLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method launches Product list wiew
     */
    public void startProducts() {
        Intent i = new Intent(this, ItemListActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method updates the status of tern agreement and enables FCM
     */
    public void agreeTerns() {
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // get the preferernce writer
        SharedPreferences.Editor editor = prefs.edit();
        // edit terns status
        editor.putBoolean("terns", true);
        editor.commit();
        FirebaseApp.initializeApp(this);
        // let the user personalize the app before starting
        startPreferences();
    }


    /**
     * This method displays preferences GUI
     */
    public void startPreferences() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method updates login status in the App
     */
    public void closeSession() {
        // get shared info
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        // reset terns y user shared info
        editor.putBoolean("terns", false);
        editor.putString("user", "invitado");
        editor.commit();
        // close remote session
        HttpsURLConnection urlConnection = com.example.proyecto_1.GeneradorConexionesSeguras.getInstance().crearConexionSegura(getApplicationContext(), "https://134.209.235.115/eandres011/WEB/gestorUsuarios.php");
        try {
            // set request parameters
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("register", false);
            // write JSON parameters
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();
            if (urlConnection.getResponseCode() == 200) {
                // launch request
            }
            // catch any exception
        } catch (Exception e) {

        }

        //launch main activity
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


    private void showGallery(){
        //launch gallery activity
        Intent i = new Intent(this, gallery.class);
        startActivity(i);
        finish();
    }

    private void showMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                showBasicMsg("requerimos el permiso de localización para poder mostrarle las ofertas en el mapa, si ha decidido no volver  a recibir aviso debe ir a ajustes y otorgar permisos a la aplicación");

            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION);

        }else {

            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
            finish();
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(this, MapsActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    showBasicMsg("requerimos el permiso de localización para poder mostrarle las ofertas en el mapa, si ha decidido no volver  a recibir aviso debe ir a ajustes y otorgar permisos a la aplicación");

                }

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

    /**
     * This method manages the back button action
     */
    @Override
    public void onBackPressed() {
        // manage to cclose nav menu if was opened
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // if was in main activity
            // exit ofertea
            finish();
        }
    }

    /**
     * This method starts when Options menu is going to be displayed
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        TextView usuario = (TextView) findViewById(R.id.textView);
        usuario.setText(user);
        if(style){
            // if personalized style apply changes
            LinearLayout nav = (LinearLayout) findViewById(R.id.main_nav);
            nav.setBackgroundColor(Color.parseColor("#FF33b5e5"));
        }
        return true;
    }

    /**
     * This method manages the actions of each menu element
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here to id.
        int id = item.getItemId();
        // switch menu cases
        if (id == R.id.catalogo) {
            startProducts();
        } else if (id == R.id.fotos) {
            //nothing
        }  else if (id == R.id.carrito) {
            //nothing
        }  else if (id == R.id.perfil) {
            startLogin();
        }  else if (id == R.id.preferencias) {
            startPreferences();
        }  else if (id == R.id.salir) {
            closeSession();
        } else if (id == R.id.mapa) {
            showMap();
        } else if (id == R.id.camara) {
            showGallery();
        }


        // close navigation bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) { // retrieve preferences
        Log.i("location updated", "yes");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // stablish a writer
        SharedPreferences.Editor editor = prefs.edit();
        // redefine style flag
        editor.putInt("latitude",(int) location.getLatitude());
        editor.putInt("longitude",(int) location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
