package org.apache.axiom.soap;

import java.util.Iterator;
import org.apache.axiom.om.OMElement;

public interface SOAPFaultDetail extends OMElement
{
    void addDetailEntry(final OMElement p0);
    
    Iterator getAllDetailEntries();
}
