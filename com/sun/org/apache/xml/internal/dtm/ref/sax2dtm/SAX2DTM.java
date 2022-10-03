package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.ref.NodeLocator;
import javax.xml.transform.SourceLocator;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import org.xml.sax.InputSource;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import org.xml.sax.SAXException;
import java.util.HashMap;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.IntVector;
import com.sun.org.apache.xml.internal.utils.StringVector;
import java.util.Map;
import com.sun.org.apache.xml.internal.dtm.ref.DTMStringPool;
import com.sun.org.apache.xml.internal.dtm.ref.DTMTreeWalker;
import org.xml.sax.Locator;
import java.util.Vector;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;

public class SAX2DTM extends DTMDefaultBaseIterators implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, DeclHandler, LexicalHandler
{
    private static final boolean DEBUG = false;
    private IncrementalSAXSource m_incrementalSAXSource;
    protected FastStringBuffer m_chars;
    protected SuballocatedIntVector m_data;
    protected transient IntStack m_parents;
    protected transient int m_previous;
    protected transient Vector m_prefixMappings;
    protected transient IntStack m_contextIndexes;
    protected transient int m_textType;
    protected transient int m_coalescedTextType;
    protected transient Locator m_locator;
    private transient String m_systemId;
    protected transient boolean m_insideDTD;
    protected DTMTreeWalker m_walker;
    protected DTMStringPool m_valuesOrPrefixes;
    protected boolean m_endDocumentOccured;
    protected SuballocatedIntVector m_dataOrQName;
    protected Map<String, Integer> m_idAttributes;
    private static final String[] m_fixednames;
    private Vector m_entities;
    private static final int ENTITY_FIELD_PUBLICID = 0;
    private static final int ENTITY_FIELD_SYSTEMID = 1;
    private static final int ENTITY_FIELD_NOTATIONNAME = 2;
    private static final int ENTITY_FIELD_NAME = 3;
    private static final int ENTITY_FIELDS_PER = 4;
    protected int m_textPendingStart;
    protected boolean m_useSourceLocationProperty;
    protected StringVector m_sourceSystemId;
    protected IntVector m_sourceLine;
    protected IntVector m_sourceColumn;
    boolean m_pastFirstElement;
    
    public SAX2DTM(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing) {
        this(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, 512, true, false);
    }
    
