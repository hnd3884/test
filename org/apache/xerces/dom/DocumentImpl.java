package org.apache.xerces.dom;

import java.io.Serializable;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import java.util.Vector;
import org.apache.xerces.dom.events.MouseEventImpl;
import org.apache.xerces.dom.events.UIEventImpl;
import org.apache.xerces.dom.events.MutationEventImpl;
import org.apache.xerces.dom.events.EventImpl;
import org.w3c.dom.events.Event;
import org.w3c.dom.ranges.Range;
import java.util.Iterator;
import java.lang.ref.Reference;
import org.w3c.dom.traversal.TreeWalker;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import java.util.Hashtable;
import java.lang.ref.ReferenceQueue;
import java.util.List;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.traversal.DocumentTraversal;

public class DocumentImpl extends CoreDocumentImpl implements DocumentTraversal, DocumentEvent, DocumentRange
{
    static final long serialVersionUID = 515687835542616694L;
    protected transient List iterators;
    protected transient ReferenceQueue iteratorReferenceQueue;
    protected transient List ranges;
    protected transient ReferenceQueue rangeReferenceQueue;
    protected Hashtable eventListeners;
    protected boolean mutationEvents;
    EnclosingAttr savedEnclosingAttr;
    
    public DocumentImpl() {
        this.mutationEvents = false;
    }
    
    public DocumentImpl(final boolean b) {
        super(b);
        this.mutationEvents = false;
    }
    
    public DocumentImpl(final DocumentType documentType) {
        super(documentType);
        this.mutationEvents = false;
    }
    
    public DocumentImpl(final DocumentType documentType, final boolean b) {
        super(documentType, b);
        this.mutationEvents = false;
    }
    
    public Node cloneNode(final boolean b) {
        final DocumentImpl documentImpl = new DocumentImpl();
        this.callUserDataHandlers(this, documentImpl, (short)1);
        this.cloneNode(documentImpl, b);
        documentImpl.mutationEvents = this.mutationEvents;
        return documentImpl;
    }
    
