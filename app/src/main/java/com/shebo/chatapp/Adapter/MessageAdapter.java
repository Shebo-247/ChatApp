package com.shebo.chatapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shebo.chatapp.Model.Message;
import com.shebo.chatapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int SENT_MSG_TYPE = 1001;
    private static final int RECEIVED_MSG_TYPE = 1002;

    private Context mContext;
    private List<Message> mMessages;


    private FirebaseUser currentUser;

    public MessageAdapter(Context mContext, List<Message> mMessages) {
        this.mContext = mContext;
        this.mMessages = mMessages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == SENT_MSG_TYPE){
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.sent_message_layout, viewGroup, false);
            return new MessageViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.received_msg_layout, viewGroup, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int position) {
        Message message = mMessages.get(position);

        messageViewHolder.displayMessage.setText(message.getMessageText());

        if (position == mMessages.size() - 1){
            if (message.isSeen()){
                messageViewHolder.seenMessage.setText("Seen");
            }
            else{
                messageViewHolder.seenMessage.setText("Delivered");
            }
        }
        else{
            messageViewHolder.seenMessage.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView displayMessage, seenMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            displayMessage = itemView.findViewById(R.id.display_message);
            seenMessage = itemView.findViewById(R.id.seen_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessages.get(position).getSender().equals(currentUser.getUid())){
            return SENT_MSG_TYPE;
        }
        else{
            return RECEIVED_MSG_TYPE;
        }
    }
}
