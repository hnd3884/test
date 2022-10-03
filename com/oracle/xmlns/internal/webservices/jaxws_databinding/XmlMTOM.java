package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.soap.MTOM;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "mtom")
public class XmlMTOM implements MTOM
{
    @XmlAttribute(name = "enabled")
    protected Boolean enabled;
    @XmlAttribute(name = "threshold")
    protected Integer threshold;
    
    public boolean isEnabled() {
        return this.enabled == null || this.enabled;
    }
    
    public void setEnabled(final Boolean value) {
        this.enabled = value;
    }
    
    public int getThreshold() {
        if (this.threshold == null) {
            return 0;
        }
        return this.threshold;
    }
    
    public void setThreshold(final Integer value) {
        this.threshold = value;
    }
    
    @Override
    public boolean enabled() {
        return Util.nullSafe(this.enabled, Boolean.TRUE);
    }
    
    @Override
    public int threshold() {
        return Util.nullSafe(this.threshold, 0);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return MTOM.class;
    }
}
