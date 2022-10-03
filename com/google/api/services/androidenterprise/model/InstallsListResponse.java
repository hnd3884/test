package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class InstallsListResponse extends GenericJson
{
    @Key
    private List<Install> install;
    
    public List<Install> getInstall() {
        return this.install;
    }
    
    public InstallsListResponse setInstall(final List<Install> install) {
        this.install = install;
        return this;
    }
    
    public InstallsListResponse set(final String fieldName, final Object value) {
        return (InstallsListResponse)super.set(fieldName, value);
    }
    
    public InstallsListResponse clone() {
        return (InstallsListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)Install.class);
    }
}
