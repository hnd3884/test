package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class BookmarksBarEnabled extends GenericJson
{
    @Key
    private String bookmarksBarEnabledMode;
    @Key
    private PolicyOptions policyOptions;
    
    public String getBookmarksBarEnabledMode() {
        return this.bookmarksBarEnabledMode;
    }
    
    public BookmarksBarEnabled setBookmarksBarEnabledMode(final String bookmarksBarEnabledMode) {
        this.bookmarksBarEnabledMode = bookmarksBarEnabledMode;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public BookmarksBarEnabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public BookmarksBarEnabled set(final String s, final Object o) {
        return (BookmarksBarEnabled)super.set(s, o);
    }
    
    public BookmarksBarEnabled clone() {
        return (BookmarksBarEnabled)super.clone();
    }
}
