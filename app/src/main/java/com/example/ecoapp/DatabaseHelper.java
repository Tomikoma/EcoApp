package com.example.ecoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mydatabase.db";
    public static final String TABLE_NAME = "transaction_table";
    public static final String ID = "id";
    public static final String DATE = "transactionDate";
    public static final String VALUE = "value";
    public static final String TYPE = "transactionType";
    public static final String PARTNER = "transactionPartner";


//

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " TEXT, " +
                VALUE + " INTEGER DEFAULT 0,  " +
                TYPE + " TEXT, " +
                PARTNER + " TEXT" +
                ")");

        Log.d("DATABASEHELPER","Database created\nVERSION: " + db.getVersion());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String date, int value, String type, String partner) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VALUE,value);
        contentValues.put(TYPE,type);
        contentValues.put(DATE,date);
        contentValues.put(PARTNER,partner);
        long result = db.insert(TABLE_NAME,null,contentValues);
        db.close();
        if(result == -1){
            return false;
        } else {
            return true;
        }
    }

    Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + DATE + ", " + VALUE + ", " + TYPE + ", " + PARTNER + " FROM " + TABLE_NAME,null );
    }

    Cursor getLastestTransaction(String trtype){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + DATE + ", " + VALUE + ", " + PARTNER + " FROM " + TABLE_NAME + " WHERE " + TYPE + "='" + trtype + "' ORDER BY " + DATE + " DESC LIMIT 1 " ,null);
    }

    int getAmountOfTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME,null);
        res.moveToNext();
        return res.getInt(0);
    }

    int getTotalValueOfType(String trtype){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT SUM(" + VALUE +") FROM " + TABLE_NAME + " WHERE " + TYPE + "='" + trtype + "'",null );
        if(res.getCount() == 0)
            return -1;
        else {
            res.moveToNext();
            return res.getInt(0);
        }
    }

    int getAvgValueOfType(String trtype){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT AVG(" + VALUE +") FROM " + TABLE_NAME + " WHERE " + TYPE + "='" + trtype + "'",null );
        if(res.getCount() == 0)
            return -1;
        else {
            res.moveToNext();
            return res.getInt(0);
        }
    }
}
