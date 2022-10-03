package org.jcp.xml.dsig.internal.dom;

import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import org.w3c.dom.Document;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.xml.security.exceptions.Base64DecodingException;
import javax.xml.crypto.MarshalException;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Element;
import java.util.Collections;
import javax.xml.crypto.XMLStructure;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import java.util.Collection;
import java.util.ArrayList;
import java.security.cert.CertificateFactory;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.X509Data;

public final class DOMX509Data extends DOMStructure implements X509Data
{
    private final List content;
    private CertificateFactory cf;
    
    public DOMX509Data(final List list) {
        if (list == null) {
            throw new NullPointerException("content cannot be null");
        }
        final ArrayList list2 = new ArrayList(list);
        if (list2.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        for (int i = 0; i < list2.size(); ++i) {
            final Object value = list2.get(i);
            if (value instanceof String) {
                new X500Principal((String)value);
            }
            else if (!(value instanceof byte[]) && !(value instanceof X509Certificate) && !(value instanceof X509CRL) && !(value instanceof XMLStructure)) {
                throw new ClassCastException("content[" + i + "] is not a valid X509Data type");
            }
        }
        this.content = Collections.unmodifiableList((List<?>)list2);
    }
    
    public DOMX509Data(final Element element) throws MarshalException {
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1) {
                final Element element2 = (Element)item;
                final String localName = element2.getLocalName();
                if (localName.equals("X509Certificate")) {
                    list.add((Object)this.unmarshalX509Certificate(element2));
                }
                else if (localName.equals("X509IssuerSerial")) {
                    list.add((Object)new DOMX509IssuerSerial(element2));
                }
                else if (localName.equals("X509SubjectName")) {
                    list.add((Object)element2.getFirstChild().getNodeValue());
                }
                else {
                    if (localName.equals("X509SKI")) {
                        try {
                            list.add((Object)Base64.decode(element2));
                            continue;
                        }
                        catch (final Base64DecodingException ex) {
                            throw new MarshalException("cannot decode X509SKI", ex);
                        }
                    }
                    if (localName.equals("X509CRL")) {
                        list.add((Object)this.unmarshalX509CRL(element2));
                    }
                    else {
                        list.add((Object)new javax.xml.crypto.dom.DOMStructure(element2));
                    }
                }
            }
        }
        this.content = Collections.unmodifiableList((List<?>)list);
    }
    
    public List getContent() {
        return this.content;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        final Element element = DOMUtils.createElement(ownerDocument, "X509Data", "http://www.w3.org/2000/09/xmldsig#", s);
        for (int i = 0; i < this.content.size(); ++i) {
            final Object value = this.content.get(i);
            if (value instanceof X509Certificate) {
                this.marshalCert((X509Certificate)value, element, ownerDocument, s);
            }
            else if (value instanceof XMLStructure) {
                if (value instanceof X509IssuerSerial) {
                    ((DOMX509IssuerSerial)value).marshal(element, s, domCryptoContext);
                }
                else {
                    DOMUtils.appendChild(element, ((javax.xml.crypto.dom.DOMStructure)value).getNode());
                }
            }
            else if (value instanceof byte[]) {
                this.marshalSKI((byte[])value, element, ownerDocument, s);
            }
            else if (value instanceof String) {
                this.marshalSubjectName((String)value, element, ownerDocument, s);
            }
            else if (value instanceof X509CRL) {
                this.marshalCRL((X509CRL)value, element, ownerDocument, s);
            }
        }
        node.appendChild(element);
    }
    
    private void marshalSKI(final byte[] array, final Node node, final Document document, final String s) {
        final Element element = DOMUtils.createElement(document, "X509SKI", "http://www.w3.org/2000/09/xmldsig#", s);
        element.appendChild(document.createTextNode(Base64.encode(array)));
        node.appendChild(element);
    }
    
    private void marshalSubjectName(final String s, final Node node, final Document document, final String s2) {
        final Element element = DOMUtils.createElement(document, "X509SubjectName", "http://www.w3.org/2000/09/xmldsig#", s2);
        element.appendChild(document.createTextNode(s));
        node.appendChild(element);
    }
    
    private void marshalCert(final X509Certificate x509Certificate, final Node node, final Document document, final String s) throws MarshalException {
        final Element element = DOMUtils.createElement(document, "X509Certificate", "http://www.w3.org/2000/09/xmldsig#", s);
        try {
            element.appendChild(document.createTextNode(Base64.encode(x509Certificate.getEncoded())));
        }
        catch (final CertificateEncodingException ex) {
            throw new MarshalException("Error encoding X509Certificate", ex);
        }
        node.appendChild(element);
    }
    
    private void marshalCRL(final X509CRL x509CRL, final Node node, final Document document, final String s) throws MarshalException {
        final Element element = DOMUtils.createElement(document, "X509CRL", "http://www.w3.org/2000/09/xmldsig#", s);
        try {
            element.appendChild(document.createTextNode(Base64.encode(x509CRL.getEncoded())));
        }
        catch (final CRLException ex) {
            throw new MarshalException("Error encoding X509CRL", ex);
        }
        node.appendChild(element);
    }
    
    private X509Certificate unmarshalX509Certificate(final Element element) throws MarshalException {
        try {
            return (X509Certificate)this.cf.generateCertificate(this.unmarshalBase64Binary(element));
        }
        catch (final CertificateException ex) {
            throw new MarshalException("Cannot create X509Certificate", ex);
        }
    }
    
    private X509CRL unmarshalX509CRL(final Element element) throws MarshalException {
        try {
            return (X509CRL)this.cf.generateCRL(this.unmarshalBase64Binary(element));
        }
        catch (final CRLException ex) {
            throw new MarshalException("Cannot create X509CRL", ex);
        }
    }
    
    private ByteArrayInputStream unmarshalBase64Binary(final Element element) throws MarshalException {
        try {
            if (this.cf == null) {
                this.cf = CertificateFactory.getInstance("X.509");
            }
            return new ByteArrayInputStream(Base64.decode(element));
        }
        catch (final CertificateException ex) {
            throw new MarshalException("Cannot create CertificateFactory", ex);
        }
        catch (final Base64DecodingException ex2) {
            throw new MarshalException("Cannot decode Base64-encoded val", ex2);
        }
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X509Data)) {
            return false;
        }
        final List content = ((X509Data)o).getContent();
        final int size = this.content.size();
        if (size != content.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            final Object value = this.content.get(i);
            final Object value2 = content.get(i);
            if (value instanceof byte[]) {
                if (!(value2 instanceof byte[]) || !Arrays.equals((byte[])value, (byte[])value2)) {
                    return false;
                }
            }
            else if (!value.equals(value2)) {
                return false;
            }
        }
        return true;
    }
    
    public int hashCode() {
        return 56;
    }
}
