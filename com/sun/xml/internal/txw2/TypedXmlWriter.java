package com.sun.xml.internal.txw2;

import javax.xml.namespace.QName;

public interface TypedXmlWriter
{
    void commit();
    
    void commit(final boolean p0);
    
    void block();
    
    Document getDocument();
    
    void _attribute(final String p0, final Object p1);
    
    void _attribute(final String p0, final String p1, final Object p2);
    
    void _attribute(final QName p0, final Object p1);
    
    void _namespace(final String p0);
    
    void _namespace(final String p0, final String p1);
    
    void _namespace(final String p0, final boolean p1);
    
    void _pcdata(final Object p0);
    
    void _cdata(final Object p0);
    
    void _comment(final Object p0) throws UnsupportedOperationException;
    
     <T extends TypedXmlWriter> T _element(final String p0, final Class<T> p1);
    
     <T extends TypedXmlWriter> T _element(final String p0, final String p1, final Class<T> p2);
    
     <T extends TypedXmlWriter> T _element(final QName p0, final Class<T> p1);
    
     <T extends TypedXmlWriter> T _element(final Class<T> p0);
    
     <T extends TypedXmlWriter> T _cast(final Class<T> p0);
}
