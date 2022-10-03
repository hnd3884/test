package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.DigestMethod;

public abstract class DOMDigestMethod extends DOMStructure implements DigestMethod
{
    private DigestMethodParameterSpec params;
    
    protected DOMDigestMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof DigestMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type DigestMethodParameterSpec");
        }
        this.checkParams((DigestMethodParameterSpec)algorithmParameterSpec);
        this.params = (DigestMethodParameterSpec)algorithmParameterSpec;
    }
    
    protected DOMDigestMethod(final Element element) throws MarshalException {
        final Element firstChildElement = DOMUtils.getFirstChildElement(element);
        if (firstChildElement != null) {
            this.params = this.unmarshalParams(firstChildElement);
        }
        try {
            this.checkParams(this.params);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new MarshalException(ex);
        }
    }
    
    static DigestMethod unmarshal(final Element element) throws MarshalException {
        final String attributeValue = DOMUtils.getAttributeValue(element, "Algorithm");
        if (attributeValue.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
            return DOMSHADigestMethod.SHA1(element);
        }
        if (attributeValue.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
            return DOMSHADigestMethod.SHA256(element);
        }
        if (attributeValue.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
            return DOMSHADigestMethod.SHA512(element);
        }
        throw new MarshalException("unsupported digest algorithm: " + attributeValue);
    }
    
    protected abstract void checkParams(final DigestMethodParameterSpec p0) throws InvalidAlgorithmParameterException;
    
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }
    
    protected abstract DigestMethodParameterSpec unmarshalParams(final Element p0) throws MarshalException;
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttribute(element, "Algorithm", this.getAlgorithm());
        if (this.params != null) {
            this.marshalParams(element, s);
        }
        node.appendChild(element);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DigestMethod)) {
            return false;
        }
        final DigestMethod digestMethod = (DigestMethod)o;
        final boolean b = (this.params == null) ? (digestMethod.getParameterSpec() == null) : this.params.equals(digestMethod.getParameterSpec());
        return this.getAlgorithm().equals(digestMethod.getAlgorithm()) && b;
    }
    
    public int hashCode() {
        return 51;
    }
    
    protected abstract void marshalParams(final Element p0, final String p1) throws MarshalException;
    
    abstract String getMessageDigestAlgorithm();
}
