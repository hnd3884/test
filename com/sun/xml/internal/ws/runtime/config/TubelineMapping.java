package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tubelineMappingCType", propOrder = { "endpointRef", "tubelineRef", "any" })
public class TubelineMapping
{
    @XmlElement(name = "endpoint-ref", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String endpointRef;
    @XmlElement(name = "tubeline-ref", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String tubelineRef;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public TubelineMapping() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public String getEndpointRef() {
        return this.endpointRef;
    }
    
    public void setEndpointRef(final String value) {
        this.endpointRef = value;
    }
    
    public String getTubelineRef() {
        return this.tubelineRef;
    }
    
    public void setTubelineRef(final String value) {
        this.tubelineRef = value;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
