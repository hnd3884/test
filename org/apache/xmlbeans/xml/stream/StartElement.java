package org.apache.xmlbeans.xml.stream;

import java.util.Map;

public interface StartElement extends XMLEvent
{
    AttributeIterator getAttributes();
    
    AttributeIterator getNamespaces();
    
    AttributeIterator getAttributesAndNamespaces();
    
    Attribute getAttributeByName(final XMLName p0);
    
    String getNamespaceUri(final String p0);
    
    Map getNamespaceMap();
}
