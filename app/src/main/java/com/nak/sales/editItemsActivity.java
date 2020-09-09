package com.nak.sales;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class editItemsActivity extends AppCompatActivity {
    EditText itemName;
    ListView itemsList;
    Button edit;
    Button delete;
    database db;
    ArrayList<String> items=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_items);

        itemName=(EditText)findViewById(R.id.edit_item_name);
        itemsList=(ListView)findViewById(R.id.edit_item_list);
        edit=(Button)findViewById(R.id.edit_item);
        delete=(Button)findViewById(R.id.delete_item);

        db=new database(editItemsActivity.this);
        items=db.getItems();
        final ArrayAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        itemsList.setAdapter(adapter);

        itemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(itemName.getText().toString());
                itemsList.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(editItemsActivity.this,items.get(i),Toast.LENGTH_SHORT).show();
            }
        });
    }
}