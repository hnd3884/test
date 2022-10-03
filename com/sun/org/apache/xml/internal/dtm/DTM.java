package com.sun.org.apache.xml.internal.dtm;

import javax.xml.transform.SourceLocator;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.LexicalHandler;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.utils.XMLString;

public interface DTM
{
    public static final int NULL = -1;
    public static final short ROOT_NODE = 0;
    public static final short ELEMENT_NODE = 1;
    public static final short ATTRIBUTE_NODE = 2;
    public static final short TEXT_NODE = 3;
    public static final short CDATA_SECTION_NODE = 4;
    public static final short ENTITY_REFERENCE_NODE = 5;
    public static final short ENTITY_NODE = 6;
    public static final short PROCESSING_INSTRUCTION_NODE = 7;
    public static final short COMMENT_NODE = 8;
    public static final short DOCUMENT_NODE = 9;
    public static final short DOCUMENT_TYPE_NODE = 10;
    public static final short DOCUMENT_FRAGMENT_NODE = 11;
    public static final short NOTATION_NODE = 12;
    public static final short NAMESPACE_NODE = 13;
    public static final short NTYPES = 14;
    
    void setFeature(final String p0, final boolean p1);
    
    void setProperty(final String p0, final Object p1);
    
    DTMAxisTraverser getAxisTraverser(final int p0);
    
    DTMAxisIterator getAxisIterator(final int p0);
    
    DTMAxisIterator getTypedAxisIterator(final int p0, final int p1);
    
    boolean hasChildNodes(final int p0);
    
    int getFirstChild(final int p0);
    
    int getLastChild(final int p0);
    
    int getAttributeNode(final int p0, final String p1, final String p2);
    
    int getFirstAttribute(final int p0);
    
    int getFirstNamespaceNode(final int p0, final boolean p1);
    
    int getNextSibling(final int p0);
    
    int getPreviousSibling(final int p0);
    
    int getNextAttribute(final int p0);
    
    int getNextNamespaceNode(final int p0, final int p1, final boolean p2);
    
    int getParent(final int p0);
    
    int getDocument();
    
    int getOwnerDocument(final int p0);
    
    int getDocumentRoot(final int p0);
    
    XMLString getStringValue(final int p0);
    
    int getStringValueChunkCount(final int p0);
    
    char[] getStringValueChunk(final int p0, final int p1, final int[] p2);
    
    int getExpandedTypeID(final int p0);
    
    int getExpandedTypeID(final String p0, final String p1, final int p2);
    
    String getLocalNameFromExpandedNameID(final int p0);
    
    String getNamespaceFromExpandedNameID(final int p0);
    
    String getNodeName(final int p0);
    
    String getNodeNameX(final int p0);
    
    String getLocalName(final int p0);
    
    String getPrefix(final int p0);
    
    String getNamespaceURI(final int p0);
    
    String getNodeValue(final int p0);
    
    short getNodeType(final int p0);
    
    short getLevel(final int p0);
    
    boolean isSupported(final String p0, final String p1);
    
    String getDocumentBaseURI();
    
    void setDocumentBaseURI(final String p0);
    
    String getDocumentSystemIdentifier(final int p0);
    
    String getDocumentEncoding(final int p0);
    
    String getDocumentStandalone(final int p0);
    
    String getDocumentVersion(final int p0);
    
    boolean getDocumentAllDeclarationsProcessed();
    
    String getDocumentTypeDeclarationSystemIdentifier();
    
    String getDocumentTypeDeclarationPublicIdentifier();
    
    int getElementById(final String p0);
    
    String getUnparsedEntityURI(final String p0);
    
    boolean supportsPreStripping();
    
    boolean isNodeAfter(final int p0, final int p1);
    
    boolean isCharacterElementContentWhitespace(final int p0);
    
    boolean isDocumentAllDeclarationsProcessed(final int p0);
    
    boolean isAttributeSpecified(final int p0);
    
    void dispatchCharactersEvents(final int p0, final ContentHandler p1, final boolean p2) throws SAXException;
    
    void dispatchToEvents(final int p0, final ContentHandler p1) throws SAXException;
    
    Node getNode(final int p0);
    
    boolean needsTwoThreads();
    
    ContentHandler getContentHandler();
    
    LexicalHandler getLexicalHandler();
    
    EntityResolver getEntityResolver();
    
    DTDHandler getDTDHandler();
    
    ErrorHandler getErrorHandler();
    
    DeclHandler getDeclHandler();
    
    void appendChild(final int p0, final boolean p1, final boolean p2);
    
    void appendTextChild(final String p0);
    
    SourceLocator getSourceLocatorFor(final int p0);
    
    void documentRegistration();
    
    void documentRelease();
    
    void migrateTo(final DTMManager p0);
}
