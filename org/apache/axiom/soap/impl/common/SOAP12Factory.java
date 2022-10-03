package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultNode;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultText;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultSubCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultValue;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFaultClassifier;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.impl.builder.SOAP12FactoryEx;

public class SOAP12Factory extends SOAPFactoryImpl implements SOAP12FactoryEx
{
    public SOAP12Factory(final OMMetaFactory metaFactory, final NodeFactory nodeFactory) {
        super(metaFactory, nodeFactory);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return SOAPHelper.SOAP12;
    }
    
    final SOAPFaultValue internalCreateSOAPFaultValue(final SOAPFaultClassifier parent, final OMXMLParserWrapper builder) {
        return this.createSOAPElement((Class<SOAPFaultValue>)AxiomSOAP12FaultValue.class, (OMElement)parent, SOAP12Constants.QNAME_FAULT_VALUE, builder);
    }
    
    public final SOAPFaultValue createSOAPFaultValue(final SOAPFaultCode parent, final OMXMLParserWrapper builder) {
        return this.internalCreateSOAPFaultValue((SOAPFaultClassifier)parent, builder);
    }
    
    public final SOAPFaultValue createSOAPFaultValue(final SOAPFaultSubCode parent, final OMXMLParserWrapper builder) {
        return this.internalCreateSOAPFaultValue((SOAPFaultClassifier)parent, builder);
    }
    
    public final SOAPFaultValue createSOAPFaultValue(final SOAPFaultCode parent) {
        return this.internalCreateSOAPFaultValue((SOAPFaultClassifier)parent, null);
    }
    
    public final SOAPFaultValue createSOAPFaultValue(final SOAPFaultSubCode parent) {
        return this.internalCreateSOAPFaultValue((SOAPFaultClassifier)parent, null);
    }
    
    public final SOAPFaultValue createSOAPFaultValue() {
        return this.internalCreateSOAPFaultValue(null, null);
    }
    
    private SOAPFaultSubCode internalCreateSOAPFaultSubCode(final SOAPFaultClassifier parent, final OMXMLParserWrapper builder) {
        return this.createSOAPElement((Class<SOAPFaultSubCode>)AxiomSOAP12FaultSubCode.class, (OMElement)parent, SOAP12Constants.QNAME_FAULT_SUBCODE, builder);
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultCode parent, final OMXMLParserWrapper builder) {
        return this.internalCreateSOAPFaultSubCode((SOAPFaultClassifier)parent, builder);
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultSubCode parent, final OMXMLParserWrapper builder) {
        return this.internalCreateSOAPFaultSubCode((SOAPFaultClassifier)parent, builder);
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultCode parent) {
        return this.internalCreateSOAPFaultSubCode((SOAPFaultClassifier)parent, null);
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultSubCode parent) {
        return this.internalCreateSOAPFaultSubCode((SOAPFaultClassifier)parent, null);
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode() {
        return this.internalCreateSOAPFaultSubCode(null, null);
    }
    
    public final SOAPFaultText createSOAPFaultText(final SOAPFaultReason parent, final OMXMLParserWrapper builder) {
        return this.createSOAPElement((Class<SOAPFaultText>)AxiomSOAP12FaultText.class, (OMElement)parent, SOAP12Constants.QNAME_FAULT_TEXT, builder);
    }
    
    public final SOAPFaultText createSOAPFaultText(final SOAPFaultReason parent) {
        return this.createSOAPFaultText(parent, null);
    }
    
    public final SOAPFaultText createSOAPFaultText() {
        return this.createSOAPFaultText(null, null);
    }
    
    public final SOAPFaultNode createSOAPFaultNode(final SOAPFault parent, final OMXMLParserWrapper builder) {
        return this.createSOAPElement((Class<SOAPFaultNode>)AxiomSOAP12FaultNode.class, (OMElement)parent, SOAP12Constants.QNAME_FAULT_NODE, builder);
    }
    
    public final SOAPFaultNode createSOAPFaultNode(final SOAPFault parent) {
        return this.createSOAPFaultNode(parent, null);
    }
    
    public final SOAPFaultNode createSOAPFaultNode() {
        return this.createSOAPFaultNode(null, null);
    }
    
    public final SOAPEnvelope getDefaultFaultEnvelope() {
        final SOAPEnvelope defaultEnvelope = this.getDefaultEnvelope();
        final SOAPFault fault = this.createSOAPFault(defaultEnvelope.getBody());
        final SOAPFaultCode faultCode = this.createSOAPFaultCode(fault);
        this.createSOAPFaultValue(faultCode);
        final SOAPFaultReason reason = this.createSOAPFaultReason(fault);
        this.createSOAPFaultText(reason);
        this.createSOAPFaultDetail(fault);
        return defaultEnvelope;
    }
}
