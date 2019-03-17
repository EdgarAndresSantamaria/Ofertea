package com.example.proyecto_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class myBD extends SQLiteOpenHelper {

    /**
     * Constructor for the persistent data manager object
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public myBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * This method generates the intern DB in the first run of the App
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // generate users table
        db.execSQL("CREATE TABLE Usuarios ('Codigo' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,'User' TEXT,'Pass' TEXT,'ultItemCat' TEXT,'ultItemName' TEXT)");
    }

    /**
     * This method manages the versions of the DB
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no versions architecture yet
    }

    /**
     * This method allowss to register a new user
     * @param nombre
     * @param pass
     * @return
     */
    public int addUsr(String nombre, String pass){
        // access database in write mode
        SQLiteDatabase DB= getWritableDatabase();
        // generate new parametric content user
        ContentValues newUser = new ContentValues();
        newUser.put("User", nombre);
        newUser.put("Pass", pass);
        // insert new user into table users
        DB.insert("Usuarios", null, newUser);
        //close connection
        DB.close();
        // return Id for the user
        return logUsr(nombre,pass);
    }

    /**
     * This method allows to make persistent last favourite item
     * @param cod
     * @param ultItemCat
     * @param ultItemName
     */
    public void addLastItem(int cod, String ultItemCat, String ultItemName){
        // access database in write mode
        SQLiteDatabase bd= getWritableDatabase();
        // generate new parametric content for update user
        ContentValues update= new ContentValues();
        update.put("ultItemCat",ultItemCat);
        update.put("ultItemName",ultItemName);
        // set user code to modify
        String[] argumentos = new String[] {String.valueOf(cod)};
        bd.update("Estudiantes", update, "Codigo=?", argumentos);
        //close connection
        bd.close();
    }

    /**
     * This method logs the specified user
     * @param nombre
     * @param pass
     * @return
     */
    public int logUsr(String nombre, String pass){
        // open DB in write mode
        SQLiteDatabase bd= getWritableDatabase();
        // set parametric values
        String[] campos = new String[] {"Codigo"};
        String[] argumentos = new String[] {nombre,pass};
        // checks if user and pass matches
        // make the especified parametric query and retain the result on cursor
        Cursor cu = bd.query("Usuarios",campos,"User=? AND Pass=?",argumentos,null,null,null);

        // user not registered code
        int cod=-1;
        while(cu.moveToNext()){
            //if check pass and mail return user id
            cod = cu.getInt(0);
        }

        // checks if the user was already registered
        argumentos = new String[] {nombre};
        // make the specified parametric query and retain the result on cursor
        cu = bd.query("Usuarios",campos,"User=? ",argumentos,null,null,null);
        if(cu.getCount()>0){
            // if was logged return the specified code
            cod=-2;
        }

        // close query and DB
        cu.close();
        bd.close();
        // return code
        return cod;
    }

    /**
     * This method returns the information result of the last favourite
     * @param cod
     * @return
     */
    public Cursor getLastItem(int cod){
        SQLiteDatabase bd= getWritableDatabase();
        String[] campos = new String[] {"ultItemCat","ultItemName"};
        String[] argumentos = new String[] {String.valueOf(cod)};
        Cursor cu = bd.query("Usuarios",campos,"Codigo=?",argumentos,null,null,null);
        bd.close();
        return cu;
    }

}
