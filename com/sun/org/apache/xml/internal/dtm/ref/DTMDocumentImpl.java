package com.sun.org.apache.xml.internal.dtm.ref;

import javax.xml.transform.SourceLocator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.utils.XMLString;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.dtm.DTM;

public class DTMDocumentImpl implements DTM, ContentHandler, LexicalHandler
{
    protected static final byte DOCHANDLE_SHIFT = 22;
    protected static final int NODEHANDLE_MASK = 8388607;
    protected static final int DOCHANDLE_MASK = -8388608;
    int m_docHandle;
    int m_docElement;
    int currentParent;
    int previousSibling;
    protected int m_currentNode;
    private boolean previousSiblingWasParent;
    int[] gotslot;
    private boolean done;
    boolean m_isError;
    private final boolean DEBUG = false;
    protected String m_documentBaseURI;
    private IncrementalSAXSource m_incrSAXSource;
    ChunkedIntArray nodes;
    private FastStringBuffer m_char;
    private int m_char_current_start;
    private DTMStringPool m_localNames;
    private DTMStringPool m_nsNames;
    private DTMStringPool m_prefixNames;
    private ExpandedNameTable m_expandedNames;
    private XMLStringFactory m_xsf;
    private static final String[] fixednames;
    
    public DTMDocumentImpl(final DTMManager mgr, final int documentNumber, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory) {
        this.m_docHandle = -1;
        this.m_docElement = -1;
        this.currentParent = 0;
        this.previousSibling = 0;
        this.m_currentNode = -1;
        this.previousSiblingWasParent = false;
        this.gotslot = new int[4];
        this.done = false;
        this.m_isError = false;
        this.m_incrSAXSource = null;
        this.nodes = new ChunkedIntArray(4);
        this.m_char = new FastStringBuffer();
        this.m_char_current_start = 0;
        this.m_localNames = new DTMStringPool();
        this.m_nsNames = new DTMStringPool();
        this.m_prefixNames = new DTMStringPool();
        this.m_expandedNames = new ExpandedNameTable();
        this.initDocument(documentNumber);
        this.m_xsf = xstringfactory;
    }
    
    public void setIncrementalSAXSource(final IncrementalSAXSource source) {
        (this.m_incrSAXSource = source).setContentHandler(this);
        source.setLexicalHandler(this);
    }
    
    private final int appendNode(final int w0, final int w1, final int w2, final int w3) {
        final int slotnumber = this.nodes.appendSlot(w0, w1, w2, w3);
        if (this.previousSiblingWasParent) {
            this.nodes.writeEntry(this.previousSibling, 2, slotnumber);
        }
        this.previousSiblingWasParent = false;
        return slotnumber;
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) {
    }
    
    public void setLocalNameTable(final DTMStringPool poolRef) {
        this.m_localNames = poolRef;
    }
    
    public DTMStringPool getLocalNameTable() {
        return this.m_localNames;
    }
    
    public void setNsNameTable(final DTMStringPool poolRef) {
        this.m_nsNames = poolRef;
    }
    
    public DTMStringPool getNsNameTable() {
        return this.m_nsNames;
    }
    
    public void setPrefixNameTable(final DTMStringPool poolRef) {
        this.m_prefixNames = poolRef;
    }
    
    public DTMStringPool getPrefixNameTable() {
        return this.m_prefixNames;
    }
    
    void setContentBuffer(final FastStringBuffer buffer) {
        this.m_char = buffer;
    }
    
    FastStringBuffer getContentBuffer() {
        return this.m_char;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        if (this.m_incrSAXSource instanceof IncrementalSAXSource_Filter) {
            return (ContentHandler)this.m_incrSAXSource;
        }
        return this;
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        if (this.m_incrSAXSource instanceof IncrementalSAXSource_Filter) {
            return (LexicalHandler)this.m_incrSAXSource;
        }
        return this;
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }
    
    @Override
    public DeclHandler getDeclHandler() {
        return null;
    }
    
    @Override
    public boolean needsTwoThreads() {
        return null != this.m_incrSAXSource;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.m_char.append(ch, start, length);
    }
    
