package com.example.arcus.ui.register;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public ListView listView;
    //private ArrayAdapter <String> adapter;

    public List<Sales> saleList;
    public List <String> data;
    public List<Double> priceList;
    public TableRow tableRow;

    public double sum = 0.00;



    public void update(List<Sales> sales){
        saleList.clear();
        saleList = sales;
    }

    private void addSale(@NonNull List<String> data, double total, double s, double tax, double tip,  String id){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ArrayList<String> items = new ArrayList<>(data);

        Map<String, Object> saleInfo = new HashMap<>();
        saleInfo.put("Subtotal", total);
        saleInfo.put("Price", s);
        saleInfo.put("Tax", tax);
        saleInfo.put("Tip", tip);
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
        return fmt(total);
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

    public double fmt(double num){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(num));
    }

    public double tipTaxCalc(double total, double times){
        return fmt(total * times);
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
                       priceList.set(index, priceList.get(index) + fmt(item.getPrice()));

                   }else {
                       Sales sales = new Sales(item.getName(), 1, fmt(item.getPrice()));
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
                    tipScreen(view, tipTaxCalc(sum, .2), tipTaxCalc(sum, .18), tipTaxCalc(sum, .15), id, price, listItemAdapter);
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Items in List", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drop_down_menu, menu);

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutItem:
                startActivity(new Intent(RegisterMenuActivity.this, SignInPage.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    public void tipScreen(View view, double tip20, double tip18, double tip15, String id, TextView price, ListItemAdapter listItemAdapter) {
        // Create a new AlertDialog object and set its content to your pop-up layout

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tip_prompt, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView textView20 = (TextView) dialogView.findViewById(R.id.tip20Text);
        TextView textView18 = (TextView) dialogView.findViewById(R.id.tip18Text);
        TextView textView15 = (TextView) dialogView.findViewById(R.id.tip15Text);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        textView20.setText("$" + decimalFormat.format(tip20));
        textView18.setText("$" + decimalFormat.format(tip18));
        textView15.setText("$" + decimalFormat.format(tip15));

        Button button0 = (Button) dialogView.findViewById(R.id.noTipButton);
        Button button20 = (Button) dialogView.findViewById(R.id.tip20);
        Button button18 = (Button) dialogView.findViewById(R.id.tip18);
        Button button15 = (Button) dialogView.findViewById(R.id.tip15);

        dialog.show();

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkoutScreen(view.getContext(), 0.00, id, price, listItemAdapter);
            }
        });

        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkoutScreen(view.getContext(), tip20, id, price, listItemAdapter);
            }
        });

        button18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkoutScreen(view.getContext(), tip18, id, price, listItemAdapter);
            }
        });

        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkoutScreen(view.getContext(), tip15, id, price, listItemAdapter);
            }
        });
    }
    @SuppressLint("SetTextI18n")
    public void checkoutScreen(Context context, double tip, String id, TextView price, ListItemAdapter listItemAdapter){
        Dialog dialogCheckOut = new Dialog(context);
        dialogCheckOut.setContentView(R.layout.checkout_prompt);
        dialogCheckOut.show();

        TextView grandTotal = dialogCheckOut.findViewById(R.id.grand_total);
        TextView tipTotal = dialogCheckOut.findViewById(R.id.tip_total);
        TextView tax = dialogCheckOut.findViewById(R.id.tax_total);

        double taxSum = tipTaxCalc(sum, .06);
        double total = fmt(sum + taxSum + tip);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        grandTotal.setText("$" + decimalFormat.format(total));
        tipTotal.setText("$" + decimalFormat.format(tip));
        tax.setText("$" + decimalFormat.format(taxSum));


        Button checkout = dialogCheckOut.findViewById(R.id.confirm_payment);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCheckOut.dismiss();
                addSale(data, total, sum, taxSum, tip, id);
                Item i = new Item();
                sum = 0.00;
                data.clear();
                priceList.clear();
                saleList.clear();
                listView.setAdapter(listItemAdapter);
                setPriceTV(price);
            }
        });



    }

}