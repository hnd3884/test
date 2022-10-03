package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.DSAKey;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Key;
import java.io.IOException;
import java.security.SignatureException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import java.security.Signature;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;

public class SignatureDSA extends SignatureAlgorithmSpi
{
    public static final String URI = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    private static final Logger LOG;
    private Signature signatureAlgorithm;
    private int size;
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    }
    
    public SignatureDSA() throws XMLSignatureException {
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        SignatureDSA.LOG.debug("Created SignatureDSA using {}", translateURItoJCEID);
        final String providerId = JCEMapper.getProviderId();
        try {
            if (providerId == null) {
                this.signatureAlgorithm = Signature.getInstance(translateURItoJCEID);
            }
            else {
                this.signatureAlgorithm = Signature.getInstance(translateURItoJCEID, providerId);
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex.getLocalizedMessage() });
        }
        catch (final NoSuchProviderException ex2) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex2.getLocalizedMessage() });
        }
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec parameter) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.setParameter(parameter);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws XMLSignatureException {
        try {
            if (SignatureDSA.LOG.isDebugEnabled()) {
                SignatureDSA.LOG.debug("Called DSA.verify() on " + XMLUtils.encodeToString(array));
            }
            return this.signatureAlgorithm.verify(JavaUtils.convertDsaXMLDSIGtoASN1(array, this.size / 8));
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final IOException ex2) {
            throw new XMLSignatureException(ex2);
        }
    }
    
    @Override
    protected void engineInitVerify(final Key key) throws XMLSignatureException {
        if (!(key instanceof PublicKey)) {
            Object name = null;
            if (key != null) {
                name = key.getClass().getName();
            }
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { name, PublicKey.class.getName() });
        }
        try {
            this.signatureAlgorithm.initVerify((PublicKey)key);
        }
        catch (final InvalidKeyException ex) {
            final Signature signatureAlgorithm = this.signatureAlgorithm;
            try {
                this.signatureAlgorithm = Signature.getInstance(this.signatureAlgorithm.getAlgorithm());
            }
            catch (final Exception ex2) {
                SignatureDSA.LOG.debug("Exception when reinstantiating Signature: {}", ex2);
                this.signatureAlgorithm = signatureAlgorithm;
            }
            throw new XMLSignatureException(ex);
        }
        this.size = ((DSAKey)key).getParams().getQ().bitLength();
    }
    
    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            return JavaUtils.convertDsaASN1toXMLDSIG(this.signatureAlgorithm.sign(), this.size / 8);
        }
        catch (final IOException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final SignatureException ex2) {
            throw new XMLSignatureException(ex2);
        }
    }
    
    @Override
    protected void engineInitSign(final Key key, final SecureRandom secureRandom) throws XMLSignatureException {
        if (!(key instanceof PrivateKey)) {
            Object name = null;
            if (key != null) {
                name = key.getClass().getName();
            }
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { name, PrivateKey.class.getName() });
        }
        try {
            if (secureRandom == null) {
                this.signatureAlgorithm.initSign((PrivateKey)key);
            }
            else {
                this.signatureAlgorithm.initSign((PrivateKey)key, secureRandom);
            }
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
        this.size = ((DSAKey)key).getParams().getQ().bitLength();
    }
    
    @Override
    protected void engineInitSign(final Key key) throws XMLSignatureException {
        this.engineInitSign(key, (SecureRandom)null);
    }
    
    @Override
    protected void engineUpdate(final byte[] array) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(array);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected void engineUpdate(final byte b) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(b);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(array, n, n2);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected String engineGetJCEAlgorithmString() {
        return this.signatureAlgorithm.getAlgorithm();
    }
    
    @Override
    protected String engineGetJCEProviderName() {
        return this.signatureAlgorithm.getProvider().getName();
    }
    
    @Override
    protected void engineSetHMACOutputLength(final int n) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
    }
    
    @Override
    protected void engineInitSign(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnDSA");
    }
    
    static {
        LOG = LoggerFactory.getLogger(SignatureDSA.class);
    }
    
    public static class SHA256 extends SignatureDSA
    {
        public SHA256() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2009/xmldsig11#dsa-sha256";
        }
    }
}
