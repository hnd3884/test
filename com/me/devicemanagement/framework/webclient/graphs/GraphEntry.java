package com.me.devicemanagement.framework.webclient.graphs;

import java.io.Serializable;

public class GraphEntry implements Serializable
{
    private String name;
    private String label;
    private Long value;
    private String actionLink;
    private String color;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getActionLink() {
        return this.actionLink;
    }
    
    public void setActionLink(final String actionLink) {
        this.actionLink = actionLink;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public Long getValue() {
        return this.value;
    }
    
    public void setValue(final Long value) {
        this.value = value;
    }
    
    public String getColor() {
        return this.color;
    }
    
    public void setColor(final String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return "GraphEntry{name=" + this.name + ", label=" + this.label + ", value=" + this.value + ", actionLink=" + this.actionLink + ", color=" + this.color + '}';
    }
}
