package com.example.arcus.ui.register;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.arcus.R;
import com.example.arcus.signin.Item;
import com.example.arcus.signin.SignInPage;
import com.example.arcus.ui.DashboardActivity;
import com.google.firebase.firestore.FirebaseFirestore;
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
    //private ArrayAdapter <String> adapter;

    private List<Sales> saleList;
    private List <String> data;
    private List<Double> priceList;
    private TableRow tableRow;


    private double sum = 0.00;



    public void update(List<Sales> sales){
        saleList.clear();
        saleList = sales;
    }

    private void addSale(@NonNull List<String> data, double sum, String id){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ArrayList<String> items = new ArrayList<>(data);

        Map<String, Object> saleInfo = new HashMap<>();
        saleInfo.put("Price", sum);
        saleInfo.put("Items", items);
        saleInfo.put("Time", now.format(formatter));
        //saleInfo.put("Receipt", receipt);

        db.collection("registers/"+id+"/Sales").add(saleInfo);
    }

    public double getPrice(List <Double> price){
        double total = 0.0;
        for (int i = 0; i < price.size(); i++){
            total += price.get(i);
        }
        return total;
    }

    public void setPriceTV (TextView textView){

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        textView.setText(numberFormat.format(sum));
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

        TextView price = findViewById(R.id.sum);
        listView = findViewById(R.id.receipt);
        saleList = new ArrayList<Sales>();
        data = new ArrayList<String>();
        priceList = new ArrayList<Double>();
        ListItemAdapter listItemAdapter = new ListItemAdapter(data, saleList, priceList, sum, price);
        //adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);


        Bundle extras = getIntent().getExtras();
        ArrayList<Item> items = new ArrayList<>();
        String id = getIntent().getStringExtra("ID");
        if (extras != null) {
            items = (ArrayList<Item>) getIntent().getSerializableExtra("itemList");
        }

        TableLayout tableLayout = findViewById(R.id.table);

        for (int i = 0; i < items.size(); i++) {

            Button itemButton = new Button(this);
            Item item = items.get(i);
            itemButton.setText(item.getName());

            if(i % 4 == 0 || i == 0) {
                tableRow = new TableRow(this);
                tableLayout.addView(tableRow);
            }

            tableRow.addView(itemButton);

            if(i == items.size()){
                tableLayout.addView(tableRow);
            }

            itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   int index = checkList(saleList, item.getName());

                   if (index > -1) {
                       Sales saleItem = saleList.get(index);
                       saleItem.setAmount(saleItem.getAmount() + 1);
                       data.set(index, saleItem.toString());
                       priceList.set(index, priceList.get(index) + item.getPrice());

                   }else {
                       Sales sales = new Sales(item.getName(), 1, item.getPrice());
                       saleList.add(sales);
                       data.add(sales.toString());
                       priceList.add(sales.getPrice());
                   }
                   sum = getPrice(priceList);
                   setPriceTV(price);
                   listView.setAdapter(listItemAdapter);
                }
            });
        }



        Button checkOut = findViewById(R.id.checkout);
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!data.isEmpty()) {
                    addSale(data, sum, id);
                    Item i = new Item();
                    sum = 0.00;
                    data.clear();
                    priceList.clear();
                    saleList.clear();
                    listView.setAdapter(listItemAdapter);
                    setPriceTV(price);
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Items in List", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button logOut = findViewById(R.id.logOut1);
        logOut.setOnClickListener(view -> startActivity(new Intent(RegisterMenuActivity.this, SignInPage.class)));



    }
}