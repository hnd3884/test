package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.dv.DatatypeValidator;

public class XMLSimpleType
{
    public static final short TYPE_CDATA = 0;
    public static final short TYPE_ENTITY = 1;
    public static final short TYPE_ENUMERATION = 2;
    public static final short TYPE_ID = 3;
    public static final short TYPE_IDREF = 4;
    public static final short TYPE_NMTOKEN = 5;
    public static final short TYPE_NOTATION = 6;
    public static final short TYPE_NAMED = 7;
    public static final short DEFAULT_TYPE_DEFAULT = 3;
    public static final short DEFAULT_TYPE_FIXED = 1;
    public static final short DEFAULT_TYPE_IMPLIED = 0;
    public static final short DEFAULT_TYPE_REQUIRED = 2;
    public short type;
    public String name;
    public String[] enumeration;
    public boolean list;
    public short defaultType;
    public String defaultValue;
    public String nonNormalizedDefaultValue;
    public DatatypeValidator datatypeValidator;
    
    public void setValues(final short type, final String name, final String[] array, final boolean list, final short defaultType, final String defaultValue, final String nonNormalizedDefaultValue, final DatatypeValidator datatypeValidator) {
        this.type = type;
        this.name = name;
        if (array != null && array.length > 0) {
            System.arraycopy(array, 0, this.enumeration = new String[array.length], 0, this.enumeration.length);
        }
        else {
            this.enumeration = null;
        }
        this.list = list;
        this.defaultType = defaultType;
        this.defaultValue = defaultValue;
        this.nonNormalizedDefaultValue = nonNormalizedDefaultValue;
        this.datatypeValidator = datatypeValidator;
    }
    
    public void setValues(final XMLSimpleType xmlSimpleType) {
        this.type = xmlSimpleType.type;
        this.name = xmlSimpleType.name;
        if (xmlSimpleType.enumeration != null && xmlSimpleType.enumeration.length > 0) {
            this.enumeration = new String[xmlSimpleType.enumeration.length];
            System.arraycopy(xmlSimpleType.enumeration, 0, this.enumeration, 0, this.enumeration.length);
        }
        else {
            this.enumeration = null;
        }
        this.list = xmlSimpleType.list;
        this.defaultType = xmlSimpleType.defaultType;
        this.defaultValue = xmlSimpleType.defaultValue;
        this.nonNormalizedDefaultValue = xmlSimpleType.nonNormalizedDefaultValue;
        this.datatypeValidator = xmlSimpleType.datatypeValidator;
    }
    
    public void clear() {
        this.type = -1;
        this.name = null;
        this.enumeration = null;
        this.list = false;
        this.defaultType = -1;
        this.defaultValue = null;
        this.nonNormalizedDefaultValue = null;
        this.datatypeValidator = null;
    }
}
