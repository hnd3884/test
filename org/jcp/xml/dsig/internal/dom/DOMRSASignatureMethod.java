package org.jcp.xml.dsig.internal.dom;

import java.security.PrivateKey;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import java.io.ByteArrayOutputStream;
import javax.xml.crypto.XMLCryptoContext;
import org.jcp.xml.dsig.internal.SignerOutputStream;
import java.util.logging.Level;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import javax.xml.crypto.dsig.XMLValidateContext;
import java.security.Key;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Signature;
import java.util.logging.Logger;

public final class DOMRSASignatureMethod extends DOMSignatureMethod
{
    private static Logger log;
    private Signature signature;
    
    public DOMRSASignatureMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        super("http://www.w3.org/2000/09/xmldsig#rsa-sha1", algorithmParameterSpec);
    }
    
    public DOMRSASignatureMethod(final Element element) throws MarshalException {
        super(element);
    }
    
    protected void checkParams(final SignatureMethodParameterSpec signatureMethodParameterSpec) throws InvalidAlgorithmParameterException {
        if (signatureMethodParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for RSA signature algorithm");
        }
    }
    
    protected SignatureMethodParameterSpec unmarshalParams(final Element element) throws MarshalException {
        throw new MarshalException("no parameters should be specified for RSA signature algorithm");
    }
    
    protected void marshalParams(final Element element, final String s) throws MarshalException {
        throw new MarshalException("no parameters should be specified for RSA signature algorithm");
    }
    
    protected boolean paramsEqual(final AlgorithmParameterSpec algorithmParameterSpec) {
        return this.getParameterSpec() == algorithmParameterSpec;
    }
    
    public boolean verify(final Key key, final DOMSignedInfo domSignedInfo, final byte[] array, final XMLValidateContext xmlValidateContext) throws InvalidKeyException, SignatureException, XMLSignatureException {
        if (key == null || domSignedInfo == null || array == null) {
            throw new NullPointerException("key, signed info or signature cannot be null");
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("key must be PublicKey");
        }
        if (this.signature == null) {
            try {
                this.signature = Signature.getInstance("SHA1withRSA");
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new SignatureException("SHA1withRSA Signature not found");
            }
        }
        this.signature.initVerify((PublicKey)key);
        if (DOMRSASignatureMethod.log.isLoggable(Level.FINE)) {
            DOMRSASignatureMethod.log.log(Level.FINE, "Signature provider:" + this.signature.getProvider());
            DOMRSASignatureMethod.log.log(Level.FINE, "verifying with key: " + key);
        }
        domSignedInfo.canonicalize(xmlValidateContext, new SignerOutputStream(this.signature));
        return this.signature.verify(array);
    }
    
    public byte[] sign(final Key key, final DOMSignedInfo domSignedInfo, final XMLSignContext xmlSignContext) throws InvalidKeyException, XMLSignatureException {
        if (key == null || domSignedInfo == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
        }
        if (this.signature == null) {
            try {
                this.signature = Signature.getInstance("SHA1withRSA");
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new InvalidKeyException("SHA1withRSA Signature not found");
            }
        }
        this.signature.initSign((PrivateKey)key);
        if (DOMRSASignatureMethod.log.isLoggable(Level.FINE)) {
            DOMRSASignatureMethod.log.log(Level.FINE, "Signature provider:" + this.signature.getProvider());
            DOMRSASignatureMethod.log.log(Level.FINE, "Signing with key: " + key);
        }
        domSignedInfo.canonicalize(xmlSignContext, new SignerOutputStream(this.signature));
        try {
            return this.signature.sign();
        }
        catch (final SignatureException ex2) {
            throw new RuntimeException(ex2.getMessage());
        }
    }
    
    static {
        DOMRSASignatureMethod.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
}
