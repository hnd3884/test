package com.sun.xml.internal.stream.dtd.nonvalidating;

import com.sun.org.apache.xerces.internal.xni.QName;

public class XMLElementDecl
{
    public static final short TYPE_ANY = 0;
    public static final short TYPE_EMPTY = 1;
    public static final short TYPE_MIXED = 2;
    public static final short TYPE_CHILDREN = 3;
    public static final short TYPE_SIMPLE = 4;
    public final QName name;
    public int scope;
    public short type;
    public final XMLSimpleType simpleType;
    
    public XMLElementDecl() {
        this.name = new QName();
        this.scope = -1;
        this.type = -1;
        this.simpleType = new XMLSimpleType();
    }
    
    public void setValues(final QName name, final int scope, final short type, final XMLSimpleType simpleType) {
        this.name.setValues(name);
        this.scope = scope;
        this.type = type;
        this.simpleType.setValues(simpleType);
    }
    
    public void clear() {
        this.name.clear();
        this.type = -1;
        this.scope = -1;
        this.simpleType.clear();
    }
}
