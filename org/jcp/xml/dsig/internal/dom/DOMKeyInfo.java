package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.crypto.MarshalException;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import java.util.Collections;
import javax.xml.crypto.XMLStructure;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public final class DOMKeyInfo extends DOMStructure implements KeyInfo
{
    private final String id;
    private final List keyInfoTypes;
    
    public DOMKeyInfo(final List list, final String id) {
        if (list == null) {
            throw new NullPointerException("content cannot be null");
        }
        final ArrayList list2 = new ArrayList(list);
        if (list2.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        for (int i = 0; i < list2.size(); ++i) {
            if (!(list2.get(i) instanceof XMLStructure)) {
                throw new ClassCastException("content[" + i + "] is not a valid KeyInfo type");
            }
        }
        this.keyInfoTypes = Collections.unmodifiableList((List<?>)list2);
        this.id = id;
    }
    
    public DOMKeyInfo(final Element element, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        this.id = DOMUtils.getAttributeValue(element, "Id");
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        if (length < 1) {
            throw new MarshalException("KeyInfo must contain at least one type");
        }
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1) {
                final Element element2 = (Element)item;
                final String localName = element2.getLocalName();
                if (localName.equals("X509Data")) {
                    list.add((Object)new DOMX509Data(element2));
                }
                else if (localName.equals("KeyName")) {
                    list.add((Object)new DOMKeyName(element2));
                }
                else if (localName.equals("KeyValue")) {
                    list.add((Object)new DOMKeyValue(element2));
                }
                else if (localName.equals("RetrievalMethod")) {
                    list.add((Object)new DOMRetrievalMethod(element2, xmlCryptoContext, provider));
                }
                else if (localName.equals("PGPData")) {
                    list.add((Object)new DOMPGPData(element2));
                }
                else {
                    list.add((Object)new javax.xml.crypto.dom.DOMStructure(element2));
                }
            }
        }
        this.keyInfoTypes = Collections.unmodifiableList((List<?>)list);
    }
    
    public String getId() {
        return this.id;
    }
    
    public List getContent() {
        return this.keyInfoTypes;
    }
    
    public void marshal(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws MarshalException {
        if (xmlStructure == null) {
            throw new NullPointerException("parent is null");
        }
        final Node node = ((javax.xml.crypto.dom.DOMStructure)xmlStructure).getNode();
        final String signaturePrefix = DOMUtils.getSignaturePrefix(xmlCryptoContext);
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", signaturePrefix);
        if (signaturePrefix == null) {
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
        }
        else {
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + signaturePrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        this.marshal(node, element, null, signaturePrefix, (DOMCryptoContext)xmlCryptoContext);
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        this.marshal(node, null, s, domCryptoContext);
    }
    
    public void marshal(final Node node, final Node node2, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        this.marshal(node, DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", s), node2, s, domCryptoContext);
    }
    
    private void marshal(final Node node, final Element element, final Node node2, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        for (int i = 0; i < this.keyInfoTypes.size(); ++i) {
            final XMLStructure xmlStructure = this.keyInfoTypes.get(i);
            if (xmlStructure instanceof DOMStructure) {
                ((DOMStructure)xmlStructure).marshal(element, s, domCryptoContext);
            }
            else {
                DOMUtils.appendChild(element, ((javax.xml.crypto.dom.DOMStructure)xmlStructure).getNode());
            }
        }
        DOMUtils.setAttributeID(element, "Id", this.id);
        node.insertBefore(element, node2);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyInfo)) {
            return false;
        }
        final KeyInfo keyInfo = (KeyInfo)o;
        final boolean b = (this.id == null) ? (keyInfo.getId() == null) : this.id.equals(keyInfo.getId());
        return this.keyInfoTypes.equals(keyInfo.getContent()) && b;
    }
    
    public int hashCode() {
        return 43;
    }
}
