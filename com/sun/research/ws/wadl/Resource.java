package com.sun.research.ws.wadl;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "doc", "param", "methodOrResource", "any" })
@XmlRootElement(name = "resource")
public class Resource
{
    protected List<Doc> doc;
    protected List<Param> param;
    @XmlElements({ @XmlElement(name = "method", type = Method.class), @XmlElement(name = "resource", type = Resource.class) })
    protected List<Object> methodOrResource;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "type")
    protected List<String> type;
    @XmlAttribute(name = "queryType")
    protected String queryType;
    @XmlAttribute(name = "path")
    protected String path;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Resource() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }
    
    public List<Param> getParam() {
        if (this.param == null) {
            this.param = new ArrayList<Param>();
        }
        return this.param;
    }
    
    public List<Object> getMethodOrResource() {
        if (this.methodOrResource == null) {
            this.methodOrResource = new ArrayList<Object>();
        }
        return this.methodOrResource;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String value) {
        this.id = value;
    }
    
    public List<String> getType() {
        if (this.type == null) {
            this.type = new ArrayList<String>();
        }
        return this.type;
    }
    
    public String getQueryType() {
        if (this.queryType == null) {
            return "application/x-www-form-urlencoded";
        }
        return this.queryType;
    }
    
    public void setQueryType(final String value) {
        this.queryType = value;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String value) {
        this.path = value;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
