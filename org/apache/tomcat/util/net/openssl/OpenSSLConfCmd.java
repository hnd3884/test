package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;

public class OpenSSLConfCmd implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    
    public OpenSSLConfCmd() {
        this.name = null;
        this.value = null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}