    public SAX2DTM(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing, final int blocksize, final boolean usePrevsib, final boolean newNameTable) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, blocksize, usePrevsib, newNameTable);
        this.m_incrementalSAXSource = null;
        this.m_previous = 0;
        this.m_prefixMappings = new Vector();
        this.m_textType = 3;
        this.m_coalescedTextType = 3;
        this.m_locator = null;
        this.m_systemId = null;
        this.m_insideDTD = false;
        this.m_walker = new DTMTreeWalker();
        this.m_endDocumentOccured = false;
        this.m_idAttributes = new HashMap<String, Integer>();
        this.m_entities = null;
        this.m_textPendingStart = -1;
        this.m_useSourceLocationProperty = false;
        this.m_pastFirstElement = false;
        if (blocksize <= 64) {
            this.m_data = new SuballocatedIntVector(blocksize, 4);
            this.m_dataOrQName = new SuballocatedIntVector(blocksize, 4);
            this.m_valuesOrPrefixes = new DTMStringPool(16);
            this.m_chars = new FastStringBuffer(7, 10);
            this.m_contextIndexes = new IntStack(4);
            this.m_parents = new IntStack(4);
        }
        else {
            this.m_data = new SuballocatedIntVector(blocksize, 32);
            this.m_dataOrQName = new SuballocatedIntVector(blocksize, 32);
            this.m_valuesOrPrefixes = new DTMStringPool();
            this.m_chars = new FastStringBuffer(10, 13);
            this.m_contextIndexes = new IntStack();
            this.m_parents = new IntStack();
        }
        this.m_data.addElement(0);
        this.m_useSourceLocationProperty = mgr.getSource_location();
        this.m_sourceSystemId = (this.m_useSourceLocationProperty ? new StringVector() : null);
        this.m_sourceLine = (this.m_useSourceLocationProperty ? new IntVector() : null);
        this.m_sourceColumn = (this.m_useSourceLocationProperty ? new IntVector() : null);
    }
    
    public void setUseSourceLocation(final boolean useSourceLocation) {
        this.m_useSourceLocationProperty = useSourceLocation;
    }
    
    protected int _dataOrQName(final int identity) {
        if (identity < this.m_size) {
            return this.m_dataOrQName.elementAt(identity);
        }
        while (true) {
            final boolean isMore = this.nextNode();
            if (!isMore) {
                return -1;
            }
            if (identity < this.m_size) {
                return this.m_dataOrQName.elementAt(identity);
            }
        }
    }
    
    public void clearCoRoutine() {
        this.clearCoRoutine(true);
    }
    
    public void clearCoRoutine(final boolean callDoTerminate) {
        if (null != this.m_incrementalSAXSource) {
            if (callDoTerminate) {
                this.m_incrementalSAXSource.deliverMoreNodes(false);
            }
            this.m_incrementalSAXSource = null;
        }
    }
    
    public void setIncrementalSAXSource(final IncrementalSAXSource incrementalSAXSource) {
        (this.m_incrementalSAXSource = incrementalSAXSource).setContentHandler(this);
        incrementalSAXSource.setLexicalHandler(this);
        incrementalSAXSource.setDTDHandler(this);
    }
    
    @Override
    public ContentHandler getContentHandler() {
        if (this.m_incrementalSAXSource.getClass().getName().equals("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Filter")) {
            return (ContentHandler)this.m_incrementalSAXSource;
        }
        return this;
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        if (this.m_incrementalSAXSource.getClass().getName().equals("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Filter")) {
            return (LexicalHandler)this.m_incrementalSAXSource;
        }
        return this;
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return this;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return this;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this;
    }
    
    @Override
    public DeclHandler getDeclHandler() {
        return this;
    }
    
    @Override
    public boolean needsTwoThreads() {
        return null != this.m_incrementalSAXSource;
    }
    
    @Override
    public void dispatchCharactersEvents(final int nodeHandle, final ContentHandler ch, final boolean normalize) throws SAXException {
        int identity = this.makeNodeIdentity(nodeHandle);
        if (identity == -1) {
            return;
        }
        int type = this._type(identity);
        if (this.isTextType(type)) {
            final int dataIndex = this.m_dataOrQName.elementAt(identity);
            final int offset = this.m_data.elementAt(dataIndex);
            final int length = this.m_data.elementAt(dataIndex + 1);
            if (normalize) {
                this.m_chars.sendNormalizedSAXcharacters(ch, offset, length);
            }
            else {
                this.m_chars.sendSAXcharacters(ch, offset, length);
            }
        }
        else {
            final int firstChild = this._firstch(identity);
            if (-1 != firstChild) {
                int offset = -1;
                int length = 0;
                final int startNode = identity;
                identity = firstChild;
                do {
                    type = this._type(identity);
                    if (this.isTextType(type)) {
                        final int dataIndex2 = this._dataOrQName(identity);
                        if (-1 == offset) {
                            offset = this.m_data.elementAt(dataIndex2);
                        }
                        length += this.m_data.elementAt(dataIndex2 + 1);
                    }
                    identity = this.getNextNodeIdentity(identity);
                } while (-1 != identity && this._parent(identity) >= startNode);
                if (length > 0) {
                    if (normalize) {
                        this.m_chars.sendNormalizedSAXcharacters(ch, offset, length);
                    }
                    else {
                        this.m_chars.sendSAXcharacters(ch, offset, length);
                    }
                }
            }
            else if (type != 1) {
                int dataIndex3 = this._dataOrQName(identity);
                if (dataIndex3 < 0) {
                    dataIndex3 = -dataIndex3;
                    dataIndex3 = this.m_data.elementAt(dataIndex3 + 1);
                }
                final String str = this.m_valuesOrPrefixes.indexToString(dataIndex3);
                if (normalize) {
                    FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 0, str.length(), ch);
                }
                else {
                    ch.characters(str.toCharArray(), 0, str.length());
                }
            }
        }
    }
    
    @Override
    public String getNodeName(final int nodeHandle) {
        final int expandedTypeID = this.getExpandedTypeID(nodeHandle);
        final int namespaceID = this.m_expandedNameTable.getNamespaceID(expandedTypeID);
        if (0 != namespaceID) {
            int qnameIndex = this.m_dataOrQName.elementAt(this.makeNodeIdentity(nodeHandle));
            if (qnameIndex < 0) {
                qnameIndex = -qnameIndex;
                qnameIndex = this.m_data.elementAt(qnameIndex);
            }
            return this.m_valuesOrPrefixes.indexToString(qnameIndex);
        }
        final int type = this.getNodeType(nodeHandle);
        if (type == 13) {
            if (null == this.m_expandedNameTable.getLocalName(expandedTypeID)) {
                return "xmlns";
            }
            return "xmlns:" + this.m_expandedNameTable.getLocalName(expandedTypeID);
        }
        else {
            if (0 == this.m_expandedNameTable.getLocalNameID(expandedTypeID)) {
                return SAX2DTM.m_fixednames[type];
            }
            return this.m_expandedNameTable.getLocalName(expandedTypeID);
        }
    }
    
    @Override
    public String getNodeNameX(final int nodeHandle) {
        final int expandedTypeID = this.getExpandedTypeID(nodeHandle);
        final int namespaceID = this.m_expandedNameTable.getNamespaceID(expandedTypeID);
        if (0 != namespaceID) {
            int qnameIndex = this.m_dataOrQName.elementAt(this.makeNodeIdentity(nodeHandle));
            if (qnameIndex < 0) {
                qnameIndex = -qnameIndex;
                qnameIndex = this.m_data.elementAt(qnameIndex);
            }
            return this.m_valuesOrPrefixes.indexToString(qnameIndex);
        }
        final String name = this.m_expandedNameTable.getLocalName(expandedTypeID);
        if (name == null) {
            return "";
        }
        return name;
    }
    
    @Override
    public boolean isAttributeSpecified(final int attributeHandle) {
        return true;
    }
    
    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }
    
    @Override
    protected int getNextNodeIdentity(int identity) {
        ++identity;
        while (identity >= this.m_size) {
            if (null == this.m_incrementalSAXSource) {
                return -1;
            }
            this.nextNode();
        }
        return identity;
    }
    
    @Override
    public void dispatchToEvents(final int nodeHandle, final ContentHandler ch) throws SAXException {
        DTMTreeWalker treeWalker = this.m_walker;
        final ContentHandler prevCH = treeWalker.getcontentHandler();
        if (null != prevCH) {
            treeWalker = new DTMTreeWalker();
        }
        treeWalker.setcontentHandler(ch);
        treeWalker.setDTM(this);
        try {
            treeWalker.traverse(nodeHandle);
        }
        finally {
            treeWalker.setcontentHandler(null);
        }
    }
    
    public int getNumberOfNodes() {
        return this.m_size;
    }
    
    @Override
    protected boolean nextNode() {
        if (null == this.m_incrementalSAXSource) {
            return false;
        }
        if (this.m_endDocumentOccured) {
            this.clearCoRoutine();
            return false;
        }
        final Object gotMore = this.m_incrementalSAXSource.deliverMoreNodes(true);
        if (gotMore instanceof Boolean) {
            if (gotMore != Boolean.TRUE) {
                this.clearCoRoutine();
            }
            return true;
        }
        if (gotMore instanceof RuntimeException) {
            throw (RuntimeException)gotMore;
        }
        if (gotMore instanceof Exception) {
            throw new WrappedRuntimeException((Exception)gotMore);
        }
        this.clearCoRoutine();
        return false;
    }
    
    private final boolean isTextType(final int type) {
        return 3 == type || 4 == type;
    }
    
    protected int addNode(final int type, final int expandedTypeID, final int parentIndex, final int previousSibling, final int dataOrPrefix, final boolean canHaveFirstChild) {
        final int nodeIndex = this.m_size++;
        if (this.m_dtmIdent.size() == nodeIndex >>> 16) {
            this.addNewDTMID(nodeIndex);
        }
        this.m_firstch.addElement(canHaveFirstChild ? -2 : -1);
        this.m_nextsib.addElement(-2);
        this.m_parent.addElement(parentIndex);
        this.m_exptype.addElement(expandedTypeID);
        this.m_dataOrQName.addElement(dataOrPrefix);
        if (this.m_prevsib != null) {
            this.m_prevsib.addElement(previousSibling);
        }
        if (-1 != previousSibling) {
            this.m_nextsib.setElementAt(nodeIndex, previousSibling);
        }
        if (this.m_locator != null && this.m_useSourceLocationProperty) {
            this.setSourceLocation();
        }
        switch (type) {
            case 13: {
                this.declareNamespaceInContext(parentIndex, nodeIndex);
                break;
            }
            case 2: {
                break;
            }
            default: {
                if (-1 == previousSibling && -1 != parentIndex) {
                    this.m_firstch.setElementAt(nodeIndex, parentIndex);
                    break;
                }
                break;
            }
        }
        return nodeIndex;
    }
    
    protected void addNewDTMID(final int nodeIndex) {
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
    
    @Override
    public void migrateTo(final DTMManager manager) {
        super.migrateTo(manager);
        final int numDTMs = this.m_dtmIdent.size();
        int dtmId = this.m_mgrDefault.getFirstFreeDTMID();
        int nodeIndex = 0;
        for (int i = 0; i < numDTMs; ++i) {
            this.m_dtmIdent.setElementAt(dtmId << 16, i);
            this.m_mgrDefault.addDTM(this, dtmId, nodeIndex);
            ++dtmId;
            nodeIndex += 65536;
        }
    }
    
    protected void setSourceLocation() {
        this.m_sourceSystemId.addElement(this.m_locator.getSystemId());
        this.m_sourceLine.addElement(this.m_locator.getLineNumber());
        this.m_sourceColumn.addElement(this.m_locator.getColumnNumber());
        if (this.m_sourceSystemId.size() != this.m_size) {
            final String msg = "CODING ERROR in Source Location: " + this.m_size + " != " + this.m_sourceSystemId.size();
            System.err.println(msg);
            throw new RuntimeException(msg);
        }
    }
    
    @Override
    public String getNodeValue(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        final int type = this._type(identity);
        if (this.isTextType(type)) {
            final int dataIndex = this._dataOrQName(identity);
            final int offset = this.m_data.elementAt(dataIndex);
            final int length = this.m_data.elementAt(dataIndex + 1);
            return this.m_chars.getString(offset, length);
        }
        if (1 == type || 11 == type || 9 == type) {
            return null;
        }
        int dataIndex = this._dataOrQName(identity);
        if (dataIndex < 0) {
            dataIndex = -dataIndex;
            dataIndex = this.m_data.elementAt(dataIndex + 1);
        }
        return this.m_valuesOrPrefixes.indexToString(dataIndex);
    }
    
    @Override
    public String getLocalName(final int nodeHandle) {
        return this.m_expandedNameTable.getLocalName(this._exptype(this.makeNodeIdentity(nodeHandle)));
    }
    
    @Override
    public String getUnparsedEntityURI(final String name) {
        String url = "";
        if (null == this.m_entities) {
            return url;
        }
        final int n = this.m_entities.size();
        int i = 0;
        while (i < n) {
            final String ename = this.m_entities.elementAt(i + 3);
            if (null != ename && ename.equals(name)) {
                final String nname = this.m_entities.elementAt(i + 2);
                if (null == nname) {
                    break;
                }
                url = this.m_entities.elementAt(i + 1);
                if (null == url) {
                    url = this.m_entities.elementAt(i + 0);
                    break;
                }
                break;
            }
            else {
                i += 4;
            }
        }
        return url;
    }
    
    @Override
    public String getPrefix(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        final int type = this._type(identity);
        if (1 != type) {
            if (2 == type) {
                int prefixIndex = this._dataOrQName(identity);
                if (prefixIndex < 0) {
                    prefixIndex = this.m_data.elementAt(-prefixIndex);
                    final String qname = this.m_valuesOrPrefixes.indexToString(prefixIndex);
                    return this.getPrefix(qname, null);
                }
            }
            return "";
        }
        int prefixIndex = this._dataOrQName(identity);
        if (0 == prefixIndex) {
            return "";
        }
        final String qname = this.m_valuesOrPrefixes.indexToString(prefixIndex);
        return this.getPrefix(qname, null);
    }
    
    @Override
    public int getAttributeNode(final int nodeHandle, final String namespaceURI, final String name) {
        for (int attrH = this.getFirstAttribute(nodeHandle); -1 != attrH; attrH = this.getNextAttribute(attrH)) {
            final String attrNS = this.getNamespaceURI(attrH);
            final String attrName = this.getLocalName(attrH);
            final boolean nsMatch = namespaceURI == attrNS || (namespaceURI != null && namespaceURI.equals(attrNS));
            if (nsMatch && name.equals(attrName)) {
                return attrH;
            }
        }
        return -1;
    }
    
    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }
    
    @Override
    public String getNamespaceURI(final int nodeHandle) {
        return this.m_expandedNameTable.getNamespace(this._exptype(this.makeNodeIdentity(nodeHandle)));
    }
    
    @Override
    public XMLString getStringValue(final int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        int type;
        if (identity == -1) {
            type = -1;
        }
        else {
            type = this._type(identity);
        }
        if (this.isTextType(type)) {
            final int dataIndex = this._dataOrQName(identity);
            final int offset = this.m_data.elementAt(dataIndex);
            final int length = this.m_data.elementAt(dataIndex + 1);
            return this.m_xstrf.newstr(this.m_chars, offset, length);
        }
        final int firstChild = this._firstch(identity);
        if (-1 != firstChild) {
            int offset = -1;
            int length = 0;
            final int startNode = identity;
            identity = firstChild;
            do {
                type = this._type(identity);
                if (this.isTextType(type)) {
                    final int dataIndex2 = this._dataOrQName(identity);
                    if (-1 == offset) {
                        offset = this.m_data.elementAt(dataIndex2);
                    }
                    length += this.m_data.elementAt(dataIndex2 + 1);
                }
                identity = this.getNextNodeIdentity(identity);
            } while (-1 != identity && this._parent(identity) >= startNode);
            if (length > 0) {
                return this.m_xstrf.newstr(this.m_chars, offset, length);
            }
        }
        else if (type != 1) {
            int dataIndex3 = this._dataOrQName(identity);
            if (dataIndex3 < 0) {
                dataIndex3 = -dataIndex3;
                dataIndex3 = this.m_data.elementAt(dataIndex3 + 1);
            }
            return this.m_xstrf.newstr(this.m_valuesOrPrefixes.indexToString(dataIndex3));
        }
        return this.m_xstrf.emptystr();
    }
    
    public boolean isWhitespace(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        int type;
        if (identity == -1) {
            type = -1;
        }
        else {
            type = this._type(identity);
        }
        if (this.isTextType(type)) {
            final int dataIndex = this._dataOrQName(identity);
            final int offset = this.m_data.elementAt(dataIndex);
            final int length = this.m_data.elementAt(dataIndex + 1);
            return this.m_chars.isWhitespace(offset, length);
        }
        return false;
    }
    
    @Override
    public int getElementById(final String elementId) {
        boolean isMore = true;
        Integer intObj;
        do {
            intObj = this.m_idAttributes.get(elementId);
            if (null != intObj) {
                return this.makeNodeHandle(intObj);
            }
            if (!isMore) {
                break;
            }
            if (this.m_endDocumentOccured) {
                break;
            }
            isMore = this.nextNode();
        } while (null == intObj);
        return -1;
    }
    
    public String getPrefix(final String qname, final String uri) {
        int uriIndex = -1;
        String prefix;
        if (null != uri && uri.length() > 0) {
            do {
                uriIndex = this.m_prefixMappings.indexOf(uri, ++uriIndex);
            } while ((uriIndex & 0x1) == 0x0);
            if (uriIndex >= 0) {
                prefix = this.m_prefixMappings.elementAt(uriIndex - 1);
            }
            else if (null != qname) {
                final int indexOfNSSep = qname.indexOf(58);
                if (qname.equals("xmlns")) {
                    prefix = "";
                }
                else if (qname.startsWith("xmlns:")) {
                    prefix = qname.substring(indexOfNSSep + 1);
                }
                else {
                    prefix = ((indexOfNSSep > 0) ? qname.substring(0, indexOfNSSep) : null);
                }
            }
            else {
                prefix = null;
            }
        }
        else if (null != qname) {
            final int indexOfNSSep = qname.indexOf(58);
            if (indexOfNSSep > 0) {
                if (qname.startsWith("xmlns:")) {
                    prefix = qname.substring(indexOfNSSep + 1);
                }
                else {
                    prefix = qname.substring(0, indexOfNSSep);
                }
            }
            else if (qname.equals("xmlns")) {
                prefix = "";
            }
            else {
                prefix = null;
            }
        }
        else {
            prefix = null;
        }
        return prefix;
    }
    
    public int getIdForNamespace(final String uri) {
        return this.m_valuesOrPrefixes.stringToIndex(uri);
    }
    
    public String getNamespaceURI(String prefix) {
        String uri = "";
        int prefixIndex = this.m_contextIndexes.peek() - 1;
        if (null == prefix) {
            prefix = "";
        }
        do {
            prefixIndex = this.m_prefixMappings.indexOf(prefix, ++prefixIndex);
        } while (prefixIndex >= 0 && (prefixIndex & 0x1) == 0x1);
        if (prefixIndex > -1) {
            uri = this.m_prefixMappings.elementAt(prefixIndex + 1);
        }
        return uri;
    }
    
    public void setIDAttribute(final String id, final int elem) {
        this.m_idAttributes.put(id, elem);
    }
    
    protected void charactersFlush() {
        if (this.m_textPendingStart >= 0) {
            final int length = this.m_chars.size() - this.m_textPendingStart;
            boolean doStrip = false;
            if (this.getShouldStripWhitespace()) {
                doStrip = this.m_chars.isWhitespace(this.m_textPendingStart, length);
            }
            if (doStrip) {
                this.m_chars.setLength(this.m_textPendingStart);
            }
            else if (length > 0) {
                final int exName = this.m_expandedNameTable.getExpandedTypeID(3);
                final int dataIndex = this.m_data.size();
                this.m_previous = this.addNode(this.m_coalescedTextType, exName, this.m_parents.peek(), this.m_previous, dataIndex, false);
                this.m_data.addElement(this.m_textPendingStart);
                this.m_data.addElement(length);
            }
            this.m_textPendingStart = -1;
            final int n = 3;
            this.m_coalescedTextType = n;
            this.m_textType = n;
        }
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        return null;
    }
    
    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final String publicId, String systemId, final String notationName) throws SAXException {
        if (null == this.m_entities) {
            this.m_entities = new Vector();
        }
        try {
            systemId = SystemIDResolver.getAbsoluteURI(systemId, this.getDocumentBaseURI());
        }
        catch (final Exception e) {
            throw new SAXException(e);
        }
        this.m_entities.addElement(publicId);
        this.m_entities.addElement(systemId);
        this.m_entities.addElement(notationName);
        this.m_entities.addElement(name);
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.m_locator = locator;
        this.m_systemId = locator.getSystemId();
    }
    
    @Override
    public void startDocument() throws SAXException {
        final int doc = this.addNode(9, this.m_expandedNameTable.getExpandedTypeID(9), -1, -1, 0, true);
        this.m_parents.push(doc);
        this.m_previous = -1;
        this.m_contextIndexes.push(this.m_prefixMappings.size());
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.charactersFlush();
        this.m_nextsib.setElementAt(-1, 0);
        if (this.m_firstch.elementAt(0) == -2) {
            this.m_firstch.setElementAt(-1, 0);
        }
        if (-1 != this.m_previous) {
            this.m_nextsib.setElementAt(-1, this.m_previous);
        }
        this.m_parents = null;
        this.m_prefixMappings = null;
        this.m_contextIndexes = null;
        this.m_endDocumentOccured = true;
        this.m_locator = null;
    }
    
    @Override
    public void startPrefixMapping(String prefix, final String uri) throws SAXException {
        if (null == prefix) {
            prefix = "";
        }
        this.m_prefixMappings.addElement(prefix);
        this.m_prefixMappings.addElement(uri);
    }
    
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if (null == prefix) {
            prefix = "";
        }
        int index = this.m_contextIndexes.peek() - 1;
        do {
            index = this.m_prefixMappings.indexOf(prefix, ++index);
        } while (index >= 0 && (index & 0x1) == 0x1);
        if (index > -1) {
            this.m_prefixMappings.setElementAt("%@$#^@#", index);
            this.m_prefixMappings.setElementAt("%@$#^@#", index + 1);
        }
    }
    
    protected boolean declAlreadyDeclared(final String prefix) {
        final int startDecls = this.m_contextIndexes.peek();
        final Vector prefixMappings = this.m_prefixMappings;
        for (int nDecls = prefixMappings.size(), i = startDecls; i < nDecls; i += 2) {
            final String prefixDecl = prefixMappings.elementAt(i);
            if (prefixDecl != null) {
                if (prefixDecl.equals(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        this.charactersFlush();
        int exName = this.m_expandedNameTable.getExpandedTypeID(uri, localName, 1);
        String prefix = this.getPrefix(qName, uri);
        int prefixIndex = (null != prefix) ? this.m_valuesOrPrefixes.stringToIndex(qName) : 0;
        final int elemNode = this.addNode(1, exName, this.m_parents.peek(), this.m_previous, prefixIndex, true);
        if (this.m_indexing) {
            this.indexNode(exName, elemNode);
        }
        this.m_parents.push(elemNode);
        final int startDecls = this.m_contextIndexes.peek();
        final int nDecls = this.m_prefixMappings.size();
        int prev = -1;
        if (!this.m_pastFirstElement) {
            prefix = "xml";
            final String declURL = "http://www.w3.org/XML/1998/namespace";
            exName = this.m_expandedNameTable.getExpandedTypeID(null, prefix, 13);
            final int val = this.m_valuesOrPrefixes.stringToIndex(declURL);
            prev = this.addNode(13, exName, elemNode, prev, val, false);
            this.m_pastFirstElement = true;
        }
        for (int i = startDecls; i < nDecls; i += 2) {
            prefix = this.m_prefixMappings.elementAt(i);
            if (prefix != null) {
                final String declURL2 = this.m_prefixMappings.elementAt(i + 1);
                exName = this.m_expandedNameTable.getExpandedTypeID(null, prefix, 13);
                final int val2 = this.m_valuesOrPrefixes.stringToIndex(declURL2);
                prev = this.addNode(13, exName, elemNode, prev, val2, false);
            }
        }
        for (int n = attributes.getLength(), j = 0; j < n; ++j) {
            final String attrUri = attributes.getURI(j);
            final String attrQName = attributes.getQName(j);
            String valString = attributes.getValue(j);
            prefix = this.getPrefix(attrQName, attrUri);
            final String attrLocalName = attributes.getLocalName(j);
            int nodeType;
            if (null != attrQName && (attrQName.equals("xmlns") || attrQName.startsWith("xmlns:"))) {
                if (this.declAlreadyDeclared(prefix)) {
                    continue;
                }
                nodeType = 13;
            }
            else {
                nodeType = 2;
                if (attributes.getType(j).equalsIgnoreCase("ID")) {
                    this.setIDAttribute(valString, elemNode);
                }
            }
            if (null == valString) {
                valString = "";
            }
            int val3 = this.m_valuesOrPrefixes.stringToIndex(valString);
            if (null != prefix) {
                prefixIndex = this.m_valuesOrPrefixes.stringToIndex(attrQName);
                final int dataIndex = this.m_data.size();
                this.m_data.addElement(prefixIndex);
                this.m_data.addElement(val3);
                val3 = -dataIndex;
            }
            exName = this.m_expandedNameTable.getExpandedTypeID(attrUri, attrLocalName, nodeType);
            prev = this.addNode(nodeType, exName, elemNode, prev, val3, false);
        }
        if (-1 != prev) {
            this.m_nextsib.setElementAt(-1, prev);
        }
        if (null != this.m_wsfilter) {
            final short wsv = this.m_wsfilter.getShouldStripSpace(this.makeNodeHandle(elemNode), this);
            final boolean shouldStrip = (3 == wsv) ? this.getShouldStripWhitespace() : (2 == wsv);
            this.pushShouldStripWhitespace(shouldStrip);
        }
        this.m_previous = -1;
        this.m_contextIndexes.push(this.m_prefixMappings.size());
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.charactersFlush();
        this.m_contextIndexes.quickPop(1);
        final int topContextIndex = this.m_contextIndexes.peek();
        if (topContextIndex != this.m_prefixMappings.size()) {
            this.m_prefixMappings.setSize(topContextIndex);
        }
        final int lastNode = this.m_previous;
        this.m_previous = this.m_parents.pop();
        if (-1 == lastNode) {
            this.m_firstch.setElementAt(-1, this.m_previous);
        }
        else {
            this.m_nextsib.setElementAt(-1, lastNode);
        }
        this.popShouldStripWhitespace();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.m_textPendingStart == -1) {
            this.m_textPendingStart = this.m_chars.size();
            this.m_coalescedTextType = this.m_textType;
        }
        else if (this.m_textType == 3) {
            this.m_coalescedTextType = 3;
        }
        this.m_chars.append(ch, start, length);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.characters(ch, start, length);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.charactersFlush();
        final int exName = this.m_expandedNameTable.getExpandedTypeID(null, target, 7);
        final int dataIndex = this.m_valuesOrPrefixes.stringToIndex(data);
        this.m_previous = this.addNode(7, exName, this.m_parents.peek(), this.m_previous, dataIndex, false);
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        System.err.println(e.getMessage());
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    @Override
    public void elementDecl(final String name, final String model) throws SAXException {
    }
    
    @Override
    public void attributeDecl(final String eName, final String aName, final String type, final String valueDefault, final String value) throws SAXException {
    }
    
    @Override
    public void internalEntityDecl(final String name, final String value) throws SAXException {
    }
    
    @Override
    public void externalEntityDecl(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        this.m_insideDTD = true;
    }
    
    @Override
    public void endDTD() throws SAXException {
        this.m_insideDTD = false;
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this.m_textType = 4;
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this.m_textType = 3;
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.m_insideDTD) {
            return;
        }
        this.charactersFlush();
        final int exName = this.m_expandedNameTable.getExpandedTypeID(8);
        final int dataIndex = this.m_valuesOrPrefixes.stringToIndex(new String(ch, start, length));
        this.m_previous = this.addNode(8, exName, this.m_parents.peek(), this.m_previous, dataIndex, false);
    }
    
    @Override
    public void setProperty(final String property, final Object value) {
    }
    
    @Override
    public SourceLocator getSourceLocatorFor(int node) {
        if (this.m_useSourceLocationProperty) {
            node = this.makeNodeIdentity(node);
            return new NodeLocator(null, this.m_sourceSystemId.elementAt(node), this.m_sourceLine.elementAt(node), this.m_sourceColumn.elementAt(node));
        }
        if (this.m_locator != null) {
            return new NodeLocator(null, this.m_locator.getSystemId(), -1, -1);
        }
        if (this.m_systemId != null) {
            return new NodeLocator(null, this.m_systemId, -1, -1);
        }
        return null;
    }
    
    public String getFixedNames(final int type) {
        return SAX2DTM.m_fixednames[type];
    }
    
    static {
        m_fixednames = new String[] { null, null, null, "#text", "#cdata_section", null, null, null, "#comment", "#document", null, "#document-fragment", null };
    }
}
