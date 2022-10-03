package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Key;
import java.security.SignatureException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import java.security.Signature;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;

public abstract class SignatureBaseRSA extends SignatureAlgorithmSpi
{
    private static final Logger LOG;
    private Signature signatureAlgorithm;
    
    public abstract String engineGetURI();
    
    public SignatureBaseRSA() throws XMLSignatureException {
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        SignatureBaseRSA.LOG.debug("Created SignatureRSA using {}", translateURItoJCEID);
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
            return this.signatureAlgorithm.verify(array);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException(ex);
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
                SignatureBaseRSA.LOG.debug("Exception when reinstantiating Signature: {}", ex2);
                this.signatureAlgorithm = signatureAlgorithm;
            }
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            return this.signatureAlgorithm.sign();
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException(ex);
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
        LOG = LoggerFactory.getLogger(SignatureBaseRSA.class);
    }
    
    public static class SignatureRSASHA1 extends SignatureBaseRSA
    {
        public SignatureRSASHA1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
    }
    
    public static class SignatureRSASHA224 extends SignatureBaseRSA
    {
        public SignatureRSASHA224() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
        }
    }
    
    public static class SignatureRSASHA256 extends SignatureBaseRSA
    {
        public SignatureRSASHA256() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        }
    }
    
    public static class SignatureRSASHA384 extends SignatureBaseRSA
    {
        public SignatureRSASHA384() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        }
    }
    
    public static class SignatureRSASHA512 extends SignatureBaseRSA
    {
        public SignatureRSASHA512() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        }
    }
    
    public static class SignatureRSARIPEMD160 extends SignatureBaseRSA
    {
        public SignatureRSARIPEMD160() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
        }
    }
    
    public static class SignatureRSAMD5 extends SignatureBaseRSA
    {
        public SignatureRSAMD5() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
        }
    }
    
    public static class SignatureRSASHA1MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA1MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA224MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA224MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA256MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA256MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA384MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA384MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA512MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA512MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA3_224MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA3_224MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA3_256MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA3_256MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA3_384MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA3_384MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1";
        }
    }
    
    public static class SignatureRSASHA3_512MGF1 extends SignatureBaseRSA
    {
        public SignatureRSASHA3_512MGF1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1";
        }
    }
}
