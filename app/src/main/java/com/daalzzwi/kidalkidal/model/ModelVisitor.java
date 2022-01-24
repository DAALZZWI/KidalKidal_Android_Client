package com.daalzzwi.kidalkidal.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ModelVisitor implements Serializable {

    private String visitorType;
    private String visitorName;
    private Bitmap visitorImage;
    private String visitorNumber;

    public ModelVisitor() {

        visitorType = "";
        visitorName = "";
        visitorImage = null;
        visitorNumber = "";
    }

    public String getVisitorType() { return visitorType; }

    public void setVisitorType( String visitorType ) { this.visitorType = visitorType; }

    public String getVisitorName() { return visitorName; }

    public void setVisitorName( String visitorName ) { this.visitorName = visitorName; }

    public Bitmap getVisitorImage() { return visitorImage; }

    public void setVisitorImage( Bitmap visitorImage ) { this.visitorImage = visitorImage; }

    public String getVisitorNumber() { return visitorNumber; }

    public void setVisitorNumber( String visitorNumber ) { this.visitorNumber = visitorNumber; }
}
