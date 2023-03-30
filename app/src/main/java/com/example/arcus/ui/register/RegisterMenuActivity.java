package com.example.arcus.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.arcus.R;
import com.example.arcus.signin.Item;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class RegisterMenuActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListView listView;
    private ArrayAdapter <String> adapter;

    private List<Sales> saleList;
    private List <String> data;

    private double sum = 0.00;

    private void addSale(List<String> data, double sum, String id){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String recpit = "ARCUS \n"
                + now.format(formatter) + "\n"+
                "--------------------\n";


        for(int i = 0; i < data.size(); i++){
            recpit = recpit + (data.get(i) + "\n");
        }

        Map<String, Object> saleInfo = new HashMap<>();
        saleInfo.put("Price", sum);
        saleInfo.put("Recpit", recpit);



        db.collection("registers/"+id+"/Sales").add(saleInfo);
    }

    private String addToPrice(Item item, Boolean add){

        if(add) {
            sum += item.getPrice();

            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            String total = numberFormat.format(sum);

            return total;
        }
        else{
            return "0.00";
        }
    }

    private int checkList(List arrayList, String name){

        if(arrayList.isEmpty()){
            return -1;
        }

        for(int i = 0; i < arrayList.size(); i++){
            Sales sales = (Sales) arrayList.get(i);
            if(sales.getName().equals(name)){
                return i;
            }
        }
        return -1;
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_menu);

        listView = findViewById(R.id.receipt);
        saleList = new ArrayList<Sales>();
        data = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);


        Bundle extras = getIntent().getExtras();
        ArrayList<Item> items = new ArrayList<>();
        String id = getIntent().getStringExtra("ID");
        if (extras != null) {
            items = (ArrayList<Item>) getIntent().getSerializableExtra("itemList");
        }
        TextView price = findViewById(R.id.sum);


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout layout = findViewById(R.id.linear);
        for (int i = 0; i < items.size(); i++) {
            Button button = new Button(this);
            Item item = items.get(i);
            button.setText(item.getName());




            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    price.setText(addToPrice(item, true));

                    int index = checkList( saleList, item.getName());

                    if( index > -1){
                        Sales saleItem = saleList.get(index);
                        saleItem.setAmount(saleItem.getAmount() + 1);
                        data.set(index, saleItem.toString());
                    }
                    else{
                        Sales sales = new Sales(item.getName(), 1, item.getPrice());
                        saleList.add(sales);
                        data.add(sales.toString());
                    }

                    listView.setAdapter(adapter);
                }
            });

            layout.addView(button);

            Button checkOut = findViewById(R.id.checkout);
            checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addSale(data, sum, id);
                    Item i = new Item();
                    sum = 0.00;
                    data.clear();
                    saleList.clear();
                    listView.setAdapter(adapter);
                    price.setText(addToPrice(i, false));

                }
            });
        }
    }
}