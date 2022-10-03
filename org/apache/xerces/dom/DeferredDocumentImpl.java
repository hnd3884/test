package org.apache.xerces.dom;

import java.util.Hashtable;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;
import java.util.ArrayList;

public class DeferredDocumentImpl extends DocumentImpl implements DeferredNode
{
    static final long serialVersionUID = 5186323580749626857L;
    private static final boolean DEBUG_PRINT_REF_COUNTS = false;
    private static final boolean DEBUG_PRINT_TABLES = false;
    private static final boolean DEBUG_IDS = false;
    protected static final int CHUNK_SHIFT = 11;
    protected static final int CHUNK_SIZE = 2048;
    protected static final int CHUNK_MASK = 2047;
    protected static final int INITIAL_CHUNK_COUNT = 32;
    protected transient int fNodeCount;
    protected transient int[][] fNodeType;
    protected transient Object[][] fNodeName;
    protected transient Object[][] fNodeValue;
    protected transient int[][] fNodeParent;
    protected transient int[][] fNodeLastChild;
    protected transient int[][] fNodePrevSib;
    protected transient Object[][] fNodeURI;
    protected transient int[][] fNodeExtra;
    protected transient int fIdCount;
    protected transient String[] fIdName;
    protected transient int[] fIdElement;
    protected boolean fNamespacesEnabled;
    private final transient StringBuffer fBufferStr;
    private final transient ArrayList fStrChunks;
    private static final int[] INIT_ARRAY;
    
    public DeferredDocumentImpl() {
        this(false);
    }
    
    public DeferredDocumentImpl(final boolean b) {
        this(b, false);
    }
    
    public DeferredDocumentImpl(final boolean fNamespacesEnabled, final boolean b) {
        super(b);
        this.fNodeCount = 0;
        this.fNamespacesEnabled = false;
        this.fBufferStr = new StringBuffer();
        this.fStrChunks = new ArrayList();
        this.needsSyncData(true);
        this.needsSyncChildren(true);
        this.fNamespacesEnabled = fNamespacesEnabled;
    }
    
    public DOMImplementation getImplementation() {
        return DeferredDOMImplementationImpl.getDOMImplementation();
    }
    
    boolean getNamespacesEnabled() {
        return this.fNamespacesEnabled;
    }
    
    void setNamespacesEnabled(final boolean fNamespacesEnabled) {
        this.fNamespacesEnabled = fNamespacesEnabled;
    }
    
    public int createDeferredDocument() {
        return this.createNode((short)9);
    }
    
    public int createDeferredDocumentType(final String s, final String s2, final String s3) {
        final int node = this.createNode((short)10);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        this.setChunkValue(this.fNodeName, s, n, n2);
        this.setChunkValue(this.fNodeValue, s2, n, n2);
        this.setChunkValue(this.fNodeURI, s3, n, n2);
        return node;
    }
    
    public void setInternalSubset(final int n, final String s) {
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        final int node = this.createNode((short)10);
        final int n4 = node >> 11;
        final int n5 = node & 0x7FF;
        this.setChunkIndex(this.fNodeExtra, node, n2, n3);
        this.setChunkValue(this.fNodeValue, s, n4, n5);
    }
    
    public int createDeferredNotation(final String s, final String s2, final String s3, final String s4) {
        final int node = this.createNode((short)12);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        final int node2 = this.createNode((short)12);
        final int n3 = node2 >> 11;
        final int n4 = node2 & 0x7FF;
        this.setChunkValue(this.fNodeName, s, n, n2);
        this.setChunkValue(this.fNodeValue, s2, n, n2);
        this.setChunkValue(this.fNodeURI, s3, n, n2);
        this.setChunkIndex(this.fNodeExtra, node2, n, n2);
        this.setChunkValue(this.fNodeName, s4, n3, n4);
        return node;
    }
    
    public int createDeferredEntity(final String s, final String s2, final String s3, final String s4, final String s5) {
        final int node = this.createNode((short)6);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        final int node2 = this.createNode((short)6);
        final int n3 = node2 >> 11;
        final int n4 = node2 & 0x7FF;
        this.setChunkValue(this.fNodeName, s, n, n2);
        this.setChunkValue(this.fNodeValue, s2, n, n2);
        this.setChunkValue(this.fNodeURI, s3, n, n2);
        this.setChunkIndex(this.fNodeExtra, node2, n, n2);
        this.setChunkValue(this.fNodeName, s4, n3, n4);
        this.setChunkValue(this.fNodeValue, null, n3, n4);
        this.setChunkValue(this.fNodeURI, null, n3, n4);
        final int node3 = this.createNode((short)6);
        final int n5 = node3 >> 11;
        final int n6 = node3 & 0x7FF;
        this.setChunkIndex(this.fNodeExtra, node3, n3, n4);
        this.setChunkValue(this.fNodeName, s5, n5, n6);
        return node;
    }
    
