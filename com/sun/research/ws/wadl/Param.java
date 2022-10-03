package com.sun.research.ws.wadl;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "doc", "option", "link", "any" })
@XmlRootElement(name = "param")
public class Param
{
    protected List<Doc> doc;
    protected List<Option> option;
    protected Link link;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "href")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String name;
    @XmlAttribute(name = "style")
    protected ParamStyle style;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "type")
    protected QName type;
    @XmlAttribute(name = "default")
    protected String _default;
    @XmlAttribute(name = "required")
    protected Boolean required;
    @XmlAttribute(name = "repeating")
    protected Boolean repeating;
    @XmlAttribute(name = "fixed")
    protected String fixed;
    @XmlAttribute(name = "path")
    protected String path;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Param() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }
    
    public List<Option> getOption() {
        if (this.option == null) {
            this.option = new ArrayList<Option>();
        }
        return this.option;
    }
    
    public Link getLink() {
        return this.link;
    }
    
    public void setLink(final Link value) {
        this.link = value;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getHref() {
        return this.href;
    }
    
    public void setHref(final String value) {
        this.href = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public ParamStyle getStyle() {
        return this.style;
    }
    
    public void setStyle(final ParamStyle value) {
        this.style = value;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String value) {
        this.id = value;
    }
    
    public QName getType() {
        if (this.type == null) {
            return new QName("http://www.w3.org/2001/XMLSchema", "string", "xs");
        }
        return this.type;
    }
    
    public void setType(final QName value) {
        this.type = value;
    }
    
    public String getDefault() {
        return this._default;
    }
    
    public void setDefault(final String value) {
        this._default = value;
    }
    
    public boolean isRequired() {
        return this.required != null && this.required;
    }
    
    public void setRequired(final Boolean value) {
        this.required = value;
    }
    
    public boolean isRepeating() {
        return this.repeating != null && this.repeating;
    }
    
    public void setRepeating(final Boolean value) {
        this.repeating = value;
    }
    
    public String getFixed() {
        return this.fixed;
    }
    
    public void setFixed(final String value) {
        this.fixed = value;
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
