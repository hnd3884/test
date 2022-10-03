package com.sun.research.ws.wadl;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import java.util.Map;
import javax.xml.namespace.QName;
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
@XmlType(name = "", propOrder = { "doc", "param", "any" })
@XmlRootElement(name = "representation")
public class Representation
{
    protected List<Doc> doc;
    protected List<Param> param;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "element")
    protected QName element;
    @XmlAttribute(name = "mediaType")
    protected String mediaType;
    @XmlAttribute(name = "href")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "profile")
    protected List<String> profile;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Representation() {
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
    
    public QName getElement() {
        return this.element;
    }
    
    public void setElement(final QName value) {
        this.element = value;
    }
    
    public String getMediaType() {
        return this.mediaType;
    }
    
    public void setMediaType(final String value) {
        this.mediaType = value;
    }
    
    public String getHref() {
        return this.href;
    }
    
    public void setHref(final String value) {
        this.href = value;
    }
    
    public List<String> getProfile() {
        if (this.profile == null) {
            this.profile = new ArrayList<String>();
        }
        return this.profile;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
