package com.shebo.chatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shebo.chatapp.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtMailAddress, txtUsername, txtPassword;
    private Button btnRegister, btnAlreadyHaveAccount;

    private ProgressDialog progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initWidgets();
        progressBar = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);
        btnAlreadyHaveAccount.setOnClickListener(this);
    }

    private void initWidgets() {
        txtMailAddress = findViewById(R.id.txt_register_mail);
        txtUsername = findViewById(R.id.txt_register_name);
        txtPassword = findViewById(R.id.txt_register_password);

        btnRegister = findViewById(R.id.btn_register);
        btnAlreadyHaveAccount = findViewById(R.id.btn_already_have_account);

        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register){
            register();
        }
        else if (v.getId() == R.id.btn_already_have_account){
            openLoginPage();
        }
    }

    private void openLoginPage() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void register() {
        final String mail = txtMailAddress.getText().toString();
        final String username = txtUsername.getText().toString();
        final String password = txtPassword.getText().toString();

        if (TextUtils.isEmpty(mail)){
            View view = findViewById(R.id.linearLayout1);
            Snackbar.make(view, "Enter your mail address", Snackbar.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(username)){
            View view = findViewById(R.id.linearLayout1);
            Snackbar.make(view, "Enter your username", Snackbar.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            View view = findViewById(R.id.linearLayout1);
            Snackbar.make(view, "Enter your password", Snackbar.LENGTH_LONG).show();
        }
        else{

            final View view = findViewById(R.id.linearLayout1);

            progressBar.setMessage("Loading");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            mAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                currentUserID = mAuth.getCurrentUser().getUid();

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", currentUserID);
                                hashMap.put("username", username);
                                hashMap.put("password", password);
                                hashMap.put("image", "default");
                                hashMap.put("status", "Offline");

                                userReference.child(currentUserID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Snackbar.make(view, "User has been created successfully", Snackbar.LENGTH_LONG)
                                                .show();
                                        progressBar.dismiss();
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                });
                            }
                            else{
                                Snackbar.make(view, "User has been created successfully", Snackbar.LENGTH_LONG)
                                        .show();
                                progressBar.dismiss();
                            }
                        }
                    });
        }
    }
}
