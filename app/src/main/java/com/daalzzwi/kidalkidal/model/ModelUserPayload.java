package com.daalzzwi.kidalkidal.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelUserPayload implements Serializable , Cloneable {

    private int code;
    private String msg;
    private ModelUser modelUser;
    private ArrayList<ModelUser> modelUsers;

    public ModelUserPayload() {

        this.code = 0;
        this.msg = "";
        this.modelUser = new ModelUser();
        this.modelUsers = new ArrayList<ModelUser>();
    }

    public void setModelUserPayload( int code , String msg , ModelUser modelUser ) {

        this.code = code;
        this.msg = msg;
        this.modelUser = modelUser;
    }
    public void setModelUsersPayload( int code , String msg , ArrayList<ModelUser> modelUsers ) {

        this.code = code;
        this.msg = msg;
        this.modelUsers = modelUsers;
    }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }

    public void setMsg( String msg ) { this.msg = msg; }

    public ModelUser getModelUser() { return modelUser; }

    public void setModelUser( ModelUser modelUser ) { this.modelUser = modelUser; }

    public ArrayList<ModelUser> getModelUsers() { return modelUsers; }

    public void setModelUsers( ArrayList<ModelUser> modelUsers ) { this.modelUsers = modelUsers; }
}
