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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.arcus.R;
import com.example.arcus.signin.Item;
import com.example.arcus.signin.SignInPage;
import com.example.arcus.ui.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
    public String idPub = "";

    public double sum = 0.00;



    public void update(List<Sales> sales){
        saleList.clear();
        saleList = sales;
    }

    private void addSale(@NonNull List<String> data, double total, double s, double tax, double tip,  String id, String pin, String rc){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ArrayList<String> items = new ArrayList<>(data);

        Map<String, Object> saleInfo = new HashMap<>();
        saleInfo.put("Subtotal", total);
        saleInfo.put("Price", s);
        saleInfo.put("Tax", tax);
        saleInfo.put("Tip", tip);
        saleInfo.put("Items", items);
        saleInfo.put("reg", id);
        saleInfo.put("rc", rc);
        saleInfo.put("emp", pin);
        saleInfo.put("Time", now.format(formatter));

        db.collection("Sales").add(saleInfo);
        DocumentReference employeeREF = db.collection("Employee").document(pin);
        DocumentReference registerREF = db.collection("Registers").document(id);
        DocumentReference revCenterRef = db.collection("RevenueCenter").document(rc);

        employeeREF.update("tips", FieldValue.increment(tip));
        employeeREF.update("totalSales", FieldValue.increment(1));

        registerREF.update("revenue", FieldValue.increment(total));
        registerREF.update("salesTotal", FieldValue.increment(1));

        revCenterRef.update("revenue", FieldValue.increment(total));
        revCenterRef.update("sales", FieldValue.increment(1));
    }

    public double getPrice(List <Double> price){
        double total = 0.0;
        for (int i = 0; i < price.size(); i++){
            total += price.get(i);
        }
        return fmt(total);
    }

    public void getName(String id, TextView textView){

        DocumentReference documentReference = db.collection("Employee").document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){

                        String fName = (String) documentSnapshot.get("fname");
                        String lName = (String) documentSnapshot.get("lname");
                        System.out.println(fName + " " + lName);
                        textView.setText(fName + " " + lName);

                    }
                }
            }
        });

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
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return Double.parseDouble(decimalFormat.format(num));
    }

    public double tipTaxCalc(double total, double times){
        return fmt(total * times);
    }

    public String rc;

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


        Bundle extras = getIntent().getExtras();
        ArrayList<Item> items = new ArrayList<>();
        String id = getIntent().getStringExtra("ID");
        idPub = id;
        String pin = getIntent().getStringExtra("PIN");
        rc = getIntent().getStringExtra("rc");

        TextView cash = findViewById(R.id.cashName);

        getName(pin, cash);

        if (extras != null) {
            items = (ArrayList<Item>) getIntent().getSerializableExtra("itemList");
        }

        TableLayout tableLayout = findViewById(R.id.table);

        for (int i = 0; i < items.size(); i++) {

            Button itemButton = new Button(this);
            Item item = items.get(i);
            itemButton.setText(item.getName());
            itemButton.setWidth(400);
            itemButton.setHeight(400);
            itemButton.setTextSize(20);


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
                sum = getPrice(priceList);
                setPriceTV(price);
                if(!data.isEmpty()) {
                    tipScreen(view, tipTaxCalc(sum, .2), tipTaxCalc(sum, .18), tipTaxCalc(sum, .15), id, price, listItemAdapter, pin, rc);
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
            case R.id.signoutItem:
                Intent i = new Intent(RegisterMenuActivity.this, DashboardActivity.class);
                i.putExtra("id", idPub);
                i.putExtra("rc", rc);
                startActivity(i);
                return true;

            case R.id.logoutItem:
                pinPad(RegisterMenuActivity.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public String pin = "";

    public void pinPad(Context context){
        String code = "198008";

        Dialog dialogPin = new Dialog(context);
        dialogPin.setContentView(R.layout.pinlayout);
        dialogPin.show();

        TextView pinNum = dialogPin.findViewById(R.id.pinNum);
        pinNum.setText("");

        Button button1 = dialogPin.findViewById(R.id.button1);
        Button button2 = dialogPin.findViewById(R.id.button2);
        Button button3 = dialogPin.findViewById(R.id.button3);
        Button button4 = dialogPin.findViewById(R.id.button4);
        Button button5 = dialogPin.findViewById(R.id.button5);
        Button button6 = dialogPin.findViewById(R.id.button6);
        Button button7 = dialogPin.findViewById(R.id.button7);
        Button button8 = dialogPin.findViewById(R.id.button8);
        Button button9 = dialogPin.findViewById(R.id.button9);
        Button button0 = dialogPin.findViewById(R.id.button0);

        ImageButton go = dialogPin.findViewById(R.id.buttonGo);
        ImageButton back = dialogPin.findViewById(R.id.buttonX);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("1", pinNum);

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("2", pinNum);

            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("3", pinNum);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("4", pinNum);

            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("5", pinNum);

            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("6", pinNum);

            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("7", pinNum);

            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("8", pinNum);

            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("9", pinNum);

            }
        });
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addto("0", pinNum);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractFromPin();
                pinNum.setText(pin);
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pin.isEmpty()) {
                    pin = "";
                    pinNum.setHint("Pin Can Not Be Empty");
                }

                else if (pin.equals(code)) {
                    startActivity(new Intent(RegisterMenuActivity.this, SignInPage.class));
                }

                else{
                    pin = "";
                    pinNum.setHint("INVALID PIN");
                }

            }
        });

    }

    public void addto(String p, TextView textView){
        if(pin == null || pin.length() <= 0){
            pin = p;
        }
        else{
            pin = pin + p;
        }
        textView.setText(pin);
    }

    public void subtractFromPin() {
        if (pin != null && pin.length() > 0) {
            pin = pin.substring(0, pin.length() - 1);
        }
    }


    public void tipScreen(View view, double tip20, double tip18, double tip15, String id, TextView price, ListItemAdapter listItemAdapter, String pin, String rc) {
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
                checkoutScreen(view.getContext(), 0.00, id, price, listItemAdapter, pin, rc);
            }
        });

        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkoutScreen(view.getContext(), tip20, id, price, listItemAdapter, pin, rc);
            }
        });

        button18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkoutScreen(view.getContext(), tip18, id, price, listItemAdapter, pin, rc);
            }
        });

        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkoutScreen(view.getContext(), tip15, id, price, listItemAdapter, pin, rc);
            }
        });
    }
    @SuppressLint("SetTextI18n")
    public void checkoutScreen(Context context, double tip, String id, TextView price, ListItemAdapter listItemAdapter, String pin, String rc){
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
                addSale(data, total, sum, taxSum, tip, id, pin, rc);
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