package org.apache.xml.security.algorithms.implementations;

import org.apache.commons.logging.LogFactory;
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
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.algorithms.JCEMapper;
import java.security.Signature;
import org.apache.commons.logging.Log;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;

public abstract class SignatureBaseRSA extends SignatureAlgorithmSpi
{
    static Log log;
    private Signature _signatureAlgorithm;
    
    public abstract String engineGetURI();
    
    public SignatureBaseRSA() throws XMLSignatureException {
        this._signatureAlgorithm = null;
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        if (SignatureBaseRSA.log.isDebugEnabled()) {
            SignatureBaseRSA.log.debug((Object)("Created SignatureDSA using " + translateURItoJCEID));
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
            return this._signatureAlgorithm.verify(array);
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
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
            return this._signatureAlgorithm.sign();
        }
        catch (final SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
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
        SignatureBaseRSA.log = LogFactory.getLog(SignatureBaseRSA.class.getName());
    }
    
    public static class SignatureRSAMD5 extends SignatureBaseRSA
    {
        public SignatureRSAMD5() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
        }
    }
    
    public static class SignatureRSARIPEMD160 extends SignatureBaseRSA
    {
        public SignatureRSARIPEMD160() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
        }
    }
    
    public static class SignatureRSASHA1 extends SignatureBaseRSA
    {
        public SignatureRSASHA1() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
    }
    
    public static class SignatureRSASHA256 extends SignatureBaseRSA
    {
        public SignatureRSASHA256() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        }
    }
    
    public static class SignatureRSASHA384 extends SignatureBaseRSA
    {
        public SignatureRSASHA384() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        }
    }
    
    public static class SignatureRSASHA512 extends SignatureBaseRSA
    {
        public SignatureRSASHA512() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        }
    }
}
