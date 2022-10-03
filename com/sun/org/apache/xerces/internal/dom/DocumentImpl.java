package com.sun.org.apache.xerces.internal.dom;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;
import java.io.ObjectOutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.events.EventException;
import java.util.Collection;
import org.w3c.dom.events.EventListener;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.dom.events.MutationEventImpl;
import com.sun.org.apache.xerces.internal.dom.events.EventImpl;
import org.w3c.dom.events.Event;
import org.w3c.dom.traversal.TreeWalker;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import java.io.ObjectStreamField;
import java.util.Map;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.NodeIterator;
import java.util.List;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.traversal.DocumentTraversal;

public class DocumentImpl extends CoreDocumentImpl implements DocumentTraversal, DocumentEvent, DocumentRange
{
    static final long serialVersionUID = 515687835542616694L;
    protected List<NodeIterator> iterators;
    protected List<Range> ranges;
    protected Map<NodeImpl, List<LEntry>> eventListeners;
    protected boolean mutationEvents;
    private static final ObjectStreamField[] serialPersistentFields;
    EnclosingAttr savedEnclosingAttr;
    
    public DocumentImpl() {
        this.mutationEvents = false;
    }
    
    public DocumentImpl(final boolean grammarAccess) {
        super(grammarAccess);
        this.mutationEvents = false;
    }
    
    public DocumentImpl(final DocumentType doctype) {
        super(doctype);
        this.mutationEvents = false;
    }
    
    public DocumentImpl(final DocumentType doctype, final boolean grammarAccess) {
        super(doctype, grammarAccess);
        this.mutationEvents = false;
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final DocumentImpl newdoc = new DocumentImpl();
        this.callUserDataHandlers(this, newdoc, (short)1);
        this.cloneNode(newdoc, deep);
        newdoc.mutationEvents = this.mutationEvents;
        return newdoc;
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }
    
    public NodeIterator createNodeIterator(final Node root, final short whatToShow, final NodeFilter filter) {
        return this.createNodeIterator(root, whatToShow, filter, true);
    }
    
