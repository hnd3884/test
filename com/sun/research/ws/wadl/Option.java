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
@XmlType(name = "", propOrder = { "doc", "any" })
@XmlRootElement(name = "option")
public class Option
{
    protected List<Doc> doc;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "value", required = true)
    protected String value;
    @XmlAttribute(name = "mediaType")
    protected String mediaType;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Option() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getMediaType() {
        return this.mediaType;
    }
    
    public void setMediaType(final String value) {
        this.mediaType = value;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
