package com.example.sunil.cartadd.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sunil.cartadd.Database.DatabaseTableName.CartTable;
import com.example.sunil.cartadd.Database.DatabaseTableName.ProductTable;
import com.example.sunil.cartadd.Database.DatabaseTableName.UserTable;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "User.db";

    public static final int DATABASE_VERSION = 1;

    public static Database mInstance = null;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static Database getmInstance(Context context) {
        if (mInstance == null)
            mInstance = new Database(context);

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(UserTable.CREATE_TABLE_USER);
        db.execSQL(ProductTable.CREATE_TABLE_PROD);
        db.execSQL(CartTable.CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion == oldVersion + 1) {
            db.execSQL(UserTable.upgradeUser);
            db.execSQL(ProductTable.upgradeProduct);
            db.execSQL(CartTable.upgradeCart);
        }

    }


}