    private void processAccumulatedText() {
        final int len = this.m_char.length();
        if (len != this.m_char_current_start) {
            this.appendTextChild(this.m_char_current_start, len - this.m_char_current_start);
            this.m_char_current_start = len;
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.appendEndDocument();
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        this.processAccumulatedText();
        this.appendEndElement();
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.processAccumulatedText();
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        this.processAccumulatedText();
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.appendStartDocument();
    }
    
    @Override
    public void startElement(final String namespaceURI, String localName, String qName, final Attributes atts) throws SAXException {
        this.processAccumulatedText();
        String prefix = null;
        int colon = qName.indexOf(58);
        if (colon > 0) {
            prefix = qName.substring(0, colon);
        }
        System.out.println("Prefix=" + prefix + " index=" + this.m_prefixNames.stringToIndex(prefix));
        this.appendStartElement(this.m_nsNames.stringToIndex(namespaceURI), this.m_localNames.stringToIndex(localName), this.m_prefixNames.stringToIndex(prefix));
        final int nAtts = (atts == null) ? 0 : atts.getLength();
        for (int i = nAtts - 1; i >= 0; --i) {
            qName = atts.getQName(i);
            if (qName.startsWith("xmlns:") || "xmlns".equals(qName)) {
                prefix = null;
                colon = qName.indexOf(58);
                if (colon > 0) {
                    prefix = qName.substring(0, colon);
                }
                else {
                    prefix = null;
                }
                this.appendNSDeclaration(this.m_prefixNames.stringToIndex(prefix), this.m_nsNames.stringToIndex(atts.getValue(i)), atts.getType(i).equalsIgnoreCase("ID"));
            }
        }
        for (int i = nAtts - 1; i >= 0; --i) {
            qName = atts.getQName(i);
            if (!qName.startsWith("xmlns:") && !"xmlns".equals(qName)) {
                prefix = null;
                colon = qName.indexOf(58);
                if (colon > 0) {
                    prefix = qName.substring(0, colon);
                    localName = qName.substring(colon + 1);
                }
                else {
                    prefix = "";
                    localName = qName;
                }
                this.m_char.append(atts.getValue(i));
                final int contentEnd = this.m_char.length();
                if (!"xmlns".equals(prefix) && !"xmlns".equals(qName)) {
                    this.appendAttribute(this.m_nsNames.stringToIndex(atts.getURI(i)), this.m_localNames.stringToIndex(localName), this.m_prefixNames.stringToIndex(prefix), atts.getType(i).equalsIgnoreCase("ID"), this.m_char_current_start, contentEnd - this.m_char_current_start);
                }
                this.m_char_current_start = contentEnd;
            }
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        this.processAccumulatedText();
        this.m_char.append(ch, start, length);
        this.appendComment(this.m_char_current_start, length);
        this.m_char_current_start += length;
    }
    
    @Override
    public void endCDATA() throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    final void initDocument(final int documentNumber) {
        this.m_docHandle = documentNumber << 22;
        this.nodes.writeSlot(0, 9, -1, -1, 0);
        this.done = false;
    }
    
    @Override
    public boolean hasChildNodes(final int nodeHandle) {
        return this.getFirstChild(nodeHandle) != -1;
    }
    
    @Override
    public int getFirstChild(int nodeHandle) {
        nodeHandle &= 0x7FFFFF;
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final short type = (short)(this.gotslot[0] & 0xFFFF);
        if (type == 1 || type == 9 || type == 5) {
            int kid = nodeHandle + 1;
            this.nodes.readSlot(kid, this.gotslot);
            while (0x2 == (this.gotslot[0] & 0xFFFF)) {
                kid = this.gotslot[2];
                if (kid == -1) {
                    return -1;
                }
                this.nodes.readSlot(kid, this.gotslot);
            }
            if (this.gotslot[1] == nodeHandle) {
                final int firstChild = kid | this.m_docHandle;
                return firstChild;
            }
        }
        return -1;
    }
    
    @Override
    public int getLastChild(int nodeHandle) {
        nodeHandle &= 0x7FFFFF;
        int lastChild = -1;
        for (int nextkid = this.getFirstChild(nodeHandle); nextkid != -1; nextkid = this.getNextSibling(nextkid)) {
            lastChild = nextkid;
        }
        return lastChild | this.m_docHandle;
    }
    
    @Override
    public int getAttributeNode(int nodeHandle, final String namespaceURI, final String name) {
        final int nsIndex = this.m_nsNames.stringToIndex(namespaceURI);
        final int nameIndex = this.m_localNames.stringToIndex(name);
        nodeHandle &= 0x7FFFFF;
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final short type = (short)(this.gotslot[0] & 0xFFFF);
        if (type == 1) {
            ++nodeHandle;
        }
        while (type == 2) {
            if (nsIndex == this.gotslot[0] << 16 && this.gotslot[3] == nameIndex) {
                return nodeHandle | this.m_docHandle;
            }
            nodeHandle = this.gotslot[2];
            this.nodes.readSlot(nodeHandle, this.gotslot);
        }
        return -1;
    }
    
    @Override
    public int getFirstAttribute(int nodeHandle) {
        nodeHandle &= 0x7FFFFF;
        if (0x1 != (this.nodes.readEntry(nodeHandle, 0) & 0xFFFF)) {
            return -1;
        }
        ++nodeHandle;
        return (0x2 == (this.nodes.readEntry(nodeHandle, 0) & 0xFFFF)) ? (nodeHandle | this.m_docHandle) : -1;
    }
    
    @Override
    public int getFirstNamespaceNode(final int nodeHandle, final boolean inScope) {
        return -1;
    }
    
    @Override
    public int getNextSibling(int nodeHandle) {
        nodeHandle &= 0x7FFFFF;
        if (nodeHandle == 0) {
            return -1;
        }
        final short type = (short)(this.nodes.readEntry(nodeHandle, 0) & 0xFFFF);
        if (type == 1 || type == 2 || type == 5) {
            final int nextSib = this.nodes.readEntry(nodeHandle, 2);
            if (nextSib == -1) {
                return -1;
            }
            if (nextSib != 0) {
                return this.m_docHandle | nextSib;
            }
        }
        final int thisParent = this.nodes.readEntry(nodeHandle, 1);
        if (this.nodes.readEntry(++nodeHandle, 1) == thisParent) {
            return this.m_docHandle | nodeHandle;
        }
        return -1;
    }
    
    @Override
    public int getPreviousSibling(int nodeHandle) {
        nodeHandle &= 0x7FFFFF;
        if (nodeHandle == 0) {
            return -1;
        }
        final int parent = this.nodes.readEntry(nodeHandle, 1);
        int kid = -1;
        for (int nextkid = this.getFirstChild(parent); nextkid != nodeHandle; nextkid = this.getNextSibling(nextkid)) {
            kid = nextkid;
        }
        return kid | this.m_docHandle;
    }
    
    @Override
    public int getNextAttribute(int nodeHandle) {
        nodeHandle &= 0x7FFFFF;
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final short type = (short)(this.gotslot[0] & 0xFFFF);
        if (type == 1) {
            return this.getFirstAttribute(nodeHandle);
        }
        if (type == 2 && this.gotslot[2] != -1) {
            return this.m_docHandle | this.gotslot[2];
        }
        return -1;
    }
    
    @Override
    public int getNextNamespaceNode(final int baseHandle, final int namespaceHandle, final boolean inScope) {
        return -1;
    }
    
    public int getNextDescendant(int subtreeRootHandle, int nodeHandle) {
        subtreeRootHandle &= 0x7FFFFF;
        nodeHandle &= 0x7FFFFF;
        if (nodeHandle == 0) {
            return -1;
        }
        while (!this.m_isError) {
            if (this.done && nodeHandle > this.nodes.slotsUsed()) {
                break;
            }
            if (nodeHandle > subtreeRootHandle) {
                this.nodes.readSlot(nodeHandle + 1, this.gotslot);
                if (this.gotslot[2] != 0) {
                    final short type = (short)(this.gotslot[0] & 0xFFFF);
                    if (type == 2) {
                        nodeHandle += 2;
                    }
                    else {
                        final int nextParentPos = this.gotslot[1];
                        if (nextParentPos >= subtreeRootHandle) {
                            return this.m_docHandle | nodeHandle + 1;
                        }
                        break;
                    }
                }
                else {
                    if (!this.done) {
                        continue;
                    }
                    break;
                }
            }
            else {
                ++nodeHandle;
            }
        }
        return -1;
    }
    
    public int getNextFollowing(final int axisContextHandle, final int nodeHandle) {
        return -1;
    }
    
    public int getNextPreceding(final int axisContextHandle, int nodeHandle) {
        nodeHandle &= 0x7FFFFF;
        while (nodeHandle > 1) {
            --nodeHandle;
            if (0x2 == (this.nodes.readEntry(nodeHandle, 0) & 0xFFFF)) {
                continue;
            }
            return this.m_docHandle | this.nodes.specialFind(axisContextHandle, nodeHandle);
        }
        return -1;
    }
    
    @Override
    public int getParent(final int nodeHandle) {
        return this.m_docHandle | this.nodes.readEntry(nodeHandle, 1);
    }
    
    public int getDocumentRoot() {
        return this.m_docHandle | this.m_docElement;
    }
    
    @Override
    public int getDocument() {
        return this.m_docHandle;
    }
    
    @Override
    public int getOwnerDocument(final int nodeHandle) {
        if ((nodeHandle & 0x7FFFFF) == 0x0) {
            return -1;
        }
        return nodeHandle & 0xFF800000;
    }
    
    @Override
    public int getDocumentRoot(final int nodeHandle) {
        if ((nodeHandle & 0x7FFFFF) == 0x0) {
            return -1;
        }
        return nodeHandle & 0xFF800000;
    }
    
    @Override
    public XMLString getStringValue(final int nodeHandle) {
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final int nodetype = this.gotslot[0] & 0xFF;
        String value = null;
        switch (nodetype) {
            case 3:
            case 4:
            case 8: {
                value = this.m_char.getString(this.gotslot[2], this.gotslot[3]);
                break;
            }
        }
        return this.m_xsf.newstr(value);
    }
    
    @Override
    public int getStringValueChunkCount(final int nodeHandle) {
        return 0;
    }
    
    @Override
    public char[] getStringValueChunk(final int nodeHandle, final int chunkIndex, final int[] startAndLen) {
        return new char[0];
    }
    
    @Override
    public int getExpandedTypeID(final int nodeHandle) {
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final String qName = this.m_localNames.indexToString(this.gotslot[3]);
        final int colonpos = qName.indexOf(":");
        final String localName = qName.substring(colonpos + 1);
        final String namespace = this.m_nsNames.indexToString(this.gotslot[0] << 16);
        final String expandedName = namespace + ":" + localName;
        final int expandedNameID = this.m_nsNames.stringToIndex(expandedName);
        return expandedNameID;
    }
    
    @Override
    public int getExpandedTypeID(final String namespace, final String localName, final int type) {
        final String expandedName = namespace + ":" + localName;
        final int expandedNameID = this.m_nsNames.stringToIndex(expandedName);
        return expandedNameID;
    }
    
    @Override
    public String getLocalNameFromExpandedNameID(final int ExpandedNameID) {
        final String expandedName = this.m_localNames.indexToString(ExpandedNameID);
        final int colonpos = expandedName.indexOf(":");
        final String localName = expandedName.substring(colonpos + 1);
        return localName;
    }
    
    @Override
    public String getNamespaceFromExpandedNameID(final int ExpandedNameID) {
        final String expandedName = this.m_localNames.indexToString(ExpandedNameID);
        final int colonpos = expandedName.indexOf(":");
        final String nsName = expandedName.substring(0, colonpos);
        return nsName;
    }
    
    @Override
    public String getNodeName(final int nodeHandle) {
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final short type = (short)(this.gotslot[0] & 0xFFFF);
        String name = DTMDocumentImpl.fixednames[type];
        if (null == name) {
            final int i = this.gotslot[3];
            System.out.println("got i=" + i + " " + (i >> 16) + "/" + (i & 0xFFFF));
            name = this.m_localNames.indexToString(i & 0xFFFF);
            final String prefix = this.m_prefixNames.indexToString(i >> 16);
            if (prefix != null && prefix.length() > 0) {
                name = prefix + ":" + name;
            }
        }
        return name;
    }
    
    @Override
    public String getNodeNameX(final int nodeHandle) {
        return null;
    }
    
    @Override
    public String getLocalName(final int nodeHandle) {
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final short type = (short)(this.gotslot[0] & 0xFFFF);
        String name = "";
        if (type == 1 || type == 2) {
            final int i = this.gotslot[3];
            name = this.m_localNames.indexToString(i & 0xFFFF);
            if (name == null) {
                name = "";
            }
        }
        return name;
    }
    
    @Override
    public String getPrefix(final int nodeHandle) {
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final short type = (short)(this.gotslot[0] & 0xFFFF);
        String name = "";
        if (type == 1 || type == 2) {
            final int i = this.gotslot[3];
            name = this.m_prefixNames.indexToString(i >> 16);
            if (name == null) {
                name = "";
            }
        }
        return name;
    }
    
    @Override
    public String getNamespaceURI(final int nodeHandle) {
        return null;
    }
    
    @Override
    public String getNodeValue(final int nodeHandle) {
        this.nodes.readSlot(nodeHandle, this.gotslot);
        final int nodetype = this.gotslot[0] & 0xFF;
        String value = null;
        switch (nodetype) {
            case 2: {
                this.nodes.readSlot(nodeHandle + 1, this.gotslot);
            }
            case 3:
            case 4:
            case 8: {
                value = this.m_char.getString(this.gotslot[2], this.gotslot[3]);
                break;
            }
        }
        return value;
    }
    
    @Override
    public short getNodeType(final int nodeHandle) {
        return (short)(this.nodes.readEntry(nodeHandle, 0) & 0xFFFF);
    }
    
    @Override
    public short getLevel(int nodeHandle) {
        short count = 0;
        while (nodeHandle != 0) {
            ++count;
            nodeHandle = this.nodes.readEntry(nodeHandle, 1);
        }
        return count;
    }
    
    @Override
    public boolean isSupported(final String feature, final String version) {
        return false;
    }
    
    @Override
    public String getDocumentBaseURI() {
        return this.m_documentBaseURI;
    }
    
    @Override
    public void setDocumentBaseURI(final String baseURI) {
        this.m_documentBaseURI = baseURI;
    }
    
    @Override
    public String getDocumentSystemIdentifier(final int nodeHandle) {
        return null;
    }
    
    @Override
    public String getDocumentEncoding(final int nodeHandle) {
        return null;
    }
    
    @Override
    public String getDocumentStandalone(final int nodeHandle) {
        return null;
    }
    
    @Override
    public String getDocumentVersion(final int documentHandle) {
        return null;
    }
    
    @Override
    public boolean getDocumentAllDeclarationsProcessed() {
        return false;
    }
    
    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        return null;
    }
    
    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        return null;
    }
    
