package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;

class BEADialect extends AbstractStAXDialect
{
    public static final StAXDialect INSTANCE;
    
    public String getName() {
        return "BEA";
    }
    
    public XMLInputFactory enableCDataReporting(final XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        factory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
        return factory;
    }
    
    public XMLInputFactory disallowDoctypeDecl(final XMLInputFactory factory) {
        return StAXDialectUtils.disallowDoctypeDecl(factory);
    }
    
    public XMLInputFactory makeThreadSafe(final XMLInputFactory factory) {
        return factory;
    }
    
    public XMLOutputFactory makeThreadSafe(final XMLOutputFactory factory) {
        return factory;
    }
    
    @Override
    public XMLStreamReader normalize(final XMLStreamReader reader) {
        return new BEAStreamReaderWrapper(reader, null);
    }
    
    @Override
    public XMLStreamWriter normalize(final XMLStreamWriter writer) {
        return new NamespaceContextCorrectingXMLStreamWriterWrapper(writer);
    }
    
    public XMLInputFactory normalize(final XMLInputFactory factory) {
        return new BEAInputFactoryWrapper(factory);
    }
    
    public XMLOutputFactory normalize(final XMLOutputFactory factory) {
        return new NormalizingXMLOutputFactoryWrapper(factory, this);
    }
    
    static {
        INSTANCE = new BEADialect();
    }
}
