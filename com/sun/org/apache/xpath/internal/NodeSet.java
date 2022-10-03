package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.DOM2Helper;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import com.sun.org.apache.xpath.internal.axes.ContextNodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.NodeList;

public class NodeSet implements NodeList, NodeIterator, Cloneable, ContextNodeList
{
    protected transient int m_next;
    protected transient boolean m_mutable;
    protected transient boolean m_cacheNodes;
    private transient int m_last;
    private int m_blocksize;
    Node[] m_map;
    protected int m_firstFree;
    private int m_mapSize;
    
    public NodeSet() {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_last = 0;
        this.m_firstFree = 0;
        this.m_blocksize = 32;
        this.m_mapSize = 0;
    }
    
    public NodeSet(final int blocksize) {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_last = 0;
        this.m_firstFree = 0;
        this.m_blocksize = blocksize;
        this.m_mapSize = 0;
    }
    
    public NodeSet(final NodeList nodelist) {
        this(32);
        this.addNodes(nodelist);
    }
    
    public NodeSet(final NodeSet nodelist) {
        this(32);
        this.addNodes((NodeIterator)nodelist);
    }
    
    public NodeSet(final NodeIterator ni) {
        this(32);
        this.addNodes(ni);
    }
    
    public NodeSet(final Node node) {
        this(32);
        this.addNode(node);
    }
    
    @Override
    public Node getRoot() {
        return null;
    }
    
    @Override
    public NodeIterator cloneWithReset() throws CloneNotSupportedException {
        final NodeSet clone = (NodeSet)this.clone();
        clone.reset();
        return clone;
    }
    
    @Override
    public void reset() {
        this.m_next = 0;
    }
    
    @Override
    public int getWhatToShow() {
        return -17;
    }
    
    @Override
    public NodeFilter getFilter() {
        return null;
    }
    
    @Override
    public boolean getExpandEntityReferences() {
        return true;
    }
    
    @Override
    public Node nextNode() throws DOMException {
        if (this.m_next < this.size()) {
            final Node next = this.elementAt(this.m_next);
            ++this.m_next;
            return next;
        }
        return null;
    }
    
