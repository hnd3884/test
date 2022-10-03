package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBase;
import java.util.HashMap;
import java.util.Map;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xalan.internal.xsltc.DOM;

public final class MultiDOM implements DOM
{
    private static final int NO_TYPE = -2;
    private static final int INITIAL_SIZE = 4;
    private DOM[] _adapters;
    private DOMAdapter _main;
    private DTMManager _dtmManager;
    private int _free;
    private int _size;
    private Map<String, Integer> _documents;
    
    public MultiDOM(final DOM main) {
        this._documents = new HashMap<String, Integer>();
        this._size = 4;
        this._free = 1;
        this._adapters = new DOM[4];
        final DOMAdapter adapter = (DOMAdapter)main;
        this._adapters[0] = adapter;
        this._main = adapter;
        final DOM dom = adapter.getDOMImpl();
        if (dom instanceof DTMDefaultBase) {
            this._dtmManager = ((DTMDefaultBase)dom).getManager();
        }
        this.addDOMAdapter(adapter, false);
    }
    
    public int nextMask() {
        return this._free;
    }
    
    @Override
    public void setupMapping(final String[] names, final String[] uris, final int[] types, final String[] namespaces) {
    }
    
    public int addDOMAdapter(final DOMAdapter adapter) {
        return this.addDOMAdapter(adapter, true);
    }
    
    private int addDOMAdapter(final DOMAdapter adapter, final boolean indexByURI) {
        final DOM dom = adapter.getDOMImpl();
        int domNo = 1;
        int dtmSize = 1;
        SuballocatedIntVector dtmIds = null;
        if (dom instanceof DTMDefaultBase) {
            final DTMDefaultBase dtmdb = (DTMDefaultBase)dom;
            dtmIds = dtmdb.getDTMIDs();
            dtmSize = dtmIds.size();
            domNo = dtmIds.elementAt(dtmSize - 1) >>> 16;
        }
        else if (dom instanceof SimpleResultTreeImpl) {
            final SimpleResultTreeImpl simpleRTF = (SimpleResultTreeImpl)dom;
            domNo = simpleRTF.getDocument() >>> 16;
        }
        if (domNo >= this._size) {
            final int oldSize = this._size;
            do {
                this._size *= 2;
            } while (this._size <= domNo);
            final DOMAdapter[] newArray = new DOMAdapter[this._size];
            System.arraycopy(this._adapters, 0, newArray, 0, oldSize);
            this._adapters = newArray;
        }
        this._free = domNo + 1;
        if (dtmSize == 1) {
            this._adapters[domNo] = adapter;
        }
        else if (dtmIds != null) {
            int domPos = 0;
            for (int i = dtmSize - 1; i >= 0; --i) {
                domPos = dtmIds.elementAt(i) >>> 16;
                this._adapters[domPos] = adapter;
            }
            domNo = domPos;
        }
        if (indexByURI) {
            final String uri = adapter.getDocumentURI(0);
            this._documents.put(uri, domNo);
        }
        if (dom instanceof AdaptiveResultTreeImpl) {
            final AdaptiveResultTreeImpl adaptiveRTF = (AdaptiveResultTreeImpl)dom;
            final DOM nestedDom = adaptiveRTF.getNestedDOM();
            if (nestedDom != null) {
                final DOMAdapter newAdapter = new DOMAdapter(nestedDom, adapter.getNamesArray(), adapter.getUrisArray(), adapter.getTypesArray(), adapter.getNamespaceArray());
                this.addDOMAdapter(newAdapter);
            }
        }
        return domNo;
    }
    
    public int getDocumentMask(final String uri) {
        final Integer domIdx = this._documents.get(uri);
        if (domIdx == null) {
            return -1;
        }
        return domIdx;
    }
    
    public DOM getDOMAdapter(final String uri) {
        final Integer domIdx = this._documents.get(uri);
        if (domIdx == null) {
            return null;
        }
        return this._adapters[domIdx];
    }
    
