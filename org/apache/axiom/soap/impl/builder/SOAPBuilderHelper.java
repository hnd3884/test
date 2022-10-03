package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import javax.xml.stream.XMLStreamReader;

public abstract class SOAPBuilderHelper
{
    protected final StAXSOAPModelBuilder builder;
    protected XMLStreamReader parser;
    
    protected SOAPBuilderHelper(final StAXSOAPModelBuilder builder) {
        this.builder = builder;
    }
    
    public abstract OMElement handleEvent(final XMLStreamReader p0, final OMElement p1, final int p2) throws SOAPProcessingException;
}
