package org.apache.xmlbeans.impl.soap;

import java.util.Iterator;

public interface SOAPHeader extends SOAPElement
{
    SOAPHeaderElement addHeaderElement(final Name p0) throws SOAPException;
    
    Iterator examineHeaderElements(final String p0);
    
    Iterator extractHeaderElements(final String p0);
    
    Iterator examineMustUnderstandHeaderElements(final String p0);
    
    Iterator examineAllHeaderElements();
    
    Iterator extractAllHeaderElements();
}
