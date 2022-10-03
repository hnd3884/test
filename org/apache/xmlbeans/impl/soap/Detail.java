package org.apache.xmlbeans.impl.soap;

import java.util.Iterator;

public interface Detail extends SOAPFaultElement
{
    DetailEntry addDetailEntry(final Name p0) throws SOAPException;
    
    Iterator getDetailEntries();
}
