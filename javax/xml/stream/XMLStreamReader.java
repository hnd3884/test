package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public interface XMLStreamReader extends XMLStreamConstants
{
    void close() throws XMLStreamException;
    
    int getAttributeCount();
    
    String getAttributeLocalName(final int p0);
    
    QName getAttributeName(final int p0);
    
    String getAttributeNamespace(final int p0);
    
    String getAttributePrefix(final int p0);
    
    String getAttributeType(final int p0);
    
    String getAttributeValue(final int p0);
    
    String getAttributeValue(final String p0, final String p1);
    
    String getCharacterEncodingScheme();
    
    String getElementText() throws XMLStreamException;
    
    String getEncoding();
    
    int getEventType();
    
    String getLocalName();
    
    Location getLocation();
    
    QName getName();
    
    NamespaceContext getNamespaceContext();
    
    int getNamespaceCount();
    
    String getNamespacePrefix(final int p0);
    
    String getNamespaceURI();
    
    String getNamespaceURI(final int p0);
    
    String getNamespaceURI(final String p0);
    
    String getPIData();
    
    String getPITarget();
    
    String getPrefix();
    
    Object getProperty(final String p0) throws IllegalArgumentException;
    
    String getText();
    
    char[] getTextCharacters();
    
    int getTextCharacters(final int p0, final char[] p1, final int p2, final int p3) throws XMLStreamException;
    
    int getTextLength();
    
    int getTextStart();
    
    String getVersion();
    
    boolean hasName();
    
    boolean hasNext() throws XMLStreamException;
    
    boolean hasText();
    
    boolean isAttributeSpecified(final int p0);
    
    boolean isCharacters();
    
    boolean isEndElement();
    
    boolean isStandalone();
    
    boolean isStartElement();
    
    boolean isWhiteSpace();
    
    int next() throws XMLStreamException;
    
    int nextTag() throws XMLStreamException;
    
    void require(final int p0, final String p1, final String p2) throws XMLStreamException;
    
    boolean standaloneSet();
}
