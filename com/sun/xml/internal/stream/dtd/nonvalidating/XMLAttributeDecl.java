package com.sun.xml.internal.stream.dtd.nonvalidating;

import com.sun.org.apache.xerces.internal.xni.QName;

public class XMLAttributeDecl
{
    public final QName name;
    public final XMLSimpleType simpleType;
    public boolean optional;
    
    public XMLAttributeDecl() {
        this.name = new QName();
        this.simpleType = new XMLSimpleType();
    }
    
    public void setValues(final QName name, final XMLSimpleType simpleType, final boolean optional) {
        this.name.setValues(name);
        this.simpleType.setValues(simpleType);
        this.optional = optional;
    }
    
    public void clear() {
        this.name.clear();
        this.simpleType.clear();
        this.optional = false;
    }
}
