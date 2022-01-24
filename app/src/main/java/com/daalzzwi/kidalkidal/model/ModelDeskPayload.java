package com.daalzzwi.kidalkidal.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelDeskPayload implements Serializable {

    private int code;
    private String msg;
    private ModelDesk modelDesk;
    private ArrayList<ModelDesk> modelDesks;

    public ModelDeskPayload() {

        this.code = 0;
        this.msg = "";
        this.modelDesk = new ModelDesk();
        this.modelDesks = new ArrayList<ModelDesk>();
    }

    public void setModelDeskPayload( int code , String msg , ModelDesk modelDesk ) {

        this.code = code;
        this.msg = msg;
        this.modelDesk = modelDesk;
    }

    public void setModelDesksPayload( int code , String msg , ArrayList<ModelDesk> modelDesks ) {

        this.code = code;
        this.msg = msg;
        this.modelDesks = modelDesks;
    }

    public int getCode() { return code; }

    public void setCode( int code ) { this.code = code; }

    public String getMsg() { return msg; }

    public void setMsg( String msg ) { this.msg = msg; }

    public ModelDesk getModelDesk() { return modelDesk; }

    public void setModelDesk( ModelDesk modelDesk ) { this.modelDesk = modelDesk; }

    public ArrayList<ModelDesk> getModelDesks() { return modelDesks; }

    public void setModelDesks( ArrayList<ModelDesk> modelDesks ) { this.modelDesks = modelDesks; }

    @NonNull
    @Override
    protected Object clone() {

        Object object = null;

        try{

            object = super.clone();
        } catch ( CloneNotSupportedException e ) {}

        return object;
    }
}
