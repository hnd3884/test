package net.oauth.signature;

import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.oauth.OAuth;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import net.oauth.OAuthException;
import javax.crypto.SecretKey;

class HMAC_SHA1 extends OAuthSignatureMethod
{
    private static final String ENCODING = "UTF-8";
    private static final String MAC_NAME = "HmacSHA1";
    private SecretKey key;
    
    HMAC_SHA1() {
        this.key = null;
    }
    
    @Override
    protected String getSignature(final String baseString) throws OAuthException {
        try {
            final String signature = OAuthSignatureMethod.base64Encode(this.computeSignature(baseString));
            return signature;
        }
        catch (final GeneralSecurityException e) {
            throw new OAuthException(e);
        }
        catch (final UnsupportedEncodingException e2) {
            throw new OAuthException(e2);
        }
    }
    
    @Override
    protected boolean isValid(final String signature, final String baseString) throws OAuthException {
        try {
            final byte[] expected = this.computeSignature(baseString);
            final byte[] actual = OAuthSignatureMethod.decodeBase64(signature);
            return OAuthSignatureMethod.equals(expected, actual);
        }
        catch (final GeneralSecurityException e) {
            throw new OAuthException(e);
        }
        catch (final UnsupportedEncodingException e2) {
            throw new OAuthException(e2);
        }
    }
    
    private byte[] computeSignature(final String baseString) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKey key = null;
        synchronized (this) {
            if (this.key == null) {
                final String keyString = OAuth.percentEncode(this.getConsumerSecret()) + '&' + OAuth.percentEncode(this.getTokenSecret());
                final byte[] keyBytes = keyString.getBytes("UTF-8");
                this.key = new SecretKeySpec(keyBytes, "HmacSHA1");
            }
            key = this.key;
        }
        final Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        final byte[] text = baseString.getBytes("UTF-8");
        return mac.doFinal(text);
    }
    
    public void setConsumerSecret(final String consumerSecret) {
        synchronized (this) {
            this.key = null;
        }
        super.setConsumerSecret(consumerSecret);
    }
    
    @Override
    public void setTokenSecret(final String tokenSecret) {
        synchronized (this) {
            this.key = null;
        }
        super.setTokenSecret(tokenSecret);
    }
}
