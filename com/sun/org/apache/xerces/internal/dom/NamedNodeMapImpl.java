package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import java.util.List;
import java.io.Serializable;
import org.w3c.dom.NamedNodeMap;

public class NamedNodeMapImpl implements NamedNodeMap, Serializable
{
    static final long serialVersionUID = -7039242451046758020L;
    protected short flags;
    protected static final short READONLY = 1;
    protected static final short CHANGED = 2;
    protected static final short HASDEFAULTS = 4;
    protected List nodes;
    protected NodeImpl ownerNode;
    
    protected NamedNodeMapImpl(final NodeImpl ownerNode) {
        this.ownerNode = ownerNode;
    }
    
    @Override
    public int getLength() {
        return (this.nodes != null) ? this.nodes.size() : 0;
    }
    
    @Override
    public Node item(final int index) {
        return (this.nodes != null && index < this.nodes.size()) ? this.nodes.get(index) : null;
    }
    
    @Override
    public Node getNamedItem(final String name) {
        final int i = this.findNamePoint(name, 0);
        return (i < 0) ? null : this.nodes.get(i);
    }
    
    @Override
    public Node getNamedItemNS(final String namespaceURI, final String localName) {
        final int i = this.findNamePoint(namespaceURI, localName);
        return (i < 0) ? null : this.nodes.get(i);
    }
    
