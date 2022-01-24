package com.daalzzwi.kidalkidal.model;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ModelIntent implements Serializable {

    private String intentToActivity;
    private String intentFromActivity;
    private Object intentData1;
    private Object intentData2;
    private Object intentData3;

    public ModelIntent() {

        intentToActivity = "";
        intentFromActivity = "";
        intentData1 = null;
        intentData2 = null;
        intentData3 = null;
    }

    @NonNull
    @Override
    protected Object clone() {

        Object object = null;

        try{

            object = super.clone();
        } catch ( CloneNotSupportedException e ) {}

        return object;
    }

    public String getIntentToActivity() { return intentToActivity; }

    public void setIntentToActivity( String intentToActivity ) { this.intentToActivity = intentToActivity; }

    public String getIntentFromActivity() { return intentFromActivity; }

    public void setIntentFromActivity( String intentFromActivity ) { this.intentFromActivity = intentFromActivity; }

    public Object getIntentData1() { return intentData1; }

    public void setIntentData1( Object intentData1 ) { this.intentData1 = intentData1; }

    public Object getIntentData2() { return intentData2; }

    public void setIntentData2( Object intentData2 ) { this.intentData2 = intentData2; }

    public Object getIntentData3() { return intentData3; }

    public void setIntentData3( Object intentData3 ) { this.intentData3 = intentData3; }
}
