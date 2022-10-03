package com.sun.org.apache.xml.internal.dtm.ref.dom2dtm;

import javax.xml.transform.SourceLocator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.LexicalHandler;
import com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource;
import org.w3c.dom.Entity;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import com.sun.org.apache.xml.internal.utils.XMLString;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.ContentHandler;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.dom.DOMSource;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.TreeWalker;
import java.util.Vector;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;

public class DOM2DTM extends DTMDefaultBaseIterators
{
    static final boolean JJK_DEBUG = false;
    static final boolean JJK_NEWCODE = true;
    static final String NAMESPACE_DECL_NS = "http://www.w3.org/XML/1998/namespace";
    private transient Node m_pos;
    private int m_last_parent;
    private int m_last_kid;
    private transient Node m_root;
    boolean m_processedFirstElement;
    private transient boolean m_nodesAreProcessed;
    protected Vector m_nodes;
    TreeWalker m_walker;
    
    public DOM2DTM(final DTMManager mgr, final DOMSource domSource, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing) {
        super(mgr, domSource, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing);
        this.m_last_parent = 0;
        this.m_last_kid = -1;
        this.m_processedFirstElement = false;
        this.m_nodes = new Vector();
        this.m_walker = new TreeWalker(null);
        final Node node = domSource.getNode();
        this.m_root = node;
        this.m_pos = node;
        final int n = -1;
        this.m_last_kid = n;
        this.m_last_parent = n;
        this.m_last_kid = this.addNode(this.m_root, this.m_last_parent, this.m_last_kid, -1);
        if (1 == this.m_root.getNodeType()) {
            final NamedNodeMap attrs = this.m_root.getAttributes();
            final int attrsize = (attrs == null) ? 0 : attrs.getLength();
            if (attrsize > 0) {
                int attrIndex = -1;
                for (int i = 0; i < attrsize; ++i) {
                    attrIndex = this.addNode(attrs.item(i), 0, attrIndex, -1);
                    this.m_firstch.setElementAt(-1, attrIndex);
                }
                this.m_nextsib.setElementAt(-1, attrIndex);
            }
        }
        this.m_nodesAreProcessed = false;
    }
    
