package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "methodAnnotation", "javaParams" })
@XmlRootElement(name = "java-method")
public class JavaMethod
{
    @XmlElementRefs({ @XmlElementRef(name = "web-endpoint", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlWebEndpoint.class, required = false), @XmlElementRef(name = "oneway", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlOneway.class, required = false), @XmlElementRef(name = "action", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlAction.class, required = false), @XmlElementRef(name = "soap-binding", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlSOAPBinding.class, required = false), @XmlElementRef(name = "web-result", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlWebResult.class, required = false), @XmlElementRef(name = "web-method", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlWebMethod.class, required = false) })
    @XmlAnyElement
    protected List<Object> methodAnnotation;
    @XmlElement(name = "java-params")
    protected JavaParams javaParams;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes;
    
    public JavaMethod() {
        this.otherAttributes = new HashMap<QName, String>();
    }
    
    public List<Object> getMethodAnnotation() {
        if (this.methodAnnotation == null) {
            this.methodAnnotation = new ArrayList<Object>();
        }
        return this.methodAnnotation;
    }
    
    public JavaParams getJavaParams() {
        return this.javaParams;
    }
    
    public void setJavaParams(final JavaParams value) {
        this.javaParams = value;
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
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "javaParam" })
    public static class JavaParams
    {
        @XmlElement(name = "java-param", required = true)
        protected List<JavaParam> javaParam;
        
        public List<JavaParam> getJavaParam() {
            if (this.javaParam == null) {
                this.javaParam = new ArrayList<JavaParam>();
            }
            return this.javaParam;
        }
    }
}
