package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;
import java.security.SecureRandom;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.Key;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchAlgorithmException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import javax.crypto.Mac;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;

public abstract class IntegrityHmac extends SignatureAlgorithmSpi
{
    private static final Logger LOG;
    private Mac macAlgorithm;
    private int HMACOutputLength;
    private boolean HMACOutputLengthSet;
    
    public abstract String engineGetURI();
    
    abstract int getDigestLength();
    
    public IntegrityHmac() throws XMLSignatureException {
        this.HMACOutputLengthSet = false;
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        IntegrityHmac.LOG.debug("Created IntegrityHmacSHA1 using {}", translateURItoJCEID);
        try {
            this.macAlgorithm = Mac.getInstance(translateURItoJCEID);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex.getLocalizedMessage() });
        }
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        throw new XMLSignatureException("empty", new Object[] { "Incorrect method call" });
    }
    
    @Override
    public void reset() {
        this.HMACOutputLength = 0;
        this.HMACOutputLengthSet = false;
        this.macAlgorithm.reset();
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws XMLSignatureException {
        try {
            if (this.HMACOutputLengthSet && this.HMACOutputLength < this.getDigestLength()) {
                IntegrityHmac.LOG.debug("HMACOutputLength must not be less than {}", this.getDigestLength());
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", new Object[] { String.valueOf(this.getDigestLength()) });
            }
            return MessageDigestAlgorithm.isEqual(this.macAlgorithm.doFinal(), array);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected void engineInitVerify(final Key key) throws XMLSignatureException {
        if (!(key instanceof SecretKey)) {
            Object name = null;
            if (key != null) {
                name = key.getClass().getName();
            }
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { name, SecretKey.class.getName() });
        }
        try {
            this.macAlgorithm.init(key);
        }
        catch (final InvalidKeyException ex) {
            final Mac macAlgorithm = this.macAlgorithm;
            try {
                this.macAlgorithm = Mac.getInstance(this.macAlgorithm.getAlgorithm());
            }
            catch (final Exception ex2) {
                IntegrityHmac.LOG.debug("Exception when reinstantiating Mac: {}", ex2);
                this.macAlgorithm = macAlgorithm;
            }
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            if (this.HMACOutputLengthSet && this.HMACOutputLength < this.getDigestLength()) {
                IntegrityHmac.LOG.debug("HMACOutputLength must not be less than {}", this.getDigestLength());
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", new Object[] { String.valueOf(this.getDigestLength()) });
            }
            return this.macAlgorithm.doFinal();
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected void engineInitSign(final Key key) throws XMLSignatureException {
        this.engineInitSign(key, (AlgorithmParameterSpec)null);
    }
    
    @Override
    protected void engineInitSign(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        if (!(key instanceof SecretKey)) {
            Object name = null;
            if (key != null) {
                name = key.getClass().getName();
            }
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { name, SecretKey.class.getName() });
        }
        try {
            if (algorithmParameterSpec == null) {
                this.macAlgorithm.init(key);
            }
            else {
                this.macAlgorithm.init(key, algorithmParameterSpec);
            }
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new XMLSignatureException(ex2);
        }
    }
    
    @Override
    protected void engineInitSign(final Key key, final SecureRandom secureRandom) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
    }
    
    @Override
    protected void engineUpdate(final byte[] array) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(array);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected void engineUpdate(final byte b) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(b);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(array, n, n2);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    @Override
    protected String engineGetJCEAlgorithmString() {
        return this.macAlgorithm.getAlgorithm();
    }
    
    @Override
    protected String engineGetJCEProviderName() {
        return this.macAlgorithm.getProvider().getName();
    }
    
    @Override
    protected void engineSetHMACOutputLength(final int hmacOutputLength) {
        this.HMACOutputLength = hmacOutputLength;
        this.HMACOutputLengthSet = true;
    }
    
    @Override
    protected void engineGetContextFromElement(final Element element) {
        super.engineGetContextFromElement(element);
        if (element == null) {
            throw new IllegalArgumentException("element null");
        }
        final Element selectDsNode = XMLUtils.selectDsNode(element.getFirstChild(), "HMACOutputLength", 0);
        if (selectDsNode != null) {
            final String fullTextChildrenFromNode = XMLUtils.getFullTextChildrenFromNode(selectDsNode);
            if (fullTextChildrenFromNode != null && !"".equals(fullTextChildrenFromNode)) {
                this.HMACOutputLength = Integer.parseInt(fullTextChildrenFromNode);
                this.HMACOutputLengthSet = true;
            }
        }
    }
    
    public void engineAddContextToElement(final Element element) {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }
        if (this.HMACOutputLengthSet) {
            final Document ownerDocument = element.getOwnerDocument();
            final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(ownerDocument, "HMACOutputLength");
            elementInSignatureSpace.appendChild(ownerDocument.createTextNode("" + this.HMACOutputLength));
            XMLUtils.addReturnToElement(element);
            element.appendChild(elementInSignatureSpace);
            XMLUtils.addReturnToElement(element);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(IntegrityHmac.class);
    }
    
    public static class IntegrityHmacSHA1 extends IntegrityHmac
    {
        public IntegrityHmacSHA1() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
        }
        
        @Override
        int getDigestLength() {
            return 160;
        }
    }
    
    public static class IntegrityHmacSHA224 extends IntegrityHmac
    {
        public IntegrityHmacSHA224() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
        }
        
        @Override
        int getDigestLength() {
            return 224;
        }
    }
    
    public static class IntegrityHmacSHA256 extends IntegrityHmac
    {
        public IntegrityHmacSHA256() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
        }
        
        @Override
        int getDigestLength() {
            return 256;
        }
    }
    
    public static class IntegrityHmacSHA384 extends IntegrityHmac
    {
        public IntegrityHmacSHA384() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
        }
        
        @Override
        int getDigestLength() {
            return 384;
        }
    }
    
    public static class IntegrityHmacSHA512 extends IntegrityHmac
    {
        public IntegrityHmacSHA512() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
        }
        
        @Override
        int getDigestLength() {
            return 512;
        }
    }
    
    public static class IntegrityHmacRIPEMD160 extends IntegrityHmac
    {
        public IntegrityHmacRIPEMD160() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
        }
        
        @Override
        int getDigestLength() {
            return 160;
        }
    }
    
    public static class IntegrityHmacMD5 extends IntegrityHmac
    {
        public IntegrityHmacMD5() throws XMLSignatureException {
        }
        
        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
        }
        
        @Override
        int getDigestLength() {
            return 128;
        }
    }
}