    public String getDeferredEntityBaseURI(final int n) {
        if (n != -1) {
            return this.getNodeName(this.getNodeExtra(this.getNodeExtra(n, false), false), false);
        }
        return null;
    }
    
    public void setEntityInfo(final int n, final String s, final String s2) {
        final int nodeExtra = this.getNodeExtra(n, false);
        if (nodeExtra != -1) {
            final int n2 = nodeExtra >> 11;
            final int n3 = nodeExtra & 0x7FF;
            this.setChunkValue(this.fNodeValue, s, n2, n3);
            this.setChunkValue(this.fNodeURI, s2, n2, n3);
        }
    }
    
    public void setTypeInfo(final int n, final Object o) {
        this.setChunkValue(this.fNodeValue, o, n >> 11, n & 0x7FF);
    }
    
    public void setInputEncoding(final int n, final String s) {
        final int nodeExtra = this.getNodeExtra(this.getNodeExtra(n, false), false);
        this.setChunkValue(this.fNodeValue, s, nodeExtra >> 11, nodeExtra & 0x7FF);
    }
    
    public int createDeferredEntityReference(final String s, final String s2) {
        final int node = this.createNode((short)5);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        this.setChunkValue(this.fNodeName, s, n, n2);
        this.setChunkValue(this.fNodeValue, s2, n, n2);
        return node;
    }
    
    public int createDeferredElement(final String s, final String s2, final Object o) {
        final int node = this.createNode((short)1);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        this.setChunkValue(this.fNodeName, s2, n, n2);
        this.setChunkValue(this.fNodeURI, s, n, n2);
        this.setChunkValue(this.fNodeValue, o, n, n2);
        return node;
    }
    
    public int createDeferredElement(final String s) {
        return this.createDeferredElement(null, s);
    }
    
    public int createDeferredElement(final String s, final String s2) {
        final int node = this.createNode((short)1);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        this.setChunkValue(this.fNodeName, s2, n, n2);
        this.setChunkValue(this.fNodeURI, s, n, n2);
        return node;
    }
    
    public int setDeferredAttribute(final int n, final String s, final String s2, final String s3, final boolean b, final boolean b2, final Object o) {
        final int deferredAttribute = this.createDeferredAttribute(s, s2, s3, b);
        final int n2 = deferredAttribute >> 11;
        final int n3 = deferredAttribute & 0x7FF;
        this.setChunkIndex(this.fNodeParent, n, n2, n3);
        final int n4 = n >> 11;
        final int n5 = n & 0x7FF;
        final int chunkIndex = this.getChunkIndex(this.fNodeExtra, n4, n5);
        if (chunkIndex != 0) {
            this.setChunkIndex(this.fNodePrevSib, chunkIndex, n2, n3);
        }
        this.setChunkIndex(this.fNodeExtra, deferredAttribute, n4, n5);
        final int chunkIndex2 = this.getChunkIndex(this.fNodeExtra, n2, n3);
        if (b2) {
            this.setChunkIndex(this.fNodeExtra, chunkIndex2 | 0x200, n2, n3);
            this.putIdentifier(this.getChunkValue(this.fNodeValue, n2, n3), n);
        }
        if (o != null) {
            final int node = this.createNode((short)20);
            final int n6 = node >> 11;
            final int n7 = node & 0x7FF;
            this.setChunkIndex(this.fNodeLastChild, node, n2, n3);
            this.setChunkValue(this.fNodeValue, o, n6, n7);
        }
        return deferredAttribute;
    }
    
    public int setDeferredAttribute(final int n, final String s, final String s2, final String s3, final boolean b) {
        final int deferredAttribute = this.createDeferredAttribute(s, s2, s3, b);
        final int n2 = deferredAttribute >> 11;
        final int n3 = deferredAttribute & 0x7FF;
        this.setChunkIndex(this.fNodeParent, n, n2, n3);
        final int n4 = n >> 11;
        final int n5 = n & 0x7FF;
        final int chunkIndex = this.getChunkIndex(this.fNodeExtra, n4, n5);
        if (chunkIndex != 0) {
            this.setChunkIndex(this.fNodePrevSib, chunkIndex, n2, n3);
        }
        this.setChunkIndex(this.fNodeExtra, deferredAttribute, n4, n5);
        return deferredAttribute;
    }
    
    public int createDeferredAttribute(final String s, final String s2, final boolean b) {
        return this.createDeferredAttribute(s, null, s2, b);
    }
    
    public int createDeferredAttribute(final String s, final String s2, final String s3, final boolean b) {
        final int node = this.createNode((short)2);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        this.setChunkValue(this.fNodeName, s, n, n2);
        this.setChunkValue(this.fNodeURI, s2, n, n2);
        this.setChunkValue(this.fNodeValue, s3, n, n2);
        this.setChunkIndex(this.fNodeExtra, b ? 32 : 0, n, n2);
        return node;
    }
    
    public int createDeferredElementDefinition(final String s) {
        final int node = this.createNode((short)21);
        this.setChunkValue(this.fNodeName, s, node >> 11, node & 0x7FF);
        return node;
    }
    
