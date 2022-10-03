package org.cyberneko.html.filters;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import java.util.Hashtable;

public class ElementRemover extends DefaultFilter
{
    protected static final Object NULL;
    protected Hashtable fAcceptedElements;
    protected Hashtable fRemovedElements;
    protected int fElementDepth;
    protected int fRemovalElementDepth;
    
    public ElementRemover() {
        this.fAcceptedElements = new Hashtable();
        this.fRemovedElements = new Hashtable();
    }
    
    public void acceptElement(final String element, final String[] attributes) {
        final Object key = element.toLowerCase();
        Object value = ElementRemover.NULL;
        if (attributes != null) {
            final String[] newarray = new String[attributes.length];
            for (int i = 0; i < attributes.length; ++i) {
                newarray[i] = attributes[i].toLowerCase();
            }
            value = attributes;
        }
        this.fAcceptedElements.put(key, value);
    }
    
    public void removeElement(final String element) {
        final Object key = element.toLowerCase();
        final Object value = ElementRemover.NULL;
        this.fRemovedElements.put(key, value);
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) throws XNIException {
        this.fElementDepth = 0;
        this.fRemovalElementDepth = Integer.MAX_VALUE;
        super.startDocument(locator, encoding, nscontext, augs);
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }
    
    public void startPrefixMapping(final String prefix, final String uri, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.startPrefixMapping(prefix, uri, augs);
        }
    }
    
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth && this.handleOpenTag(element, attributes)) {
            super.startElement(element, attributes, augs);
        }
        ++this.fElementDepth;
    }
    
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth && this.handleOpenTag(element, attributes)) {
            super.emptyElement(element, attributes, augs);
        }
    }
    
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.comment(text, augs);
        }
    }
    
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.processingInstruction(target, data, augs);
        }
    }
    
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.characters(text, augs);
        }
    }
    
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.ignorableWhitespace(text, augs);
        }
    }
    
    public void startGeneralEntity(final String name, final XMLResourceIdentifier id, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.startGeneralEntity(name, id, encoding, augs);
        }
    }
    
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.textDecl(version, encoding, augs);
        }
    }
    
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.endGeneralEntity(name, augs);
        }
    }
    
    public void startCDATA(final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.startCDATA(augs);
        }
    }
    
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.endCDATA(augs);
        }
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth && this.elementAccepted(element.rawname)) {
            super.endElement(element, augs);
        }
        --this.fElementDepth;
        if (this.fElementDepth == this.fRemovalElementDepth) {
            this.fRemovalElementDepth = Integer.MAX_VALUE;
        }
    }
    
    public void endPrefixMapping(final String prefix, final Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.endPrefixMapping(prefix, augs);
        }
    }
    
    protected boolean elementAccepted(final String element) {
        final Object key = element.toLowerCase();
        return this.fAcceptedElements.containsKey(key);
    }
    
    protected boolean elementRemoved(final String element) {
        final Object key = element.toLowerCase();
        return this.fRemovedElements.containsKey(key);
    }
    
    protected boolean handleOpenTag(final QName element, final XMLAttributes attributes) {
        if (this.elementAccepted(element.rawname)) {
            final Object key = element.rawname.toLowerCase();
            final Object value = this.fAcceptedElements.get(key);
            if (value != ElementRemover.NULL) {
                final String[] anames = (String[])value;
                int attributeCount = attributes.getLength();
                int i = 0;
            Label_0058:
                while (i < attributeCount) {
                    final String aname = attributes.getQName(i).toLowerCase();
                    while (true) {
                        for (int j = 0; j < anames.length; ++j) {
                            if (anames[j].equals(aname)) {
                                ++i;
                                continue Label_0058;
                            }
                        }
                        attributes.removeAttributeAt(i--);
                        --attributeCount;
                        continue;
                    }
                }
            }
            else {
                attributes.removeAllAttributes();
            }
            return true;
        }
        if (this.elementRemoved(element.rawname)) {
            this.fRemovalElementDepth = this.fElementDepth;
        }
        return false;
    }
    
    static {
        NULL = new Object();
    }
}
