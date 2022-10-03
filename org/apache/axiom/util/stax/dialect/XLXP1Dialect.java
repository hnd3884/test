package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;

class XLXP1Dialect extends AbstractStAXDialect
{
    private final boolean isSetPrefixBroken;
    
    public XLXP1Dialect(final boolean isSetPrefixBroken) {
        this.isSetPrefixBroken = isSetPrefixBroken;
    }
    
    public String getName() {
        return this.isSetPrefixBroken ? "XL XP-J (StAX non-compliant versions)" : "XL XP-J (StAX compliant versions)";
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
        return new XLXP1StreamReaderWrapper(reader);
    }
    
    @Override
    public XMLStreamWriter normalize(final XMLStreamWriter writer) {
        XMLStreamWriter wrapper = new XLXPStreamWriterWrapper(writer);
        if (this.isSetPrefixBroken) {
            wrapper = new NamespaceContextCorrectingXMLStreamWriterWrapper(wrapper);
        }
        return wrapper;
    }
    
    public XMLInputFactory normalize(final XMLInputFactory factory) {
        return new CloseShieldXMLInputFactoryWrapper(new XLXPInputFactoryWrapper(factory, this));
    }
    
    public XMLOutputFactory normalize(final XMLOutputFactory factory) {
        return new NormalizingXMLOutputFactoryWrapper(factory, this);
    }
}
