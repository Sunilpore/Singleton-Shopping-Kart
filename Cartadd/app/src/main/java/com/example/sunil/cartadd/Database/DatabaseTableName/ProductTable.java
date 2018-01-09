package com.example.sunil.cartadd.Database.DatabaseTableName;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sunil.cartadd.Database.Database;
import com.example.sunil.cartadd.Model.CartModel;
import com.example.sunil.cartadd.Model.ProductModel;
import com.example.sunil.cartadd.Singleton.PreferenceHelper;

import java.util.ArrayList;

public class ProductTable {

    public static final String UserIDK = "UserIDkey";

    PreferenceHelper sp;

    Context mContext;
    Database mDB;

    public static final String TABLE_NAME_PROD = "product_table";

    public static final String COL_PROD_ID = "PRODUCT_ID";
    public static final String COL_PROD_NAME = "PRODUCT_NAME";
    public static final String COL_PROD_PRICE = "PRODUCT_PRICE";
    public static final String COL_PROD_CATEGORY = "PRODUCT_CATEGORY";

    public static String CREATE_TABLE_PROD = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PROD +
            "(" + COL_PROD_ID + " INTEGER PRIMARY KEY,"
            + COL_PROD_NAME + " TEXT,"
            + COL_PROD_PRICE + " INT,"
            + COL_PROD_CATEGORY + " TEXT" + ")";

    public static String upgradeProduct = "ALTER TABLE " + TABLE_NAME_PROD + " ADD COLUMN NEW-COL TEXT";

    public ProductTable(Context context) {
        this.mContext = context;
        mDB = Database.getmInstance(mContext);
    }


    //Add Product data
    public boolean addProductData(ProductModel pmd) {

        SQLiteDatabase db = mDB.getWritableDatabase();
        db.beginTransaction();

        ContentValues value = new ContentValues();
        value.put(COL_PROD_NAME, pmd.getProdname());
        value.put(COL_PROD_PRICE, pmd.getProdprice());
        value.put(COL_PROD_CATEGORY, pmd.getProdcat());

        long result = db.insert(TABLE_NAME_PROD, null, value);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    /*-----------------------------------*-*-*-----------------------------------*/
                                    /*Product List using Fragment*/

    public Cursor getAllelecdProductData() {

//        sp = mContext.getSharedPreferences(MyprefK, Context.MODE_PRIVATE);
//        ed = sp.edit();
        sp = PreferenceHelper.getmInstance(mContext);

        int useridSP = sp.getUserID(UserIDK, 0);

        SQLiteDatabase db = mDB.getReadableDatabase();
        db.beginTransaction();
        // Cursor cur = db.rawQuery("select * from " + TABLE_NAME_PROD + " WHERE " + COL_PROD_CATEGORY + " =" + "'ELECTRONICS'", null);

        //Left JOIN and INNER QUERY use for Disable ADD button in Homepage. It disable ADD Button to those item which already present in CartView
        Cursor cur = db.rawQuery("select * from " + TABLE_NAME_PROD + " LEFT JOIN "
                + "(select * from " + CartTable.TABLE_NAME_CART + " WHERE " + CartTable.COL_CART_USERID + " = " + useridSP + ")"
                + " ON " + COL_PROD_ID + " = " + CartTable.COL_CART_PRODID + " WHERE " + COL_PROD_CATEGORY + " = " + "'ELECTRONICS'", null);

        db.setTransactionSuccessful();
        db.endTransaction();

        return cur;
    }

    public ArrayList<CartModel> getElectronicsProduct() {
        SQLiteDatabase db = mDB.getWritableDatabase();
        Cursor cur = getAllelecdProductData();

        ArrayList<CartModel> mlist = new ArrayList<>();
        ProductModel pmd;

        if (cur != null) {

            cur.moveToFirst();
            do {
                int productid = cur.getInt(cur.getColumnIndex(COL_PROD_ID));
                String prodname = cur.getString(cur.getColumnIndex(COL_PROD_NAME));
                int prodprice = cur.getInt(cur.getColumnIndex(COL_PROD_PRICE));
                int cartId = cur.getInt(cur.getColumnIndex(CartTable.COL_CART_ID));
                int cartProdId = cur.getInt(cur.getColumnIndex(CartTable.COL_CART_PRODID));

                /*String checkAdded=cur.getString(cur.getColumnIndex(COL_CART_ID));
                boolean bt;
                if(checkAdded==null)
                    bt=false;
                else
                    bt=true;*/
                //Ref 2


                pmd = new ProductModel(productid, prodname, prodprice);
                mlist.add(new CartModel(cartId, cartProdId, pmd));
                // mlist.add(new ProductModel(productid, prodname, prodprice,bt));
            } while (cur.moveToNext());
        }
        cur.close();
        return mlist;
    }


    public Cursor getAllgroceryProductData() {

//        sp = mContext.getSharedPreferences(MyprefK, Context.MODE_PRIVATE);
//        ed = sp.edit();
        sp = PreferenceHelper.getmInstance(mContext);

        int useridSP = sp.getUserID(UserIDK, 0);

        SQLiteDatabase db = mDB.getReadableDatabase();
        db.beginTransaction();
        // Cursor cur = db.rawQuery("select * from " + TABLE_NAME_PROD + " WHERE " + COL_PROD_CATEGORY + " =" + "'GROCERY'", null);

        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NAME_PROD + " LEFT JOIN "
                + "(SELECT * FROM " + CartTable.TABLE_NAME_CART + " WHERE " + CartTable.COL_CART_USERID + " = " + useridSP + ")"
                + " ON " + COL_PROD_ID + " = " + CartTable.COL_CART_PRODID + " WHERE " + COL_PROD_CATEGORY + " = " + "'GROCERY'", null);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();

        return cur;
    }


