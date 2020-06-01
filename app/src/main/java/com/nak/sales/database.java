package com.nak.sales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;


public class database extends SQLiteOpenHelper {
    Context context;
    public database(Context context) {
        super(context, "sales", null, 1);
        this.context=context;
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

    public void update(int id, String date, String itemName, int price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("SALESDATE",date);
        contentValues.put("ITEM",itemName);
        contentValues.put("PRICE",price);
        String whereClause="SALESID=?";
        String whereArgs[]={id+""};
        db.update("SALES",contentValues,whereClause,whereArgs);
    }

    public ArrayList<dbPojo> getData(){
        ArrayList<dbPojo> arrayList = new ArrayList<>();
        dbPojo pojo;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM SALES;",null);
        if(cursor.moveToFirst()){
            do{
                pojo=new dbPojo();
                pojo.setId(cursor.getInt(0));
                pojo.setDate(cursor.getString(1));
                pojo.setItemName(cursor.getString(2));
                pojo.setPrice(cursor.getInt(3));
                arrayList.add(pojo);
            }while(cursor.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<String> getItems(){
        ArrayList<String> arrayList=new ArrayList<>();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM ITEMS;",null);
        if(cursor.moveToFirst()){
            do{
                arrayList.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<dbPojo> filterResult(String from, String to){
        ArrayList<dbPojo> arrayList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
//        Cursor cursor=db.rawQuery("SELECT ITEM, SUM(PRICE) FROM SALES WHERE SALESDATE BETWEEN '"+from+"' AND '"+to+"';",null);
        Cursor cursor=db.rawQuery("SELECT SUM(PRICE) FROM SALES WHERE SALESDATE BETWEEN '"+from+"' AND '"+to+"';",null);
        if(cursor.moveToFirst()){
            do{
                dbPojo pojo=new dbPojo();
//                pojo.setItemName(cursor.getString(0));
                pojo.setItemName("null");
                pojo.setPrice(cursor.getInt(0));
                arrayList.add(pojo);
                Toast.makeText(context,cursor.getString(0)+"---->",Toast.LENGTH_LONG).show();
            }while(cursor.moveToNext());
        }
        return  arrayList;
    }
}
