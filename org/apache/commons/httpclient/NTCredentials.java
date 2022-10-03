package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.LangUtils;

public class NTCredentials extends UsernamePasswordCredentials
{
    private String domain;
    private String host;
    
    public NTCredentials() {
    }
    
    public NTCredentials(final String userName, final String password, final String host, final String domain) {
        super(userName, password);
        if (domain == null) {
            throw new IllegalArgumentException("Domain may not be null");
        }
        this.domain = domain;
        if (host == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        this.host = host;
    }
    
    public void setDomain(final String domain) {
        if (domain == null) {
            throw new IllegalArgumentException("Domain may not be null");
        }
        this.domain = domain;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void setHost(final String host) {
        if (host == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        this.host = host;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String toString() {
        final StringBuffer sbResult = new StringBuffer(super.toString());
        sbResult.append("@");
        sbResult.append(this.host);
        sbResult.append(".");
        sbResult.append(this.domain);
        return sbResult.toString();
    }
    
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.domain);
        return hash;
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (super.equals(o) && o instanceof NTCredentials) {
            final NTCredentials that = (NTCredentials)o;
            return LangUtils.equals(this.domain, that.domain) && LangUtils.equals(this.host, that.host);
        }
        return false;
    }
}
