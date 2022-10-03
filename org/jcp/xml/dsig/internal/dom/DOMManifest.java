package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import java.util.Collections;
import javax.xml.crypto.dsig.Reference;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.dsig.Manifest;

public final class DOMManifest extends DOMStructure implements Manifest
{
    private final List references;
    private final String id;
    
    public DOMManifest(final List list, final String id) {
        if (list == null) {
            throw new NullPointerException("references cannot be null");
        }
        final ArrayList list2 = new ArrayList(list);
        if (list2.isEmpty()) {
            throw new IllegalArgumentException("list of references must contain at least one entry");
        }
        for (int i = 0; i < list2.size(); ++i) {
            if (!(list2.get(i) instanceof Reference)) {
                throw new ClassCastException("references[" + i + "] is not a valid type");
            }
        }
        this.references = Collections.unmodifiableList((List<?>)list2);
        this.id = id;
    }
    
    public DOMManifest(final Element element, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        this.id = DOMUtils.getAttributeValue(element, "Id");
        Element element2 = DOMUtils.getFirstChildElement(element);
        final ArrayList list = new ArrayList();
        while (element2 != null) {
            list.add(new DOMReference(element2, xmlCryptoContext, provider));
            element2 = DOMUtils.getNextSiblingElement(element2);
        }
        this.references = Collections.unmodifiableList((List<?>)list);
    }
    
    public String getId() {
        return this.id;
    }
    
    public List getReferences() {
        return this.references;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "Manifest", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttributeID(element, "Id", this.id);
        for (int i = 0; i < this.references.size(); ++i) {
            ((DOMReference)this.references.get(i)).marshal(element, s, domCryptoContext);
        }
        node.appendChild(element);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Manifest)) {
            return false;
        }
        final Manifest manifest = (Manifest)o;
        return ((this.id == null) ? (manifest.getId() == null) : this.id.equals(manifest.getId())) && this.references.equals(manifest.getReferences());
    }
    
    public int hashCode() {
        return 46;
    }
}
