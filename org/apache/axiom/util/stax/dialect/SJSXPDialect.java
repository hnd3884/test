package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLInputFactory;

class SJSXPDialect extends AbstractStAXDialect
{
    private final boolean isUnsafeStreamResult;
    
    public SJSXPDialect(final boolean isUnsafeStreamResult) {
        this.isUnsafeStreamResult = isUnsafeStreamResult;
    }
    
    public String getName() {
        return this.isUnsafeStreamResult ? "SJSXP (with thread safety issue)" : "SJSXP";
    }
    
    public XMLInputFactory enableCDataReporting(final XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        factory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
        return factory;
    }
    
    public XMLInputFactory disallowDoctypeDecl(final XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.supportDTD", Boolean.TRUE);
        factory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        factory.setXMLResolver(new XMLResolver() {
            public Object resolveEntity(final String publicID, final String systemID, final String baseURI, final String namespace) throws XMLStreamException {
                throw new XMLStreamException("DOCTYPE is not allowed");
            }
        });
        return new DisallowDoctypeDeclInputFactoryWrapper(factory);
    }
    
    public XMLInputFactory makeThreadSafe(final XMLInputFactory factory) {
        factory.setProperty("reuse-instance", Boolean.FALSE);
        return factory;
    }
    
    public XMLOutputFactory makeThreadSafe(XMLOutputFactory factory) {
        factory.setProperty("reuse-instance", Boolean.FALSE);
        if (this.isUnsafeStreamResult) {
            factory = new SynchronizedOutputFactoryWrapper(factory);
        }
        return factory;
    }
    
    @Override
    public XMLStreamReader normalize(final XMLStreamReader reader) {
        return new SJSXPStreamReaderWrapper(reader);
    }
    
    @Override
    public XMLStreamWriter normalize(final XMLStreamWriter writer) {
        return new SJSXPStreamWriterWrapper(writer);
    }
    
    public XMLInputFactory normalize(final XMLInputFactory factory) {
        return new NormalizingXMLInputFactoryWrapper(factory, this);
    }
    
    public XMLOutputFactory normalize(final XMLOutputFactory factory) {
        return new SJSXPOutputFactoryWrapper(factory, this);
    }
}
