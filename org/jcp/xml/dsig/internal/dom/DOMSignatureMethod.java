package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import javax.xml.crypto.dsig.XMLValidateContext;
import java.security.Key;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.SignatureMethod;

public abstract class DOMSignatureMethod extends DOMStructure implements SignatureMethod
{
    private String algorithm;
    private SignatureMethodParameterSpec params;
    
    protected DOMSignatureMethod(final String algorithm, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm cannot be null");
        }
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof SignatureMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec");
        }
        this.checkParams((SignatureMethodParameterSpec)algorithmParameterSpec);
        this.algorithm = algorithm;
        this.params = (SignatureMethodParameterSpec)algorithmParameterSpec;
    }
    
    protected DOMSignatureMethod(final Element element) throws MarshalException {
        this.algorithm = DOMUtils.getAttributeValue(element, "Algorithm");
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
    
    static SignatureMethod unmarshal(final Element element) throws MarshalException {
        final String attributeValue = DOMUtils.getAttributeValue(element, "Algorithm");
        if (attributeValue.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
            return new DOMHMACSignatureMethod(element);
        }
        if (attributeValue.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
            return new DOMRSASignatureMethod(element);
        }
        if (attributeValue.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
            return new DOMDSASignatureMethod(element);
        }
        throw new MarshalException("unsupported signature algorithm: " + attributeValue);
    }
    
    protected abstract void checkParams(final SignatureMethodParameterSpec p0) throws InvalidAlgorithmParameterException;
    
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }
    
    public final String getAlgorithm() {
        return this.algorithm;
    }
    
    protected abstract SignatureMethodParameterSpec unmarshalParams(final Element p0) throws MarshalException;
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "SignatureMethod", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttribute(element, "Algorithm", this.algorithm);
        if (this.params != null) {
            this.marshalParams(element, s);
        }
        node.appendChild(element);
    }
    
    public abstract boolean verify(final Key p0, final DOMSignedInfo p1, final byte[] p2, final XMLValidateContext p3) throws InvalidKeyException, SignatureException, XMLSignatureException;
    
    public abstract byte[] sign(final Key p0, final DOMSignedInfo p1, final XMLSignContext p2) throws InvalidKeyException, XMLSignatureException;
    
    protected abstract void marshalParams(final Element p0, final String p1) throws MarshalException;
    
    protected abstract boolean paramsEqual(final AlgorithmParameterSpec p0);
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureMethod)) {
            return false;
        }
        final SignatureMethod signatureMethod = (SignatureMethod)o;
        return this.algorithm.equals(signatureMethod.getAlgorithm()) && this.paramsEqual(signatureMethod.getParameterSpec());
    }
    
    public int hashCode() {
        return 57;
    }
}