    @Override
    public Node setNamedItem(final Node arg) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        if (ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            if (arg.getOwnerDocument() != ownerDocument) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException((short)4, msg);
            }
        }
        int i = this.findNamePoint(arg.getNodeName(), 0);
        NodeImpl previous = null;
        if (i >= 0) {
            previous = this.nodes.get(i);
            this.nodes.set(i, arg);
        }
        else {
            i = -1 - i;
            if (null == this.nodes) {
                this.nodes = new ArrayList(5);
            }
            this.nodes.add(i, arg);
        }
        return previous;
    }
    
    @Override
    public Node setNamedItemNS(final Node arg) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        if (ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            if (arg.getOwnerDocument() != ownerDocument) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException((short)4, msg);
            }
        }
        int i = this.findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
        NodeImpl previous = null;
        if (i >= 0) {
            previous = this.nodes.get(i);
            this.nodes.set(i, arg);
        }
        else {
            i = this.findNamePoint(arg.getNodeName(), 0);
            if (i >= 0) {
                previous = this.nodes.get(i);
                this.nodes.add(i, arg);
            }
            else {
                i = -1 - i;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(i, arg);
            }
        }
        return previous;
    }
    
    @Override
    public Node removeNamedItem(final String name) throws DOMException {
        if (this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        final int i = this.findNamePoint(name, 0);
        if (i < 0) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException((short)8, msg2);
        }
        final NodeImpl n = this.nodes.get(i);
        this.nodes.remove(i);
        return n;
    }
    
    @Override
    public Node removeNamedItemNS(final String namespaceURI, final String name) throws DOMException {
        if (this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        final int i = this.findNamePoint(namespaceURI, name);
        if (i < 0) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException((short)8, msg2);
        }
        final NodeImpl n = this.nodes.get(i);
        this.nodes.remove(i);
        return n;
    }
    
    public NamedNodeMapImpl cloneMap(final NodeImpl ownerNode) {
        final NamedNodeMapImpl newmap = new NamedNodeMapImpl(ownerNode);
        newmap.cloneContent(this);
        return newmap;
    }
    
    protected void cloneContent(final NamedNodeMapImpl srcmap) {
        final List srcnodes = srcmap.nodes;
        if (srcnodes != null) {
            final int size = srcnodes.size();
            if (size != 0) {
                if (this.nodes == null) {
                    this.nodes = new ArrayList(size);
                }
                else {
                    this.nodes.clear();
                }
                for (int i = 0; i < size; ++i) {
                    final NodeImpl n = srcmap.nodes.get(i);
                    final NodeImpl clone = (NodeImpl)n.cloneNode(true);
                    clone.isSpecified(n.isSpecified());
                    this.nodes.add(clone);
                }
            }
        }
    }
    
    void setReadOnly(final boolean readOnly, final boolean deep) {
        this.isReadOnly(readOnly);
        if (deep && this.nodes != null) {
            for (int i = this.nodes.size() - 1; i >= 0; --i) {
                this.nodes.get(i).setReadOnly(readOnly, deep);
            }
        }
    }
    
    boolean getReadOnly() {
        return this.isReadOnly();
    }
    
    protected void setOwnerDocument(final CoreDocumentImpl doc) {
        if (this.nodes != null) {
            for (int size = this.nodes.size(), i = 0; i < size; ++i) {
                ((NodeImpl)this.item(i)).setOwnerDocument(doc);
            }
        }
    }
    
    final boolean isReadOnly() {
        return (this.flags & 0x1) != 0x0;
    }
    
    final void isReadOnly(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x1) : (this.flags & 0xFFFFFFFE));
    }
    
    final boolean changed() {
        return (this.flags & 0x2) != 0x0;
    }
    
    final void changed(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x2) : (this.flags & 0xFFFFFFFD));
    }
    
    final boolean hasDefaults() {
        return (this.flags & 0x4) != 0x0;
    }
    
    final void hasDefaults(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x4) : (this.flags & 0xFFFFFFFB));
    }
    
    protected int findNamePoint(final String name, final int start) {
        int i = 0;
        if (this.nodes != null) {
            int first = start;
            int last = this.nodes.size() - 1;
            while (first <= last) {
                i = (first + last) / 2;
                final int test = name.compareTo(this.nodes.get(i).getNodeName());
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
    
    protected int findNamePoint(final String namespaceURI, final String name) {
        if (this.nodes == null) {
            return -1;
        }
        if (name == null) {
            return -1;
        }
        for (int size = this.nodes.size(), i = 0; i < size; ++i) {
            final NodeImpl a = this.nodes.get(i);
            final String aNamespaceURI = a.getNamespaceURI();
            final String aLocalName = a.getLocalName();
            if (namespaceURI == null) {
                if (aNamespaceURI == null && (name.equals(aLocalName) || (aLocalName == null && name.equals(a.getNodeName())))) {
                    return i;
                }
            }
            else if (namespaceURI.equals(aNamespaceURI) && name.equals(aLocalName)) {
                return i;
            }
        }
        return -1;
    }
    
    protected boolean precedes(final Node a, final Node b) {
        if (this.nodes != null) {
            for (int size = this.nodes.size(), i = 0; i < size; ++i) {
                final Node n = this.nodes.get(i);
                if (n == a) {
                    return true;
                }
                if (n == b) {
                    return false;
                }
            }
        }
        return false;
    }
    
    protected void removeItem(final int index) {
        if (this.nodes != null && index < this.nodes.size()) {
            this.nodes.remove(index);
        }
    }
    
    protected Object getItem(final int index) {
        if (this.nodes != null) {
            return this.nodes.get(index);
        }
        return null;
    }
    
    protected int addItem(final Node arg) {
        int i = this.findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
        if (i >= 0) {
            this.nodes.set(i, arg);
        }
        else {
            i = this.findNamePoint(arg.getNodeName(), 0);
            if (i >= 0) {
                this.nodes.add(i, arg);
            }
            else {
                i = -1 - i;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(i, arg);
            }
        }
        return i;
    }
    
    protected ArrayList cloneMap(ArrayList list) {
        if (list == null) {
            list = new ArrayList(5);
        }
        list.clear();
        if (this.nodes != null) {
            for (int size = this.nodes.size(), i = 0; i < size; ++i) {
                list.add(this.nodes.get(i));
            }
        }
        return list;
    }
    
    protected int getNamedItemIndex(final String namespaceURI, final String localName) {
        return this.findNamePoint(namespaceURI, localName);
    }
    
    public void removeAll() {
        if (this.nodes != null) {
            this.nodes.clear();
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.nodes != null) {
            this.nodes = new ArrayList(this.nodes);
        }
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        final List oldNodes = this.nodes;
        try {
            if (oldNodes != null) {
                this.nodes = new Vector(oldNodes);
            }
            out.defaultWriteObject();
        }
        finally {
            this.nodes = oldNodes;
        }
    }
}
