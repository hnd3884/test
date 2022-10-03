package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.util.Map;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.DOM;

public final class DOMAdapter implements DOM
{
    private DOMEnhancedForDTM _enhancedDOM;
    private DOM _dom;
    private String[] _namesArray;
    private String[] _urisArray;
    private int[] _typesArray;
    private String[] _namespaceArray;
    private short[] _mapping;
    private int[] _reverse;
    private short[] _NSmapping;
    private short[] _NSreverse;
    private int _multiDOMMask;
    
    public DOMAdapter(final DOM dom, final String[] namesArray, final String[] urisArray, final int[] typesArray, final String[] namespaceArray) {
        this._mapping = null;
        this._reverse = null;
        this._NSmapping = null;
        this._NSreverse = null;
        if (dom instanceof DOMEnhancedForDTM) {
            this._enhancedDOM = (DOMEnhancedForDTM)dom;
        }
        this._dom = dom;
        this._namesArray = namesArray;
        this._urisArray = urisArray;
        this._typesArray = typesArray;
        this._namespaceArray = namespaceArray;
    }
    
    @Override
    public void setupMapping(final String[] names, final String[] urisArray, final int[] typesArray, final String[] namespaces) {
        this._namesArray = names;
        this._urisArray = urisArray;
        this._typesArray = typesArray;
        this._namespaceArray = namespaces;
    }
    
    public String[] getNamesArray() {
        return this._namesArray;
    }
    
    public String[] getUrisArray() {
        return this._urisArray;
    }
    
    public int[] getTypesArray() {
        return this._typesArray;
    }
    
    public String[] getNamespaceArray() {
        return this._namespaceArray;
    }
    
    public DOM getDOMImpl() {
        return this._dom;
    }
    
    private short[] getMapping() {
        if (this._mapping == null && this._enhancedDOM != null) {
            this._mapping = this._enhancedDOM.getMapping(this._namesArray, this._urisArray, this._typesArray);
        }
        return this._mapping;
    }
    
    private int[] getReverse() {
        if (this._reverse == null && this._enhancedDOM != null) {
            this._reverse = this._enhancedDOM.getReverseMapping(this._namesArray, this._urisArray, this._typesArray);
        }
        return this._reverse;
    }
    
    private short[] getNSMapping() {
        if (this._NSmapping == null && this._enhancedDOM != null) {
            this._NSmapping = this._enhancedDOM.getNamespaceMapping(this._namespaceArray);
        }
        return this._NSmapping;
    }
    
    private short[] getNSReverse() {
        if (this._NSreverse == null && this._enhancedDOM != null) {
            this._NSreverse = this._enhancedDOM.getReverseNamespaceMapping(this._namespaceArray);
        }
        return this._NSreverse;
    }
    
    @Override
    public DTMAxisIterator getIterator() {
        return this._dom.getIterator();
    }
    
    @Override
    public String getStringValue() {
        return this._dom.getStringValue();
    }
    
