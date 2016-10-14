package com.spacetime.mesh.wordusion.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by mehul on 1/17/16.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    //private static final String KEY_NAME = "name";
    private static final String KEY_USERNAME = "username";
    private static final String SCORE_WON = "won";
    private static final String SCORE_LOST = "lost";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("

                + KEY_USERNAME + " TEXT UNIQUE," + SCORE_WON + " INTEGER, " + SCORE_LOST + " INTEGER" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String username, int won, int lost){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(SCORE_WON, won);
        values.put(SCORE_LOST, lost);
        Log.d(TAG, values.toString());

        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
        Log.d("here in the database", username);

        Log.d(TAG, "New username inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("username", cursor.getString(0));
            user.put("won", cursor.getString(1));
            user.put("lost", cursor.getString(2));
        }

        Log.d("I'm the fetcher", "FUCK YOU MAN!");
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void updateUser(String username, int won, int lost){
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues values = new ContentValues();
        //values.put(KEY_USERNAME, username);
        //values.put(SCORE_WON, lost);
        //values.put(SCORE_LOST, won);
        db.execSQL("UPDATE " + TABLE_USER + " SET " + SCORE_WON + " = " + won + " , " + SCORE_LOST
                    + " = " + lost + " WHERE username = '" + username + "'");
        //db.update(TABLE_USER, values, null, null);
        //Log.d(TAG, values.toString());

        db.close(); // Closing database connection
        Log.d("here in the database", username);

        Log.d(TAG, "Table updated");

    }

}

