package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.NodeVector;

public class NodeSetDTM extends NodeVector implements DTMIterator, Cloneable
{
    static final long serialVersionUID = 7686480133331317070L;
    DTMManager m_manager;
    protected transient int m_next;
    protected transient boolean m_mutable;
    protected transient boolean m_cacheNodes;
    protected int m_root;
    private transient int m_last;
    
    public NodeSetDTM(final DTMManager dtmManager) {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_root = -1;
        this.m_last = 0;
        this.m_manager = dtmManager;
    }
    
    public NodeSetDTM(final int blocksize, final int dummy, final DTMManager dtmManager) {
        super(blocksize);
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_root = -1;
        this.m_last = 0;
        this.m_manager = dtmManager;
    }
    
    public NodeSetDTM(final NodeSetDTM nodelist) {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_root = -1;
        this.m_last = 0;
        this.m_manager = nodelist.getDTMManager();
        this.m_root = nodelist.getRoot();
        this.addNodes(nodelist);
    }
    
    public NodeSetDTM(final DTMIterator ni) {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_root = -1;
        this.m_last = 0;
        this.m_manager = ni.getDTMManager();
        this.m_root = ni.getRoot();
        this.addNodes(ni);
    }
    
    public NodeSetDTM(final NodeIterator iterator, final XPathContext xctxt) {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_root = -1;
        this.m_last = 0;
        this.m_manager = xctxt.getDTMManager();
        Node node;
        while (null != (node = iterator.nextNode())) {
            final int handle = xctxt.getDTMHandleFromNode(node);
            this.addNodeInDocOrder(handle, xctxt);
        }
    }
    
    public NodeSetDTM(final NodeList nodeList, final XPathContext xctxt) {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_root = -1;
        this.m_last = 0;
        this.m_manager = xctxt.getDTMManager();
        for (int n = nodeList.getLength(), i = 0; i < n; ++i) {
            final Node node = nodeList.item(i);
            final int handle = xctxt.getDTMHandleFromNode(node);
            this.addNode(handle);
        }
    }
    
    public NodeSetDTM(final int node, final DTMManager dtmManager) {
        this.m_next = 0;
        this.m_mutable = true;
        this.m_cacheNodes = true;
        this.m_root = -1;
        this.m_last = 0;
        this.m_manager = dtmManager;
        this.addNode(node);
    }
    
    public void setEnvironment(final Object environment) {
    }
    
    @Override
    public int getRoot() {
        if (-1 != this.m_root) {
            return this.m_root;
        }
        if (this.size() > 0) {
            return this.item(0);
        }
        return -1;
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final NodeSetDTM clone = (NodeSetDTM)super.clone();
        return clone;
    }
    
    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        final NodeSetDTM clone = (NodeSetDTM)this.clone();
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
    
    public DTMFilter getFilter() {
        return null;
    }
    
    @Override
    public boolean getExpandEntityReferences() {
        return true;
    }
    
    @Override
    public DTM getDTM(final int nodeHandle) {
        return this.m_manager.getDTM(nodeHandle);
    }
    
    @Override
    public DTMManager getDTMManager() {
        return this.m_manager;
    }
    
    @Override
    public int nextNode() {
        if (this.m_next < this.size()) {
            final int next = this.elementAt(this.m_next);
            ++this.m_next;
            return next;
        }
        return -1;
    }
    
