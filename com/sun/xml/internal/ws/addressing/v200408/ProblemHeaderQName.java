package com.sun.xml.internal.ws.addressing.v200408;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProblemHeaderQName", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
public class ProblemHeaderQName
{
    @XmlValue
    private QName value;
    
    public ProblemHeaderQName() {
    }
    
    public ProblemHeaderQName(final QName name) {
        this.value = name;
    }
}
