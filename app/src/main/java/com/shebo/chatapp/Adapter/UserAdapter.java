package com.shebo.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shebo.chatapp.Activity.ChatActivity;
import com.shebo.chatapp.Model.Message;
import com.shebo.chatapp.R;
import com.shebo.chatapp.Model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    private String lastMessgae;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View userView = LayoutInflater.from(mContext)
                .inflate(R.layout.user_custom_layout, viewGroup, false);
        return new UserViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position) {
        final User user = mUsers.get(position);

        userViewHolder.txtDisplayUser.setText(user.getUsername());
        if (user.getImage().equals("default")){
            Picasso.get().load(R.mipmap.ic_launcher_round).into(userViewHolder.imgUserProfile);
        }
        else{
            Picasso.get().load(user.getImage()).into(userViewHolder.imgUserProfile);
        }

        if (isChat){
            displayLastMessage(user.getId(), userViewHolder.txtLastMessage);

            if (user.getStatus().equals("Online")){
                userViewHolder.imgUserStatus.setImageResource(R.drawable.online);
            }
            else{
                userViewHolder.imgUserStatus.setImageResource(R.drawable.offline);
            }
        }
        else{
            userViewHolder.txtLastMessage.setVisibility(View.GONE);
            userViewHolder.imgUserStatus.setVisibility(View.GONE);
        }

        userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("receiver_id", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imgUserProfile;
        public ImageView imgUserStatus;
        public TextView txtDisplayUser, txtLastMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserProfile = itemView.findViewById(R.id.img_user_profile);
            imgUserStatus = itemView.findViewById(R.id.img_user_status);
            txtDisplayUser = itemView.findViewById(R.id.txt_display_username);
            txtLastMessage = itemView.findViewById(R.id.txt_last_message);
        }
    }

    private void displayLastMessage(final String receiverID, final TextView txtLastMsg){
        lastMessgae = "";

        final String senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);

                    if (message.getReceiver().equals(senderID) && message.getSender().equals(receiverID) ||
                            message.getReceiver().equals(receiverID) && message.getSender().equals(senderID)){
                        lastMessgae = message.getMessageText();
                    }

                    switch (lastMessgae){
                        case "":
                            txtLastMsg.setText("");
                            break;

                        default:
                            txtLastMsg.setText(lastMessgae);
                            break;
                    }

                    lastMessgae = "";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
