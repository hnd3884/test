package org.apache.axiom.om.impl.common.serializer.pull;

import org.apache.axiom.om.OMDataSource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.ext.stax.DTDReader;

abstract class PullSerializerState
{
    abstract DTDReader getDTDReader();
    
    abstract DataHandlerReader getDataHandlerReader();
    
    abstract CharacterDataReader getCharacterDataReader();
    
    abstract int getEventType();
    
    abstract boolean hasNext() throws XMLStreamException;
    
    abstract void next() throws XMLStreamException;
    
    abstract int nextTag() throws XMLStreamException;
    
    abstract Object getProperty(final String p0) throws IllegalArgumentException;
    
    abstract String getVersion();
    
    abstract String getCharacterEncodingScheme();
    
    abstract String getEncoding();
    
    abstract boolean isStandalone();
    
    abstract boolean standaloneSet();
    
    abstract String getPrefix();
    
    abstract String getNamespaceURI();
    
    abstract String getLocalName();
    
    abstract QName getName();
    
    abstract int getNamespaceCount();
    
    abstract String getNamespacePrefix(final int p0);
    
    abstract String getNamespaceURI(final int p0);
    
    abstract int getAttributeCount();
    
    abstract String getAttributePrefix(final int p0);
    
    abstract String getAttributeNamespace(final int p0);
    
    abstract String getAttributeLocalName(final int p0);
    
    abstract QName getAttributeName(final int p0);
    
    abstract boolean isAttributeSpecified(final int p0);
    
    abstract String getAttributeType(final int p0);
    
    abstract String getAttributeValue(final int p0);
    
    abstract String getAttributeValue(final String p0, final String p1);
    
    abstract NamespaceContext getNamespaceContext();
    
    abstract String getNamespaceURI(final String p0);
    
    abstract String getElementText() throws XMLStreamException;
    
    abstract String getText();
    
    abstract char[] getTextCharacters();
    
    abstract int getTextStart();
    
    abstract int getTextLength();
    
    abstract int getTextCharacters(final int p0, final char[] p1, final int p2, final int p3) throws XMLStreamException;
    
    abstract Boolean isWhiteSpace();
    
    abstract String getPIData();
    
    abstract String getPITarget();
    
    abstract OMDataSource getDataSource();
    
    abstract void released() throws XMLStreamException;
    
    abstract void restored() throws XMLStreamException;
}
