package org.apache.axiom.util.stax.dialect;

import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import org.apache.axiom.util.stax.wrapper.XMLInputFactoryWrapper;

final class CloseShieldXMLInputFactoryWrapper extends XMLInputFactoryWrapper
{
    public CloseShieldXMLInputFactoryWrapper(final XMLInputFactory parent) {
        super(parent);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream, final String encoding) throws XMLStreamException {
        return super.createXMLStreamReader(new CloseShieldInputStream(stream), encoding);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream) throws XMLStreamException {
        return super.createXMLStreamReader(new CloseShieldInputStream(stream));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Reader reader) throws XMLStreamException {
        return super.createXMLStreamReader(new CloseShieldReader(reader));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final InputStream stream) throws XMLStreamException {
        return super.createXMLStreamReader(systemId, new CloseShieldInputStream(stream));
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader reader) throws XMLStreamException {
        return super.createXMLStreamReader(systemId, new CloseShieldReader(reader));
    }
}
