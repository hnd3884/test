package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslClient;

final class CramMD5Client extends CramMD5Base implements SaslClient
{
    private String username;
    
    CramMD5Client(final String username, final byte[] pw) throws SaslException {
        if (username == null || pw == null) {
            throw new SaslException("CRAM-MD5: authentication ID and password must be specified");
        }
        this.username = username;
        this.pw = pw;
    }
    
    @Override
    public boolean hasInitialResponse() {
        return false;
    }
    
    @Override
    public byte[] evaluateChallenge(final byte[] array) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("CRAM-MD5 authentication already completed");
        }
        if (this.aborted) {
            throw new IllegalStateException("CRAM-MD5 authentication previously aborted due to error");
        }
        try {
            if (CramMD5Client.logger.isLoggable(Level.FINE)) {
                CramMD5Client.logger.log(Level.FINE, "CRAMCLNT01:Received challenge: {0}", new String(array, "UTF8"));
            }
            final String hmac_MD5 = CramMD5Base.HMAC_MD5(this.pw, array);
            this.clearPassword();
            final String string = this.username + " " + hmac_MD5;
            CramMD5Client.logger.log(Level.FINE, "CRAMCLNT02:Sending response: {0}", string);
            this.completed = true;
            return string.getBytes("UTF8");
        }
        catch (final NoSuchAlgorithmException ex) {
            this.aborted = true;
            throw new SaslException("MD5 algorithm not available on platform", ex);
        }
        catch (final UnsupportedEncodingException ex2) {
            this.aborted = true;
            throw new SaslException("UTF8 not available on platform", ex2);
        }
    }
}
