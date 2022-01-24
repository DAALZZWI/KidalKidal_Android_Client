package com.daalzzwi.kidalkidal.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelTokenPayload implements Serializable {

    private int code;
    private String msg;
    private ModelToken modelToken;
    private ArrayList< ModelToken > modelTokens;

    public ModelTokenPayload() {

        this.code = 0;
        this.msg = "";
        this.modelToken = new ModelToken();
        this.modelTokens = new ArrayList< ModelToken >();
    }

    public void setModelTokenPayload( int code , String msg , ModelToken modelToken ) {

        this.code = code;
        this.msg = msg;
        this.modelToken = modelToken;
    }

    public void setModelTokensPayload( int code , String msg , ArrayList<ModelToken> modelTokens ) {

        this.code = code;
        this.msg = msg;
        this.modelTokens = modelTokens;
    }

    public int getCode() { return code; }

    public void setCode( int code ) { this.code = code; }

    public String getMsg() { return msg; }

    public void setMsg( String msg ) { this.msg = msg; }

    public ModelToken getModelToken() { return modelToken; }

    public void setModelToken( ModelToken modelToken ) { this.modelToken = modelToken; }

    public ArrayList< ModelToken > getModelTokens() { return modelTokens; }

    public void setModelTokens( ArrayList<ModelToken> modelTokens ) { this.modelTokens = modelTokens; }
}
