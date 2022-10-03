package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class EditBookmarksDisabled extends GenericJson
{
    @Key
    private Boolean editBookmarksDisabled;
    @Key
    private PolicyOptions policyOptions;
    
    public Boolean getEditBookmarksDisabled() {
        return this.editBookmarksDisabled;
    }
    
    public EditBookmarksDisabled setEditBookmarksDisabled(final Boolean editBookmarksDisabled) {
        this.editBookmarksDisabled = editBookmarksDisabled;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public EditBookmarksDisabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public EditBookmarksDisabled set(final String s, final Object o) {
        return (EditBookmarksDisabled)super.set(s, o);
    }
    
    public EditBookmarksDisabled clone() {
        return (EditBookmarksDisabled)super.clone();
    }
}
