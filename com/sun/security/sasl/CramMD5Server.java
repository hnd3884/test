package com.sun.security.sasl;

import java.io.IOException;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import java.util.logging.Level;
import java.util.Random;
import javax.security.sasl.SaslException;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslServer;

final class CramMD5Server extends CramMD5Base implements SaslServer
{
    private String fqdn;
    private byte[] challengeData;
    private String authzid;
    private CallbackHandler cbh;
    
    CramMD5Server(final String s, final String fqdn, final Map<String, ?> map, final CallbackHandler cbh) throws SaslException {
        this.challengeData = null;
        if (fqdn == null) {
            throw new SaslException("CRAM-MD5: fully qualified server name must be specified");
        }
        this.fqdn = fqdn;
        this.cbh = cbh;
    }
    
    @Override
    public byte[] evaluateResponse(final byte[] array) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("CRAM-MD5 authentication already completed");
        }
        if (this.aborted) {
            throw new IllegalStateException("CRAM-MD5 authentication previously aborted due to error");
        }
        try {
            if (this.challengeData == null) {
                if (array.length != 0) {
                    this.aborted = true;
                    throw new SaslException("CRAM-MD5 does not expect any initial response");
                }
                final long nextLong = new Random().nextLong();
                final long currentTimeMillis = System.currentTimeMillis();
                final StringBuffer sb = new StringBuffer();
                sb.append('<');
                sb.append(nextLong);
                sb.append('.');
                sb.append(currentTimeMillis);
                sb.append('@');
                sb.append(this.fqdn);
                sb.append('>');
                final String string = sb.toString();
                CramMD5Server.logger.log(Level.FINE, "CRAMSRV01:Generated challenge: {0}", string);
                this.challengeData = string.getBytes("UTF8");
                return this.challengeData.clone();
            }
            else {
                if (CramMD5Server.logger.isLoggable(Level.FINE)) {
                    CramMD5Server.logger.log(Level.FINE, "CRAMSRV02:Received response: {0}", new String(array, "UTF8"));
                }
                int n = 0;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] == 32) {
                        n = i;
                        break;
                    }
                }
                if (n == 0) {
                    this.aborted = true;
                    throw new SaslException("CRAM-MD5: Invalid response; space missing");
                }
                final String s = new String(array, 0, n, "UTF8");
                CramMD5Server.logger.log(Level.FINE, "CRAMSRV03:Extracted username: {0}", s);
                final NameCallback nameCallback = new NameCallback("CRAM-MD5 authentication ID: ", s);
                final PasswordCallback passwordCallback = new PasswordCallback("CRAM-MD5 password: ", false);
                this.cbh.handle(new Callback[] { nameCallback, passwordCallback });
                final char[] password = passwordCallback.getPassword();
                if (password == null || password.length == 0) {
                    this.aborted = true;
                    throw new SaslException("CRAM-MD5: username not found: " + s);
                }
                passwordCallback.clearPassword();
                final String s2 = new String(password);
                for (int j = 0; j < password.length; ++j) {
                    password[j] = '\0';
                }
                this.pw = s2.getBytes("UTF8");
                final String hmac_MD5 = CramMD5Base.HMAC_MD5(this.pw, this.challengeData);
                CramMD5Server.logger.log(Level.FINE, "CRAMSRV04:Expecting digest: {0}", hmac_MD5);
                this.clearPassword();
                final byte[] bytes = hmac_MD5.getBytes("UTF8");
                if (bytes.length != array.length - n - 1) {
                    this.aborted = true;
                    throw new SaslException("Invalid response");
                }
                int n2 = 0;
                for (int k = n + 1; k < array.length; ++k) {
                    if (bytes[n2++] != array[k]) {
                        this.aborted = true;
                        throw new SaslException("Invalid response");
                    }
                }
                final AuthorizeCallback authorizeCallback = new AuthorizeCallback(s, s);
                this.cbh.handle(new Callback[] { authorizeCallback });
                if (authorizeCallback.isAuthorized()) {
                    this.authzid = authorizeCallback.getAuthorizedID();
                    CramMD5Server.logger.log(Level.FINE, "CRAMSRV05:Authorization id: {0}", this.authzid);
                    this.completed = true;
                    return null;
                }
                this.aborted = true;
                throw new SaslException("CRAM-MD5: user not authorized: " + s);
            }
        }
        catch (final UnsupportedEncodingException ex) {
            this.aborted = true;
            throw new SaslException("UTF8 not available on platform", ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            this.aborted = true;
            throw new SaslException("MD5 algorithm not available on platform", ex2);
        }
        catch (final UnsupportedCallbackException ex3) {
            this.aborted = true;
            throw new SaslException("CRAM-MD5 authentication failed", ex3);
        }
        catch (final SaslException ex4) {
            throw ex4;
        }
        catch (final IOException ex5) {
            this.aborted = true;
            throw new SaslException("CRAM-MD5 authentication failed", ex5);
        }
    }
    
    @Override
    public String getAuthorizationID() {
        if (this.completed) {
            return this.authzid;
        }
        throw new IllegalStateException("CRAM-MD5 authentication not completed");
    }
}
