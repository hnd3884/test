package com.sun.research.ws.wadl;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "doc", "param", "representation", "any" })
@XmlRootElement(name = "response")
public class Response
{
    protected List<Doc> doc;
    protected List<Param> param;
    protected List<Representation> representation;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "status")
    protected List<Long> status;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Response() {
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
    
    public List<Representation> getRepresentation() {
        if (this.representation == null) {
            this.representation = new ArrayList<Representation>();
        }
        return this.representation;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public List<Long> getStatus() {
        if (this.status == null) {
            this.status = new ArrayList<Long>();
        }
        return this.status;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
