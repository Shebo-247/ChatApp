package com.shebo.chatapp.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shebo.chatapp.Model.ChatList;
import com.shebo.chatapp.Notification.Token;
import com.shebo.chatapp.R;
import com.shebo.chatapp.Model.User;
import com.shebo.chatapp.Adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView usersHolder;

    private UserAdapter adapter;

    private List<User> mUsers;
    private List<ChatList> userIDs;

    private String currentUser;

    private DatabaseReference chatListReference, userReference;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);

        usersHolder = view.findViewById(R.id.users_holder);
        usersHolder.setHasFixedSize(true);
        usersHolder.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        userIDs = new ArrayList<>();
        mUsers = new ArrayList<>();

        chatListReference = FirebaseDatabase.getInstance().getReference("ChatList").child(currentUser);
        chatListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Called", "Inside OnDataChange");
                userIDs.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList chatList= snapshot.getValue(ChatList.class);

                    userIDs.add(chatList);
                }

                getChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void updateToken(String referenceToken) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token = new Token(referenceToken);

        reference.child(currentUser).setValue(token);
    }

    private void getChatList() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Call", "readChats");
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    for (ChatList chatList : userIDs){
                        if (user.getId().equals(chatList.getId())){
                            mUsers.add(user);
                        }
                    }
                }

                adapter = new UserAdapter(getContext(), mUsers, true);
                usersHolder.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
