package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import javax.xml.crypto.XMLStructure;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.dsig.XMLObject;

public final class DOMXMLObject extends DOMStructure implements XMLObject
{
    private final String id;
    private final String mimeType;
    private final String encoding;
    private final List content;
    
    public DOMXMLObject(final List list, final String id, final String mimeType, final String encoding) {
        if (list == null || list.isEmpty()) {
            this.content = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list2 = new ArrayList(list);
            for (int i = 0; i < list2.size(); ++i) {
                if (!(list2.get(i) instanceof XMLStructure)) {
                    throw new ClassCastException("content[" + i + "] is not a valid type");
                }
            }
            this.content = Collections.unmodifiableList((List<?>)list2);
        }
        this.id = id;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }
    
    public DOMXMLObject(final Element element, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        this.encoding = DOMUtils.getAttributeValue(element, "Encoding");
        this.id = DOMUtils.getAttributeValue(element, "Id");
        this.mimeType = DOMUtils.getAttributeValue(element, "MimeType");
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1) {
                final Element element2 = (Element)item;
                final String localName = element2.getLocalName();
                if (localName.equals("Manifest")) {
                    list.add((Object)new DOMManifest(element2, xmlCryptoContext, provider));
                    continue;
                }
                if (localName.equals("SignatureProperties")) {
                    list.add((Object)new DOMSignatureProperties(element2));
                    continue;
                }
                if (localName.equals("X509Data")) {
                    list.add((Object)new DOMX509Data(element2));
                    continue;
                }
            }
            list.add((Object)new javax.xml.crypto.dom.DOMStructure(item));
        }
        if (list.isEmpty()) {
            this.content = Collections.EMPTY_LIST;
        }
        else {
            this.content = Collections.unmodifiableList((List<?>)list);
        }
    }
    
    public List getContent() {
        return this.content;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "Object", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttributeID(element, "Id", this.id);
        DOMUtils.setAttribute(element, "MimeType", this.mimeType);
        DOMUtils.setAttribute(element, "Encoding", this.encoding);
        for (int i = 0; i < this.content.size(); ++i) {
            final XMLStructure xmlStructure = this.content.get(i);
            if (xmlStructure instanceof DOMStructure) {
                ((DOMStructure)xmlStructure).marshal(element, s, domCryptoContext);
            }
            else {
                DOMUtils.appendChild(element, ((javax.xml.crypto.dom.DOMStructure)xmlStructure).getNode());
            }
        }
        node.appendChild(element);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XMLObject)) {
            return false;
        }
        final XMLObject xmlObject = (XMLObject)o;
        final boolean b = (this.id == null) ? (xmlObject.getId() == null) : this.id.equals(xmlObject.getId());
        final boolean b2 = (this.encoding == null) ? (xmlObject.getEncoding() == null) : this.encoding.equals(xmlObject.getEncoding());
        final boolean b3 = (this.mimeType == null) ? (xmlObject.getMimeType() == null) : this.mimeType.equals(xmlObject.getMimeType());
        return b && b2 && b3 && this.equalsContent(xmlObject.getContent());
    }
    
    public int hashCode() {
        return 53;
    }
    
    private boolean equalsContent(final List list) {
        if (this.content.size() != list.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
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
