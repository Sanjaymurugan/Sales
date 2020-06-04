package com.nak.sales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView date;
    CardView cv;
    DatePickerDialog pickerDialog;
    TextInputEditText itemPrice;
    AutoCompleteTextView itemName;
    Button add,filter,cancel;
    ListView homeList;
    database db;
    homeListAdapter adapter;
    ArrayList<dbPojo> arrayList;
    public static int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date=(TextView)findViewById(R.id.date);
        cv=(CardView)findViewById(R.id.dateCV);
        itemName=(AutoCompleteTextView)findViewById(R.id.itemName);
        itemPrice=(TextInputEditText)findViewById(R.id.price);
        add=(Button)findViewById(R.id.add);
        filter=(Button)findViewById(R.id.filter);
        cancel=(Button)findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        homeList=(ListView)findViewById(R.id.salesList);

        db=new database(MainActivity.this);
        adapter=new homeListAdapter(MainActivity.this,db.getData());
        homeList.setAdapter(adapter);
        arrayList=db.getData();
        setDate();

        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,db.getItems());
        itemName.setThreshold(1);
        itemName.setAdapter(arrayAdapter);

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                pickerDialog=new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        date.setText(i2+"-"+(i1+1)+"-"+i);
                    }
                },day,month+1,year);
                pickerDialog.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(itemName.getText())&&!TextUtils.isEmpty(itemPrice.getText())) {
                    if(add.getText().equals("Edit"))
                        db.update(id,date.getText().toString(),itemName.getText().toString(),Integer.parseInt(itemPrice.getText().toString()));
                    else
                        db.insertData(date.getText().toString(), itemName.getText().toString(), Integer.parseInt(itemPrice.getText().toString()));
                    itemName.setText("");
                    itemPrice.setText("");
                    trigger();
                    cancel.setVisibility(View.GONE);
                }
                else if(TextUtils.isEmpty(itemName.getText()))
                    itemName.setError("Please Enter the Item name");
                else if(TextUtils.isEmpty(itemPrice.getText()))
                    itemPrice.setError("Please Enter the Price");
            }
        });

        homeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                arrayList=db.getData();
                dbPojo pojo=arrayList.get(i);
                id=pojo.getId();
                itemName.setText(pojo.getItemName());
                itemPrice.setText(pojo.getPrice()+"");
                date.setText(pojo.getDate());
                add.setText("Edit");
                filter.setText("Delete");
                cancel.setVisibility(View.VISIBLE);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filter.getText().equals("View Sales"))
                    startActivity(new Intent(MainActivity.this,Filter.class));
                else{
                    db.delete(id);
                    itemName.setText("");
                    itemPrice.setText("");
                    setDate();
                    Toast.makeText(MainActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                    adapter = new homeListAdapter(MainActivity.this, db.getData());
                    homeList.setAdapter(adapter);
                    add.setText("Add");
                    filter.setText("View Sales");
                    cancel.setVisibility(View.GONE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate();
                add.setText("Add");
                filter.setText("View Sales");
                itemName.setText("");
                itemPrice.setText("");
                cancel.setVisibility(View.GONE);
            }
        });
    }

    public void setDate(){
        Calendar cal=Calendar.getInstance();
        int day=cal.get(Calendar.DAY_OF_MONTH);
        int month=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);
        date.setText(day+"-"+(month+1)+"-"+year);
    }

    public void trigger(){
        setDate();
        Toast.makeText(MainActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
        adapter = new homeListAdapter(MainActivity.this, db.getData());
        homeList.setAdapter(adapter);
        add.setText("Add");
        filter.setText("View Sales");
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,db.getItems());
        itemName.setThreshold(1);
        itemName.setAdapter(arrayAdapter);
    }
}
