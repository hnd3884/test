package com.adventnet.iam.security;

public class HttpCookie implements Cloneable
{
    String name;
    private String value;
    private String domain;
    private int maxAge;
    private String path;
    private String priority;
    private boolean secure;
    private boolean httpOnly;
    private String sameSite;
    boolean encloseValueWithQuotes;
    
    public HttpCookie(final String name, final String value) {
        this.name = null;
        this.value = null;
        this.domain = null;
        this.maxAge = -1;
        this.path = null;
        this.priority = null;
        this.secure = false;
        this.httpOnly = false;
        this.sameSite = null;
        this.encloseValueWithQuotes = false;
        this.name = name;
        this.value = value;
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain;
    }
    
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }
    
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    public boolean isHttpOnly() {
        return this.httpOnly;
    }
    
    public void setSameSite(final String flagValue) {
        this.sameSite = flagValue;
    }
    
    public String getSameSiteValue() {
        return this.sameSite;
    }
    
    public void setPriority(final PRIORITY priority) {
        this.priority = priority.name().toLowerCase();
    }
    
    public String generateCookie() {
        final StringBuilder header = new StringBuilder();
        header.append(this.name);
        header.append('=');
        if (this.value != null && this.value.length() > 0) {
            if (this.encloseValueWithQuotes) {
                header.append('\"');
                header.append(this.value);
                header.append('\"');
            }
            else {
                header.append(this.value);
            }
        }
        if (this.maxAge > -1) {
            header.append(";Max-Age=");
            header.append(this.maxAge);
        }
        if (this.domain != null && this.domain.length() > 0) {
            header.append(";domain=");
            header.append(this.domain);
        }
        if (this.path != null && this.path.length() > 0) {
            header.append(";path=");
            header.append(this.path);
        }
        if (this.getSameSiteValue() != null) {
            header.append(";SameSite=");
            header.append(this.getSameSiteValue());
        }
        if (this.isSecure()) {
            header.append(";Secure");
        }
        if (this.isHttpOnly()) {
            header.append(";HttpOnly");
        }
        if (this.priority != null && !"medium".equalsIgnoreCase(this.priority)) {
            header.append(";priority=");
            header.append(this.priority);
        }
        return header.toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public enum PRIORITY
    {
        HIGH, 
        MEDIUM, 
        LOW;
    }
    
    public enum SAMESITE
    {
        STRICT("Strict"), 
        LAX("Lax"), 
        NONE("None");
        
        private String value;
        
        private SAMESITE(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
