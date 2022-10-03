package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.LangUtils;
import java.io.Serializable;

public class NameValuePair implements Serializable
{
    private String name;
    private String value;
    
    public NameValuePair() {
        this(null, null);
    }
    
    public NameValuePair(final String name, final String value) {
        this.name = null;
        this.value = null;
        this.name = name;
        this.value = value;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String toString() {
        return "name=" + this.name + ", " + "value=" + this.value;
    }
    
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof NameValuePair) {
            final NameValuePair that = (NameValuePair)object;
            return LangUtils.equals(this.name, that.name) && LangUtils.equals(this.value, that.value);
        }
        return false;
    }
    
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.name);
        hash = LangUtils.hashCode(hash, this.value);
        return hash;
    }
}
