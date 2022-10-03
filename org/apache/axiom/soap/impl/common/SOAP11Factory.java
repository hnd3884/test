package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMMetaFactory;

public class SOAP11Factory extends SOAPFactoryImpl
{
    public SOAP11Factory(final OMMetaFactory metaFactory, final NodeFactory nodeFactory) {
        super(metaFactory, nodeFactory);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return SOAPHelper.SOAP11;
    }
    
    public final SOAPFaultValue createSOAPFaultValue(final SOAPFaultCode parent) {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultValue createSOAPFaultValue(final SOAPFaultSubCode parent) {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultValue createSOAPFaultValue() {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultCode parent) {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultSubCode parent) {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultSubCode createSOAPFaultSubCode() {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultText createSOAPFaultText(final SOAPFaultReason parent) {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultText createSOAPFaultText() {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultNode createSOAPFaultNode(final SOAPFault parent) {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPFaultNode createSOAPFaultNode() {
        throw new UnsupportedOperationException();
    }
    
    public final SOAPEnvelope getDefaultFaultEnvelope() {
        final SOAPEnvelope defaultEnvelope = this.getDefaultEnvelope();
        final SOAPFault fault = this.createSOAPFault(defaultEnvelope.getBody());
        this.createSOAPFaultCode(fault);
        this.createSOAPFaultReason(fault);
        this.createSOAPFaultDetail(fault);
        return defaultEnvelope;
    }
}
