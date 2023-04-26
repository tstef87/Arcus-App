package com.example.arcus.ui;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arcus.R;
import com.example.arcus.signin.Item;
import com.example.arcus.signin.SignInPage;
import com.example.arcus.ui.register.ListItemAdapter;
import com.example.arcus.ui.register.RegisterMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;



public class DashboardActivity extends AppCompatActivity {
    public String pin;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private static ArrayList<Item> itemArrayList = new ArrayList<>();;
    private String id = "";

    private void updateDataB(String id){
        itemArrayList.clear();
        db.collection("registers/"+id+"/items").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<Item> types = queryDocumentSnapshots.toObjects(Item.class);

                            // Add all to your list
                            itemArrayList.addAll(types);
                            Log.d(TAG, "onSuccess: " + itemArrayList);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        TextView reggid = findViewById(R.id.reg_id);
        TextView itemList = findViewById(R.id.item_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            reggid.setText(id);
        }

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(view -> startActivity(new Intent(DashboardActivity.this, SignInPage.class)));

        Button goToReg = findViewById(R.id.goToReg);
        goToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pin(view.getContext());

            }
        });


        Button updateDB = findViewById(R.id.updateDB);
        updateDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataB(id);
            }
        });
    }

    public void pin(Context context) {

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

        Button go = dialogPin.findViewById(R.id.buttonGo);
        Button back = dialogPin.findViewById(R.id.buttonX);


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
                    pinNum.setError("Invalid Pin");
                    pinNum.requestFocus();
                    return;
                }

                DocumentReference documentReference = db.collection("employee").document(pin);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                String p = String.valueOf(documentSnapshot.get("pin"));


                                if(p.equals(pin)){
                                    Toast.makeText(DashboardActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                                    Intent openMenu = new Intent(DashboardActivity.this, RegisterMenuActivity.class);
                                    openMenu.putExtra("PIN", pin);
                                    openMenu.putExtra("itemList", itemArrayList);
                                    openMenu.putExtra("ID", id);
                                    startActivity(openMenu);

                                }

                            }
                        }
                        else{
                            Toast.makeText(DashboardActivity.this, "Inavild Pin", Toast.LENGTH_LONG).show();
                        }
                    }
                });

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

}
