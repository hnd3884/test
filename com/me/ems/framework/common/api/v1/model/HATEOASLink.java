package com.me.ems.framework.common.api.v1.model;

import java.util.Map;

public class HATEOASLink
{
    private String rel;
    private String href;
    private String type;
    private Map header;
    
    public String getRel() {
        return this.rel;
    }
    
    public void setRel(final String rel) {
        this.rel = rel;
    }
    
    public String getHref() {
        return this.href;
    }
    
    public void setHref(final String href) {
        this.href = href;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public Map getHeader() {
        return this.header;
    }
    
    public void setHeader(final Map header) {
        this.header = header;
    }
}
