package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import javax.xml.transform.SourceLocator;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.utils.XMLStringDefault;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import java.util.Map;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xml.internal.serializer.EmptySerializer;

public class SimpleResultTreeImpl extends EmptySerializer implements DOM, DTM
{
    private static final DTMAxisIterator EMPTY_ITERATOR;
    public static final int RTF_ROOT = 0;
    public static final int RTF_TEXT = 1;
    public static final int NUMBER_OF_NODES = 2;
    private static int _documentURIIndex;
    private static final String EMPTY_STR = "";
    private String _text;
    protected String[] _textArray;
    protected XSLTCDTMManager _dtmManager;
    protected int _size;
    private int _documentID;
    private BitArray _dontEscape;
    private boolean _escaping;
    
    public SimpleResultTreeImpl(final XSLTCDTMManager dtmManager, final int documentID) {
        this._size = 0;
        this._dontEscape = null;
        this._escaping = true;
        this._dtmManager = dtmManager;
        this._documentID = documentID;
        this._textArray = new String[4];
    }
    
    public DTMManagerDefault getDTMManager() {
        return this._dtmManager;
    }
    
    @Override
    public int getDocument() {
        return this._documentID;
    }
    
    @Override
    public String getStringValue() {
        return this._text;
    }
    
    @Override
    public DTMAxisIterator getIterator() {
        return new SingletonIterator(this.getDocument());
    }
    
    @Override
    public DTMAxisIterator getChildren(final int node) {
        return new SimpleIterator().setStartNode(node);
    }
    
    @Override
    public DTMAxisIterator getTypedChildren(final int type) {
        return new SimpleIterator(1, type);
    }
    
    @Override
    public DTMAxisIterator getAxisIterator(final int axis) {
        switch (axis) {
            case 3:
            case 4: {
                return new SimpleIterator(1);
            }
            case 0:
            case 10: {
                return new SimpleIterator(0);
            }
            case 1: {
                return new SimpleIterator(0).includeSelf();
            }
            case 5: {
                return new SimpleIterator(1).includeSelf();
            }
            case 13: {
                return new SingletonIterator();
            }
            default: {
                return SimpleResultTreeImpl.EMPTY_ITERATOR;
            }
        }
    }
    
