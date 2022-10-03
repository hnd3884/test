package com.sun.xml.internal.ws.api.model.wsdl;

import java.util.List;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;

public interface WSDLExtensible extends WSDLObject
{
    Iterable<WSDLExtension> getExtensions();
    
     <T extends WSDLExtension> Iterable<T> getExtensions(final Class<T> p0);
    
     <T extends WSDLExtension> T getExtension(final Class<T> p0);
    
    void addExtension(final WSDLExtension p0);
    
    boolean areRequiredExtensionsUnderstood();
    
    void addNotUnderstoodExtension(final QName p0, final Locator p1);
    
    List<? extends WSDLExtension> getNotUnderstoodExtensions();
}
