package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface NonElement<T, C> extends TypeInfo<T, C>
{
    public static final QName ANYTYPE_NAME = new QName("http://www.w3.org/2001/XMLSchema", "anyType");
    
    QName getTypeName();
    
    boolean isSimpleType();
}