    protected int addNode(final Node node, final int parentIndex, final int previousSibling, final int forceNodeType) {
        final int nodeIndex = this.m_nodes.size();
        if (this.m_dtmIdent.size() == nodeIndex >>> 16) {
            try {
                if (this.m_mgr == null) {
                    throw new ClassCastException();
                }
                final DTMManagerDefault mgrD = (DTMManagerDefault)this.m_mgr;
                final int id = mgrD.getFirstFreeDTMID();
                mgrD.addDTM(this, id, nodeIndex);
                this.m_dtmIdent.addElement(id << 16);
            }
            catch (final ClassCastException e) {
                this.error(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
            }
        }
        ++this.m_size;
        int type;
        if (-1 == forceNodeType) {
            type = node.getNodeType();
        }
        else {
            type = forceNodeType;
        }
        if (2 == type) {
            final String name = node.getNodeName();
            if (name.startsWith("xmlns:") || name.equals("xmlns")) {
                type = 13;
            }
        }
        this.m_nodes.addElement(node);
        this.m_firstch.setElementAt(-2, nodeIndex);
        this.m_nextsib.setElementAt(-2, nodeIndex);
        this.m_prevsib.setElementAt(previousSibling, nodeIndex);
        this.m_parent.setElementAt(parentIndex, nodeIndex);
        if (-1 != parentIndex && type != 2 && type != 13 && -2 == this.m_firstch.elementAt(parentIndex)) {
            this.m_firstch.setElementAt(nodeIndex, parentIndex);
        }
        final String nsURI = node.getNamespaceURI();
        String localName = (type == 7) ? node.getNodeName() : node.getLocalName();
        if ((type == 1 || type == 2) && null == localName) {
            localName = node.getNodeName();
        }
        final ExpandedNameTable exnt = this.m_expandedNameTable;
        if (node.getLocalName() != null || type == 1 || type == 2) {}
        final int expandedNameID = (null != localName) ? exnt.getExpandedTypeID(nsURI, localName, type) : exnt.getExpandedTypeID(type);
        this.m_exptype.setElementAt(expandedNameID, nodeIndex);
        this.indexNode(expandedNameID, nodeIndex);
        if (-1 != previousSibling) {
            this.m_nextsib.setElementAt(nodeIndex, previousSibling);
        }
        if (type == 13) {
            this.declareNamespaceInContext(parentIndex, nodeIndex);
        }
        return nodeIndex;
    }
    
    public int getNumberOfNodes() {
        return this.m_nodes.size();
    }
    
    @Override
    protected boolean nextNode() {
        if (this.m_nodesAreProcessed) {
            return false;
        }
        Node pos = this.m_pos;
        Node next = null;
        int nexttype = -1;
        do {
            if (pos.hasChildNodes()) {
                next = pos.getFirstChild();
                if (next != null && 10 == next.getNodeType()) {
                    next = next.getNextSibling();
                }
                if (5 != pos.getNodeType()) {
                    this.m_last_parent = this.m_last_kid;
                    this.m_last_kid = -1;
                    if (null != this.m_wsfilter) {
                        final short wsv = this.m_wsfilter.getShouldStripSpace(this.makeNodeHandle(this.m_last_parent), this);
                        final boolean shouldStrip = (3 == wsv) ? this.getShouldStripWhitespace() : (2 == wsv);
                        this.pushShouldStripWhitespace(shouldStrip);
                    }
                }
            }
            else {
                if (this.m_last_kid != -1 && this.m_firstch.elementAt(this.m_last_kid) == -2) {
                    this.m_firstch.setElementAt(-1, this.m_last_kid);
                }
                while (this.m_last_parent != -1) {
                    next = pos.getNextSibling();
                    if (next != null && 10 == next.getNodeType()) {
                        next = next.getNextSibling();
                    }
                    if (next != null) {
                        break;
                    }
                    pos = pos.getParentNode();
                    if (pos == null) {}
                    if (pos != null && 5 == pos.getNodeType()) {
                        continue;
                    }
                    this.popShouldStripWhitespace();
                    if (this.m_last_kid == -1) {
                        this.m_firstch.setElementAt(-1, this.m_last_parent);
                    }
                    else {
                        this.m_nextsib.setElementAt(-1, this.m_last_kid);
                    }
                    final SuballocatedIntVector parent = this.m_parent;
                    final int last_parent = this.m_last_parent;
                    this.m_last_kid = last_parent;
                    this.m_last_parent = parent.elementAt(last_parent);
                }
                if (this.m_last_parent == -1) {
                    next = null;
                }
            }
            if (next != null) {
                nexttype = next.getNodeType();
            }
            if (5 == nexttype) {
                pos = next;
            }
        } while (5 == nexttype);
        if (next == null) {
            this.m_nextsib.setElementAt(-1, 0);
            this.m_nodesAreProcessed = true;
            this.m_pos = null;
            return false;
        }
        boolean suppressNode = false;
        Node lastTextNode = null;
        nexttype = next.getNodeType();
        if (3 == nexttype || 4 == nexttype) {
            suppressNode = (null != this.m_wsfilter && this.getShouldStripWhitespace());
            for (Node n = next; n != null; n = this.logicalNextDOMTextNode(n)) {
                lastTextNode = n;
                if (3 == n.getNodeType()) {
                    nexttype = 3;
                }
                suppressNode &= XMLCharacterRecognizer.isWhiteSpace(n.getNodeValue());
            }
        }
        else if (7 == nexttype) {
            suppressNode = pos.getNodeName().toLowerCase().equals("xml");
        }
        if (!suppressNode) {
            final int nextindex = this.addNode(next, this.m_last_parent, this.m_last_kid, nexttype);
            this.m_last_kid = nextindex;
            if (1 == nexttype) {
                int attrIndex = -1;
                final NamedNodeMap attrs = next.getAttributes();
                final int attrsize = (attrs == null) ? 0 : attrs.getLength();
                if (attrsize > 0) {
                    for (int i = 0; i < attrsize; ++i) {
                        attrIndex = this.addNode(attrs.item(i), nextindex, attrIndex, -1);
                        this.m_firstch.setElementAt(-1, attrIndex);
                        if (!this.m_processedFirstElement && "xmlns:xml".equals(attrs.item(i).getNodeName())) {
                            this.m_processedFirstElement = true;
                        }
                    }
                }
                if (!this.m_processedFirstElement) {
                    attrIndex = this.addNode(new DOM2DTMdefaultNamespaceDeclarationNode((Element)next, "xml", "http://www.w3.org/XML/1998/namespace", this.makeNodeHandle(((attrIndex == -1) ? nextindex : attrIndex) + 1)), nextindex, attrIndex, -1);
                    this.m_firstch.setElementAt(-1, attrIndex);
                    this.m_processedFirstElement = true;
                }
                if (attrIndex != -1) {
                    this.m_nextsib.setElementAt(-1, attrIndex);
                }
            }
        }
        if (3 == nexttype || 4 == nexttype) {
            next = lastTextNode;
        }
        this.m_pos = next;
        return true;
    }
    
    @Override
    public Node getNode(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        return this.m_nodes.elementAt(identity);
    }
    
    protected Node lookupNode(final int nodeIdentity) {
        return this.m_nodes.elementAt(nodeIdentity);
    }
    
    @Override
    protected int getNextNodeIdentity(int identity) {
        if (++identity >= this.m_nodes.size() && !this.nextNode()) {
            identity = -1;
        }
        return identity;
    }
    
    private int getHandleFromNode(final Node node) {
        if (null != node) {
            int len = this.m_nodes.size();
            int i = 0;
            while (true) {
                if (i < len) {
                    if (this.m_nodes.elementAt(i) == node) {
                        return this.makeNodeHandle(i);
                    }
                    ++i;
                }
                else {
                    final boolean isMore = this.nextNode();
                    len = this.m_nodes.size();
                    if (!isMore && i >= len) {
                        break;
                    }
                    continue;
                }
            }
        }
        return -1;
    }
    
    public int getHandleOfNode(final Node node) {
        if (null != node && (this.m_root == node || (this.m_root.getNodeType() == 9 && this.m_root == node.getOwnerDocument()) || (this.m_root.getNodeType() != 9 && this.m_root.getOwnerDocument() == node.getOwnerDocument()))) {
            for (Node cursor = node; cursor != null; cursor = ((cursor.getNodeType() != 2) ? cursor.getParentNode() : ((Attr)cursor).getOwnerElement())) {
                if (cursor == this.m_root) {
                    return this.getHandleFromNode(node);
                }
            }
        }
        return -1;
    }
    
    @Override
    public int getAttributeNode(final int nodeHandle, String namespaceURI, final String name) {
        if (null == namespaceURI) {
            namespaceURI = "";
        }
        int type = this.getNodeType(nodeHandle);
        if (1 == type) {
            int identity = this.makeNodeIdentity(nodeHandle);
            while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                type = this._type(identity);
                if (type != 2 && type != 13) {
                    break;
                }
                final Node node = this.lookupNode(identity);
                String nodeuri = node.getNamespaceURI();
                if (null == nodeuri) {
                    nodeuri = "";
                }
                final String nodelocalname = node.getLocalName();
                if (nodeuri.equals(namespaceURI) && name.equals(nodelocalname)) {
                    return this.makeNodeHandle(identity);
                }
            }
        }
        return -1;
    }
    
