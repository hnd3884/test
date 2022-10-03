package org.apache.xml.security.algorithms.implementations;

import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import java.security.SecureRandom;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.Key;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchAlgorithmException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.algorithms.JCEMapper;
import javax.crypto.Mac;
import org.apache.commons.logging.Log;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;

public abstract class IntegrityHmac extends SignatureAlgorithmSpi
{
    static Log log;
    private Mac _macAlgorithm;
    int _HMACOutputLength;
    
    public abstract String engineGetURI();
    
    public IntegrityHmac() throws XMLSignatureException {
        this._macAlgorithm = null;
        this._HMACOutputLength = 0;
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        if (IntegrityHmac.log.isDebugEnabled()) {
            IntegrityHmac.log.debug((Object)("Created IntegrityHmacSHA1 using " + translateURItoJCEID));
        }
        try {
            this._macAlgorithm = Mac.getInstance(translateURItoJCEID);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex.getLocalizedMessage() });
        }
    }
    
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        throw new XMLSignatureException("empty");
    }
    
    public void reset() {
        this._HMACOutputLength = 0;
    }
    
    protected boolean engineVerify(final byte[] array) throws XMLSignatureException {
        try {
            final byte[] doFinal = this._macAlgorithm.doFinal();
            if (this._HMACOutputLength == 0 || this._HMACOutputLength >= 160) {
                return MessageDigestAlgorithm.isEqual(doFinal, array);
            }
            return MessageDigestAlgorithm.isEqual(reduceBitLength(doFinal, this._HMACOutputLength), array);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineInitVerify(final Key key) throws XMLSignatureException {
        if (!(key instanceof SecretKey)) {
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { key.getClass().getName(), SecretKey.class.getName() });
        }
        try {
            this._macAlgorithm.init(key);
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            final byte[] doFinal = this._macAlgorithm.doFinal();
            if (this._HMACOutputLength == 0 || this._HMACOutputLength >= 160) {
                return doFinal;
            }
            return reduceBitLength(doFinal, this._HMACOutputLength);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    private static byte[] reduceBitLength(final byte[] array, final int n) {
        final int n2 = n / 8;
        final int n3 = n % 8;
        final byte[] array2 = new byte[n2 + ((n3 != 0) ? 1 : 0)];
        System.arraycopy(array, 0, array2, 0, n2);
        if (n3 > 0) {
            array2[n2] = (byte)(array[n2] & (new byte[] { 0, -128, -64, -32, -16, -8, -4, -2 })[n3]);
        }
        return array2;
    }
    
    protected void engineInitSign(final Key key) throws XMLSignatureException {
        if (!(key instanceof SecretKey)) {
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { key.getClass().getName(), SecretKey.class.getName() });
        }
        try {
            this._macAlgorithm.init(key);
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineInitSign(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        if (!(key instanceof SecretKey)) {
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", new Object[] { key.getClass().getName(), SecretKey.class.getName() });
        }
        try {
            this._macAlgorithm.init(key, algorithmParameterSpec);
        }
        catch (final InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
    }
    
    protected void engineInitSign(final Key key, final SecureRandom secureRandom) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
    }
    
    protected void engineUpdate(final byte[] array) throws XMLSignatureException {
        try {
            this._macAlgorithm.update(array);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineUpdate(final byte b) throws XMLSignatureException {
        try {
            this._macAlgorithm.update(b);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws XMLSignatureException {
        try {
            this._macAlgorithm.update(array, n, n2);
        }
        catch (final IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    protected String engineGetJCEAlgorithmString() {
        IntegrityHmac.log.debug((Object)"engineGetJCEAlgorithmString()");
        return this._macAlgorithm.getAlgorithm();
    }
    
    protected String engineGetJCEProviderName() {
        return this._macAlgorithm.getProvider().getName();
    }
    
    protected void engineSetHMACOutputLength(final int hmacOutputLength) {
        this._HMACOutputLength = hmacOutputLength;
    }
    
    protected void engineGetContextFromElement(final Element element) {
        super.engineGetContextFromElement(element);
        if (element == null) {
            throw new IllegalArgumentException("element null");
        }
        final Text selectDsNodeText = XMLUtils.selectDsNodeText(element.getFirstChild(), "HMACOutputLength", 0);
        if (selectDsNodeText != null) {
            this._HMACOutputLength = Integer.parseInt(selectDsNodeText.getData());
        }
    }
    
    public void engineAddContextToElement(final Element element) {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }
        if (this._HMACOutputLength != 0) {
            final Document ownerDocument = element.getOwnerDocument();
            final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(ownerDocument, "HMACOutputLength");
            elementInSignatureSpace.appendChild(ownerDocument.createTextNode(new Integer(this._HMACOutputLength).toString()));
            XMLUtils.addReturnToElement(element);
            element.appendChild(elementInSignatureSpace);
            XMLUtils.addReturnToElement(element);
        }
    }
    
    static {
        IntegrityHmac.log = LogFactory.getLog(IntegrityHmacSHA1.class.getName());
    }
    
    public static class IntegrityHmacMD5 extends IntegrityHmac
    {
        public IntegrityHmacMD5() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
        }
    }
    
    public static class IntegrityHmacRIPEMD160 extends IntegrityHmac
    {
        public IntegrityHmacRIPEMD160() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
        }
    }
    
    public static class IntegrityHmacSHA1 extends IntegrityHmac
    {
        public IntegrityHmacSHA1() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
        }
    }
    
    public static class IntegrityHmacSHA256 extends IntegrityHmac
    {
        public IntegrityHmacSHA256() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
        }
    }
    
    public static class IntegrityHmacSHA384 extends IntegrityHmac
    {
        public IntegrityHmacSHA384() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
        }
    }
    
    public static class IntegrityHmacSHA512 extends IntegrityHmac
    {
        public IntegrityHmacSHA512() throws XMLSignatureException {
        }
        
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
        }
    }
}
