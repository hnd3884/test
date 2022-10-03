package javax.xml.stream.events;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import javax.xml.namespace.QName;

public interface StartElement extends XMLEvent
{
    Attribute getAttributeByName(final QName p0);
    
    Iterator getAttributes();
    
    QName getName();
    
    NamespaceContext getNamespaceContext();
    
    Iterator getNamespaces();
    
    String getNamespaceURI(final String p0);
}
