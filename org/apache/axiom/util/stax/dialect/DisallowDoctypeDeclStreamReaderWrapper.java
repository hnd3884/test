package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class DisallowDoctypeDeclStreamReaderWrapper extends XMLStreamReaderWrapper implements DelegatingXMLStreamReader
{
    public DisallowDoctypeDeclStreamReaderWrapper(final XMLStreamReader parent) {
        super(parent);
    }
    
    @Override
    public int next() throws XMLStreamException {
        final int event = super.next();
        if (event == 11) {
            throw new XMLStreamException("DOCTYPE is not allowed");
        }
        return event;
    }
    
    public XMLStreamReader getParent() {
        return super.getParent();
    }
}
