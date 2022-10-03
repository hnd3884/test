package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.soap.Addressing;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "addressing")
public class XmlAddressing implements Addressing
{
    @XmlAttribute(name = "enabled")
    protected Boolean enabled;
    @XmlAttribute(name = "required")
    protected Boolean required;
    
    public Boolean getEnabled() {
        return this.enabled();
    }
    
    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Boolean getRequired() {
        return this.required();
    }
    
    public void setRequired(final Boolean required) {
        this.required = required;
    }
    
    @Override
    public boolean enabled() {
        return Util.nullSafe(this.enabled, true);
    }
    
    @Override
    public boolean required() {
        return Util.nullSafe(this.required, false);
    }
    
    @Override
    public AddressingFeature.Responses responses() {
        return AddressingFeature.Responses.ALL;
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return Addressing.class;
    }
}