    @Override
    public int getElementById(final String elementId) {
        return 0;
    }
    
    @Override
    public String getUnparsedEntityURI(final String name) {
        return null;
    }
    
    @Override
    public boolean supportsPreStripping() {
        return false;
    }
    
    @Override
    public boolean isNodeAfter(final int nodeHandle1, final int nodeHandle2) {
        return false;
    }
    
    @Override
    public boolean isCharacterElementContentWhitespace(final int nodeHandle) {
        return false;
    }
    
    @Override
    public boolean isDocumentAllDeclarationsProcessed(final int documentHandle) {
        return false;
    }
    
    @Override
    public boolean isAttributeSpecified(final int attributeHandle) {
        return false;
    }
    
    @Override
    public void dispatchCharactersEvents(final int nodeHandle, final ContentHandler ch, final boolean normalize) throws SAXException {
    }
    
    @Override
    public void dispatchToEvents(final int nodeHandle, final ContentHandler ch) throws SAXException {
    }
    
    @Override
    public Node getNode(final int nodeHandle) {
        return null;
    }
    
    @Override
    public void appendChild(final int newChild, final boolean clone, final boolean cloneDepth) {
        final boolean sameDoc = (newChild & 0xFF800000) == this.m_docHandle;
        if (clone || !sameDoc) {}
    }
    
