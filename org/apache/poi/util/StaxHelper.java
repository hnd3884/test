package org.apache.poi.util;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;

@Deprecated
@Removal(version = "5.0.0")
public final class StaxHelper
{
    private StaxHelper() {
    }
    
    public static XMLInputFactory newXMLInputFactory() {
        return XMLHelper.newXMLInputFactory();
    }
    
    public static XMLOutputFactory newXMLOutputFactory() {
        return XMLHelper.newXMLOutputFactory();
    }
    
    public static XMLEventFactory newXMLEventFactory() {
        return XMLHelper.newXMLEventFactory();
    }
}
