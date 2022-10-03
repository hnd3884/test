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
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "doc", "request", "response", "any" })
@XmlRootElement(name = "method")
public class Method
{
    protected List<Doc> doc;
    protected Request request;
    protected List<Response> response;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "href")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Method() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }
    
    public Request getRequest() {
        return this.request;
    }
    
    public void setRequest(final Request value) {
        this.request = value;
    }
    
    public List<Response> getResponse() {
        if (this.response == null) {
            this.response = new ArrayList<Response>();
        }
        return this.response;
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
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public String getHref() {
        return this.href;
    }
    
    public void setHref(final String value) {
        this.href = value;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