    public int createDeferredTextNode(final String s, final boolean b) {
        final int node = this.createNode((short)3);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        this.setChunkValue(this.fNodeValue, s, n, n2);
        this.setChunkIndex(this.fNodeExtra, b ? 1 : 0, n, n2);
        return node;
    }
    
    public int createDeferredCDATASection(final String s) {
        final int node = this.createNode((short)4);
        this.setChunkValue(this.fNodeValue, s, node >> 11, node & 0x7FF);
        return node;
    }
    
    public int createDeferredProcessingInstruction(final String s, final String s2) {
        final int node = this.createNode((short)7);
        final int n = node >> 11;
        final int n2 = node & 0x7FF;
        this.setChunkValue(this.fNodeName, s, n, n2);
        this.setChunkValue(this.fNodeValue, s2, n, n2);
        return node;
    }
    
    public int createDeferredComment(final String s) {
        final int node = this.createNode((short)8);
        this.setChunkValue(this.fNodeValue, s, node >> 11, node & 0x7FF);
        return node;
    }
    
    public int cloneNode(final int n, final boolean b) {
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        final int n4 = this.fNodeType[n2][n3];
        final int node = this.createNode((short)n4);
        final int n5 = node >> 11;
        final int n6 = node & 0x7FF;
        this.setChunkValue(this.fNodeName, this.fNodeName[n2][n3], n5, n6);
        this.setChunkValue(this.fNodeValue, this.fNodeValue[n2][n3], n5, n6);
        this.setChunkValue(this.fNodeURI, this.fNodeURI[n2][n3], n5, n6);
        int cloneNode = this.fNodeExtra[n2][n3];
        if (cloneNode != -1) {
            if (n4 != 2 && n4 != 3) {
                cloneNode = this.cloneNode(cloneNode, false);
            }
            this.setChunkIndex(this.fNodeExtra, cloneNode, n5, n6);
        }
        if (b) {
            int n7 = -1;
            for (int i = this.getLastChild(n, false); i != -1; i = this.getRealPrevSibling(i, false)) {
                final int cloneNode2 = this.cloneNode(i, b);
                this.insertBefore(node, cloneNode2, n7);
                n7 = cloneNode2;
            }
        }
        return node;
    }
    
    public void appendChild(final int n, final int n2) {
        final int n3 = n >> 11;
        final int n4 = n & 0x7FF;
        final int n5 = n2 >> 11;
        final int n6 = n2 & 0x7FF;
        this.setChunkIndex(this.fNodeParent, n, n5, n6);
        this.setChunkIndex(this.fNodePrevSib, this.getChunkIndex(this.fNodeLastChild, n3, n4), n5, n6);
        this.setChunkIndex(this.fNodeLastChild, n2, n3, n4);
    }
    
    public int setAttributeNode(final int n, final int n2) {
        final int n3 = n >> 11;
        final int n4 = n & 0x7FF;
        final int n5 = n2 >> 11;
        final int n6 = n2 & 0x7FF;
        final String chunkValue = this.getChunkValue(this.fNodeName, n5, n6);
        int i = this.getChunkIndex(this.fNodeExtra, n3, n4);
        int n7 = -1;
        int n8;
        int n9;
        for (n8 = -1, n9 = -1; i != -1; i = this.getChunkIndex(this.fNodePrevSib, n8, n9)) {
            n8 = i >> 11;
            n9 = (i & 0x7FF);
            if (this.getChunkValue(this.fNodeName, n8, n9).equals(chunkValue)) {
                break;
            }
            n7 = i;
        }
        if (i != -1) {
            final int chunkIndex = this.getChunkIndex(this.fNodePrevSib, n8, n9);
            if (n7 == -1) {
                this.setChunkIndex(this.fNodeExtra, chunkIndex, n3, n4);
            }
            else {
                this.setChunkIndex(this.fNodePrevSib, chunkIndex, n7 >> 11, n7 & 0x7FF);
            }
            this.clearChunkIndex(this.fNodeType, n8, n9);
            this.clearChunkValue(this.fNodeName, n8, n9);
            this.clearChunkValue(this.fNodeValue, n8, n9);
            this.clearChunkIndex(this.fNodeParent, n8, n9);
            this.clearChunkIndex(this.fNodePrevSib, n8, n9);
            final int clearChunkIndex = this.clearChunkIndex(this.fNodeLastChild, n8, n9);
            final int n10 = clearChunkIndex >> 11;
            final int n11 = clearChunkIndex & 0x7FF;
            this.clearChunkIndex(this.fNodeType, n10, n11);
            this.clearChunkValue(this.fNodeValue, n10, n11);
            this.clearChunkIndex(this.fNodeParent, n10, n11);
            this.clearChunkIndex(this.fNodeLastChild, n10, n11);
        }
        final int chunkIndex2 = this.getChunkIndex(this.fNodeExtra, n3, n4);
        this.setChunkIndex(this.fNodeExtra, n2, n3, n4);
        this.setChunkIndex(this.fNodePrevSib, chunkIndex2, n5, n6);
        return i;
    }
    
