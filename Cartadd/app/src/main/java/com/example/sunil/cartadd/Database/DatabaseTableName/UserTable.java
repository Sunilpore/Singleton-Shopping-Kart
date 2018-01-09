package com.example.sunil.cartadd.Database.DatabaseTableName;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.sunil.cartadd.Database.Database;
import com.example.sunil.cartadd.Model.UserModel;

public class UserTable {

    Context mContext;
    Database mDB;

    public static final String TABLE_NAME_USER = "user_table";

    public static final String COL_ID = "USER_ID";
    public static final String COL_FULLNAME = "FULLNAME";
    public static final String COL_USERNAME = "NAME";
    public static final String COL_PASS = "PASS";


    public static String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER +
            "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_FULLNAME + " TEXT,"
            + COL_USERNAME + " TEXT UNIQUE,"
            + COL_PASS + " TEXT" + ")";

    public static String upgradeUser = "ALTER TABLE " + TABLE_NAME_USER + " ADD COLUMN NEW-COL TEXT";

    public UserTable(Context context) {
        this.mContext = context;
        mDB = Database.getmInstance(mContext);
    }

    //Add data for UserModel For signin
    public boolean addUserData(UserModel umd) {
        boolean addCheck = false;

        try {
            SQLiteDatabase db = mDB.getWritableDatabase();
            db.beginTransaction();

            ContentValues value = new ContentValues();
            value.put(COL_FULLNAME, umd.getFullname());
            value.put(COL_USERNAME, umd.getUname());
            value.put(COL_PASS, umd.getPass());

            long result = db.insertOrThrow(TABLE_NAME_USER, null, value);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            if (result == -1)
                addCheck = false;
            else
                addCheck = true;


        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Toast.makeText(mContext, "User name Already Exist", Toast.LENGTH_LONG).show();
        }
        return addCheck;
    }


    /*-----------------------------------*-*-*-----------------------------------*/
                       /*Verify user credential at Login Page*/

    public Cursor getAllUserData(String username, String password) {

        SQLiteDatabase db = mDB.getReadableDatabase();
        db.beginTransaction();

        String loginQuery = "SELECT " + COL_ID + " FROM " + TABLE_NAME_USER + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASS + " = ?";

        Cursor cur = db.rawQuery(loginQuery, new String[]{username, password});
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();

        return cur;
    }


    //This method is call for view Login user's SignUp Record on Login page
    public Cursor getAllUserData() {

        SQLiteDatabase db = mDB.getReadableDatabase();
        db.beginTransaction();

        Cursor cur = db.rawQuery("select * from " + TABLE_NAME_USER, null);
        db.setTransactionSuccessful();
        db.endTransaction();

        return cur;
    }

}