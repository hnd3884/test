package net.oauth.signature;

import net.oauth.OAuth;
import net.oauth.OAuthException;

class PLAINTEXT extends OAuthSignatureMethod
{
    private String signature;
    
    PLAINTEXT() {
        this.signature = null;
    }
    
    public String getSignature(final String baseString) {
        return this.getSignature();
    }
    
    @Override
    protected boolean isValid(final String signature, final String baseString) throws OAuthException {
        return OAuthSignatureMethod.equals(this.getSignature(), signature);
    }
    
    private synchronized String getSignature() {
        if (this.signature == null) {
            this.signature = OAuth.percentEncode(this.getConsumerSecret()) + '&' + OAuth.percentEncode(this.getTokenSecret());
        }
        return this.signature;
    }
    
    public void setConsumerSecret(final String consumerSecret) {
        synchronized (this) {
            this.signature = null;
        }
        super.setConsumerSecret(consumerSecret);
    }
    
    @Override
    public void setTokenSecret(final String tokenSecret) {
        synchronized (this) {
            this.signature = null;
        }
        super.setTokenSecret(tokenSecret);
    }
}
