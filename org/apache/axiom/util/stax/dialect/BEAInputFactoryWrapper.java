package org.apache.axiom.util.stax.dialect;

import javax.xml.transform.Source;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import org.apache.axiom.util.stax.wrapper.XMLInputFactoryWrapper;

class BEAInputFactoryWrapper extends XMLInputFactoryWrapper
{
    public BEAInputFactoryWrapper(final XMLInputFactory parent) {
        super(parent);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream) throws XMLStreamException {
        return this.createXMLStreamReader(null, stream);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, InputStream stream) throws XMLStreamException {
        final EncodingDetectionHelper helper = new EncodingDetectionHelper(stream);
        stream = helper.getInputStream();
        final String encoding = helper.detectEncoding();
        XMLStreamReader reader;
        if (systemId == null) {
            reader = super.createXMLStreamReader(stream);
        }
        else {
            reader = super.createXMLStreamReader(systemId, stream);
        }
        return new BEAStreamReaderWrapper(reader, encoding);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final InputStream stream, final String encoding) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(stream, encoding), null);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Reader reader) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(reader), null);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final Source source) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(source), null);
    }
    
    @Override
    public XMLStreamReader createXMLStreamReader(final String systemId, final Reader reader) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(systemId, reader), null);
    }
}
