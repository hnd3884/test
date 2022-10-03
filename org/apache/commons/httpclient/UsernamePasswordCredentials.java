package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.LangUtils;

public class UsernamePasswordCredentials implements Credentials
{
    private String userName;
    private String password;
    
    public UsernamePasswordCredentials() {
    }
    
    public UsernamePasswordCredentials(final String usernamePassword) {
        if (usernamePassword == null) {
            throw new IllegalArgumentException("Username:password string may not be null");
        }
        final int atColon = usernamePassword.indexOf(58);
        if (atColon >= 0) {
            this.userName = usernamePassword.substring(0, atColon);
            this.password = usernamePassword.substring(atColon + 1);
        }
        else {
            this.userName = usernamePassword;
        }
    }
    
    public UsernamePasswordCredentials(final String userName, final String password) {
        if (userName == null) {
            throw new IllegalArgumentException("Username may not be null");
        }
        this.userName = userName;
        this.password = password;
    }
    
    public void setUserName(final String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("Username may not be null");
        }
        this.userName = userName;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append(this.userName);
        result.append(":");
        result.append((this.password == null) ? "null" : this.password);
        return result.toString();
    }
    
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.userName);
        hash = LangUtils.hashCode(hash, this.password);
        return hash;
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (this.getClass().equals(o.getClass())) {
            final UsernamePasswordCredentials that = (UsernamePasswordCredentials)o;
            if (LangUtils.equals(this.userName, that.userName) && LangUtils.equals(this.password, that.password)) {
                return true;
            }
        }
        return false;
    }
}