    @Override
    public NodeIterator createNodeIterator(final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion) {
        if (root == null) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException((short)9, msg);
        }
        final NodeIterator iterator = new NodeIteratorImpl(this, root, whatToShow, filter, entityReferenceExpansion);
        if (this.iterators == null) {
            this.iterators = new ArrayList<NodeIterator>();
        }
        this.iterators.add(iterator);
        return iterator;
    }
    
    public TreeWalker createTreeWalker(final Node root, final short whatToShow, final NodeFilter filter) {
        return this.createTreeWalker(root, whatToShow, filter, true);
    }
    
    @Override
    public TreeWalker createTreeWalker(final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion) {
        if (root == null) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException((short)9, msg);
        }
        return new TreeWalkerImpl(root, whatToShow, filter, entityReferenceExpansion);
    }
    
    void removeNodeIterator(final NodeIterator nodeIterator) {
        if (nodeIterator == null) {
            return;
        }
        if (this.iterators == null) {
            return;
        }
        this.iterators.remove(nodeIterator);
    }
    
    @Override
    public Range createRange() {
        if (this.ranges == null) {
            this.ranges = new ArrayList<Range>();
        }
        final Range range = new RangeImpl(this);
        this.ranges.add(range);
        return range;
    }
    
    void removeRange(final Range range) {
        if (range == null) {
            return;
        }
        if (this.ranges == null) {
            return;
        }
        this.ranges.remove(range);
    }
    
    @Override
    void replacedText(final NodeImpl node) {
        if (this.ranges != null) {
            for (int size = this.ranges.size(), i = 0; i != size; ++i) {
                this.ranges.get(i).receiveReplacedText(node);
            }
        }
    }
    
    @Override
    void deletedText(final NodeImpl node, final int offset, final int count) {
        if (this.ranges != null) {
            for (int size = this.ranges.size(), i = 0; i != size; ++i) {
                this.ranges.get(i).receiveDeletedText(node, offset, count);
            }
        }
    }
    
    @Override
    void insertedText(final NodeImpl node, final int offset, final int count) {
        if (this.ranges != null) {
            for (int size = this.ranges.size(), i = 0; i != size; ++i) {
                this.ranges.get(i).receiveInsertedText(node, offset, count);
            }
        }
    }
    
    void splitData(final Node node, final Node newNode, final int offset) {
        if (this.ranges != null) {
            for (int size = this.ranges.size(), i = 0; i != size; ++i) {
                this.ranges.get(i).receiveSplitData(node, newNode, offset);
            }
        }
    }
    
    @Override
    public Event createEvent(final String type) throws DOMException {
        if (type.equalsIgnoreCase("Events") || "Event".equals(type)) {
            return new EventImpl();
        }
        if (type.equalsIgnoreCase("MutationEvents") || "MutationEvent".equals(type)) {
            return new MutationEventImpl();
        }
        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException((short)9, msg);
    }
    
    @Override
    void setMutationEvents(final boolean set) {
        this.mutationEvents = set;
    }
    
    @Override
    boolean getMutationEvents() {
        return this.mutationEvents;
    }
    
    private void setEventListeners(final NodeImpl n, final List<LEntry> listeners) {
        if (this.eventListeners == null) {
            this.eventListeners = new HashMap<NodeImpl, List<LEntry>>();
        }
        if (listeners == null) {
            this.eventListeners.remove(n);
            if (this.eventListeners.isEmpty()) {
                this.mutationEvents = false;
            }
        }
        else {
            this.eventListeners.put(n, listeners);
            this.mutationEvents = true;
        }
    }
    
    private List<LEntry> getEventListeners(final NodeImpl n) {
        if (this.eventListeners == null) {
            return null;
        }
        return this.eventListeners.get(n);
    }
    
    @Override
    protected void addEventListener(final NodeImpl node, final String type, final EventListener listener, final boolean useCapture) {
        if (type == null || type.equals("") || listener == null) {
            return;
        }
        this.removeEventListener(node, type, listener, useCapture);
        List<LEntry> nodeListeners = this.getEventListeners(node);
        if (nodeListeners == null) {
            nodeListeners = new ArrayList<LEntry>();
            this.setEventListeners(node, nodeListeners);
        }
        nodeListeners.add(new LEntry(type, listener, useCapture));
        final LCount lc = LCount.lookup(type);
        if (useCapture) {
            final LCount lCount = lc;
            ++lCount.captures;
            final LCount lCount2 = lc;
            ++lCount2.total;
        }
        else {
            final LCount lCount3 = lc;
            ++lCount3.bubbles;
            final LCount lCount4 = lc;
            ++lCount4.total;
        }
    }
    
    @Override
    protected void removeEventListener(final NodeImpl node, final String type, final EventListener listener, final boolean useCapture) {
        if (type == null || type.equals("") || listener == null) {
            return;
        }
        final List<LEntry> nodeListeners = this.getEventListeners(node);
        if (nodeListeners == null) {
            return;
        }
        int i = nodeListeners.size() - 1;
        while (i >= 0) {
            final LEntry le = nodeListeners.get(i);
            if (le.useCapture == useCapture && le.listener == listener && le.type.equals(type)) {
                nodeListeners.remove(i);
                if (nodeListeners.isEmpty()) {
                    this.setEventListeners(node, null);
                }
                final LCount lc = LCount.lookup(type);
                if (useCapture) {
                    final LCount lCount = lc;
                    --lCount.captures;
                    final LCount lCount2 = lc;
                    --lCount2.total;
                    break;
                }
                final LCount lCount3 = lc;
                --lCount3.bubbles;
                final LCount lCount4 = lc;
                --lCount4.total;
                break;
            }
            else {
                --i;
            }
        }
    }
    
    @Override
    protected void copyEventListeners(final NodeImpl src, final NodeImpl tgt) {
        final List<LEntry> nodeListeners = this.getEventListeners(src);
        if (nodeListeners == null) {
            return;
        }
        this.setEventListeners(tgt, new ArrayList<LEntry>(nodeListeners));
    }
    
    @Override
    protected boolean dispatchEvent(final NodeImpl node, final Event event) {
        if (event == null) {
            return false;
        }
        final EventImpl evt = (EventImpl)event;
        if (!evt.initialized || evt.type == null || evt.type.equals("")) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UNSPECIFIED_EVENT_TYPE_ERR", null);
            throw new EventException((short)0, msg);
        }
        final LCount lc = LCount.lookup(evt.getType());
        if (lc.total == 0) {
            return evt.preventDefault;
        }
        evt.target = node;
        evt.stopPropagation = false;
        evt.preventDefault = false;
        final List<Node> pv = new ArrayList<Node>(10);
        Node p = node;
        for (Node n = p.getParentNode(); n != null; n = n.getParentNode()) {
            pv.add(n);
            p = n;
        }
        if (lc.captures > 0) {
            evt.eventPhase = 1;
            for (int j = pv.size() - 1; j >= 0; --j) {
                if (evt.stopPropagation) {
                    break;
                }
                final NodeImpl nn = pv.get(j);
                evt.currentTarget = nn;
                final List<LEntry> nodeListeners = this.getEventListeners(nn);
                if (nodeListeners != null) {
                    final List<LEntry> nl = (List<LEntry>)((ArrayList)nodeListeners).clone();
                    for (int nlsize = nl.size(), i = 0; i < nlsize; ++i) {
                        final LEntry le = nl.get(i);
                        if (le.useCapture && le.type.equals(evt.type) && nodeListeners.contains(le)) {
                            try {
                                le.listener.handleEvent(evt);
                            }
                            catch (final Exception ex) {}
                        }
                    }
                }
            }
        }
        if (lc.bubbles > 0) {
            evt.eventPhase = 2;
            evt.currentTarget = node;
            List<LEntry> nodeListeners2 = this.getEventListeners(node);
            if (!evt.stopPropagation && nodeListeners2 != null) {
                final List<LEntry> nl2 = (List<LEntry>)((ArrayList)nodeListeners2).clone();
                for (int nlsize2 = nl2.size(), k = 0; k < nlsize2; ++k) {
                    final LEntry le2 = nl2.get(k);
                    if (!le2.useCapture && le2.type.equals(evt.type) && nodeListeners2.contains(le2)) {
                        try {
                            le2.listener.handleEvent(evt);
                        }
                        catch (final Exception ex2) {}
                    }
                }
            }
            if (evt.bubbles) {
                evt.eventPhase = 3;
                for (int pvsize = pv.size(), l = 0; l < pvsize; ++l) {
                    if (evt.stopPropagation) {
                        break;
                    }
                    final NodeImpl nn2 = pv.get(l);
                    evt.currentTarget = nn2;
                    nodeListeners2 = this.getEventListeners(nn2);
                    if (nodeListeners2 != null) {
                        final List<LEntry> nl3 = (List<LEntry>)((ArrayList)nodeListeners2).clone();
                        for (int nlsize3 = nl3.size(), m = 0; m < nlsize3; ++m) {
                            final LEntry le3 = nl3.get(m);
                            if (!le3.useCapture && le3.type.equals(evt.type) && nodeListeners2.contains(le3)) {
                                try {
                                    le3.listener.handleEvent(evt);
                                }
                                catch (final Exception ex3) {}
                            }
                        }
                    }
                }
            }
        }
        if (lc.defaults <= 0 || !evt.cancelable || !evt.preventDefault) {}
        return evt.preventDefault;
    }
    
    protected void dispatchEventToSubtree(final Node n, final Event e) {
        ((NodeImpl)n).dispatchEvent(e);
        if (n.getNodeType() == 1) {
            final NamedNodeMap a = n.getAttributes();
            for (int i = a.getLength() - 1; i >= 0; --i) {
                this.dispatchingEventToSubtree(a.item(i), e);
            }
        }
        this.dispatchingEventToSubtree(n.getFirstChild(), e);
    }
    
    protected void dispatchingEventToSubtree(final Node n, final Event e) {
        if (n == null) {
            return;
        }
        ((NodeImpl)n).dispatchEvent(e);
        if (n.getNodeType() == 1) {
            final NamedNodeMap a = n.getAttributes();
            for (int i = a.getLength() - 1; i >= 0; --i) {
                this.dispatchingEventToSubtree(a.item(i), e);
            }
        }
        this.dispatchingEventToSubtree(n.getFirstChild(), e);
        this.dispatchingEventToSubtree(n.getNextSibling(), e);
    }
    
    protected void dispatchAggregateEvents(final NodeImpl node, final EnclosingAttr ea) {
        if (ea != null) {
            this.dispatchAggregateEvents(node, ea.node, ea.oldvalue, (short)1);
        }
        else {
            this.dispatchAggregateEvents(node, null, null, (short)0);
        }
    }
    
    protected void dispatchAggregateEvents(final NodeImpl node, final AttrImpl enclosingAttr, final String oldvalue, final short change) {
        NodeImpl owner = null;
        if (enclosingAttr != null) {
            final LCount lc = LCount.lookup("DOMAttrModified");
            owner = (NodeImpl)enclosingAttr.getOwnerElement();
            if (lc.total > 0 && owner != null) {
                final MutationEventImpl me = new MutationEventImpl();
                me.initMutationEvent("DOMAttrModified", true, false, enclosingAttr, oldvalue, enclosingAttr.getNodeValue(), enclosingAttr.getNodeName(), change);
                owner.dispatchEvent(me);
            }
        }
        final LCount lc = LCount.lookup("DOMSubtreeModified");
        if (lc.total > 0) {
            final MutationEvent me2 = new MutationEventImpl();
            me2.initMutationEvent("DOMSubtreeModified", true, false, null, null, null, null, (short)0);
            if (enclosingAttr != null) {
                this.dispatchEvent(enclosingAttr, me2);
                if (owner != null) {
                    this.dispatchEvent(owner, me2);
                }
            }
            else {
                this.dispatchEvent(node, me2);
            }
        }
    }
    
    protected void saveEnclosingAttr(final NodeImpl node) {
        this.savedEnclosingAttr = null;
        final LCount lc = LCount.lookup("DOMAttrModified");
        if (lc.total > 0) {
            for (NodeImpl eventAncestor = node; eventAncestor != null; eventAncestor = eventAncestor.parentNode()) {
                final int type = eventAncestor.getNodeType();
                if (type == 2) {
                    final EnclosingAttr retval = new EnclosingAttr();
                    retval.node = (AttrImpl)eventAncestor;
                    retval.oldvalue = retval.node.getNodeValue();
                    this.savedEnclosingAttr = retval;
                    return;
                }
                if (type != 5) {
                    if (type != 3) {
                        return;
                    }
                }
            }
        }
    }
    
    @Override
    void modifyingCharacterData(final NodeImpl node, final boolean replace) {
        if (this.mutationEvents && !replace) {
            this.saveEnclosingAttr(node);
        }
    }
    
    @Override
    void modifiedCharacterData(final NodeImpl node, final String oldvalue, final String value, final boolean replace) {
        if (this.mutationEvents && !replace) {
            final LCount lc = LCount.lookup("DOMCharacterDataModified");
            if (lc.total > 0) {
                final MutationEvent me = new MutationEventImpl();
                me.initMutationEvent("DOMCharacterDataModified", true, false, null, oldvalue, value, null, (short)0);
                this.dispatchEvent(node, me);
            }
            this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
        }
    }
    
    @Override
    void replacedCharacterData(final NodeImpl node, final String oldvalue, final String value) {
        this.modifiedCharacterData(node, oldvalue, value, false);
    }
    
    @Override
    void insertingNode(final NodeImpl node, final boolean replace) {
        if (this.mutationEvents && !replace) {
            this.saveEnclosingAttr(node);
        }
    }
    
    @Override
    void insertedNode(final NodeImpl node, final NodeImpl newInternal, final boolean replace) {
        if (this.mutationEvents) {
            LCount lc = LCount.lookup("DOMNodeInserted");
            if (lc.total > 0) {
                final MutationEventImpl me = new MutationEventImpl();
                me.initMutationEvent("DOMNodeInserted", true, false, node, null, null, null, (short)0);
                this.dispatchEvent(newInternal, me);
            }
            lc = LCount.lookup("DOMNodeInsertedIntoDocument");
            if (lc.total > 0) {
                NodeImpl eventAncestor = node;
                if (this.savedEnclosingAttr != null) {
                    eventAncestor = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement();
                }
                if (eventAncestor != null) {
                    NodeImpl p = eventAncestor;
                    while (p != null) {
                        eventAncestor = p;
                        if (p.getNodeType() == 2) {
                            p = (NodeImpl)((AttrImpl)p).getOwnerElement();
                        }
                        else {
                            p = p.parentNode();
                        }
                    }
                    if (eventAncestor.getNodeType() == 9) {
                        final MutationEventImpl me2 = new MutationEventImpl();
                        me2.initMutationEvent("DOMNodeInsertedIntoDocument", false, false, null, null, null, null, (short)0);
                        this.dispatchEventToSubtree(newInternal, me2);
                    }
                }
            }
            if (!replace) {
                this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
            }
        }
        if (this.ranges != null) {
            for (int size = this.ranges.size(), i = 0; i != size; ++i) {
                this.ranges.get(i).insertedNodeFromDOM(newInternal);
            }
        }
    }
    
    @Override
    void removingNode(final NodeImpl node, final NodeImpl oldChild, final boolean replace) {
        if (this.iterators != null) {
            for (int size = this.iterators.size(), i = 0; i != size; ++i) {
                this.iterators.get(i).removeNode(oldChild);
            }
        }
        if (this.ranges != null) {
            for (int size = this.ranges.size(), i = 0; i != size; ++i) {
                this.ranges.get(i).removeNode(oldChild);
            }
        }
        if (this.mutationEvents) {
            if (!replace) {
                this.saveEnclosingAttr(node);
            }
            LCount lc = LCount.lookup("DOMNodeRemoved");
            if (lc.total > 0) {
                final MutationEventImpl me = new MutationEventImpl();
                me.initMutationEvent("DOMNodeRemoved", true, false, node, null, null, null, (short)0);
                this.dispatchEvent(oldChild, me);
            }
            lc = LCount.lookup("DOMNodeRemovedFromDocument");
            if (lc.total > 0) {
                NodeImpl eventAncestor = this;
                if (this.savedEnclosingAttr != null) {
                    eventAncestor = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement();
                }
                if (eventAncestor != null) {
                    for (NodeImpl p = eventAncestor.parentNode(); p != null; p = p.parentNode()) {
                        eventAncestor = p;
                    }
                    if (eventAncestor.getNodeType() == 9) {
                        final MutationEventImpl me2 = new MutationEventImpl();
                        me2.initMutationEvent("DOMNodeRemovedFromDocument", false, false, null, null, null, null, (short)0);
                        this.dispatchEventToSubtree(oldChild, me2);
                    }
                }
            }
        }
    }
    
    @Override
    void removedNode(final NodeImpl node, final boolean replace) {
        if (this.mutationEvents && !replace) {
            this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
        }
    }
    
    @Override
    void replacingNode(final NodeImpl node) {
        if (this.mutationEvents) {
            this.saveEnclosingAttr(node);
        }
    }
    
    @Override
    void replacingData(final NodeImpl node) {
        if (this.mutationEvents) {
            this.saveEnclosingAttr(node);
        }
    }
    
    @Override
    void replacedNode(final NodeImpl node) {
        if (this.mutationEvents) {
            this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
        }
    }
    
    @Override
    void modifiedAttrValue(final AttrImpl attr, final String oldvalue) {
        if (this.mutationEvents) {
            this.dispatchAggregateEvents(attr, attr, oldvalue, (short)1);
        }
    }
    
    @Override
    void setAttrNode(final AttrImpl attr, final AttrImpl previous) {
        if (this.mutationEvents) {
            if (previous == null) {
                this.dispatchAggregateEvents(attr.ownerNode, attr, null, (short)2);
            }
            else {
                this.dispatchAggregateEvents(attr.ownerNode, attr, previous.getNodeValue(), (short)1);
            }
        }
    }
    
    @Override
    void removedAttrNode(final AttrImpl attr, final NodeImpl oldOwner, final String name) {
        if (this.mutationEvents) {
            final LCount lc = LCount.lookup("DOMAttrModified");
            if (lc.total > 0) {
                final MutationEventImpl me = new MutationEventImpl();
                me.initMutationEvent("DOMAttrModified", true, false, attr, attr.getNodeValue(), null, name, (short)3);
                this.dispatchEvent(oldOwner, me);
            }
            this.dispatchAggregateEvents(oldOwner, null, null, (short)0);
        }
    }
    
    @Override
    void renamedAttrNode(final Attr oldAt, final Attr newAt) {
    }
    
    @Override
    void renamedElement(final Element oldEl, final Element newEl) {
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        final Vector<NodeIterator> it = (this.iterators == null) ? null : new Vector<NodeIterator>(this.iterators);
        final Vector<Range> r = (this.ranges == null) ? null : new Vector<Range>(this.ranges);
        Hashtable<NodeImpl, Vector<LEntry>> el = null;
        if (this.eventListeners != null) {
            el = new Hashtable<NodeImpl, Vector<LEntry>>();
            for (final Map.Entry<NodeImpl, List<LEntry>> e : this.eventListeners.entrySet()) {
                el.put(e.getKey(), new Vector<LEntry>(e.getValue()));
            }
        }
        final ObjectOutputStream.PutField pf = out.putFields();
        pf.put("iterators", it);
        pf.put("ranges", r);
        pf.put("eventListeners", el);
        pf.put("mutationEvents", this.mutationEvents);
        out.writeFields();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField gf = in.readFields();
        final Vector<NodeIterator> it = (Vector<NodeIterator>)gf.get("iterators", null);
        final Vector<Range> r = (Vector<Range>)gf.get("ranges", null);
        final Hashtable<NodeImpl, Vector<LEntry>> el = (Hashtable<NodeImpl, Vector<LEntry>>)gf.get("eventListeners", null);
        this.mutationEvents = gf.get("mutationEvents", false);
        if (it != null) {
            this.iterators = new ArrayList<NodeIterator>(it);
        }
        if (r != null) {
            this.ranges = new ArrayList<Range>(r);
        }
        if (el != null) {
            this.eventListeners = new HashMap<NodeImpl, List<LEntry>>();
            for (final Map.Entry<NodeImpl, Vector<LEntry>> e : el.entrySet()) {
                this.eventListeners.put(e.getKey(), new ArrayList<LEntry>(e.getValue()));
            }
        }
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("iterators", Vector.class), new ObjectStreamField("ranges", Vector.class), new ObjectStreamField("eventListeners", Hashtable.class), new ObjectStreamField("mutationEvents", Boolean.TYPE) };
    }
    
    class LEntry implements Serializable
    {
        private static final long serialVersionUID = -8426757059492421631L;
        String type;
        EventListener listener;
        boolean useCapture;
        
        LEntry(final String type, final EventListener listener, final boolean useCapture) {
            this.type = type;
            this.listener = listener;
            this.useCapture = useCapture;
        }
    }
    
    class EnclosingAttr implements Serializable
    {
        private static final long serialVersionUID = 5208387723391647216L;
        AttrImpl node;
        String oldvalue;
    }
}
