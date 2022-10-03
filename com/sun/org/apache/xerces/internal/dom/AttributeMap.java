package com.sun.org.apache.xerces.internal.dom;

import java.util.List;
import org.w3c.dom.Element;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class AttributeMap extends NamedNodeMapImpl
{
    static final long serialVersionUID = 8872606282138665383L;
    
    protected AttributeMap(final ElementImpl ownerNode, final NamedNodeMapImpl defaults) {
        super(ownerNode);
        if (defaults != null) {
            this.cloneContent(defaults);
            if (this.nodes != null) {
                this.hasDefaults(true);
            }
        }
    }
    
    @Override
    public Node setNamedItem(final Node arg) throws DOMException {
        final boolean errCheck = this.ownerNode.ownerDocument().errorChecking;
        if (errCheck) {
            if (this.isReadOnly()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            if (arg.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException((short)4, msg);
            }
            if (arg.getNodeType() != 2) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException((short)3, msg);
            }
        }
        final AttrImpl argn = (AttrImpl)arg;
        if (!argn.isOwned()) {
            argn.ownerNode = this.ownerNode;
            argn.isOwned(true);
            int i = this.findNamePoint(argn.getNodeName(), 0);
            AttrImpl previous = null;
            if (i >= 0) {
                previous = this.nodes.get(i);
                this.nodes.set(i, arg);
                previous.ownerNode = this.ownerNode.ownerDocument();
                previous.isOwned(false);
                previous.isSpecified(true);
            }
            else {
                i = -1 - i;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(i, arg);
            }
            this.ownerNode.ownerDocument().setAttrNode(argn, previous);
            if (!argn.isNormalized()) {
                this.ownerNode.isNormalized(false);
            }
            return previous;
        }
        if (errCheck && argn.getOwnerElement() != this.ownerNode) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
            throw new DOMException((short)10, msg2);
        }
        return arg;
    }
    
    @Override
    public Node setNamedItemNS(final Node arg) throws DOMException {
        final boolean errCheck = this.ownerNode.ownerDocument().errorChecking;
        if (errCheck) {
            if (this.isReadOnly()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            if (arg.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException((short)4, msg);
            }
            if (arg.getNodeType() != 2) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException((short)3, msg);
            }
        }
        final AttrImpl argn = (AttrImpl)arg;
        if (!argn.isOwned()) {
            argn.ownerNode = this.ownerNode;
            argn.isOwned(true);
            int i = this.findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
            AttrImpl previous = null;
            if (i >= 0) {
                previous = this.nodes.get(i);
                this.nodes.set(i, arg);
                previous.ownerNode = this.ownerNode.ownerDocument();
                previous.isOwned(false);
                previous.isSpecified(true);
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
            this.ownerNode.ownerDocument().setAttrNode(argn, previous);
            if (!argn.isNormalized()) {
                this.ownerNode.isNormalized(false);
            }
            return previous;
        }
        if (errCheck && argn.getOwnerElement() != this.ownerNode) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
            throw new DOMException((short)10, msg2);
        }
        return arg;
    }
    
    @Override
    public Node removeNamedItem(final String name) throws DOMException {
        return this.internalRemoveNamedItem(name, true);
    }
    
    Node safeRemoveNamedItem(final String name) {
        return this.internalRemoveNamedItem(name, false);
    }
    
    protected Node removeItem(final Node item, final boolean addDefault) throws DOMException {
        int index = -1;
        if (this.nodes != null) {
            for (int size = this.nodes.size(), i = 0; i < size; ++i) {
                if (this.nodes.get(i) == item) {
                    index = i;
                    break;
                }
            }
        }
        if (index < 0) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException((short)8, msg);
        }
        return this.remove((AttrImpl)item, index, addDefault);
    }
    
    protected final Node internalRemoveNamedItem(final String name, final boolean raiseEx) {
        if (this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        final int i = this.findNamePoint(name, 0);
        if (i >= 0) {
            return this.remove(this.nodes.get(i), i, true);
        }
        if (raiseEx) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException((short)8, msg2);
        }
        return null;
    }
    
    private final Node remove(final AttrImpl attr, final int index, final boolean addDefault) {
        final CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        final String name = attr.getNodeName();
        if (attr.isIdAttribute()) {
            ownerDocument.removeIdentifier(attr.getValue());
        }
        if (this.hasDefaults() && addDefault) {
            final NamedNodeMapImpl defaults = ((ElementImpl)this.ownerNode).getDefaultAttributes();
            final Node d;
            if (defaults != null && (d = defaults.getNamedItem(name)) != null && this.findNamePoint(name, index + 1) < 0) {
                final NodeImpl clone = (NodeImpl)d.cloneNode(true);
                if (d.getLocalName() != null) {
                    ((AttrNSImpl)clone).namespaceURI = attr.getNamespaceURI();
                }
                clone.ownerNode = this.ownerNode;
                clone.isOwned(true);
                clone.isSpecified(false);
                this.nodes.set(index, clone);
                if (attr.isIdAttribute()) {
                    ownerDocument.putIdentifier(clone.getNodeValue(), (Element)this.ownerNode);
                }
            }
            else {
                this.nodes.remove(index);
            }
        }
        else {
            this.nodes.remove(index);
        }
        attr.ownerNode = ownerDocument;
        attr.isOwned(false);
        attr.isSpecified(true);
        attr.isIdAttribute(false);
        ownerDocument.removedAttrNode(attr, this.ownerNode, name);
        return attr;
    }
    
    @Override
    public Node removeNamedItemNS(final String namespaceURI, final String name) throws DOMException {
        return this.internalRemoveNamedItemNS(namespaceURI, name, true);
    }
    
    Node safeRemoveNamedItemNS(final String namespaceURI, final String name) {
        return this.internalRemoveNamedItemNS(namespaceURI, name, false);
    }
    
    protected final Node internalRemoveNamedItemNS(final String namespaceURI, final String name, final boolean raiseEx) {
        final CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        final int i = this.findNamePoint(namespaceURI, name);
        if (i >= 0) {
            final AttrImpl n = this.nodes.get(i);
            if (n.isIdAttribute()) {
                ownerDocument.removeIdentifier(n.getValue());
            }
            final String nodeName = n.getNodeName();
            if (this.hasDefaults()) {
                final NamedNodeMapImpl defaults = ((ElementImpl)this.ownerNode).getDefaultAttributes();
                final Node d;
                if (defaults != null && (d = defaults.getNamedItem(nodeName)) != null) {
                    final int j = this.findNamePoint(nodeName, 0);
                    if (j >= 0 && this.findNamePoint(nodeName, j + 1) < 0) {
                        final NodeImpl clone = (NodeImpl)d.cloneNode(true);
                        clone.ownerNode = this.ownerNode;
                        if (d.getLocalName() != null) {
                            ((AttrNSImpl)clone).namespaceURI = namespaceURI;
                        }
                        clone.isOwned(true);
                        clone.isSpecified(false);
                        this.nodes.set(i, clone);
                        if (clone.isIdAttribute()) {
                            ownerDocument.putIdentifier(clone.getNodeValue(), (Element)this.ownerNode);
                        }
                    }
                    else {
                        this.nodes.remove(i);
                    }
                }
                else {
                    this.nodes.remove(i);
                }
            }
            else {
                this.nodes.remove(i);
            }
            n.ownerNode = ownerDocument;
            n.isOwned(false);
            n.isSpecified(true);
            n.isIdAttribute(false);
            ownerDocument.removedAttrNode(n, this.ownerNode, name);
            return n;
        }
        if (raiseEx) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException((short)8, msg2);
        }
        return null;
    }
    
    @Override
    public NamedNodeMapImpl cloneMap(final NodeImpl ownerNode) {
        final AttributeMap newmap = new AttributeMap((ElementImpl)ownerNode, null);
        newmap.hasDefaults(this.hasDefaults());
        newmap.cloneContent(this);
        return newmap;
    }
    
    @Override
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
                    final NodeImpl n = srcnodes.get(i);
                    final NodeImpl clone = (NodeImpl)n.cloneNode(true);
                    clone.isSpecified(n.isSpecified());
                    this.nodes.add(clone);
                    clone.ownerNode = this.ownerNode;
                    clone.isOwned(true);
                }
            }
        }
    }
    
    void moveSpecifiedAttributes(final AttributeMap srcmap) {
        final int nsize = (srcmap.nodes != null) ? srcmap.nodes.size() : 0;
        for (int i = nsize - 1; i >= 0; --i) {
            final AttrImpl attr = srcmap.nodes.get(i);
            if (attr.isSpecified()) {
                srcmap.remove(attr, i, false);
                if (attr.getLocalName() != null) {
                    this.setNamedItem(attr);
                }
                else {
                    this.setNamedItemNS(attr);
                }
            }
        }
    }
    
    protected void reconcileDefaults(final NamedNodeMapImpl defaults) {
        final int nsize = (this.nodes != null) ? this.nodes.size() : 0;
        for (int i = nsize - 1; i >= 0; --i) {
            final AttrImpl attr = this.nodes.get(i);
            if (!attr.isSpecified()) {
                this.remove(attr, i, false);
            }
        }
        if (defaults == null) {
            return;
        }
        if (this.nodes == null || this.nodes.size() == 0) {
            this.cloneContent(defaults);
        }
        else {
            for (int dsize = defaults.nodes.size(), n = 0; n < dsize; ++n) {
                final AttrImpl d = defaults.nodes.get(n);
                int j = this.findNamePoint(d.getNodeName(), 0);
                if (j < 0) {
                    j = -1 - j;
                    final NodeImpl clone = (NodeImpl)d.cloneNode(true);
                    clone.ownerNode = this.ownerNode;
                    clone.isOwned(true);
                    clone.isSpecified(false);
                    this.nodes.add(j, clone);
                }
            }
        }
    }
    
    @Override
    protected final int addItem(final Node arg) {
        final AttrImpl argn = (AttrImpl)arg;
        argn.ownerNode = this.ownerNode;
        argn.isOwned(true);
        int i = this.findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
        if (i >= 0) {
            this.nodes.set(i, arg);
        }
        else {
            i = this.findNamePoint(argn.getNodeName(), 0);
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
        this.ownerNode.ownerDocument().setAttrNode(argn, null);
        return i;
    }
}
