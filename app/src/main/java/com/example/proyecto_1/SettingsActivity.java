package com.example.proyecto_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Switch;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    //TODO change personalized colour palette

    // intern values to manage preference settings
    private Switch color;
    private Switch language;

    /**
     * This method creates the GUI asssociated to setting sctivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // stablish the preferece peersonalized theme
        setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_settings);
        // retrieve the GUI components to manage preferences
        color = (Switch) findViewById(R.id.switch1);
        language = (Switch) findViewById(R.id.switch2);
        // ensemble the lgic to return butto
        FloatingActionButton fab7 = (FloatingActionButton) findViewById(R.id.fab7);
        fab7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // return to main view
                startMain();
            }
        });
    }

    /**
     * This method manages the back button logic
     */
    @Override
    public void onBackPressed() {
        // return to main view
        startMain();
    }

    /**
     * This method starts the activity Main after checking preferences status
     */
    public void startMain(){
        // before returning
        // load new color prefs
        if (color.isChecked())
            setTheme(true);
        else
            setTheme(false);
        // load new language prefs
        if (language.isChecked())
            setLanguage(true);
        else
            setLanguage(false);
        // return to main activity
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method sets language preferences true = AppTheme1 , false = AppTheme
     */
    public void setTheme(Boolean style){
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // stablish a writer
        SharedPreferences.Editor editor = prefs.edit();
        // redefine style flag
        editor.putBoolean("estilo",style);
        editor.commit();
    }

    /**
     * This method updates the devices localization to maintain consistence with os calls display language
     * @param newLocal
     */
    public void updateLocalization(String newLocal){
        // change the localization of the device to maintain consistence in the OS routines language
        Locale nuevaloc = new Locale(newLocal);
        // stablish the new localization
        Locale.setDefault(nuevaloc);
        // retrieve config OS prefs
        Configuration config = new Configuration();
        // set the new localization
        config.locale = nuevaloc;
        // update localization
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * This method sets language preferences true = english , false = spanish
     */
    public void setLanguage(Boolean lang){
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // stablish a writer
        SharedPreferences.Editor editor = prefs.edit();
        // redefine language flag
        editor.putBoolean("ingles",lang);
        // set changes
        editor.commit();
        if(lang){
            // localization english
            updateLocalization("en");
        }else{
            // localization spanish
            updateLocalization("es");
        }

    }

}
