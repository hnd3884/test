package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.AccessorException;

public interface Transducer<ValueT>
{
    boolean isDefault();
    
    boolean useNamespace();
    
    void declareNamespace(final ValueT p0, final XMLSerializer p1) throws AccessorException;
    
    @NotNull
    CharSequence print(@NotNull final ValueT p0) throws AccessorException;
    
    ValueT parse(final CharSequence p0) throws AccessorException, SAXException;
    
    void writeText(final XMLSerializer p0, final ValueT p1, final String p2) throws IOException, SAXException, XMLStreamException, AccessorException;
    
    void writeLeafElement(final XMLSerializer p0, final Name p1, @NotNull final ValueT p2, final String p3) throws IOException, SAXException, XMLStreamException, AccessorException;
    
    QName getTypeName(@NotNull final ValueT p0);
}
