package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import org.apache.axiom.util.stax.wrapper.WrappingXMLOutputFactory;

class NormalizingXMLOutputFactoryWrapper extends WrappingXMLOutputFactory
{
    private final AbstractStAXDialect dialect;
    
    NormalizingXMLOutputFactoryWrapper(final XMLOutputFactory parent, final AbstractStAXDialect dialect) {
        super(parent);
        this.dialect = dialect;
    }
    
    @Override
    protected final XMLStreamWriter wrap(final XMLStreamWriter writer) {
        return this.dialect.normalize(writer);
    }
}
