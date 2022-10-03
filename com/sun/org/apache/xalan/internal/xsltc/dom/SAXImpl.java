package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.dtm.Axis;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.transform.dom.DOMSource;
import java.util.HashMap;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import java.util.Map;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;

public final class SAXImpl extends SAX2DTM2 implements DOMEnhancedForDTM, DOMBuilder
{
    private int _uriCount;
    private int[] _xmlSpaceStack;
    private int _idx;
    private boolean _preserve;
    private static final String XML_PREFIX = "xml";
    private static final String XMLSPACE_STRING = "xml:space";
    private static final String PRESERVE_STRING = "preserve";
    private static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
    private boolean _escaping;
    private boolean _disableEscaping;
    private int _textNodeToProcess;
    private static final String EMPTYSTRING = "";
    private static final DTMAxisIterator EMPTYITERATOR;
    private int _namesSize;
    private Map<Integer, Integer> _nsIndex;
    private int _size;
    private BitArray _dontEscape;
    private static int _documentURIIndex;
    private Document _document;
    private Map<Node, Integer> _node2Ids;
    private boolean _hasDOMSource;
    private XSLTCDTMManager _dtmManager;
    private Node[] _nodes;
    private NodeList[] _nodeLists;
    
    @Override
    public void setDocumentURI(final String uri) {
        if (uri != null) {
            this.setDocumentBaseURI(SystemIDResolver.getAbsoluteURI(uri));
        }
    }
    
    @Override
    public String getDocumentURI() {
        final String baseURI = this.getDocumentBaseURI();
        return (baseURI != null) ? baseURI : ("rtf" + SAXImpl._documentURIIndex++);
    }
    
    @Override
    public String getDocumentURI(final int node) {
        return this.getDocumentURI();
    }
    
    @Override
    public void setupMapping(final String[] names, final String[] urisArray, final int[] typesArray, final String[] namespaces) {
    }
    
    @Override
    public String lookupNamespace(final int node, final String prefix) throws TransletException {
        final AncestorIterator ancestors = new AncestorIterator();
        if (this.isElement(node)) {
            ancestors.includeSelf();
        }
        ancestors.setStartNode(node);
        int anode;
        while ((anode = ancestors.next()) != -1) {
            final NamespaceIterator namespaces = new NamespaceIterator();
            namespaces.setStartNode(anode);
            int nsnode;
            while ((nsnode = namespaces.next()) != -1) {
                if (this.getLocalName(nsnode).equals(prefix)) {
                    return this.getNodeValue(nsnode);
                }
            }
        }
        BasisLibrary.runTimeError("NAMESPACE_PREFIX_ERR", prefix);
        return null;
    }
    
    @Override
    public boolean isElement(final int node) {
        return this.getNodeType(node) == 1;
    }
    
    @Override
    public boolean isAttribute(final int node) {
        return this.getNodeType(node) == 2;
    }
    
    @Override
    public int getSize() {
        return this.getNumberOfNodes();
    }
    
    @Override
    public void setFilter(final StripFilter filter) {
    }
    
    @Override
    public boolean lessThan(final int node1, final int node2) {
        return node1 != -1 && (node2 == -1 || node1 < node2);
    }
    
    @Override
    public Node makeNode(final int index) {
        if (this._nodes == null) {
            this._nodes = new Node[this._namesSize];
        }
        final int nodeID = this.makeNodeIdentity(index);
        if (nodeID < 0) {
            return null;
        }
        if (nodeID < this._nodes.length) {
            return (this._nodes[nodeID] != null) ? this._nodes[nodeID] : (this._nodes[nodeID] = new DTMNodeProxy(this, index));
        }
        return new DTMNodeProxy(this, index);
    }
    
    @Override
    public Node makeNode(final DTMAxisIterator iter) {
        return this.makeNode(iter.next());
    }
    
    @Override
    public NodeList makeNodeList(final int index) {
        if (this._nodeLists == null) {
            this._nodeLists = new NodeList[this._namesSize];
        }
        final int nodeID = this.makeNodeIdentity(index);
        if (nodeID < 0) {
            return null;
        }
        if (nodeID < this._nodeLists.length) {
            return (this._nodeLists[nodeID] != null) ? this._nodeLists[nodeID] : (this._nodeLists[nodeID] = new DTMAxisIterNodeList(this, new SingletonIterator(index)));
        }
        return new DTMAxisIterNodeList(this, new SingletonIterator(index));
    }
    
