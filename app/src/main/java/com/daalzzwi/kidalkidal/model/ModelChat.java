package com.daalzzwi.kidalkidal.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ModelChat implements Serializable {

    private String chatType;
    private String chatTime;
    private String chatUserEmail;
    private String chatUserName;
    private Bitmap chatUserImage;
    private String chatMessage;
    private int chatViewType;

    public ModelChat() {

        this.chatType = "";
        this.chatTime = "";
        this.chatUserEmail = "";
        this.chatUserName = "";
        this.chatUserImage = null;
        this.chatMessage = "";
        this.chatViewType = 0;
    }

    public String getChatType() { return chatType; }

    public void setChatType( String chatType ) { this.chatType = chatType; }

    public String getChatTime() { return chatTime; }

    public void setChatTime( String chatTime ) { this.chatTime = chatTime; }

    public String getChatUserEmail() { return chatUserEmail; }

    public void setChatUserEmail( String chatUserEmail ) { this.chatUserEmail = chatUserEmail; }

    public String getChatUserName() { return chatUserName; }

    public void setChatUserName( String chatUser ) { this.chatUserName = chatUser; }

    public Bitmap getChatUserImage() { return chatUserImage; }

    public void setChatUserImage( Bitmap chatUserImage) { this.chatUserImage = chatUserImage; }

    public String getChatMessage() { return chatMessage; }

    public void setChatMessage( String chatMessage ) { this.chatMessage = chatMessage; }

    public int getChatViewType() { return chatViewType; }

    public void setChatViewType( int chatViewType ) { this.chatViewType = chatViewType; }
}
