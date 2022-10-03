package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class GroupLicensesListResponse extends GenericJson
{
    @Key
    private List<GroupLicense> groupLicense;
    
    public List<GroupLicense> getGroupLicense() {
        return this.groupLicense;
    }
    
    public GroupLicensesListResponse setGroupLicense(final List<GroupLicense> groupLicense) {
        this.groupLicense = groupLicense;
        return this;
    }
    
    public GroupLicensesListResponse set(final String fieldName, final Object value) {
        return (GroupLicensesListResponse)super.set(fieldName, value);
    }
    
    public GroupLicensesListResponse clone() {
        return (GroupLicensesListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)GroupLicense.class);
    }
}
