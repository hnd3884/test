package com.sun.org.apache.xerces.internal.dom;

import java.util.HashMap;
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
    protected static final int CHUNK_SHIFT = 8;
    protected static final int CHUNK_SIZE = 256;
    protected static final int CHUNK_MASK = 255;
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
    private final transient StringBuilder fBufferStr;
    private final transient ArrayList fStrChunks;
    private static final int[] INIT_ARRAY;
    
    public DeferredDocumentImpl() {
        this(false);
    }
    
    public DeferredDocumentImpl(final boolean namespacesEnabled) {
        this(namespacesEnabled, false);
    }
    
    public DeferredDocumentImpl(final boolean namespaces, final boolean grammarAccess) {
        super(grammarAccess);
        this.fNodeCount = 0;
        this.fNamespacesEnabled = false;
        this.fBufferStr = new StringBuilder();
        this.fStrChunks = new ArrayList();
        this.needsSyncData(true);
        this.needsSyncChildren(true);
        this.fNamespacesEnabled = namespaces;
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return DeferredDOMImplementationImpl.getDOMImplementation();
    }
    
    boolean getNamespacesEnabled() {
        return this.fNamespacesEnabled;
    }
    
    void setNamespacesEnabled(final boolean enable) {
        this.fNamespacesEnabled = enable;
    }
    
    public int createDeferredDocument() {
        final int nodeIndex = this.createNode((short)9);
        return nodeIndex;
    }
    
    public int createDeferredDocumentType(final String rootElementName, final String publicId, final String systemId) {
        final int nodeIndex = this.createNode((short)10);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeName, rootElementName, chunk, index);
        this.setChunkValue(this.fNodeValue, publicId, chunk, index);
        this.setChunkValue(this.fNodeURI, systemId, chunk, index);
        return nodeIndex;
    }
    
    public void setInternalSubset(final int doctypeIndex, final String subset) {
        final int chunk = doctypeIndex >> 8;
        final int index = doctypeIndex & 0xFF;
        final int extraDataIndex = this.createNode((short)10);
        final int echunk = extraDataIndex >> 8;
        final int eindex = extraDataIndex & 0xFF;
        this.setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
        this.setChunkValue(this.fNodeValue, subset, echunk, eindex);
    }
    
    public int createDeferredNotation(final String notationName, final String publicId, final String systemId, final String baseURI) {
        final int nodeIndex = this.createNode((short)12);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        final int extraDataIndex = this.createNode((short)12);
        final int echunk = extraDataIndex >> 8;
        final int eindex = extraDataIndex & 0xFF;
        this.setChunkValue(this.fNodeName, notationName, chunk, index);
        this.setChunkValue(this.fNodeValue, publicId, chunk, index);
        this.setChunkValue(this.fNodeURI, systemId, chunk, index);
        this.setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
        this.setChunkValue(this.fNodeName, baseURI, echunk, eindex);
        return nodeIndex;
    }
    
    public int createDeferredEntity(final String entityName, final String publicId, final String systemId, final String notationName, final String baseURI) {
        final int nodeIndex = this.createNode((short)6);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        final int extraDataIndex = this.createNode((short)6);
        final int echunk = extraDataIndex >> 8;
        final int eindex = extraDataIndex & 0xFF;
        this.setChunkValue(this.fNodeName, entityName, chunk, index);
        this.setChunkValue(this.fNodeValue, publicId, chunk, index);
        this.setChunkValue(this.fNodeURI, systemId, chunk, index);
        this.setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
        this.setChunkValue(this.fNodeName, notationName, echunk, eindex);
        this.setChunkValue(this.fNodeValue, null, echunk, eindex);
        this.setChunkValue(this.fNodeURI, null, echunk, eindex);
        final int extraDataIndex2 = this.createNode((short)6);
        final int echunk2 = extraDataIndex2 >> 8;
        final int eindex2 = extraDataIndex2 & 0xFF;
        this.setChunkIndex(this.fNodeExtra, extraDataIndex2, echunk, eindex);
        this.setChunkValue(this.fNodeName, baseURI, echunk2, eindex2);
        return nodeIndex;
    }
    
    public String getDeferredEntityBaseURI(final int entityIndex) {
        if (entityIndex != -1) {
            int extraDataIndex = this.getNodeExtra(entityIndex, false);
            extraDataIndex = this.getNodeExtra(extraDataIndex, false);
            return this.getNodeName(extraDataIndex, false);
        }
        return null;
    }
    
    public void setEntityInfo(final int currentEntityDecl, final String version, final String encoding) {
        final int eNodeIndex = this.getNodeExtra(currentEntityDecl, false);
        if (eNodeIndex != -1) {
            final int echunk = eNodeIndex >> 8;
            final int eindex = eNodeIndex & 0xFF;
            this.setChunkValue(this.fNodeValue, version, echunk, eindex);
            this.setChunkValue(this.fNodeURI, encoding, echunk, eindex);
        }
    }
    
    public void setTypeInfo(final int elementNodeIndex, final Object type) {
        final int elementChunk = elementNodeIndex >> 8;
        final int elementIndex = elementNodeIndex & 0xFF;
        this.setChunkValue(this.fNodeValue, type, elementChunk, elementIndex);
    }
    
    public void setInputEncoding(final int currentEntityDecl, final String value) {
        final int nodeIndex = this.getNodeExtra(currentEntityDecl, false);
        final int extraDataIndex = this.getNodeExtra(nodeIndex, false);
        final int echunk = extraDataIndex >> 8;
        final int eindex = extraDataIndex & 0xFF;
        this.setChunkValue(this.fNodeValue, value, echunk, eindex);
    }
    
    public int createDeferredEntityReference(final String name, final String baseURI) {
        final int nodeIndex = this.createNode((short)5);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeName, name, chunk, index);
        this.setChunkValue(this.fNodeValue, baseURI, chunk, index);
        return nodeIndex;
    }
    
    @Deprecated
    public int createDeferredElement(final String elementURI, final String elementName, final Object type) {
        final int elementNodeIndex = this.createNode((short)1);
        final int elementChunk = elementNodeIndex >> 8;
        final int elementIndex = elementNodeIndex & 0xFF;
        this.setChunkValue(this.fNodeName, elementName, elementChunk, elementIndex);
        this.setChunkValue(this.fNodeURI, elementURI, elementChunk, elementIndex);
        this.setChunkValue(this.fNodeValue, type, elementChunk, elementIndex);
        return elementNodeIndex;
    }
    
    @Deprecated
    public int createDeferredElement(final String elementName) {
        return this.createDeferredElement(null, elementName);
    }
    
    public int createDeferredElement(final String elementURI, final String elementName) {
        final int elementNodeIndex = this.createNode((short)1);
        final int elementChunk = elementNodeIndex >> 8;
        final int elementIndex = elementNodeIndex & 0xFF;
        this.setChunkValue(this.fNodeName, elementName, elementChunk, elementIndex);
        this.setChunkValue(this.fNodeURI, elementURI, elementChunk, elementIndex);
        return elementNodeIndex;
    }
    
    public int setDeferredAttribute(final int elementNodeIndex, final String attrName, final String attrURI, final String attrValue, final boolean specified, final boolean id, final Object type) {
        final int attrNodeIndex = this.createDeferredAttribute(attrName, attrURI, attrValue, specified);
        final int attrChunk = attrNodeIndex >> 8;
        final int attrIndex = attrNodeIndex & 0xFF;
        this.setChunkIndex(this.fNodeParent, elementNodeIndex, attrChunk, attrIndex);
        final int elementChunk = elementNodeIndex >> 8;
        final int elementIndex = elementNodeIndex & 0xFF;
        final int lastAttrNodeIndex = this.getChunkIndex(this.fNodeExtra, elementChunk, elementIndex);
        if (lastAttrNodeIndex != 0) {
            this.setChunkIndex(this.fNodePrevSib, lastAttrNodeIndex, attrChunk, attrIndex);
        }
        this.setChunkIndex(this.fNodeExtra, attrNodeIndex, elementChunk, elementIndex);
        int extra = this.getChunkIndex(this.fNodeExtra, attrChunk, attrIndex);
        if (id) {
            extra |= 0x200;
            this.setChunkIndex(this.fNodeExtra, extra, attrChunk, attrIndex);
            final String value = this.getChunkValue(this.fNodeValue, attrChunk, attrIndex);
            this.putIdentifier(value, elementNodeIndex);
        }
        if (type != null) {
            final int extraDataIndex = this.createNode((short)20);
            final int echunk = extraDataIndex >> 8;
            final int eindex = extraDataIndex & 0xFF;
            this.setChunkIndex(this.fNodeLastChild, extraDataIndex, attrChunk, attrIndex);
            this.setChunkValue(this.fNodeValue, type, echunk, eindex);
        }
        return attrNodeIndex;
    }
    
    @Deprecated
    public int setDeferredAttribute(final int elementNodeIndex, final String attrName, final String attrURI, final String attrValue, final boolean specified) {
        final int attrNodeIndex = this.createDeferredAttribute(attrName, attrURI, attrValue, specified);
        final int attrChunk = attrNodeIndex >> 8;
        final int attrIndex = attrNodeIndex & 0xFF;
        this.setChunkIndex(this.fNodeParent, elementNodeIndex, attrChunk, attrIndex);
        final int elementChunk = elementNodeIndex >> 8;
        final int elementIndex = elementNodeIndex & 0xFF;
        final int lastAttrNodeIndex = this.getChunkIndex(this.fNodeExtra, elementChunk, elementIndex);
        if (lastAttrNodeIndex != 0) {
            this.setChunkIndex(this.fNodePrevSib, lastAttrNodeIndex, attrChunk, attrIndex);
        }
        this.setChunkIndex(this.fNodeExtra, attrNodeIndex, elementChunk, elementIndex);
        return attrNodeIndex;
    }
    
    public int createDeferredAttribute(final String attrName, final String attrValue, final boolean specified) {
        return this.createDeferredAttribute(attrName, null, attrValue, specified);
    }
    
    public int createDeferredAttribute(final String attrName, final String attrURI, final String attrValue, final boolean specified) {
        final int nodeIndex = this.createNode((short)2);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeName, attrName, chunk, index);
        this.setChunkValue(this.fNodeURI, attrURI, chunk, index);
        this.setChunkValue(this.fNodeValue, attrValue, chunk, index);
        final int extra = specified ? 32 : 0;
        this.setChunkIndex(this.fNodeExtra, extra, chunk, index);
        return nodeIndex;
    }
    
    public int createDeferredElementDefinition(final String elementName) {
        final int nodeIndex = this.createNode((short)21);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeName, elementName, chunk, index);
        return nodeIndex;
    }
    
    public int createDeferredTextNode(final String data, final boolean ignorableWhitespace) {
        final int nodeIndex = this.createNode((short)3);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeValue, data, chunk, index);
        this.setChunkIndex(this.fNodeExtra, ignorableWhitespace ? 1 : 0, chunk, index);
        return nodeIndex;
    }
    
    public int createDeferredCDATASection(final String data) {
        final int nodeIndex = this.createNode((short)4);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeValue, data, chunk, index);
        return nodeIndex;
    }
    
    public int createDeferredProcessingInstruction(final String target, final String data) {
        final int nodeIndex = this.createNode((short)7);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeName, target, chunk, index);
        this.setChunkValue(this.fNodeValue, data, chunk, index);
        return nodeIndex;
    }
    
    public int createDeferredComment(final String data) {
        final int nodeIndex = this.createNode((short)8);
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        this.setChunkValue(this.fNodeValue, data, chunk, index);
        return nodeIndex;
    }
    
    public int cloneNode(final int nodeIndex, final boolean deep) {
        final int nchunk = nodeIndex >> 8;
        final int nindex = nodeIndex & 0xFF;
        final int nodeType = this.fNodeType[nchunk][nindex];
        final int cloneIndex = this.createNode((short)nodeType);
        final int cchunk = cloneIndex >> 8;
        final int cindex = cloneIndex & 0xFF;
        this.setChunkValue(this.fNodeName, this.fNodeName[nchunk][nindex], cchunk, cindex);
        this.setChunkValue(this.fNodeValue, this.fNodeValue[nchunk][nindex], cchunk, cindex);
        this.setChunkValue(this.fNodeURI, this.fNodeURI[nchunk][nindex], cchunk, cindex);
        int extraIndex = this.fNodeExtra[nchunk][nindex];
        if (extraIndex != -1) {
            if (nodeType != 2 && nodeType != 3) {
                extraIndex = this.cloneNode(extraIndex, false);
            }
            this.setChunkIndex(this.fNodeExtra, extraIndex, cchunk, cindex);
        }
        if (deep) {
            int prevIndex = -1;
            for (int childIndex = this.getLastChild(nodeIndex, false); childIndex != -1; childIndex = this.getRealPrevSibling(childIndex, false)) {
                final int clonedChildIndex = this.cloneNode(childIndex, deep);
                this.insertBefore(cloneIndex, clonedChildIndex, prevIndex);
                prevIndex = clonedChildIndex;
            }
        }
        return cloneIndex;
    }
    
    public void appendChild(final int parentIndex, final int childIndex) {
        final int pchunk = parentIndex >> 8;
        final int pindex = parentIndex & 0xFF;
        final int cchunk = childIndex >> 8;
        final int cindex = childIndex & 0xFF;
        this.setChunkIndex(this.fNodeParent, parentIndex, cchunk, cindex);
        final int olast = this.getChunkIndex(this.fNodeLastChild, pchunk, pindex);
        this.setChunkIndex(this.fNodePrevSib, olast, cchunk, cindex);
        this.setChunkIndex(this.fNodeLastChild, childIndex, pchunk, pindex);
    }
    
    public int setAttributeNode(final int elemIndex, final int attrIndex) {
        final int echunk = elemIndex >> 8;
        final int eindex = elemIndex & 0xFF;
        final int achunk = attrIndex >> 8;
        final int aindex = attrIndex & 0xFF;
        final String attrName = this.getChunkValue(this.fNodeName, achunk, aindex);
        int oldAttrIndex = this.getChunkIndex(this.fNodeExtra, echunk, eindex);
        int nextIndex = -1;
        int oachunk;
        int oaindex;
        for (oachunk = -1, oaindex = -1; oldAttrIndex != -1; oldAttrIndex = this.getChunkIndex(this.fNodePrevSib, oachunk, oaindex)) {
            oachunk = oldAttrIndex >> 8;
            oaindex = (oldAttrIndex & 0xFF);
            final String oldAttrName = this.getChunkValue(this.fNodeName, oachunk, oaindex);
            if (oldAttrName.equals(attrName)) {
                break;
            }
            nextIndex = oldAttrIndex;
        }
        if (oldAttrIndex != -1) {
            final int prevIndex = this.getChunkIndex(this.fNodePrevSib, oachunk, oaindex);
            if (nextIndex == -1) {
                this.setChunkIndex(this.fNodeExtra, prevIndex, echunk, eindex);
            }
            else {
                final int pchunk = nextIndex >> 8;
                final int pindex = nextIndex & 0xFF;
                this.setChunkIndex(this.fNodePrevSib, prevIndex, pchunk, pindex);
            }
            this.clearChunkIndex(this.fNodeType, oachunk, oaindex);
            this.clearChunkValue(this.fNodeName, oachunk, oaindex);
            this.clearChunkValue(this.fNodeValue, oachunk, oaindex);
            this.clearChunkIndex(this.fNodeParent, oachunk, oaindex);
            this.clearChunkIndex(this.fNodePrevSib, oachunk, oaindex);
            final int attrTextIndex = this.clearChunkIndex(this.fNodeLastChild, oachunk, oaindex);
            final int atchunk = attrTextIndex >> 8;
            final int atindex = attrTextIndex & 0xFF;
            this.clearChunkIndex(this.fNodeType, atchunk, atindex);
            this.clearChunkValue(this.fNodeValue, atchunk, atindex);
            this.clearChunkIndex(this.fNodeParent, atchunk, atindex);
            this.clearChunkIndex(this.fNodeLastChild, atchunk, atindex);
        }
        final int prevIndex = this.getChunkIndex(this.fNodeExtra, echunk, eindex);
        this.setChunkIndex(this.fNodeExtra, attrIndex, echunk, eindex);
        this.setChunkIndex(this.fNodePrevSib, prevIndex, achunk, aindex);
        return oldAttrIndex;
    }
    
    public void setIdAttributeNode(final int elemIndex, final int attrIndex) {
        final int chunk = attrIndex >> 8;
        final int index = attrIndex & 0xFF;
        int extra = this.getChunkIndex(this.fNodeExtra, chunk, index);
        extra |= 0x200;
        this.setChunkIndex(this.fNodeExtra, extra, chunk, index);
        final String value = this.getChunkValue(this.fNodeValue, chunk, index);
        this.putIdentifier(value, elemIndex);
    }
    
    public void setIdAttribute(final int attrIndex) {
        final int chunk = attrIndex >> 8;
        final int index = attrIndex & 0xFF;
        int extra = this.getChunkIndex(this.fNodeExtra, chunk, index);
        extra |= 0x200;
        this.setChunkIndex(this.fNodeExtra, extra, chunk, index);
    }
    
    public int insertBefore(final int parentIndex, final int newChildIndex, final int refChildIndex) {
        if (refChildIndex == -1) {
            this.appendChild(parentIndex, newChildIndex);
            return newChildIndex;
        }
        final int nchunk = newChildIndex >> 8;
        final int nindex = newChildIndex & 0xFF;
        final int rchunk = refChildIndex >> 8;
        final int rindex = refChildIndex & 0xFF;
        final int previousIndex = this.getChunkIndex(this.fNodePrevSib, rchunk, rindex);
        this.setChunkIndex(this.fNodePrevSib, newChildIndex, rchunk, rindex);
        this.setChunkIndex(this.fNodePrevSib, previousIndex, nchunk, nindex);
        return newChildIndex;
    }
    
    public void setAsLastChild(final int parentIndex, final int childIndex) {
        final int pchunk = parentIndex >> 8;
        final int pindex = parentIndex & 0xFF;
        this.setChunkIndex(this.fNodeLastChild, childIndex, pchunk, pindex);
    }
    
    public int getParentNode(final int nodeIndex) {
        return this.getParentNode(nodeIndex, false);
    }
    
    public int getParentNode(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return -1;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? this.clearChunkIndex(this.fNodeParent, chunk, index) : this.getChunkIndex(this.fNodeParent, chunk, index);
    }
    
    public int getLastChild(final int nodeIndex) {
        return this.getLastChild(nodeIndex, true);
    }
    
    public int getLastChild(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return -1;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? this.clearChunkIndex(this.fNodeLastChild, chunk, index) : this.getChunkIndex(this.fNodeLastChild, chunk, index);
    }
    
    public int getPrevSibling(final int nodeIndex) {
        return this.getPrevSibling(nodeIndex, true);
    }
    
    public int getPrevSibling(int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return -1;
        }
        int chunk = nodeIndex >> 8;
        int index = nodeIndex & 0xFF;
        int type = this.getChunkIndex(this.fNodeType, chunk, index);
        if (type == 3) {
            do {
                nodeIndex = this.getChunkIndex(this.fNodePrevSib, chunk, index);
                if (nodeIndex == -1) {
                    break;
                }
                chunk = nodeIndex >> 8;
                index = (nodeIndex & 0xFF);
                type = this.getChunkIndex(this.fNodeType, chunk, index);
            } while (type == 3);
        }
        else {
            nodeIndex = this.getChunkIndex(this.fNodePrevSib, chunk, index);
        }
        return nodeIndex;
    }
    
    public int getRealPrevSibling(final int nodeIndex) {
        return this.getRealPrevSibling(nodeIndex, true);
    }
    
    public int getRealPrevSibling(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return -1;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? this.clearChunkIndex(this.fNodePrevSib, chunk, index) : this.getChunkIndex(this.fNodePrevSib, chunk, index);
    }
    
    public int lookupElementDefinition(final String elementName) {
        if (this.fNodeCount > 1) {
            int docTypeIndex = -1;
            for (int nchunk = 0, nindex = 0, index = this.getChunkIndex(this.fNodeLastChild, nchunk, nindex); index != -1; index = this.getChunkIndex(this.fNodePrevSib, nchunk, nindex)) {
                nchunk = index >> 8;
                nindex = (index & 0xFF);
                if (this.getChunkIndex(this.fNodeType, nchunk, nindex) == 10) {
                    docTypeIndex = index;
                    break;
                }
            }
            if (docTypeIndex == -1) {
                return -1;
            }
            for (int nchunk = docTypeIndex >> 8, nindex = docTypeIndex & 0xFF, index = this.getChunkIndex(this.fNodeLastChild, nchunk, nindex); index != -1; index = this.getChunkIndex(this.fNodePrevSib, nchunk, nindex)) {
                nchunk = index >> 8;
                nindex = (index & 0xFF);
                if (this.getChunkIndex(this.fNodeType, nchunk, nindex) == 21 && this.getChunkValue(this.fNodeName, nchunk, nindex) == elementName) {
                    return index;
                }
            }
        }
        return -1;
    }
    
    public DeferredNode getNodeObject(final int nodeIndex) {
        if (nodeIndex == -1) {
            return null;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        final int type = this.getChunkIndex(this.fNodeType, chunk, index);
        if (type != 3 && type != 4) {
            this.clearChunkIndex(this.fNodeType, chunk, index);
        }
        DeferredNode node = null;
        switch (type) {
            case 2: {
                if (this.fNamespacesEnabled) {
                    node = new DeferredAttrNSImpl(this, nodeIndex);
                    break;
                }
                node = new DeferredAttrImpl(this, nodeIndex);
                break;
            }
            case 4: {
                node = new DeferredCDATASectionImpl(this, nodeIndex);
                break;
            }
            case 8: {
                node = new DeferredCommentImpl(this, nodeIndex);
                break;
            }
            case 9: {
                node = this;
                break;
            }
            case 10: {
                node = new DeferredDocumentTypeImpl(this, nodeIndex);
                this.docType = (DocumentTypeImpl)node;
                break;
            }
            case 1: {
                if (this.fNamespacesEnabled) {
                    node = new DeferredElementNSImpl(this, nodeIndex);
                }
                else {
                    node = new DeferredElementImpl(this, nodeIndex);
                }
                if (this.fIdElement != null) {
                    int idIndex = binarySearch(this.fIdElement, 0, this.fIdCount - 1, nodeIndex);
                    while (idIndex != -1) {
                        final String name = this.fIdName[idIndex];
                        if (name != null) {
                            this.putIdentifier0(name, (Element)node);
                            this.fIdName[idIndex] = null;
                        }
                        if (idIndex + 1 < this.fIdCount && this.fIdElement[idIndex + 1] == nodeIndex) {
                            ++idIndex;
                        }
                        else {
                            idIndex = -1;
                        }
                    }
                    break;
                }
                break;
            }
            case 6: {
                node = new DeferredEntityImpl(this, nodeIndex);
                break;
            }
            case 5: {
                node = new DeferredEntityReferenceImpl(this, nodeIndex);
                break;
            }
            case 12: {
                node = new DeferredNotationImpl(this, nodeIndex);
                break;
            }
            case 7: {
                node = new DeferredProcessingInstructionImpl(this, nodeIndex);
                break;
            }
            case 3: {
                node = new DeferredTextImpl(this, nodeIndex);
                break;
            }
            case 21: {
                node = new DeferredElementDefinitionImpl(this, nodeIndex);
                break;
            }
            default: {
                throw new IllegalArgumentException("type: " + type);
            }
        }
        if (node != null) {
            return node;
        }
        throw new IllegalArgumentException();
    }
    
    public String getNodeName(final int nodeIndex) {
        return this.getNodeName(nodeIndex, true);
    }
    
    public String getNodeName(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return null;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? this.clearChunkValue(this.fNodeName, chunk, index) : this.getChunkValue(this.fNodeName, chunk, index);
    }
    
    public String getNodeValueString(final int nodeIndex) {
        return this.getNodeValueString(nodeIndex, true);
    }
    
    public String getNodeValueString(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return null;
        }
        int chunk = nodeIndex >> 8;
        int index = nodeIndex & 0xFF;
        String value = free ? this.clearChunkValue(this.fNodeValue, chunk, index) : this.getChunkValue(this.fNodeValue, chunk, index);
        if (value == null) {
            return null;
        }
        final int type = this.getChunkIndex(this.fNodeType, chunk, index);
        if (type == 3) {
            int prevSib = this.getRealPrevSibling(nodeIndex);
            if (prevSib != -1 && this.getNodeType(prevSib, false) == 3) {
                this.fStrChunks.add(value);
                do {
                    chunk = prevSib >> 8;
                    index = (prevSib & 0xFF);
                    value = this.getChunkValue(this.fNodeValue, chunk, index);
                    this.fStrChunks.add(value);
                    prevSib = this.getChunkIndex(this.fNodePrevSib, chunk, index);
                    if (prevSib == -1) {
                        break;
                    }
                } while (this.getNodeType(prevSib, false) == 3);
                final int chunkCount = this.fStrChunks.size();
                for (int i = chunkCount - 1; i >= 0; --i) {
                    this.fBufferStr.append(this.fStrChunks.get(i));
                }
                value = this.fBufferStr.toString();
                this.fStrChunks.clear();
                this.fBufferStr.setLength(0);
                return value;
            }
        }
        else if (type == 4) {
            int child = this.getLastChild(nodeIndex, false);
            if (child != -1) {
                this.fBufferStr.append(value);
                while (child != -1) {
                    chunk = child >> 8;
                    index = (child & 0xFF);
                    value = this.getChunkValue(this.fNodeValue, chunk, index);
                    this.fStrChunks.add(value);
                    child = this.getChunkIndex(this.fNodePrevSib, chunk, index);
                }
                for (int j = this.fStrChunks.size() - 1; j >= 0; --j) {
                    this.fBufferStr.append(this.fStrChunks.get(j));
                }
                value = this.fBufferStr.toString();
                this.fStrChunks.clear();
                this.fBufferStr.setLength(0);
                return value;
            }
        }
        return value;
    }
    
    public String getNodeValue(final int nodeIndex) {
        return this.getNodeValue(nodeIndex, true);
    }
    
    public Object getTypeInfo(final int nodeIndex) {
        if (nodeIndex == -1) {
            return null;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        final Object value = (this.fNodeValue[chunk] != null) ? this.fNodeValue[chunk][index] : null;
        if (value != null) {
            this.fNodeValue[chunk][index] = null;
            final RefCount refCount;
            final RefCount c = refCount = (RefCount)this.fNodeValue[chunk][256];
            --refCount.fCount;
            if (c.fCount == 0) {
                this.fNodeValue[chunk] = null;
            }
        }
        return value;
    }
    
    public String getNodeValue(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return null;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? this.clearChunkValue(this.fNodeValue, chunk, index) : this.getChunkValue(this.fNodeValue, chunk, index);
    }
    
    public int getNodeExtra(final int nodeIndex) {
        return this.getNodeExtra(nodeIndex, true);
    }
    
    public int getNodeExtra(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return -1;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? this.clearChunkIndex(this.fNodeExtra, chunk, index) : this.getChunkIndex(this.fNodeExtra, chunk, index);
    }
    
    public short getNodeType(final int nodeIndex) {
        return this.getNodeType(nodeIndex, true);
    }
    
    public short getNodeType(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return -1;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? ((short)this.clearChunkIndex(this.fNodeType, chunk, index)) : ((short)this.getChunkIndex(this.fNodeType, chunk, index));
    }
    
    public String getAttribute(final int elemIndex, final String name) {
        if (elemIndex == -1 || name == null) {
            return null;
        }
        final int echunk = elemIndex >> 8;
        final int eindex = elemIndex & 0xFF;
        int achunk;
        int aindex;
        for (int attrIndex = this.getChunkIndex(this.fNodeExtra, echunk, eindex); attrIndex != -1; attrIndex = this.getChunkIndex(this.fNodePrevSib, achunk, aindex)) {
            achunk = attrIndex >> 8;
            aindex = (attrIndex & 0xFF);
            if (this.getChunkValue(this.fNodeName, achunk, aindex) == name) {
                return this.getChunkValue(this.fNodeValue, achunk, aindex);
            }
        }
        return null;
    }
    
    public String getNodeURI(final int nodeIndex) {
        return this.getNodeURI(nodeIndex, true);
    }
    
    public String getNodeURI(final int nodeIndex, final boolean free) {
        if (nodeIndex == -1) {
            return null;
        }
        final int chunk = nodeIndex >> 8;
        final int index = nodeIndex & 0xFF;
        return free ? this.clearChunkValue(this.fNodeURI, chunk, index) : this.getChunkValue(this.fNodeURI, chunk, index);
    }
    
    public void putIdentifier(final String name, final int elementNodeIndex) {
        if (this.fIdName == null) {
            this.fIdName = new String[64];
            this.fIdElement = new int[64];
        }
        if (this.fIdCount == this.fIdName.length) {
            final String[] idName = new String[this.fIdCount * 2];
            System.arraycopy(this.fIdName, 0, idName, 0, this.fIdCount);
            this.fIdName = idName;
            final int[] idElement = new int[idName.length];
            System.arraycopy(this.fIdElement, 0, idElement, 0, this.fIdCount);
            this.fIdElement = idElement;
        }
        this.fIdName[this.fIdCount] = name;
        this.fIdElement[this.fIdCount] = elementNodeIndex;
        ++this.fIdCount;
    }
    
    public void print() {
    }
    
    @Override
    public int getNodeIndex() {
        return 0;
    }
    
    @Override
    protected void synchronizeData() {
        this.needsSyncData(false);
        if (this.fIdElement != null) {
            final IntVector path = new IntVector();
            for (int i = 0; i < this.fIdCount; ++i) {
                final int elementNodeIndex = this.fIdElement[i];
                String idName = this.fIdName[i];
                if (idName != null) {
                    path.removeAllElements();
                    int index = elementNodeIndex;
                    do {
                        path.addElement(index);
                        final int pchunk = index >> 8;
                        final int pindex = index & 0xFF;
                        index = this.getChunkIndex(this.fNodeParent, pchunk, pindex);
                    } while (index != -1);
                    Node place = this;
                    for (int j = path.size() - 2; j >= 0; --j) {
                        index = path.elementAt(j);
                        for (Node child = place.getLastChild(); child != null; child = child.getPreviousSibling()) {
                            if (child instanceof DeferredNode) {
                                final int nodeIndex = ((DeferredNode)child).getNodeIndex();
                                if (nodeIndex == index) {
                                    place = child;
                                    break;
                                }
                            }
                        }
                    }
                    final Element element = (Element)place;
                    this.putIdentifier0(idName, element);
                    this.fIdName[i] = null;
                    while (i + 1 < this.fIdCount && this.fIdElement[i + 1] == elementNodeIndex) {
                        idName = this.fIdName[++i];
                        if (idName == null) {
                            continue;
                        }
                        this.putIdentifier0(idName, element);
                    }
                }
            }
        }
    }
    
    @Override
    protected void synchronizeChildren() {
        if (this.needsSyncData()) {
            this.synchronizeData();
            if (!this.needsSyncChildren()) {
                return;
            }
        }
        final boolean orig = this.mutationEvents;
        this.needsSyncChildren(this.mutationEvents = false);
        this.getNodeType(0);
        ChildNode first = null;
        ChildNode last = null;
        for (int index = this.getLastChild(0); index != -1; index = this.getPrevSibling(index)) {
            final ChildNode node = (ChildNode)this.getNodeObject(index);
            if (last == null) {
                last = node;
            }
            else {
                first.previousSibling = node;
            }
            node.ownerNode = this;
            node.isOwned(true);
            node.nextSibling = first;
            first = node;
            final int type = node.getNodeType();
            if (type == 1) {
                this.docElement = (ElementImpl)node;
            }
            else if (type == 10) {
                this.docType = (DocumentTypeImpl)node;
            }
        }
        if (first != null) {
            (this.firstChild = first).isFirstChild(true);
            this.lastChild(last);
        }
        this.mutationEvents = orig;
    }
    
    protected final void synchronizeChildren(final AttrImpl a, final int nodeIndex) {
        final boolean orig = this.getMutationEvents();
        this.setMutationEvents(false);
        a.needsSyncChildren(false);
        final int last = this.getLastChild(nodeIndex);
        final int prev = this.getPrevSibling(last);
        if (prev == -1) {
            a.value = this.getNodeValueString(nodeIndex);
            a.hasStringValue(true);
        }
        else {
            ChildNode firstNode = null;
            ChildNode lastNode = null;
            for (int index = last; index != -1; index = this.getPrevSibling(index)) {
                final ChildNode node = (ChildNode)this.getNodeObject(index);
                if (lastNode == null) {
                    lastNode = node;
                }
                else {
                    firstNode.previousSibling = node;
                }
                node.ownerNode = a;
                node.isOwned(true);
                node.nextSibling = firstNode;
                firstNode = node;
            }
            if (lastNode != null) {
                ((NodeImpl)(a.value = firstNode)).isFirstChild(true);
                a.lastChild(lastNode);
            }
            a.hasStringValue(false);
        }
        this.setMutationEvents(orig);
    }
    
    protected final void synchronizeChildren(final ParentNode p, final int nodeIndex) {
        final boolean orig = this.getMutationEvents();
        this.setMutationEvents(false);
        p.needsSyncChildren(false);
        ChildNode firstNode = null;
        ChildNode lastNode = null;
        for (int index = this.getLastChild(nodeIndex); index != -1; index = this.getPrevSibling(index)) {
            final ChildNode node = (ChildNode)this.getNodeObject(index);
            if (lastNode == null) {
                lastNode = node;
            }
            else {
                firstNode.previousSibling = node;
            }
            node.ownerNode = p;
            node.isOwned(true);
            node.nextSibling = firstNode;
            firstNode = node;
        }
        if (lastNode != null) {
            (p.firstChild = firstNode).isFirstChild(true);
            p.lastChild(lastNode);
        }
        this.setMutationEvents(orig);
    }
    
    protected void ensureCapacity(final int chunk) {
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
        else if (this.fNodeType.length <= chunk) {
            final int newsize = chunk * 2;
            int[][] newArray = new int[newsize][];
            System.arraycopy(this.fNodeType, 0, newArray, 0, chunk);
            this.fNodeType = newArray;
            Object[][] newStrArray = new Object[newsize][];
            System.arraycopy(this.fNodeName, 0, newStrArray, 0, chunk);
            this.fNodeName = newStrArray;
            newStrArray = new Object[newsize][];
            System.arraycopy(this.fNodeValue, 0, newStrArray, 0, chunk);
            this.fNodeValue = newStrArray;
            newArray = new int[newsize][];
            System.arraycopy(this.fNodeParent, 0, newArray, 0, chunk);
            this.fNodeParent = newArray;
            newArray = new int[newsize][];
            System.arraycopy(this.fNodeLastChild, 0, newArray, 0, chunk);
            this.fNodeLastChild = newArray;
            newArray = new int[newsize][];
            System.arraycopy(this.fNodePrevSib, 0, newArray, 0, chunk);
            this.fNodePrevSib = newArray;
            newStrArray = new Object[newsize][];
            System.arraycopy(this.fNodeURI, 0, newStrArray, 0, chunk);
            this.fNodeURI = newStrArray;
            newArray = new int[newsize][];
            System.arraycopy(this.fNodeExtra, 0, newArray, 0, chunk);
            this.fNodeExtra = newArray;
        }
        else if (this.fNodeType[chunk] != null) {
            return;
        }
        this.createChunk(this.fNodeType, chunk);
        this.createChunk(this.fNodeName, chunk);
        this.createChunk(this.fNodeValue, chunk);
        this.createChunk(this.fNodeParent, chunk);
        this.createChunk(this.fNodeLastChild, chunk);
        this.createChunk(this.fNodePrevSib, chunk);
        this.createChunk(this.fNodeURI, chunk);
        this.createChunk(this.fNodeExtra, chunk);
    }
    
    protected int createNode(final short nodeType) {
        final int chunk = this.fNodeCount >> 8;
        final int index = this.fNodeCount & 0xFF;
        this.ensureCapacity(chunk);
        this.setChunkIndex(this.fNodeType, nodeType, chunk, index);
        return this.fNodeCount++;
    }
    
    protected static int binarySearch(final int[] values, int start, int end, final int target) {
        while (start <= end) {
            int middle = start + end >>> 1;
            final int value = values[middle];
            if (value == target) {
                while (middle > 0 && values[middle - 1] == target) {
                    --middle;
                }
                return middle;
            }
            if (value > target) {
                end = middle - 1;
            }
            else {
                start = middle + 1;
            }
        }
        return -1;
    }
    
    private final void createChunk(final int[][] data, final int chunk) {
        data[chunk] = new int[257];
        System.arraycopy(DeferredDocumentImpl.INIT_ARRAY, 0, data[chunk], 0, 256);
    }
    
    private final void createChunk(final Object[][] data, final int chunk) {
        (data[chunk] = new Object[257])[256] = new RefCount();
    }
    
    private final int setChunkIndex(final int[][] data, final int value, final int chunk, final int index) {
        if (value == -1) {
            return this.clearChunkIndex(data, chunk, index);
        }
        int[] dataChunk = data[chunk];
        if (dataChunk == null) {
            this.createChunk(data, chunk);
            dataChunk = data[chunk];
        }
        final int ovalue = dataChunk[index];
        if (ovalue == -1) {
            final int[] array = dataChunk;
            final int n = 256;
            ++array[n];
        }
        dataChunk[index] = value;
        return ovalue;
    }
    
    private final String setChunkValue(final Object[][] data, final Object value, final int chunk, final int index) {
        if (value == null) {
            return this.clearChunkValue(data, chunk, index);
        }
        Object[] dataChunk = data[chunk];
        if (dataChunk == null) {
            this.createChunk(data, chunk);
            dataChunk = data[chunk];
        }
        final String ovalue = (String)dataChunk[index];
        if (ovalue == null) {
            final RefCount refCount;
            final RefCount c = refCount = (RefCount)dataChunk[256];
            ++refCount.fCount;
        }
        dataChunk[index] = value;
        return ovalue;
    }
    
    private final int getChunkIndex(final int[][] data, final int chunk, final int index) {
        return (data[chunk] != null) ? data[chunk][index] : -1;
    }
    
    private final String getChunkValue(final Object[][] data, final int chunk, final int index) {
        return (data[chunk] != null) ? ((String)data[chunk][index]) : null;
    }
    
    private final String getNodeValue(final int chunk, final int index) {
        final Object data = this.fNodeValue[chunk][index];
        if (data == null) {
            return null;
        }
        if (data instanceof String) {
            return (String)data;
        }
        return data.toString();
    }
    
    private final int clearChunkIndex(final int[][] data, final int chunk, final int index) {
        final int value = (data[chunk] != null) ? data[chunk][index] : -1;
        if (value != -1) {
            final int[] array = data[chunk];
            final int n = 256;
            --array[n];
            data[chunk][index] = -1;
            if (data[chunk][256] == 0) {
                data[chunk] = null;
            }
        }
        return value;
    }
    
    private final String clearChunkValue(final Object[][] data, final int chunk, final int index) {
        final String value = (data[chunk] != null) ? ((String)data[chunk][index]) : null;
        if (value != null) {
            data[chunk][index] = null;
            final RefCount refCount;
            final RefCount c = refCount = (RefCount)data[chunk][256];
            --refCount.fCount;
            if (c.fCount == 0) {
                data[chunk] = null;
            }
        }
        return value;
    }
    
    private final void putIdentifier0(final String idName, final Element element) {
        if (this.identifiers == null) {
            this.identifiers = new HashMap<String, Node>();
        }
        this.identifiers.put(idName, element);
    }
    
    private static void print(final int[] values, final int start, final int end, final int middle, final int target) {
    }
    
    static {
        INIT_ARRAY = new int[257];
        for (int i = 0; i < 256; ++i) {
            DeferredDocumentImpl.INIT_ARRAY[i] = -1;
        }
    }
    
    static final class RefCount
    {
        int fCount;
    }
    
    static final class IntVector
    {
        private int[] data;
        private int size;
        
        public int size() {
            return this.size;
        }
        
        public int elementAt(final int index) {
            return this.data[index];
        }
        
        public void addElement(final int element) {
            this.ensureCapacity(this.size + 1);
            this.data[this.size++] = element;
        }
        
        public void removeAllElements() {
            this.size = 0;
        }
        
        private void ensureCapacity(final int newsize) {
            if (this.data == null) {
                this.data = new int[newsize + 15];
            }
            else if (newsize > this.data.length) {
                final int[] newdata = new int[newsize + 15];
                System.arraycopy(this.data, 0, newdata, 0, this.data.length);
                this.data = newdata;
            }
        }
    }
}
