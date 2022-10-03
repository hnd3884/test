package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.dtd.models.ContentModelValidator;
import org.apache.xerces.xni.QName;

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
    public ContentModelValidator contentModelValidator;
    public final XMLSimpleType simpleType;
    
    public XMLElementDecl() {
        this.name = new QName();
        this.scope = -1;
        this.type = -1;
        this.simpleType = new XMLSimpleType();
    }
    
    public void setValues(final QName values, final int scope, final short type, final ContentModelValidator contentModelValidator, final XMLSimpleType values2) {
        this.name.setValues(values);
        this.scope = scope;
        this.type = type;
        this.contentModelValidator = contentModelValidator;
        this.simpleType.setValues(values2);
    }
    
    public void clear() {
        this.name.clear();
        this.type = -1;
        this.scope = -1;
        this.contentModelValidator = null;
        this.simpleType.clear();
    }
}