    public void setIdAttributeNode(final int n, final int n2) {
        final int n3 = n2 >> 11;
        final int n4 = n2 & 0x7FF;
        this.setChunkIndex(this.fNodeExtra, this.getChunkIndex(this.fNodeExtra, n3, n4) | 0x200, n3, n4);
        this.putIdentifier(this.getChunkValue(this.fNodeValue, n3, n4), n);
    }
    
    public void setIdAttribute(final int n) {
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        this.setChunkIndex(this.fNodeExtra, this.getChunkIndex(this.fNodeExtra, n2, n3) | 0x200, n2, n3);
    }
    
    public int insertBefore(final int n, final int n2, final int n3) {
        if (n3 == -1) {
            this.appendChild(n, n2);
            return n2;
        }
        final int n4 = n2 >> 11;
        final int n5 = n2 & 0x7FF;
        final int n6 = n3 >> 11;
        final int n7 = n3 & 0x7FF;
        final int chunkIndex = this.getChunkIndex(this.fNodePrevSib, n6, n7);
        this.setChunkIndex(this.fNodePrevSib, n2, n6, n7);
        this.setChunkIndex(this.fNodePrevSib, chunkIndex, n4, n5);
        return n2;
    }
    
    public void setAsLastChild(final int n, final int n2) {
        this.setChunkIndex(this.fNodeLastChild, n2, n >> 11, n & 0x7FF);
    }
    
    public int getParentNode(final int n) {
        return this.getParentNode(n, false);
    }
    