    @Override
    public XMLString getStringValue(final int nodeHandle) {
        final int type = this.getNodeType(nodeHandle);
        Node node = this.getNode(nodeHandle);
        if (1 == type || 9 == type || 11 == type) {
            final FastStringBuffer buf = StringBufferPool.get();
            String s;
            try {
                getNodeData(node, buf);
                s = ((buf.length() > 0) ? buf.toString() : "");
            }
            finally {
                StringBufferPool.free(buf);
            }
            return this.m_xstrf.newstr(s);
        }
        if (3 == type || 4 == type) {
            final FastStringBuffer buf = StringBufferPool.get();
            while (node != null) {
                buf.append(node.getNodeValue());
                node = this.logicalNextDOMTextNode(node);
            }
            final String s = (buf.length() > 0) ? buf.toString() : "";
            StringBufferPool.free(buf);
            return this.m_xstrf.newstr(s);
        }
        return this.m_xstrf.newstr(node.getNodeValue());
    }
    
    public boolean isWhitespace(final int nodeHandle) {
        final int type = this.getNodeType(nodeHandle);
        Node node = this.getNode(nodeHandle);
        if (3 == type || 4 == type) {
            final FastStringBuffer buf = StringBufferPool.get();
            while (node != null) {
                buf.append(node.getNodeValue());
                node = this.logicalNextDOMTextNode(node);
            }
            final boolean b = buf.isWhitespace(0, buf.length());
            StringBufferPool.free(buf);
            return b;
        }
        return false;
    }
    
    protected static void getNodeData(final Node node, final FastStringBuffer buf) {
        switch (node.getNodeType()) {
            case 1:
            case 9:
            case 11: {
                for (Node child = node.getFirstChild(); null != child; child = child.getNextSibling()) {
                    getNodeData(child, buf);
                }
                break;
            }
            case 2:
            case 3:
            case 4: {
                buf.append(node.getNodeValue());
            }
        }
    }
    
