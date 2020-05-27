package com.nak.sales;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database extends SQLiteOpenHelper {
    public database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ITEMS (ITEM TEXT UNIQUE NOT NULL);");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS SALES (SALESID INT PRIMARY KEY AUTOINCREMENT,SALESDATE DATE NOT NULL, ITEM TEXT NOT NULL, PRICE INT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
