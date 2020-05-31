package com.nak.sales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class homeListAdapter extends BaseAdapter {
    Context context;
    ArrayList<dbPojo> arrayList;

    public homeListAdapter(Context mContext, ArrayList<dbPojo> mArrayList) {
        context=mContext;
        arrayList=mArrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
            view= LayoutInflater.from(context).inflate(R.layout.home_list_child,viewGroup,false);
        TextView date=(TextView)view.findViewById(R.id.date);
        TextView itemName=(TextView)view.findViewById(R.id.itemName);
        TextView price=(TextView)view.findViewById(R.id.price);

        dbPojo pojo=arrayList.get(i);
        date.setText(pojo.getDate());
        itemName.setText(pojo.getItemName());
        price.setText("₹"+pojo.getPrice());

        return view;
    }
}
