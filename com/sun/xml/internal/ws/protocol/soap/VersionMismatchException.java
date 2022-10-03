package com.sun.xml.internal.ws.protocol.soap;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;
import com.sun.xml.internal.ws.encoding.soap.SOAPConstants;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

public class VersionMismatchException extends ExceptionHasMessage
{
    private final SOAPVersion soapVersion;
    
    public VersionMismatchException(final SOAPVersion soapVersion, final Object... args) {
        super("soap.version.mismatch.err", args);
        this.soapVersion = soapVersion;
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.soap";
    }
    
    @Override
    public Message getFaultMessage() {
        final QName faultCode = (this.soapVersion == SOAPVersion.SOAP_11) ? SOAPConstants.FAULT_CODE_VERSION_MISMATCH : SOAP12Constants.FAULT_CODE_VERSION_MISMATCH;
        return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.getLocalizedMessage(), faultCode);
    }
}
