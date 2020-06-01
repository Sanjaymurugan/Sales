package com.nak.sales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class filterListAdapter extends BaseAdapter {
    ArrayList<dbPojo> arrayList;
    Context context;

    public filterListAdapter(Context context, ArrayList<dbPojo> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
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
            view= LayoutInflater.from(context).inflate(R.layout.filter_child,viewGroup,false);
        TextView itemName=(TextView)view.findViewById(R.id.filterItem);
        TextView price=(TextView)view.findViewById(R.id.filterPrice);

        dbPojo pojo=arrayList.get(i);
        itemName.setText(pojo.getItemName());
        price.setText("₹"+pojo.getPrice());

        return view;
    }
}
