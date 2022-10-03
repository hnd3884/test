package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.jws.HandlerChain;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "handler-chain")
public class XmlHandlerChain implements HandlerChain
{
    @XmlAttribute(name = "file")
    protected String file;
    
    public String getFile() {
        return this.file;
    }
    
    public void setFile(final String value) {
        this.file = value;
    }
    
    @Override
    public String file() {
        return Util.nullSafe(this.file);
    }
    
    @Override
    public String name() {
        return "";
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return HandlerChain.class;
    }
}
