package com.sun.research.ws.wadl;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "doc", "resource", "any" })
@XmlRootElement(name = "resources")
public class Resources
{
    protected List<Doc> doc;
    @XmlElement(required = true)
    protected List<Resource> resource;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "base")
    @XmlSchemaType(name = "anyURI")
    protected String base;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Resources() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }
    
    public List<Resource> getResource() {
        if (this.resource == null) {
            this.resource = new ArrayList<Resource>();
        }
        return this.resource;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getBase() {
        return this.base;
    }
    
    public void setBase(final String value) {
        this.base = value;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
