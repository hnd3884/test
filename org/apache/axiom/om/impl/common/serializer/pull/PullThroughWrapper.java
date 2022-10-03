package org.apache.axiom.om.impl.common.serializer.pull;

import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMDocument;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

final class PullThroughWrapper extends AbstractWrapper
{
    private final StAXOMBuilder builder;
    private final OMContainer container;
    
    PullThroughWrapper(final PullSerializer serializer, final StAXOMBuilder builder, final OMContainer container, final XMLStreamReader reader, final int startDepth) {
        super(serializer, reader, startDepth);
        this.builder = builder;
        this.container = container;
    }
    
    @Override
    void released() throws XMLStreamException {
        if (this.container instanceof OMDocument) {
            this.builder.close();
        }
        else {
            while (this.doNext()) {}
            this.builder.reenableCaching(this.container);
        }
    }
}