    @Override
    public DTMAxisIterator getChildren(final int node) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getChildren(node);
        }
        final DTMAxisIterator iterator = this._dom.getChildren(node);
        return iterator.setStartNode(node);
    }
    
    @Override
    public void setFilter(final StripFilter filter) {
    }
    
    @Override
    public DTMAxisIterator getTypedChildren(final int type) {
        final int[] reverse = this.getReverse();
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getTypedChildren(reverse[type]);
        }
        return this._dom.getTypedChildren(type);
    }
    
    @Override
    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns) {
        return this._dom.getNamespaceAxisIterator(axis, this.getNSReverse()[ns]);
    }
    
    @Override
    public DTMAxisIterator getAxisIterator(final int axis) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getAxisIterator(axis);
        }
        return this._dom.getAxisIterator(axis);
    }
    
    @Override
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type) {
        final int[] reverse = this.getReverse();
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getTypedAxisIterator(axis, reverse[type]);
        }
        return this._dom.getTypedAxisIterator(axis, type);
    }
    
    public int getMultiDOMMask() {
        return this._multiDOMMask;
    }
    
    public void setMultiDOMMask(final int mask) {
        this._multiDOMMask = mask;
    }
    
    @Override
    public DTMAxisIterator getNthDescendant(final int type, final int n, final boolean includeself) {
        return this._dom.getNthDescendant(this.getReverse()[type], n, includeself);
    }
    
    @Override
    public DTMAxisIterator getNodeValueIterator(final DTMAxisIterator iterator, final int type, final String value, final boolean op) {
        return this._dom.getNodeValueIterator(iterator, type, value, op);
    }
    
    @Override
    public DTMAxisIterator orderNodes(final DTMAxisIterator source, final int node) {
        return this._dom.orderNodes(source, node);
    }
    
    @Override
    public int getExpandedTypeID(final int node) {
        final short[] mapping = this.getMapping();
        int type;
        if (this._enhancedDOM != null) {
            type = mapping[this._enhancedDOM.getExpandedTypeID2(node)];
        }
        else if (null != mapping) {
            type = mapping[this._dom.getExpandedTypeID(node)];
        }
        else {
            type = this._dom.getExpandedTypeID(node);
        }
        return type;
    }
    
    @Override
    public int getNamespaceType(final int node) {
        return this.getNSMapping()[this._dom.getNSType(node)];
    }
    
    @Override
    public int getNSType(final int node) {
        return this._dom.getNSType(node);
    }
    
    @Override
    public int getParent(final int node) {
        return this._dom.getParent(node);
    }
    
    @Override
    public int getAttributeNode(final int type, final int element) {
        return this._dom.getAttributeNode(this.getReverse()[type], element);
    }
    
    @Override
    public String getNodeName(final int node) {
        if (node == -1) {
            return "";
        }
        return this._dom.getNodeName(node);
    }
    
    @Override
    public String getNodeNameX(final int node) {
        if (node == -1) {
            return "";
        }
        return this._dom.getNodeNameX(node);
    }
    
    @Override
    public String getNamespaceName(final int node) {
        if (node == -1) {
            return "";
        }
        return this._dom.getNamespaceName(node);
    }
    
    @Override
    public String getStringValueX(final int node) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getStringValueX(node);
        }
        if (node == -1) {
            return "";
        }
        return this._dom.getStringValueX(node);
    }
    
    @Override
    public void copy(final int node, final SerializationHandler handler) throws TransletException {
        this._dom.copy(node, handler);
    }
    
    @Override
    public void copy(final DTMAxisIterator nodes, final SerializationHandler handler) throws TransletException {
        this._dom.copy(nodes, handler);
    }
    
    @Override
    public String shallowCopy(final int node, final SerializationHandler handler) throws TransletException {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.shallowCopy(node, handler);
        }
        return this._dom.shallowCopy(node, handler);
    }
    
    @Override
    public boolean lessThan(final int node1, final int node2) {
        return this._dom.lessThan(node1, node2);
    }
    
    @Override
    public void characters(final int textNode, final SerializationHandler handler) throws TransletException {
        if (this._enhancedDOM != null) {
            this._enhancedDOM.characters(textNode, handler);
        }
        else {
            this._dom.characters(textNode, handler);
        }
    }
    
    @Override
    public Node makeNode(final int index) {
        return this._dom.makeNode(index);
    }
    
    @Override
    public Node makeNode(final DTMAxisIterator iter) {
        return this._dom.makeNode(iter);
    }
    
    @Override
    public NodeList makeNodeList(final int index) {
        return this._dom.makeNodeList(index);
    }
    
    @Override
    public NodeList makeNodeList(final DTMAxisIterator iter) {
        return this._dom.makeNodeList(iter);
    }
    
    @Override
    public String getLanguage(final int node) {
        return this._dom.getLanguage(node);
    }
    
    @Override
    public int getSize() {
        return this._dom.getSize();
    }
    
    public void setDocumentURI(final String uri) {
        if (this._enhancedDOM != null) {
            this._enhancedDOM.setDocumentURI(uri);
        }
    }
    
    public String getDocumentURI() {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getDocumentURI();
        }
        return "";
    }
    
    @Override
    public String getDocumentURI(final int node) {
        return this._dom.getDocumentURI(node);
    }
    
    @Override
    public int getDocument() {
        return this._dom.getDocument();
    }
    
    @Override
    public boolean isElement(final int node) {
        return this._dom.isElement(node);
    }
    
    @Override
    public boolean isAttribute(final int node) {
        return this._dom.isAttribute(node);
    }
    
    @Override
    public int getNodeIdent(final int nodeHandle) {
        return this._dom.getNodeIdent(nodeHandle);
    }
    
    @Override
    public int getNodeHandle(final int nodeId) {
        return this._dom.getNodeHandle(nodeId);
    }
    
    @Override
    public DOM getResultTreeFrag(final int initSize, final int rtfType) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getResultTreeFrag(initSize, rtfType);
        }
        return this._dom.getResultTreeFrag(initSize, rtfType);
    }
    
    @Override
    public DOM getResultTreeFrag(final int initSize, final int rtfType, final boolean addToManager) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getResultTreeFrag(initSize, rtfType, addToManager);
        }
        return this._dom.getResultTreeFrag(initSize, rtfType, addToManager);
    }
    
    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this._dom.getOutputDomBuilder();
    }
    
    @Override
    public String lookupNamespace(final int node, final String prefix) throws TransletException {
        return this._dom.lookupNamespace(node, prefix);
    }
    
    @Override
    public String getUnparsedEntityURI(final String entity) {
        return this._dom.getUnparsedEntityURI(entity);
    }
    
    @Override
    public Map<String, Integer> getElementsWithIDs() {
        return this._dom.getElementsWithIDs();
    }
    
    @Override
    public void release() {
        this._dom.release();
    }
}
