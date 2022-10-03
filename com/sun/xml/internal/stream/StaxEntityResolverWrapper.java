package com.sun.xml.internal.stream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.InputStream;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import javax.xml.stream.XMLResolver;

public class StaxEntityResolverWrapper
{
    XMLResolver fStaxResolver;
    
    public StaxEntityResolverWrapper(final XMLResolver resolver) {
        this.fStaxResolver = resolver;
    }
    
    public void setStaxEntityResolver(final XMLResolver resolver) {
        this.fStaxResolver = resolver;
    }
    
    public XMLResolver getStaxEntityResolver() {
        return this.fStaxResolver;
    }
    
    public StaxXMLInputSource resolveEntity(final XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
        Object object = null;
        try {
            object = this.fStaxResolver.resolveEntity(resourceIdentifier.getPublicId(), resourceIdentifier.getLiteralSystemId(), resourceIdentifier.getBaseSystemId(), null);
            return this.getStaxInputSource(object);
        }
        catch (final XMLStreamException streamException) {
            throw new XNIException(streamException);
        }
    }
    
    StaxXMLInputSource getStaxInputSource(final Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof InputStream) {
            return new StaxXMLInputSource(new XMLInputSource(null, null, null, (InputStream)object, null));
        }
        if (object instanceof XMLStreamReader) {
            return new StaxXMLInputSource((XMLStreamReader)object);
        }
        if (object instanceof XMLEventReader) {
            return new StaxXMLInputSource((XMLEventReader)object);
        }
        return null;
    }
}
