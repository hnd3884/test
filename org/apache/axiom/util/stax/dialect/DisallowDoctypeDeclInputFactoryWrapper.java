package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import org.apache.axiom.util.stax.wrapper.WrappingXMLInputFactory;

class DisallowDoctypeDeclInputFactoryWrapper extends WrappingXMLInputFactory
{
    public DisallowDoctypeDeclInputFactoryWrapper(final XMLInputFactory parent) {
        super(parent);
    }
    
    @Override
    protected XMLStreamReader wrap(final XMLStreamReader reader) {
        return new DisallowDoctypeDeclStreamReaderWrapper(reader);
    }
}
