package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tubelinesConfigCType", propOrder = { "tubelineMappings", "tubelineDefinitions", "any" })
public class Tubelines
{
    @XmlElement(name = "tubeline-mapping")
    protected List<TubelineMapping> tubelineMappings;
    @XmlElement(name = "tubeline")
    protected List<TubelineDefinition> tubelineDefinitions;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "default")
    @XmlSchemaType(name = "anyURI")
    protected String _default;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public Tubelines() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<TubelineMapping> getTubelineMappings() {
        if (this.tubelineMappings == null) {
            this.tubelineMappings = new ArrayList<TubelineMapping>();
        }
        return this.tubelineMappings;
    }
    
    public List<TubelineDefinition> getTubelineDefinitions() {
        if (this.tubelineDefinitions == null) {
            this.tubelineDefinitions = new ArrayList<TubelineDefinition>();
        }
        return this.tubelineDefinitions;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getDefault() {
        return this._default;
    }
    
    public void setDefault(final String value) {
        this._default = value;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}