    @Override
    public String getNodeName(final int nodeHandle) {
        final Node node = this.getNode(nodeHandle);
        return node.getNodeName();
    }
    
    @Override
    public String getNodeNameX(final int nodeHandle) {
        final short type = this.getNodeType(nodeHandle);
        String name = null;
        switch (type) {
            case 13: {
                final Node node = this.getNode(nodeHandle);
                name = node.getNodeName();
                if (name.startsWith("xmlns:")) {
                    name = QName.getLocalPart(name);
                }
                else if (name.equals("xmlns")) {
                    name = "";
                }
                break;
            }
            case 1:
            case 2:
            case 5:
            case 7: {
                final Node node = this.getNode(nodeHandle);
                name = node.getNodeName();
                break;
            }
            default: {
                name = "";
                break;
            }
        }
        return name;
    }
    
    @Override
    public String getLocalName(final int nodeHandle) {
        final int id = this.makeNodeIdentity(nodeHandle);
        if (-1 == id) {
            return null;
        }
        final Node newnode = this.m_nodes.elementAt(id);
        String newname = newnode.getLocalName();
        if (null == newname) {
            final String qname = newnode.getNodeName();
            if ('#' == qname.charAt(0)) {
                newname = "";
            }
            else {
                final int index = qname.indexOf(58);
                newname = ((index < 0) ? qname : qname.substring(index + 1));
            }
        }
        return newname;
    }
    
    @Override
    public String getPrefix(final int nodeHandle) {
        final short type = this.getNodeType(nodeHandle);
        String prefix = null;
        switch (type) {
            case 13: {
                final Node node = this.getNode(nodeHandle);
                final String qname = node.getNodeName();
                final int index = qname.indexOf(58);
                prefix = ((index < 0) ? "" : qname.substring(index + 1));
                break;
            }
            case 1:
            case 2: {
                final Node node = this.getNode(nodeHandle);
                final String qname = node.getNodeName();
                final int index = qname.indexOf(58);
                prefix = ((index < 0) ? "" : qname.substring(0, index));
                break;
            }
            default: {
                prefix = "";
                break;
            }
        }
        return prefix;
    }
    
    @Override
    public String getNamespaceURI(final int nodeHandle) {
        final int id = this.makeNodeIdentity(nodeHandle);
        if (id == -1) {
            return null;
        }
        final Node node = this.m_nodes.elementAt(id);
        return node.getNamespaceURI();
    }
    
    private Node logicalNextDOMTextNode(Node n) {
        Node p = n.getNextSibling();
        if (p == null) {
            for (n = n.getParentNode(); n != null && 5 == n.getNodeType(); n = n.getParentNode()) {
                p = n.getNextSibling();
                if (p != null) {
                    break;
                }
            }
        }
        n = p;
        while (n != null && 5 == n.getNodeType()) {
            if (n.hasChildNodes()) {
                n = n.getFirstChild();
            }
            else {
                n = n.getNextSibling();
            }
        }
        if (n != null) {
            final int ntype = n.getNodeType();
            if (3 != ntype && 4 != ntype) {
                n = null;
            }
        }
        return n;
    }
    
