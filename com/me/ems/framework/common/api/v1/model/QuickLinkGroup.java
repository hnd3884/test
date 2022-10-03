package com.me.ems.framework.common.api.v1.model;

import java.util.HashMap;
import java.util.List;

public class QuickLinkGroup
{
    private String category;
    private List<HashMap<String, Object>> links;
    private String label;
    private String moreUrl;
    
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(final String category) {
        this.category = category;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String getMoreUrl() {
        return this.moreUrl;
    }
    
    public void setMoreUrl(final String moreUrl) {
        this.moreUrl = moreUrl;
    }
    
    public List<HashMap<String, Object>> getLinks() {
        return this.links;
    }
    
    public void setLinks(final List<HashMap<String, Object>> links) {
        this.links = links;
    }
}
