package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.jws.soap.SOAPBinding;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "soap-binding")
public class XmlSOAPBinding implements SOAPBinding
{
    @XmlAttribute(name = "style")
    protected SoapBindingStyle style;
    @XmlAttribute(name = "use")
    protected SoapBindingUse use;
    @XmlAttribute(name = "parameter-style")
    protected SoapBindingParameterStyle parameterStyle;
    
    public SoapBindingStyle getStyle() {
        if (this.style == null) {
            return SoapBindingStyle.DOCUMENT;
        }
        return this.style;
    }
    
    public void setStyle(final SoapBindingStyle value) {
        this.style = value;
    }
    
    public SoapBindingUse getUse() {
        if (this.use == null) {
            return SoapBindingUse.LITERAL;
        }
        return this.use;
    }
    
    public void setUse(final SoapBindingUse value) {
        this.use = value;
    }
    
    public SoapBindingParameterStyle getParameterStyle() {
        if (this.parameterStyle == null) {
            return SoapBindingParameterStyle.WRAPPED;
        }
        return this.parameterStyle;
    }
    
    public void setParameterStyle(final SoapBindingParameterStyle value) {
        this.parameterStyle = value;
    }
    
    @Override
    public Style style() {
        return Util.nullSafe(this.style, Style.DOCUMENT);
    }
    
    @Override
    public Use use() {
        return Util.nullSafe(this.use, Use.LITERAL);
    }
    
    @Override
    public ParameterStyle parameterStyle() {
        return Util.nullSafe(this.parameterStyle, ParameterStyle.WRAPPED);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return SOAPBinding.class;
    }
}
