package com.example.arcus.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.arcus.R;
import com.example.arcus.ui.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignInPage extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextPassword;
    private ProgressBar progressBar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        Button signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    public void onClick(View v) {
        userLogin();

    }

    private void userLogin() {
        String user = editTextUsername.getText().toString().trim();
        String pw = editTextPassword.getText().toString().trim();

        //conditions to see if EditText inputs are formatted correctly
        if (user.isEmpty()) {
            editTextUsername.setError("Email can not be empty");
            editTextUsername.requestFocus();
            return;
        }


        if (pw.length() <= 0 ) {
            editTextPassword.setError("password can not be empty");
            editTextPassword.requestFocus();
            return;
        }

        DocumentReference documentReference = db.collection("registers").document(user);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String p = String.valueOf(documentSnapshot.get("password"));


                        if(p.equals(pw)){
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(SignInPage.this, "Logged in", Toast.LENGTH_LONG).show();
                            Intent signin = new Intent(SignInPage.this, DashboardActivity.class);
                            signin.putExtra("id", user);
                            startActivity(signin);

                        }

                    }
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(SignInPage.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });


    }
}
