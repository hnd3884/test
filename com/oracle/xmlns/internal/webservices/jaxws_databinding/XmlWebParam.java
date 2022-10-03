package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.jws.WebParam;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-param")
public class XmlWebParam implements WebParam
{
    @XmlAttribute(name = "header")
    protected Boolean header;
    @XmlAttribute(name = "mode")
    protected WebParamMode mode;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "part-name")
    protected String partName;
    @XmlAttribute(name = "target-namespace")
    protected String targetNamespace;
    
    public boolean isHeader() {
        return this.header != null && this.header;
    }
    
    public void setHeader(final Boolean value) {
        this.header = value;
    }
    
    public WebParamMode getMode() {
        if (this.mode == null) {
            return WebParamMode.IN;
        }
        return this.mode;
    }
    
    public void setMode(final WebParamMode value) {
        this.mode = value;
    }
    
    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public String getPartName() {
        if (this.partName == null) {
            return "";
        }
        return this.partName;
    }
    
    public void setPartName(final String value) {
        this.partName = value;
    }
    
    public String getTargetNamespace() {
        if (this.targetNamespace == null) {
            return "";
        }
        return this.targetNamespace;
    }
    
    public void setTargetNamespace(final String value) {
        this.targetNamespace = value;
    }
    
    @Override
    public String name() {
        return Util.nullSafe(this.name);
    }
    
    @Override
    public String partName() {
        return Util.nullSafe(this.partName);
    }
    
    @Override
    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }
    
    @Override
    public Mode mode() {
        return Util.nullSafe(this.mode, Mode.IN);
    }
    
    @Override
    public boolean header() {
        return Util.nullSafe(this.header, false);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return WebParam.class;
    }
}
