package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import org.apache.xml.security.utils.Base64;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.Reference;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.io.InputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.SignedInfo;

public final class DOMSignedInfo extends DOMStructure implements SignedInfo
{
    private static Logger log;
    private List references;
    private CanonicalizationMethod canonicalizationMethod;
    private SignatureMethod signatureMethod;
    private String id;
    private Document ownerDoc;
    private Element localSiElem;
    private InputStream canonData;
    
    public DOMSignedInfo(final CanonicalizationMethod canonicalizationMethod, final SignatureMethod signatureMethod, final List list) {
        if (canonicalizationMethod == null || signatureMethod == null || list == null) {
            throw new NullPointerException();
        }
        this.canonicalizationMethod = canonicalizationMethod;
        this.signatureMethod = signatureMethod;
        this.references = Collections.unmodifiableList((List<?>)new ArrayList<Object>(list));
        if (this.references.isEmpty()) {
            throw new IllegalArgumentException("list of references must contain at least one entry");
        }
        for (int i = 0; i < this.references.size(); ++i) {
            if (!(this.references.get(i) instanceof Reference)) {
                throw new ClassCastException("list of references contains an illegal type");
            }
        }
    }
    
    public DOMSignedInfo(final CanonicalizationMethod canonicalizationMethod, final SignatureMethod signatureMethod, final List list, final String id) {
        this(canonicalizationMethod, signatureMethod, list);
        this.id = id;
    }
    
    public DOMSignedInfo(final Element localSiElem, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        this.localSiElem = localSiElem;
        this.ownerDoc = localSiElem.getOwnerDocument();
        this.id = DOMUtils.getAttributeValue(localSiElem, "Id");
        final Element firstChildElement = DOMUtils.getFirstChildElement(localSiElem);
        this.canonicalizationMethod = new DOMCanonicalizationMethod(firstChildElement, xmlCryptoContext, provider);
        final Element nextSiblingElement = DOMUtils.getNextSiblingElement(firstChildElement);
        this.signatureMethod = DOMSignatureMethod.unmarshal(nextSiblingElement);
        final ArrayList list = new ArrayList(5);
        for (Element element = DOMUtils.getNextSiblingElement(nextSiblingElement); element != null; element = DOMUtils.getNextSiblingElement(element)) {
            list.add(new DOMReference(element, xmlCryptoContext, provider));
        }
        this.references = Collections.unmodifiableList((List<?>)list);
    }
    
    public CanonicalizationMethod getCanonicalizationMethod() {
        return this.canonicalizationMethod;
    }
    
    public SignatureMethod getSignatureMethod() {
        return this.signatureMethod;
    }
    
    public String getId() {
        return this.id;
    }
    
    public List getReferences() {
        return this.references;
    }
    
    public InputStream getCanonicalizedData() {
        return this.canonData;
    }
    
    public void canonicalize(final XMLCryptoContext xmlCryptoContext, final ByteArrayOutputStream byteArrayOutputStream) throws XMLSignatureException {
        if (xmlCryptoContext == null) {
            throw new NullPointerException("context cannot be null");
        }
        final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(byteArrayOutputStream);
        try {
            unsyncBufferedOutputStream.close();
        }
        catch (final IOException ex) {}
        final DOMSubTreeData domSubTreeData = new DOMSubTreeData(this.localSiElem, true);
        try {
            ((DOMCanonicalizationMethod)this.canonicalizationMethod).canonicalize(domSubTreeData, xmlCryptoContext, unsyncBufferedOutputStream);
        }
        catch (final TransformException ex2) {
            throw new XMLSignatureException(ex2);
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        if (DOMSignedInfo.log.isLoggable(Level.FINE)) {
            final InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(byteArray));
            final char[] array = new char[byteArray.length];
            try {
                inputStreamReader.read(array);
                DOMSignedInfo.log.log(Level.FINE, "Canonicalized SignedInfo:\n" + new String(array));
            }
            catch (final IOException ex3) {
                DOMSignedInfo.log.log(Level.FINE, "IOException reading SignedInfo bytes");
            }
            DOMSignedInfo.log.log(Level.FINE, "Data to be signed/verified:" + Base64.encode(byteArray));
        }
        this.canonData = new ByteArrayInputStream(byteArray);
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        this.ownerDoc = DOMUtils.getOwnerDocument(node);
        final Element element = DOMUtils.createElement(this.ownerDoc, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#", s);
        ((DOMCanonicalizationMethod)this.canonicalizationMethod).marshal(element, s, domCryptoContext);
        ((DOMSignatureMethod)this.signatureMethod).marshal(element, s, domCryptoContext);
        for (int i = 0; i < this.references.size(); ++i) {
            ((DOMReference)this.references.get(i)).marshal(element, s, domCryptoContext);
        }
        DOMUtils.setAttributeID(element, "Id", this.id);
        node.appendChild(element);
        this.localSiElem = element;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignedInfo)) {
            return false;
        }
        final SignedInfo signedInfo = (SignedInfo)o;
        final boolean b = (this.id == null) ? (signedInfo.getId() == null) : this.id.equals(signedInfo.getId());
        return this.canonicalizationMethod.equals(signedInfo.getCanonicalizationMethod()) && this.signatureMethod.equals(signedInfo.getSignatureMethod()) && this.references.equals(signedInfo.getReferences()) && b;
    }
    
    public int hashCode() {
        return 59;
    }
    
    static {
        DOMSignedInfo.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
}
