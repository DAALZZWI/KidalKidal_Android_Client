package com.daalzzwi.kidalkidal.model;

import java.io.Serializable;

public class ModelCompany implements Serializable {

    private static final long serialVersionUID = 403162008526742577L;

    private String companyId;
    private String companyName;

    public ModelCompany() {

        this.companyId = "";
        this.companyName = "";
    }

    public String getCompanyId() { return companyId; }

    public void setCompanyId( String companyId ) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }

    public void setCompanyName( String companyName ) { this.companyName = companyName; }
}
