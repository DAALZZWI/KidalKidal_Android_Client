package com.daalzzwi.kidalkidal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelChatPayload implements Serializable {

    private Map< String , String > chatImage;
    private String chatRoomType;

    public ModelChatPayload() {

        this.chatImage = new HashMap< String , String >();
        this.chatRoomType = "";
    }

    public Map< String , String > getChatImage() { return chatImage; }

    public void setChatImage( Map<String, String> chatImage ) { this.chatImage = chatImage; }

    public String getChatRoomType() { return chatRoomType; }

    public void setChatRoomType( String chatRoomType ) { this.chatRoomType = chatRoomType; }
}
