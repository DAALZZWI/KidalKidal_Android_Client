package com.daalzzwi.kidalkidal.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelQrPayload implements Serializable {

    private int code;
    private String msg;
    private ModelQr modelQr;
    private ArrayList<ModelQr> modelQrs;

    public ModelQrPayload() {

        this.code = 0;
        this.msg = "";
        this.modelQr = new ModelQr();
        this.modelQrs = new ArrayList<ModelQr>();
    }

    public void setModelQrPayload( int code , String msg , ModelQr modelQr ) {

        this.code = code;
        this.msg = msg;
        this.modelQr = modelQr;
    }

    public void setModelQrsPayload( int code , String msg , ArrayList<ModelQr> modelQrs ) {

        this.code = code;
        this.msg = msg;
        this.modelQrs = modelQrs;
    }

    public int getCode() { return code; }

    public void setCode( int code ) { this.code = code; }

    public String getMsg() { return msg; }

    public void setMsg( String msg ) { this.msg = msg; }

    public ModelQr getModelQr() { return modelQr; }

    public void setModelQr( ModelQr modelQr ) { this.modelQr = modelQr; }

    public ArrayList<ModelQr> getModelQrs() { return modelQrs; }

    public void setModelQrs( ArrayList<ModelQr> modelQrs ) { this.modelQrs = modelQrs; }
}
