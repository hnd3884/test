package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.io.ByteArrayOutputStream;
import javax.xml.crypto.XMLCryptoContext;
import org.jcp.xml.dsig.internal.MacOutputStream;
import javax.xml.crypto.dsig.XMLValidateContext;
import java.security.Key;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.util.logging.Level;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.jcp.xml.dsig.internal.HmacSHA1;
import java.util.logging.Logger;

public final class DOMHMACSignatureMethod extends DOMSignatureMethod
{
    private static Logger log;
    private HmacSHA1 hmac;
    private int outputLength;
    
    public DOMHMACSignatureMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        super("http://www.w3.org/2000/09/xmldsig#hmac-sha1", algorithmParameterSpec);
        this.hmac = new HmacSHA1();
    }
    
    public DOMHMACSignatureMethod(final Element element) throws MarshalException {
        super(element);
        this.hmac = new HmacSHA1();
    }
    
    protected void checkParams(final SignatureMethodParameterSpec signatureMethodParameterSpec) throws InvalidAlgorithmParameterException {
        if (signatureMethodParameterSpec != null) {
            if (!(signatureMethodParameterSpec instanceof HMACParameterSpec)) {
                throw new InvalidAlgorithmParameterException("params must be of type HMACParameterSpec");
            }
            this.outputLength = ((HMACParameterSpec)signatureMethodParameterSpec).getOutputLength();
            if (DOMHMACSignatureMethod.log.isLoggable(Level.FINE)) {
                DOMHMACSignatureMethod.log.log(Level.FINE, "Setting outputLength from HMACParameterSpec to: " + this.outputLength);
            }
        }
        else {
            this.outputLength = -1;
        }
    }
    
    protected SignatureMethodParameterSpec unmarshalParams(final Element element) throws MarshalException {
        this.outputLength = new Integer(element.getFirstChild().getNodeValue());
        if (DOMHMACSignatureMethod.log.isLoggable(Level.FINE)) {
            DOMHMACSignatureMethod.log.log(Level.FINE, "unmarshalled outputLength: " + this.outputLength);
        }
        return new HMACParameterSpec(this.outputLength);
    }
    
    protected void marshalParams(final Element element, final String s) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(element);
        final Element element2 = DOMUtils.createElement(ownerDocument, "HMACOutputLength", "http://www.w3.org/2000/09/xmldsig#", s);
        element2.appendChild(ownerDocument.createTextNode(String.valueOf(this.outputLength)));
        element.appendChild(element2);
    }
    
    public boolean verify(final Key key, final DOMSignedInfo domSignedInfo, final byte[] array, final XMLValidateContext xmlValidateContext) throws InvalidKeyException, SignatureException, XMLSignatureException {
        if (key == null || domSignedInfo == null || array == null) {
            throw new NullPointerException("key, signedinfo or signature data can't be null");
        }
        if (DOMHMACSignatureMethod.log.isLoggable(Level.FINE)) {
            DOMHMACSignatureMethod.log.log(Level.FINE, "outputLength = " + this.outputLength);
        }
        this.hmac.init(key, this.outputLength);
        domSignedInfo.canonicalize(xmlValidateContext, new MacOutputStream(this.hmac));
        return this.hmac.verify(array);
    }
    
    public byte[] sign(final Key key, final DOMSignedInfo domSignedInfo, final XMLSignContext xmlSignContext) throws InvalidKeyException, XMLSignatureException {
        if (key == null || domSignedInfo == null) {
            throw new NullPointerException();
        }
        this.hmac.init(key, this.outputLength);
        domSignedInfo.canonicalize(xmlSignContext, new MacOutputStream(this.hmac));
        try {
            return this.hmac.sign();
        }
        catch (final SignatureException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public boolean paramsEqual(final AlgorithmParameterSpec algorithmParameterSpec) {
        return this.getParameterSpec() == algorithmParameterSpec || (algorithmParameterSpec instanceof HMACParameterSpec && this.outputLength == ((HMACParameterSpec)algorithmParameterSpec).getOutputLength());
    }
    
    static {
        DOMHMACSignatureMethod.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
}
