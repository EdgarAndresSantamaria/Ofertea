package com.example.proyecto_1.dummy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.preference.PreferenceManager;
import android.util.JsonReader;
import com.example.proyecto_1.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces
 */
public class DummyContent {


    /**
     * Static list of items
     */
    public static  List ITEMS;
    /**
     * A map of sample  items, by ID.
     */
    public static final Map<String, Item > ITEM_MAP = new HashMap<String, Item>();

    /**
     * Constructor to initialize DummyContent from a JSON type
     * @param app
     */
    public DummyContent(Context app){
        // initialize items static
        ITEMS = new ArrayList<Item>();
        // initialize items for charge
        List<Item> items = null;
        try {
            // charge items from an JSON file
            items = readJsonStream(app);
        } catch (IOException e) {
            // threat exception
            e.printStackTrace();
        }
        // atach loaded items to static list
        Iterator it = items.iterator();
        while (it.hasNext()) {
            // add actual item
            Item act = (Item) it.next();
            ITEMS.add(act);
            addItem(act);

        }

    }

    /**
     * Method to add an item to the static list
     * @param item
     */
    private static void addItem(Item item) {
        // add item
        ITEM_MAP.put(item.company, item);
    }

    /**
     * This Method is the upped layer wich manages the load of a JSON file
     * @param app
     * @return
     * @throws IOException
     */
    public List<Item> readJsonStream(Context app) throws IOException {
        // new JsonReader instance
        Resources r = app.getResources();
        // check language preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);
        Boolean language =  prefs.getBoolean("ingles",false);
        // instantiate th inputStream
        InputStream fraw;
        if(language){
            // english path input
            fraw = r.openRawResource(R.raw.catalogo_en);
        }else{
            // spanish path input
            fraw = r.openRawResource(R.raw.catalogo_es);
        }
        // JSON parser object 'reader' instantiate
        JsonReader reader = new JsonReader(new InputStreamReader(fraw, "UTF-8"));
        try {
            // load Offer Array with reader
            return readOfferArray(reader);
        } finally {
            reader.close();
        }

    }

    /**
     * This method ensembles the array composed by all offers in the JSON file
     * @param reader
     * @return
     * @throws IOException
     */
    private List readOfferArray(JsonReader reader) throws IOException {
        // temp list
        ArrayList offers = new ArrayList();

        // start reading
        reader.beginArray();
        while (reader.hasNext()) {
            // read next object
            offers.add(readOffer(reader));
        }
        // close parser
        reader.endArray();
        // return temp Array
        return offers;
    }

    /**
     * This method reads the next object from the 'reader' JSON parser
     * @param reader
     * @return
     * @throws IOException
     */
    private Item readOffer(JsonReader reader) throws IOException {
        // initialize content
        String company = null;
        String offer = null;
        String description = null;

        // read next object
        reader.beginObject();
        while (reader.hasNext()) {
            // initialize content with object values
            String name = reader.nextName();
            switch (name) {
                case "compañia":
                    company = reader.nextString();
                    break;
                case "oferta":
                    offer = reader.nextString();
                    break;
                case "descripcion":
                    description = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        // close object
        reader.endObject();
        // return the offer item
        return new Item(company, offer, description);
    }

    /**
     * A dummy item representing a piece of content intern object class.
     */
    public class Item {
        // content accesible vars
        public  String company;
        public  String offer;
        public  String description;

        /**
         * Constructor for dummy items
         * @param id
         * @param content
         * @param details
         */
        public Item(String id, String content, String details) {
            // initialize content values
            company = id;
            offer = content;
            description = details;
        }

        /**
         * This method returns the offer value
         * @return
         */
        @Override
        public String toString() {
            return offer;
        }
    }
}
