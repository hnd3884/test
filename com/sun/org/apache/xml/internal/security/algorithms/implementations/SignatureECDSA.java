package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Key;
import java.security.SignatureException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import java.io.IOException;
import java.security.Signature;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;

public abstract class SignatureECDSA extends SignatureAlgorithmSpi
{
    private static final Logger LOG;
    private Signature signatureAlgorithm;
    
    public abstract String engineGetURI();
    
    public static byte[] convertASN1toXMLDSIG(final byte[] array) throws IOException {
        return ECDSAUtils.convertASN1toXMLDSIG(array);
    }
    
    public static byte[] convertXMLDSIGtoASN1(final byte[] array) throws IOException {
        return ECDSAUtils.convertXMLDSIGtoASN1(array);
    }
    
    public SignatureECDSA() throws XMLSignatureException {
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        SignatureECDSA.LOG.debug("Created SignatureECDSA using {}", translateURItoJCEID);
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
            final byte[] convertXMLDSIGtoASN1 = convertXMLDSIGtoASN1(array);
            if (SignatureECDSA.LOG.isDebugEnabled()) {
                SignatureECDSA.LOG.debug("Called ECDSA.verify() on " + XMLUtils.encodeToString(array));
            }
            return this.signatureAlgorithm.verify(convertXMLDSIGtoASN1);
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
                SignatureECDSA.LOG.debug("Exception when reinstantiating Signature: {}", ex2);
                this.signatureAlgorithm = signatureAlgorithm;
            }
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            return convertASN1toXMLDSIG(this.signatureAlgorithm.sign());
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final IOException ex2) {
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
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
    }
    
    static {
        LOG = LoggerFactory.getLogger(SignatureECDSA.class);
    }
    
    public static class SignatureECDSASHA1 extends SignatureECDSA
    {
        public SignatureECDSASHA1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
        }
    }
    
    public static class SignatureECDSASHA224 extends SignatureECDSA
    {
        public SignatureECDSASHA224() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
        }
    }
    
    public static class SignatureECDSASHA256 extends SignatureECDSA
    {
        public SignatureECDSASHA256() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
        }
    }
    
    public static class SignatureECDSASHA384 extends SignatureECDSA
    {
        public SignatureECDSASHA384() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
        }
    }
    
    public static class SignatureECDSASHA512 extends SignatureECDSA
    {
        public SignatureECDSASHA512() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
        }
    }
    
    public static class SignatureECDSARIPEMD160 extends SignatureECDSA
    {
        public SignatureECDSARIPEMD160() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
        }
    }
}
