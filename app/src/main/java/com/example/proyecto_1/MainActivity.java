package com.example.proyecto_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceManager;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // intern management use variables
    private String user;
    private  Boolean language;
    private  Boolean style;

    /**
     * This method displays the GUI associated to main Activity and ensembles its logic
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO parametrize Alert config and fab config
        super.onCreate(savedInstanceState);
        // retrieve user preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        style =  prefs.getBoolean("estilo",false);
        // check style settings
        if(style){
            // if personalized
            setTheme(R.style.AppTheme1);
        }else{
            // if default
            setTheme(R.style.AppTheme);
        }
        // display GUI
        setContentView(R.layout.activity_main);
        // retrieve terns of use staus
        Boolean terminos =  prefs.getBoolean("terns",false);
        //retrieve language preference
        language =  prefs.getBoolean("ingles",false);
        if(!terminos) {
            // if terns not yet agreed
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // generate an alert
            if(!language){
                // if english
                // configure terns alert in english
                builder.setTitle("Términos de uso");
                builder.setMessage("Aceptar los términos de la App (foolink)?");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agreeTerns();
                    }
                });
                // if negative to terns
                builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // shutdown
                        finish();
                    }
                });
            }else{
                // if spanish
                // configure terns alert in spanish
                builder.setTitle("Terms of use");
                builder.setMessage("Accept App terms(foolink)?");
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agreeTerns();
                    }
                });
                // if negative to terns
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //shutdown
                        finish();
                    }
                });
            }
            //
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
              if(language){
                  // if english
                  Snackbar.make(view, "Login / Register", Snackbar.LENGTH_LONG);
                  showBasicMsg("This function manages the login or register on the app, " +
                          "only registered users ll get notified of favourite products");
              }else{
                  // if spanish
                  Snackbar.make(view, "Entrar / Registrarse", Snackbar.LENGTH_LONG);
                  showBasicMsg("Esta función te permitirá registrarte o entrar en la App, " +
                          "los usuarios registrados recibirán notificaciones de productos relacionados");
              }
            }
        });
        // configure the explanation for Selected products button
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (language) {
                    // if english
                    Snackbar.make(view, "Selected products", Snackbar.LENGTH_LONG);
                    showBasicMsg("This function allows to see selected offers " +
                            "and finalize the purchase");
                } else {
                    // if spanish
                    Snackbar.make(view, "ver carro de compra", Snackbar.LENGTH_LONG);
                    showBasicMsg("Esta función permite ver las ofertas que has seleccionado para " +
                            "proceder a su adquisición");
                }
            }
        });
        // configure the explanation for Contact information button
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (language) {
                    // if english
                    Snackbar.make(view, "Contact information", Snackbar.LENGTH_LONG);
                    showBasicMsg("This function sllows to see all the information about the App");

                } else {
                    // if spanish
                    Snackbar.make(view, "ver información", Snackbar.LENGTH_LONG);
                    showBasicMsg("Esta función te permitirá ver la información de contacto de esta App");

                }
            }
        });
        // configure the explanation for Products button
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (language) {
                    // if english
                    Snackbar.make(view, "Products", Snackbar.LENGTH_LONG);
                    showBasicMsg("This function shows all avaliable offers in the App");

                } else {
                    // if spanish
                    Snackbar.make(view, "ver catálogo", Snackbar.LENGTH_LONG);
                    showBasicMsg("Esta función te permite visualizar todas las ofertas del sistema");
                }
            }
        });
        // configure the explanation for Preferences button
        FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (language) {
                    // if english
                    Snackbar.make(view, "Preferences", Snackbar.LENGTH_LONG);
                    showBasicMsg("This function allows the App configuration");

                } else {
                    // if spanish
                    Snackbar.make(view, "ver preferencias", Snackbar.LENGTH_LONG);
                    showBasicMsg("Esta función te permitirá configurar la App");
                }
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
        nav_Menu.findItem(R.id.contacto).setVisible(false);
        nav_Menu.findItem(R.id.carrito).setVisible(false);
        nav_Menu.findItem(R.id.salir).setVisible(false);
        // retrieve log information
        user =  prefs.getString("user","invitado");
        if (!user.equals("invitado")) {
            // display and hide dynamically
            nav_Menu.findItem(R.id.perfil).setVisible(false);
            nav_Menu.findItem(R.id.salir).setVisible(true);
            // initialize welcome greetings
            TextView welcome = (TextView) findViewById(R.id.textView3);
            if(language){
                // if english
                welcome.setText("Welcome " + user + " !");
            }else {
                // if spanish
                welcome.setText("bienvenido " + user + " !");
            }
        }

        if(style){
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
    public void startLogin(){
        Intent i = new Intent(this , LoginActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method launches Product list wiew
     */
    public void startProducts(){
        Intent i = new Intent(this , ItemListActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method updates the status of tern agreement
     */
    public void agreeTerns(){
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // get the preferernce writer
        SharedPreferences.Editor editor = prefs.edit();
        // edit terns status
        editor.putBoolean("terns", true);
        editor.commit();
        // let the user personalize the app before starting
        startPreferences();
    }

    /**
     * TODO in development
     */
    public void startSelectedProducts(){
        //Intent i = new Intent(this , LoginActivity.class);
        //startActivity(i);
    }

    /**
     * TODO in development
     */
    public void startInfo(){
        //Intent i = new Intent(this , LoginActivity.class);
        //startActivity(i);
    }

    /**
     * This method displays preferences GUI
     */
    public void startPreferences(){
        Intent i = new Intent(this , SettingsActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method updates login status in the App
     */
    public void closeSession(){
        //TODO parametrize main activity launch
        // get shared info
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor= prefs.edit();
        // reset terns y user shared info
        editor.putBoolean("terns", false);
        editor.putString("user","invitado");
        editor.commit();
        //launch main activity
        Intent i = new Intent(this , MainActivity.class);
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

    /**
     * This method manages the back button action
     */
    @Override
    public void onBackPressed() {
        //TODO parametrize main activity launch
        // manage to cclose nav menu if was opened
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // if was in main activity
            // re-launch main activity
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
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
        } else if (id == R.id.contacto) {
            startInfo();
        }  else if (id == R.id.carrito) {
            startSelectedProducts();
        }  else if (id == R.id.perfil) {
            startLogin();
        }  else if (id == R.id.preferencias) {
            startPreferences();
        }  else if (id == R.id.salir) {
            closeSession();
        }
        // close navigation bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
