package com.adventnet.tools.prevalent;

import java.util.ArrayList;

public class Group
{
    private String name;
    private ArrayList subGroups;
    private String type;
    private String defaultSubGroupName;
    private ArrayList subGroupNames;
    
    public Group(final String type) {
        this.name = null;
        this.subGroups = null;
        this.type = null;
        this.defaultSubGroupName = null;
        this.subGroupNames = null;
        this.type = type;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setDefaultSubGroupName(final String name) {
        if (this.subGroupNames == null) {
            this.subGroupNames = new ArrayList();
        }
        this.subGroupNames.add(name);
        this.defaultSubGroupName = name;
    }
    
    public String getDefaultSubGroupName() {
        return this.defaultSubGroupName;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void addSubGroup(final SubGroup sgroup) {
        if (this.subGroups == null) {
            this.subGroups = new ArrayList();
        }
        this.subGroups.add(sgroup);
    }
    
    public ArrayList getSubGroups() {
        return this.subGroups;
    }
    
    public SubGroup getSubGroup(final String name) {
        if (this.subGroups == null) {
            return null;
        }
        for (int size = this.subGroups.size(), i = 0; i < size; ++i) {
            final SubGroup sgrp = this.subGroups.get(i);
            final String grpName = sgrp.getName();
            if (grpName.equals(name)) {
                return sgrp;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(" Group Type: " + this.type);
        buf.append(" Group Name: " + this.name);
        for (int size = this.subGroups.size(), i = 0; i < size; ++i) {
            System.out.println("The sub group:" + this.subGroups.get(i));
        }
        return buf.toString();
    }
    
    public void addSubGroupName(final String name) {
        if (this.subGroupNames == null) {
            this.subGroupNames = new ArrayList();
        }
        if (!this.subGroupNames.contains(name)) {
            this.subGroupNames.add(name);
        }
    }
    
    public ArrayList getSelectedSubGroups() {
        return this.subGroupNames;
    }
}
