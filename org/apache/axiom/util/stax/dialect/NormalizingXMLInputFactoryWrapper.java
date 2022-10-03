package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import org.apache.axiom.util.stax.wrapper.WrappingXMLInputFactory;

class NormalizingXMLInputFactoryWrapper extends WrappingXMLInputFactory
{
    private final AbstractStAXDialect dialect;
    
    NormalizingXMLInputFactoryWrapper(final XMLInputFactory parent, final AbstractStAXDialect dialect) {
        super(parent);
        this.dialect = dialect;
    }
    
    @Override
    protected final XMLStreamReader wrap(final XMLStreamReader reader) {
        return this.dialect.normalize(reader);
    }
}
