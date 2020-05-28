package com.nak.sales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class database extends SQLiteOpenHelper {
    public database(Context context) {
        super(context, "sales", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ITEMS (ITEM TEXT UNIQUE NOT NULL);");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS SALES (SALESID INTEGER PRIMARY KEY AUTOINCREMENT,SALESDATE DATE NOT NULL, ITEM TEXT NOT NULL, PRICE INT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ITEMS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS SALES");
    }

    public void insertData(String date,String itemName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        int unique=1;
        ContentValues contentValues=new ContentValues();
        contentValues.put("SALESDATE",date);
        contentValues.put("ITEM",itemName);
        contentValues.put("PRICE",price);
        db.insert("SALES",null,contentValues);
        Cursor cursor =db.rawQuery("SELECT EXISTS (SELECT * FROM ITEMS WHERE ITEM='"+itemName+"')",null);
        if (cursor.moveToFirst())
            unique=cursor.getInt(0);
        if(unique==0){
            contentValues=new ContentValues();
            contentValues.put("ITEM",itemName);
            db.insert("ITEMS",null,contentValues);
        }
    }

    public ArrayList<dbPojo> getData(){
        ArrayList<dbPojo> arrayList = new ArrayList<>();
        dbPojo pojo=new dbPojo();
        SQLiteDatabase db=this.getWritableDatabase();
        return arrayList;
    }
}
