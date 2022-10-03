package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.SOAPProcessingException;
import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public interface AxiomSOAPHeaderBlock extends AxiomSOAPElement, AxiomSourcedElement, SOAPHeaderBlock
{
    boolean getMustUnderstand() throws SOAPProcessingException;
    
    boolean getRelay();
    
    String getRole();
    
    SOAPVersion getVersion();
    
     <T> void initAncillaryData(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
    
    boolean isProcessed();
    
    void setMustUnderstand(final String p0) throws SOAPProcessingException;
    
    void setMustUnderstand(final boolean p0);
    
    void setProcessed();
    
    void setRelay(final boolean p0);
    
    void setRole(final String p0);
}
