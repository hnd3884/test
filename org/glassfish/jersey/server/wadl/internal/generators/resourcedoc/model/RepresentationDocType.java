package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "representationDoc", propOrder = {})
public class RepresentationDocType
{
    @XmlAttribute
    private QName element;
    private String example;
    @XmlAttribute
    private Long status;
    @XmlAttribute
    private String mediaType;
    private String doc;
    
    public QName getElement() {
        return this.element;
    }
    
    public void setElement(final QName element) {
        this.element = element;
    }
    
    public String getExample() {
        return this.example;
    }
    
    public void setExample(final String example) {
        this.example = example;
    }
    
    public Long getStatus() {
        return this.status;
    }
    
    public void setStatus(final Long status) {
        this.status = status;
    }
    
    public String getMediaType() {
        return this.mediaType;
    }
    
    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }
    
    public String getDoc() {
        return this.doc;
    }
    
    public void setDoc(final String doc) {
        this.doc = doc;
    }
}
