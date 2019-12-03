package com.shebo.chatapp.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shebo.chatapp.R;
import com.shebo.chatapp.Adapter.TabAccessorAdapter;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private TabAccessorAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");


        adapter = new TabAccessorAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setStatus(String status){
        currentUserID = mAuth.getCurrentUser().getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        userReference.child(currentUserID).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("Offline");
    }

    public void initWidgets(){
        mainToolbar = findViewById(R.id.main_toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Chat App");
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.item_profile){
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.item_sign_out){
            mAuth.signOut();
            sendUserToLoginPage();
        }

        return true;
    }

    private void sendUserToLoginPage() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            sendUserToLoginPage();
        }
    }
}
