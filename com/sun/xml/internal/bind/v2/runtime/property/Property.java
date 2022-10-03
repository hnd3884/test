package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.api.AccessorException;

public interface Property<BeanT> extends StructureLoaderBuilder
{
    void reset(final BeanT p0) throws AccessorException;
    
    void serializeBody(final BeanT p0, final XMLSerializer p1, final Object p2) throws SAXException, AccessorException, IOException, XMLStreamException;
    
    void serializeURIs(final BeanT p0, final XMLSerializer p1) throws SAXException, AccessorException;
    
    boolean hasSerializeURIAction();
    
    String getIdValue(final BeanT p0) throws AccessorException, SAXException;
    
    PropertyKind getKind();
    
    Accessor getElementPropertyAccessor(final String p0, final String p1);
    
    void wrapUp();
    
    RuntimePropertyInfo getInfo();
    
    boolean isHiddenByOverride();
    
    void setHiddenByOverride(final boolean p0);
    
    String getFieldName();
}
