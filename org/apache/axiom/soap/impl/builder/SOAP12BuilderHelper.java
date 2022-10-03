package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.om.impl.exception.OMBuilderException;
import org.apache.axiom.om.OMElement;
import javax.xml.stream.XMLStreamReader;
import java.util.Vector;

public class SOAP12BuilderHelper extends SOAPBuilderHelper
{
    private final SOAP12FactoryEx factory;
    private boolean codePresent;
    private boolean reasonPresent;
    private boolean nodePresent;
    private boolean rolePresent;
    private boolean detailPresent;
    private boolean subcodeValuePresent;
    private boolean subSubcodePresent;
    private boolean valuePresent;
    private boolean subcodePresent;
    private boolean codeprocessing;
    private boolean subCodeProcessing;
    private boolean reasonProcessing;
    private boolean processingDetailElements;
    private Vector detailElementNames;
    
    public SOAP12BuilderHelper(final StAXSOAPModelBuilder builder, final SOAP12FactoryEx factory) {
        super(builder);
        this.codePresent = false;
        this.reasonPresent = false;
        this.nodePresent = false;
        this.rolePresent = false;
        this.detailPresent = false;
        this.subcodeValuePresent = false;
        this.subSubcodePresent = false;
        this.valuePresent = false;
        this.subcodePresent = false;
        this.codeprocessing = false;
        this.subCodeProcessing = false;
        this.reasonProcessing = false;
        this.processingDetailElements = false;
        this.factory = factory;
    }
    
