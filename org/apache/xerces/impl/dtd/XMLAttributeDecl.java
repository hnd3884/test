package org.apache.xerces.impl.dtd;

import org.apache.xerces.xni.QName;

public class XMLAttributeDecl
{
    public final QName name;
    public final XMLSimpleType simpleType;
    public boolean optional;
    
    public XMLAttributeDecl() {
        this.name = new QName();
        this.simpleType = new XMLSimpleType();
    }
    
    public void setValues(final QName values, final XMLSimpleType values2, final boolean optional) {
        this.name.setValues(values);
        this.simpleType.setValues(values2);
        this.optional = optional;
    }
    
    public void clear() {
        this.name.clear();
        this.simpleType.clear();
        this.optional = false;
    }
}
