package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tubeFactoryListCType", propOrder = { "tubeFactoryConfigs", "any" })
public class TubeFactoryList
{
    @XmlElement(name = "tube-factory", required = true)
    protected List<TubeFactoryConfig> tubeFactoryConfigs;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public TubeFactoryList() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<TubeFactoryConfig> getTubeFactoryConfigs() {
        if (this.tubeFactoryConfigs == null) {
            this.tubeFactoryConfigs = new ArrayList<TubeFactoryConfig>();
        }
        return this.tubeFactoryConfigs;
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
