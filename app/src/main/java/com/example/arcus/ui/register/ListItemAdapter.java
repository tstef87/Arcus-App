package com.example.arcus.ui.register;

import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.arcus.R;

import java.util.List;
import java.util.Locale;

public class ListItemAdapter extends BaseAdapter {


    private List<String> items;
    private List<Sales> sales;
    private List<Double> price;
    private double sum;

    private TextView textView;


    public ListItemAdapter(List<String> items, List<Sales> sales, List<Double> price, double sum, TextView textView){
        this.items = items;
        this.sales = sales;
        this.price = price;
        this.sum = sum;
        this.textView = textView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_format, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.itemName);
        textView.setText(items.get(position));

        Button del = convertView.findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSum(position);
                removeItem(position);
            }
        });

        return convertView;
    }

    public void removeItem(int position) {
        items.remove(position);
        sales.remove(position);
        price.remove(position);
        notifyDataSetChanged();

    }

    public void setSum(int position){
        sum = 0;
        for(int i = 0; i < price.size(); i++){
            sum += price.get(i);
        }
        sum -= price.get(position);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        textView.setText(numberFormat.format(sum));
    }
}

