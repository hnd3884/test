package org.apache.commons.net.imap;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import org.apache.commons.net.util.Base64;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import javax.net.ssl.SSLContext;

public class AuthenticatingIMAPClient extends IMAPSClient
{
    public AuthenticatingIMAPClient() {
        this("TLS", false);
    }
    
    public AuthenticatingIMAPClient(final boolean implicit) {
        this("TLS", implicit);
    }
    
    public AuthenticatingIMAPClient(final String proto) {
        this(proto, false);
    }
    
    public AuthenticatingIMAPClient(final String proto, final boolean implicit) {
        this(proto, implicit, null);
    }
    
    public AuthenticatingIMAPClient(final String proto, final boolean implicit, final SSLContext ctx) {
        super(proto, implicit, ctx);
    }
    
    public AuthenticatingIMAPClient(final boolean implicit, final SSLContext ctx) {
        this("TLS", implicit, ctx);
    }
    
    public AuthenticatingIMAPClient(final SSLContext context) {
        this(false, context);
    }
    
    public boolean authenticate(final AUTH_METHOD method, final String username, final String password) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        return this.auth(method, username, password);
    }
    
    public boolean auth(final AUTH_METHOD method, final String username, final String password) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        if (!IMAPReply.isContinuation(this.sendCommand(IMAPCommand.AUTHENTICATE, method.getAuthName()))) {
            return false;
        }
        switch (method) {
            case PLAIN: {
                final int result = this.sendData(Base64.encodeBase64StringUnChunked(("\u0000" + username + "\u0000" + password).getBytes(this.getCharset())));
                if (result == 0) {
                    this.setState(IMAPState.AUTH_STATE);
                }
                return result == 0;
            }
            case CRAM_MD5: {
                final byte[] serverChallenge = Base64.decodeBase64(this.getReplyString().substring(2).trim());
                final Mac hmac_md5 = Mac.getInstance("HmacMD5");
                hmac_md5.init(new SecretKeySpec(password.getBytes(this.getCharset()), "HmacMD5"));
                final byte[] hmacResult = this._convertToHexString(hmac_md5.doFinal(serverChallenge)).getBytes(this.getCharset());
                final byte[] usernameBytes = username.getBytes(this.getCharset());
                final byte[] toEncode = new byte[usernameBytes.length + 1 + hmacResult.length];
                System.arraycopy(usernameBytes, 0, toEncode, 0, usernameBytes.length);
                toEncode[usernameBytes.length] = 32;
                System.arraycopy(hmacResult, 0, toEncode, usernameBytes.length + 1, hmacResult.length);
                final int result2 = this.sendData(Base64.encodeBase64StringUnChunked(toEncode));
                if (result2 == 0) {
                    this.setState(IMAPState.AUTH_STATE);
                }
                return result2 == 0;
            }
            case LOGIN: {
                if (this.sendData(Base64.encodeBase64StringUnChunked(username.getBytes(this.getCharset()))) != 3) {
                    return false;
                }
                final int result = this.sendData(Base64.encodeBase64StringUnChunked(password.getBytes(this.getCharset())));
                if (result == 0) {
                    this.setState(IMAPState.AUTH_STATE);
                }
                return result == 0;
            }
            case XOAUTH: {
                final int result = this.sendData(username);
                if (result == 0) {
                    this.setState(IMAPState.AUTH_STATE);
                }
                return result == 0;
            }
            default: {
                return false;
            }
        }
    }
    
    private String _convertToHexString(final byte[] a) {
        final StringBuilder result = new StringBuilder(a.length * 2);
        for (final byte element : a) {
            if ((element & 0xFF) <= 15) {
                result.append("0");
            }
            result.append(Integer.toHexString(element & 0xFF));
        }
        return result.toString();
    }
    
    public enum AUTH_METHOD
    {
        PLAIN("PLAIN"), 
        CRAM_MD5("CRAM-MD5"), 
        LOGIN("LOGIN"), 
        XOAUTH("XOAUTH");
        
        private final String authName;
        
        private AUTH_METHOD(final String name) {
            this.authName = name;
        }
        
        public final String getAuthName() {
            return this.authName;
        }
    }
}
