package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.utils.XMLStringDefault;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import org.xml.sax.Attributes;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.XMLString;
import java.util.Vector;
import com.sun.org.apache.xml.internal.dtm.ref.ExtendedType;

public class SAX2DTM2 extends SAX2DTM
{
    private int[] m_exptype_map0;
    private int[] m_nextsib_map0;
    private int[] m_firstch_map0;
    private int[] m_parent_map0;
    private int[][] m_exptype_map;
    private int[][] m_nextsib_map;
    private int[][] m_firstch_map;
    private int[][] m_parent_map;
    protected ExtendedType[] m_extendedTypes;
    protected Vector m_values;
    private int m_valueIndex;
    private int m_maxNodeIndex;
    protected int m_SHIFT;
    protected int m_MASK;
    protected int m_blocksize;
    protected static final int TEXT_LENGTH_BITS = 10;
    protected static final int TEXT_OFFSET_BITS = 21;
    protected static final int TEXT_LENGTH_MAX = 1023;
    protected static final int TEXT_OFFSET_MAX = 2097151;
    protected boolean m_buildIdIndex;
    private static final String EMPTY_STR = "";
    private static final XMLString EMPTY_XML_STR;
    
    public SAX2DTM2(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing) {
        this(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, 512, true, true, false);
    }
    
