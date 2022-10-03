package org.apache.commons.validator;

import java.io.Serializable;

public class Arg implements Cloneable, Serializable
{
    protected String bundle;
    protected String key;
    protected String name;
    protected int position;
    protected boolean resource;
    
    public Arg() {
        this.bundle = null;
        this.key = null;
        this.name = null;
        this.position = -1;
        this.resource = true;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    public String getBundle() {
        return this.bundle;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public boolean isResource() {
        return this.resource;
    }
    
    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    public void setResource(final boolean resource) {
        this.resource = resource;
    }
    
    public String toString() {
        final StringBuffer results = new StringBuffer();
        results.append("Arg: name=");
        results.append(this.name);
        results.append("  key=");
        results.append(this.key);
        results.append("  position=");
        results.append(this.position);
        results.append("  bundle=");
        results.append(this.bundle);
        results.append("  resource=");
        results.append(this.resource);
        results.append("\n");
        return results.toString();
    }
}
