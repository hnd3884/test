package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import java.util.Collections;
import javax.xml.crypto.dsig.SignatureProperty;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.dsig.SignatureProperties;

public final class DOMSignatureProperties extends DOMStructure implements SignatureProperties
{
    private final String id;
    private final List properties;
    
    public DOMSignatureProperties(final List list, final String id) {
        if (list == null) {
            throw new NullPointerException("properties cannot be null");
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("properties cannot be empty");
        }
        final ArrayList list2 = new ArrayList(list);
        for (int i = 0; i < list2.size(); ++i) {
            if (!(list2.get(i) instanceof SignatureProperty)) {
                throw new ClassCastException("properties[" + i + "] is not a valid type");
            }
        }
        this.properties = Collections.unmodifiableList((List<?>)list2);
        this.id = id;
    }
    
    public DOMSignatureProperties(final Element element) throws MarshalException {
        this.id = DOMUtils.getAttributeValue(element, "Id");
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1) {
                list.add((Object)new DOMSignatureProperty((Element)item));
            }
        }
        if (list.isEmpty()) {
            throw new MarshalException("properties cannot be empty");
        }
        this.properties = Collections.unmodifiableList((List<?>)list);
    }
    
    public List getProperties() {
        return this.properties;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "SignatureProperties", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttributeID(element, "Id", this.id);
        for (int i = 0; i < this.properties.size(); ++i) {
            ((DOMSignatureProperty)this.properties.get(i)).marshal(element, s, domCryptoContext);
        }
        node.appendChild(element);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureProperties)) {
            return false;
        }
        final SignatureProperties signatureProperties = (SignatureProperties)o;
        final boolean b = (this.id == null) ? (signatureProperties.getId() == null) : this.id.equals(signatureProperties.getId());
        return this.properties.equals(signatureProperties.getProperties()) && b;
    }
    
    public int hashCode() {
        return 49;
    }
}
