package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;

class Woodstox3Dialect extends AbstractStAXDialect
{
    public static final Woodstox3Dialect INSTANCE;
    
    public String getName() {
        return "Woodstox 3.x";
    }
    
    public XMLInputFactory enableCDataReporting(final XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
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
        return new Woodstox3StreamReaderWrapper(reader);
    }
    
    @Override
    public XMLStreamWriter normalize(final XMLStreamWriter writer) {
        return new Woodstox3StreamWriterWrapper(writer);
    }
    
    public XMLInputFactory normalize(final XMLInputFactory factory) {
        return new NormalizingXMLInputFactoryWrapper(factory, this);
    }
    
    public XMLOutputFactory normalize(final XMLOutputFactory factory) {
        return new Woodstox3OutputFactoryWrapper(factory, this);
    }
    
    static {
        INSTANCE = new Woodstox3Dialect();
    }
}
