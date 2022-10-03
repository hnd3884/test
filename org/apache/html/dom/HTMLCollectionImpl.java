package org.apache.html.dom;

import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableSectionElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLAreaElement;
import org.w3c.dom.html.HTMLObjectElement;
import org.w3c.dom.html.HTMLAppletElement;
import org.w3c.dom.html.HTMLImageElement;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.Element;
import java.io.Serializable;
import org.w3c.dom.html.HTMLCollection;

class HTMLCollectionImpl implements HTMLCollection, Serializable
{
    private static final long serialVersionUID = 9112122196669185082L;
    static final short ANCHOR = 1;
    static final short FORM = 2;
    static final short IMAGE = 3;
    static final short APPLET = 4;
    static final short LINK = 5;
    static final short OPTION = 6;
    static final short ROW = 7;
    static final short ELEMENT = 8;
    static final short AREA = -1;
    static final short TBODY = -2;
    static final short CELL = -3;
    private short _lookingFor;
    private Element _topLevel;
    
    HTMLCollectionImpl(final HTMLElement topLevel, final short lookingFor) {
        if (topLevel == null) {
            throw new NullPointerException("HTM011 Argument 'topLevel' is null.");
        }
        this._topLevel = topLevel;
        this._lookingFor = lookingFor;
    }
    
    public final int getLength() {
        return this.getLength(this._topLevel);
    }
    
    public final Node item(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("HTM012 Argument 'index' is negative.");
        }
        return this.item(this._topLevel, new CollectionIndex(n));
    }
    
    public final Node namedItem(final String s) {
        if (s == null) {
            throw new NullPointerException("HTM013 Argument 'name' is null.");
        }
        return this.namedItem(this._topLevel, s);
    }
    
    private int getLength(final Element element) {
        int n;
        synchronized (element) {
            n = 0;
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node instanceof Element) {
                    if (this.collectionMatch((Element)node, null)) {
                        ++n;
                    }
                    else if (this.recurse()) {
                        n += this.getLength((Element)node);
                    }
                }
            }
        }
        return n;
    }
    
    private Node item(final Element element, final CollectionIndex collectionIndex) {
        synchronized (element) {
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node instanceof Element) {
                    if (this.collectionMatch((Element)node, null)) {
                        if (collectionIndex.isZero()) {
                            return node;
                        }
                        collectionIndex.decrement();
                    }
                    else if (this.recurse()) {
                        final Node item = this.item((Element)node, collectionIndex);
                        if (item != null) {
                            return item;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private Node namedItem(final Element element, final String s) {
        synchronized (element) {
            Node node;
            for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node instanceof Element) {
                    if (this.collectionMatch((Element)node, s)) {
                        return node;
                    }
                    if (this.recurse()) {
                        final Node namedItem = this.namedItem((Element)node, s);
                        if (namedItem != null) {
                            return namedItem;
                        }
                    }
                }
            }
            return node;
        }
    }
    
    protected boolean recurse() {
        return this._lookingFor > 0;
    }
    
    protected boolean collectionMatch(final Element element, final String s) {
        boolean equals;
        synchronized (element) {
            equals = false;
            switch (this._lookingFor) {
                case 1: {
                    equals = (element instanceof HTMLAnchorElement && element.getAttribute("name").length() > 0);
                    break;
                }
                case 2: {
                    equals = (element instanceof HTMLFormElement);
                    break;
                }
                case 3: {
                    equals = (element instanceof HTMLImageElement);
                    break;
                }
                case 4: {
                    equals = (element instanceof HTMLAppletElement || (element instanceof HTMLObjectElement && ("application/java".equals(element.getAttribute("codetype")) || element.getAttribute("classid").startsWith("java:"))));
                    break;
                }
                case 8: {
                    equals = (element instanceof HTMLFormControl);
                    break;
                }
                case 5: {
                    equals = ((element instanceof HTMLAnchorElement || element instanceof HTMLAreaElement) && element.getAttribute("href").length() > 0);
                    break;
                }
                case -1: {
                    equals = (element instanceof HTMLAreaElement);
                    break;
                }
                case 6: {
                    equals = (element instanceof HTMLOptionElement);
                    break;
                }
                case 7: {
                    equals = (element instanceof HTMLTableRowElement);
                    break;
                }
                case -2: {
                    equals = (element instanceof HTMLTableSectionElement && element.getTagName().equals("TBODY"));
                    break;
                }
                case -3: {
                    equals = (element instanceof HTMLTableCellElement);
                    break;
                }
            }
            if (equals && s != null) {
                if (element instanceof HTMLAnchorElement && s.equals(element.getAttribute("name"))) {
                    return true;
                }
                equals = s.equals(element.getAttribute("id"));
            }
        }
        return equals;
    }
}
