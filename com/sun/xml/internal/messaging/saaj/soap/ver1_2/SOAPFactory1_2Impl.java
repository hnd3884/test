package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import javax.xml.soap.SOAPFault;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Detail;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;

public class SOAPFactory1_2Impl extends SOAPFactoryImpl
{
    @Override
    protected SOAPDocumentImpl createDocument() {
        return new SOAPPart1_2Impl().getDocument();
    }
    
    @Override
    public Detail createDetail() throws SOAPException {
        return new Detail1_2Impl(this.createDocument());
    }
    
    @Override
    public SOAPFault createFault(final String reasonText, final QName faultCode) throws SOAPException {
        if (faultCode == null) {
            throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
        }
        if (reasonText == null) {
            throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
        }
        final Fault1_2Impl fault = new Fault1_2Impl(this.createDocument(), (String)null);
        fault.setFaultCode(faultCode);
        fault.setFaultString(reasonText);
        return fault;
    }
    
    @Override
    public SOAPFault createFault() throws SOAPException {
        final Fault1_2Impl fault = new Fault1_2Impl(this.createDocument(), (String)null);
        fault.setFaultCode(fault.getDefaultFaultCode());
        fault.setFaultString("Fault string, and possibly fault code, not set");
        return fault;
    }
}
