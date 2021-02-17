package com.ujwal.locate2.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ChatMessage implements Parcelable {
    private String messageText;
    private String senderuuid;
    private String recieveruuid;
    private boolean read=false;
    private long messageTime=new Date().getTime();

    public ChatMessage(String messageText, String senderuuid, String recieveruuid, boolean read) {
        this.messageText = messageText;
        this.recieveruuid=recieveruuid;
        this.senderuuid=senderuuid;
        this.read=read;
        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderuuid() {
        return senderuuid;
    }

    public void setSenderuuid(String senderuuid) {
        this.senderuuid = senderuuid;
    }

    public String getRecieveruuid() {
        return recieveruuid;
    }

    public void setRecieveruuid(String recieveruuid) {
        this.recieveruuid = recieveruuid;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }


    protected ChatMessage(Parcel in) {
        messageText = in.readString();
        senderuuid = in.readString();
        recieveruuid = in.readString();
        read = in.readByte() != 0x00;
        messageTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageText);
        dest.writeString(senderuuid);
        dest.writeString(recieveruuid);
        dest.writeByte((byte) (read ? 0x01 : 0x00));
        dest.writeLong(messageTime);
    }

    @SuppressWarnings("unused")
    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}