package org.apache.commons.validator;

import java.io.Serializable;

public class Msg implements Cloneable, Serializable
{
    protected String bundle;
    protected String key;
    protected String name;
    protected boolean resource;
    
    public Msg() {
        this.bundle = null;
        this.key = null;
        this.name = null;
        this.resource = true;
    }
    
    public String getBundle() {
        return this.bundle;
    }
    
    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public boolean isResource() {
        return this.resource;
    }
    
    public void setResource(final boolean resource) {
        this.resource = resource;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    public String toString() {
        final StringBuffer results = new StringBuffer();
        results.append("Msg: name=");
        results.append(this.name);
        results.append("  key=");
        results.append(this.key);
        results.append("  resource=");
        results.append(this.resource);
        results.append("  bundle=");
        results.append(this.bundle);
        results.append("\n");
        return results.toString();
    }
}
