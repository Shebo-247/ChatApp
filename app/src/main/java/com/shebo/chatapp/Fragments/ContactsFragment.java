package com.shebo.chatapp.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shebo.chatapp.R;
import com.shebo.chatapp.Model.User;
import com.shebo.chatapp.Adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView contactsHolder;
    private UserAdapter adapter;
    private List<User> mUsers;

    //FirebaseUser currentUser;
    DatabaseReference usersReference;

    String currentUserID;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsHolder = view.findViewById(R.id.contacts_holder);
        contactsHolder.setHasFixedSize(true);
        contactsHolder.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsers = new ArrayList<>();
        readAllUsers();

        return view;
    }

    private void readAllUsers() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    if (!user.getId().equals(currentUserID))
                        mUsers.add(user);
                }

                adapter = new UserAdapter(getContext(), mUsers, false);
                contactsHolder.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}