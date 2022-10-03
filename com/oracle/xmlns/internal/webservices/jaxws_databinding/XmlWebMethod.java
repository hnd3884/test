package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.jws.WebMethod;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-method")
public class XmlWebMethod implements WebMethod
{
    @XmlAttribute(name = "action")
    protected String action;
    @XmlAttribute(name = "exclude")
    protected Boolean exclude;
    @XmlAttribute(name = "operation-name")
    protected String operationName;
    
    public String getAction() {
        if (this.action == null) {
            return "";
        }
        return this.action;
    }
    
    public void setAction(final String value) {
        this.action = value;
    }
    
    public boolean isExclude() {
        return this.exclude != null && this.exclude;
    }
    
    public void setExclude(final Boolean value) {
        this.exclude = value;
    }
    
    public String getOperationName() {
        if (this.operationName == null) {
            return "";
        }
        return this.operationName;
    }
    
    public void setOperationName(final String value) {
        this.operationName = value;
    }
    
    @Override
    public String operationName() {
        return Util.nullSafe(this.operationName);
    }
    
    @Override
    public String action() {
        return Util.nullSafe(this.action);
    }
    
    @Override
    public boolean exclude() {
        return Util.nullSafe(this.exclude, false);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return WebMethod.class;
    }
}
