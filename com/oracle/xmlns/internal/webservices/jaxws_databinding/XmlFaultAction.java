package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.FaultAction;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "fault-action")
public class XmlFaultAction implements FaultAction
{
    @XmlAttribute(name = "className", required = true)
    protected String className;
    @XmlAttribute(name = "value")
    protected String value;
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String value) {
        this.className = value;
    }
    
    public String getValue() {
        return Util.nullSafe(this.value);
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public Class<? extends Exception> className() {
        return (Class<? extends Exception>)Util.findClass(this.className);
    }
    
    @Override
    public String value() {
        return Util.nullSafe(this.value);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return FaultAction.class;
    }
}
