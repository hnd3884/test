package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;

public interface StAXDialect
{
    String getName();
    
    XMLInputFactory enableCDataReporting(final XMLInputFactory p0);
    
    XMLInputFactory disallowDoctypeDecl(final XMLInputFactory p0);
    
    XMLInputFactory makeThreadSafe(final XMLInputFactory p0);
    
    XMLOutputFactory makeThreadSafe(final XMLOutputFactory p0);
    
    XMLInputFactory normalize(final XMLInputFactory p0);
    
    XMLOutputFactory normalize(final XMLOutputFactory p0);
}
