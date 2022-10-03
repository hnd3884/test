package org.apache.xmlbeans.impl.soap;

import java.util.Locale;

public interface SOAPFault extends SOAPBodyElement
{
    void setFaultCode(final String p0) throws SOAPException;
    
    String getFaultCode();
    
    void setFaultActor(final String p0) throws SOAPException;
    
    String getFaultActor();
    
    void setFaultString(final String p0) throws SOAPException;
    
    String getFaultString();
    
    Detail getDetail();
    
    Detail addDetail() throws SOAPException;
    
    void setFaultCode(final Name p0) throws SOAPException;
    
    Name getFaultCodeAsName();
    
    void setFaultString(final String p0, final Locale p1) throws SOAPException;
    
    Locale getFaultStringLocale();
}
