package org.apache.xml.security.algorithms.implementations;

import org.apache.commons.logging.LogFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Key;
import java.security.SignatureException;
import org.apache.xml.security.utils.Base64;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.algorithms.JCEMapper;
import java.io.IOException;
import java.security.Signature;
import org.apache.commons.logging.Log;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;

public abstract class SignatureECDSA extends SignatureAlgorithmSpi
{
    static Log log;
    private Signature _signatureAlgorithm;
    
    public abstract String engineGetURI();
    
    private static byte[] convertASN1toXMLDSIG(final byte[] array) throws IOException {
        int n;
        byte b;
        for (b = (byte)(n = array[3]); n > 0 && array[4 + b - n] == 0; --n) {}
        int n2;
        byte b2;
        for (b2 = (byte)(n2 = array[5 + b]); n2 > 0 && array[6 + b + b2 - n2] == 0; --n2) {}
        if (array[0] != 48 || array[1] != array.length - 2 || array[2] != 2 || n > 24 || array[4 + b] != 2 || n2 > 24) {
            throw new IOException("Invalid ASN.1 format of ECDSA signature");
        }
        final byte[] array2 = new byte[48];
        System.arraycopy(array, 4 + b - n, array2, 24 - n, n);
        System.arraycopy(array, 6 + b + b2 - n2, array2, 48 - n2, n2);
        return array2;
    }
    
    private static byte[] convertXMLDSIGtoASN1(final byte[] array) throws IOException {
        if (array.length != 48) {
            throw new IOException("Invalid XMLDSIG format of ECDSA signature");
        }
        int n;
        for (n = 24; n > 0 && array[24 - n] == 0; --n) {}
        int n2 = n;
        if (array[24 - n] < 0) {
            ++n2;
        }
        int n3;
        for (n3 = 24; n3 > 0 && array[48 - n3] == 0; --n3) {}
        int n4 = n3;
        if (array[48 - n3] < 0) {
            ++n4;
        }
        final byte[] array2 = new byte[6 + n2 + n4];
        array2[0] = 48;
        array2[1] = (byte)(4 + n2 + n4);
        array2[2] = 2;
        array2[3] = (byte)n2;
        System.arraycopy(array, 24 - n, array2, 4 + n2 - n, n);
        array2[4 + n2] = 2;
        array2[5 + n2] = (byte)n4;
        System.arraycopy(array, 48 - n3, array2, 6 + n2 + n4 - n3, n3);
        return array2;
    }
    
    public SignatureECDSA() throws XMLSignatureException {
        this._signatureAlgorithm = null;
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        if (SignatureECDSA.log.isDebugEnabled()) {
            SignatureECDSA.log.debug((Object)("Created SignatureECDSA using " + translateURItoJCEID));
        }
        final String providerId = JCEMapper.getProviderId();
        try {
            if (providerId == null) {
                this._signatureAlgorithm = Signature.getInstance(translateURItoJCEID);
            }
            else {
                this._signatureAlgorithm = Signature.getInstance(translateURItoJCEID, providerId);
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex.getLocalizedMessage() });
        }
        catch (final NoSuchProviderException ex2) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex2.getLocalizedMessage() });
        }
    }
    
    protected void engineSetParameter(final AlgorithmParameterSpec parameter) throws XMLSignatureException {
        try {
            this._signatureAlgorithm.setParameter(parameter);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected boolean engineVerify(final byte[] array) throws XMLSignatureException {
        try {
            final byte[] convertXMLDSIGtoASN1 = convertXMLDSIGtoASN1(array);
            if (SignatureECDSA.log.isDebugEnabled()) {
                SignatureECDSA.log.debug((Object)("Called ECDSA.verify() on " + Base64.encode(array)));
            }
            return this._signatureAlgorithm.verify(convertXMLDSIGtoASN1);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        catch (final IOException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
    }
    
    protected void engineInitVerify(final Key key) throws XMLSignatureException {
        if (!(key instanceof PublicKey)) {
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { key.getClass().getName(), PublicKey.class.getName() });
        }
        try {
            this._signatureAlgorithm.initVerify((PublicKey)key);
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            return convertASN1toXMLDSIG(this._signatureAlgorithm.sign());
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        catch (final IOException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
    }
    
    protected void engineInitSign(final Key key, final SecureRandom secureRandom) throws XMLSignatureException {
        if (!(key instanceof PrivateKey)) {
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { key.getClass().getName(), PrivateKey.class.getName() });
        }
        try {
            this._signatureAlgorithm.initSign((PrivateKey)key, secureRandom);
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineInitSign(final Key key) throws XMLSignatureException {
        if (!(key instanceof PrivateKey)) {
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { key.getClass().getName(), PrivateKey.class.getName() });
        }
        try {
            this._signatureAlgorithm.initSign((PrivateKey)key);
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineUpdate(final byte[] array) throws XMLSignatureException {
        try {
            this._signatureAlgorithm.update(array);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineUpdate(final byte b) throws XMLSignatureException {
        try {
            this._signatureAlgorithm.update(b);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws XMLSignatureException {
        try {
            this._signatureAlgorithm.update(array, n, n2);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected String engineGetJCEAlgorithmString() {
        return this._signatureAlgorithm.getAlgorithm();
    }
    
    protected String engineGetJCEProviderName() {
        return this._signatureAlgorithm.getProvider().getName();
    }
    
    protected void engineSetHMACOutputLength(final int n) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
    }
    
    protected void engineInitSign(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
    }
    
    static {
        SignatureECDSA.log = LogFactory.getLog(SignatureECDSA.class.getName());
    }
    
    public static class SignatureECDSASHA1 extends SignatureECDSA
    {
        public SignatureECDSASHA1() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
        }
    }
}
