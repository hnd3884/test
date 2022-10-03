package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.soap.SOAPProcessingException;
import org.w3c.dom.Element;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.om.OMElement;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.soap.SOAP11Constants;

public class SOAP11BuilderHelper extends SOAPBuilderHelper implements SOAP11Constants
{
    private final SOAPFactoryEx factory;
    private boolean faultcodePresent;
    private boolean faultstringPresent;
    
    public SOAP11BuilderHelper(final StAXSOAPModelBuilder builder, final SOAPFactoryEx factory) {
        super(builder);
        this.faultcodePresent = false;
        this.faultstringPresent = false;
        this.factory = factory;
    }
    
    @Override
    public OMElement handleEvent(final XMLStreamReader parser, final OMElement parent, final int elementLevel) throws SOAPProcessingException {
        this.parser = parser;
        OMElement element = null;
        final String localName = parser.getLocalName();
        if (elementLevel == 4) {
            if ("faultcode".equals(localName)) {
                element = this.factory.createSOAPFaultCode((SOAPFault)parent, this.builder);
                this.faultcodePresent = true;
            }
            else if ("faultstring".equals(localName)) {
                element = this.factory.createSOAPFaultReason((SOAPFault)parent, this.builder);
                this.faultstringPresent = true;
            }
            else if ("faultactor".equals(localName)) {
                element = this.factory.createSOAPFaultRole((SOAPFault)parent, this.builder);
            }
            else if ("detail".equals(localName)) {
                element = this.factory.createSOAPFaultDetail((SOAPFault)parent, this.builder);
            }
            else {
                element = this.factory.createOMElement(localName, parent, this.builder);
            }
        }
        else if (elementLevel == 5) {
            String parentTagName = "";
            if (parent instanceof Element) {
                parentTagName = ((Element)parent).getTagName();
            }
            else {
                parentTagName = parent.getLocalName();
            }
            if (parentTagName.equals("faultcode")) {
                throw new SOAPProcessingException("faultcode element should not have children");
            }
            if (parentTagName.equals("faultstring")) {
                throw new SOAPProcessingException("faultstring element should not have children");
            }
            if (parentTagName.equals("faultactor")) {
                throw new SOAPProcessingException("faultactor element should not have children");
            }
            element = this.factory.createOMElement(localName, parent, this.builder);
        }
        else if (elementLevel > 5) {
            element = this.factory.createOMElement(localName, parent, this.builder);
        }
        return element;
    }
}