    public SAX2DTM2(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing, int blocksize, final boolean usePrevsib, final boolean buildIdIndex, final boolean newNameTable) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, blocksize, usePrevsib, newNameTable);
        this.m_valueIndex = 0;
        this.m_buildIdIndex = true;
        int shift = 0;
        while ((blocksize >>>= 1) != 0) {
            ++shift;
        }
        this.m_blocksize = 1 << shift;
        this.m_SHIFT = shift;
        this.m_MASK = this.m_blocksize - 1;
        this.m_buildIdIndex = buildIdIndex;
        this.m_values = new Vector(32, 512);
        this.m_maxNodeIndex = 65536;
        this.m_exptype_map0 = this.m_exptype.getMap0();
        this.m_nextsib_map0 = this.m_nextsib.getMap0();
        this.m_firstch_map0 = this.m_firstch.getMap0();
        this.m_parent_map0 = this.m_parent.getMap0();
    }
    
    public final int _exptype(final int identity) {
        return this.m_exptype.elementAt(identity);
    }
    
    public final int _exptype2(final int identity) {
        if (identity < this.m_blocksize) {
            return this.m_exptype_map0[identity];
        }
        return this.m_exptype_map[identity >>> this.m_SHIFT][identity & this.m_MASK];
    }
    
    public final int _nextsib2(final int identity) {
        if (identity < this.m_blocksize) {
            return this.m_nextsib_map0[identity];
        }
        return this.m_nextsib_map[identity >>> this.m_SHIFT][identity & this.m_MASK];
    }
    
    public final int _firstch2(final int identity) {
        if (identity < this.m_blocksize) {
            return this.m_firstch_map0[identity];
        }
        return this.m_firstch_map[identity >>> this.m_SHIFT][identity & this.m_MASK];
    }
    
    public final int _parent2(final int identity) {
        if (identity < this.m_blocksize) {
            return this.m_parent_map0[identity];
        }
        return this.m_parent_map[identity >>> this.m_SHIFT][identity & this.m_MASK];
    }
    
    public final int _type2(final int identity) {
        int eType;
        if (identity < this.m_blocksize) {
            eType = this.m_exptype_map0[identity];
        }
        else {
            eType = this.m_exptype_map[identity >>> this.m_SHIFT][identity & this.m_MASK];
        }
        if (-1 != eType) {
            return this.m_extendedTypes[eType].getNodeType();
        }
        return -1;
    }
    
    public final int getExpandedTypeID2(final int nodeHandle) {
        final int nodeID = this.makeNodeIdentity(nodeHandle);
        if (nodeID == -1) {
            return -1;
        }
        if (nodeID < this.m_blocksize) {
            return this.m_exptype_map0[nodeID];
        }
        return this.m_exptype_map[nodeID >>> this.m_SHIFT][nodeID & this.m_MASK];
    }
    
    public final int _exptype2Type(final int exptype) {
        if (-1 != exptype) {
            return this.m_extendedTypes[exptype].getNodeType();
        }
        return -1;
    }
    
    @Override
    public int getIdForNamespace(final String uri) {
        final int index = this.m_values.indexOf(uri);
        if (index < 0) {
            this.m_values.addElement(uri);
            return this.m_valueIndex++;
        }
        return index;
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        this.charactersFlush();
        int exName = this.m_expandedNameTable.getExpandedTypeID(uri, localName, 1);
        int prefixIndex = (qName.length() != localName.length()) ? this.m_valuesOrPrefixes.stringToIndex(qName) : 0;
        final int elemNode = this.addNode(1, exName, this.m_parents.peek(), this.m_previous, prefixIndex, true);
        if (this.m_indexing) {
            this.indexNode(exName, elemNode);
        }
        this.m_parents.push(elemNode);
        final int startDecls = this.m_contextIndexes.peek();
        final int nDecls = this.m_prefixMappings.size();
        if (!this.m_pastFirstElement) {
            final String prefix = "xml";
            final String declURL = "http://www.w3.org/XML/1998/namespace";
            exName = this.m_expandedNameTable.getExpandedTypeID(null, prefix, 13);
            this.m_values.addElement(declURL);
            final int val = this.m_valueIndex++;
            this.addNode(13, exName, elemNode, -1, val, false);
            this.m_pastFirstElement = true;
        }
        for (int i = startDecls; i < nDecls; i += 2) {
            final String prefix = this.m_prefixMappings.elementAt(i);
            if (prefix != null) {
                final String declURL2 = this.m_prefixMappings.elementAt(i + 1);
                exName = this.m_expandedNameTable.getExpandedTypeID(null, prefix, 13);
                this.m_values.addElement(declURL2);
                final int val2 = this.m_valueIndex++;
                this.addNode(13, exName, elemNode, -1, val2, false);
            }
        }
        for (int n = attributes.getLength(), j = 0; j < n; ++j) {
            final String attrUri = attributes.getURI(j);
            final String attrQName = attributes.getQName(j);
            String valString = attributes.getValue(j);
            final String attrLocalName = attributes.getLocalName(j);
            int nodeType;
            if (null != attrQName && (attrQName.equals("xmlns") || attrQName.startsWith("xmlns:"))) {
                final String prefix = this.getPrefix(attrQName, attrUri);
                if (this.declAlreadyDeclared(prefix)) {
                    continue;
                }
                nodeType = 13;
            }
            else {
                nodeType = 2;
                if (this.m_buildIdIndex && attributes.getType(j).equalsIgnoreCase("ID")) {
                    this.setIDAttribute(valString, elemNode);
                }
            }
            if (null == valString) {
                valString = "";
            }
            this.m_values.addElement(valString);
            int val3 = this.m_valueIndex++;
            if (attrLocalName.length() != attrQName.length()) {
                prefixIndex = this.m_valuesOrPrefixes.stringToIndex(attrQName);
                final int dataIndex = this.m_data.size();
                this.m_data.addElement(prefixIndex);
                this.m_data.addElement(val3);
                val3 = -dataIndex;
            }
            exName = this.m_expandedNameTable.getExpandedTypeID(attrUri, attrLocalName, nodeType);
            this.addNode(nodeType, exName, elemNode, -1, val3, false);
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
        this.m_previous = this.m_parents.pop();
        this.popShouldStripWhitespace();
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.m_insideDTD) {
            return;
        }
        this.charactersFlush();
        this.m_values.addElement(new String(ch, start, length));
        final int dataIndex = this.m_valueIndex++;
        this.m_previous = this.addNode(8, 8, this.m_parents.peek(), this.m_previous, dataIndex, false);
    }
    
    @Override
    public void startDocument() throws SAXException {
        final int doc = this.addNode(9, 9, -1, -1, 0, true);
        this.m_parents.push(doc);
        this.m_previous = -1;
        this.m_contextIndexes.push(this.m_prefixMappings.size());
    }
    
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        this.m_exptype.addElement(-1);
        this.m_parent.addElement(-1);
        this.m_nextsib.addElement(-1);
        this.m_firstch.addElement(-1);
        this.m_extendedTypes = this.m_expandedNameTable.getExtendedTypes();
        this.m_exptype_map = this.m_exptype.getMap();
        this.m_nextsib_map = this.m_nextsib.getMap();
        this.m_firstch_map = this.m_firstch.getMap();
        this.m_parent_map = this.m_parent.getMap();
    }
    
    @Override
    protected final int addNode(final int type, final int expandedTypeID, final int parentIndex, final int previousSibling, final int dataOrPrefix, final boolean canHaveFirstChild) {
        final int nodeIndex = this.m_size++;
        if (nodeIndex == this.m_maxNodeIndex) {
            this.addNewDTMID(nodeIndex);
            this.m_maxNodeIndex += 65536;
        }
        this.m_firstch.addElement(-1);
        this.m_nextsib.addElement(-1);
        this.m_parent.addElement(parentIndex);
        this.m_exptype.addElement(expandedTypeID);
        this.m_dataOrQName.addElement(dataOrPrefix);
        if (this.m_prevsib != null) {
            this.m_prevsib.addElement(previousSibling);
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
                if (-1 != previousSibling) {
                    this.m_nextsib.setElementAt(nodeIndex, previousSibling);
                    break;
                }
                if (-1 != parentIndex) {
                    this.m_firstch.setElementAt(nodeIndex, parentIndex);
                    break;
                }
                break;
            }
        }
        return nodeIndex;
    }
    
    @Override
    protected final void charactersFlush() {
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
                if (length <= 1023 && this.m_textPendingStart <= 2097151) {
                    this.m_previous = this.addNode(this.m_coalescedTextType, 3, this.m_parents.peek(), this.m_previous, length + (this.m_textPendingStart << 10), false);
                }
                else {
                    final int dataIndex = this.m_data.size();
                    this.m_previous = this.addNode(this.m_coalescedTextType, 3, this.m_parents.peek(), this.m_previous, -dataIndex, false);
                    this.m_data.addElement(this.m_textPendingStart);
                    this.m_data.addElement(length);
                }
            }
            this.m_textPendingStart = -1;
            final int n = 3;
            this.m_coalescedTextType = n;
            this.m_textType = n;
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.charactersFlush();
        final int dataIndex = this.m_data.size();
        this.m_previous = this.addNode(7, 7, this.m_parents.peek(), this.m_previous, -dataIndex, false);
        this.m_data.addElement(this.m_valuesOrPrefixes.stringToIndex(target));
        this.m_values.addElement(data);
        this.m_data.addElement(this.m_valueIndex++);
    }
    
    @Override
    public final int getFirstAttribute(final int nodeHandle) {
        int nodeID = this.makeNodeIdentity(nodeHandle);
        if (nodeID == -1) {
            return -1;
        }
        int type = this._type2(nodeID);
        if (1 == type) {
            do {
                ++nodeID;
                type = this._type2(nodeID);
                if (type == 2) {
                    return this.makeNodeHandle(nodeID);
                }
            } while (13 == type);
        }
        return -1;
    }
    
    @Override
    protected int getFirstAttributeIdentity(int identity) {
        if (identity == -1) {
            return -1;
        }
        int type = this._type2(identity);
        if (1 == type) {
            do {
                ++identity;
                type = this._type2(identity);
                if (type == 2) {
                    return identity;
                }
            } while (13 == type);
        }
        return -1;
    }
    
    @Override
    protected int getNextAttributeIdentity(int identity) {
        while (true) {
            ++identity;
            final int type = this._type2(identity);
            if (type == 2) {
                return identity;
            }
            if (type != 13) {
                return -1;
            }
        }
    }
    
    @Override
    protected final int getTypedAttribute(final int nodeHandle, final int attType) {
        int nodeID = this.makeNodeIdentity(nodeHandle);
        if (nodeID == -1) {
            return -1;
        }
        int type = this._type2(nodeID);
        if (1 == type) {
            while (true) {
                ++nodeID;
                final int expType = this._exptype2(nodeID);
                if (expType == -1) {
                    return -1;
                }
                type = this.m_extendedTypes[expType].getNodeType();
                if (type == 2) {
                    if (expType == attType) {
                        return this.makeNodeHandle(nodeID);
                    }
                    continue;
                }
                else {
                    if (13 != type) {
                        break;
                    }
                    continue;
                }
            }
        }
        return -1;
    }
    
    @Override
    public String getLocalName(final int nodeHandle) {
        final int expType = this._exptype(this.makeNodeIdentity(nodeHandle));
        if (expType == 7) {
            int dataIndex = this._dataOrQName(this.makeNodeIdentity(nodeHandle));
            dataIndex = this.m_data.elementAt(-dataIndex);
            return this.m_valuesOrPrefixes.indexToString(dataIndex);
        }
        return this.m_expandedNameTable.getLocalName(expType);
    }
    
    @Override
    public final String getNodeNameX(final int nodeHandle) {
        final int nodeID = this.makeNodeIdentity(nodeHandle);
        final int eType = this._exptype2(nodeID);
        if (eType == 7) {
            int dataIndex = this._dataOrQName(nodeID);
            dataIndex = this.m_data.elementAt(-dataIndex);
            return this.m_valuesOrPrefixes.indexToString(dataIndex);
        }
        final ExtendedType extType = this.m_extendedTypes[eType];
        if (extType.getNamespace().length() == 0) {
            return extType.getLocalName();
        }
        int qnameIndex = this.m_dataOrQName.elementAt(nodeID);
        if (qnameIndex == 0) {
            return extType.getLocalName();
        }
        if (qnameIndex < 0) {
            qnameIndex = -qnameIndex;
            qnameIndex = this.m_data.elementAt(qnameIndex);
        }
        return this.m_valuesOrPrefixes.indexToString(qnameIndex);
    }
    
    @Override
    public String getNodeName(final int nodeHandle) {
        final int nodeID = this.makeNodeIdentity(nodeHandle);
        final int eType = this._exptype2(nodeID);
        final ExtendedType extType = this.m_extendedTypes[eType];
        if (extType.getNamespace().length() == 0) {
            final int type = extType.getNodeType();
            final String localName = extType.getLocalName();
            if (type == 13) {
                if (localName.length() == 0) {
                    return "xmlns";
                }
                return "xmlns:" + localName;
            }
            else {
                if (type == 7) {
                    int dataIndex = this._dataOrQName(nodeID);
                    dataIndex = this.m_data.elementAt(-dataIndex);
                    return this.m_valuesOrPrefixes.indexToString(dataIndex);
                }
                if (localName.length() == 0) {
                    return this.getFixedNames(type);
                }
                return localName;
            }
        }
        else {
            int qnameIndex = this.m_dataOrQName.elementAt(nodeID);
            if (qnameIndex == 0) {
                return extType.getLocalName();
            }
            if (qnameIndex < 0) {
                qnameIndex = -qnameIndex;
                qnameIndex = this.m_data.elementAt(qnameIndex);
            }
            return this.m_valuesOrPrefixes.indexToString(qnameIndex);
        }
    }
    
    @Override
    public XMLString getStringValue(final int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        if (identity == -1) {
            return SAX2DTM2.EMPTY_XML_STR;
        }
        int type = this._type2(identity);
        if (type == 1 || type == 9) {
            final int startNode = identity;
            identity = this._firstch2(identity);
            if (-1 == identity) {
                return SAX2DTM2.EMPTY_XML_STR;
            }
            int offset = -1;
            int length = 0;
            do {
                type = this._exptype2(identity);
                if (type == 3 || type == 4) {
                    final int dataIndex = this.m_dataOrQName.elementAt(identity);
                    if (dataIndex >= 0) {
                        if (-1 == offset) {
                            offset = dataIndex >>> 10;
                        }
                        length += (dataIndex & 0x3FF);
                    }
                    else {
                        if (-1 == offset) {
                            offset = this.m_data.elementAt(-dataIndex);
                        }
                        length += this.m_data.elementAt(-dataIndex + 1);
                    }
                }
                ++identity;
            } while (this._parent2(identity) >= startNode);
            if (length <= 0) {
                return SAX2DTM2.EMPTY_XML_STR;
            }
            if (this.m_xstrf != null) {
                return this.m_xstrf.newstr(this.m_chars, offset, length);
            }
            return new XMLStringDefault(this.m_chars.getString(offset, length));
        }
        else if (3 == type || 4 == type) {
            final int dataIndex2 = this.m_dataOrQName.elementAt(identity);
            if (dataIndex2 >= 0) {
                if (this.m_xstrf != null) {
                    return this.m_xstrf.newstr(this.m_chars, dataIndex2 >>> 10, dataIndex2 & 0x3FF);
                }
                return new XMLStringDefault(this.m_chars.getString(dataIndex2 >>> 10, dataIndex2 & 0x3FF));
            }
            else {
                if (this.m_xstrf != null) {
                    return this.m_xstrf.newstr(this.m_chars, this.m_data.elementAt(-dataIndex2), this.m_data.elementAt(-dataIndex2 + 1));
                }
                return new XMLStringDefault(this.m_chars.getString(this.m_data.elementAt(-dataIndex2), this.m_data.elementAt(-dataIndex2 + 1)));
            }
        }
        else {
            int dataIndex2 = this.m_dataOrQName.elementAt(identity);
            if (dataIndex2 < 0) {
                dataIndex2 = -dataIndex2;
                dataIndex2 = this.m_data.elementAt(dataIndex2 + 1);
            }
            if (this.m_xstrf != null) {
                return this.m_xstrf.newstr(this.m_values.elementAt(dataIndex2));
            }
            return new XMLStringDefault(this.m_values.elementAt(dataIndex2));
        }
    }
    
    public final String getStringValueX(final int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        if (identity == -1) {
            return "";
        }
        int type = this._type2(identity);
        if (type == 1 || type == 9) {
            final int startNode = identity;
            identity = this._firstch2(identity);
            if (-1 == identity) {
                return "";
            }
            int offset = -1;
            int length = 0;
            do {
                type = this._exptype2(identity);
                if (type == 3 || type == 4) {
                    final int dataIndex = this.m_dataOrQName.elementAt(identity);
                    if (dataIndex >= 0) {
                        if (-1 == offset) {
                            offset = dataIndex >>> 10;
                        }
                        length += (dataIndex & 0x3FF);
                    }
                    else {
                        if (-1 == offset) {
                            offset = this.m_data.elementAt(-dataIndex);
                        }
                        length += this.m_data.elementAt(-dataIndex + 1);
                    }
                }
                ++identity;
            } while (this._parent2(identity) >= startNode);
            if (length > 0) {
                return this.m_chars.getString(offset, length);
            }
            return "";
        }
        else {
            if (3 != type && 4 != type) {
                int dataIndex2 = this.m_dataOrQName.elementAt(identity);
                if (dataIndex2 < 0) {
                    dataIndex2 = -dataIndex2;
                    dataIndex2 = this.m_data.elementAt(dataIndex2 + 1);
                }
                return this.m_values.elementAt(dataIndex2);
            }
            int dataIndex2 = this.m_dataOrQName.elementAt(identity);
            if (dataIndex2 >= 0) {
                return this.m_chars.getString(dataIndex2 >>> 10, dataIndex2 & 0x3FF);
            }
            return this.m_chars.getString(this.m_data.elementAt(-dataIndex2), this.m_data.elementAt(-dataIndex2 + 1));
        }
    }
    
    public String getStringValue() {
        final int child = this._firstch2(0);
        if (child == -1) {
            return "";
        }
        if (this._exptype2(child) != 3 || this._nextsib2(child) != -1) {
            return this.getStringValueX(this.getDocument());
        }
        final int dataIndex = this.m_dataOrQName.elementAt(child);
        if (dataIndex >= 0) {
            return this.m_chars.getString(dataIndex >>> 10, dataIndex & 0x3FF);
        }
        return this.m_chars.getString(this.m_data.elementAt(-dataIndex), this.m_data.elementAt(-dataIndex + 1));
    }
    
    @Override
    public final void dispatchCharactersEvents(final int nodeHandle, final ContentHandler ch, final boolean normalize) throws SAXException {
        int identity = this.makeNodeIdentity(nodeHandle);
        if (identity == -1) {
            return;
        }
        int type = this._type2(identity);
        if (type == 1 || type == 9) {
            final int startNode = identity;
            identity = this._firstch2(identity);
            if (-1 != identity) {
                int offset = -1;
                int length = 0;
                do {
                    type = this._exptype2(identity);
                    if (type == 3 || type == 4) {
                        final int dataIndex = this.m_dataOrQName.elementAt(identity);
                        if (dataIndex >= 0) {
                            if (-1 == offset) {
                                offset = dataIndex >>> 10;
                            }
                            length += (dataIndex & 0x3FF);
                        }
                        else {
                            if (-1 == offset) {
                                offset = this.m_data.elementAt(-dataIndex);
                            }
                            length += this.m_data.elementAt(-dataIndex + 1);
                        }
                    }
                    ++identity;
                } while (this._parent2(identity) >= startNode);
                if (length > 0) {
                    if (normalize) {
                        this.m_chars.sendNormalizedSAXcharacters(ch, offset, length);
                    }
                    else {
                        this.m_chars.sendSAXcharacters(ch, offset, length);
                    }
                }
            }
        }
        else if (3 == type || 4 == type) {
            final int dataIndex2 = this.m_dataOrQName.elementAt(identity);
            if (dataIndex2 >= 0) {
                if (normalize) {
                    this.m_chars.sendNormalizedSAXcharacters(ch, dataIndex2 >>> 10, dataIndex2 & 0x3FF);
                }
                else {
                    this.m_chars.sendSAXcharacters(ch, dataIndex2 >>> 10, dataIndex2 & 0x3FF);
                }
            }
            else if (normalize) {
                this.m_chars.sendNormalizedSAXcharacters(ch, this.m_data.elementAt(-dataIndex2), this.m_data.elementAt(-dataIndex2 + 1));
            }
            else {
                this.m_chars.sendSAXcharacters(ch, this.m_data.elementAt(-dataIndex2), this.m_data.elementAt(-dataIndex2 + 1));
            }
        }
        else {
            int dataIndex2 = this.m_dataOrQName.elementAt(identity);
            if (dataIndex2 < 0) {
                dataIndex2 = -dataIndex2;
                dataIndex2 = this.m_data.elementAt(dataIndex2 + 1);
            }
            final String str = this.m_values.elementAt(dataIndex2);
            if (normalize) {
                FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 0, str.length(), ch);
            }
            else {
                ch.characters(str.toCharArray(), 0, str.length());
            }
        }
    }
    
    @Override
    public String getNodeValue(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        final int type = this._type2(identity);
        if (type == 3 || type == 4) {
            final int dataIndex = this._dataOrQName(identity);
            if (dataIndex > 0) {
                return this.m_chars.getString(dataIndex >>> 10, dataIndex & 0x3FF);
            }
            return this.m_chars.getString(this.m_data.elementAt(-dataIndex), this.m_data.elementAt(-dataIndex + 1));
        }
        else {
            if (1 == type || 11 == type || 9 == type) {
                return null;
            }
            int dataIndex = this.m_dataOrQName.elementAt(identity);
            if (dataIndex < 0) {
                dataIndex = -dataIndex;
                dataIndex = this.m_data.elementAt(dataIndex + 1);
            }
            return this.m_values.elementAt(dataIndex);
        }
    }
    
    protected final void copyTextNode(final int nodeID, final SerializationHandler handler) throws SAXException {
        if (nodeID != -1) {
            final int dataIndex = this.m_dataOrQName.elementAt(nodeID);
            if (dataIndex >= 0) {
                this.m_chars.sendSAXcharacters(handler, dataIndex >>> 10, dataIndex & 0x3FF);
            }
            else {
                this.m_chars.sendSAXcharacters(handler, this.m_data.elementAt(-dataIndex), this.m_data.elementAt(-dataIndex + 1));
            }
        }
    }
    
    protected final String copyElement(final int nodeID, final int exptype, final SerializationHandler handler) throws SAXException {
        final ExtendedType extType = this.m_extendedTypes[exptype];
        final String uri = extType.getNamespace();
        final String name = extType.getLocalName();
        if (uri.length() == 0) {
            handler.startElement(name);
            return name;
        }
        int qnameIndex = this.m_dataOrQName.elementAt(nodeID);
        if (qnameIndex == 0) {
            handler.startElement(name);
            handler.namespaceAfterStartElement("", uri);
            return name;
        }
        if (qnameIndex < 0) {
            qnameIndex = -qnameIndex;
            qnameIndex = this.m_data.elementAt(qnameIndex);
        }
        final String qName = this.m_valuesOrPrefixes.indexToString(qnameIndex);
        handler.startElement(qName);
        final int prefixIndex = qName.indexOf(58);
        String prefix;
        if (prefixIndex > 0) {
            prefix = qName.substring(0, prefixIndex);
        }
        else {
            prefix = null;
        }
        handler.namespaceAfterStartElement(prefix, uri);
        return qName;
    }
    
    protected final void copyNS(final int nodeID, final SerializationHandler handler, final boolean inScope) throws SAXException {
        if (this.m_namespaceDeclSetElements != null && this.m_namespaceDeclSetElements.size() == 1 && this.m_namespaceDeclSets != null && this.m_namespaceDeclSets.elementAt(0).size() == 1) {
            return;
        }
        SuballocatedIntVector nsContext = null;
        int nextNSNode;
        if (inScope) {
            nsContext = this.findNamespaceContext(nodeID);
            if (nsContext == null || nsContext.size() < 1) {
                return;
            }
            nextNSNode = this.makeNodeIdentity(nsContext.elementAt(0));
        }
        else {
            nextNSNode = this.getNextNamespaceNode2(nodeID);
        }
        int nsIndex = 1;
        while (nextNSNode != -1) {
            final int eType = this._exptype2(nextNSNode);
            final String nodeName = this.m_extendedTypes[eType].getLocalName();
            int dataIndex = this.m_dataOrQName.elementAt(nextNSNode);
            if (dataIndex < 0) {
                dataIndex = -dataIndex;
                dataIndex = this.m_data.elementAt(dataIndex + 1);
            }
            final String nodeValue = this.m_values.elementAt(dataIndex);
            handler.namespaceAfterStartElement(nodeName, nodeValue);
            if (inScope) {
                if (nsIndex >= nsContext.size()) {
                    return;
                }
                nextNSNode = this.makeNodeIdentity(nsContext.elementAt(nsIndex));
                ++nsIndex;
            }
            else {
                nextNSNode = this.getNextNamespaceNode2(nextNSNode);
            }
        }
    }
    
    protected final int getNextNamespaceNode2(int baseID) {
        int type;
        while ((type = this._type2(++baseID)) == 2) {}
        if (type == 13) {
            return baseID;
        }
        return -1;
    }
    
    protected final void copyAttributes(final int nodeID, final SerializationHandler handler) throws SAXException {
        for (int current = this.getFirstAttributeIdentity(nodeID); current != -1; current = this.getNextAttributeIdentity(current)) {
            final int eType = this._exptype2(current);
            this.copyAttribute(current, eType, handler);
        }
    }
    
    protected final void copyAttribute(final int nodeID, final int exptype, final SerializationHandler handler) throws SAXException {
        final ExtendedType extType = this.m_extendedTypes[exptype];
        final String uri = extType.getNamespace();
        final String localName = extType.getLocalName();
        String prefix = null;
        String qname = null;
        int valueIndex;
        final int dataIndex = valueIndex = this._dataOrQName(nodeID);
        if (dataIndex <= 0) {
            final int prefixIndex = this.m_data.elementAt(-dataIndex);
            valueIndex = this.m_data.elementAt(-dataIndex + 1);
            qname = this.m_valuesOrPrefixes.indexToString(prefixIndex);
            final int colonIndex = qname.indexOf(58);
            if (colonIndex > 0) {
                prefix = qname.substring(0, colonIndex);
            }
        }
        if (uri.length() != 0) {
            handler.namespaceAfterStartElement(prefix, uri);
        }
        final String nodeName = (prefix != null) ? qname : localName;
        final String nodeValue = this.m_values.elementAt(valueIndex);
        handler.addAttribute(uri, localName, nodeName, "CDATA", nodeValue);
    }
    
    static {
        EMPTY_XML_STR = new XMLStringDefault("");
    }
    
    public final class ChildrenIterator extends InternalAxisIteratorBase
    {
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                this._startNode = node;
                this._currentNode = ((node == -1) ? -1 : SAX2DTM2.this._firstch2(SAX2DTM2.this.makeNodeIdentity(node)));
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            if (this._currentNode != -1) {
                final int node = this._currentNode;
                this._currentNode = SAX2DTM2.this._nextsib2(node);
                return this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
            }
            return -1;
        }
    }
    
    public final class ParentIterator extends InternalAxisIteratorBase
    {
        private int _nodeType;
        
        public ParentIterator() {
            this._nodeType = -1;
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                if ((this._startNode = node) != -1) {
                    this._currentNode = SAX2DTM2.this._parent2(SAX2DTM2.this.makeNodeIdentity(node));
                }
                else {
                    this._currentNode = -1;
                }
                return this.resetPosition();
            }
            return this;
        }
        
        public DTMAxisIterator setNodeType(final int type) {
            this._nodeType = type;
            return this;
        }
        
        @Override
        public int next() {
            final int result = this._currentNode;
            if (result == -1) {
                return -1;
            }
            if (this._nodeType == -1) {
                this._currentNode = -1;
                return this.returnNode(SAX2DTM2.this.makeNodeHandle(result));
            }
            if (this._nodeType >= 14) {
                if (this._nodeType == SAX2DTM2.this._exptype2(result)) {
                    this._currentNode = -1;
                    return this.returnNode(SAX2DTM2.this.makeNodeHandle(result));
                }
            }
            else if (this._nodeType == SAX2DTM2.this._type2(result)) {
                this._currentNode = -1;
                return this.returnNode(SAX2DTM2.this.makeNodeHandle(result));
            }
            return -1;
        }
    }
    
    public final class TypedChildrenIterator extends InternalAxisIteratorBase
    {
        private final int _nodeType;
        
        public TypedChildrenIterator(final int nodeType) {
            this._nodeType = nodeType;
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                this._startNode = node;
                this._currentNode = ((node == -1) ? -1 : SAX2DTM2.this._firstch2(SAX2DTM2.this.makeNodeIdentity(this._startNode)));
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            int node = this._currentNode;
            if (node == -1) {
                return -1;
            }
            final int nodeType = this._nodeType;
            if (nodeType != 1) {
                while (node != -1 && SAX2DTM2.this._exptype2(node) != nodeType) {
                    node = SAX2DTM2.this._nextsib2(node);
                }
            }
            else {
                while (node != -1) {
                    final int eType = SAX2DTM2.this._exptype2(node);
                    if (eType >= 14) {
                        break;
                    }
                    node = SAX2DTM2.this._nextsib2(node);
                }
            }
            if (node == -1) {
                return this._currentNode = -1;
            }
            this._currentNode = SAX2DTM2.this._nextsib2(node);
            return this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
        
        @Override
        public int getNodeByPosition(final int position) {
            if (position <= 0) {
                return -1;
            }
            int node = this._currentNode;
            int pos = 0;
            final int nodeType = this._nodeType;
            if (nodeType != 1) {
                while (node != -1) {
                    if (SAX2DTM2.this._exptype2(node) == nodeType && ++pos == position) {
                        return SAX2DTM2.this.makeNodeHandle(node);
                    }
                    node = SAX2DTM2.this._nextsib2(node);
                }
                return -1;
            }
            while (node != -1) {
                if (SAX2DTM2.this._exptype2(node) >= 14 && ++pos == position) {
                    return SAX2DTM2.this.makeNodeHandle(node);
                }
                node = SAX2DTM2.this._nextsib2(node);
            }
            return -1;
        }
    }
    
    public class TypedRootIterator extends RootIterator
    {
        private final int _nodeType;
        
        public TypedRootIterator(final int nodeType) {
            this._nodeType = nodeType;
        }
        
        @Override
        public int next() {
            if (this._startNode == this._currentNode) {
                return -1;
            }
            final int node = this._startNode;
            final int expType = SAX2DTM2.this._exptype2(SAX2DTM2.this.makeNodeIdentity(node));
            this._currentNode = node;
            if (this._nodeType >= 14) {
                if (this._nodeType == expType) {
                    return this.returnNode(node);
                }
            }
            else if (expType < 14) {
                if (expType == this._nodeType) {
                    return this.returnNode(node);
                }
            }
            else if (SAX2DTM2.this.m_extendedTypes[expType].getNodeType() == this._nodeType) {
                return this.returnNode(node);
            }
            return -1;
        }
    }
    
    public class FollowingSiblingIterator extends InternalAxisIteratorBase
    {
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                this._startNode = node;
                this._currentNode = SAX2DTM2.this.makeNodeIdentity(node);
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            this._currentNode = ((this._currentNode == -1) ? -1 : SAX2DTM2.this._nextsib2(this._currentNode));
            return this.returnNode(SAX2DTM2.this.makeNodeHandle(this._currentNode));
        }
    }
    
    public final class TypedFollowingSiblingIterator extends FollowingSiblingIterator
    {
        private final int _nodeType;
        
        public TypedFollowingSiblingIterator(final int type) {
            this._nodeType = type;
        }
        
        @Override
        public int next() {
            if (this._currentNode == -1) {
                return -1;
            }
            int node = this._currentNode;
            final int nodeType = this._nodeType;
            if (nodeType != 1) {
                while ((node = SAX2DTM2.this._nextsib2(node)) != -1 && SAX2DTM2.this._exptype2(node) != nodeType) {}
            }
            else {
                while ((node = SAX2DTM2.this._nextsib2(node)) != -1 && SAX2DTM2.this._exptype2(node) < 14) {}
            }
            return ((this._currentNode = node) == -1) ? -1 : this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
    }
    
    public final class AttributeIterator extends InternalAxisIteratorBase
    {
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                this._startNode = node;
                this._currentNode = SAX2DTM2.this.getFirstAttributeIdentity(SAX2DTM2.this.makeNodeIdentity(node));
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            final int node = this._currentNode;
            if (node != -1) {
                this._currentNode = SAX2DTM2.this.getNextAttributeIdentity(node);
                return this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
            }
            return -1;
        }
    }
    
    public final class TypedAttributeIterator extends InternalAxisIteratorBase
    {
        private final int _nodeType;
        
        public TypedAttributeIterator(final int nodeType) {
            this._nodeType = nodeType;
        }
        
        @Override
        public DTMAxisIterator setStartNode(final int node) {
            if (this._isRestartable) {
                this._startNode = node;
                this._currentNode = SAX2DTM2.this.getTypedAttribute(node, this._nodeType);
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            final int node = this._currentNode;
            this._currentNode = -1;
            return this.returnNode(node);
        }
    }
    
    public class PrecedingSiblingIterator extends InternalAxisIteratorBase
    {
        protected int _startNodeID;
        
        @Override
        public boolean isReverse() {
            return true;
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (!this._isRestartable) {
                return this;
            }
            this._startNode = node;
            final int nodeIdentity = SAX2DTM2.this.makeNodeIdentity(node);
            this._startNodeID = nodeIdentity;
            node = nodeIdentity;
            if (node == -1) {
                this._currentNode = node;
                return this.resetPosition();
            }
            final int type = SAX2DTM2.this._type2(node);
            if (2 == type || 13 == type) {
                this._currentNode = node;
            }
            else {
                this._currentNode = SAX2DTM2.this._parent2(node);
                if (-1 != this._currentNode) {
                    this._currentNode = SAX2DTM2.this._firstch2(this._currentNode);
                }
                else {
                    this._currentNode = node;
                }
            }
            return this.resetPosition();
        }
        
        @Override
        public int next() {
            if (this._currentNode == this._startNodeID || this._currentNode == -1) {
                return -1;
            }
            final int node = this._currentNode;
            this._currentNode = SAX2DTM2.this._nextsib2(node);
            return this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
    }
    
    public final class TypedPrecedingSiblingIterator extends PrecedingSiblingIterator
    {
        private final int _nodeType;
        
        public TypedPrecedingSiblingIterator(final int type) {
            this._nodeType = type;
        }
        
        @Override
        public int next() {
            int node = this._currentNode;
            final int nodeType = this._nodeType;
            final int startNodeID = this._startNodeID;
            if (nodeType != 1) {
                while (node != -1 && node != startNodeID && SAX2DTM2.this._exptype2(node) != nodeType) {
                    node = SAX2DTM2.this._nextsib2(node);
                }
            }
            else {
                while (node != -1 && node != startNodeID && SAX2DTM2.this._exptype2(node) < 14) {
                    node = SAX2DTM2.this._nextsib2(node);
                }
            }
            if (node == -1 || node == startNodeID) {
                return this._currentNode = -1;
            }
            this._currentNode = SAX2DTM2.this._nextsib2(node);
            return this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
        
        @Override
        public int getLast() {
            if (this._last != -1) {
                return this._last;
            }
            this.setMark();
            int node = this._currentNode;
            final int nodeType = this._nodeType;
            final int startNodeID = this._startNodeID;
            int last = 0;
            if (nodeType != 1) {
                while (node != -1 && node != startNodeID) {
                    if (SAX2DTM2.this._exptype2(node) == nodeType) {
                        ++last;
                    }
                    node = SAX2DTM2.this._nextsib2(node);
                }
            }
            else {
                while (node != -1 && node != startNodeID) {
                    if (SAX2DTM2.this._exptype2(node) >= 14) {
                        ++last;
                    }
                    node = SAX2DTM2.this._nextsib2(node);
                }
            }
            this.gotoMark();
            return this._last = last;
        }
    }
    
    public class PrecedingIterator extends InternalAxisIteratorBase
    {
        private final int _maxAncestors = 8;
        protected int[] _stack;
        protected int _sp;
        protected int _oldsp;
        protected int _markedsp;
        protected int _markedNode;
        protected int _markedDescendant;
        
        public PrecedingIterator() {
            this._stack = new int[8];
        }
        
        @Override
        public boolean isReverse() {
            return true;
        }
        
        @Override
        public DTMAxisIterator cloneIterator() {
            this._isRestartable = false;
            try {
                final PrecedingIterator clone = (PrecedingIterator)super.clone();
                final int[] stackCopy = new int[this._stack.length];
                System.arraycopy(this._stack, 0, stackCopy, 0, this._stack.length);
                clone._stack = stackCopy;
                return clone;
            }
            catch (final CloneNotSupportedException e) {
                throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
            }
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                node = SAX2DTM2.this.makeNodeIdentity(node);
                if (SAX2DTM2.this._type2(node) == 2) {
                    node = SAX2DTM2.this._parent2(node);
                }
                this._startNode = node;
                int index;
                this._stack[index = 0] = node;
                int parent = node;
                while ((parent = SAX2DTM2.this._parent2(parent)) != -1) {
                    if (++index == this._stack.length) {
                        final int[] stack = new int[index * 2];
                        System.arraycopy(this._stack, 0, stack, 0, index);
                        this._stack = stack;
                    }
                    this._stack[index] = parent;
                }
                if (index > 0) {
                    --index;
                }
                this._currentNode = this._stack[index];
                final int n = index;
                this._sp = n;
                this._oldsp = n;
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            ++this._currentNode;
            while (this._sp >= 0) {
                if (this._currentNode < this._stack[this._sp]) {
                    final int type = SAX2DTM2.this._type2(this._currentNode);
                    if (type != 2 && type != 13) {
                        return this.returnNode(SAX2DTM2.this.makeNodeHandle(this._currentNode));
                    }
                }
                else {
                    --this._sp;
                }
                ++this._currentNode;
            }
            return -1;
        }
        
        @Override
        public DTMAxisIterator reset() {
            this._sp = this._oldsp;
            return this.resetPosition();
        }
        
        @Override
        public void setMark() {
            this._markedsp = this._sp;
            this._markedNode = this._currentNode;
            this._markedDescendant = this._stack[0];
        }
        
        @Override
        public void gotoMark() {
            this._sp = this._markedsp;
            this._currentNode = this._markedNode;
        }
    }
    
    public final class TypedPrecedingIterator extends PrecedingIterator
    {
        private final int _nodeType;
        
        public TypedPrecedingIterator(final int type) {
            this._nodeType = type;
        }
        
        @Override
        public int next() {
            int node = this._currentNode;
            final int nodeType = this._nodeType;
            if (nodeType >= 14) {
                while (true) {
                    ++node;
                    if (this._sp < 0) {
                        node = -1;
                        break;
                    }
                    if (node >= this._stack[this._sp]) {
                        if (--this._sp < 0) {
                            node = -1;
                            break;
                        }
                        continue;
                    }
                    else {
                        if (SAX2DTM2.this._exptype2(node) == nodeType) {
                            break;
                        }
                        continue;
                    }
                }
            }
            else {
                while (true) {
                    ++node;
                    if (this._sp < 0) {
                        node = -1;
                        break;
                    }
                    if (node >= this._stack[this._sp]) {
                        if (--this._sp < 0) {
                            node = -1;
                            break;
                        }
                        continue;
                    }
                    else {
                        final int expType = SAX2DTM2.this._exptype2(node);
                        if (expType < 14) {
                            if (expType == nodeType) {
                                break;
                            }
                            continue;
                        }
                        else {
                            if (SAX2DTM2.this.m_extendedTypes[expType].getNodeType() == nodeType) {
                                break;
                            }
                            continue;
                        }
                    }
                }
            }
            this._currentNode = node;
            return (node == -1) ? -1 : this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
    }
    
    public class FollowingIterator extends InternalAxisIteratorBase
    {
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                this._startNode = node;
                node = SAX2DTM2.this.makeNodeIdentity(node);
                final int type = SAX2DTM2.this._type2(node);
                if (2 == type || 13 == type) {
                    node = SAX2DTM2.this._parent2(node);
                    final int first = SAX2DTM2.this._firstch2(node);
                    if (-1 != first) {
                        this._currentNode = SAX2DTM2.this.makeNodeHandle(first);
                        return this.resetPosition();
                    }
                }
                int first;
                do {
                    first = SAX2DTM2.this._nextsib2(node);
                    if (-1 == first) {
                        node = SAX2DTM2.this._parent2(node);
                    }
                } while (-1 == first && -1 != node);
                this._currentNode = SAX2DTM2.this.makeNodeHandle(first);
                return this.resetPosition();
            }
            return this;
        }
        
        @Override
        public int next() {
            final int node = this._currentNode;
            int current = SAX2DTM2.this.makeNodeIdentity(node);
            while (true) {
                ++current;
                final int type = SAX2DTM2.this._type2(current);
                if (-1 == type) {
                    this._currentNode = -1;
                    return this.returnNode(node);
                }
                if (2 == type) {
                    continue;
                }
                if (13 == type) {
                    continue;
                }
                this._currentNode = SAX2DTM2.this.makeNodeHandle(current);
                return this.returnNode(node);
            }
        }
    }
    
    public final class TypedFollowingIterator extends FollowingIterator
    {
        private final int _nodeType;
        
        public TypedFollowingIterator(final int type) {
            this._nodeType = type;
        }
        
        @Override
        public int next() {
            final int nodeType = this._nodeType;
            int currentNodeID = SAX2DTM2.this.makeNodeIdentity(this._currentNode);
            int node;
            if (nodeType >= 14) {
                do {
                    int current;
                    node = (current = currentNodeID);
                    int type;
                    do {
                        ++current;
                        type = SAX2DTM2.this._type2(current);
                    } while (type != -1 && (2 == type || 13 == type));
                    currentNodeID = ((type != -1) ? current : -1);
                    if (node != -1) {
                        continue;
                    }
                    break;
                } while (SAX2DTM2.this._exptype2(node) != nodeType);
            }
            else {
                do {
                    int current;
                    node = (current = currentNodeID);
                    int type;
                    do {
                        ++current;
                        type = SAX2DTM2.this._type2(current);
                    } while (type != -1 && (2 == type || 13 == type));
                    currentNodeID = ((type != -1) ? current : -1);
                } while (node != -1 && SAX2DTM2.this._exptype2(node) != nodeType && SAX2DTM2.this._type2(node) != nodeType);
            }
            this._currentNode = SAX2DTM2.this.makeNodeHandle(currentNodeID);
            return (node == -1) ? -1 : this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
    }
    
    public class AncestorIterator extends InternalAxisIteratorBase
    {
        private static final int m_blocksize = 32;
        int[] m_ancestors;
        int m_size;
        int m_ancestorsPos;
        int m_markedPos;
        int m_realStartNode;
        
        public AncestorIterator() {
            this.m_ancestors = new int[32];
            this.m_size = 0;
        }
        
        @Override
        public int getStartNode() {
            return this.m_realStartNode;
        }
        
        @Override
        public final boolean isReverse() {
            return true;
        }
        
        @Override
        public DTMAxisIterator cloneIterator() {
            this._isRestartable = false;
            try {
                final AncestorIterator clone = (AncestorIterator)super.clone();
                clone._startNode = this._startNode;
                return clone;
            }
            catch (final CloneNotSupportedException e) {
                throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
            }
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            this.m_realStartNode = node;
            if (!this._isRestartable) {
                return this;
            }
            int nodeID = SAX2DTM2.this.makeNodeIdentity(node);
            this.m_size = 0;
            if (nodeID == -1) {
                this._currentNode = -1;
                this.m_ancestorsPos = 0;
                return this;
            }
            if (!this._includeSelf) {
                nodeID = SAX2DTM2.this._parent2(nodeID);
                node = SAX2DTM2.this.makeNodeHandle(nodeID);
            }
            this._startNode = node;
            while (nodeID != -1) {
                if (this.m_size >= this.m_ancestors.length) {
                    final int[] newAncestors = new int[this.m_size * 2];
                    System.arraycopy(this.m_ancestors, 0, newAncestors, 0, this.m_ancestors.length);
                    this.m_ancestors = newAncestors;
                }
                this.m_ancestors[this.m_size++] = node;
                nodeID = SAX2DTM2.this._parent2(nodeID);
                node = SAX2DTM2.this.makeNodeHandle(nodeID);
            }
            this.m_ancestorsPos = this.m_size - 1;
            this._currentNode = ((this.m_ancestorsPos >= 0) ? this.m_ancestors[this.m_ancestorsPos] : -1);
            return this.resetPosition();
        }
        
        @Override
        public DTMAxisIterator reset() {
            this.m_ancestorsPos = this.m_size - 1;
            this._currentNode = ((this.m_ancestorsPos >= 0) ? this.m_ancestors[this.m_ancestorsPos] : -1);
            return this.resetPosition();
        }
        
        @Override
        public int next() {
            final int next = this._currentNode;
            final int ancestorsPos = this.m_ancestorsPos - 1;
            this.m_ancestorsPos = ancestorsPos;
            final int pos = ancestorsPos;
            this._currentNode = ((pos >= 0) ? this.m_ancestors[this.m_ancestorsPos] : -1);
            return this.returnNode(next);
        }
        
        @Override
        public void setMark() {
            this.m_markedPos = this.m_ancestorsPos;
        }
        
        @Override
        public void gotoMark() {
            this.m_ancestorsPos = this.m_markedPos;
            this._currentNode = ((this.m_ancestorsPos >= 0) ? this.m_ancestors[this.m_ancestorsPos] : -1);
        }
    }
    
    public final class TypedAncestorIterator extends AncestorIterator
    {
        private final int _nodeType;
        
        public TypedAncestorIterator(final int type) {
            this._nodeType = type;
        }
        
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            this.m_realStartNode = node;
            if (!this._isRestartable) {
                return this;
            }
            int nodeID = SAX2DTM2.this.makeNodeIdentity(node);
            this.m_size = 0;
            if (nodeID == -1) {
                this._currentNode = -1;
                this.m_ancestorsPos = 0;
                return this;
            }
            final int nodeType = this._nodeType;
            if (!this._includeSelf) {
                nodeID = SAX2DTM2.this._parent2(nodeID);
                node = SAX2DTM2.this.makeNodeHandle(nodeID);
            }
            this._startNode = node;
            if (nodeType >= 14) {
                while (nodeID != -1) {
                    final int eType = SAX2DTM2.this._exptype2(nodeID);
                    if (eType == nodeType) {
                        if (this.m_size >= this.m_ancestors.length) {
                            final int[] newAncestors = new int[this.m_size * 2];
                            System.arraycopy(this.m_ancestors, 0, newAncestors, 0, this.m_ancestors.length);
                            this.m_ancestors = newAncestors;
                        }
                        this.m_ancestors[this.m_size++] = SAX2DTM2.this.makeNodeHandle(nodeID);
                    }
                    nodeID = SAX2DTM2.this._parent2(nodeID);
                }
            }
            else {
                while (nodeID != -1) {
                    final int eType = SAX2DTM2.this._exptype2(nodeID);
                    if ((eType < 14 && eType == nodeType) || (eType >= 14 && SAX2DTM2.this.m_extendedTypes[eType].getNodeType() == nodeType)) {
                        if (this.m_size >= this.m_ancestors.length) {
                            final int[] newAncestors = new int[this.m_size * 2];
                            System.arraycopy(this.m_ancestors, 0, newAncestors, 0, this.m_ancestors.length);
                            this.m_ancestors = newAncestors;
                        }
                        this.m_ancestors[this.m_size++] = SAX2DTM2.this.makeNodeHandle(nodeID);
                    }
                    nodeID = SAX2DTM2.this._parent2(nodeID);
                }
            }
            this.m_ancestorsPos = this.m_size - 1;
            this._currentNode = ((this.m_ancestorsPos >= 0) ? this.m_ancestors[this.m_ancestorsPos] : -1);
            return this.resetPosition();
        }
        
        @Override
        public int getNodeByPosition(final int position) {
            if (position > 0 && position <= this.m_size) {
                return this.m_ancestors[position - 1];
            }
            return -1;
        }
        
        @Override
        public int getLast() {
            return this.m_size;
        }
    }
    
    public class DescendantIterator extends InternalAxisIteratorBase
    {
        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAX2DTM2.this.getDocument();
            }
            if (this._isRestartable) {
                node = SAX2DTM2.this.makeNodeIdentity(node);
                this._startNode = node;
                if (this._includeSelf) {
                    --node;
                }
                this._currentNode = node;
                return this.resetPosition();
            }
            return this;
        }
        
        protected final boolean isDescendant(final int identity) {
            return SAX2DTM2.this._parent2(identity) >= this._startNode || this._startNode == identity;
        }
        
        @Override
        public int next() {
            final int startNode = this._startNode;
            if (startNode == -1) {
                return -1;
            }
            if (this._includeSelf && this._currentNode + 1 == startNode) {
                return this.returnNode(SAX2DTM2.this.makeNodeHandle(++this._currentNode));
            }
            int node = this._currentNode;
            if (startNode == 0) {
                int eType;
                int type;
                do {
                    ++node;
                    eType = SAX2DTM2.this._exptype2(node);
                    if (-1 == eType) {
                        return this._currentNode = -1;
                    }
                } while (eType == 3 || (type = SAX2DTM2.this.m_extendedTypes[eType].getNodeType()) == 2 || type == 13);
            }
            else {
                int type;
                do {
                    ++node;
                    type = SAX2DTM2.this._type2(node);
                    if (-1 == type || !this.isDescendant(node)) {
                        return this._currentNode = -1;
                    }
                } while (2 == type || 3 == type || 13 == type);
            }
            this._currentNode = node;
            return this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
        
        @Override
        public DTMAxisIterator reset() {
            final boolean temp = this._isRestartable;
            this._isRestartable = true;
            this.setStartNode(SAX2DTM2.this.makeNodeHandle(this._startNode));
            this._isRestartable = temp;
            return this;
        }
    }
    
    public final class TypedDescendantIterator extends DescendantIterator
    {
        private final int _nodeType;
        
        public TypedDescendantIterator(final int nodeType) {
            this._nodeType = nodeType;
        }
        
        @Override
        public int next() {
            final int startNode = this._startNode;
            if (this._startNode == -1) {
                return -1;
            }
            int node = this._currentNode;
            final int nodeType = this._nodeType;
            if (nodeType != 1) {
                int expType;
                do {
                    ++node;
                    expType = SAX2DTM2.this._exptype2(node);
                    if (-1 == expType || (SAX2DTM2.this._parent2(node) < startNode && startNode != node)) {
                        return this._currentNode = -1;
                    }
                } while (expType != nodeType);
            }
            else if (startNode == 0) {
                int expType;
                do {
                    ++node;
                    expType = SAX2DTM2.this._exptype2(node);
                    if (-1 == expType) {
                        return this._currentNode = -1;
                    }
                } while (expType < 14 || SAX2DTM2.this.m_extendedTypes[expType].getNodeType() != 1);
            }
            else {
                int expType;
                do {
                    ++node;
                    expType = SAX2DTM2.this._exptype2(node);
                    if (-1 == expType || (SAX2DTM2.this._parent2(node) < startNode && startNode != node)) {
                        return this._currentNode = -1;
                    }
                } while (expType < 14 || SAX2DTM2.this.m_extendedTypes[expType].getNodeType() != 1);
            }
            this._currentNode = node;
            return this.returnNode(SAX2DTM2.this.makeNodeHandle(node));
        }
    }
    
    public final class TypedSingletonIterator extends SingletonIterator
    {
        private final int _nodeType;
        
        public TypedSingletonIterator(final int nodeType) {
            this._nodeType = nodeType;
        }
        
        @Override
        public int next() {
            final int result = this._currentNode;
            if (result == -1) {
                return -1;
            }
            this._currentNode = -1;
            if (this._nodeType >= 14) {
                if (SAX2DTM2.this._exptype2(SAX2DTM2.this.makeNodeIdentity(result)) == this._nodeType) {
                    return this.returnNode(result);
                }
            }
            else if (SAX2DTM2.this._type2(SAX2DTM2.this.makeNodeIdentity(result)) == this._nodeType) {
                return this.returnNode(result);
            }
            return -1;
        }
    }
}
