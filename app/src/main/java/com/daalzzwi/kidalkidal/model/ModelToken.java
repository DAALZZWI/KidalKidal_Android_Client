package com.daalzzwi.kidalkidal.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ModelToken implements Serializable {

    private int tokenId;
    private String tokenAccess;
    private String tokenRefresh;
    private String tokenAccessExpirationDate;
    private String tokenRefreshExpirationDate;
    private Object tokenData1;

    public ModelToken() {

        this.tokenId = 0;
        this.tokenAccess = "";
        this.tokenRefresh = "";
        this.tokenAccessExpirationDate = "";
        this.tokenRefreshExpirationDate = "";
        this.tokenData1 = null;
    }

    public int getTokenId() { return tokenId; }

    public void setTokenId( int tokenId ) { this.tokenId = tokenId; }

    public String getTokenAccess() { return tokenAccess; }

    public void setTokenAccess( String tokenAccess ) { this.tokenAccess = tokenAccess; }

    public String getTokenRefresh() { return tokenRefresh; }

    public void setTokenRefresh( String tokenRefresh ) { this.tokenRefresh = tokenRefresh; }

    public String getTokenAccessExpirationDate() { return tokenAccessExpirationDate; }

    public void setTokenAccessExpirationDate( String tokenAccessExpirationDate ) { this.tokenAccessExpirationDate = tokenAccessExpirationDate; }

    public String getTokenRefreshExpirationDate() { return tokenRefreshExpirationDate; }

    public void setTokenRefreshExpirationDate( String tokenRefreshExpirationDate ) { this.tokenRefreshExpirationDate = tokenRefreshExpirationDate; }

    public Object getTokenData1() { return tokenData1; }

    public void setTokenData1( Object tokenData1 ) { this.tokenData1 = tokenData1; }
}