    @Override
    public void appendTextChild(final String str) {
    }
    
    void appendTextChild(final int m_char_current_start, final int contentLength) {
        final int w0 = 3;
        final int w2 = this.currentParent;
        final int w3 = m_char_current_start;
        final int w4 = contentLength;
        final int ourslot = this.appendNode(w0, w2, w3, w4);
        this.previousSibling = ourslot;
    }
    
    void appendComment(final int m_char_current_start, final int contentLength) {
        final int w0 = 8;
        final int w2 = this.currentParent;
        final int w3 = m_char_current_start;
        final int w4 = contentLength;
        final int ourslot = this.appendNode(w0, w2, w3, w4);
        this.previousSibling = ourslot;
    }
    
    void appendStartElement(final int namespaceIndex, final int localNameIndex, final int prefixIndex) {
        final int w0 = namespaceIndex << 16 | 0x1;
        final int w2 = this.currentParent;
        final int w3 = 0;
        final int w4 = localNameIndex | prefixIndex << 16;
        System.out.println("set w3=" + w4 + " " + (w4 >> 16) + "/" + (w4 & 0xFFFF));
        final int ourslot = this.appendNode(w0, w2, w3, w4);
        this.currentParent = ourslot;
        this.previousSibling = 0;
        if (this.m_docElement == -1) {
            this.m_docElement = ourslot;
        }
    }
    
