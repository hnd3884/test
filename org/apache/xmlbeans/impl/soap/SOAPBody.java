package org.apache.xmlbeans.impl.soap;

import org.w3c.dom.Document;
import java.util.Locale;

public interface SOAPBody extends SOAPElement
{
    SOAPFault addFault() throws SOAPException;
    
    boolean hasFault();
    
    SOAPFault getFault();
    
    SOAPBodyElement addBodyElement(final Name p0) throws SOAPException;
    
    SOAPFault addFault(final Name p0, final String p1, final Locale p2) throws SOAPException;
    
    SOAPFault addFault(final Name p0, final String p1) throws SOAPException;
    
    SOAPBodyElement addDocument(final Document p0) throws SOAPException;
}
