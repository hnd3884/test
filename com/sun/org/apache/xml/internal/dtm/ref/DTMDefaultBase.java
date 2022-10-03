package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.XMLString;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStream;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.utils.BoolStack;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import java.util.Vector;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.dtm.DTM;

public abstract class DTMDefaultBase implements DTM
{
    static final boolean JJK_DEBUG = false;
    public static final int ROOTNODE = 0;
    protected int m_size;
    protected SuballocatedIntVector m_exptype;
    protected SuballocatedIntVector m_firstch;
    protected SuballocatedIntVector m_nextsib;
    protected SuballocatedIntVector m_prevsib;
    protected SuballocatedIntVector m_parent;
    protected Vector m_namespaceDeclSets;
    protected SuballocatedIntVector m_namespaceDeclSetElements;
    protected int[][][] m_elemIndexes;
    public static final int DEFAULT_BLOCKSIZE = 512;
    public static final int DEFAULT_NUMBLOCKS = 32;
    public static final int DEFAULT_NUMBLOCKS_SMALL = 4;
    protected static final int NOTPROCESSED = -2;
    public DTMManager m_mgr;
    protected DTMManagerDefault m_mgrDefault;
    protected SuballocatedIntVector m_dtmIdent;
    protected String m_documentBaseURI;
    protected DTMWSFilter m_wsfilter;
    protected boolean m_shouldStripWS;
    protected BoolStack m_shouldStripWhitespaceStack;
    protected XMLStringFactory m_xstrf;
    protected ExpandedNameTable m_expandedNameTable;
    protected boolean m_indexing;
    protected DTMAxisTraverser[] m_traversers;
    private Vector m_namespaceLists;
    
    public DTMDefaultBase(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing) {
        this(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, 512, true, false);
    }
    
    public DTMDefaultBase(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing, final int blocksize, final boolean usePrevsib, final boolean newNameTable) {
        this.m_size = 0;
        this.m_namespaceDeclSets = null;
        this.m_namespaceDeclSetElements = null;
        this.m_mgrDefault = null;
        this.m_shouldStripWS = false;
        this.m_namespaceLists = null;
        int numblocks;
        if (blocksize <= 64) {
            numblocks = 4;
            this.m_dtmIdent = new SuballocatedIntVector(4, 1);
        }
        else {
            numblocks = 32;
            this.m_dtmIdent = new SuballocatedIntVector(32);
        }
        this.m_exptype = new SuballocatedIntVector(blocksize, numblocks);
        this.m_firstch = new SuballocatedIntVector(blocksize, numblocks);
        this.m_nextsib = new SuballocatedIntVector(blocksize, numblocks);
        this.m_parent = new SuballocatedIntVector(blocksize, numblocks);
        if (usePrevsib) {
            this.m_prevsib = new SuballocatedIntVector(blocksize, numblocks);
        }
        this.m_mgr = mgr;
        if (mgr instanceof DTMManagerDefault) {
            this.m_mgrDefault = (DTMManagerDefault)mgr;
        }
        this.m_documentBaseURI = ((null != source) ? source.getSystemId() : null);
        this.m_dtmIdent.setElementAt(dtmIdentity, 0);
        this.m_wsfilter = whiteSpaceFilter;
        this.m_xstrf = xstringfactory;
        this.m_indexing = doIndexing;
        if (doIndexing) {
            this.m_expandedNameTable = new ExpandedNameTable();
        }
        else {
            this.m_expandedNameTable = this.m_mgrDefault.getExpandedNameTable(this);
        }
        if (null != whiteSpaceFilter) {
            this.m_shouldStripWhitespaceStack = new BoolStack();
            this.pushShouldStripWhitespace(false);
        }
    }
    
