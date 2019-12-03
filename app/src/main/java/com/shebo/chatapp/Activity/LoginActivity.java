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
import com.shebo.chatapp.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtMailAddress, txtPassword;
    private Button btnLogin, btnCreateNewAccount;

    private ProgressDialog progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initWidgets();
        progressBar = new ProgressDialog(this);

        btnLogin.setOnClickListener(this);
        btnCreateNewAccount.setOnClickListener(this);
    }

    public void initWidgets(){
        txtMailAddress = findViewById(R.id.txt_login_mail);
        txtPassword = findViewById(R.id.txt_login_password);

        btnLogin = findViewById(R.id.btn_login);
        btnCreateNewAccount = findViewById(R.id.btn_create_new_account);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login){
            login();
        }
        else if (v.getId() == R.id.btn_create_new_account){
            openRegisterPage();
        }
    }

    private void openRegisterPage() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void login() {
        String mail = txtMailAddress.getText().toString();
        String password = txtPassword.getText().toString();
        final View view = findViewById(R.id.textView);

        if (TextUtils.isEmpty(mail)){
            Snackbar.make(view, "Please enter your mail", Snackbar.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Snackbar.make(view, "Please enter your password", Snackbar.LENGTH_LONG).show();
        }
        else{
            progressBar.setMessage("Loading");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressBar.dismiss();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                    else{
                        Snackbar.make(view, "Error username or password !", Snackbar.LENGTH_LONG).show();
                        progressBar.dismiss();
                    }
                }
            });
        }
    }
}