    public DOMImplementation getImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }
    
    public NodeIterator createNodeIterator(final Node node, final short n, final NodeFilter nodeFilter) {
        return this.createNodeIterator(node, n, nodeFilter, true);
    }
    
    public NodeIterator createNodeIterator(final Node node, final int n, final NodeFilter nodeFilter, final boolean b) {
        if (node == null) {
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
        }
        final NodeIteratorImpl nodeIteratorImpl = new NodeIteratorImpl(this, node, n, nodeFilter, b);
        if (this.iterators == null) {
            this.iterators = new LinkedList();
            this.iteratorReferenceQueue = new ReferenceQueue();
        }
        this.removeStaleIteratorReferences();
        this.iterators.add(new WeakReference(nodeIteratorImpl, this.iteratorReferenceQueue));
        return nodeIteratorImpl;
    }
    
    public TreeWalker createTreeWalker(final Node node, final short n, final NodeFilter nodeFilter) {
        return this.createTreeWalker(node, n, nodeFilter, true);
    }
    
    public TreeWalker createTreeWalker(final Node node, final int n, final NodeFilter nodeFilter, final boolean b) {
        if (node == null) {
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
        }
        return new TreeWalkerImpl(node, n, nodeFilter, b);
    }
    
    void removeNodeIterator(final NodeIterator nodeIterator) {
        if (nodeIterator == null) {
            return;
        }
        if (this.iterators == null) {
            return;
        }
        this.removeStaleIteratorReferences();
        final Iterator iterator = this.iterators.iterator();
        while (iterator.hasNext()) {
            final Object value = ((Reference<Object>)iterator.next()).get();
            if (value == nodeIterator) {
                iterator.remove();
                return;
            }
            if (value != null) {
                continue;
            }
            iterator.remove();
        }
    }
    
    private void removeStaleIteratorReferences() {
        this.removeStaleReferences(this.iteratorReferenceQueue, this.iterators);
    }
    
    private void removeStaleReferences(final ReferenceQueue referenceQueue, final List list) {
        Reference reference = referenceQueue.poll();
        int n = 0;
        while (reference != null) {
            ++n;
            reference = referenceQueue.poll();
        }
        if (n > 0) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                if (((Reference<Object>)iterator.next()).get() == null) {
                    iterator.remove();
                    if (--n <= 0) {
                        return;
                    }
                    continue;
                }
            }
        }
    }
    
    public Range createRange() {
        if (this.ranges == null) {
            this.ranges = new LinkedList();
            this.rangeReferenceQueue = new ReferenceQueue();
        }
        final RangeImpl rangeImpl = new RangeImpl(this);
        this.removeStaleRangeReferences();
        this.ranges.add(new WeakReference(rangeImpl, this.rangeReferenceQueue));
        return rangeImpl;
    }
    
    void removeRange(final Range range) {
        if (range == null) {
            return;
        }
        if (this.ranges == null) {
            return;
        }
        this.removeStaleRangeReferences();
        final Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            final Object value = ((Reference<Object>)iterator.next()).get();
            if (value == range) {
                iterator.remove();
                return;
            }
            if (value != null) {
                continue;
            }
            iterator.remove();
        }
    }
    
    void replacedText(final CharacterDataImpl characterDataImpl) {
        if (this.ranges != null) {
            this.notifyRangesReplacedText(characterDataImpl);
        }
    }
    
    private void notifyRangesReplacedText(final CharacterDataImpl characterDataImpl) {
        this.removeStaleRangeReferences();
        final Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            final RangeImpl rangeImpl = ((Reference<RangeImpl>)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveReplacedText(characterDataImpl);
            }
            else {
                iterator.remove();
            }
        }
    }
    
    void deletedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
        if (this.ranges != null) {
            this.notifyRangesDeletedText(characterDataImpl, n, n2);
        }
    }
    
    private void notifyRangesDeletedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
        this.removeStaleRangeReferences();
        final Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            final RangeImpl rangeImpl = ((Reference<RangeImpl>)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveDeletedText(characterDataImpl, n, n2);
            }
            else {
                iterator.remove();
            }
        }
    }
    
    void insertedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
        if (this.ranges != null) {
            this.notifyRangesInsertedText(characterDataImpl, n, n2);
        }
    }
    
    private void notifyRangesInsertedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
        this.removeStaleRangeReferences();
        final Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            final RangeImpl rangeImpl = ((Reference<RangeImpl>)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveInsertedText(characterDataImpl, n, n2);
            }
            else {
                iterator.remove();
            }
        }
    }
    
    void splitData(final Node node, final Node node2, final int n) {
        if (this.ranges != null) {
            this.notifyRangesSplitData(node, node2, n);
        }
    }
    
    private void notifyRangesSplitData(final Node node, final Node node2, final int n) {
        this.removeStaleRangeReferences();
        final Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            final RangeImpl rangeImpl = ((Reference<RangeImpl>)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveSplitData(node, node2, n);
            }
            else {
                iterator.remove();
            }
        }
    }
    
    private void removeStaleRangeReferences() {
        this.removeStaleReferences(this.rangeReferenceQueue, this.ranges);
    }
    
    public Event createEvent(final String s) throws DOMException {
        if (s.equalsIgnoreCase("Events") || "Event".equals(s)) {
            return new EventImpl();
        }
        if (s.equalsIgnoreCase("MutationEvents") || "MutationEvent".equals(s)) {
            return new MutationEventImpl();
        }
        if (s.equalsIgnoreCase("UIEvents") || "UIEvent".equals(s)) {
            return new UIEventImpl();
        }
        if (s.equalsIgnoreCase("MouseEvents") || "MouseEvent".equals(s)) {
            return new MouseEventImpl();
        }
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    void setMutationEvents(final boolean mutationEvents) {
        this.mutationEvents = mutationEvents;
    }
    
    boolean getMutationEvents() {
        return this.mutationEvents;
    }
    
    protected void setEventListeners(final NodeImpl nodeImpl, final Vector vector) {
        if (this.eventListeners == null) {
            this.eventListeners = new Hashtable();
        }
        if (vector == null) {
            this.eventListeners.remove(nodeImpl);
            if (this.eventListeners.isEmpty()) {
                this.mutationEvents = false;
            }
        }
        else {
            this.eventListeners.put(nodeImpl, vector);
            this.mutationEvents = true;
        }
    }
    
    protected Vector getEventListeners(final NodeImpl nodeImpl) {
        if (this.eventListeners == null) {
            return null;
        }
        return this.eventListeners.get(nodeImpl);
    }
    
    protected void addEventListener(final NodeImpl nodeImpl, final String s, final EventListener eventListener, final boolean b) {
        if (s == null || s.length() == 0 || eventListener == null) {
            return;
        }
        this.removeEventListener(nodeImpl, s, eventListener, b);
        Vector eventListeners = this.getEventListeners(nodeImpl);
        if (eventListeners == null) {
            eventListeners = new Vector();
            this.setEventListeners(nodeImpl, eventListeners);
        }
        eventListeners.addElement(new LEntry(s, eventListener, b));
        final LCount lookup = LCount.lookup(s);
        if (b) {
            final LCount lCount = lookup;
            ++lCount.captures;
            final LCount lCount2 = lookup;
            ++lCount2.total;
        }
        else {
            final LCount lCount3 = lookup;
            ++lCount3.bubbles;
            final LCount lCount4 = lookup;
            ++lCount4.total;
        }
    }
    
    protected void removeEventListener(final NodeImpl nodeImpl, final String s, final EventListener eventListener, final boolean b) {
        if (s == null || s.length() == 0 || eventListener == null) {
            return;
        }
        final Vector eventListeners = this.getEventListeners(nodeImpl);
        if (eventListeners == null) {
            return;
        }
        int i = eventListeners.size() - 1;
        while (i >= 0) {
            final LEntry lEntry = eventListeners.elementAt(i);
            if (lEntry.useCapture == b && lEntry.listener == eventListener && lEntry.type.equals(s)) {
                eventListeners.removeElementAt(i);
                if (eventListeners.size() == 0) {
                    this.setEventListeners(nodeImpl, null);
                }
                final LCount lookup = LCount.lookup(s);
                if (b) {
                    final LCount lCount = lookup;
                    --lCount.captures;
                    final LCount lCount2 = lookup;
                    --lCount2.total;
                    break;
                }
                final LCount lCount3 = lookup;
                --lCount3.bubbles;
                final LCount lCount4 = lookup;
                --lCount4.total;
                break;
            }
            else {
                --i;
            }
        }
    }
    
    protected void copyEventListeners(final NodeImpl nodeImpl, final NodeImpl nodeImpl2) {
        final Vector eventListeners = this.getEventListeners(nodeImpl);
        if (eventListeners == null) {
            return;
        }
        this.setEventListeners(nodeImpl2, (Vector)eventListeners.clone());
    }
    
    protected boolean dispatchEvent(final NodeImpl nodeImpl, final Event event) {
        if (event == null) {
            return false;
        }
        final EventImpl eventImpl = (EventImpl)event;
        if (!eventImpl.initialized || eventImpl.type == null || eventImpl.type.length() == 0) {
            throw new EventException((short)0, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UNSPECIFIED_EVENT_TYPE_ERR", null));
        }
        final LCount lookup = LCount.lookup(eventImpl.getType());
        if (lookup.total == 0) {
            return eventImpl.preventDefault;
        }
        eventImpl.target = nodeImpl;
        eventImpl.stopPropagation = false;
        eventImpl.preventDefault = false;
        final ArrayList list = new ArrayList(10);
        for (Node node = nodeImpl.getParentNode(); node != null; node = node.getParentNode()) {
            list.add(node);
        }
        if (lookup.captures > 0) {
            eventImpl.eventPhase = 1;
            for (int i = list.size() - 1; i >= 0; --i) {
                if (eventImpl.stopPropagation) {
                    break;
                }
                final NodeImpl currentTarget = list.get(i);
                eventImpl.currentTarget = currentTarget;
                final Vector eventListeners = this.getEventListeners(currentTarget);
                if (eventListeners != null) {
                    final Vector vector = (Vector)eventListeners.clone();
                    for (int size = vector.size(), j = 0; j < size; ++j) {
                        final LEntry lEntry = vector.elementAt(j);
                        if (lEntry.useCapture && lEntry.type.equals(eventImpl.type) && eventListeners.contains(lEntry)) {
                            try {
                                lEntry.listener.handleEvent(eventImpl);
                            }
                            catch (final Exception ex) {}
                        }
                    }
                }
            }
        }
        if (lookup.bubbles > 0) {
            eventImpl.eventPhase = 2;
            eventImpl.currentTarget = nodeImpl;
            final Vector eventListeners2 = this.getEventListeners(nodeImpl);
            if (!eventImpl.stopPropagation && eventListeners2 != null) {
                final Vector vector2 = (Vector)eventListeners2.clone();
                for (int size2 = vector2.size(), k = 0; k < size2; ++k) {
                    final LEntry lEntry2 = vector2.elementAt(k);
                    if (!lEntry2.useCapture && lEntry2.type.equals(eventImpl.type) && eventListeners2.contains(lEntry2)) {
                        try {
                            lEntry2.listener.handleEvent(eventImpl);
                        }
                        catch (final Exception ex2) {}
                    }
                }
            }
            if (eventImpl.bubbles) {
                eventImpl.eventPhase = 3;
                for (int size3 = list.size(), l = 0; l < size3; ++l) {
                    if (eventImpl.stopPropagation) {
                        break;
                    }
                    final NodeImpl currentTarget2 = list.get(l);
                    eventImpl.currentTarget = currentTarget2;
                    final Vector eventListeners3 = this.getEventListeners(currentTarget2);
                    if (eventListeners3 != null) {
                        final Vector vector3 = (Vector)eventListeners3.clone();
                        for (int size4 = vector3.size(), n = 0; n < size4; ++n) {
                            final LEntry lEntry3 = vector3.elementAt(n);
                            if (!lEntry3.useCapture && lEntry3.type.equals(eventImpl.type) && eventListeners3.contains(lEntry3)) {
                                try {
                                    lEntry3.listener.handleEvent(eventImpl);
                                }
                                catch (final Exception ex3) {}
                            }
                        }
                    }
                }
            }
        }
        if (lookup.defaults <= 0 || !eventImpl.cancelable || !eventImpl.preventDefault) {}
        return eventImpl.preventDefault;
    }
    
    protected void dispatchEventToSubtree(final Node node, final Event event) {
        ((NodeImpl)node).dispatchEvent(event);
        if (node.getNodeType() == 1) {
            final NamedNodeMap attributes = node.getAttributes();
            for (int i = attributes.getLength() - 1; i >= 0; --i) {
                this.dispatchingEventToSubtree(attributes.item(i), event);
            }
        }
        this.dispatchingEventToSubtree(node.getFirstChild(), event);
    }
    
    protected void dispatchingEventToSubtree(final Node node, final Event event) {
        if (node == null) {
            return;
        }
        ((NodeImpl)node).dispatchEvent(event);
        if (node.getNodeType() == 1) {
            final NamedNodeMap attributes = node.getAttributes();
            for (int i = attributes.getLength() - 1; i >= 0; --i) {
                this.dispatchingEventToSubtree(attributes.item(i), event);
            }
        }
        this.dispatchingEventToSubtree(node.getFirstChild(), event);
        this.dispatchingEventToSubtree(node.getNextSibling(), event);
    }
    
    protected void dispatchAggregateEvents(final NodeImpl nodeImpl, final EnclosingAttr enclosingAttr) {
        if (enclosingAttr != null) {
            this.dispatchAggregateEvents(nodeImpl, enclosingAttr.node, enclosingAttr.oldvalue, (short)1);
        }
        else {
            this.dispatchAggregateEvents(nodeImpl, null, null, (short)0);
        }
    }
    
    protected void dispatchAggregateEvents(final NodeImpl nodeImpl, final AttrImpl attrImpl, final String s, final short n) {
        NodeImpl nodeImpl2 = null;
        if (attrImpl != null) {
            final LCount lookup = LCount.lookup("DOMAttrModified");
            nodeImpl2 = (NodeImpl)attrImpl.getOwnerElement();
            if (lookup.total > 0 && nodeImpl2 != null) {
                final MutationEventImpl mutationEventImpl = new MutationEventImpl();
                mutationEventImpl.initMutationEvent("DOMAttrModified", true, false, attrImpl, s, attrImpl.getNodeValue(), attrImpl.getNodeName(), n);
                nodeImpl2.dispatchEvent(mutationEventImpl);
            }
        }
        if (LCount.lookup("DOMSubtreeModified").total > 0) {
            final MutationEventImpl mutationEventImpl2 = new MutationEventImpl();
            mutationEventImpl2.initMutationEvent("DOMSubtreeModified", true, false, null, null, null, null, (short)0);
            if (attrImpl != null) {
                this.dispatchEvent(attrImpl, mutationEventImpl2);
                if (nodeImpl2 != null) {
                    this.dispatchEvent(nodeImpl2, mutationEventImpl2);
                }
            }
            else {
                this.dispatchEvent(nodeImpl, mutationEventImpl2);
            }
        }
    }
    
    protected void saveEnclosingAttr(final NodeImpl nodeImpl) {
        this.savedEnclosingAttr = null;
        if (LCount.lookup("DOMAttrModified").total > 0) {
            for (NodeImpl nodeImpl2 = nodeImpl; nodeImpl2 != null; nodeImpl2 = nodeImpl2.parentNode()) {
                final short nodeType = nodeImpl2.getNodeType();
                if (nodeType == 2) {
                    final EnclosingAttr savedEnclosingAttr = new EnclosingAttr();
                    savedEnclosingAttr.node = (AttrImpl)nodeImpl2;
                    savedEnclosingAttr.oldvalue = savedEnclosingAttr.node.getNodeValue();
                    this.savedEnclosingAttr = savedEnclosingAttr;
                    return;
                }
                if (nodeType != 5) {
                    if (nodeType != 3) {
                        return;
                    }
                }
            }
        }
    }
    
    void modifyingCharacterData(final NodeImpl nodeImpl, final boolean b) {
        if (this.mutationEvents && !b) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }
    
    void modifiedCharacterData(final NodeImpl nodeImpl, final String s, final String s2, final boolean b) {
        if (this.mutationEvents) {
            this.mutationEventsModifiedCharacterData(nodeImpl, s, s2, b);
        }
    }
    
    private void mutationEventsModifiedCharacterData(final NodeImpl nodeImpl, final String s, final String s2, final boolean b) {
        if (!b) {
            if (LCount.lookup("DOMCharacterDataModified").total > 0) {
                final MutationEventImpl mutationEventImpl = new MutationEventImpl();
                mutationEventImpl.initMutationEvent("DOMCharacterDataModified", true, false, null, s, s2, null, (short)0);
                this.dispatchEvent(nodeImpl, mutationEventImpl);
            }
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }
    
    void replacedCharacterData(final NodeImpl nodeImpl, final String s, final String s2) {
        this.modifiedCharacterData(nodeImpl, s, s2, false);
    }
    
    void insertingNode(final NodeImpl nodeImpl, final boolean b) {
        if (this.mutationEvents && !b) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }
    
    void insertedNode(final NodeImpl nodeImpl, final NodeImpl nodeImpl2, final boolean b) {
        if (this.mutationEvents) {
            this.mutationEventsInsertedNode(nodeImpl, nodeImpl2, b);
        }
        if (this.ranges != null) {
            this.notifyRangesInsertedNode(nodeImpl2);
        }
    }
    
    private void mutationEventsInsertedNode(final NodeImpl nodeImpl, final NodeImpl nodeImpl2, final boolean b) {
        if (LCount.lookup("DOMNodeInserted").total > 0) {
            final MutationEventImpl mutationEventImpl = new MutationEventImpl();
            mutationEventImpl.initMutationEvent("DOMNodeInserted", true, false, nodeImpl, null, null, null, (short)0);
            this.dispatchEvent(nodeImpl2, mutationEventImpl);
        }
        if (LCount.lookup("DOMNodeInsertedIntoDocument").total > 0) {
            NodeImpl nodeImpl3 = nodeImpl;
            if (this.savedEnclosingAttr != null) {
                nodeImpl3 = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement();
            }
            if (nodeImpl3 != null) {
                NodeImpl parentNode = nodeImpl3;
                while (parentNode != null) {
                    nodeImpl3 = parentNode;
                    if (parentNode.getNodeType() == 2) {
                        parentNode = (NodeImpl)((AttrImpl)parentNode).getOwnerElement();
                    }
                    else {
                        parentNode = parentNode.parentNode();
                    }
                }
                if (nodeImpl3.getNodeType() == 9) {
                    final MutationEventImpl mutationEventImpl2 = new MutationEventImpl();
                    mutationEventImpl2.initMutationEvent("DOMNodeInsertedIntoDocument", false, false, null, null, null, null, (short)0);
                    this.dispatchEventToSubtree(nodeImpl2, mutationEventImpl2);
                }
            }
        }
        if (!b) {
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }
    
    private void notifyRangesInsertedNode(final NodeImpl nodeImpl) {
        this.removeStaleRangeReferences();
        final Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            final RangeImpl rangeImpl = ((Reference<RangeImpl>)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.insertedNodeFromDOM(nodeImpl);
            }
            else {
                iterator.remove();
            }
        }
    }
    
    void removingNode(final NodeImpl nodeImpl, final NodeImpl nodeImpl2, final boolean b) {
        if (this.iterators != null) {
            this.notifyIteratorsRemovingNode(nodeImpl2);
        }
        if (this.ranges != null) {
            this.notifyRangesRemovingNode(nodeImpl2);
        }
        if (this.mutationEvents) {
            this.mutationEventsRemovingNode(nodeImpl, nodeImpl2, b);
        }
    }
    
    private void notifyIteratorsRemovingNode(final NodeImpl nodeImpl) {
        this.removeStaleIteratorReferences();
        final Iterator iterator = this.iterators.iterator();
        while (iterator.hasNext()) {
            final NodeIteratorImpl nodeIteratorImpl = ((Reference<NodeIteratorImpl>)iterator.next()).get();
            if (nodeIteratorImpl != null) {
                nodeIteratorImpl.removeNode(nodeImpl);
            }
            else {
                iterator.remove();
            }
        }
    }
    
    private void notifyRangesRemovingNode(final NodeImpl nodeImpl) {
        this.removeStaleRangeReferences();
        final Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            final RangeImpl rangeImpl = ((Reference<RangeImpl>)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.removeNode(nodeImpl);
            }
            else {
                iterator.remove();
            }
        }
    }
    
    private void mutationEventsRemovingNode(final NodeImpl nodeImpl, final NodeImpl nodeImpl2, final boolean b) {
        if (!b) {
            this.saveEnclosingAttr(nodeImpl);
        }
        if (LCount.lookup("DOMNodeRemoved").total > 0) {
            final MutationEventImpl mutationEventImpl = new MutationEventImpl();
            mutationEventImpl.initMutationEvent("DOMNodeRemoved", true, false, nodeImpl, null, null, null, (short)0);
            this.dispatchEvent(nodeImpl2, mutationEventImpl);
        }
        if (LCount.lookup("DOMNodeRemovedFromDocument").total > 0) {
            NodeImpl nodeImpl3 = this;
            if (this.savedEnclosingAttr != null) {
                nodeImpl3 = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement();
            }
            if (nodeImpl3 != null) {
                for (NodeImpl nodeImpl4 = nodeImpl3.parentNode(); nodeImpl4 != null; nodeImpl4 = nodeImpl4.parentNode()) {
                    nodeImpl3 = nodeImpl4;
                }
                if (nodeImpl3.getNodeType() == 9) {
                    final MutationEventImpl mutationEventImpl2 = new MutationEventImpl();
                    mutationEventImpl2.initMutationEvent("DOMNodeRemovedFromDocument", false, false, null, null, null, null, (short)0);
                    this.dispatchEventToSubtree(nodeImpl2, mutationEventImpl2);
                }
            }
        }
    }
    
    void removedNode(final NodeImpl nodeImpl, final boolean b) {
        if (this.mutationEvents && !b) {
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }
    
    void replacingNode(final NodeImpl nodeImpl) {
        if (this.mutationEvents) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }
    
    void replacingData(final NodeImpl nodeImpl) {
        if (this.mutationEvents) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }
    
    void replacedNode(final NodeImpl nodeImpl) {
        if (this.mutationEvents) {
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }
    
    void modifiedAttrValue(final AttrImpl attrImpl, final String s) {
        if (this.mutationEvents) {
            this.dispatchAggregateEvents(attrImpl, attrImpl, s, (short)1);
        }
    }
    
    void setAttrNode(final AttrImpl attrImpl, final AttrImpl attrImpl2) {
        if (this.mutationEvents) {
            if (attrImpl2 == null) {
                this.dispatchAggregateEvents(attrImpl.ownerNode, attrImpl, null, (short)2);
            }
            else {
                this.dispatchAggregateEvents(attrImpl.ownerNode, attrImpl, attrImpl2.getNodeValue(), (short)1);
            }
        }
    }
    
    void removedAttrNode(final AttrImpl attrImpl, final NodeImpl nodeImpl, final String s) {
        if (this.mutationEvents) {
            this.mutationEventsRemovedAttrNode(attrImpl, nodeImpl, s);
        }
    }
    
    private void mutationEventsRemovedAttrNode(final AttrImpl attrImpl, final NodeImpl nodeImpl, final String s) {
        if (LCount.lookup("DOMAttrModified").total > 0) {
            final MutationEventImpl mutationEventImpl = new MutationEventImpl();
            mutationEventImpl.initMutationEvent("DOMAttrModified", true, false, attrImpl, attrImpl.getNodeValue(), null, s, (short)3);
            this.dispatchEvent(nodeImpl, mutationEventImpl);
        }
        this.dispatchAggregateEvents(nodeImpl, null, null, (short)0);
    }
    
    void renamedAttrNode(final Attr attr, final Attr attr2) {
    }
    
    void renamedElement(final Element element, final Element element2) {
    }
    
    class EnclosingAttr implements Serializable
    {
        private static final long serialVersionUID = 5208387723391647216L;
        AttrImpl node;
        String oldvalue;
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
}