    protected void ensureSizeOfIndex(final int namespaceID, final int LocalNameID) {
        if (null == this.m_elemIndexes) {
            this.m_elemIndexes = new int[namespaceID + 20][][];
        }
        else if (this.m_elemIndexes.length <= namespaceID) {
            final int[][][] indexes = this.m_elemIndexes;
            System.arraycopy(indexes, 0, this.m_elemIndexes = new int[namespaceID + 20][][], 0, indexes.length);
        }
        int[][] localNameIndex = this.m_elemIndexes[namespaceID];
        if (null == localNameIndex) {
            localNameIndex = new int[LocalNameID + 100][];
            this.m_elemIndexes[namespaceID] = localNameIndex;
        }
        else if (localNameIndex.length <= LocalNameID) {
            final int[][] indexes2 = localNameIndex;
            localNameIndex = new int[LocalNameID + 100][];
            System.arraycopy(indexes2, 0, localNameIndex, 0, indexes2.length);
            this.m_elemIndexes[namespaceID] = localNameIndex;
        }
        int[] elemHandles = localNameIndex[LocalNameID];
        if (null == elemHandles) {
            elemHandles = new int[128];
            (localNameIndex[LocalNameID] = elemHandles)[0] = 1;
        }
        else if (elemHandles.length <= elemHandles[0] + 1) {
            final int[] indexes3 = elemHandles;
            elemHandles = new int[elemHandles[0] + 1024];
            System.arraycopy(indexes3, 0, elemHandles, 0, indexes3.length);
            localNameIndex[LocalNameID] = elemHandles;
        }
    }
    
    protected void indexNode(final int expandedTypeID, final int identity) {
        final ExpandedNameTable ent = this.m_expandedNameTable;
        final short type = ent.getType(expandedTypeID);
        if (1 == type) {
            final int namespaceID = ent.getNamespaceID(expandedTypeID);
            final int localNameID = ent.getLocalNameID(expandedTypeID);
            this.ensureSizeOfIndex(namespaceID, localNameID);
            final int[] index = this.m_elemIndexes[namespaceID][localNameID];
            index[index[0]] = identity;
            final int[] array = index;
            final int n = 0;
            ++array[n];
        }
    }
    
    protected int findGTE(final int[] list, final int start, final int len, final int value) {
        int low = start;
        final int end;
        int high = end = start + (len - 1);
        while (low <= high) {
            final int mid = low + high >>> 1;
            final int c = list[mid];
            if (c > value) {
                high = mid - 1;
            }
            else {
                if (c >= value) {
                    return mid;
                }
                low = mid + 1;
            }
        }
        return (low <= end && list[low] > value) ? low : -1;
    }
    
    int findElementFromIndex(final int nsIndex, final int lnIndex, final int firstPotential) {
        final int[][][] indexes = this.m_elemIndexes;
        if (null != indexes && nsIndex < indexes.length) {
            final int[][] lnIndexs = indexes[nsIndex];
            if (null != lnIndexs && lnIndex < lnIndexs.length) {
                final int[] elems = lnIndexs[lnIndex];
                if (null != elems) {
                    final int pos = this.findGTE(elems, 1, elems[0], firstPotential);
                    if (pos > -1) {
                        return elems[pos];
                    }
                }
            }
        }
        return -2;
    }
    
    protected abstract int getNextNodeIdentity(final int p0);
    
    protected abstract boolean nextNode();
    
    protected abstract int getNumberOfNodes();
    
    protected short _type(final int identity) {
        final int info = this._exptype(identity);
        if (-1 != info) {
            return this.m_expandedNameTable.getType(info);
        }
        return -1;
    }
    
    protected int _exptype(final int identity) {
        if (identity == -1) {
            return -1;
        }
        while (identity >= this.m_size) {
            if (!this.nextNode() && identity >= this.m_size) {
                return -1;
            }
        }
        return this.m_exptype.elementAt(identity);
    }
    
    protected int _level(int identity) {
        while (identity >= this.m_size) {
            final boolean isMore = this.nextNode();
            if (!isMore && identity >= this.m_size) {
                return -1;
            }
        }
        int i = 0;
        while (-1 != (identity = this._parent(identity))) {
            ++i;
        }
        return i;
    }
    
    protected int _firstch(final int identity) {
        int info = (identity >= this.m_size) ? -2 : this.m_firstch.elementAt(identity);
        while (info == -2) {
            final boolean isMore = this.nextNode();
            if (identity >= this.m_size && !isMore) {
                return -1;
            }
            info = this.m_firstch.elementAt(identity);
            if (info == -2 && !isMore) {
                return -1;
            }
        }
        return info;
    }
    
