package com.example.ricardofernandes.tohomeresponsavel.helper;

/**
 * Created by RicardoFernandes on 03/06/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tohomecrud";

    // Login table name
    private static final String TABLE_CLIENTE = "cliente";
    private static final String TABLE_RESPONSAVEL = "resp";
    private static final String TABLE_RESIDENCIA = "residencia";
    private static final String TABLE_ETAPAS = "etapa";


    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_NAME = "username";
    private static final String KEY_USER_PASSWORD = "password";

    private static final String KEY_RESP_ID = "id";
    private static final String KEY_RESP_NAME = "name";
    private static final String KEY_RESP_EMAIL = "email";
    private static final String KEY_RESP_USER_NAME = "username";
    private static final String KEY_RESP_USER_PASSWORD = "password";

    private static final String KEY_RESI_ID = "id";
    private static final String KEY_RESI_ENDERECO = "address";
    private static final String KEY_RESI_BAIRRO = "hood";
    private static final String KEY_RESI_CEP = "zip_code";
    private static final String KEY_RESI_CIDADE = "city";
    private static final String KEY_RESI_ESTADO = "state";
    private static final String KEY_RESI_DATA_INICIO = "begindate";
    private static final String KEY_RESI_DATA_FIM = "enddate";
    private static final String KEY_RESI_ID_CLIENTE = "idcliente";
    private static final String KEY_RESI_ID_RESPONSAVEL = "idresp";

    private static final String KEY_ETAPA_ID = "id";
    private static final String KEY_ETAPA_RESI_ID = "idresi";
    private static final String KEY_ETAPA_NAME = "name";
    private static final String KEY_ETAPA_DETAILS = "details";
    private static final String KEY_ETAPA_STATUS = "status";



    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_CLIENTE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  + KEY_NAME + " TEXT,"
                + KEY_USER_NAME + " TEXT," + KEY_USER_PASSWORD + " TEXT," +  KEY_EMAIL + " TEXT" +  ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_RESP_TABLE = "CREATE TABLE " + TABLE_RESPONSAVEL + "("
                + KEY_RESP_ID + " INTEGER PRIMARY KEY,"  + KEY_RESP_NAME + " TEXT,"
                + KEY_RESP_USER_NAME + " TEXT," + KEY_RESP_USER_PASSWORD + " TEXT," +  KEY_RESP_EMAIL + " TEXT" +  ")";
        db.execSQL(CREATE_RESP_TABLE);

        String CREATE_TABLE_RESIDENCIA = "CREATE TABLE " + TABLE_RESIDENCIA + "("
                + KEY_RESI_ID + " INTEGER PRIMARY KEY,"  + KEY_RESI_ENDERECO + " TEXT,"
                + KEY_RESI_BAIRRO + " TEXT," + KEY_RESI_CEP + " TEXT,"
                + KEY_RESI_CIDADE + " TEXT," + KEY_RESI_ESTADO + " TEXT," + KEY_RESI_DATA_INICIO + " TEXT,"
                + KEY_RESI_DATA_FIM + " TEXT," + KEY_RESI_ID_CLIENTE + " INTEGER," + KEY_RESI_ID_RESPONSAVEL +
                " INTEGER" +  ")";
        db.execSQL(CREATE_TABLE_RESIDENCIA);

        String CREATE_TABLE_ETAPAS = "CREATE TABLE " + TABLE_ETAPAS + "("
                + KEY_ETAPA_ID + " INTEGER PRIMARY KEY,"  + KEY_ETAPA_RESI_ID + " TEXT,"
                + KEY_ETAPA_NAME + " TEXT," + KEY_ETAPA_DETAILS + " TEXT,"
                + KEY_ETAPA_STATUS + " TEXT," +  ")";
        db.execSQL(CREATE_TABLE_ETAPAS);

        //AQUI PASSAMOS TODAS AS OUTRAS TABELAS QUE TEM NO BANCO

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTE);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESPONSAVEL);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESIDENCIA);



        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database PODEMOS USAR ESSE METODO PARA FAZER O UPDATE DAS ETAPAS
     * */
    public void addUser(String id, String name, String username,String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_NAME, name); // Name
        values.put(KEY_USER_NAME, username); // NOME DE USUÁRIO
        values.put(KEY_USER_PASSWORD, password); // SENHA
        values.put(KEY_EMAIL, email); // Email


        // Inserting Row
        long uid = db.insert(TABLE_CLIENTE, null, values);
       // values.put(KEY_ID, id); // Email

        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + uid);


    }

    public void addResp(String id, String name, String username,String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_RESP_NAME, name); // Name
        values.put(KEY_RESP_USER_NAME, username); // NOME DE USUÁRIO
        values.put(KEY_RESP_USER_PASSWORD, password); // SENHA
        values.put(KEY_RESP_EMAIL, email); // Email

        long uidresp = db.insert(TABLE_RESPONSAVEL, null, values);
        values.put(KEY_RESP_ID, id); // Email

        Log.d(TAG, "New user inserted into sqlite: " + uidresp);

    }

    public void addResidencia(String id, String address, String hood, String zip_code, String city,
                              String state, String begindate,String enddate,String idcliente, String idresp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_RESI_ID,id);
        values.put(KEY_RESI_ENDERECO,address);
        values.put (KEY_RESI_BAIRRO,hood);
        values.put (KEY_RESI_CEP,zip_code);
        values.put (KEY_RESI_CIDADE,city);
        values.put (KEY_RESI_ESTADO,state);
        values.put (KEY_RESI_DATA_INICIO,begindate);
        values.put (KEY_RESI_DATA_FIM,enddate);
        values.put (KEY_RESI_ID_CLIENTE,idcliente);
        values.put (KEY_RESI_ID_RESPONSAVEL,idresp);

        // Inserting Row
        long uidresi = db.insert(TABLE_RESIDENCIA, null, values);
        values.put(KEY_RESI_ID, id); // Email
        values.put(KEY_RESI_ID_RESPONSAVEL,idresp);

        db.close(); // Closing database connection

        Log.d(TAG, "New RESIDENCIA inserted into sqlite: " + uidresi + " and Responsável: " + idresp);

    }

    public void addEtapa(String id, String idresi, String name, String details, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ETAPA_ID,id);
        values.put(KEY_ETAPA_RESI_ID,idresi);
      //  values.put(KEY_ETAPA_NAME,name);
       // values.put(KEY_ETAPA_DETAILS,details);
      //  values.put(KEY_ETAPA_STATUS,status);

        // Inserting Row
        long uidetapa = db.insert(TABLE_ETAPAS, null, values);
        values.put(KEY_ETAPA_ID, id); // Email
        values.put(KEY_ETAPA_RESI_ID,idresi);

        db.close(); // Closing database connection

        Log.d(TAG, "New Etapa inserted into sqlite: " + uidetapa + " and ID DA ETAPA: " + id + " and ID DA RESIDENCIA: "+idresi);

    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_CLIENTE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("username", cursor.getString(2));
            user.put("password", cursor.getString(3));
            user.put("email", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetailsResp() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_RESPONSAVEL;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("username", cursor.getString(2));
            user.put("password", cursor.getString(3));
            user.put("email", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public HashMap<String, String> getUserDetailsResi() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_RESIDENCIA;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("address", cursor.getString(1));
            user.put("hood", cursor.getString(2));
            user.put("city", cursor.getString(3));
            user.put("state", cursor.getString(4));
            user.put("begindate", cursor.getString(5));
            user.put("enddate", cursor.getString(6));
            user.put("idcliente", cursor.getString(7));
            user.put("idresp", cursor.getString(8));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }
    public HashMap<String, String> getUserDetailsEtapa() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_ETAPAS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("idresi", cursor.getString(1));
            //user.put("name", cursor.getString(2));
            //user.put("details", cursor.getString(3));
           // user.put("status", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite ETAPAS: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CLIENTE, null, null);
     //   db.delete(TABLE_RESPONSAVEL, null, null);
        db.delete(TABLE_RESIDENCIA, null, null);
        db.delete(TABLE_ETAPAS, null, null);

        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
    public void deleteResi() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        //   db.delete(TABLE_RESPONSAVEL, null, null);
        db.delete(TABLE_RESIDENCIA, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
    public void deleteEtapas() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        //   db.delete(TABLE_RESPONSAVEL, null, null);
        db.delete(TABLE_ETAPAS, null, null);
        db.close();

        Log.d(TAG, "Tabela de Etapas excluída do SQLITE");
    }

}