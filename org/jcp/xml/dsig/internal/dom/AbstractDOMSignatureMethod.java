package org.jcp.xml.dsig.internal.dom;

import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.SignedInfo;
import java.security.Key;
import javax.xml.crypto.dsig.SignatureMethod;

abstract class AbstractDOMSignatureMethod extends DOMStructure implements SignatureMethod
{
    abstract boolean verify(final Key p0, final SignedInfo p1, final byte[] p2, final XMLValidateContext p3) throws InvalidKeyException, SignatureException, XMLSignatureException;
    
    abstract byte[] sign(final Key p0, final SignedInfo p1, final XMLSignContext p2) throws InvalidKeyException, XMLSignatureException;
    
    abstract String getJCAAlgorithm();
    
    abstract Type getAlgorithmType();
    
    @Override
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "SignatureMethod", "http://www.w3.org/2000/09/xmldsig#", s);
        DOMUtils.setAttribute(element, "Algorithm", this.getAlgorithm());
        if (this.getParameterSpec() != null) {
            this.marshalParams(element, s);
        }
        node.appendChild(element);
    }
    
    void marshalParams(final Element element, final String s) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + this.getAlgorithm() + " SignatureMethod algorithm");
    }
    
    SignatureMethodParameterSpec unmarshalParams(final Element element) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + this.getAlgorithm() + " SignatureMethod algorithm");
    }
    
    void checkParams(final SignatureMethodParameterSpec signatureMethodParameterSpec) throws InvalidAlgorithmParameterException {
        if (signatureMethodParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + this.getAlgorithm() + " SignatureMethod algorithm");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureMethod)) {
            return false;
        }
        final SignatureMethod signatureMethod = (SignatureMethod)o;
        return this.getAlgorithm().equals(signatureMethod.getAlgorithm()) && this.paramsEqual(signatureMethod.getParameterSpec());
    }
    
    @Override
    public int hashCode() {
        int n = 31 * 17 + this.getAlgorithm().hashCode();
        final AlgorithmParameterSpec parameterSpec = this.getParameterSpec();
        if (parameterSpec != null) {
            n = 31 * n + parameterSpec.hashCode();
        }
        return n;
    }
    
    boolean paramsEqual(final AlgorithmParameterSpec algorithmParameterSpec) {
        return this.getParameterSpec() == algorithmParameterSpec;
    }
    
    enum Type
    {
        DSA, 
        RSA, 
        ECDSA, 
        HMAC;
    }
}
