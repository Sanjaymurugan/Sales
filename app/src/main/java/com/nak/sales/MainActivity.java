package com.nak.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

public class MainActivity extends AppCompatActivity {
    TextView date,totalOnDate,noSales;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_items:
                startActivity(new Intent(MainActivity.this,editItemsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declarations
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
        noSales=(TextView)findViewById(R.id.noSales);
        tableList=new ArrayList<>();

        setTitle("Expense Monitor");

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(!preferences.getBoolean("firstTime",false)){

            new FancyShowCaseView.Builder(this)
                    .focusOn(tableSpinner)
                    .backgroundColor(R.color.colorPrimary)
                    .title("Select the Category")
                    .titleGravity(RIGHT)
                    .build()
                    .show();

            new FancyShowCaseView.Builder(this)
                    .focusOn(date)
                    .backgroundColor(R.color.colorPrimary)
                    .title("Select required date")
                    .titleGravity(LEFT)
                    .delay(3000)
                    .build()
                    .show();

            new FancyShowCaseView.Builder(this)
                    .focusOn(itemName)
                    .backgroundColor(R.color.colorPrimary)
                    .title("Enter the Item name")
                    .titleGravity(RIGHT)
                    .delay(6000)
                    .build()
                    .show();

            new FancyShowCaseView.Builder(this)
                    .focusOn(itemPrice)
                    .backgroundColor(R.color.colorPrimary)
                    .title("Enter the price of the item")
                    .titleGravity(LEFT)
                    .delay(9000)
                    .build()
                    .show();

            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("firstTime",true);
            editor.commit();
        }

        tableList.add("Expense");
        tableList.add("Purchase"); //Contents for the spinner
        tableList.add("Sales");
        ArrayAdapter<String> tableSpinnerAdapter=new ArrayAdapter<>(this,R.layout.textview_with_padding,tableList);
        tableSpinnerAdapter.setDropDownViewResource(R.layout.textview_with_padding);
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

        setDate();
        db=new database(MainActivity.this);
        adapter=new homeListAdapter(MainActivity.this,db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString()));
        homeList.setAdapter(adapter);
        arrayList=db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString());
        if(arrayList.size()==0) {
            noSales.setVisibility(View.VISIBLE);
            noSales.setText("NO " + tableSpinner.getSelectedItem().toString().toUpperCase() + " ON " + date.getText().toString());
        } else
            noSales.setVisibility(View.GONE);

        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,db.getItems()); //ArrayAdapter for autocomplete(suggestion)
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
                        String one,two;

                        //For change the date to the format of DD-MM-YYYY
                        if(i2/10==0)
                            one="0"+i2;
                        else
                            one=i2+"";
                        i1+=1; //month has to be added one
                        if(i1/10==0)
                            two="0"+i1;
                        else
                            two=i1+"";
                        date.setText(one+"-"+two+"-"+i);
                        trigger();
                    }
                },day,month+1,year);
                String temp[]=date.getText().toString().split("-");
                int d=Integer.parseInt(temp[0]);
                int m=Integer.parseInt(temp[1])-1;
                int y=Integer.parseInt(temp[2]);
                pickerDialog.updateDate(y,m,d); //For displaying the current date as the selected date in date picker
                pickerDialog.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(itemName.getText())&&!TextUtils.isEmpty(itemPrice.getText())) {
                    if(add.getText().equals("Edit")) //For editing the existing data
                        db.update(tableSpinner.getSelectedItem().toString(),id,date.getText().toString(),itemName.getText().toString(),Integer.parseInt(itemPrice.getText().toString()));
                    else //For adding new data
                        db.insertData(tableSpinner.getSelectedItem().toString(),date.getText().toString(), itemName.getText().toString(), Integer.parseInt(itemPrice.getText().toString()));
                    Toast.makeText(MainActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
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

        homeList.setOnItemClickListener(new AdapterView.OnItemClickListener() { //For enable editing the existing data
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
                int total=db.getTotalOnDate(tableSpinner.getSelectedItem().toString(),date.getText().toString());
                totalOnDate.setText("₹"+total);
                adapter = new homeListAdapter(MainActivity.this, db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString()));
                homeList.setAdapter(adapter);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* This button is for viewing sales but the button changes to delete on edit mode */
                if(filter.getText().equals("View "+tableSpinner.getSelectedItem().toString())){ //For viewing sales
                    Intent intent=new Intent(MainActivity.this,Filter.class);
                    intent.putExtra("tableName",tableSpinner.getSelectedItem().toString());
                    //Sending the name of the table as sales or purchase from the spinner to the next activity(Filter activity)
                    startActivity(intent);
                }
                else{ //For deleting data
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

        cancel.setOnClickListener(new View.OnClickListener() { //For canceling the edit option
            @Override
            public void onClick(View view) {
                /* It appears only when the list item is selected */
                add.setText("Add");
                filter.setText("View Sales");
                itemName.setText("");
                itemPrice.setText("");
                cancel.setVisibility(View.GONE);
            }
        });
    }

    public void setDate(){ //For setting today's date
        Calendar cal=Calendar.getInstance();
        int day=cal.get(Calendar.DAY_OF_MONTH);
        int month=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);
        month+=1;

        //For changing the format of the date to DD-MM-YYYY
        String one,two;
        if(day/10==0)
            one="0"+day;
        else
            one=""+day;

        if(month/10==0)
            two="0"+month;
        else
            two=""+month;

        date.setText(one+"-"+two+"-"+year);
    }

    public void trigger(){ //For updating the list whenever the data is being added or updated
        int total=db.getTotalOnDate(tableSpinner.getSelectedItem().toString(),date.getText().toString());
        totalOnDate.setText("₹"+total);
        arrayList= db.getData(tableSpinner.getSelectedItem().toString(),date.getText().toString());
        adapter = new homeListAdapter(MainActivity.this,arrayList);
        homeList.setAdapter(adapter);
        cancel.setVisibility(View.GONE);
        add.setText("Add");
        filter.setText("View "+tableSpinner.getSelectedItem().toString());
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,db.getItems());
        itemName.setThreshold(1);
        itemName.setAdapter(arrayAdapter);
        if(arrayList.size()==0) {
            noSales.setVisibility(View.VISIBLE);
            noSales.setText("NO " + tableSpinner.getSelectedItem().toString().toUpperCase() + " ON " + date.getText().toString());
        } else
            noSales.setVisibility(View.GONE);
    }
}
