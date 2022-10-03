package org.apache.commons.validator;

import java.io.Serializable;

public class Var implements Cloneable, Serializable
{
    public static final String JSTYPE_INT = "int";
    public static final String JSTYPE_STRING = "string";
    public static final String JSTYPE_REGEXP = "regexp";
    private String name;
    private String value;
    private String jsType;
    private boolean resource;
    private String bundle;
    
    public Var() {
        this.name = null;
        this.value = null;
        this.jsType = null;
        this.resource = false;
        this.bundle = null;
    }
    
    public Var(final String name, final String value, final String jsType) {
        this.name = null;
        this.value = null;
        this.jsType = null;
        this.resource = false;
        this.bundle = null;
        this.name = name;
        this.value = value;
        this.jsType = jsType;
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
    
    public boolean isResource() {
        return this.resource;
    }
    
    public void setResource(final boolean resource) {
        this.resource = resource;
    }
    
    public String getBundle() {
        return this.bundle;
    }
    
    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }
    
    public String getJsType() {
        return this.jsType;
    }
    
    public void setJsType(final String jsType) {
        this.jsType = jsType;
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
        results.append("Var: name=");
        results.append(this.name);
        results.append("  value=");
        results.append(this.value);
        results.append("  resource=");
        results.append(this.resource);
        if (this.resource) {
            results.append("  bundle=");
            results.append(this.bundle);
        }
        results.append("  jsType=");
        results.append(this.jsType);
        results.append("\n");
        return results.toString();
    }
}