    @Override
    public OMElement handleEvent(final XMLStreamReader parser, final OMElement parent, final int elementLevel) throws SOAPProcessingException {
        this.parser = parser;
        OMElement element = null;
        if (elementLevel == 4) {
            if (parser.getLocalName().equals("Code")) {
                if (this.codePresent) {
                    throw new OMBuilderException("Multiple Code element encountered");
                }
                element = this.factory.createSOAPFaultCode((SOAPFault)parent, this.builder);
                this.codePresent = true;
                this.codeprocessing = true;
            }
            else if (parser.getLocalName().equals("Reason")) {
                if (!this.codeprocessing && !this.subCodeProcessing) {
                    if (!this.codePresent) {
                        throw new OMBuilderException("Wrong element order encountred at " + parser.getLocalName());
                    }
                    if (this.reasonPresent) {
                        throw new OMBuilderException("Multiple Reason Element encountered");
                    }
                    element = this.factory.createSOAPFaultReason((SOAPFault)parent, this.builder);
                    this.reasonPresent = true;
                    this.reasonProcessing = true;
                }
                else {
                    if (this.codeprocessing) {
                        throw new OMBuilderException("Code doesn't have a value");
                    }
                    throw new OMBuilderException("A subcode doesn't have a Value");
                }
            }
            else if (parser.getLocalName().equals("Node")) {
                if (this.reasonProcessing) {
                    throw new OMBuilderException("Reason element Should have a text");
                }
                if (!this.reasonPresent || this.rolePresent || this.detailPresent) {
                    throw new OMBuilderException("wrong element order encountered at " + parser.getLocalName());
                }
                if (this.nodePresent) {
                    throw new OMBuilderException("Multiple Node element encountered");
                }
                element = this.factory.createSOAPFaultNode((SOAPFault)parent, this.builder);
                this.nodePresent = true;
            }
            else if (parser.getLocalName().equals("Role")) {
                if (this.reasonProcessing) {
                    throw new OMBuilderException("Reason element should have a text");
                }
                if (!this.reasonPresent || this.detailPresent) {
                    throw new OMBuilderException("Wrong element order encountered at " + parser.getLocalName());
                }
                if (this.rolePresent) {
                    throw new OMBuilderException("Multiple Role element encountered");
                }
                element = this.factory.createSOAPFaultRole((SOAPFault)parent, this.builder);
                this.rolePresent = true;
            }
            else {
                if (!parser.getLocalName().equals("Detail")) {
                    throw new OMBuilderException(parser.getLocalName() + " unsupported element in SOAPFault element");
                }
                if (this.reasonProcessing) {
                    throw new OMBuilderException("Reason element should have a text");
                }
                if (!this.reasonPresent) {
                    throw new OMBuilderException("wrong element order encountered at " + parser.getLocalName());
                }
                if (this.detailPresent) {
                    throw new OMBuilderException("Multiple detail element encountered");
                }
                element = this.factory.createSOAPFaultDetail((SOAPFault)parent, this.builder);
                this.detailPresent = true;
            }
        }
        else if (elementLevel == 5) {
            if (parent.getLocalName().equals("Code")) {
                if (parser.getLocalName().equals("Value")) {
                    if (this.valuePresent) {
                        throw new OMBuilderException("Multiple value Encountered in code element");
                    }
                    element = this.factory.createSOAPFaultValue((SOAPFaultCode)parent, this.builder);
                    this.valuePresent = true;
                    this.codeprocessing = false;
                }
                else {
                    if (!parser.getLocalName().equals("Subcode")) {
                        throw new OMBuilderException(parser.getLocalName() + " is not supported inside the code element");
                    }
                    if (this.subcodePresent) {
                        throw new OMBuilderException("multiple subcode Encountered in code element");
                    }
                    if (!this.valuePresent) {
                        throw new OMBuilderException("Value should present before the subcode");
                    }
                    element = this.factory.createSOAPFaultSubCode((SOAPFaultCode)parent, this.builder);
                    this.subcodePresent = true;
                    this.subCodeProcessing = true;
                }
            }
            else if (parent.getLocalName().equals("Reason")) {
                if (!parser.getLocalName().equals("Text")) {
                    throw new OMBuilderException(parser.getLocalName() + " is not supported inside the reason");
                }
                element = this.factory.createSOAPFaultText((SOAPFaultReason)parent, this.builder);
                ((OMNodeEx)element).setComplete(false);
                this.reasonProcessing = false;
            }
            else {
                if (!parent.getLocalName().equals("Detail")) {
                    throw new OMBuilderException(parent.getLocalName() + " should not have child element");
                }
                element = this.factory.createOMElement(parser.getLocalName(), parent, this.builder);
                this.processingDetailElements = true;
                (this.detailElementNames = new Vector()).add(parser.getLocalName());
            }
        }
        else if (elementLevel > 5) {
            if (parent.getLocalName().equals("Subcode")) {
                if (parser.getLocalName().equals("Value")) {
                    if (this.subcodeValuePresent) {
                        throw new OMBuilderException("multiple subCode value encountered");
                    }
                    element = this.factory.createSOAPFaultValue((SOAPFaultSubCode)parent, this.builder);
                    this.subcodeValuePresent = true;
                    this.subSubcodePresent = false;
                    this.subCodeProcessing = false;
                }
                else {
                    if (!parser.getLocalName().equals("Subcode")) {
                        throw new OMBuilderException(parser.getLocalName() + " is not supported inside the subCode element");
                    }
                    if (!this.subcodeValuePresent) {
                        throw new OMBuilderException("Value should present before the subcode");
                    }
                    if (this.subSubcodePresent) {
                        throw new OMBuilderException("multiple subcode encountered");
                    }
                    element = this.factory.createSOAPFaultSubCode((SOAPFaultSubCode)parent, this.builder);
                    this.subcodeValuePresent = false;
                    this.subSubcodePresent = true;
                    this.subCodeProcessing = true;
                }
            }
            else {
                if (!this.processingDetailElements) {
                    throw new OMBuilderException(parent.getLocalName() + " should not have child at element level " + elementLevel);
                }
                int detailElementLevel = 0;
                boolean localNameExist = false;
                for (int i = 0; i < this.detailElementNames.size(); ++i) {
                    if (parent.getLocalName().equals(this.detailElementNames.get(i))) {
                        localNameExist = true;
                        detailElementLevel = i + 1;
                    }
                }
                if (localNameExist) {
                    this.detailElementNames.setSize(detailElementLevel);
                    element = this.factory.createOMElement(parser.getLocalName(), parent, this.builder);
                    this.detailElementNames.add(parser.getLocalName());
                }
            }
        }
        return element;
    }
}
