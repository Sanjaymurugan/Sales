package com.nak.sales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


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
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS PURCHASE (SALESID INTEGER PRIMARY KEY AUTOINCREMENT,SALESDATE DATE NOT NULL, ITEM TEXT NOT NULL, PRICE INT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ITEMS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS SALES");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS PURCHASE");
    }

    public void insertData(String tableName,String date,String itemName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        int unique=1;
        String convertedDate=convertDateFormat(date);
        ContentValues contentValues=new ContentValues();
        contentValues.put("SALESDATE",convertedDate);
        contentValues.put("ITEM",itemName);
        contentValues.put("PRICE",price);
        db.insert(tableName,null,contentValues);
        Cursor cursor =db.rawQuery("SELECT EXISTS (SELECT * FROM ITEMS WHERE ITEM='"+itemName+"')",null);
        if (cursor.moveToFirst())
            unique=cursor.getInt(0);
        if(unique==0){
            contentValues=new ContentValues();
            contentValues.put("ITEM",itemName);
            db.insert("ITEMS",null,contentValues);
        }
    }

    public void update(String tableName,int id, String date, String itemName, int price){
        SQLiteDatabase db=this.getWritableDatabase();
        String convertedDate=convertDateFormat(date);
        ContentValues contentValues=new ContentValues();
        contentValues.put("SALESDATE",convertedDate);
        contentValues.put("ITEM",itemName);
        contentValues.put("PRICE",price);
        String whereClause="SALESID=?";
        String whereArgs[]={id+""};
        db.update(tableName,contentValues,whereClause,whereArgs);
    }

    public ArrayList<dbPojo> getData(String tableName, String date){
        ArrayList<dbPojo> arrayList = new ArrayList<>();
        dbPojo pojo;

        String convertedOnDate=convertDateFormat(date);
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+tableName+" WHERE SALESDATE='"+convertedOnDate+"';",null);
        if(cursor.moveToFirst()){
            do{
                pojo=new dbPojo();
                pojo.setId(cursor.getInt(0));
                String convertedDate=convertDateFormat(cursor.getString(1));
                pojo.setDate(convertedDate);
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

    public ArrayList<dbPojo> filterResult(String tableName,String from, String to){
        ArrayList<dbPojo> arrayList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String convertedFrom=convertDateFormat(from);
        String convertedTo=convertDateFormat(to);
        Cursor cursor=db.rawQuery("SELECT ITEM, SUM(PRICE) FROM "+tableName+" WHERE SALESDATE BETWEEN '"+convertedFrom+"' AND '"+convertedTo+"' GROUP BY ITEM ORDER BY ITEM ASC;",null);
        if(cursor.moveToFirst()){
            do{
                dbPojo pojo=new dbPojo();
                pojo.setItemName(cursor.getString(0));
                pojo.setPrice(cursor.getInt(1));
                arrayList.add(pojo);
            }while(cursor.moveToNext());
        }
        return  arrayList;
    }

    public String convertDateFormat(String date){
        String arr[]=date.split("-");
        String finalDate=arr[2]+"-"+arr[1]+"-"+arr[0];
        return finalDate;
    }

    public void delete(String tableName,int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tableName+" WHERE SALESID="+id+";");
    }

    public int getGrandTotal(String tableName,String from , String to){
        int total=0;
        SQLiteDatabase db=this.getWritableDatabase();
        String convertedFrom=convertDateFormat(from);
        String convertedTo=convertDateFormat(to);
        Cursor cursor=db.rawQuery("SELECT SUM(PRICE) FROM "+tableName+" WHERE SALESDATE BETWEEN '"+convertedFrom+"' AND '"+convertedTo+"';", null);
        if(cursor.moveToFirst()){
            total=cursor.getInt(0);
        }
        return total;
    }

    public int getTotalOnDate(String tableName,String date){
        int total=0;
        SQLiteDatabase db=this.getWritableDatabase();
        String convertedDate=convertDateFormat(date);
        Cursor cursor=db.rawQuery("SELECT SUM(PRICE) FROM "+tableName+" WHERE SALESDATE='"+convertedDate+"';",null);
        if(cursor.moveToFirst()){
            total=cursor.getInt(0);
        }
        return total;
    }
}
