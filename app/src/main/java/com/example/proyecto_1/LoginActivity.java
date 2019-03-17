package com.example.proyecto_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     *  Authentication BD subsystem
     */
    private myBD bd = new myBD(this,"BD",null,1);

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // GUI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /**
     * This method creates the GUI and ensembles the logic control for diferent views
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retrieve user preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // check style preferences
        Boolean style =  prefs.getBoolean("estilo",false);
        // theme selection
        if(style){
            // set presonalized style
            setTheme(R.style.AppTheme1);
        }else{
            // set default style
            setTheme(R.style.AppTheme);
        }
        // display GUI
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // Set the password input
        mPasswordView = (EditText) findViewById(R.id.password);
        // ensemble the login logit to singInButton
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // try to login
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // if not initialized
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        // start control logic
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            // e-mail slot was empty
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            // e-mail slot was wrong
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // if something was wrong cancel tryout
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(email, password, bd);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * This method manages the back button use
     */
    @Override
    public void onBackPressed() {
        // return to main view
        returnMain();
    }

    /**
     * This method calls main activity and ends login activity
     */
    public void returnMain(){
        // return to main view 'MainActivity'
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method checks if the e-mail form has @
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * This method checks wether password length is at least 8
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 8;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        // values to intern control login attempt
        private final String mEmail;
        private final String mPassword;
        private myBD bd;
        public Boolean status ;

        /**
         * Constructor for asynchronous login task
         * @param email
         * @param password
         * @param labd
         */
        public UserLoginTask(String email, String password, myBD labd) {
            // set up control values
            mEmail = email;
            mPassword = password;
            bd = labd;
        }

        /**
         * This method launches the verification / register task in background
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            if(bd.logUsr(mEmail,mPassword) == -1){
                //if user doesnt exist
                bd.addUsr(mEmail,mPassword);
            }else  if(bd.logUsr(mEmail,mPassword) == -2){
                // if user exists but password dont match
                return false;
            }
            return true;
        }

        /**
         * This method displays an error if login attemp failed
         * @param success
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                //if success
                // update user preferences with the loged user
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user", mEmail);
                editor.commit();
                //return to main view
                returnMain();
                finish();
            } else {

                // if something failed
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        /**
         * This method manages the situation that the user stops the login attempt
         */
        @Override
        protected void onCancelled() {
            // destroy login tadk object
            mAuthTask = null;
        }
    }
}

