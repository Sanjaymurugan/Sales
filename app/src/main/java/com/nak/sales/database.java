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
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ITEMS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS SALES");
    }

    public void insertData(String date,String itemName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        int unique=1;
        String convertedDate=convertDateFormat(date);
        ContentValues contentValues=new ContentValues();
        contentValues.put("SALESDATE",convertedDate);
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
        String convertedDate=convertDateFormat(date);
        ContentValues contentValues=new ContentValues();
        contentValues.put("SALESDATE",convertedDate);
        contentValues.put("ITEM",itemName);
        contentValues.put("PRICE",price);
        String whereClause="SALESID=?";
        String whereArgs[]={id+""};
        db.update("SALES",contentValues,whereClause,whereArgs);
    }

    public ArrayList<dbPojo> getData(){
        ArrayList<dbPojo> arrayList = new ArrayList<>();
        dbPojo pojo;
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        String curr_date=year+"-"+(month+1)+"-"+day;
        String from_date=year+"-"+(month-1)+"-"+day;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM SALES WHERE SALESDATE BETWEEN '"+from_date+"' AND '"+curr_date+"';",null);
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

    public ArrayList<dbPojo> filterResult(String from, String to){
        ArrayList<dbPojo> arrayList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String convertedFrom=convertDateFormat(from);
        String convertedTo=convertDateFormat(to);
        Cursor cursor=db.rawQuery("SELECT ITEM, SUM(PRICE) FROM SALES WHERE SALESDATE BETWEEN '"+convertedFrom+"' AND '"+convertedTo+"' GROUP BY ITEM;",null);
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

    public void delete(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM SALES WHERE SALESID="+id+";");
    }
}
