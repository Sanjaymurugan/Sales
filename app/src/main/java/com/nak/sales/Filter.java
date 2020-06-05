package com.nak.sales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class Filter extends AppCompatActivity {
    CardView fromCV, toCV;
    DatePickerDialog pickerDialog;
    TextView from,to,grandTotal;
    ListView filterList;
    database db;
    String tableName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        tableName=getIntent().getExtras().getString("tableName");

        fromCV=(CardView)findViewById(R.id.fromCV);
        toCV=(CardView)findViewById(R.id.toCV);
        from=(TextView)findViewById(R.id.from);
        to=(TextView)findViewById(R.id.to);
        grandTotal=(TextView)findViewById(R.id.total);
        filterList=(ListView)findViewById(R.id.filterList);
        db=new database(Filter.this);

        Calendar cal=Calendar.getInstance();
        int day=cal.get(Calendar.DAY_OF_MONTH);
        int month=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);
        to.setText(day+"-"+(month+1)+"-"+year);

        cal.add(Calendar.MONTH,-1);
        day=cal.get(Calendar.DAY_OF_MONTH);
        month=cal.get(Calendar.MONTH);
        year=cal.get(Calendar.YEAR);
        from.setText(day+"-"+(month+1)+"-"+year);

        fromCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                pickerDialog=new DatePickerDialog(Filter.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        from.setText(i2+"-"+(i1+1)+"-"+i);
                        trigger();
                    }
                },day,month+1,year);
                pickerDialog.show();
            }
        });

        toCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                pickerDialog=new DatePickerDialog(Filter.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        to.setText(i2+"-"+(i1+1)+"-"+i);
                        trigger();
                    }
                },day,month+1,year);
                pickerDialog.show();
            }
        });

        trigger();
    }

    public void trigger(){
        homeListAdapter adapter=new homeListAdapter(Filter.this,db.filterResult(tableName,from.getText().toString(),to.getText().toString()));
        filterList.setAdapter(adapter);
        grandTotal.setText("₹"+db.getGrandTotal(tableName,from.getText().toString(),to.getText().toString()));
    }
}