    @Override
    public NodeList makeNodeList(final DTMAxisIterator iter) {
        return new DTMAxisIterNodeList(this, iter);
    }
    
    @Override
    public DTMAxisIterator getNodeValueIterator(final DTMAxisIterator iterator, final int type, final String value, final boolean op) {
        return new NodeValueIterator(iterator, type, value, op);
    }
    
    @Override
    public DTMAxisIterator orderNodes(final DTMAxisIterator source, final int node) {
        return new DupFilterIterator(source);
    }
    
    @Override
    public DTMAxisIterator getIterator() {
        return new SingletonIterator(this.getDocument(), true);
    }
    
    @Override
    public int getNSType(final int node) {
        final String s = this.getNamespaceURI(node);
        if (s == null) {
            return 0;
        }
        final int eType = this.getIdForNamespace(s);
        return this._nsIndex.get(new Integer(eType));
    }
    
    @Override
    public int getNamespaceType(final int node) {
        return super.getNamespaceType(node);
    }
    
    public int getGeneralizedType(final String name) {
        return this.getGeneralizedType(name, true);
    }
    
    public int getGeneralizedType(final String name, final boolean searchOnly) {
        String ns = null;
        int index = -1;
        if ((index = name.lastIndexOf(":")) > -1) {
            ns = name.substring(0, index);
        }
        int lNameStartIdx = index + 1;
        int code;
        if (name.charAt(lNameStartIdx) == '@') {
            code = 2;
            ++lNameStartIdx;
        }
        else {
            code = 1;
        }
        final String lName = (lNameStartIdx == 0) ? name : name.substring(lNameStartIdx);
        return this.m_expandedNameTable.getExpandedTypeID(ns, lName, code, searchOnly);
    }
    
    @Override
    public short[] getMapping(final String[] names, final String[] uris, final int[] types) {
        if (this._namesSize < 0) {
            return this.getMapping2(names, uris, types);
        }
        final int namesLength = names.length;
        final int exLength = this.m_expandedNameTable.getSize();
        final short[] result = new short[exLength];
        for (int i = 0; i < 14; ++i) {
            result[i] = (short)i;
        }
        for (int i = 14; i < exLength; ++i) {
            result[i] = this.m_expandedNameTable.getType(i);
        }
        for (int i = 0; i < namesLength; ++i) {
            final int genType = this.m_expandedNameTable.getExpandedTypeID(uris[i], names[i], types[i], true);
            if (genType >= 0 && genType < exLength) {
                result[genType] = (short)(i + 14);
            }
        }
        return result;
    }
    
    @Override
    public int[] getReverseMapping(final String[] names, final String[] uris, final int[] types) {
        final int[] result = new int[names.length + 14];
        for (int i = 0; i < 14; ++i) {
            result[i] = i;
        }
        for (int i = 0; i < names.length; ++i) {
            final int type = this.m_expandedNameTable.getExpandedTypeID(uris[i], names[i], types[i], true);
            result[i + 14] = type;
        }
        return result;
    }
    
    private short[] getMapping2(final String[] names, final String[] uris, final int[] types) {
        final int namesLength = names.length;
        final int exLength = this.m_expandedNameTable.getSize();
        int[] generalizedTypes = null;
        if (namesLength > 0) {
            generalizedTypes = new int[namesLength];
        }
        int resultLength = exLength;
        for (int i = 0; i < namesLength; ++i) {
            generalizedTypes[i] = this.m_expandedNameTable.getExpandedTypeID(uris[i], names[i], types[i], false);
            if (this._namesSize < 0 && generalizedTypes[i] >= resultLength) {
                resultLength = generalizedTypes[i] + 1;
            }
        }
        final short[] result = new short[resultLength];
        for (int i = 0; i < 14; ++i) {
            result[i] = (short)i;
        }
        for (int i = 14; i < exLength; ++i) {
            result[i] = this.m_expandedNameTable.getType(i);
        }
        for (int i = 0; i < namesLength; ++i) {
            final int genType = generalizedTypes[i];
            if (genType >= 0 && genType < resultLength) {
                result[genType] = (short)(i + 14);
            }
        }
        return result;
    }
    
