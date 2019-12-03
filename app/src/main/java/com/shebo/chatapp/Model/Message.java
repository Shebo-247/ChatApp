package com.shebo.chatapp.Model;

public class Message {
    String sender, receiver, messageText;
    boolean isSeen;

    public Message() {
    }

    public Message(String sender, String receiver, String messageText, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.isSeen = isSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }
}