    @Override
    public Node previousNode() throws DOMException {
        if (!this.m_cacheNodes) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_CANNOT_ITERATE", null));
        }
        if (this.m_next - 1 > 0) {
            --this.m_next;
            return this.elementAt(this.m_next);
        }
        return null;
    }
    
    @Override
    public void detach() {
    }
    
    @Override
    public boolean isFresh() {
        return this.m_next == 0;
    }
    
    @Override
    public void runTo(final int index) {
        if (!this.m_cacheNodes) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null));
        }
        if (index >= 0 && this.m_next < this.m_firstFree) {
            this.m_next = index;
        }
        else {
            this.m_next = this.m_firstFree - 1;
        }
    }
    
    @Override
    public Node item(final int index) {
        this.runTo(index);
        return this.elementAt(index);
    }
    
    @Override
    public int getLength() {
        this.runTo(-1);
        return this.size();
    }
    
    public void addNode(final Node n) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        this.addElement(n);
    }
    
    public void insertNode(final Node n, final int pos) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        this.insertElementAt(n, pos);
    }
    
    public void removeNode(final Node n) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        this.removeElement(n);
    }
    
    public void addNodes(final NodeList nodelist) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        if (null != nodelist) {
            for (int nChildren = nodelist.getLength(), i = 0; i < nChildren; ++i) {
                final Node obj = nodelist.item(i);
                if (null != obj) {
                    this.addElement(obj);
                }
            }
        }
    }
    
    public void addNodes(final NodeSet ns) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        this.addNodes((NodeIterator)ns);
    }
    
    public void addNodes(final NodeIterator iterator) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        if (null != iterator) {
            Node obj;
            while (null != (obj = iterator.nextNode())) {
                this.addElement(obj);
            }
        }
    }
    
    public void addNodesInDocOrder(final NodeList nodelist, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        for (int nChildren = nodelist.getLength(), i = 0; i < nChildren; ++i) {
            final Node node = nodelist.item(i);
            if (null != node) {
                this.addNodeInDocOrder(node, support);
            }
        }
    }
    
    public void addNodesInDocOrder(final NodeIterator iterator, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        Node node;
        while (null != (node = iterator.nextNode())) {
            this.addNodeInDocOrder(node, support);
        }
    }
    
    private boolean addNodesInDocOrder(final int start, final int end, int testIndex, final NodeList nodelist, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        final boolean foundit = false;
        final Node node = nodelist.item(testIndex);
        int i = end;
        while (i >= start) {
            final Node child = this.elementAt(i);
            if (child == node) {
                i = -2;
                break;
            }
            if (!DOM2Helper.isNodeAfter(node, child)) {
                this.insertElementAt(node, i + 1);
                if (--testIndex > 0) {
                    final boolean foundPrev = this.addNodesInDocOrder(0, i, testIndex, nodelist, support);
                    if (!foundPrev) {
                        this.addNodesInDocOrder(i, this.size() - 1, testIndex, nodelist, support);
                    }
                    break;
                }
                break;
            }
            else {
                --i;
            }
        }
        if (i == -1) {
            this.insertElementAt(node, 0);
        }
        return foundit;
    }
    
    public int addNodeInDocOrder(final Node node, final boolean test, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        int insertIndex = -1;
        if (test) {
            final int size = this.size();
            int i;
            for (i = size - 1; i >= 0; --i) {
                final Node child = this.elementAt(i);
                if (child == node) {
                    i = -2;
                    break;
                }
                if (!DOM2Helper.isNodeAfter(node, child)) {
                    break;
                }
            }
            if (i != -2) {
                insertIndex = i + 1;
                this.insertElementAt(node, insertIndex);
            }
        }
        else {
            insertIndex = this.size();
            boolean foundit = false;
            for (int i = 0; i < insertIndex; ++i) {
                if (this.item(i).equals(node)) {
                    foundit = true;
                    break;
                }
            }
            if (!foundit) {
                this.addElement(node);
            }
        }
        return insertIndex;
    }
    
    public int addNodeInDocOrder(final Node node, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        return this.addNodeInDocOrder(node, true, support);
    }
    
    @Override
    public int getCurrentPos() {
        return this.m_next;
    }
    
    @Override
    public void setCurrentPos(final int i) {
        if (!this.m_cacheNodes) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null));
        }
        this.m_next = i;
    }
    
    @Override
    public Node getCurrentNode() {
        if (!this.m_cacheNodes) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null));
        }
        final int saved = this.m_next;
        final Node n = (this.m_next < this.m_firstFree) ? this.elementAt(this.m_next) : null;
        this.m_next = saved;
        return n;
    }
    
    public boolean getShouldCacheNodes() {
        return this.m_cacheNodes;
    }
    
    @Override
    public void setShouldCacheNodes(final boolean b) {
        if (!this.isFresh()) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_CANNOT_CALL_SETSHOULDCACHENODE", null));
        }
        this.m_cacheNodes = b;
        this.m_mutable = true;
    }
    
    @Override
    public int getLast() {
        return this.m_last;
    }
    
    @Override
    public void setLast(final int last) {
        this.m_last = last;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final NodeSet clone = (NodeSet)super.clone();
        if (null != this.m_map && this.m_map == clone.m_map) {
            clone.m_map = new Node[this.m_map.length];
            System.arraycopy(this.m_map, 0, clone.m_map, 0, this.m_map.length);
        }
        return clone;
    }
    
    @Override
    public int size() {
        return this.m_firstFree;
    }
    
    public void addElement(final Node value) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            if (null == this.m_map) {
                this.m_map = new Node[this.m_blocksize];
                this.m_mapSize = this.m_blocksize;
            }
            else {
                this.m_mapSize += this.m_blocksize;
                final Node[] newMap = new Node[this.m_mapSize];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
                this.m_map = newMap;
            }
        }
        this.m_map[this.m_firstFree] = value;
        ++this.m_firstFree;
    }
    
    public final void push(final Node value) {
        int ff = this.m_firstFree;
        if (ff + 1 >= this.m_mapSize) {
            if (null == this.m_map) {
                this.m_map = new Node[this.m_blocksize];
                this.m_mapSize = this.m_blocksize;
            }
            else {
                this.m_mapSize += this.m_blocksize;
                final Node[] newMap = new Node[this.m_mapSize];
                System.arraycopy(this.m_map, 0, newMap, 0, ff + 1);
                this.m_map = newMap;
            }
        }
        this.m_map[ff] = value;
        ++ff;
        this.m_firstFree = ff;
    }
    
    public final Node pop() {
        --this.m_firstFree;
        final Node n = this.m_map[this.m_firstFree];
        this.m_map[this.m_firstFree] = null;
        return n;
    }
    
    public final Node popAndTop() {
        --this.m_firstFree;
        this.m_map[this.m_firstFree] = null;
        return (this.m_firstFree == 0) ? null : this.m_map[this.m_firstFree - 1];
    }
    
    public final void popQuick() {
        --this.m_firstFree;
        this.m_map[this.m_firstFree] = null;
    }
    
    public final Node peepOrNull() {
        return (null != this.m_map && this.m_firstFree > 0) ? this.m_map[this.m_firstFree - 1] : null;
    }
    
    public final void pushPair(final Node v1, final Node v2) {
        if (null == this.m_map) {
            this.m_map = new Node[this.m_blocksize];
            this.m_mapSize = this.m_blocksize;
        }
        else if (this.m_firstFree + 2 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final Node[] newMap = new Node[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = v1;
        this.m_map[this.m_firstFree + 1] = v2;
        this.m_firstFree += 2;
    }
    
    public final void popPair() {
        this.m_firstFree -= 2;
        this.m_map[this.m_firstFree] = null;
        this.m_map[this.m_firstFree + 1] = null;
    }
    
    public final void setTail(final Node n) {
        this.m_map[this.m_firstFree - 1] = n;
    }
    
    public final void setTailSub1(final Node n) {
        this.m_map[this.m_firstFree - 2] = n;
    }
    
    public final Node peepTail() {
        return this.m_map[this.m_firstFree - 1];
    }
    
    public final Node peepTailSub1() {
        return this.m_map[this.m_firstFree - 2];
    }
    
    public void insertElementAt(final Node value, final int at) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        if (null == this.m_map) {
            this.m_map = new Node[this.m_blocksize];
            this.m_mapSize = this.m_blocksize;
        }
        else if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final Node[] newMap = new Node[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        if (at <= this.m_firstFree - 1) {
            System.arraycopy(this.m_map, at, this.m_map, at + 1, this.m_firstFree - at);
        }
        this.m_map[at] = value;
        ++this.m_firstFree;
    }
    
    public void appendNodes(final NodeSet nodes) {
        final int nNodes = nodes.size();
        if (null == this.m_map) {
            this.m_mapSize = nNodes + this.m_blocksize;
            this.m_map = new Node[this.m_mapSize];
        }
        else if (this.m_firstFree + nNodes >= this.m_mapSize) {
            this.m_mapSize += nNodes + this.m_blocksize;
            final Node[] newMap = new Node[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + nNodes);
            this.m_map = newMap;
        }
        System.arraycopy(nodes.m_map, 0, this.m_map, this.m_firstFree, nNodes);
        this.m_firstFree += nNodes;
    }
    
    public void removeAllElements() {
        if (null == this.m_map) {
            return;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            this.m_map[i] = null;
        }
        this.m_firstFree = 0;
    }
    
    public boolean removeElement(final Node s) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        if (null == this.m_map) {
            return false;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            final Node node = this.m_map[i];
            if (null != node && node.equals(s)) {
                if (i < this.m_firstFree - 1) {
                    System.arraycopy(this.m_map, i + 1, this.m_map, i, this.m_firstFree - i - 1);
                }
                --this.m_firstFree;
                this.m_map[this.m_firstFree] = null;
                return true;
            }
        }
        return false;
    }
    
    public void removeElementAt(final int i) {
        if (null == this.m_map) {
            return;
        }
        if (i >= this.m_firstFree) {
            throw new ArrayIndexOutOfBoundsException(i + " >= " + this.m_firstFree);
        }
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        if (i < this.m_firstFree - 1) {
            System.arraycopy(this.m_map, i + 1, this.m_map, i, this.m_firstFree - i - 1);
        }
        --this.m_firstFree;
        this.m_map[this.m_firstFree] = null;
    }
    
    public void setElementAt(final Node node, final int index) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
        }
        if (null == this.m_map) {
            this.m_map = new Node[this.m_blocksize];
            this.m_mapSize = this.m_blocksize;
        }
        this.m_map[index] = node;
    }
    
    public Node elementAt(final int i) {
        if (null == this.m_map) {
            return null;
        }
        return this.m_map[i];
    }
    
    public boolean contains(final Node s) {
        this.runTo(-1);
        if (null == this.m_map) {
            return false;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            final Node node = this.m_map[i];
            if (null != node && node.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOf(final Node elem, final int index) {
        this.runTo(-1);
        if (null == this.m_map) {
            return -1;
        }
        for (int i = index; i < this.m_firstFree; ++i) {
            final Node node = this.m_map[i];
            if (null != node && node.equals(elem)) {
                return i;
            }
        }
        return -1;
    }
    
    public int indexOf(final Node elem) {
        this.runTo(-1);
        if (null == this.m_map) {
            return -1;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            final Node node = this.m_map[i];
            if (null != node && node.equals(elem)) {
                return i;
            }
        }
        return -1;
    }
}
