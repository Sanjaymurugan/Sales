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

        tableName=getIntent().getExtras().getString("tableName"); //Getting the table name from the previous activity

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
        month+=1; //Month has to be added one to get the exact month number

        //Changing the date to the format DD-MM-YYYY
        String one,two;
        if(day/10==0)
            one="0"+day;
        else
            one=""+day;
        if(month/10==0)
            two="0"+month;
        else
            two=""+month;
        to.setText(one+"-"+two+"-"+year);

        cal.add(Calendar.MONTH,-1);
        day=cal.get(Calendar.DAY_OF_MONTH);
        month=cal.get(Calendar.MONTH);
        year=cal.get(Calendar.YEAR);
        month+=1;

        if(day/10==0)
            one="0"+day;
        else
            one=""+day;
        if(month/10==0)
            two="0"+month;
        else
            two=""+month;
        from.setText(one+"-"+two+"-"+year);

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
                        String one,two;
                        i1+=1;
                        if(i2/10==0)
                            one="0"+i2;
                        else
                            one=i2+"";
                        if(i1/10==0)
                            two="0"+i1;
                        else
                            two=""+i1;
                        from.setText(one+"-"+two+"-"+i);
                        trigger();
                    }
                },day,month+1,year);
                String temp[]=from.getText().toString().split("-");
                int d=Integer.parseInt(temp[0]);
                int m=Integer.parseInt(temp[1])-1;
                int y=Integer.parseInt(temp[2]);
                pickerDialog.updateDate(y,m,d); //For displaying the selected date in the date picker
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
                        String one,two;
                        i1+=1;
                        if(i2/10==0)
                            one="0"+i2;
                        else
                            one=""+i2;
                        if(i1/10==0)
                            two="0"+i1;
                        else
                            two=""+i1;
                        to.setText(one+"-"+two+"-"+i);
                        trigger();
                    }
                },day,month+1,year);
                String temp[]=to.getText().toString().split("-");
                int d=Integer.parseInt(temp[0]);
                int m=Integer.parseInt(temp[1])-1;
                int y=Integer.parseInt(temp[2]);
                pickerDialog.updateDate(y,m,d); //For displaying the selected date in the date picker
                pickerDialog.show();
            }
        });

        trigger();
    }

    public void trigger(){ //List has to updated whenever the from and to dates are being changed
        filterListAdapter adapter=new filterListAdapter(Filter.this,db.filterResult(tableName,from.getText().toString(),to.getText().toString()));
        filterList.setAdapter(adapter);
        grandTotal.setText("₹"+db.getGrandTotal(tableName,from.getText().toString(),to.getText().toString()));
    }
}