    @Override
    public int getDocument() {
        return this._main.getDocument();
    }
    
    public DTMManager getDTMManager() {
        return this._dtmManager;
    }
    
    @Override
    public DTMAxisIterator getIterator() {
        return this._main.getIterator();
    }
    
    @Override
    public String getStringValue() {
        return this._main.getStringValue();
    }
    
    @Override
    public DTMAxisIterator getChildren(final int node) {
        return this._adapters[this.getDTMId(node)].getChildren(node);
    }
    
    @Override
    public DTMAxisIterator getTypedChildren(final int type) {
        return new AxisIterator(3, type);
    }
    
    @Override
    public DTMAxisIterator getAxisIterator(final int axis) {
        return new AxisIterator(axis, -2);
    }
    
    @Override
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type) {
        return new AxisIterator(axis, type);
    }
    
    @Override
    public DTMAxisIterator getNthDescendant(final int node, final int n, final boolean includeself) {
        return this._adapters[this.getDTMId(node)].getNthDescendant(node, n, includeself);
    }
    
    @Override
    public DTMAxisIterator getNodeValueIterator(final DTMAxisIterator iterator, final int type, final String value, final boolean op) {
        return new NodeValueIterator(iterator, type, value, op);
    }
    
    @Override
    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns) {
        final DTMAxisIterator iterator = this._main.getNamespaceAxisIterator(axis, ns);
        return iterator;
    }
    
    @Override
    public DTMAxisIterator orderNodes(final DTMAxisIterator source, final int node) {
        return this._adapters[this.getDTMId(node)].orderNodes(source, node);
    }
    
    @Override
    public int getExpandedTypeID(final int node) {
        if (node != -1) {
            return this._adapters[node >>> 16].getExpandedTypeID(node);
        }
        return -1;
    }
    
    @Override
    public int getNamespaceType(final int node) {
        return this._adapters[this.getDTMId(node)].getNamespaceType(node);
    }
    
    @Override
    public int getNSType(final int node) {
        return this._adapters[this.getDTMId(node)].getNSType(node);
    }
    
    @Override
    public int getParent(final int node) {
        if (node == -1) {
            return -1;
        }
        return this._adapters[node >>> 16].getParent(node);
    }
    
    @Override
    public int getAttributeNode(final int type, final int el) {
        if (el == -1) {
            return -1;
        }
        return this._adapters[el >>> 16].getAttributeNode(type, el);
    }
    
    @Override
    public String getNodeName(final int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getNodeName(node);
    }
    
    @Override
    public String getNodeNameX(final int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getNodeNameX(node);
    }
    
    @Override
    public String getNamespaceName(final int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getNamespaceName(node);
    }
    
    @Override
    public String getStringValueX(final int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getStringValueX(node);
    }
    
    @Override
    public void copy(final int node, final SerializationHandler handler) throws TransletException {
        if (node != -1) {
            this._adapters[node >>> 16].copy(node, handler);
        }
    }
    
    @Override
    public void copy(final DTMAxisIterator nodes, final SerializationHandler handler) throws TransletException {
        int node;
        while ((node = nodes.next()) != -1) {
            this._adapters[node >>> 16].copy(node, handler);
        }
    }
    
    @Override
    public String shallowCopy(final int node, final SerializationHandler handler) throws TransletException {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].shallowCopy(node, handler);
    }
    
    @Override
    public boolean lessThan(final int node1, final int node2) {
        if (node1 == -1) {
            return true;
        }
        if (node2 == -1) {
            return false;
        }
        final int dom1 = this.getDTMId(node1);
        final int dom2 = this.getDTMId(node2);
        return (dom1 == dom2) ? this._adapters[dom1].lessThan(node1, node2) : (dom1 < dom2);
    }
    
    @Override
    public void characters(final int textNode, final SerializationHandler handler) throws TransletException {
        if (textNode != -1) {
            this._adapters[textNode >>> 16].characters(textNode, handler);
        }
    }
    
    @Override
    public void setFilter(final StripFilter filter) {
        for (int dom = 0; dom < this._free; ++dom) {
            if (this._adapters[dom] != null) {
                this._adapters[dom].setFilter(filter);
            }
        }
    }
    
    @Override
    public Node makeNode(final int index) {
        if (index == -1) {
            return null;
        }
        return this._adapters[this.getDTMId(index)].makeNode(index);
    }
    
    @Override
    public Node makeNode(final DTMAxisIterator iter) {
        return this._main.makeNode(iter);
    }
    
    @Override
    public NodeList makeNodeList(final int index) {
        if (index == -1) {
            return null;
        }
        return this._adapters[this.getDTMId(index)].makeNodeList(index);
    }
    
    @Override
    public NodeList makeNodeList(final DTMAxisIterator iter) {
        final int index = iter.next();
        if (index == -1) {
            return new DTMAxisIterNodeList(null, null);
        }
        iter.reset();
        return this._adapters[this.getDTMId(index)].makeNodeList(iter);
    }
    
    @Override
    public String getLanguage(final int node) {
        return this._adapters[this.getDTMId(node)].getLanguage(node);
    }
    
    @Override
    public int getSize() {
        int size = 0;
        for (int i = 0; i < this._size; ++i) {
            size += this._adapters[i].getSize();
        }
        return size;
    }
    
    @Override
    public String getDocumentURI(int node) {
        if (node == -1) {
            node = 0;
        }
        return this._adapters[node >>> 16].getDocumentURI(0);
    }
    
    @Override
    public boolean isElement(final int node) {
        return node != -1 && this._adapters[node >>> 16].isElement(node);
    }
    
    @Override
    public boolean isAttribute(final int node) {
        return node != -1 && this._adapters[node >>> 16].isAttribute(node);
    }
    
    public int getDTMId(final int nodeHandle) {
        if (nodeHandle == -1) {
            return 0;
        }
        int id;
        for (id = nodeHandle >>> 16; id >= 2 && this._adapters[id] == this._adapters[id - 1]; --id) {}
        return id;
    }
    
    public DOM getDTM(final int nodeHandle) {
        return this._adapters[this.getDTMId(nodeHandle)];
    }
    
    @Override
    public int getNodeIdent(final int nodeHandle) {
        return this._adapters[nodeHandle >>> 16].getNodeIdent(nodeHandle);
    }
    
    @Override
    public int getNodeHandle(final int nodeId) {
        return this._main.getNodeHandle(nodeId);
    }
    
    @Override
    public DOM getResultTreeFrag(final int initSize, final int rtfType) {
        return this._main.getResultTreeFrag(initSize, rtfType);
    }
    
    @Override
    public DOM getResultTreeFrag(final int initSize, final int rtfType, final boolean addToManager) {
        return this._main.getResultTreeFrag(initSize, rtfType, addToManager);
    }
    
    public DOM getMain() {
        return this._main;
    }
    
    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this._main.getOutputDomBuilder();
    }
    
    @Override
    public String lookupNamespace(final int node, final String prefix) throws TransletException {
        return this._main.lookupNamespace(node, prefix);
    }
    
    @Override
    public String getUnparsedEntityURI(final String entity) {
        return this._main.getUnparsedEntityURI(entity);
    }
    
    @Override
    public Map<String, Integer> getElementsWithIDs() {
        return this._main.getElementsWithIDs();
    }
    
    @Override
    public void release() {
        this._main.release();
    }
    
    private boolean isMatchingAdapterEntry(final DOM entry, final DOMAdapter adapter) {
        final DOM dom = adapter.getDOMImpl();
        return entry == adapter || (dom instanceof AdaptiveResultTreeImpl && entry instanceof DOMAdapter && ((AdaptiveResultTreeImpl)dom).getNestedDOM() == ((DOMAdapter)entry).getDOMImpl());
    }
    
    public void removeDOMAdapter(final DOMAdapter adapter) {
        this._documents.remove(adapter.getDocumentURI(0));
        final DOM dom = adapter.getDOMImpl();
        if (dom instanceof DTMDefaultBase) {
            final SuballocatedIntVector ids = ((DTMDefaultBase)dom).getDTMIDs();
            for (int idsSize = ids.size(), i = 0; i < idsSize; ++i) {
                this._adapters[ids.elementAt(i) >>> 16] = null;
            }
        }
        else {
            final int id = dom.getDocument() >>> 16;
            if (id > 0 && id < this._adapters.length && this.isMatchingAdapterEntry(this._adapters[id], adapter)) {
                this._adapters[id] = null;
            }
            else {
                boolean found = false;
                for (int i = 0; i < this._adapters.length; ++i) {
                    if (this.isMatchingAdapterEntry(this._adapters[id], adapter)) {
                        this._adapters[i] = null;
                        found = true;
                        break;
                    }
                }
            }
        }
    }
    
    private final class AxisIterator extends DTMAxisIteratorBase
    {
        private final int _axis;
        private final int _type;
        private DTMAxisIterator _source;
        private int _dtmId;
        
        public AxisIterator(final int axis, final int type) {
            this._dtmId = -1;
            this._axis = axis;
            this._type = type;
        }
        
        @Override
        public int next() {
            if (this._source == null) {
                return -1;
            }
            return this._source.next();
        }
        
        @Override
        public void setRestartable(final boolean flag) {
            if (this._source != null) {
                this._source.setRestartable(flag);
            }
        }
        
        @Override
        public DTMAxisIterator setStartNode(final int node) {
            if (node == -1) {
                return this;
            }
            final int dom = node >>> 16;
            if (this._source == null || this._dtmId != dom) {
                if (this._type == -2) {
                    this._source = MultiDOM.this._adapters[dom].getAxisIterator(this._axis);
                }
                else if (this._axis == 3) {
                    this._source = MultiDOM.this._adapters[dom].getTypedChildren(this._type);
                }
                else {
                    this._source = MultiDOM.this._adapters[dom].getTypedAxisIterator(this._axis, this._type);
                }
            }
            this._dtmId = dom;
            this._source.setStartNode(node);
            return this;
        }
        
        @Override
        public DTMAxisIterator reset() {
            if (this._source != null) {
                this._source.reset();
            }
            return this;
        }
        
        @Override
        public int getLast() {
            if (this._source != null) {
                return this._source.getLast();
            }
            return -1;
        }
        
        @Override
        public int getPosition() {
            if (this._source != null) {
                return this._source.getPosition();
            }
            return -1;
        }
        
        @Override
        public boolean isReverse() {
            return Axis.isReverse(this._axis);
        }
        
        @Override
        public void setMark() {
            if (this._source != null) {
                this._source.setMark();
            }
        }
        
        @Override
        public void gotoMark() {
            if (this._source != null) {
                this._source.gotoMark();
            }
        }
        
        @Override
        public DTMAxisIterator cloneIterator() {
            final AxisIterator clone = new AxisIterator(this._axis, this._type);
            if (this._source != null) {
                clone._source = this._source.cloneIterator();
            }
            clone._dtmId = this._dtmId;
            return clone;
        }
    }
    
    private final class NodeValueIterator extends DTMAxisIteratorBase
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
                clone._source = this._source.cloneIterator();
                clone.setRestartable(false);
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
                final String val = MultiDOM.this.getStringValueX(node);
                if (this._value.equals(val) == this._op) {
                    if (this._returnType == 0) {
                        return this.returnNode(node);
                    }
                    return this.returnNode(MultiDOM.this.getParent(node));
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
}
