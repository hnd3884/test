package org.apache.axiom.soap;

import org.apache.axiom.om.OMException;

public class SOAPProcessingException extends OMException
{
    private static final long serialVersionUID = -5432612295193716211L;
    private String soapFaultCode;
    
    public SOAPProcessingException(final String message) {
        super(message);
    }
    
    public SOAPProcessingException(final Throwable cause) {
        super(cause);
    }
    
    public SOAPProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public SOAPProcessingException(final String messageText, final String faultCode, final Throwable cause) {
        super(messageText, cause);
        this.soapFaultCode = faultCode;
    }
    
    public SOAPProcessingException(final String messageText, final String faultCode) {
        super(messageText);
        this.soapFaultCode = faultCode;
    }
    
    public String getFaultCode() {
        return this.soapFaultCode;
    }
    
    public void setFaultCode(final String soapFaultCode) {
        this.soapFaultCode = soapFaultCode;
    }
}
