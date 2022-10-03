package org.jcp.xml.dsig.internal.dom;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.Data;
import org.w3c.dom.Document;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import java.net.URISyntaxException;
import java.net.URI;
import javax.xml.crypto.dsig.Transform;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import org.w3c.dom.Attr;
import java.util.List;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

public final class DOMRetrievalMethod extends DOMStructure implements RetrievalMethod, DOMURIReference
{
    private final List transforms;
    private String uri;
    private String type;
    private Attr here;
    
    public DOMRetrievalMethod(final String uri, final String type, final List list) {
        if (uri == null) {
            throw new NullPointerException("uri cannot be null");
        }
        if (list == null || list.isEmpty()) {
            this.transforms = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list2 = new ArrayList(list);
            for (int i = 0; i < list2.size(); ++i) {
                if (!(list2.get(i) instanceof Transform)) {
                    throw new ClassCastException("transforms[" + i + "] is not a valid type");
                }
            }
            this.transforms = Collections.unmodifiableList((List<?>)list2);
        }
        this.uri = uri;
        if (uri != null && !uri.equals("")) {
            try {
                new URI(uri);
            }
            catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
        this.type = type;
    }
    
    public DOMRetrievalMethod(final Element element, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        this.uri = DOMUtils.getAttributeValue(element, "URI");
        this.type = DOMUtils.getAttributeValue(element, "Type");
        this.here = element.getAttributeNodeNS(null, "URI");
        final ArrayList list = new ArrayList();
        final Element firstChildElement = DOMUtils.getFirstChildElement(element);
        if (firstChildElement != null) {
            for (Element element2 = DOMUtils.getFirstChildElement(firstChildElement); element2 != null; element2 = DOMUtils.getNextSiblingElement(element2)) {
                list.add(new DOMTransform(element2, xmlCryptoContext, provider));
            }
        }
        if (list.isEmpty()) {
            this.transforms = Collections.EMPTY_LIST;
        }
        else {
            this.transforms = Collections.unmodifiableList((List<?>)list);
        }
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public String getType() {
        return this.type;
    }
    
    public List getTransforms() {
        return this.transforms;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        final Element element = DOMUtils.createElement(ownerDocument, "RetrievalMethod", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttribute(element, "URI", this.uri);
        DOMUtils.setAttribute(element, "Type", this.type);
        if (!this.transforms.isEmpty()) {
            final Element element2 = DOMUtils.createElement(ownerDocument, "Transforms", "http://www.w3.org/2000/09/xmldsig#", s);
            element.appendChild(element2);
            for (int i = 0; i < this.transforms.size(); ++i) {
                ((DOMTransform)this.transforms.get(i)).marshal(element2, s, domCryptoContext);
            }
        }
        node.appendChild(element);
        this.here = element.getAttributeNodeNS(null, "URI");
    }
    
    public Node getHere() {
        return this.here;
    }
    
    public Data dereference(final XMLCryptoContext xmlCryptoContext) throws URIReferenceException {
        if (xmlCryptoContext == null) {
            throw new NullPointerException("context cannot be null");
        }
        URIDereferencer uriDereferencer = xmlCryptoContext.getURIDereferencer();
        if (uriDereferencer == null) {
            uriDereferencer = DOMURIDereferencer.INSTANCE;
        }
        Data data = uriDereferencer.dereference(this, xmlCryptoContext);
        try {
            for (int i = 0; i < this.transforms.size(); ++i) {
                data = ((DOMTransform)this.transforms.get(i)).transform(data, xmlCryptoContext);
            }
        }
        catch (final Exception ex) {
            throw new URIReferenceException(ex);
        }
        return data;
    }
    
    public XMLStructure dereferenceAsXMLStructure(final XMLCryptoContext xmlCryptoContext) throws URIReferenceException {
        try {
            final ApacheData apacheData = (ApacheData)this.dereference(xmlCryptoContext);
            final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
            instance.setNamespaceAware(true);
            final Element documentElement = instance.newDocumentBuilder().parse(new ByteArrayInputStream(apacheData.getXMLSignatureInput().getBytes())).getDocumentElement();
            if (documentElement.getLocalName().equals("X509Data")) {
                return new DOMX509Data(documentElement);
            }
            return null;
        }
        catch (final Exception ex) {
            throw new URIReferenceException(ex);
        }
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RetrievalMethod)) {
            return false;
        }
        final RetrievalMethod retrievalMethod = (RetrievalMethod)o;
        final boolean b = (this.type == null) ? (retrievalMethod.getType() == null) : this.type.equals(retrievalMethod.getType());
        return this.uri.equals(retrievalMethod.getURI()) && this.transforms.equals(retrievalMethod.getTransforms()) && b;
    }
    
    public int hashCode() {
        return 48;
    }
}
