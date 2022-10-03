package com.adventnet.tools.prevalent;

import java.util.ArrayList;
import java.io.Serializable;

public class Component implements Serializable
{
    private static final long serialVersionUID = 3487495895819000L;
    private String name;
    private ArrayList prop;
    private ArrayList limitProp;
    private ArrayList groups;
    
    public Component() {
        this.name = null;
        this.prop = null;
        this.limitProp = null;
        this.groups = null;
        this.prop = new ArrayList();
        this.limitProp = new ArrayList();
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setProperty(final String name, final String value) {
        this.setProperty(name, value, null);
    }
    
    public void setProperty(final String name, final String value, final String state) {
        this.prop.add(name);
        this.prop.add(value);
        if (state != null) {
            this.prop.add(state);
        }
    }
    
    public void setLimitProperty(final String name, final String value) {
        this.setLimitProperty(name, value, null);
    }
    
    public void setLimitProperty(final String name, final String value, final String state) {
        this.limitProp.add(name);
        this.limitProp.add(value);
        if (state != null) {
            this.limitProp.add(state);
        }
    }
    
    public void addGroup(final Group group) {
        if (this.groups == null) {
            this.groups = new ArrayList();
        }
        this.groups.add(group);
    }
    
    public ArrayList getGroups() {
        return this.groups;
    }
    
    public ArrayList getProperties() {
        return this.prop;
    }
    
    public ArrayList getLimitProperties() {
        return this.limitProp;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(" Component Name: " + this.name);
        buf.append(" Component Properties: " + this.prop);
        buf.append(" Component Groups: " + this.groups);
        return buf.toString();
    }
}
