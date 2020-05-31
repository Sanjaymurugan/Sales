package com.nak.sales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;

public class Filter extends AppCompatActivity {
    CardView fromCV, toCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        fromCV=(CardView)findViewById(R.id.fromCV);
        toCV=(CardView)findViewById(R.id.toCV);
    }
}