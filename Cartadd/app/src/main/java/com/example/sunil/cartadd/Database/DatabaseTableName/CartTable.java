package com.example.sunil.cartadd.Database.DatabaseTableName;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.sunil.cartadd.Database.Database;
import com.example.sunil.cartadd.Model.CartModel;
import com.example.sunil.cartadd.Model.ProductModel;
import com.example.sunil.cartadd.Singleton.PreferenceHelper;

import java.util.ArrayList;

public class CartTable {

    public static final String UserIDK = "UserIDkey";

    PreferenceHelper sp;

    Context mContext;
    Database mDB;

    public static final String TABLE_NAME_CART = "cart_table";

    public static final String COL_CART_ID = "CART_ID";
    public static final String COL_CART_QUANTITY = "CART_QTY";
    public static final String COL_CART_USERID = "CART_USERID";
    public static final String COL_CART_PRODID = "CART_PRODID";

    public static String CREATE_TABLE_CART = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CART +
            "(" + COL_CART_ID + " INTEGER PRIMARY KEY,"
            + COL_CART_QUANTITY + " INTEGER DEFAULT '1',"
            + COL_CART_USERID + " INTEGER,"
            + COL_CART_PRODID + " INTEGER,"
            + " FOREIGN KEY " + "(" + COL_CART_USERID + ")" + " REFERENCES " + UserTable.TABLE_NAME_USER + "(" + UserTable.COL_ID + ")"
            + " FOREIGN KEY " + "(" + COL_CART_PRODID + ")" + " REFERENCES " + ProductTable.TABLE_NAME_PROD + "(" + ProductTable.COL_PROD_ID + ")" + ")";

    public static String upgradeCart = "ALTER TABLE " + TABLE_NAME_CART + " ADD COLUMN NEW-COL TEXT";

    public CartTable(Context context) {
        this.mContext = context;
        mDB = Database.getmInstance(mContext);
    }

    //Add Cart data
    public boolean addtoCart(CartModel cmd) {

        boolean addCheck = false;

        try {

            SQLiteDatabase db = mDB.getWritableDatabase();
            db.beginTransaction();

            ContentValues value = new ContentValues();
            value.put(COL_CART_QUANTITY, cmd.getCartquantity());
            value.put(COL_CART_USERID, cmd.getUserid());
            value.put(COL_CART_PRODID, cmd.getProdid());

            long result = db.insertOrThrow(TABLE_NAME_CART, null, value);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            if (result == -1)
                addCheck = false;
            else
                addCheck = true;


        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Toast.makeText(mContext, "Product already added into the Cart", Toast.LENGTH_LONG).show();
        }

        return addCheck;
    }

    /*-----------------------------------*-*-*-----------------------------------*/
                      /*Update Cart Quantity for individual item*/

    public boolean qtyUpdate(CartModel cmd) {

        SQLiteDatabase db = mDB.getWritableDatabase();
        db.beginTransaction();

        //Ref 1

        ContentValues value = new ContentValues();
        value.put(COL_CART_QUANTITY, cmd.getCartquantity());
        //Log.d(tag, "" + cmd.getCartid());
        long result = db.update(TABLE_NAME_CART, value, COL_CART_ID + "=?", new String[]{String.valueOf(cmd.cartid)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        if (result == -1)
            return false;
        else {
            return true;
        }

    }


    public boolean cartItemdelete(int cartid) {

        SQLiteDatabase db = mDB.getWritableDatabase();
        db.beginTransaction();

        int result = db.delete(TABLE_NAME_CART, "CART_ID=?", new String[]{String.valueOf(cartid)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        if (result > 0) {
            return true;
        } else
            return false;
    }


    public Cursor getAllCartData() {

//        sp = mContext.getSharedPreferences(MyprefK, Context.MODE_PRIVATE);
//        ed = sp.edit();
        sp = PreferenceHelper.getmInstance(mContext);

        int useridSP = sp.getUserID(UserIDK, 0);

        SQLiteDatabase db = mDB.getReadableDatabase();
        db.beginTransaction();

        String cartJointQuery = "SELECT * FROM " + TABLE_NAME_CART
                + " JOIN " + UserTable.TABLE_NAME_USER
                + " ON " + TABLE_NAME_CART + "." + COL_CART_USERID + " = " + UserTable.TABLE_NAME_USER + "." + UserTable.COL_ID
                + " JOIN " + ProductTable.TABLE_NAME_PROD
                + " ON " + TABLE_NAME_CART + "." + COL_CART_PRODID + " = " + ProductTable.TABLE_NAME_PROD + "." + ProductTable.COL_PROD_ID
                + " WHERE " + UserTable.COL_ID + " =?";

        //Important step.At here we can use UserID in where clause for individual record
        Cursor cur = db.rawQuery(cartJointQuery, new String[]{String.valueOf(useridSP)});
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();

        return cur;
    }

    public ArrayList<CartModel> getCartData(/*int useridSP*/) {

        Cursor cur = getAllCartData();

        //M 4.1, Setter method
        ProductModel pmd;
        ArrayList<CartModel> cartlist = new ArrayList<>();

        if (cur != null) {
            while (cur.moveToNext()) {

                int productid = cur.getInt(cur.getColumnIndex(ProductTable.COL_PROD_ID));
                String cartProdname = cur.getString(cur.getColumnIndex(ProductTable.COL_PROD_NAME));
                int cartProdprize = cur.getInt(cur.getColumnIndex(ProductTable.COL_PROD_PRICE));
                int cartItemQty = cur.getInt(cur.getColumnIndex(COL_CART_QUANTITY));
                int cartid = cur.getInt(cur.getColumnIndex(COL_CART_ID));
                // int productid = cur.getInt(cur.getColumnIndex(COL_CART_PRODID));

                // M 4.2, Setter method

                //    Log.d(tag, cartProdname);

                pmd = new ProductModel(productid, cartProdname, cartProdprize);
                cartlist.add(new CartModel(pmd, cartItemQty, cartid));
            }
            cur.close();
        }
        return cartlist;
    }

    public int cartCount(int userId) {
        int count = 0;
        SQLiteDatabase db = mDB.getReadableDatabase();
        db.beginTransaction();

        String queryCount = "SELECT " + COL_CART_ID + " FROM " + TABLE_NAME_CART + " WHERE " + COL_CART_USERID + " =" + userId;

        Cursor cur = db.rawQuery(queryCount, null);

        count = cur.getCount();
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();

        return count;
    }

}