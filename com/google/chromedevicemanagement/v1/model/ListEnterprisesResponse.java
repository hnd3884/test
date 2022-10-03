package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ListEnterprisesResponse extends GenericJson
{
    @Key
    private Enterprise enterprise;
    
    public Enterprise getEnterprise() {
        return this.enterprise;
    }
    
    public ListEnterprisesResponse setEnterprise(final Enterprise enterprise) {
        this.enterprise = enterprise;
        return this;
    }
    
    public ListEnterprisesResponse set(final String s, final Object o) {
        return (ListEnterprisesResponse)super.set(s, o);
    }
    
    public ListEnterprisesResponse clone() {
        return (ListEnterprisesResponse)super.clone();
    }
}
