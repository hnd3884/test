package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.ws.Service;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.ServiceMode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "service-mode")
public class XmlServiceMode implements ServiceMode
{
    @XmlAttribute(name = "value")
    protected String value;
    
    public String getValue() {
        if (this.value == null) {
            return "PAYLOAD";
        }
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public Service.Mode value() {
        return Service.Mode.valueOf(Util.nullSafe(this.value, "PAYLOAD"));
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return ServiceMode.class;
    }
}
