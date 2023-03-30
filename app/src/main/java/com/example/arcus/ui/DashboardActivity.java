package com.example.arcus.ui;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arcus.R;
import com.example.arcus.signin.Item;
import com.example.arcus.signin.SignInPage;
import com.example.arcus.ui.register.RegisterMenuActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class DashboardActivity extends AppCompatActivity {

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
                Intent openMenu = new Intent(DashboardActivity.this, RegisterMenuActivity.class);
                openMenu.putExtra("itemList", itemArrayList);
                openMenu.putExtra("ID", id);
                startActivity(openMenu);
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


}