    @Override
    public short[] getNamespaceMapping(final String[] namespaces) {
        final int nsLength = namespaces.length;
        final int mappingLength = this._uriCount;
        final short[] result = new short[mappingLength];
        for (int i = 0; i < mappingLength; ++i) {
            result[i] = -1;
        }
        for (int i = 0; i < nsLength; ++i) {
            final int eType = this.getIdForNamespace(namespaces[i]);
            final Integer type = this._nsIndex.get(eType);
            if (type != null) {
                result[type] = (short)i;
            }
        }
        return result;
    }
    
    @Override
    public short[] getReverseNamespaceMapping(final String[] namespaces) {
        final int length = namespaces.length;
        final short[] result = new short[length];
        for (int i = 0; i < length; ++i) {
            final int eType = this.getIdForNamespace(namespaces[i]);
            final Integer type = this._nsIndex.get(eType);
            result[i] = (short)((type == null) ? -1 : type.shortValue());
        }
        return result;
    }
    
    public SAXImpl(final XSLTCDTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing, final boolean buildIdIndex) {
        this(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, 512, buildIdIndex, false);
    }
    
    public SAXImpl(final XSLTCDTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing, final int blocksize, final boolean buildIdIndex, final boolean newNameTable) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, blocksize, false, buildIdIndex, newNameTable);
        this._uriCount = 0;
        this._idx = 1;
        this._preserve = false;
        this._escaping = true;
        this._disableEscaping = false;
        this._textNodeToProcess = -1;
        this._namesSize = -1;
        this._nsIndex = new HashMap<Integer, Integer>();
        this._size = 0;
        this._dontEscape = null;
        this._node2Ids = null;
        this._hasDOMSource = false;
        this._dtmManager = mgr;
        this._size = blocksize;
        (this._xmlSpaceStack = new int[(blocksize <= 64) ? 4 : 64])[0] = 0;
        if (source instanceof DOMSource) {
            this._hasDOMSource = true;
            final DOMSource domsrc = (DOMSource)source;
            final Node node = domsrc.getNode();
            if (node instanceof Document) {
                this._document = (Document)node;
            }
            else {
                this._document = node.getOwnerDocument();
            }
            this._node2Ids = new HashMap<Node, Integer>();
        }
    }
    
    @Override
    public void migrateTo(final DTMManager manager) {
        super.migrateTo(manager);
        if (manager instanceof XSLTCDTMManager) {
            this._dtmManager = (XSLTCDTMManager)manager;
        }
    }
    
    @Override
    public int getElementById(final String idString) {
        final Node node = this._document.getElementById(idString);
        if (node != null) {
            final Integer id = this._node2Ids.get(node);
            return (id != null) ? id : -1;
        }
        return -1;
    }
    
    @Override
    public boolean hasDOMSource() {
        return this._hasDOMSource;
    }
    
    private void xmlSpaceDefine(final String val, final int node) {
        final boolean setting = val.equals("preserve");
        if (setting != this._preserve) {
            this._xmlSpaceStack[this._idx++] = node;
            this._preserve = setting;
        }
    }
    
    private void xmlSpaceRevert(final int node) {
        if (node == this._xmlSpaceStack[this._idx - 1]) {
            --this._idx;
            this._preserve = !this._preserve;
        }
    }
    
    @Override
    protected boolean getShouldStripWhitespace() {
        return !this._preserve && super.getShouldStripWhitespace();
    }
    
    private void handleTextEscaping() {
        if (this._disableEscaping && this._textNodeToProcess != -1 && this._type(this._textNodeToProcess) == 3) {
            if (this._dontEscape == null) {
                this._dontEscape = new BitArray(this._size);
            }
            if (this._textNodeToProcess >= this._dontEscape.size()) {
                this._dontEscape.resize(this._dontEscape.size() * 2);
            }
            this._dontEscape.setBit(this._textNodeToProcess);
            this._disableEscaping = false;
        }
        this._textNodeToProcess = -1;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        this._disableEscaping = !this._escaping;
        this._textNodeToProcess = this.getNumberOfNodes();
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this._nsIndex.put(0, this._uriCount++);
        this.definePrefixAndUri("xml", "http://www.w3.org/XML/1998/namespace");
    }
    
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        this.handleTextEscaping();
        this._namesSize = this.m_expandedNameTable.getSize();
    }
    
    public void startElement(final String uri, final String localName, final String qname, final Attributes attributes, final Node node) throws SAXException {
        this.startElement(uri, localName, qname, attributes);
        if (this.m_buildIdIndex) {
            this._node2Ids.put(node, new Integer(this.m_parents.peek()));
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qname, final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qname, attributes);
        this.handleTextEscaping();
        if (this.m_wsfilter != null) {
            final int index = attributes.getIndex("xml:space");
            if (index >= 0) {
                this.xmlSpaceDefine(attributes.getValue(index), this.m_parents.peek());
            }
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qname) throws SAXException {
        super.endElement(namespaceURI, localName, qname);
        this.handleTextEscaping();
        if (this.m_wsfilter != null) {
            this.xmlSpaceRevert(this.m_previous);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        super.processingInstruction(target, data);
        this.handleTextEscaping();
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        this._textNodeToProcess = this.getNumberOfNodes();
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
        this.handleTextEscaping();
        this.definePrefixAndUri(prefix, uri);
    }
    
    private void definePrefixAndUri(final String prefix, final String uri) throws SAXException {
        final Integer eType = new Integer(this.getIdForNamespace(uri));
        if (this._nsIndex.get(eType) == null) {
            this._nsIndex.put(eType, this._uriCount++);
        }
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        super.comment(ch, start, length);
        this.handleTextEscaping();
    }
    
    public boolean setEscaping(final boolean value) {
        final boolean temp = this._escaping;
        this._escaping = value;
        return temp;
    }
    
    public void print(final int node, final int level) {
        switch (this.getNodeType(node)) {
            case 0:
            case 9: {
                this.print(this.getFirstChild(node), level);
                break;
            }
            case 3:
            case 7:
            case 8: {
                System.out.print(this.getStringValueX(node));
                break;
            }
            default: {
                final String name = this.getNodeName(node);
                System.out.print("<" + name);
                for (int a = this.getFirstAttribute(node); a != -1; a = this.getNextAttribute(a)) {
                    System.out.print("\n" + this.getNodeName(a) + "=\"" + this.getStringValueX(a) + "\"");
                }
                System.out.print('>');
                for (int child = this.getFirstChild(node); child != -1; child = this.getNextSibling(child)) {
                    this.print(child, level + 1);
                }
                System.out.println("</" + name + '>');
                break;
            }
        }
    }
    
    @Override
    public String getNodeName(final int node) {
        final int nodeh = node;
        final short type = this.getNodeType(nodeh);
        switch (type) {
            case 0:
            case 3:
            case 8:
            case 9: {
                return "";
            }
            case 13: {
                return this.getLocalName(nodeh);
            }
            default: {
                return super.getNodeName(nodeh);
            }
        }
    }
    
    @Override
    public String getNamespaceName(final int node) {
        if (node == -1) {
            return "";
        }
        final String s;
        return ((s = this.getNamespaceURI(node)) == null) ? "" : s;
    }
    
    @Override
    public int getAttributeNode(final int type, final int element) {
        for (int attr = this.getFirstAttribute(element); attr != -1; attr = this.getNextAttribute(attr)) {
            if (this.getExpandedTypeID(attr) == type) {
                return attr;
            }
        }
        return -1;
    }
    
    public String getAttributeValue(final int type, final int element) {
        final int attr = this.getAttributeNode(type, element);
        return (attr != -1) ? this.getStringValueX(attr) : "";
    }
    
    public String getAttributeValue(final String name, final int element) {
        return this.getAttributeValue(this.getGeneralizedType(name), element);
    }
    
    @Override
    public DTMAxisIterator getChildren(final int node) {
        return new ChildrenIterator().setStartNode(node);
    }
    
    @Override
    public DTMAxisIterator getTypedChildren(final int type) {
        return new TypedChildrenIterator(type);
    }
    
    @Override
    public DTMAxisIterator getAxisIterator(final int axis) {
        switch (axis) {
            case 13: {
                return new SingletonIterator();
            }
            case 3: {
                return new ChildrenIterator();
            }
            case 10: {
                return new ParentIterator();
            }
            case 0: {
                return new AncestorIterator();
            }
            case 1: {
                return new AncestorIterator().includeSelf();
            }
            case 2: {
                return new AttributeIterator();
            }
            case 4: {
                return new DescendantIterator();
            }
            case 5: {
                return new DescendantIterator().includeSelf();
            }
            case 6: {
                return new FollowingIterator();
            }
            case 11: {
                return new PrecedingIterator();
            }
            case 7: {
                return new FollowingSiblingIterator();
            }
            case 12: {
                return new PrecedingSiblingIterator();
            }
            case 9: {
                return new NamespaceIterator();
            }
            case 19: {
                return new RootIterator();
            }
            default: {
                BasisLibrary.runTimeError("AXIS_SUPPORT_ERR", Axis.getNames(axis));
                return null;
            }
        }
    }
    
    @Override
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type) {
        if (axis == 3) {
            return new TypedChildrenIterator(type);
        }
        if (type == -1) {
            return SAXImpl.EMPTYITERATOR;
        }
        switch (axis) {
            case 13: {
                return new TypedSingletonIterator(type);
            }
            case 3: {
                return new TypedChildrenIterator(type);
            }
            case 10: {
                return new ParentIterator().setNodeType(type);
            }
            case 0: {
                return new TypedAncestorIterator(type);
            }
            case 1: {
                return new TypedAncestorIterator(type).includeSelf();
            }
            case 2: {
                return new TypedAttributeIterator(type);
            }
            case 4: {
                return new TypedDescendantIterator(type);
            }
            case 5: {
                return new TypedDescendantIterator(type).includeSelf();
            }
            case 6: {
                return new TypedFollowingIterator(type);
            }
            case 11: {
                return new TypedPrecedingIterator(type);
            }
            case 7: {
                return new TypedFollowingSiblingIterator(type);
            }
            case 12: {
                return new TypedPrecedingSiblingIterator(type);
            }
            case 9: {
                return new TypedNamespaceIterator(type);
            }
            case 19: {
                return new TypedRootIterator(type);
            }
            default: {
                BasisLibrary.runTimeError("TYPED_AXIS_SUPPORT_ERR", Axis.getNames(axis));
                return null;
            }
        }
    }
    
    @Override
    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns) {
        if (ns == -1) {
            return SAXImpl.EMPTYITERATOR;
        }
        switch (axis) {
            case 3: {
                return new NamespaceChildrenIterator(ns);
            }
            case 2: {
                return new NamespaceAttributeIterator(ns);
            }
            default: {
                return new NamespaceWildcardIterator(axis, ns);
            }
        }
    }
    
    public DTMAxisIterator getTypedDescendantIterator(final int type) {
        return new TypedDescendantIterator(type);
    }
    
    @Override
    public DTMAxisIterator getNthDescendant(final int type, final int n, final boolean includeself) {
        return new NthDescendantIterator(n);
    }
    
    @Override
    public void characters(final int node, final SerializationHandler handler) throws TransletException {
        if (node != -1) {
            try {
                this.dispatchCharactersEvents(node, handler, false);
            }
            catch (final SAXException e) {
                throw new TransletException(e);
            }
        }
    }
    
    @Override
    public void copy(final DTMAxisIterator nodes, final SerializationHandler handler) throws TransletException {
        int node;
        while ((node = nodes.next()) != -1) {
            this.copy(node, handler);
        }
    }
    
    public void copy(final SerializationHandler handler) throws TransletException {
        this.copy(this.getDocument(), handler);
    }
    
    @Override
    public void copy(final int node, final SerializationHandler handler) throws TransletException {
        this.copy(node, handler, false);
    }
    
    private final void copy(final int node, final SerializationHandler handler, final boolean isChild) throws TransletException {
        final int nodeID = this.makeNodeIdentity(node);
        final int eType = this._exptype2(nodeID);
        final int type = this._exptype2Type(eType);
        try {
            switch (type) {
                case 0:
                case 9: {
                    for (int c = this._firstch2(nodeID); c != -1; c = this._nextsib2(c)) {
                        this.copy(this.makeNodeHandle(c), handler, true);
                    }
                    break;
                }
                case 7: {
                    this.copyPI(node, handler);
                    break;
                }
                case 8: {
                    handler.comment(this.getStringValueX(node));
                    break;
                }
                case 3: {
                    boolean oldEscapeSetting = false;
                    boolean escapeBit = false;
                    if (this._dontEscape != null) {
                        escapeBit = this._dontEscape.getBit(this.getNodeIdent(node));
                        if (escapeBit) {
                            oldEscapeSetting = handler.setEscaping(false);
                        }
                    }
                    this.copyTextNode(nodeID, handler);
                    if (escapeBit) {
                        handler.setEscaping(oldEscapeSetting);
                        break;
                    }
                    break;
                }
                case 2: {
                    this.copyAttribute(nodeID, eType, handler);
                    break;
                }
                case 13: {
                    handler.namespaceAfterStartElement(this.getNodeNameX(node), this.getNodeValue(node));
                    break;
                }
                default: {
                    if (type == 1) {
                        final String name = this.copyElement(nodeID, eType, handler);
                        this.copyNS(nodeID, handler, !isChild);
                        this.copyAttributes(nodeID, handler);
                        for (int c2 = this._firstch2(nodeID); c2 != -1; c2 = this._nextsib2(c2)) {
                            this.copy(this.makeNodeHandle(c2), handler, true);
                        }
                        handler.endElement(name);
                        break;
                    }
                    final String uri = this.getNamespaceName(node);
                    if (uri.length() != 0) {
                        final String prefix = this.getPrefix(node);
                        handler.namespaceAfterStartElement(prefix, uri);
                    }
                    handler.addAttribute(this.getNodeName(node), this.getNodeValue(node));
                    break;
                }
            }
        }
        catch (final Exception e) {
            throw new TransletException(e);
        }
    }
    
    private void copyPI(final int node, final SerializationHandler handler) throws TransletException {
        final String target = this.getNodeName(node);
        final String value = this.getStringValueX(node);
        try {
            handler.processingInstruction(target, value);
        }
        catch (final Exception e) {
            throw new TransletException(e);
        }
    }
    
    @Override
    public String shallowCopy(final int node, final SerializationHandler handler) throws TransletException {
        final int nodeID = this.makeNodeIdentity(node);
        final int exptype = this._exptype2(nodeID);
        final int type = this._exptype2Type(exptype);
        try {
            switch (type) {
                case 1: {
                    final String name = this.copyElement(nodeID, exptype, handler);
                    this.copyNS(nodeID, handler, true);
                    return name;
                }
                case 0:
                case 9: {
                    return "";
                }
                case 3: {
                    this.copyTextNode(nodeID, handler);
                    return null;
                }
                case 7: {
                    this.copyPI(node, handler);
                    return null;
                }
                case 8: {
                    handler.comment(this.getStringValueX(node));
                    return null;
                }
                case 13: {
                    handler.namespaceAfterStartElement(this.getNodeNameX(node), this.getNodeValue(node));
                    return null;
                }
                case 2: {
                    this.copyAttribute(nodeID, exptype, handler);
                    return null;
                }
                default: {
                    final String uri1 = this.getNamespaceName(node);
                    if (uri1.length() != 0) {
                        final String prefix = this.getPrefix(node);
                        handler.namespaceAfterStartElement(prefix, uri1);
                    }
                    handler.addAttribute(this.getNodeName(node), this.getNodeValue(node));
                    return null;
                }
            }
        }
        catch (final Exception e) {
            throw new TransletException(e);
        }
    }
    
    @Override
    public String getLanguage(final int node) {
        for (int parent = node; -1 != parent; parent = this.getParent(parent)) {
            if (1 == this.getNodeType(parent)) {
                final int langAttr = this.getAttributeNode(parent, "http://www.w3.org/XML/1998/namespace", "lang");
                if (-1 != langAttr) {
                    return this.getNodeValue(langAttr);
                }
            }
        }
        return null;
    }
    
    public DOMBuilder getBuilder() {
        return this;
    }
    
    @Override
    public SerializationHandler getOutputDomBuilder() {
        return new ToXMLSAXHandler(this, "UTF-8");
    }
    
    @Override
    public DOM getResultTreeFrag(final int initSize, final int rtfType) {
        return this.getResultTreeFrag(initSize, rtfType, true);
    }
    
    @Override
    public DOM getResultTreeFrag(final int initSize, final int rtfType, final boolean addToManager) {
        if (rtfType == 0) {
            if (addToManager) {
                final int dtmPos = this._dtmManager.getFirstFreeDTMID();
                final SimpleResultTreeImpl rtf = new SimpleResultTreeImpl(this._dtmManager, dtmPos << 16);
                this._dtmManager.addDTM(rtf, dtmPos, 0);
                return rtf;
            }
            return new SimpleResultTreeImpl(this._dtmManager, 0);
        }
        else {
            if (rtfType != 1) {
                return (DOM)this._dtmManager.getDTM(null, true, this.m_wsfilter, true, false, false, initSize, this.m_buildIdIndex);
            }
            if (addToManager) {
                final int dtmPos = this._dtmManager.getFirstFreeDTMID();
                final AdaptiveResultTreeImpl rtf2 = new AdaptiveResultTreeImpl(this._dtmManager, dtmPos << 16, this.m_wsfilter, initSize, this.m_buildIdIndex);
                this._dtmManager.addDTM(rtf2, dtmPos, 0);
                return rtf2;
            }
            return new AdaptiveResultTreeImpl(this._dtmManager, 0, this.m_wsfilter, initSize, this.m_buildIdIndex);
        }
    }
    
    @Override
    public Map<String, Integer> getElementsWithIDs() {
        return this.m_idAttributes;
    }
    
    @Override
    public String getUnparsedEntityURI(final String name) {
        if (this._document != null) {
            String uri = "";
            final DocumentType doctype = this._document.getDoctype();
            if (doctype != null) {
                final NamedNodeMap entities = doctype.getEntities();
                if (entities == null) {
                    return uri;
                }
                final Entity entity = (Entity)entities.getNamedItem(name);
                if (entity == null) {
                    return uri;
                }
                final String notationName = entity.getNotationName();
                if (notationName != null) {
                    uri = entity.getSystemId();
                    if (uri == null) {
                        uri = entity.getPublicId();
                    }
                }
            }
            return uri;
        }
        return super.getUnparsedEntityURI(name);
    }
    
    @Override
    public void release() {
        this._dtmManager.release(this, true);
    }
    
    static {
        EMPTYITERATOR = EmptyIterator.getInstance();
        SAXImpl._documentURIIndex = 0;
    }
    
    public class TypedNamespaceIterator extends NamespaceIterator
    {
        private String _nsPrefix;
        
        public TypedNamespaceIterator(final int nodeType) {
            if (SAXImpl.this.m_expandedNameTable != null) {
                this._nsPrefix = SAXImpl.this.m_expandedNameTable.getLocalName(nodeType);
            }
        }
        
        @Override
        public int next() {
            if (this._nsPrefix == null || this._nsPrefix.length() == 0) {
                return -1;
            }
            int node;
            for (node = -1, node = super.next(); node != -1; node = super.next()) {
                if (this._nsPrefix.compareTo(SAXImpl.this.getLocalName(node)) == 0) {
                    return this.returnNode(node);
                }
            }
            return -1;
        }
    }
    
    private final class NodeValueIterator extends InternalAxisIteratorBase
    {
        private DTMAxisIterator _source;
        private String _value;
        private boolean _op;
        private final boolean _isReverse;
        private int _returnType;
        
        public NodeValueIterator(final DTMAxisIterator source, final int returnType, final String value, final boolean op) {
            this._returnType = 1;
            this._source = source;
            this._returnType = returnType;
            this._value = value;
            this._op = op;
            this._isReverse = source.isReverse();
        }
        
        @Override
        public boolean isReverse() {
            return this._isReverse;
        }
        
        @Override
        public DTMAxisIterator cloneIterator() {
            try {
                final NodeValueIterator clone = (NodeValueIterator)super.clone();
                clone._isRestartable = false;
                clone._source = this._source.cloneIterator();
                clone._value = this._value;
                clone._op = this._op;
                return clone.reset();
            }
            catch (final CloneNotSupportedException e) {
                BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
                return null;
            }
        }
        
        @Override
        public void setRestartable(final boolean isRestartable) {
            this._isRestartable = isRestartable;
            this._source.setRestartable(isRestartable);
        }
        
        @Override
        public DTMAxisIterator reset() {
            this._source.reset();
            return this.resetPosition();
        }
        
        @Override
        public int next() {
            int node;
            while ((node = this._source.next()) != -1) {
                final String val = SAXImpl.this.getStringValueX(node);
                if (this._value.equals(val) == this._op) {
                    if (this._returnType == 0) {
                        return this.returnNode(node);
                    }
                    return this.returnNode(SAXImpl.this.getParent(node));
                }
            }
            return -1;
        }
        
        @Override
        public DTMAxisIterator setStartNode(final int node) {
            if (this._isRestartable) {
                this._source.setStartNode(this._startNode = node);
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public void setMark() {
            this._source.setMark();
        }
        
        @Override
        public void gotoMark() {
            this._source.gotoMark();
        }
    }
    
    public final class NamespaceWildcardIterator extends InternalAxisIteratorBase
    {
        protected int m_nsType;
        protected DTMAxisIterator m_baseIterator;
        
        public NamespaceWildcardIterator(final int axis, final int nsType) {
            this.m_nsType = nsType;
            switch (axis) {
                case 2: {
                    this.m_baseIterator = SAXImpl.this.getAxisIterator(axis);
                }
                case 9: {
                    this.m_baseIterator = SAXImpl.this.getAxisIterator(axis);
                    break;
                }
            }
            this.m_baseIterator = SAXImpl.this.getTypedAxisIterator(axis, 1);
        }
        
        @Override
        public DTMAxisIterator setStartNode(final int node) {
            if (this._isRestartable) {
                this._startNode = node;
                this.m_baseIterator.setStartNode(node);
                this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            int node;
            while ((node = this.m_baseIterator.next()) != -1) {
                if (SAXImpl.this.getNSType(node) == this.m_nsType) {
                    return this.returnNode(node);
                }
            }
            return -1;
        }
        
        @Override
        public DTMAxisIterator cloneIterator() {
            try {
                final DTMAxisIterator nestedClone = this.m_baseIterator.cloneIterator();
                final NamespaceWildcardIterator clone = (NamespaceWildcardIterator)super.clone();
                clone.m_baseIterator = nestedClone;
                clone.m_nsType = this.m_nsType;
                clone._isRestartable = false;
                return clone;
            }
            catch (final CloneNotSupportedException e) {
                BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
                return null;
            }
        }
        
        @Override
        public boolean isReverse() {
            return this.m_baseIterator.isReverse();
        }
        
        @Override
        public void setMark() {
            this.m_baseIterator.setMark();
        }
        
        @Override
        public void gotoMark() {
            this.m_baseIterator.gotoMark();
        }
    }
    
    public final class NamespaceChildrenIterator extends InternalAxisIteratorBase
    {
        private final int _nsType;
        
        public NamespaceChildrenIterator(final int type) {
            this._nsType = type;
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAXImpl.this.getDocument();
            }
            if (this._isRestartable) {
                this._startNode = node;
                this._currentNode = ((node == -1) ? -1 : -2);
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            if (this._currentNode != -1) {
                for (int node = (-2 == this._currentNode) ? DTMDefaultBase.this._firstch(SAXImpl.this.makeNodeIdentity(this._startNode)) : DTMDefaultBase.this._nextsib(this._currentNode); node != -1; node = DTMDefaultBase.this._nextsib(node)) {
                    final int nodeHandle = SAXImpl.this.makeNodeHandle(node);
                    if (SAXImpl.this.getNSType(nodeHandle) == this._nsType) {
                        this._currentNode = node;
                        return this.returnNode(nodeHandle);
                    }
                }
            }
            return -1;
        }
    }
    
    public final class NamespaceAttributeIterator extends InternalAxisIteratorBase
    {
        private final int _nsType;
        
        public NamespaceAttributeIterator(final int nsType) {
            this._nsType = nsType;
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAXImpl.this.getDocument();
            }
            if (this._isRestartable) {
                final int nsType = this._nsType;
                this._startNode = node;
                for (node = SAXImpl.this.getFirstAttribute(node); node != -1 && SAXImpl.this.getNSType(node) != nsType; node = SAXImpl.this.getNextAttribute(node)) {}
                this._currentNode = node;
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            final int node = this._currentNode;
            final int nsType = this._nsType;
            if (node == -1) {
                return -1;
            }
            int nextNode;
            for (nextNode = SAXImpl.this.getNextAttribute(node); nextNode != -1 && SAXImpl.this.getNSType(nextNode) != nsType; nextNode = SAXImpl.this.getNextAttribute(nextNode)) {}
            this._currentNode = nextNode;
            return this.returnNode(node);
        }
    }
}
