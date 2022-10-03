package com.sun.xml.internal.ws.fault;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;

class ReasonType
{
    @XmlElements({ @XmlElement(name = "Text", namespace = "http://www.w3.org/2003/05/soap-envelope", type = TextType.class) })
    private final List<TextType> text;
    
    ReasonType() {
        this.text = new ArrayList<TextType>();
    }
    
    ReasonType(final String txt) {
        (this.text = new ArrayList<TextType>()).add(new TextType(txt));
    }
    
    List<TextType> texts() {
        return this.text;
    }
}