    protected int _nextsib(final int identity) {
        int info = (identity >= this.m_size) ? -2 : this.m_nextsib.elementAt(identity);
        while (info == -2) {
            final boolean isMore = this.nextNode();
            if (identity >= this.m_size && !isMore) {
                return -1;
            }
            info = this.m_nextsib.elementAt(identity);
            if (info == -2 && !isMore) {
                return -1;
            }
        }
        return info;
    }
    
    protected int _prevsib(final int identity) {
        if (identity < this.m_size) {
            return this.m_prevsib.elementAt(identity);
        }
        while (true) {
            final boolean isMore = this.nextNode();
            if (identity >= this.m_size && !isMore) {
                return -1;
            }
            if (identity < this.m_size) {
                return this.m_prevsib.elementAt(identity);
            }
        }
    }
    
    protected int _parent(final int identity) {
        if (identity < this.m_size) {
            return this.m_parent.elementAt(identity);
        }
        while (true) {
            final boolean isMore = this.nextNode();
            if (identity >= this.m_size && !isMore) {
                return -1;
            }
            if (identity < this.m_size) {
                return this.m_parent.elementAt(identity);
            }
        }
    }
    
    public void dumpDTM(OutputStream os) {
        try {
            if (os == null) {
                final File f = new File("DTMDump" + this.hashCode() + ".txt");
                System.err.println("Dumping... " + f.getAbsolutePath());
                os = new FileOutputStream(f);
            }
            final PrintStream ps = new PrintStream(os);
            while (this.nextNode()) {}
            final int nRecords = this.m_size;
            ps.println("Total nodes: " + nRecords);
            for (int index = 0; index < nRecords; ++index) {
                final int i = this.makeNodeHandle(index);
                ps.println("=========== index=" + index + " handle=" + i + " ===========");
                ps.println("NodeName: " + this.getNodeName(i));
                ps.println("NodeNameX: " + this.getNodeNameX(i));
                ps.println("LocalName: " + this.getLocalName(i));
                ps.println("NamespaceURI: " + this.getNamespaceURI(i));
                ps.println("Prefix: " + this.getPrefix(i));
                final int exTypeID = this._exptype(index);
                ps.println("Expanded Type ID: " + Integer.toHexString(exTypeID));
                final int type = this._type(index);
                String typestring = null;
                switch (type) {
                    case 2: {
                        typestring = "ATTRIBUTE_NODE";
                        break;
                    }
                    case 4: {
                        typestring = "CDATA_SECTION_NODE";
                        break;
                    }
                    case 8: {
                        typestring = "COMMENT_NODE";
                        break;
                    }
                    case 11: {
                        typestring = "DOCUMENT_FRAGMENT_NODE";
                        break;
                    }
                    case 9: {
                        typestring = "DOCUMENT_NODE";
                        break;
                    }
                    case 10: {
                        typestring = "DOCUMENT_NODE";
                        break;
                    }
                    case 1: {
                        typestring = "ELEMENT_NODE";
                        break;
                    }
                    case 6: {
                        typestring = "ENTITY_NODE";
                        break;
                    }
                    case 5: {
                        typestring = "ENTITY_REFERENCE_NODE";
                        break;
                    }
                    case 13: {
                        typestring = "NAMESPACE_NODE";
                        break;
                    }
                    case 12: {
                        typestring = "NOTATION_NODE";
                        break;
                    }
                    case -1: {
                        typestring = "NULL";
                        break;
                    }
                    case 7: {
                        typestring = "PROCESSING_INSTRUCTION_NODE";
                        break;
                    }
                    case 3: {
                        typestring = "TEXT_NODE";
                        break;
                    }
                    default: {
                        typestring = "Unknown!";
                        break;
                    }
                }
                ps.println("Type: " + typestring);
                final int firstChild = this._firstch(index);
                if (-1 == firstChild) {
                    ps.println("First child: DTM.NULL");
                }
                else if (-2 == firstChild) {
                    ps.println("First child: NOTPROCESSED");
                }
                else {
                    ps.println("First child: " + firstChild);
                }
                if (this.m_prevsib != null) {
                    final int prevSibling = this._prevsib(index);
                    if (-1 == prevSibling) {
                        ps.println("Prev sibling: DTM.NULL");
                    }
                    else if (-2 == prevSibling) {
                        ps.println("Prev sibling: NOTPROCESSED");
                    }
                    else {
                        ps.println("Prev sibling: " + prevSibling);
                    }
                }
                final int nextSibling = this._nextsib(index);
                if (-1 == nextSibling) {
                    ps.println("Next sibling: DTM.NULL");
                }
                else if (-2 == nextSibling) {
                    ps.println("Next sibling: NOTPROCESSED");
                }
                else {
                    ps.println("Next sibling: " + nextSibling);
                }
                final int parent = this._parent(index);
                if (-1 == parent) {
                    ps.println("Parent: DTM.NULL");
                }
                else if (-2 == parent) {
                    ps.println("Parent: NOTPROCESSED");
                }
                else {
                    ps.println("Parent: " + parent);
                }
                final int level = this._level(index);
                ps.println("Level: " + level);
                ps.println("Node Value: " + this.getNodeValue(i));
                ps.println("String Value: " + this.getStringValue(i));
            }
        }
        catch (final IOException ioe) {
            ioe.printStackTrace(System.err);
            throw new RuntimeException(ioe.getMessage());
        }
    }
    