    public int getParentNode(final int n, final boolean b) {
        if (n == -1) {
            return -1;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? this.clearChunkIndex(this.fNodeParent, n2, n3) : this.getChunkIndex(this.fNodeParent, n2, n3);
    }
    
    public int getLastChild(final int n) {
        return this.getLastChild(n, true);
    }
    
    public int getLastChild(final int n, final boolean b) {
        if (n == -1) {
            return -1;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? this.clearChunkIndex(this.fNodeLastChild, n2, n3) : this.getChunkIndex(this.fNodeLastChild, n2, n3);
    }
    
    public int getPrevSibling(final int n) {
        return this.getPrevSibling(n, true);
    }
    
    public int getPrevSibling(int n, final boolean b) {
        if (n == -1) {
            return -1;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        if (this.getChunkIndex(this.fNodeType, n2, n3) == 3) {
            do {
                n = this.getChunkIndex(this.fNodePrevSib, n2, n3);
                if (n == -1) {
                    break;
                }
                n2 = n >> 11;
                n3 = (n & 0x7FF);
            } while (this.getChunkIndex(this.fNodeType, n2, n3) == 3);
        }
        else {
            n = this.getChunkIndex(this.fNodePrevSib, n2, n3);
        }
        return n;
    }
    
    public int getRealPrevSibling(final int n) {
        return this.getRealPrevSibling(n, true);
    }
    
    public int getRealPrevSibling(final int n, final boolean b) {
        if (n == -1) {
            return -1;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? this.clearChunkIndex(this.fNodePrevSib, n2, n3) : this.getChunkIndex(this.fNodePrevSib, n2, n3);
    }
    
    public int lookupElementDefinition(final String s) {
        if (this.fNodeCount > 1) {
            int n = -1;
            int n2;
            int n3;
            for (int i = this.getChunkIndex(this.fNodeLastChild, 0, 0); i != -1; i = this.getChunkIndex(this.fNodePrevSib, n2, n3)) {
                n2 = i >> 11;
                n3 = (i & 0x7FF);
                if (this.getChunkIndex(this.fNodeType, n2, n3) == 10) {
                    n = i;
                    break;
                }
            }
            if (n == -1) {
                return -1;
            }
            int n4;
            int n5;
            for (int j = this.getChunkIndex(this.fNodeLastChild, n >> 11, n & 0x7FF); j != -1; j = this.getChunkIndex(this.fNodePrevSib, n4, n5)) {
                n4 = j >> 11;
                n5 = (j & 0x7FF);
                if (this.getChunkIndex(this.fNodeType, n4, n5) == 21 && this.getChunkValue(this.fNodeName, n4, n5) == s) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    public DeferredNode getNodeObject(final int n) {
        if (n == -1) {
            return null;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        final int chunkIndex = this.getChunkIndex(this.fNodeType, n2, n3);
        if (chunkIndex != 3 && chunkIndex != 4) {
            this.clearChunkIndex(this.fNodeType, n2, n3);
        }
        DeferredNode deferredNode = null;
        switch (chunkIndex) {
            case 2: {
                if (this.fNamespacesEnabled) {
                    deferredNode = new DeferredAttrNSImpl(this, n);
                    break;
                }
                deferredNode = new DeferredAttrImpl(this, n);
                break;
            }
            case 4: {
                deferredNode = new DeferredCDATASectionImpl(this, n);
                break;
            }
            case 8: {
                deferredNode = new DeferredCommentImpl(this, n);
                break;
            }
            case 9: {
                deferredNode = this;
                break;
            }
            case 10: {
                deferredNode = new DeferredDocumentTypeImpl(this, n);
                this.docType = (DocumentTypeImpl)deferredNode;
                break;
            }
            case 1: {
                if (this.fNamespacesEnabled) {
                    deferredNode = new DeferredElementNSImpl(this, n);
                }
                else {
                    deferredNode = new DeferredElementImpl(this, n);
                }
                if (this.fIdElement != null) {
                    int i = binarySearch(this.fIdElement, 0, this.fIdCount - 1, n);
                    while (i != -1) {
                        final String s = this.fIdName[i];
                        if (s != null) {
                            this.putIdentifier0(s, (Element)deferredNode);
                            this.fIdName[i] = null;
                        }
                        if (i + 1 < this.fIdCount && this.fIdElement[i + 1] == n) {
                            ++i;
                        }
                        else {
                            i = -1;
                        }
                    }
                    break;
                }
                break;
            }
            case 6: {
                deferredNode = new DeferredEntityImpl(this, n);
                break;
            }
            case 5: {
                deferredNode = new DeferredEntityReferenceImpl(this, n);
                break;
            }
            case 12: {
                deferredNode = new DeferredNotationImpl(this, n);
                break;
            }
            case 7: {
                deferredNode = new DeferredProcessingInstructionImpl(this, n);
                break;
            }
            case 3: {
                deferredNode = new DeferredTextImpl(this, n);
                break;
            }
            case 21: {
                deferredNode = new DeferredElementDefinitionImpl(this, n);
                break;
            }
            default: {
                throw new IllegalArgumentException("type: " + chunkIndex);
            }
        }
        if (deferredNode != null) {
            return deferredNode;
        }
        throw new IllegalArgumentException();
    }
    
    public String getNodeName(final int n) {
        return this.getNodeName(n, true);
    }
    
    public String getNodeName(final int n, final boolean b) {
        if (n == -1) {
            return null;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? this.clearChunkValue(this.fNodeName, n2, n3) : this.getChunkValue(this.fNodeName, n2, n3);
    }
    
    public String getNodeValueString(final int n) {
        return this.getNodeValueString(n, true);
    }
    
    public String getNodeValueString(final int n, final boolean b) {
        if (n == -1) {
            return null;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        final String s = b ? this.clearChunkValue(this.fNodeValue, n2, n3) : this.getChunkValue(this.fNodeValue, n2, n3);
        if (s == null) {
            return null;
        }
        final int chunkIndex = this.getChunkIndex(this.fNodeType, n2, n3);
        if (chunkIndex == 3) {
            int n4 = this.getRealPrevSibling(n);
            if (n4 != -1 && this.getNodeType(n4, false) == 3) {
                this.fStrChunks.add(s);
                do {
                    final int n5 = n4 >> 11;
                    final int n6 = n4 & 0x7FF;
                    this.fStrChunks.add(this.getChunkValue(this.fNodeValue, n5, n6));
                    n4 = this.getChunkIndex(this.fNodePrevSib, n5, n6);
                    if (n4 == -1) {
                        break;
                    }
                } while (this.getNodeType(n4, false) == 3);
                for (int i = this.fStrChunks.size() - 1; i >= 0; --i) {
                    this.fBufferStr.append((String)this.fStrChunks.get(i));
                }
                final String string = this.fBufferStr.toString();
                this.fStrChunks.clear();
                this.fBufferStr.setLength(0);
                return string;
            }
        }
        else if (chunkIndex == 4) {
            int j = this.getLastChild(n, false);
            if (j != -1) {
                this.fBufferStr.append(s);
                while (j != -1) {
                    final int n7 = j >> 11;
                    final int n8 = j & 0x7FF;
                    this.fStrChunks.add(this.getChunkValue(this.fNodeValue, n7, n8));
                    j = this.getChunkIndex(this.fNodePrevSib, n7, n8);
                }
                for (int k = this.fStrChunks.size() - 1; k >= 0; --k) {
                    this.fBufferStr.append((String)this.fStrChunks.get(k));
                }
                final String string2 = this.fBufferStr.toString();
                this.fStrChunks.clear();
                this.fBufferStr.setLength(0);
                return string2;
            }
        }
        return s;
    }
    
    public String getNodeValue(final int n) {
        return this.getNodeValue(n, true);
    }
    
    public Object getTypeInfo(final int n) {
        if (n == -1) {
            return null;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        final Object o = (this.fNodeValue[n2] != null) ? this.fNodeValue[n2][n3] : null;
        if (o != null) {
            this.fNodeValue[n2][n3] = null;
            final RefCount refCount2;
            final RefCount refCount = refCount2 = (RefCount)this.fNodeValue[n2][2048];
            --refCount2.fCount;
            if (refCount.fCount == 0) {
                this.fNodeValue[n2] = null;
            }
        }
        return o;
    }
    
    public String getNodeValue(final int n, final boolean b) {
        if (n == -1) {
            return null;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? this.clearChunkValue(this.fNodeValue, n2, n3) : this.getChunkValue(this.fNodeValue, n2, n3);
    }
    
    public int getNodeExtra(final int n) {
        return this.getNodeExtra(n, true);
    }
    
    public int getNodeExtra(final int n, final boolean b) {
        if (n == -1) {
            return -1;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? this.clearChunkIndex(this.fNodeExtra, n2, n3) : this.getChunkIndex(this.fNodeExtra, n2, n3);
    }
    
    public short getNodeType(final int n) {
        return this.getNodeType(n, true);
    }
    
    public short getNodeType(final int n, final boolean b) {
        if (n == -1) {
            return -1;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? ((short)this.clearChunkIndex(this.fNodeType, n2, n3)) : ((short)this.getChunkIndex(this.fNodeType, n2, n3));
    }
    
    public String getAttribute(final int n, final String s) {
        if (n == -1 || s == null) {
            return null;
        }
        int n2;
        int n3;
        for (int i = this.getChunkIndex(this.fNodeExtra, n >> 11, n & 0x7FF); i != -1; i = this.getChunkIndex(this.fNodePrevSib, n2, n3)) {
            n2 = i >> 11;
            n3 = (i & 0x7FF);
            if (this.getChunkValue(this.fNodeName, n2, n3) == s) {
                return this.getChunkValue(this.fNodeValue, n2, n3);
            }
        }
        return null;
    }
    
    public String getNodeURI(final int n) {
        return this.getNodeURI(n, true);
    }
    
    public String getNodeURI(final int n, final boolean b) {
        if (n == -1) {
            return null;
        }
        final int n2 = n >> 11;
        final int n3 = n & 0x7FF;
        return b ? this.clearChunkValue(this.fNodeURI, n2, n3) : this.getChunkValue(this.fNodeURI, n2, n3);
    }
    
    public void putIdentifier(final String s, final int n) {
        if (this.fIdName == null) {
            this.fIdName = new String[64];
            this.fIdElement = new int[64];
        }
        if (this.fIdCount == this.fIdName.length) {
            final String[] fIdName = new String[this.fIdCount * 2];
            System.arraycopy(this.fIdName, 0, fIdName, 0, this.fIdCount);
            this.fIdName = fIdName;
            final int[] fIdElement = new int[fIdName.length];
            System.arraycopy(this.fIdElement, 0, fIdElement, 0, this.fIdCount);
            this.fIdElement = fIdElement;
        }
        this.fIdName[this.fIdCount] = s;
        this.fIdElement[this.fIdCount] = n;
        ++this.fIdCount;
    }
    
    public void print() {
    }
    
    public int getNodeIndex() {
        return 0;
    }
    
    protected void synchronizeData() {
        this.needsSyncData(false);
        if (this.fIdElement != null) {
            final IntVector intVector = new IntVector();
            for (int i = 0; i < this.fIdCount; ++i) {
                final int n = this.fIdElement[i];
                final String s = this.fIdName[i];
                if (s != null) {
                    intVector.removeAllElements();
                    int j = n;
                    do {
                        intVector.addElement(j);
                        j = this.getChunkIndex(this.fNodeParent, j >> 11, j & 0x7FF);
                    } while (j != -1);
                    Object o = this;
                    for (int k = intVector.size() - 2; k >= 0; --k) {
                        final int element = intVector.elementAt(k);
                        for (Node node = ((Node)o).getLastChild(); node != null; node = node.getPreviousSibling()) {
                            if (node instanceof DeferredNode && ((DeferredNode)node).getNodeIndex() == element) {
                                o = node;
                                break;
                            }
                        }
                    }
                    final Element element2 = (Element)o;
                    this.putIdentifier0(s, element2);
                    this.fIdName[i] = null;
                    while (i + 1 < this.fIdCount && this.fIdElement[i + 1] == n) {
                        final String s2 = this.fIdName[++i];
                        if (s2 == null) {
                            continue;
                        }
                        this.putIdentifier0(s2, element2);
                    }
                }
            }
        }
    }
    
    protected void synchronizeChildren() {
        if (this.needsSyncData()) {
            this.synchronizeData();
            if (!this.needsSyncChildren()) {
                return;
            }
        }
        final boolean mutationEvents = this.mutationEvents;
        this.needsSyncChildren(this.mutationEvents = false);
        this.getNodeType(0);
        ChildNode childNode = null;
        ChildNode childNode2 = null;
        for (int i = this.getLastChild(0); i != -1; i = this.getPrevSibling(i)) {
            final ChildNode previousSibling = (ChildNode)this.getNodeObject(i);
            if (childNode2 == null) {
                childNode2 = previousSibling;
            }
            else {
                childNode.previousSibling = previousSibling;
            }
            previousSibling.ownerNode = this;
            previousSibling.isOwned(true);
            previousSibling.nextSibling = childNode;
            childNode = previousSibling;
            final short nodeType = previousSibling.getNodeType();
            if (nodeType == 1) {
                this.docElement = (ElementImpl)previousSibling;
            }
            else if (nodeType == 10) {
                this.docType = (DocumentTypeImpl)previousSibling;
            }
        }
        if (childNode != null) {
            (this.firstChild = childNode).isFirstChild(true);
            this.lastChild(childNode2);
        }
        this.mutationEvents = mutationEvents;
    }
    
    protected final void synchronizeChildren(final AttrImpl ownerNode, final int n) {
        final boolean mutationEvents = this.getMutationEvents();
        this.setMutationEvents(false);
        ownerNode.needsSyncChildren(false);
        final int lastChild = this.getLastChild(n);
        if (this.getPrevSibling(lastChild) == -1) {
            ownerNode.value = this.getNodeValueString(n);
            ownerNode.hasStringValue(true);
        }
        else {
            ChildNode childNode = null;
            ChildNode childNode2 = null;
            for (int i = lastChild; i != -1; i = this.getPrevSibling(i)) {
                final ChildNode previousSibling = (ChildNode)this.getNodeObject(i);
                if (childNode2 == null) {
                    childNode2 = previousSibling;
                }
                else {
                    childNode.previousSibling = previousSibling;
                }
                previousSibling.ownerNode = ownerNode;
                previousSibling.isOwned(true);
                previousSibling.nextSibling = childNode;
                childNode = previousSibling;
            }
            if (childNode2 != null) {
                ((NodeImpl)(ownerNode.value = childNode)).isFirstChild(true);
                ownerNode.lastChild(childNode2);
            }
            ownerNode.hasStringValue(false);
        }
        this.setMutationEvents(mutationEvents);
    }
    
    protected final void synchronizeChildren(final ParentNode ownerNode, final int n) {
        final boolean mutationEvents = this.getMutationEvents();
        this.setMutationEvents(false);
        ownerNode.needsSyncChildren(false);
        ChildNode childNode = null;
        ChildNode childNode2 = null;
        for (int i = this.getLastChild(n); i != -1; i = this.getPrevSibling(i)) {
            final ChildNode previousSibling = (ChildNode)this.getNodeObject(i);
            if (childNode2 == null) {
                childNode2 = previousSibling;
            }
            else {
                childNode.previousSibling = previousSibling;
            }
            previousSibling.ownerNode = ownerNode;
            previousSibling.isOwned(true);
            previousSibling.nextSibling = childNode;
            childNode = previousSibling;
        }
        if (childNode2 != null) {
            (ownerNode.firstChild = childNode).isFirstChild(true);
            ownerNode.lastChild(childNode2);
        }
        this.setMutationEvents(mutationEvents);
    }
    
    protected void ensureCapacity(final int n) {
        if (this.fNodeType == null) {
            this.fNodeType = new int[32][];
            this.fNodeName = new Object[32][];
            this.fNodeValue = new Object[32][];
            this.fNodeParent = new int[32][];
            this.fNodeLastChild = new int[32][];
            this.fNodePrevSib = new int[32][];
            this.fNodeURI = new Object[32][];
            this.fNodeExtra = new int[32][];
        }
        else if (this.fNodeType.length <= n) {
            final int n2 = n * 2;
            final int[][] fNodeType = new int[n2][];
            System.arraycopy(this.fNodeType, 0, fNodeType, 0, n);
            this.fNodeType = fNodeType;
            final Object[][] fNodeName = new Object[n2][];
            System.arraycopy(this.fNodeName, 0, fNodeName, 0, n);
            this.fNodeName = fNodeName;
            final Object[][] fNodeValue = new Object[n2][];
            System.arraycopy(this.fNodeValue, 0, fNodeValue, 0, n);
            this.fNodeValue = fNodeValue;
            final int[][] fNodeParent = new int[n2][];
            System.arraycopy(this.fNodeParent, 0, fNodeParent, 0, n);
            this.fNodeParent = fNodeParent;
            final int[][] fNodeLastChild = new int[n2][];
            System.arraycopy(this.fNodeLastChild, 0, fNodeLastChild, 0, n);
            this.fNodeLastChild = fNodeLastChild;
            final int[][] fNodePrevSib = new int[n2][];
            System.arraycopy(this.fNodePrevSib, 0, fNodePrevSib, 0, n);
            this.fNodePrevSib = fNodePrevSib;
            final Object[][] fNodeURI = new Object[n2][];
            System.arraycopy(this.fNodeURI, 0, fNodeURI, 0, n);
            this.fNodeURI = fNodeURI;
            final int[][] fNodeExtra = new int[n2][];
            System.arraycopy(this.fNodeExtra, 0, fNodeExtra, 0, n);
            this.fNodeExtra = fNodeExtra;
        }
        else if (this.fNodeType[n] != null) {
            return;
        }
        this.createChunk(this.fNodeType, n);
        this.createChunk(this.fNodeName, n);
        this.createChunk(this.fNodeValue, n);
        this.createChunk(this.fNodeParent, n);
        this.createChunk(this.fNodeLastChild, n);
        this.createChunk(this.fNodePrevSib, n);
        this.createChunk(this.fNodeURI, n);
        this.createChunk(this.fNodeExtra, n);
    }
    
    protected int createNode(final short n) {
        final int n2 = this.fNodeCount >> 11;
        final int n3 = this.fNodeCount & 0x7FF;
        this.ensureCapacity(n2);
        this.setChunkIndex(this.fNodeType, n, n2, n3);
        return this.fNodeCount++;
    }
    
    protected static int binarySearch(final int[] array, int i, int n, final int n2) {
        while (i <= n) {
            int n3 = i + n >>> 1;
            final int n4 = array[n3];
            if (n4 == n2) {
                while (n3 > 0 && array[n3 - 1] == n2) {
                    --n3;
                }
                return n3;
            }
            if (n4 > n2) {
                n = n3 - 1;
            }
            else {
                i = n3 + 1;
            }
        }
        return -1;
    }
    
    private final void createChunk(final int[][] array, final int n) {
        array[n] = new int[2049];
        System.arraycopy(DeferredDocumentImpl.INIT_ARRAY, 0, array[n], 0, 2048);
    }
    
    private final void createChunk(final Object[][] array, final int n) {
        (array[n] = new Object[2049])[2048] = new RefCount();
    }
    
    private final int setChunkIndex(final int[][] array, final int n, final int n2, final int n3) {
        if (n == -1) {
            return this.clearChunkIndex(array, n2, n3);
        }
        int[] array2 = array[n2];
        if (array2 == null) {
            this.createChunk(array, n2);
            array2 = array[n2];
        }
        final int n4 = array2[n3];
        if (n4 == -1) {
            final int[] array3 = array2;
            final int n5 = 2048;
            ++array3[n5];
        }
        array2[n3] = n;
        return n4;
    }
    
    private final String setChunkValue(final Object[][] array, final Object o, final int n, final int n2) {
        if (o == null) {
            return this.clearChunkValue(array, n, n2);
        }
        Object[] array2 = array[n];
        if (array2 == null) {
            this.createChunk(array, n);
            array2 = array[n];
        }
        final String s = (String)array2[n2];
        if (s == null) {
            final RefCount refCount = (RefCount)array2[2048];
            ++refCount.fCount;
        }
        array2[n2] = o;
        return s;
    }
    
    private final int getChunkIndex(final int[][] array, final int n, final int n2) {
        return (array[n] != null) ? array[n][n2] : -1;
    }
    
    private final String getChunkValue(final Object[][] array, final int n, final int n2) {
        return (array[n] != null) ? ((String)array[n][n2]) : null;
    }
    
    private final String getNodeValue(final int n, final int n2) {
        final Object o = this.fNodeValue[n][n2];
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return (String)o;
        }
        return o.toString();
    }
    
    private final int clearChunkIndex(final int[][] array, final int n, final int n2) {
        final int n3 = (array[n] != null) ? array[n][n2] : -1;
        if (n3 != -1) {
            final int[] array2 = array[n];
            final int n4 = 2048;
            --array2[n4];
            array[n][n2] = -1;
            if (array[n][2048] == 0) {
                array[n] = null;
            }
        }
        return n3;
    }
    
    private final String clearChunkValue(final Object[][] array, final int n, final int n2) {
        final String s = (array[n] != null) ? ((String)array[n][n2]) : null;
        if (s != null) {
            array[n][n2] = null;
            final RefCount refCount2;
            final RefCount refCount = refCount2 = (RefCount)array[n][2048];
            --refCount2.fCount;
            if (refCount.fCount == 0) {
                array[n] = null;
            }
        }
        return s;
    }
    
    private final void putIdentifier0(final String s, final Element element) {
        if (this.identifiers == null) {
            this.identifiers = new Hashtable();
        }
        this.identifiers.put(s, element);
    }
    
    private static void print(final int[] array, final int n, final int n2, final int n3, final int n4) {
    }
    
    static {
        INIT_ARRAY = new int[2049];
        for (int i = 0; i < 2048; ++i) {
            DeferredDocumentImpl.INIT_ARRAY[i] = -1;
        }
    }
    
    static final class IntVector
    {
        private int[] data;
        private int size;
        
        public int size() {
            return this.size;
        }
        
        public int elementAt(final int n) {
            return this.data[n];
        }
        
        public void addElement(final int n) {
            this.ensureCapacity(this.size + 1);
            this.data[this.size++] = n;
        }
        
        public void removeAllElements() {
            this.size = 0;
        }
        
        private void ensureCapacity(final int n) {
            if (this.data == null) {
                this.data = new int[n + 15];
            }
            else if (n > this.data.length) {
                final int[] data = new int[n + 15];
                System.arraycopy(this.data, 0, data, 0, this.data.length);
                this.data = data;
            }
        }
    }
    
    static final class RefCount
    {
        int fCount;
    }
}
