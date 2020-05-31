package com.nak.sales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
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
    Button add,filter;
    ListView homeList;
    database db;
    Date currentDate;
    SimpleDateFormat sdf;
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
        homeList=(ListView)findViewById(R.id.salesList);

        db=new database(MainActivity.this);
        currentDate=new Date();
        sdf=new SimpleDateFormat("dd-MM-yyyy");
        adapter=new homeListAdapter(MainActivity.this,db.getData());
        homeList.setAdapter(adapter);
        arrayList=db.getData();

        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,db.getItems());
        itemName.setThreshold(1);
        itemName.setAdapter(arrayAdapter);

        final String today=sdf.format(currentDate);
        date.setText(today);

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
                        date.setText(i2+"-"+i1+"-"+i);
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
                    date.setText(today);
                    Toast.makeText(MainActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    adapter = new homeListAdapter(MainActivity.this, db.getData());
                    homeList.setAdapter(adapter);
                    add.setText("Add");
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
            }
        });
    }
}
