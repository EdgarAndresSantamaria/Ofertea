package com.example.proyecto_1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.proyecto_1.dummy.DummyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Keep track of the messaging task to ensure we can cancel it if requested.
     */
    private UserMessageTask mMessageTask = null;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device, land mode ...
     */
    // intern management flags
    private boolean mTwoPane;
    // favourite button to maintain consistency
    private  FloatingActionButton fab8;

    /**
     * This method displays the GUI associated to itemListActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retrieve style preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean style =  prefs.getBoolean("estilo",false);
        // manage style
        if(style){
            // if personalized
            setTheme(R.style.AppTheme1);
        }else{
            // if default
            setTheme(R.style.AppTheme);
        }
        // display GUI
        setContentView(R.layout.activity_item_list);
        // load item list content
        new DummyContent(this);
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // lnd mode.
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        // configure return button
        FloatingActionButton fab10 = findViewById(R.id.fab10);
        fab10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMain();
            }
        });

        // get favourite button
        fab8 =  findViewById(R.id.fab8);
        // configure behaviur of favourite button
        fab8.setOnClickListener(new View.OnClickListener() {
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
        fab8.hide();
        // construct the recycler view
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    /**
     * This method manages the back button action
     */
    @Override
    public void onBackPressed() {
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
     * This method sets up the recycler view
     * @param recyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
    }


    /**
     * This inner class manages the behaviour of the list fragment
     */
    private static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        // intern config values
        private final ItemListActivity mParentActivity;
        private final List<DummyContent.Item> mValues;
        private final boolean mTwoPane;
        // management of the view holder in two / one pane modes
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // retrieve clicked item
                DummyContent.Item item = (DummyContent.Item) view.getTag();
                if (mTwoPane) {
                    // if two pane manage fragment behaviour
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.company);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                    mParentActivity.fab8.show();

                } else {
                    // if one pane launch item detail aactivity
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.company);
                    context.startActivity(intent);
                }
            }
        };

        /**
         * Constructor of class SimpleItemRecyclerViewAdapter
         * @param parent
         * @param items
         * @param twoPane
         */
        public SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<DummyContent.Item> items,
                                      boolean twoPane) {
            // initialize inner config values
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        /**
         * This method starts the associated GUI to the viewholder in the fragment
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // display the GUI
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        /**
         * This method starts when an item was clicked
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            //set the values of selected items in the fragment
            holder.mIdView.setText(mValues.get(position).company);
            holder.mContentView.setText(mValues.get(position).offer);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * This methos return the number of items displayed
         * @return
         */
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * This sub intern class represents the container of item explanation in divide screen mode
         */
        private class ViewHolder extends RecyclerView.ViewHolder {
            // intern config values
            final TextView mIdView;
            final TextView mContentView;

            /**
             * Constructor of wiew holder that initialized intern values
             * @param view
             */
            public ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.id_text);
                mContentView =  view.findViewById(R.id.content);
            }
        }
    }
}
