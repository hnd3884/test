package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;

public interface XSSimpleType extends XSSimpleTypeDefinition
{
    public static final short WS_PRESERVE = 0;
    public static final short WS_REPLACE = 1;
    public static final short WS_COLLAPSE = 2;
    public static final short PRIMITIVE_STRING = 1;
    public static final short PRIMITIVE_BOOLEAN = 2;
    public static final short PRIMITIVE_DECIMAL = 3;
    public static final short PRIMITIVE_FLOAT = 4;
    public static final short PRIMITIVE_DOUBLE = 5;
    public static final short PRIMITIVE_DURATION = 6;
    public static final short PRIMITIVE_DATETIME = 7;
    public static final short PRIMITIVE_TIME = 8;
    public static final short PRIMITIVE_DATE = 9;
    public static final short PRIMITIVE_GYEARMONTH = 10;
    public static final short PRIMITIVE_GYEAR = 11;
    public static final short PRIMITIVE_GMONTHDAY = 12;
    public static final short PRIMITIVE_GDAY = 13;
    public static final short PRIMITIVE_GMONTH = 14;
    public static final short PRIMITIVE_HEXBINARY = 15;
    public static final short PRIMITIVE_BASE64BINARY = 16;
    public static final short PRIMITIVE_ANYURI = 17;
    public static final short PRIMITIVE_QNAME = 18;
    public static final short PRIMITIVE_PRECISIONDECIMAL = 19;
    public static final short PRIMITIVE_NOTATION = 20;
    
    short getPrimitiveKind();
    
    Object validate(final String p0, final ValidationContext p1, final ValidatedInfo p2) throws InvalidDatatypeValueException;
    
    Object validate(final Object p0, final ValidationContext p1, final ValidatedInfo p2) throws InvalidDatatypeValueException;
    
    void validate(final ValidationContext p0, final ValidatedInfo p1) throws InvalidDatatypeValueException;
    
    void applyFacets(final XSFacets p0, final short p1, final short p2, final ValidationContext p3) throws InvalidDatatypeFacetException;
    
    boolean isEqual(final Object p0, final Object p1);
    
    boolean isIDType();
    
    short getWhitespace() throws DatatypeException;
}
