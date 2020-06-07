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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView date,totalOnDate;
    CardView cv;
    DatePickerDialog pickerDialog;
    TextInputEditText itemPrice;
    AutoCompleteTextView itemName;
    Button add,filter,cancel;
    ListView homeList;
    Spinner tableSpinner;
    database db;
    homeListAdapter adapter;
    ArrayList<dbPojo> arrayList;
    ArrayList<String> tableList;
    public static int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date=(TextView)findViewById(R.id.date);
        totalOnDate=(TextView)findViewById(R.id.total);
        cv=(CardView)findViewById(R.id.dateCV);
        itemName=(AutoCompleteTextView)findViewById(R.id.itemName);
        itemPrice=(TextInputEditText)findViewById(R.id.price);
        add=(Button)findViewById(R.id.add);
        filter=(Button)findViewById(R.id.filter);
        tableSpinner=(Spinner)findViewById(R.id.tableSpinner);
        cancel=(Button)findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        homeList=(ListView)findViewById(R.id.salesList);
        tableList=new ArrayList<>();

        tableList.add("Sales");
        tableList.add("Purchase");
        ArrayAdapter<String> tableSpinnerAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,tableList);
        tableSpinner.setAdapter(tableSpinnerAdapter);
        tableSpinner.setSelection(0);
        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                trigger();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        db=new database(MainActivity.this);
        adapter=new homeListAdapter(MainActivity.this,db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString()));
        homeList.setAdapter(adapter);
        arrayList=db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString());
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
                        trigger();
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
                        db.update(tableSpinner.getSelectedItem().toString(),id,date.getText().toString(),itemName.getText().toString(),Integer.parseInt(itemPrice.getText().toString()));
                    else
                        db.insertData(tableSpinner.getSelectedItem().toString(),date.getText().toString(), itemName.getText().toString(), Integer.parseInt(itemPrice.getText().toString()));
                    itemName.setText("");
                    itemPrice.setText("");
                    trigger();
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
                arrayList=db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString());
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
                if(filter.getText().equals("View Sales")){
                    Intent intent=new Intent(MainActivity.this,Filter.class);
                    intent.putExtra("tableName",tableSpinner.getSelectedItem().toString());
                    startActivity(intent);
                }
                else{
                    db.delete(tableSpinner.getSelectedItem().toString(),id);
                    itemName.setText("");
                    itemPrice.setText("");
                    setDate();
                    Toast.makeText(MainActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                    adapter = new homeListAdapter(MainActivity.this, db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString()));
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
        Toast.makeText(MainActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
        int total=db.getTotalOnDate(tableSpinner.getSelectedItem().toString(),date.getText().toString());
        totalOnDate.setText("₹"+total);
        adapter = new homeListAdapter(MainActivity.this, db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString()));
        homeList.setAdapter(adapter);
        cancel.setVisibility(View.GONE);
        add.setText("Add");
        filter.setText("View Sales");
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,db.getItems());
        itemName.setThreshold(1);
        itemName.setAdapter(arrayAdapter);
    }
}
