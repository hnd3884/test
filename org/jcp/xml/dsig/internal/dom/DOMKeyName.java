package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.crypto.dsig.keyinfo.KeyName;

public final class DOMKeyName extends DOMStructure implements KeyName
{
    private final String name;
    
    public DOMKeyName(final String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.name = name;
    }
    
    public DOMKeyName(final Element element) {
        this.name = element.getFirstChild().getNodeValue();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        final Element element = DOMUtils.createElement(ownerDocument, "KeyName", "http://www.w3.org/2000/09/xmldsig#", s);
        element.appendChild(ownerDocument.createTextNode(this.name));
        node.appendChild(element);
    }
    
    public boolean equals(final Object o) {
        return this == o || (o instanceof KeyName && this.name.equals(((KeyName)o).getName()));
    }
    
    public int hashCode() {
        return 44;
    }
}