    public String dumpNode(final int nodeHandle) {
        if (nodeHandle == -1) {
            return "[null]";
        }
        String typestring = null;
        switch (this.getNodeType(nodeHandle)) {
            case 2: {
                typestring = "ATTR";
                break;
            }
            case 4: {
                typestring = "CDATA";
                break;
            }
            case 8: {
                typestring = "COMMENT";
                break;
            }
            case 11: {
                typestring = "DOC_FRAG";
                break;
            }
            case 9: {
                typestring = "DOC";
                break;
            }
            case 10: {
                typestring = "DOC_TYPE";
                break;
            }
            case 1: {
                typestring = "ELEMENT";
                break;
            }
            case 6: {
                typestring = "ENTITY";
                break;
            }
            case 5: {
                typestring = "ENT_REF";
                break;
            }
            case 13: {
                typestring = "NAMESPACE";
                break;
            }
            case 12: {
                typestring = "NOTATION";
                break;
            }
            case -1: {
                typestring = "null";
                break;
            }
            case 7: {
                typestring = "PI";
                break;
            }
            case 3: {
                typestring = "TEXT";
                break;
            }
            default: {
                typestring = "Unknown!";
                break;
            }
        }
        return "[" + nodeHandle + ": " + typestring + "(0x" + Integer.toHexString(this.getExpandedTypeID(nodeHandle)) + ") " + this.getNodeNameX(nodeHandle) + " {" + this.getNamespaceURI(nodeHandle) + "}=\"" + this.getNodeValue(nodeHandle) + "\"]";
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) {
    }
    
    @Override
    public boolean hasChildNodes(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        final int firstChild = this._firstch(identity);
        return firstChild != -1;
    }
    
    public final int makeNodeHandle(final int nodeIdentity) {
        if (-1 == nodeIdentity) {
            return -1;
        }
        return this.m_dtmIdent.elementAt(nodeIdentity >>> 16) + (nodeIdentity & 0xFFFF);
    }
    
    public final int makeNodeIdentity(final int nodeHandle) {
        if (-1 == nodeHandle) {
            return -1;
        }
        if (this.m_mgrDefault == null) {
            final int whichDTMid = this.m_dtmIdent.indexOf(nodeHandle & 0xFFFF0000);
            return (whichDTMid == -1) ? -1 : ((whichDTMid << 16) + (nodeHandle & 0xFFFF));
        }
        final int whichDTMindex = nodeHandle >>> 16;
        if (this.m_mgrDefault.m_dtms[whichDTMindex] != this) {
            return -1;
        }
        return this.m_mgrDefault.m_dtm_offsets[whichDTMindex] | (nodeHandle & 0xFFFF);
    }
    