    void appendNSDeclaration(final int prefixIndex, final int namespaceIndex, final boolean isID) {
        final int namespaceForNamespaces = this.m_nsNames.stringToIndex("http://www.w3.org/2000/xmlns/");
        final int w0 = 0xD | this.m_nsNames.stringToIndex("http://www.w3.org/2000/xmlns/") << 16;
        final int w2 = this.currentParent;
        final int w3 = 0;
        final int w4 = namespaceIndex;
        final int ourslot = this.appendNode(w0, w2, w3, w4);
        this.previousSibling = ourslot;
        this.previousSiblingWasParent = false;
    }
    
    void appendAttribute(final int namespaceIndex, final int localNameIndex, final int prefixIndex, final boolean isID, final int m_char_current_start, final int contentLength) {
        int w0 = 0x2 | namespaceIndex << 16;
        int w2 = this.currentParent;
        int w3 = 0;
        int w4 = localNameIndex | prefixIndex << 16;
        System.out.println("set w3=" + w4 + " " + (w4 >> 16) + "/" + (w4 & 0xFFFF));
        final int ourslot = this.appendNode(w0, w2, w3, w4);
        this.previousSibling = ourslot;
        w0 = 3;
        w2 = ourslot;
        w3 = m_char_current_start;
        w4 = contentLength;
        this.appendNode(w0, w2, w3, w4);
        this.previousSiblingWasParent = true;
    }
    
