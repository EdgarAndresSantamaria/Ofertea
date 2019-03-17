package com.example.proyecto_1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.proyecto_1.dummy.DummyContent;

import java.util.List;

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
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device, land mode ...
     */
    // intern management flags
    private boolean mTwoPane;
    private boolean language;
    // favourite button to maintain consistency
    private  FloatingActionButton fab8;

    /**
     * This method displays the GUI associated to itemListActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO parametrize the button fab8
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
        FloatingActionButton fab10 = (FloatingActionButton) findViewById(R.id.fab10);
        fab10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMain();
            }
        });
        // retrieve language and user status
        language =  prefs.getBoolean("ingles",false);
        String user =  prefs.getString("user","invitado");
        if(!user.equals("invitado")){
            // if not loged
            fab8 = (FloatingActionButton) findViewById(R.id.fab8);
            // configure behaviur of favourite button
            fab8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(language){
                        // if english
                        lanzarNotificacion("You added an article to favourites.", "we ll take care about it for future search");
                    }else {
                        // if spanish
                        lanzarNotificacion("Has añadido un articulo a favoritos.", "tendremos en cuenta esta información para posteriores búsquedas");
                    }
                }
            });
        }else{
            // if  loged
            fab8 = (FloatingActionButton) findViewById(R.id.fab8);
            // configure behaviur of favourite button
            fab8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(language){
                        // if english
                        lanzarNotificacion("only avaliable for registered users", "go to login / register section on menu");
                    }else {
                        // ifspanish
                        lanzarNotificacion("solo disponible para usuarios registrados", "ve al menú y registrate");
                    }
                }
            });
        }
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
     * This method launches a notification
     * @param text
     * @param subText
     */
    public void lanzarNotificacion(String text,String subText){
        // get Notification management object
        NotificationManager mnager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // retrieve the notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "IdCanal");
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            // creates the notification channel
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
            // configure notification
            builder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle("info")
                    .setContentText(text)
                    .setSubText(subText)
                    .setVibrate(new long[]{0, 1000, 500, 1000}).setAutoCancel(true);
            // display notification in the phone
            mnager.createNotificationChannel(elCanal);
            mnager.notify(1, builder.build());
        }
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
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
