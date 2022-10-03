package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;

class XLXPInputFactoryWrapper extends NormalizingXMLInputFactoryWrapper
{
    public XLXPInputFactoryWrapper(final XMLInputFactory parent, final AbstractStAXDialect dialect) {
        super(parent, dialect);
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
        if (encoding.startsWith("UTF-16")) {
            if (systemId == null) {
                return super.createXMLStreamReader(stream, encoding);
            }
            return super.createXMLStreamReader(systemId, stream);
        }
        else {
            if (systemId == null) {
                return super.createXMLStreamReader(stream);
            }
            return super.createXMLStreamReader(systemId, stream);
        }
    }
}
