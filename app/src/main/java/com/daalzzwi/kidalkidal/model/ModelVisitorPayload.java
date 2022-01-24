package com.daalzzwi.kidalkidal.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ModelVisitorPayload implements Serializable {

    private Map< String , String > visitorImage;
    private String visitorRoomType;

    public ModelVisitorPayload() {

        this.visitorImage = new HashMap< String , String >();
        this.visitorRoomType = "";
    }

    public Map< String , String > getVisitorImage() { return visitorImage; }

    public void setVisitorImage( Map< String , String > visitorImage ) { this.visitorImage = visitorImage; }

    public String getVisitorRoomType() { return visitorRoomType; }

    public void setVisitorRoomType( String visitorRoomType ) { this.visitorRoomType = visitorRoomType; }
}
