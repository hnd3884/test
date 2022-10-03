package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLInputFactory;

class XLXP2Dialect extends AbstractStAXDialect
{
    public static final StAXDialect INSTANCE;
    
    public String getName() {
        return "XLXP2";
    }
    
    public XMLInputFactory enableCDataReporting(final XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        return factory;
    }
    
    public XMLInputFactory disallowDoctypeDecl(final XMLInputFactory factory) {
        factory.setXMLResolver(new SecureXMLResolver());
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
        return new NamespaceContextCorrectingXMLStreamReaderWrapper(new XLXPStreamReaderWrapper(reader));
    }
    
    @Override
    public XMLStreamWriter normalize(final XMLStreamWriter writer) {
        return new XLXPStreamWriterWrapper(writer);
    }
    
    public XMLInputFactory normalize(final XMLInputFactory factory) {
        return new XLXPInputFactoryWrapper(factory, this);
    }
    
    public XMLOutputFactory normalize(final XMLOutputFactory factory) {
        return new NormalizingXMLOutputFactoryWrapper(factory, this);
    }
    
    static {
        INSTANCE = new XLXP2Dialect();
    }
}
