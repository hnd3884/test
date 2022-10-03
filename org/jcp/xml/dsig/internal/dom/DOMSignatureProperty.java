package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import java.util.Collections;
import javax.xml.crypto.XMLStructure;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.dsig.SignatureProperty;

public final class DOMSignatureProperty extends DOMStructure implements SignatureProperty
{
    private final String id;
    private final String target;
    private final List content;
    
    public DOMSignatureProperty(final List list, final String target, final String id) {
        if (target == null) {
            throw new NullPointerException("target cannot be null");
        }
        if (list == null) {
            throw new NullPointerException("content cannot be null");
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        final ArrayList list2 = new ArrayList(list);
        for (int i = 0; i < list2.size(); ++i) {
            if (!(list2.get(i) instanceof XMLStructure)) {
                throw new ClassCastException("content[" + i + "] is not a valid type");
            }
        }
        this.content = Collections.unmodifiableList((List<?>)list2);
        this.target = target;
        this.id = id;
    }
    
    public DOMSignatureProperty(final Element element) throws MarshalException {
        this.target = DOMUtils.getAttributeValue(element, "Target");
        if (this.target == null) {
            throw new MarshalException("target cannot be null");
        }
        this.id = DOMUtils.getAttributeValue(element, "Id");
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            list.add((Object)new javax.xml.crypto.dom.DOMStructure(childNodes.item(i)));
        }
        if (list.isEmpty()) {
            throw new MarshalException("content cannot be empty");
        }
        this.content = Collections.unmodifiableList((List<?>)list);
    }
    
    public List getContent() {
        return this.content;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "SignatureProperty", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttributeID(element, "Id", this.id);
        DOMUtils.setAttribute(element, "Target", this.target);
        for (int i = 0; i < this.content.size(); ++i) {
            DOMUtils.appendChild(element, ((javax.xml.crypto.dom.DOMStructure)this.content.get(i)).getNode());
        }
        node.appendChild(element);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureProperty)) {
            return false;
        }
        final SignatureProperty signatureProperty = (SignatureProperty)o;
        final boolean b = (this.id == null) ? (signatureProperty.getId() == null) : this.id.equals(signatureProperty.getId());
        return this.equalsContent(signatureProperty.getContent()) && this.target.equals(signatureProperty.getTarget()) && b;
    }
    
    public int hashCode() {
        return 50;
    }
    
    private boolean equalsContent(final List list) {
        final int size = list.size();
        if (this.content.size() != size) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            final XMLStructure xmlStructure = list.get(i);
            final XMLStructure xmlStructure2 = this.content.get(i);
            if (xmlStructure instanceof javax.xml.crypto.dom.DOMStructure) {
                if (!(xmlStructure2 instanceof javax.xml.crypto.dom.DOMStructure)) {
                    return false;
                }
                if (!DOMUtils.nodesEqual(((javax.xml.crypto.dom.DOMStructure)xmlStructure2).getNode(), ((javax.xml.crypto.dom.DOMStructure)xmlStructure).getNode())) {
                    return false;
                }
            }
            else if (!xmlStructure2.equals(xmlStructure)) {
                return false;
            }
        }
        return true;
    }
}
