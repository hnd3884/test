package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import javax.xml.soap.SOAPFault;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Detail;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;

public class SOAPFactory1_1Impl extends SOAPFactoryImpl
{
    @Override
    protected SOAPDocumentImpl createDocument() {
        return new SOAPPart1_1Impl().getDocument();
    }
    
    @Override
    public Detail createDetail() throws SOAPException {
        return new Detail1_1Impl(this.createDocument());
    }
    
    @Override
    public SOAPFault createFault(final String reasonText, final QName faultCode) throws SOAPException {
        if (faultCode == null) {
            throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
        }
        if (reasonText == null) {
            throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
        }
        final Fault1_1Impl fault = new Fault1_1Impl(this.createDocument(), (String)null);
        fault.setFaultCode(faultCode);
        fault.setFaultString(reasonText);
        return fault;
    }
    
    @Override
    public SOAPFault createFault() throws SOAPException {
        final Fault1_1Impl fault = new Fault1_1Impl(this.createDocument(), (String)null);
        fault.setFaultCode(fault.getDefaultFaultCode());
        fault.setFaultString("Fault string, and possibly fault code, not set");
        return fault;
    }
}
