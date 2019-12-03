package com.shebo.chatapp.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shebo.chatapp.Fragments.APIService;
import com.shebo.chatapp.Model.Message;
import com.shebo.chatapp.Adapter.MessageAdapter;
import com.shebo.chatapp.Notification.Client;
import com.shebo.chatapp.Notification.Data;
import com.shebo.chatapp.Notification.MyResponse;
import com.shebo.chatapp.Notification.Sender;
import com.shebo.chatapp.Notification.Token;
import com.shebo.chatapp.R;
import com.shebo.chatapp.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolbar;
    private CircleImageView chatReceiverProfile;
    private TextView chatReceiverName;
    private RecyclerView chatMessagesHolder;
    private EditText chatMessageText;
    private ImageButton chatSendBtn;

    private DatabaseReference rootReference, userReference, chatReference;
    String senderID, receiverID;

    private MessageAdapter adapter;
    private List<Message> mMessages = new ArrayList<>();

    ValueEventListener seenListener;

    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initWidgets();

        receiverID = getIntent().getStringExtra("receiver_id");

        rootReference = FirebaseDatabase.getInstance().getReference();
        userReference = FirebaseDatabase.getInstance().getReference("Users").child(receiverID);
        chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                chatReceiverName.setText(user.getUsername());

                if (user.getImage().equals("default")){
                    chatReceiverProfile.setImageResource(R.mipmap.ic_launcher_round);
                }
                else{
                    Picasso.get().load(user.getImage()).into(chatReceiverProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //seenMessage(senderID);

        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(chatMessageText.getText().toString().equals(""))){
                    sendMessage();
                    notify = true;
                }
                else{
                    Toast.makeText(ChatActivity.this, "Enter message first !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void seenMessage(final String userID){
        seenListener = chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);

                    if (message.getReceiver().equals(senderID) && message.getSender().equals(userID)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);

                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setStatus(String status){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        rootReference.child("Users").child(senderID).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //chatReference.removeEventListener(seenListener);
        setStatus("Offline");
    }

    @Override
    protected void onStart() {
        super.onStart();

        readAllMessages();
    }

    private void readAllMessages() {
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);

                    if (message.getReceiver().equals(receiverID) && message.getSender().equals(senderID)
                            || message.getReceiver().equals(senderID) && message.getSender().equals(receiverID)){
                        mMessages.add(message);
                    }
                }

                adapter = new MessageAdapter(ChatActivity.this, mMessages);
                chatMessagesHolder.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderID);
        hashMap.put("receiver", receiverID);
        hashMap.put("messageText", chatMessageText.getText().toString());
        hashMap.put("isSeen", false);

        rootReference.child("Chats").push().setValue(hashMap);

        // add the receiverID to ChatList of current user
        final DatabaseReference chatListReference = FirebaseDatabase.getInstance().getReference("ChatList");
        chatListReference.child(senderID).child(receiverID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()){
                            chatListReference.child(senderID).child(receiverID).child("id").setValue(receiverID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        chatListReference.child(receiverID).child(senderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()){
                            chatListReference.child(receiverID).child(senderID).child("id").setValue(senderID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        chatMessageText.setText("");

        final String msg = chatMessageText.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(senderID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiverID, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(senderID, username + ": " + msg, "New Message", receiverID, R.mipmap.ic_launcher);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initWidgets() {
        chatToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatReceiverProfile = findViewById(R.id.chat_receiver_profile);
        chatReceiverName = findViewById(R.id.chat_receiver_name);
        chatMessagesHolder = findViewById(R.id.chat_messages_holder);
        chatMessageText = findViewById(R.id.chat_message_text);
        chatSendBtn = findViewById(R.id.chat_send_btn);

        chatMessagesHolder.setLayoutManager(new LinearLayoutManager(this));
        chatMessagesHolder.setHasFixedSize(true);
    }
}