    @Override
    public String getNodeValue(final int nodeHandle) {
        int type = this._exptype(this.makeNodeIdentity(nodeHandle));
        type = ((-1 != type) ? this.getNodeType(nodeHandle) : -1);
        if (3 != type && 4 != type) {
            return this.getNode(nodeHandle).getNodeValue();
        }
        final Node node = this.getNode(nodeHandle);
        Node n = this.logicalNextDOMTextNode(node);
        if (n == null) {
            return node.getNodeValue();
        }
        final FastStringBuffer buf = StringBufferPool.get();
        buf.append(node.getNodeValue());
        while (n != null) {
            buf.append(n.getNodeValue());
            n = this.logicalNextDOMTextNode(n);
        }
        final String s = (buf.length() > 0) ? buf.toString() : "";
        StringBufferPool.free(buf);
        return s;
    }
    
    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        Document doc;
        if (this.m_root.getNodeType() == 9) {
            doc = (Document)this.m_root;
        }
        else {
            doc = this.m_root.getOwnerDocument();
        }
        if (null != doc) {
            final DocumentType dtd = doc.getDoctype();
            if (null != dtd) {
                return dtd.getSystemId();
            }
        }
        return null;
    }
    
    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        Document doc;
        if (this.m_root.getNodeType() == 9) {
            doc = (Document)this.m_root;
        }
        else {
            doc = this.m_root.getOwnerDocument();
        }
        if (null != doc) {
            final DocumentType dtd = doc.getDoctype();
            if (null != dtd) {
                return dtd.getPublicId();
            }
        }
        return null;
    }
    
    @Override
    public int getElementById(final String elementId) {
        final Document doc = (Document)((this.m_root.getNodeType() == 9) ? this.m_root : this.m_root.getOwnerDocument());
        if (null != doc) {
            final Node elem = doc.getElementById(elementId);
            if (null != elem) {
                int elemHandle = this.getHandleFromNode(elem);
                if (-1 == elemHandle) {
                    int identity = this.m_nodes.size() - 1;
                    while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                        final Node node = this.getNode(identity);
                        if (node == elem) {
                            elemHandle = this.getHandleFromNode(elem);
                            break;
                        }
                    }
                }
                return elemHandle;
            }
        }
        return -1;
    }
    
    @Override
    public String getUnparsedEntityURI(final String name) {
        String url = "";
        final Document doc = (Document)((this.m_root.getNodeType() == 9) ? this.m_root : this.m_root.getOwnerDocument());
        if (null != doc) {
            final DocumentType doctype = doc.getDoctype();
            if (null != doctype) {
                final NamedNodeMap entities = doctype.getEntities();
                if (null == entities) {
                    return url;
                }
                final Entity entity = (Entity)entities.getNamedItem(name);
                if (null == entity) {
                    return url;
                }
                final String notationName = entity.getNotationName();
                if (null != notationName) {
                    url = entity.getSystemId();
                    if (null == url) {
                        url = entity.getPublicId();
                    }
                }
            }
        }
        return url;
    }
    
    @Override
    public boolean isAttributeSpecified(final int attributeHandle) {
        final int type = this.getNodeType(attributeHandle);
        if (2 == type) {
            final Attr attr = (Attr)this.getNode(attributeHandle);
            return attr.getSpecified();
        }
        return false;
    }
    
    public void setIncrementalSAXSource(final IncrementalSAXSource source) {
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
    public boolean needsTwoThreads() {
        return false;
    }
    
    private static boolean isSpace(final char ch) {
        return XMLCharacterRecognizer.isWhiteSpace(ch);
    }
    
    @Override
    public void dispatchCharactersEvents(final int nodeHandle, final ContentHandler ch, final boolean normalize) throws SAXException {
        if (normalize) {
            XMLString str = this.getStringValue(nodeHandle);
            str = str.fixWhiteSpace(true, true, false);
            str.dispatchCharactersEvents(ch);
        }
        else {
            final int type = this.getNodeType(nodeHandle);
            Node node = this.getNode(nodeHandle);
            dispatchNodeData(node, ch, 0);
            if (3 == type || 4 == type) {
                while (null != (node = this.logicalNextDOMTextNode(node))) {
                    dispatchNodeData(node, ch, 0);
                }
            }
        }
    }
    
    protected static void dispatchNodeData(final Node node, final ContentHandler ch, final int depth) throws SAXException {
        switch (node.getNodeType()) {
            case 1:
            case 9:
            case 11: {
                for (Node child = node.getFirstChild(); null != child; child = child.getNextSibling()) {
                    dispatchNodeData(child, ch, depth + 1);
                }
                break;
            }
            case 7:
            case 8: {
                if (0 != depth) {
                    break;
                }
            }
            case 2:
            case 3:
            case 4: {
                final String str = node.getNodeValue();
                if (ch instanceof CharacterNodeHandler) {
                    ((CharacterNodeHandler)ch).characters(node);
                    break;
                }
                ch.characters(str.toCharArray(), 0, str.length());
                break;
            }
        }
    }
    
    @Override
    public void dispatchToEvents(final int nodeHandle, final ContentHandler ch) throws SAXException {
        TreeWalker treeWalker = this.m_walker;
        final ContentHandler prevCH = treeWalker.getContentHandler();
        if (null != prevCH) {
            treeWalker = new TreeWalker(null);
        }
        treeWalker.setContentHandler(ch);
        try {
            final Node node = this.getNode(nodeHandle);
            treeWalker.traverseFragment(node);
        }
        finally {
            treeWalker.setContentHandler(null);
        }
    }
    
    @Override
    public void setProperty(final String property, final Object value) {
    }
    
    @Override
    public SourceLocator getSourceLocatorFor(final int node) {
        return null;
    }
    
    public interface CharacterNodeHandler
    {
        void characters(final Node p0) throws SAXException;
    }
}
