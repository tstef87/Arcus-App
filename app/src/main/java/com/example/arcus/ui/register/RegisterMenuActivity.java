package com.example.arcus.ui.register;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.arcus.R;
import com.example.arcus.signin.Item;
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

    private String addToPrice(Item item, Boolean add){

        if(add) {
            sum += item.getPrice();
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return  numberFormat.format(sum);
        }
        else{
            return "$0.00";
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
        ListItemAdapter listItemAdapter = new ListItemAdapter(data, saleList);
        //adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);


        Bundle extras = getIntent().getExtras();
        ArrayList<Item> items = new ArrayList<>();
        String id = getIntent().getStringExtra("ID");
        if (extras != null) {
            items = (ArrayList<Item>) getIntent().getSerializableExtra("itemList");
        }
        TextView price = findViewById(R.id.sum);

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
                    price.setText(addToPrice(item, true));

                    int index = checkList(saleList, item.getName());

                    if (index > -1) {
                        Sales saleItem = saleList.get(index);
                        saleItem.setAmount(saleItem.getAmount() + 1);
                        data.set(index, saleItem.toString());
                    } else {
                        Sales sales = new Sales(item.getName(), 1, item.getPrice());
                        saleList.add(sales);
                        data.add(sales.toString());
                    }

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
                    saleList.clear();
                    //listView.setAdapter(adapter);
                    listView.setAdapter(listItemAdapter);
                    price.setText(addToPrice(i, false));
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Items in List", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}