package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;

class Woodstox4Dialect extends AbstractStAXDialect
{
    private final boolean wstx276;
    
    Woodstox4Dialect(final boolean wstx276) {
        this.wstx276 = wstx276;
    }
    
    public String getName() {
        final StringBuilder result = new StringBuilder("Woodstox 4.x");
        if (this.wstx276) {
            result.append(" [WSTX-276]");
        }
        return result.toString();
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
        return new Woodstox4StreamReaderWrapper(reader);
    }
    
    @Override
    public XMLStreamWriter normalize(final XMLStreamWriter writer) {
        return new Woodstox4StreamWriterWrapper(writer);
    }
    
    public XMLInputFactory normalize(XMLInputFactory factory) {
        factory.setProperty("org.codehaus.stax2.reportPrologWhitespace", Boolean.TRUE);
        factory = new NormalizingXMLInputFactoryWrapper(factory, this);
        if (this.wstx276) {
            factory = new CloseShieldXMLInputFactoryWrapper(factory);
        }
        return factory;
    }
    
    public XMLOutputFactory normalize(final XMLOutputFactory factory) {
        factory.setProperty("com.ctc.wstx.outputFixContent", Boolean.TRUE);
        return new Woodstox4OutputFactoryWrapper(factory, this);
    }
}