    @Override
    public DTMAxisTraverser getAxisTraverser(final int axis) {
        return null;
    }
    
    @Override
    public DTMAxisIterator getAxisIterator(final int axis) {
        return null;
    }
    
    @Override
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type) {
        return null;
    }
    
    void appendEndElement() {
        if (this.previousSiblingWasParent) {
            this.nodes.writeEntry(this.previousSibling, 2, -1);
        }
        this.previousSibling = this.currentParent;
        this.nodes.readSlot(this.currentParent, this.gotslot);
        this.currentParent = (this.gotslot[1] & 0xFFFF);
        this.previousSiblingWasParent = true;
    }
    
    void appendStartDocument() {
        this.m_docElement = -1;
        this.initDocument(0);
    }
    
    void appendEndDocument() {
        this.done = true;
    }
    
    @Override
    public void setProperty(final String property, final Object value) {
    }
    
    @Override
    public SourceLocator getSourceLocatorFor(final int node) {
        return null;
    }
    
    @Override
    public void documentRegistration() {
    }
    
    @Override
    public void documentRelease() {
    }
    
    @Override
    public void migrateTo(final DTMManager manager) {
    }
    
    static {
        fixednames = new String[] { null, null, null, "#text", "#cdata_section", null, null, null, "#comment", "#document", null, "#document-fragment", null };
    }
}
