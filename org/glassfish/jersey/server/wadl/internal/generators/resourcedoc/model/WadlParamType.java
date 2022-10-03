package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wadlParam", propOrder = {})
public class WadlParamType
{
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String style;
    @XmlAttribute
    private QName type;
    private String doc;
    
    public String getDoc() {
        return this.doc;
    }
    
    public void setDoc(final String commentText) {
        this.doc = commentText;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String paramName) {
        this.name = paramName;
    }
    
    public String getStyle() {
        return this.style;
    }
    
    public void setStyle(final String style) {
        this.style = style;
    }
    
    public QName getType() {
        return this.type;
    }
    
    public void setType(final QName type) {
        this.type = type;
    }
}
