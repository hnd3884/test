package org.jcp.xml.dsig.internal.dom;

import java.security.PrivateKey;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import java.io.IOException;
import java.util.logging.Level;
import java.io.ByteArrayOutputStream;
import javax.xml.crypto.XMLCryptoContext;
import org.jcp.xml.dsig.internal.SignerOutputStream;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import java.security.Key;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Signature;
import java.util.logging.Logger;

public final class DOMDSASignatureMethod extends DOMSignatureMethod
{
    private static Logger log;
    private Signature signature;
    
    public DOMDSASignatureMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        super("http://www.w3.org/2000/09/xmldsig#dsa-sha1", algorithmParameterSpec);
    }
    
    public DOMDSASignatureMethod(final Element element) throws MarshalException {
        super(element);
    }
    
    protected void checkParams(final SignatureMethodParameterSpec signatureMethodParameterSpec) throws InvalidAlgorithmParameterException {
        if (signatureMethodParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for DSA signature algorithm");
        }
    }
    
    protected SignatureMethodParameterSpec unmarshalParams(final Element element) throws MarshalException {
        throw new MarshalException("no parameters should be specified for DSA signature algorithm");
    }
    
    protected void marshalParams(final Element element, final String s) throws MarshalException {
        throw new MarshalException("no parameters should be specified for DSA signature algorithm");
    }
    
    protected boolean paramsEqual(final AlgorithmParameterSpec algorithmParameterSpec) {
        return this.getParameterSpec() == algorithmParameterSpec;
    }
    
    public boolean verify(final Key key, final DOMSignedInfo domSignedInfo, final byte[] array, final XMLValidateContext xmlValidateContext) throws InvalidKeyException, SignatureException, XMLSignatureException {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        if (array == null) {
            throw new NullPointerException("signature cannot be null");
        }
        if (domSignedInfo == null) {
            throw new NullPointerException("signedInfo cannot be null");
        }
        if (this.signature == null) {
            try {
                this.signature = Signature.getInstance("SHA1withDSA");
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new SignatureException("SHA1withDSA Signature not found");
            }
        }
        try {
            if (!(key instanceof PublicKey)) {
                throw new InvalidKeyException("key must be PublicKey");
            }
            this.signature.initVerify((PublicKey)key);
            domSignedInfo.canonicalize(xmlValidateContext, new SignerOutputStream(this.signature));
            if (DOMDSASignatureMethod.log.isLoggable(Level.FINE)) {
                DOMDSASignatureMethod.log.log(Level.FINE, "verifying with key: " + key);
            }
            return this.signature.verify(convertXMLDSIGtoASN1(array));
        }
        catch (final IOException ex2) {
            throw new RuntimeException(ex2.getMessage());
        }
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
                this.signature = Signature.getInstance("SHA1withDSA");
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new InvalidKeyException("SHA1withDSA Signature not found");
            }
        }
        if (DOMDSASignatureMethod.log.isLoggable(Level.FINE)) {
            DOMDSASignatureMethod.log.log(Level.FINE, "Signing with key: " + key);
        }
        this.signature.initSign((PrivateKey)key);
        domSignedInfo.canonicalize(xmlSignContext, new SignerOutputStream(this.signature));
        try {
            return convertASN1toXMLDSIG(this.signature.sign());
        }
        catch (final SignatureException ex2) {
            throw new RuntimeException(ex2.getMessage());
        }
        catch (final IOException ex3) {
            throw new RuntimeException(ex3.getMessage());
        }
    }
    
    private static byte[] convertASN1toXMLDSIG(final byte[] array) throws IOException {
        int n;
        byte b;
        for (b = (byte)(n = array[3]); n > 0 && array[4 + b - n] == 0; --n) {}
        int n2;
        byte b2;
        for (b2 = (byte)(n2 = array[5 + b]); n2 > 0 && array[6 + b + b2 - n2] == 0; --n2) {}
        if (array[0] != 48 || array[1] != array.length - 2 || array[2] != 2 || n > 20 || array[4 + b] != 2 || n2 > 20) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }
        final byte[] array2 = new byte[40];
        System.arraycopy(array, 4 + b - n, array2, 20 - n, n);
        System.arraycopy(array, 6 + b + b2 - n2, array2, 40 - n2, n2);
        return array2;
    }
    
    private static byte[] convertXMLDSIGtoASN1(final byte[] array) throws IOException {
        if (array.length != 40) {
            throw new IOException("Invalid XMLDSIG format of DSA signature");
        }
        int n;
        for (n = 20; n > 0 && array[20 - n] == 0; --n) {}
        int n2 = n;
        if (array[20 - n] < 0) {
            ++n2;
        }
        int n3;
        for (n3 = 20; n3 > 0 && array[40 - n3] == 0; --n3) {}
        int n4 = n3;
        if (array[40 - n3] < 0) {
            ++n4;
        }
        final byte[] array2 = new byte[6 + n2 + n4];
        array2[0] = 48;
        array2[1] = (byte)(4 + n2 + n4);
        array2[2] = 2;
        array2[3] = (byte)n2;
        System.arraycopy(array, 20 - n, array2, 4 + n2 - n, n);
        array2[4 + n2] = 2;
        array2[5 + n2] = (byte)n4;
        System.arraycopy(array, 40 - n3, array2, 6 + n2 + n4 - n3, n3);
        return array2;
    }
    
    static {
        DOMDSASignatureMethod.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
}
