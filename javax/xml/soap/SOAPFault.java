package javax.xml.soap;

import java.util.Locale;
import java.util.Iterator;
import javax.xml.namespace.QName;

public interface SOAPFault extends SOAPBodyElement
{
    void setFaultCode(final Name p0) throws SOAPException;
    
    void setFaultCode(final QName p0) throws SOAPException;
    
    void setFaultCode(final String p0) throws SOAPException;
    
    Name getFaultCodeAsName();
    
    QName getFaultCodeAsQName();
    
    Iterator getFaultSubcodes();
    
    void removeAllFaultSubcodes();
    
    void appendFaultSubcode(final QName p0) throws SOAPException;
    
    String getFaultCode();
    
    void setFaultActor(final String p0) throws SOAPException;
    
    String getFaultActor();
    
    void setFaultString(final String p0) throws SOAPException;
    
    void setFaultString(final String p0, final Locale p1) throws SOAPException;
    
    String getFaultString();
    
    Locale getFaultStringLocale();
    
    boolean hasDetail();
    
    Detail getDetail();
    
    Detail addDetail() throws SOAPException;
    
    Iterator getFaultReasonLocales() throws SOAPException;
    
    Iterator getFaultReasonTexts() throws SOAPException;
    
    String getFaultReasonText(final Locale p0) throws SOAPException;
    
    void addFaultReasonText(final String p0, final Locale p1) throws SOAPException;
    
    String getFaultNode();
    
    void setFaultNode(final String p0) throws SOAPException;
    
    String getFaultRole();
    
    void setFaultRole(final String p0) throws SOAPException;
}
