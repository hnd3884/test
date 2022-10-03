package org.apache.axiom.soap;

import org.apache.axiom.om.OMSourcedElement;

public interface SOAPHeaderBlock extends OMSourcedElement
{
    public static final String ROLE_PROPERTY = "org.apache.axiom.soap.SOAPHeader.ROLE";
    public static final String RELAY_PROPERTY = "org.apache.axiom.soap.SOAPHeader.RELAY";
    public static final String MUST_UNDERSTAND_PROPERTY = "org.apache.axiom.soap.SOAPHeader.MUST_UNDERSTAND";
    
    void setRole(final String p0);
    
    String getRole();
    
    void setMustUnderstand(final boolean p0);
    
    @Deprecated
    void setMustUnderstand(final String p0) throws SOAPProcessingException;
    
    boolean getMustUnderstand() throws SOAPProcessingException;
    
    boolean isProcessed();
    
    void setProcessed();
    
    void setRelay(final boolean p0);
    
    boolean getRelay();
    
    SOAPVersion getVersion();
}