    @Override
    public int previousNode() {
        if (!this.m_cacheNodes) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_ITERATE", null));
        }
        if (this.m_next - 1 > 0) {
            --this.m_next;
            return this.elementAt(this.m_next);
        }
        return -1;
    }
    
    @Override
    public void detach() {
    }
    
    @Override
    public void allowDetachToRelease(final boolean allowRelease) {
    }
    
    @Override
    public boolean isFresh() {
        return this.m_next == 0;
    }
    
    @Override
    public void runTo(final int index) {
        if (!this.m_cacheNodes) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_INDEX", null));
        }
        if (index >= 0 && this.m_next < this.m_firstFree) {
            this.m_next = index;
        }
        else {
            this.m_next = this.m_firstFree - 1;
        }
    }
    
    @Override
    public int item(final int index) {
        this.runTo(index);
        return this.elementAt(index);
    }
    
    @Override
    public int getLength() {
        this.runTo(-1);
        return this.size();
    }
    
    public void addNode(final int n) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        this.addElement(n);
    }
    
    public void insertNode(final int n, final int pos) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        this.insertElementAt(n, pos);
    }
    
    public void removeNode(final int n) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        this.removeElement(n);
    }
    
    public void addNodes(final DTMIterator iterator) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        if (null != iterator) {
            int obj;
            while (-1 != (obj = iterator.nextNode())) {
                this.addElement(obj);
            }
        }
    }
    
    public void addNodesInDocOrder(final DTMIterator iterator, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        int node;
        while (-1 != (node = iterator.nextNode())) {
            this.addNodeInDocOrder(node, support);
        }
    }
    
    public int addNodeInDocOrder(final int node, final boolean test, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        int insertIndex = -1;
        if (test) {
            final int size = this.size();
            int i;
            for (i = size - 1; i >= 0; --i) {
                final int child = this.elementAt(i);
                if (child == node) {
                    i = -2;
                    break;
                }
                final DTM dtm = support.getDTM(node);
                if (!dtm.isNodeAfter(node, child)) {
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
                if (i == node) {
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
    
    public int addNodeInDocOrder(final int node, final XPathContext support) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        return this.addNodeInDocOrder(node, true, support);
    }
    
    @Override
    public int size() {
        return super.size();
    }
    
    @Override
    public void addElement(final int value) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        super.addElement(value);
    }
    
    @Override
    public void insertElementAt(final int value, final int at) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        super.insertElementAt(value, at);
    }
    
    @Override
    public void appendNodes(final NodeVector nodes) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        super.appendNodes(nodes);
    }
    
    @Override
    public void removeAllElements() {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        super.removeAllElements();
    }
    
    @Override
    public boolean removeElement(final int s) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        return super.removeElement(s);
    }
    
    @Override
    public void removeElementAt(final int i) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        super.removeElementAt(i);
    }
    
    @Override
    public void setElementAt(final int node, final int index) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        super.setElementAt(node, index);
    }
    
    @Override
    public void setItem(final int node, final int index) {
        if (!this.m_mutable) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
        }
        super.setElementAt(node, index);
    }
    
    @Override
    public int elementAt(final int i) {
        this.runTo(i);
        return super.elementAt(i);
    }
    
    @Override
    public boolean contains(final int s) {
        this.runTo(-1);
        return super.contains(s);
    }
    
    @Override
    public int indexOf(final int elem, final int index) {
        this.runTo(-1);
        return super.indexOf(elem, index);
    }
    
    @Override
    public int indexOf(final int elem) {
        this.runTo(-1);
        return super.indexOf(elem);
    }
    
    @Override
    public int getCurrentPos() {
        return this.m_next;
    }
    
    @Override
    public void setCurrentPos(final int i) {
        if (!this.m_cacheNodes) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_INDEX", null));
        }
        this.m_next = i;
    }
    
    @Override
    public int getCurrentNode() {
        if (!this.m_cacheNodes) {
            throw new RuntimeException("This NodeSetDTM can not do indexing or counting functions!");
        }
        final int saved = this.m_next;
        final int current = (this.m_next > 0) ? (this.m_next - 1) : this.m_next;
        final int n = (current < this.m_firstFree) ? this.elementAt(current) : -1;
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
    public boolean isMutable() {
        return this.m_mutable;
    }
    
    public int getLast() {
        return this.m_last;
    }
    
    public void setLast(final int last) {
        this.m_last = last;
    }
    
    @Override
    public boolean isDocOrdered() {
        return true;
    }
    
    @Override
    public int getAxis() {
        return -1;
    }
}
