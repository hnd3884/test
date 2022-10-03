package com.sun.research.ws.wadl;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "doc", "grammars", "resources", "resourceTypeOrMethodOrRepresentation", "any" })
@XmlRootElement(name = "application")
public class Application
{
    protected List<Doc> doc;
    protected Grammars grammars;
    protected List<Resources> resources;
    @XmlElements({ @XmlElement(name = "resource_type", type = ResourceType.class), @XmlElement(name = "method", type = Method.class), @XmlElement(name = "representation", type = Representation.class), @XmlElement(name = "param", type = Param.class) })
    protected List<Object> resourceTypeOrMethodOrRepresentation;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    
    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }
    
    public Grammars getGrammars() {
        return this.grammars;
    }
    
    public void setGrammars(final Grammars value) {
        this.grammars = value;
    }
    
    public List<Resources> getResources() {
        if (this.resources == null) {
            this.resources = new ArrayList<Resources>();
        }
        return this.resources;
    }
    
    public List<Object> getResourceTypeOrMethodOrRepresentation() {
        if (this.resourceTypeOrMethodOrRepresentation == null) {
            this.resourceTypeOrMethodOrRepresentation = new ArrayList<Object>();
        }
        return this.resourceTypeOrMethodOrRepresentation;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
}