    @Override
    public int getFirstChild(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        final int firstChild = this._firstch(identity);
        return this.makeNodeHandle(firstChild);
    }
    
    public int getTypedFirstChild(final int nodeHandle, final int nodeType) {
        if (nodeType < 14) {
            for (int firstChild = this._firstch(this.makeNodeIdentity(nodeHandle)); firstChild != -1; firstChild = this._nextsib(firstChild)) {
                final int eType = this._exptype(firstChild);
                if (eType == nodeType || (eType >= 14 && this.m_expandedNameTable.getType(eType) == nodeType)) {
                    return this.makeNodeHandle(firstChild);
                }
            }
        }
        else {
            for (int firstChild = this._firstch(this.makeNodeIdentity(nodeHandle)); firstChild != -1; firstChild = this._nextsib(firstChild)) {
                if (this._exptype(firstChild) == nodeType) {
                    return this.makeNodeHandle(firstChild);
                }
            }
        }
        return -1;
    }
    
    @Override
    public int getLastChild(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        int child = this._firstch(identity);
        int lastChild = -1;
        while (child != -1) {
            lastChild = child;
            child = this._nextsib(child);
        }
        return this.makeNodeHandle(lastChild);
    }
    
    @Override
    public abstract int getAttributeNode(final int p0, final String p1, final String p2);
    
    @Override
    public int getFirstAttribute(final int nodeHandle) {
        final int nodeID = this.makeNodeIdentity(nodeHandle);
        return this.makeNodeHandle(this.getFirstAttributeIdentity(nodeID));
    }
    
