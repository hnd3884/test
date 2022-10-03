package org.apache.axiom.om.impl.common.serializer.pull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

final class IncludeWrapper extends AbstractWrapper
{
    IncludeWrapper(final PullSerializer serializer, final XMLStreamReader parent) {
        super(serializer, parent, 0);
    }
    
    @Override
    void released() throws XMLStreamException {
        this.reader.close();
    }
}