    @Override
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type) {
        switch (axis) {
            case 3:
            case 4: {
                return new SimpleIterator(1, type);
            }
            case 0:
            case 10: {
                return new SimpleIterator(0, type);
            }
            case 1: {
                return new SimpleIterator(0, type).includeSelf();
            }
            case 5: {
                return new SimpleIterator(1, type).includeSelf();
            }
            case 13: {
                return new SingletonIterator(type);
            }
            default: {
                return SimpleResultTreeImpl.EMPTY_ITERATOR;
            }
        }
    }
    
    @Override
    public DTMAxisIterator getNthDescendant(final int node, final int n, final boolean includeself) {
        return null;
    }
    
    @Override
    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns) {
        return null;
    }
    
    @Override
    public DTMAxisIterator getNodeValueIterator(final DTMAxisIterator iter, final int returnType, final String value, final boolean op) {
        return null;
    }
    
    @Override
    public DTMAxisIterator orderNodes(final DTMAxisIterator source, final int node) {
        return source;
    }
    
    @Override
    public String getNodeName(final int node) {
        if (this.getNodeIdent(node) == 1) {
            return "#text";
        }
        return "";
    }
    
    @Override
    public String getNodeNameX(final int node) {
        return "";
    }
    
    @Override
    public String getNamespaceName(final int node) {
        return "";
    }
    
    @Override
    public int getExpandedTypeID(final int nodeHandle) {
        final int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 1) {
            return 3;
        }
        if (nodeID == 0) {
            return 0;
        }
        return -1;
    }
    
    @Override
    public int getNamespaceType(final int node) {
        return 0;
    }
    
    @Override
    public int getParent(final int nodeHandle) {
        final int nodeID = this.getNodeIdent(nodeHandle);
        return (nodeID == 1) ? this.getNodeHandle(0) : -1;
    }
    
    @Override
    public int getAttributeNode(final int gType, final int element) {
        return -1;
    }
    
    @Override
    public String getStringValueX(final int nodeHandle) {
        final int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 0 || nodeID == 1) {
            return this._text;
        }
        return "";
    }
    
    @Override
    public void copy(final int node, final SerializationHandler handler) throws TransletException {
        this.characters(node, handler);
    }
    
    @Override
    public void copy(final DTMAxisIterator nodes, final SerializationHandler handler) throws TransletException {
        int node;
        while ((node = nodes.next()) != -1) {
            this.copy(node, handler);
        }
    }
    
    @Override
    public String shallowCopy(final int node, final SerializationHandler handler) throws TransletException {
        this.characters(node, handler);
        return null;
    }
    
    @Override
    public boolean lessThan(final int node1, final int node2) {
        return node1 != -1 && (node2 == -1 || node1 < node2);
    }
    
    @Override
    public void characters(final int node, final SerializationHandler handler) throws TransletException {
        final int nodeID = this.getNodeIdent(node);
        if (nodeID == 0 || nodeID == 1) {
            boolean escapeBit = false;
            boolean oldEscapeSetting = false;
            try {
                for (int i = 0; i < this._size; ++i) {
                    if (this._dontEscape != null) {
                        escapeBit = this._dontEscape.getBit(i);
                        if (escapeBit) {
                            oldEscapeSetting = handler.setEscaping(false);
                        }
                    }
                    handler.characters(this._textArray[i]);
                    if (escapeBit) {
                        handler.setEscaping(oldEscapeSetting);
                    }
                }
            }
            catch (final SAXException e) {
                throw new TransletException(e);
            }
        }
    }
    
    @Override
    public Node makeNode(final int index) {
        return null;
    }
    
    @Override
    public Node makeNode(final DTMAxisIterator iter) {
        return null;
    }
    
    @Override
    public NodeList makeNodeList(final int index) {
        return null;
    }
    
    @Override
    public NodeList makeNodeList(final DTMAxisIterator iter) {
        return null;
    }
    
    @Override
    public String getLanguage(final int node) {
        return null;
    }
    
    @Override
    public int getSize() {
        return 2;
    }
    
    @Override
    public String getDocumentURI(final int node) {
        return "simple_rtf" + SimpleResultTreeImpl._documentURIIndex++;
    }
    
    @Override
    public void setFilter(final StripFilter filter) {
    }
    
    @Override
    public void setupMapping(final String[] names, final String[] uris, final int[] types, final String[] namespaces) {
    }
    
    @Override
    public boolean isElement(final int node) {
        return false;
    }
    
    @Override
    public boolean isAttribute(final int node) {
        return false;
    }
    
    @Override
    public String lookupNamespace(final int node, final String prefix) throws TransletException {
        return null;
    }
    
    @Override
    public int getNodeIdent(final int nodehandle) {
        return (nodehandle != -1) ? (nodehandle - this._documentID) : -1;
    }
    
    @Override
    public int getNodeHandle(final int nodeId) {
        return (nodeId != -1) ? (nodeId + this._documentID) : -1;
    }
    
    @Override
    public DOM getResultTreeFrag(final int initialSize, final int rtfType) {
        return null;
    }
    
    @Override
    public DOM getResultTreeFrag(final int initialSize, final int rtfType, final boolean addToManager) {
        return null;
    }
    
    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this;
    }
    
    @Override
    public int getNSType(final int node) {
        return 0;
    }
    
    @Override
    public String getUnparsedEntityURI(final String name) {
        return null;
    }
    
    @Override
    public Map<String, Integer> getElementsWithIDs() {
        return null;
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
        if (this._size == 1) {
            this._text = this._textArray[0];
        }
        else {
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < this._size; ++i) {
                buffer.append(this._textArray[i]);
            }
            this._text = buffer.toString();
        }
    }
    
    @Override
    public void characters(final String str) throws SAXException {
        if (this._size >= this._textArray.length) {
            final String[] newTextArray = new String[this._textArray.length * 2];
            System.arraycopy(this._textArray, 0, newTextArray, 0, this._textArray.length);
            this._textArray = newTextArray;
        }
        if (!this._escaping) {
            if (this._dontEscape == null) {
                this._dontEscape = new BitArray(8);
            }
            if (this._size >= this._dontEscape.size()) {
                this._dontEscape.resize(this._dontEscape.size() * 2);
            }
            this._dontEscape.setBit(this._size);
        }
        this._textArray[this._size++] = str;
    }
    
    @Override
    public void characters(final char[] ch, final int offset, final int length) throws SAXException {
        if (this._size >= this._textArray.length) {
            final String[] newTextArray = new String[this._textArray.length * 2];
            System.arraycopy(this._textArray, 0, newTextArray, 0, this._textArray.length);
            this._textArray = newTextArray;
        }
        if (!this._escaping) {
            if (this._dontEscape == null) {
                this._dontEscape = new BitArray(8);
            }
            if (this._size >= this._dontEscape.size()) {
                this._dontEscape.resize(this._dontEscape.size() * 2);
            }
            this._dontEscape.setBit(this._size);
        }
        this._textArray[this._size++] = new String(ch, offset, length);
    }
    
    @Override
    public boolean setEscaping(final boolean escape) throws SAXException {
        final boolean temp = this._escaping;
        this._escaping = escape;
        return temp;
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) {
    }
    
    @Override
    public void setProperty(final String property, final Object value) {
    }
    
    @Override
    public DTMAxisTraverser getAxisTraverser(final int axis) {
        return null;
    }
    
    @Override
    public boolean hasChildNodes(final int nodeHandle) {
        return this.getNodeIdent(nodeHandle) == 0;
    }
    
    @Override
    public int getFirstChild(final int nodeHandle) {
        final int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 0) {
            return this.getNodeHandle(1);
        }
        return -1;
    }
    
    @Override
    public int getLastChild(final int nodeHandle) {
        return this.getFirstChild(nodeHandle);
    }
    
    @Override
    public int getAttributeNode(final int elementHandle, final String namespaceURI, final String name) {
        return -1;
    }
    
    @Override
    public int getFirstAttribute(final int nodeHandle) {
        return -1;
    }
    
    @Override
    public int getFirstNamespaceNode(final int nodeHandle, final boolean inScope) {
        return -1;
    }
    
    @Override
    public int getNextSibling(final int nodeHandle) {
        return -1;
    }
    
    @Override
    public int getPreviousSibling(final int nodeHandle) {
        return -1;
    }
    
    @Override
    public int getNextAttribute(final int nodeHandle) {
        return -1;
    }
    
    @Override
    public int getNextNamespaceNode(final int baseHandle, final int namespaceHandle, final boolean inScope) {
        return -1;
    }
    
    @Override
    public int getOwnerDocument(final int nodeHandle) {
        return this.getDocument();
    }
    
    @Override
    public int getDocumentRoot(final int nodeHandle) {
        return this.getDocument();
    }
    
    @Override
    public XMLString getStringValue(final int nodeHandle) {
        return new XMLStringDefault(this.getStringValueX(nodeHandle));
    }
    
    @Override
    public int getStringValueChunkCount(final int nodeHandle) {
        return 0;
    }
    
    @Override
    public char[] getStringValueChunk(final int nodeHandle, final int chunkIndex, final int[] startAndLen) {
        return null;
    }
    
    @Override
    public int getExpandedTypeID(final String namespace, final String localName, final int type) {
        return -1;
    }
    
    @Override
    public String getLocalNameFromExpandedNameID(final int ExpandedNameID) {
        return "";
    }
    
    @Override
    public String getNamespaceFromExpandedNameID(final int ExpandedNameID) {
        return "";
    }
    
    @Override
    public String getLocalName(final int nodeHandle) {
        return "";
    }
    
    @Override
    public String getPrefix(final int nodeHandle) {
        return null;
    }
    
    @Override
    public String getNamespaceURI(final int nodeHandle) {
        return "";
    }
    
    @Override
    public String getNodeValue(final int nodeHandle) {
        return (this.getNodeIdent(nodeHandle) == 1) ? this._text : null;
    }
    
    @Override
    public short getNodeType(final int nodeHandle) {
        final int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 1) {
            return 3;
        }
        if (nodeID == 0) {
            return 0;
        }
        return -1;
    }
    
    @Override
    public short getLevel(final int nodeHandle) {
        final int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 1) {
            return 2;
        }
        if (nodeID == 0) {
            return 1;
        }
        return -1;
    }
    
    @Override
    public boolean isSupported(final String feature, final String version) {
        return false;
    }
    
    @Override
    public String getDocumentBaseURI() {
        return "";
    }
    
    @Override
    public void setDocumentBaseURI(final String baseURI) {
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
        return -1;
    }
    
    @Override
    public boolean supportsPreStripping() {
        return false;
    }
    
    @Override
    public boolean isNodeAfter(final int firstNodeHandle, final int secondNodeHandle) {
        return this.lessThan(firstNodeHandle, secondNodeHandle);
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
        return this.makeNode(nodeHandle);
    }
    
    @Override
    public boolean needsTwoThreads() {
        return false;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return null;
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        return null;
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
    public void appendChild(final int newChild, final boolean clone, final boolean cloneDepth) {
    }
    
    @Override
    public void appendTextChild(final String str) {
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
    
    @Override
    public void release() {
        if (this._documentID != 0) {
            this._dtmManager.release(this, true);
            this._documentID = 0;
        }
    }
    
    static {
        EMPTY_ITERATOR = new DTMAxisIteratorBase() {
            @Override
            public DTMAxisIterator reset() {
                return this;
            }
            
            @Override
            public DTMAxisIterator setStartNode(final int node) {
                return this;
            }
            
            @Override
            public int next() {
                return -1;
            }
            
            @Override
            public void setMark() {
            }
            
            @Override
            public void gotoMark() {
            }
            
            @Override
            public int getLast() {
                return 0;
            }
            
            @Override
            public int getPosition() {
                return 0;
            }
            
            @Override
            public DTMAxisIterator cloneIterator() {
                return this;
            }
            
            @Override
            public void setRestartable(final boolean isRestartable) {
            }
        };
        SimpleResultTreeImpl._documentURIIndex = 0;
    }
    
    public final class SimpleIterator extends DTMAxisIteratorBase
    {
        static final int DIRECTION_UP = 0;
        static final int DIRECTION_DOWN = 1;
        static final int NO_TYPE = -1;
        int _direction;
        int _type;
        int _currentNode;
        
        public SimpleIterator() {
            this._direction = 1;
            this._type = -1;
        }
        
        public SimpleIterator(final int direction) {
            this._direction = 1;
            this._type = -1;
            this._direction = direction;
        }
        
        public SimpleIterator(final int direction, final int type) {
            this._direction = 1;
            this._type = -1;
            this._direction = direction;
            this._type = type;
        }
        
        @Override
        public int next() {
            if (this._direction == 1) {
                while (this._currentNode < 2) {
                    if (this._type == -1) {
                        return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode++));
                    }
                    if ((this._currentNode == 0 && this._type == 0) || (this._currentNode == 1 && this._type == 3)) {
                        return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode++));
                    }
                    ++this._currentNode;
                }
                return -1;
            }
            while (this._currentNode >= 0) {
                if (this._type == -1) {
                    return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode--));
                }
                if ((this._currentNode == 0 && this._type == 0) || (this._currentNode == 1 && this._type == 3)) {
                    return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode--));
                }
                --this._currentNode;
            }
            return -1;
        }
        
        @Override
        public DTMAxisIterator setStartNode(final int nodeHandle) {
            int nodeID = SimpleResultTreeImpl.this.getNodeIdent(nodeHandle);
            this._startNode = nodeID;
            if (!this._includeSelf && nodeID != -1) {
                if (this._direction == 1) {
                    ++nodeID;
                }
                else if (this._direction == 0) {
                    --nodeID;
                }
            }
            this._currentNode = nodeID;
            return this;
        }
        
        @Override
        public void setMark() {
            this._markedNode = this._currentNode;
        }
        
        @Override
        public void gotoMark() {
            this._currentNode = this._markedNode;
        }
    }
    
    public final class SingletonIterator extends DTMAxisIteratorBase
    {
        static final int NO_TYPE = -1;
        int _type;
        int _currentNode;
        
        public SingletonIterator() {
            this._type = -1;
        }
        
        public SingletonIterator(final int type) {
            this._type = -1;
            this._type = type;
        }
        
        @Override
        public void setMark() {
            this._markedNode = this._currentNode;
        }
        
        @Override
        public void gotoMark() {
            this._currentNode = this._markedNode;
        }
        
        @Override
        public DTMAxisIterator setStartNode(final int nodeHandle) {
            final int nodeIdent = SimpleResultTreeImpl.this.getNodeIdent(nodeHandle);
            this._startNode = nodeIdent;
            this._currentNode = nodeIdent;
            return this;
        }
        
        @Override
        public int next() {
            if (this._currentNode == -1) {
                return -1;
            }
            this._currentNode = -1;
            if (this._type == -1) {
                return SimpleResultTreeImpl.this.getNodeHandle(this._currentNode);
            }
            if ((this._currentNode == 0 && this._type == 0) || (this._currentNode == 1 && this._type == 3)) {
                return SimpleResultTreeImpl.this.getNodeHandle(this._currentNode);
            }
            return -1;
        }
    }
}