    protected int getFirstAttributeIdentity(int identity) {
        int type = this._type(identity);
        if (1 == type) {
            while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                type = this._type(identity);
                if (type == 2) {
                    return identity;
                }
                if (13 != type) {
                    break;
                }
            }
        }
        return -1;
    }
    
    protected int getTypedAttribute(final int nodeHandle, final int attType) {
        int type = this.getNodeType(nodeHandle);
        if (1 == type) {
            int identity = this.makeNodeIdentity(nodeHandle);
            while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                type = this._type(identity);
                if (type == 2) {
                    if (this._exptype(identity) == attType) {
                        return this.makeNodeHandle(identity);
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
    public int getNextSibling(final int nodeHandle) {
        if (nodeHandle == -1) {
            return -1;
        }
        return this.makeNodeHandle(this._nextsib(this.makeNodeIdentity(nodeHandle)));
    }
    
    public int getTypedNextSibling(final int nodeHandle, final int nodeType) {
        if (nodeHandle == -1) {
            return -1;
        }
        int node = this.makeNodeIdentity(nodeHandle);
        int eType;
        while ((node = this._nextsib(node)) != -1 && (eType = this._exptype(node)) != nodeType && this.m_expandedNameTable.getType(eType) != nodeType) {}
        return (node == -1) ? -1 : this.makeNodeHandle(node);
    }
    
    @Override
    public int getPreviousSibling(final int nodeHandle) {
        if (nodeHandle == -1) {
            return -1;
        }
        if (this.m_prevsib != null) {
            return this.makeNodeHandle(this._prevsib(this.makeNodeIdentity(nodeHandle)));
        }
        final int nodeID = this.makeNodeIdentity(nodeHandle);
        final int parent = this._parent(nodeID);
        int node = this._firstch(parent);
        int result = -1;
        while (node != nodeID) {
            result = node;
            node = this._nextsib(node);
        }
        return this.makeNodeHandle(result);
    }
    
    @Override
    public int getNextAttribute(final int nodeHandle) {
        final int nodeID = this.makeNodeIdentity(nodeHandle);
        if (this._type(nodeID) == 2) {
            return this.makeNodeHandle(this.getNextAttributeIdentity(nodeID));
        }
        return -1;
    }
    
    protected int getNextAttributeIdentity(int identity) {
        while (-1 != (identity = this.getNextNodeIdentity(identity))) {
            final int type = this._type(identity);
            if (type == 2) {
                return identity;
            }
            if (type != 13) {
                break;
            }
        }
        return -1;
    }
    
    protected void declareNamespaceInContext(final int elementNodeIndex, final int namespaceNodeIndex) {
        SuballocatedIntVector nsList = null;
        if (this.m_namespaceDeclSets == null) {
            (this.m_namespaceDeclSetElements = new SuballocatedIntVector(32)).addElement(elementNodeIndex);
            this.m_namespaceDeclSets = new Vector();
            nsList = new SuballocatedIntVector(32);
            this.m_namespaceDeclSets.addElement(nsList);
        }
        else {
            final int last = this.m_namespaceDeclSetElements.size() - 1;
            if (last >= 0 && elementNodeIndex == this.m_namespaceDeclSetElements.elementAt(last)) {
                nsList = this.m_namespaceDeclSets.elementAt(last);
            }
        }
        if (nsList == null) {
            this.m_namespaceDeclSetElements.addElement(elementNodeIndex);
            final SuballocatedIntVector inherited = this.findNamespaceContext(this._parent(elementNodeIndex));
            if (inherited != null) {
                final int isize = inherited.size();
                nsList = new SuballocatedIntVector(Math.max(Math.min(isize + 16, 2048), 32));
                for (int i = 0; i < isize; ++i) {
                    nsList.addElement(inherited.elementAt(i));
                }
            }
            else {
                nsList = new SuballocatedIntVector(32);
            }
            this.m_namespaceDeclSets.addElement(nsList);
        }
        final int newEType = this._exptype(namespaceNodeIndex);
        for (int j = nsList.size() - 1; j >= 0; --j) {
            if (newEType == this.getExpandedTypeID(nsList.elementAt(j))) {
                nsList.setElementAt(this.makeNodeHandle(namespaceNodeIndex), j);
                return;
            }
        }
        nsList.addElement(this.makeNodeHandle(namespaceNodeIndex));
    }
    
    protected SuballocatedIntVector findNamespaceContext(final int elementNodeIndex) {
        if (null != this.m_namespaceDeclSetElements) {
            int wouldBeAt = this.findInSortedSuballocatedIntVector(this.m_namespaceDeclSetElements, elementNodeIndex);
            if (wouldBeAt >= 0) {
                return this.m_namespaceDeclSets.elementAt(wouldBeAt);
            }
            if (wouldBeAt == -1) {
                return null;
            }
            wouldBeAt = -1 - wouldBeAt;
            int candidate = this.m_namespaceDeclSetElements.elementAt(--wouldBeAt);
            int ancestor = this._parent(elementNodeIndex);
            if (wouldBeAt == 0 && candidate < ancestor) {
                final int rootHandle = this.getDocumentRoot(this.makeNodeHandle(elementNodeIndex));
                final int rootID = this.makeNodeIdentity(rootHandle);
                int uppermostNSCandidateID;
                if (this.getNodeType(rootHandle) == 9) {
                    final int ch = this._firstch(rootID);
                    uppermostNSCandidateID = ((ch != -1) ? ch : rootID);
                }
                else {
                    uppermostNSCandidateID = rootID;
                }
                if (candidate == uppermostNSCandidateID) {
                    return this.m_namespaceDeclSets.elementAt(wouldBeAt);
                }
            }
            while (wouldBeAt >= 0 && ancestor > 0) {
                if (candidate == ancestor) {
                    return this.m_namespaceDeclSets.elementAt(wouldBeAt);
                }
                if (candidate < ancestor) {
                    do {
                        ancestor = this._parent(ancestor);
                    } while (candidate < ancestor);
                }
                else {
                    if (wouldBeAt <= 0) {
                        break;
                    }
                    candidate = this.m_namespaceDeclSetElements.elementAt(--wouldBeAt);
                }
            }
        }
        return null;
    }
    
    protected int findInSortedSuballocatedIntVector(final SuballocatedIntVector vector, final int lookfor) {
        int i = 0;
        if (vector != null) {
            int first = 0;
            int last = vector.size() - 1;
            while (first <= last) {
                i = (first + last) / 2;
                final int test = lookfor - vector.elementAt(i);
                if (test == 0) {
                    return i;
                }
                if (test < 0) {
                    last = i - 1;
                }
                else {
                    first = i + 1;
                }
            }
            if (first > i) {
                i = first;
            }
        }
        return -1 - i;
    }
    
    @Override
    public int getFirstNamespaceNode(final int nodeHandle, final boolean inScope) {
        if (inScope) {
            final int identity = this.makeNodeIdentity(nodeHandle);
            if (this._type(identity) != 1) {
                return -1;
            }
            final SuballocatedIntVector nsContext = this.findNamespaceContext(identity);
            if (nsContext == null || nsContext.size() < 1) {
                return -1;
            }
            return nsContext.elementAt(0);
        }
        else {
            int identity = this.makeNodeIdentity(nodeHandle);
            if (this._type(identity) == 1) {
                while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                    final int type = this._type(identity);
                    if (type == 13) {
                        return this.makeNodeHandle(identity);
                    }
                    if (2 != type) {
                        break;
                    }
                }
                return -1;
            }
            return -1;
        }
    }
    
    @Override
    public int getNextNamespaceNode(final int baseHandle, final int nodeHandle, final boolean inScope) {
        if (!inScope) {
            int identity = this.makeNodeIdentity(nodeHandle);
            while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                final int type = this._type(identity);
                if (type == 13) {
                    return this.makeNodeHandle(identity);
                }
                if (type != 2) {
                    break;
                }
            }
            return -1;
        }
        final SuballocatedIntVector nsContext = this.findNamespaceContext(this.makeNodeIdentity(baseHandle));
        if (nsContext == null) {
            return -1;
        }
        final int i = 1 + nsContext.indexOf(nodeHandle);
        if (i <= 0 || i == nsContext.size()) {
            return -1;
        }
        return nsContext.elementAt(i);
    }
    
    @Override
    public int getParent(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        if (identity > 0) {
            return this.makeNodeHandle(this._parent(identity));
        }
        return -1;
    }
    
    @Override
    public int getDocument() {
        return this.m_dtmIdent.elementAt(0);
    }
    
    @Override
    public int getOwnerDocument(final int nodeHandle) {
        if (9 == this.getNodeType(nodeHandle)) {
            return -1;
        }
        return this.getDocumentRoot(nodeHandle);
    }
    
    @Override
    public int getDocumentRoot(final int nodeHandle) {
        return this.getManager().getDTM(nodeHandle).getDocument();
    }
    
    @Override
    public abstract XMLString getStringValue(final int p0);
    
    @Override
    public int getStringValueChunkCount(final int nodeHandle) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return 0;
    }
    
    @Override
    public char[] getStringValueChunk(final int nodeHandle, final int chunkIndex, final int[] startAndLen) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }
    
    @Override
    public int getExpandedTypeID(final int nodeHandle) {
        final int id = this.makeNodeIdentity(nodeHandle);
        if (id == -1) {
            return -1;
        }
        return this._exptype(id);
    }
    
    @Override
    public int getExpandedTypeID(final String namespace, final String localName, final int type) {
        final ExpandedNameTable ent = this.m_expandedNameTable;
        return ent.getExpandedTypeID(namespace, localName, type);
    }
    
    @Override
    public String getLocalNameFromExpandedNameID(final int expandedNameID) {
        return this.m_expandedNameTable.getLocalName(expandedNameID);
    }
    
    @Override
    public String getNamespaceFromExpandedNameID(final int expandedNameID) {
        return this.m_expandedNameTable.getNamespace(expandedNameID);
    }
    
    public int getNamespaceType(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        final int expandedNameID = this._exptype(identity);
        return this.m_expandedNameTable.getNamespaceID(expandedNameID);
    }
    
    @Override
    public abstract String getNodeName(final int p0);
    
    @Override
    public String getNodeNameX(final int nodeHandle) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }
    
    @Override
    public abstract String getLocalName(final int p0);
    
    @Override
    public abstract String getPrefix(final int p0);
    
    @Override
    public abstract String getNamespaceURI(final int p0);
    
    @Override
    public abstract String getNodeValue(final int p0);
    
    @Override
    public short getNodeType(final int nodeHandle) {
        if (nodeHandle == -1) {
            return -1;
        }
        return this.m_expandedNameTable.getType(this._exptype(this.makeNodeIdentity(nodeHandle)));
    }
    
    @Override
    public short getLevel(final int nodeHandle) {
        final int identity = this.makeNodeIdentity(nodeHandle);
        return (short)(this._level(identity) + 1);
    }
    
    public int getNodeIdent(final int nodeHandle) {
        return this.makeNodeIdentity(nodeHandle);
    }
    
    public int getNodeHandle(final int nodeId) {
        return this.makeNodeHandle(nodeId);
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
        return this.m_documentBaseURI;
    }
    
    @Override
    public String getDocumentEncoding(final int nodeHandle) {
        return "UTF-8";
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
        return true;
    }
    
    @Override
    public abstract String getDocumentTypeDeclarationSystemIdentifier();
    
    @Override
    public abstract String getDocumentTypeDeclarationPublicIdentifier();
    
    @Override
    public abstract int getElementById(final String p0);
    
    @Override
    public abstract String getUnparsedEntityURI(final String p0);
    
    @Override
    public boolean supportsPreStripping() {
        return true;
    }
    
    @Override
    public boolean isNodeAfter(final int nodeHandle1, final int nodeHandle2) {
        final int index1 = this.makeNodeIdentity(nodeHandle1);
        final int index2 = this.makeNodeIdentity(nodeHandle2);
        return index1 != -1 && index2 != -1 && index1 <= index2;
    }
    
    @Override
    public boolean isCharacterElementContentWhitespace(final int nodeHandle) {
        return false;
    }
    
    @Override
    public boolean isDocumentAllDeclarationsProcessed(final int documentHandle) {
        return true;
    }
    
    @Override
    public abstract boolean isAttributeSpecified(final int p0);
    
    @Override
    public abstract void dispatchCharactersEvents(final int p0, final ContentHandler p1, final boolean p2) throws SAXException;
    
    @Override
    public abstract void dispatchToEvents(final int p0, final ContentHandler p1) throws SAXException;
    
    @Override
    public Node getNode(final int nodeHandle) {
        return new DTMNodeProxy(this, nodeHandle);
    }
    
    @Override
    public void appendChild(final int newChild, final boolean clone, final boolean cloneDepth) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    }
    
    @Override
    public void appendTextChild(final String str) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    }
    
    protected void error(final String msg) {
        throw new DTMException(msg);
    }
    
    protected boolean getShouldStripWhitespace() {
        return this.m_shouldStripWS;
    }
    
    protected void pushShouldStripWhitespace(final boolean shouldStrip) {
        this.m_shouldStripWS = shouldStrip;
        if (null != this.m_shouldStripWhitespaceStack) {
            this.m_shouldStripWhitespaceStack.push(shouldStrip);
        }
    }
    
    protected void popShouldStripWhitespace() {
        if (null != this.m_shouldStripWhitespaceStack) {
            this.m_shouldStripWS = this.m_shouldStripWhitespaceStack.popAndTop();
        }
    }
    
    protected void setShouldStripWhitespace(final boolean shouldStrip) {
        this.m_shouldStripWS = shouldStrip;
        if (null != this.m_shouldStripWhitespaceStack) {
            this.m_shouldStripWhitespaceStack.setTop(shouldStrip);
        }
    }
    
    @Override
    public void documentRegistration() {
    }
    
    @Override
    public void documentRelease() {
    }
    
    @Override
    public void migrateTo(final DTMManager mgr) {
        this.m_mgr = mgr;
        if (mgr instanceof DTMManagerDefault) {
            this.m_mgrDefault = (DTMManagerDefault)mgr;
        }
    }
    
    public DTMManager getManager() {
        return this.m_mgr;
    }
    
    public SuballocatedIntVector getDTMIDs() {
        if (this.m_mgr == null) {
            return null;
        }
        return this.m_dtmIdent;
    }
}