    public ArrayList<CartModel> getGroceryProduct() {
        SQLiteDatabase db = mDB.getWritableDatabase();
        Cursor cur = getAllgroceryProductData();

        ArrayList<CartModel> mlist = new ArrayList<>();
        ProductModel pmd;

        if (cur != null) {

            cur.moveToFirst();
            do {
                int productid = cur.getInt(cur.getColumnIndex(COL_PROD_ID));
                String prodname = cur.getString(cur.getColumnIndex(COL_PROD_NAME));
                int prodprice = cur.getInt(cur.getColumnIndex(COL_PROD_PRICE));
                int cartId = cur.getInt(cur.getColumnIndex(CartTable.COL_CART_ID));
                int cartProdId = cur.getInt(cur.getColumnIndex(CartTable.COL_CART_PRODID));

                /*String checkAdded=cur.getString(cur.getColumnIndex(COL_CART_PRODID));
                boolean bt;
                if (checkAdded==null)
                    bt=false;
                else
                    bt=true;*/

                pmd = new ProductModel(productid, prodname, prodprice);
                mlist.add(new CartModel(cartId, cartProdId, pmd));
                // mlist.add(new ProductModel(productid, prodname, prodprice,bt));
            } while (cur.moveToNext());
        }
        cur.close();
        return mlist;
    }


    public Cursor getAllsportsProductData() {

//        sp = mContext.getSharedPreferences(MyprefK, Context.MODE_PRIVATE);
//        ed = sp.edit();
        sp = PreferenceHelper.getmInstance(mContext);

        int useridSP = sp.getUserID(UserIDK, 0);

        SQLiteDatabase db = mDB.getReadableDatabase();
        db.beginTransaction();
        // Cursor cur = db.rawQuery("select * from " + TABLE_NAME_PROD + " WHERE " + COL_PROD_CATEGORY + " =" + "'SPORTS'", null);

        /*Cursor cur3 = db.rawQuery("select * from " + TABLE_NAME_PROD + " left join " + TABLE_NAME_CART
                + " ON " + COL_CART_PRODID + " = " + COL_PROD_ID + " WHERE " + COL_PROD_CATEGORY + " =" + "'SPORTS'", null);*/

        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NAME_PROD + " LEFT JOIN "
                + "(SELECT * FROM " + CartTable.TABLE_NAME_CART + " WHERE " + CartTable.COL_CART_USERID + " = " + useridSP + ")"
                + " ON " + COL_PROD_ID + " = " + CartTable.COL_CART_PRODID + " WHERE " + COL_PROD_CATEGORY + " = " + "'SPORTS'", null);

        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();

        return cur;
    }


    public ArrayList<CartModel> getSportsProduct() {
        SQLiteDatabase db = mDB.getWritableDatabase();
        Cursor cur = getAllsportsProductData();

        ArrayList<CartModel> mlist = new ArrayList<>();
        ProductModel pmd;

        if (cur != null) {

            cur.moveToFirst();
            do {
                int productid = cur.getInt(cur.getColumnIndex(COL_PROD_ID));
                String prodname = cur.getString(cur.getColumnIndex(COL_PROD_NAME));
                int prodprice = cur.getInt(cur.getColumnIndex(COL_PROD_PRICE));
                int cartId = cur.getInt(cur.getColumnIndex(CartTable.COL_CART_ID));
                int cartProdId = cur.getInt(cur.getColumnIndex(CartTable.COL_CART_PRODID));

                /*String checkAdded=cur.getString(cur.getColumnIndex(COL_CART_PRODID));
                boolean bt;
                if (checkAdded==null)
                    bt=false;
                else
                    bt=true;*/


                pmd = new ProductModel(productid, prodname, prodprice);
                mlist.add(new CartModel(cartId, cartProdId, pmd));
                //  mlist.add(new ProductModel(productid, prodname, prodprice,bt));
            } while (cur.moveToNext());
        }
        cur.close();
        return mlist;
    }


}