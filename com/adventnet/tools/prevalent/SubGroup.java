package com.adventnet.tools.prevalent;

import java.util.ArrayList;

public class SubGroup
{
    private String name;
    private ArrayList prop;
    private String type;
    private ArrayList limitProp;
    private String subName;
    
    public SubGroup() {
        this.name = null;
        this.prop = null;
        this.type = null;
        this.limitProp = null;
        this.subName = null;
        this.prop = new ArrayList();
        this.limitProp = new ArrayList();
    }
    
    public void setName(final String subName) {
        this.subName = subName;
    }
    
    public String getName() {
        return this.subName;
    }
    
    public void setProperty(final String name, final String value) {
        this.setProperty(name, value, null);
    }
    
    public void setProperty(final String name, final String value, final String limit) {
        this.prop.add(name);
        this.prop.add(value);
        if (limit != null) {
            this.prop.add(limit);
        }
    }
    
    public ArrayList getProperties() {
        return this.prop;
    }
    
    public void setLimitProperty(final String name, final String value) {
        this.setLimitProperty(name, value, null);
    }
    
    public void setLimitProperty(final String name, final String value, final String limit) {
        this.limitProp.add(name);
        this.limitProp.add(value);
        if (limit != null) {
            this.limitProp.add(limit);
        }
    }
    
    public ArrayList getLimitProperties() {
        return this.limitProp;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(" SubGroup Name: " + this.name);
        buf.append(" SubGroup Properties: " + this.prop);
        buf.append(" SubGroup Properties: " + this.limitProp);
        return buf.toString();
    }
}
