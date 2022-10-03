package com.sun.xml.internal.ws.runtime.config;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tubelineDefinitionCType", propOrder = { "clientSide", "endpointSide", "any" })
public class TubelineDefinition
{
    @XmlElement(name = "client-side")
    protected TubeFactoryList clientSide;
    @XmlElement(name = "endpoint-side")
    protected TubeFactoryList endpointSide;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String name;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public TubelineDefinition() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public TubeFactoryList getClientSide() {
        return this.clientSide;
    }
    
    public void setClientSide(final TubeFactoryList value) {
        this.clientSide = value;
    }
    
    public TubeFactoryList getEndpointSide() {
        return this.endpointSide;
    }
    
    public void setEndpointSide(final TubeFactoryList value) {
        this.endpointSide = value;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
